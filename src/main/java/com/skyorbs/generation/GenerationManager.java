package com.skyorbs.generation;

import com.skyorbs.SkyOrbs;
import com.skyorbs.biomes.BiomeType;
import com.skyorbs.core.Orb;
import com.skyorbs.features.*;
import com.skyorbs.shapes.PlanetShape;
import org.bukkit.*;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.*;

public class GenerationManager {
    
    private final SkyOrbs plugin;
    private final PlacementService placementService;
    private final PlanetSizeCalculator sizeCalculator;
    private final AsteroidGenerator asteroidGenerator;
    private final SatelliteGenerator satelliteGenerator;
    private final ExecutorService executor;
    
    public GenerationManager(SkyOrbs plugin) {
        this.plugin = plugin;
        this.placementService = new PlacementService(plugin);
        this.sizeCalculator = new PlanetSizeCalculator(plugin);
        this.asteroidGenerator = new AsteroidGenerator(plugin);
        this.satelliteGenerator = new SatelliteGenerator(plugin);
        this.executor = Executors.newFixedThreadPool(plugin.getConfigManager().getThreadPoolCoreSize());
    }
    
    public void createPlanetAsync(World world, Player player) {
        createPlanetAsync(world, player, plugin.getConfigManager().generateRandomName());
    }
    
    public void createPlanetAsync(World world, Player player, String name) {
        player.sendMessage("§eGezegen oluşturuluyor...");
        
        executor.submit(() -> {
            try {
                List<Orb> existingOrbs = plugin.getDatabaseManager().getAllOrbs();
                int radius = sizeCalculator.calculateRadius("RANDOM");
                PlacementService.PlacementResult placement = placementService.findPlacement(radius, existingOrbs);
                
                if (!placement.isSuccess()) {
                    Bukkit.getScheduler().runTask(plugin, () -> 
                        player.sendMessage("§cUygun yer bulunamadı!")
                    );
                    return;
                }
                
                placementService.reserveLocation(placement.getX(), placement.getZ());
                
                long seed = System.currentTimeMillis();
                Random random = new Random(seed);
                
                PlanetShape shape = plugin.getShapeRegistry().getRandomShape(
                    plugin.getConfigManager().getShapeWeights()
                );
                
                BiomeType biome = BiomeType.getRandomBiomeWeighted(random);
                
                Orb orb = new Orb(
                    UUID.randomUUID(),
                    name,
                    world.getName(),
                    placement.getX(),
                    placement.getY(),
                    placement.getZ(),
                    radius,
                    shape.getName(),
                    biome.name(),
                    seed,
                    System.currentTimeMillis(),
                    false,
                    null
                );
                
                // Generate planet blocks in batches (async)
                generatePlanetBlocksAsync(world, orb, shape, biome, () -> {
                    // Generate asteroids
                    try {
                        List<Orb> asteroids = asteroidGenerator.generateAsteroidsForPlanet(orb, world);
                        for (Orb asteroid : asteroids) {
                            PlanetShape asteroidShape = plugin.getShapeRegistry().getShape(asteroid.getShapeName());
                            BiomeType asteroidBiome = BiomeType.valueOf(asteroid.getBiomeName());
                            generateOrbBlocksAsync(world, asteroid, asteroidShape, asteroidBiome, null);
                            plugin.getDatabaseManager().saveOrb(asteroid);
                        }
                        
                        // Generate satellites
                        List<Orb> satellites = satelliteGenerator.generateSatellitesForPlanet(orb, world);
                        for (Orb satellite : satellites) {
                            PlanetShape satelliteShape = plugin.getShapeRegistry().getShape(satellite.getShapeName());
                            BiomeType satelliteBiome = BiomeType.valueOf(satellite.getBiomeName());
                            generateOrbBlocksAsync(world, satellite, satelliteShape, satelliteBiome, null);
                            plugin.getDatabaseManager().saveOrb(satellite);
                        }
                        
                        // Save main planet
                        plugin.getDatabaseManager().saveOrb(orb);
                        
                        // Teleport player
                        Bukkit.getScheduler().runTaskLater(plugin, () -> {
                            Location loc = new Location(world, orb.getCenterX(), orb.getCenterY() + orb.getRadius() + 10, orb.getCenterZ());
                            player.teleport(loc);
                            
                            String shapeName = plugin.getShapeRegistry().getShape(orb.getShapeName()).getDisplayName();
                            player.sendMessage("§a§lGezegen Oluşturuldu!");
                            player.sendMessage(String.format("§aGezegen: §e%s §7(%s, %s)", 
                                orb.getName(), shapeName, orb.getBiomeName()
                            ));
                            player.sendMessage(String.format("§aKonum: §f%d, %d, %d §7| Yarıçap: §f%d", 
                                orb.getCenterX(), orb.getCenterY(), orb.getCenterZ(), orb.getRadius()
                            ));
                        }, 40L);
                        
                    } catch (Exception e) {
                        plugin.logError("Asteroid/satellite hatası", e);
                    }
                });
                
                placementService.releaseLocation(placement.getX(), placement.getZ());
                
            } catch (Exception e) {
                plugin.logError("Gezegen oluşturma hatası", e);
                Bukkit.getScheduler().runTask(plugin, () -> 
                    player.sendMessage("§cHata: " + e.getMessage())
                );
            }
        });
    }
    
    private void generatePlanetBlocksAsync(World world, Orb orb, PlanetShape shape, BiomeType biome, Runnable callback) {
        int cx = orb.getCenterX();
        int cy = orb.getCenterY();
        int cz = orb.getCenterZ();
        int radius = orb.getRadius();
        long seed = orb.getSeed();
        Random random = new Random(seed);
        
        // Prepare blocks to place
        List<BlockPlacement> blocks = new ArrayList<>();
        
        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    if (shape.isBlockPart(x, y, z, radius, seed)) {
                        double distance = Math.sqrt(x * x + y * y + z * z);
                        int depth = (int)(radius - distance);
                        Material material = biome.getMaterial(depth, random);
                        blocks.add(new BlockPlacement(cx + x, cy + y, cz + z, material));
                    }
                }
            }
        }
        
        // Add ores
        List<OreGenerator.BlockData> ores = OreGenerator.generateOres(orb, biome, world);
        for (OreGenerator.BlockData ore : ores) {
            blocks.add(new BlockPlacement(ore.x, ore.y, ore.z, ore.material));
        }
        
        // Place blocks in batches
        placeBlocksInBatches(world, blocks, callback);
    }
    
    private void generateOrbBlocksAsync(World world, Orb orb, PlanetShape shape, BiomeType biome, Runnable callback) {
        int cx = orb.getCenterX();
        int cy = orb.getCenterY();
        int cz = orb.getCenterZ();
        int radius = orb.getRadius();
        long seed = orb.getSeed();
        Random random = new Random(seed);
        
        List<BlockPlacement> blocks = new ArrayList<>();
        
        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    if (shape.isBlockPart(x, y, z, radius, seed)) {
                        double distance = Math.sqrt(x * x + y * y + z * z);
                        int depth = (int)(radius - distance);
                        Material material = biome.getMaterial(depth, random);
                        blocks.add(new BlockPlacement(cx + x, cy + y, cz + z, material));
                    }
                }
            }
        }
        
        placeBlocksInBatches(world, blocks, callback);
    }
    
    private void placeBlocksInBatches(World world, List<BlockPlacement> blocks, Runnable callback) {
        int batchSize = 500; // 500 blok per tick
        int totalBatches = (blocks.size() + batchSize - 1) / batchSize;
        
        for (int i = 0; i < totalBatches; i++) {
            int start = i * batchSize;
            int end = Math.min(start + batchSize, blocks.size());
            List<BlockPlacement> batch = blocks.subList(start, end);
            
            int delay = i; // 1 tick delay per batch
            
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                for (BlockPlacement bp : batch) {
                    world.getBlockAt(bp.x, bp.y, bp.z).setType(bp.material, false); // false = no physics update
                }
            }, delay);
        }
        
        // Run callback after all batches complete
        if (callback != null) {
            Bukkit.getScheduler().runTaskLater(plugin, callback, totalBatches + 5);
        }
    }
    
    public CompletableFuture<Void> deletePlanet(Orb orb) {
        CompletableFuture<Void> future = new CompletableFuture<>();
        
        executor.submit(() -> {
            try {
                List<Orb> children = plugin.getDatabaseManager().getChildOrbs(orb.getId());
                for (Orb child : children) {
                    plugin.getDatabaseManager().deleteOrb(child.getId());
                }
                
                plugin.getDatabaseManager().deleteOrb(orb.getId());
                future.complete(null);
                
            } catch (Exception e) {
                future.completeExceptionally(e);
            }
        });
        
        return future;
    }
    
    public void shutdown() {
        executor.shutdown();
    }
    
    private static class BlockPlacement {
        int x, y, z;
        Material material;
        
        BlockPlacement(int x, int y, int z, Material material) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.material = material;
        }
    }
}
