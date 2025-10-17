package com.skyorbs.generation;

import com.skyorbs.SkyOrbs;
import com.skyorbs.biomes.BiomeType;
import com.skyorbs.core.Orb;
import com.skyorbs.features.OreGenerator;
import com.skyorbs.features.StructureGenerator;
import com.skyorbs.features.TreeGenerator;
import com.skyorbs.features.TreasureGenerator;
import com.skyorbs.shapes.PlanetShape;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitTask;

import java.sql.SQLException;
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
    private final BlockingQueue<List<BlockUpdate>> batchQueue = new LinkedBlockingQueue<>(20);
    private BukkitTask processingTask = null;
    
    public GenerationManager(SkyOrbs plugin) {
        this.plugin = plugin;
        this.placementService = new PlacementService(plugin);
        this.sizeCalculator = new PlanetSizeCalculator(plugin);
        this.asteroidGenerator = new AsteroidGenerator(plugin);
        this.satelliteGenerator = new SatelliteGenerator(plugin);
        this.executor = Executors.newFixedThreadPool(
            plugin.getConfigManager().getThreadPoolCoreSize()
        );
    }
    
    public CompletableFuture<Orb> createPlanet(World world, String name) {
        CompletableFuture<Orb> future = new CompletableFuture<>();
        
        executor.submit(() -> {
            try {
                List<Orb> existingOrbs = plugin.getDatabaseManager().getAllOrbs();
                
                int radius = sizeCalculator.calculateRadius("RANDOM");
                
                PlacementService.PlacementResult placement = placementService.findPlacement(radius, existingOrbs);
                
                if (!placement.isSuccess()) {
                    future.completeExceptionally(new RuntimeException("Uygun yer bulunamadı! (3000 blok limiti)"));
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
                
                generatePlanetBlocks(world, orb, shape, biome);
                
                List<OreGenerator.BlockData> ores = OreGenerator.generateOres(orb, biome, world);
                placeBlocks(world, ores);
                
                List<OreGenerator.BlockData> trees = TreeGenerator.generateTrees(orb, biome, world);
                placeBlocks(world, trees);
                
                List<OreGenerator.BlockData> structures = StructureGenerator.generateStructures(orb, biome, world);
                placeBlocks(world, structures);
                
                List<TreasureGenerator.TreasureLocation> treasures = TreasureGenerator.generateTreasures(orb, biome, world);
                Bukkit.getScheduler().runTask(plugin, () -> {
                    for (TreasureGenerator.TreasureLocation treasure : treasures) {
                        TreasureGenerator.placeChest(world, treasure.x, treasure.y, treasure.z, treasure.tier, treasure.biome);
                    }
                });
                
                plugin.getDatabaseManager().saveOrb(orb);
                
                List<Orb> asteroids = asteroidGenerator.generateAsteroidsForPlanet(orb, world);
                for (Orb asteroid : asteroids) {
                    generatePlanetBlocks(world, asteroid, 
                        plugin.getShapeRegistry().getShape(asteroid.getShapeName()), 
                        BiomeType.valueOf(asteroid.getBiomeName())
                    );
                    plugin.getDatabaseManager().saveOrb(asteroid);
                }
                
                List<Orb> satellites = satelliteGenerator.generateSatellitesForPlanet(orb, world);
                for (Orb satellite : satellites) {
                    generatePlanetBlocks(world, satellite, 
                        plugin.getShapeRegistry().getShape(satellite.getShapeName()), 
                        BiomeType.valueOf(satellite.getBiomeName())
                    );
                    plugin.getDatabaseManager().saveOrb(satellite);
                }
                
                placementService.releaseLocation(placement.getX(), placement.getZ());
                
                future.complete(orb);
                
            } catch (Exception e) {
                future.completeExceptionally(e);
            }
        });
        
        return future;
    }
    
    private void generatePlanetBlocks(World world, Orb orb, PlanetShape shape, BiomeType biome) {
        ensureProcessingTask();
        
        int cx = orb.getCenterX();
        int cy = orb.getCenterY();
        int cz = orb.getCenterZ();
        int radius = orb.getRadius();
        long seed = orb.getSeed();
        Random random = new Random(seed);
        
        List<BlockUpdate> currentBatch = new java.util.ArrayList<>();
        int batchSize = 500;
        
        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    if (shape.isBlockPart(x, y, z, radius, seed)) {
                        double distance = Math.sqrt(x * x + y * y + z * z);
                        int depth = (int)(radius - distance);
                        
                        Material material = biome.getMaterial(depth, random);
                        currentBatch.add(new BlockUpdate(cx + x, cy + y, cz + z, material, world));
                        
                        if (currentBatch.size() >= batchSize) {
                            try {
                                batchQueue.put(new java.util.ArrayList<>(currentBatch));
                            } catch (InterruptedException e) {
                                Thread.currentThread().interrupt();
                                return;
                            }
                            currentBatch.clear();
                        }
                    }
                }
            }
        }
        
        if (!currentBatch.isEmpty()) {
            try {
                batchQueue.put(new java.util.ArrayList<>(currentBatch));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
    
    private void placeBlocks(World world, List<OreGenerator.BlockData> blocks) {
        ensureProcessingTask();
        
        List<BlockUpdate> currentBatch = new java.util.ArrayList<>();
        int batchSize = 500;
        
        for (OreGenerator.BlockData block : blocks) {
            currentBatch.add(new BlockUpdate(block.x, block.y, block.z, block.material, world));
            
            if (currentBatch.size() >= batchSize) {
                try {
                    batchQueue.put(new java.util.ArrayList<>(currentBatch));
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
                currentBatch.clear();
            }
        }
        
        if (!currentBatch.isEmpty()) {
            try {
                batchQueue.put(new java.util.ArrayList<>(currentBatch));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
    
    private void ensureProcessingTask() {
        if (processingTask == null || processingTask.isCancelled()) {
            processingTask = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
                int batchesPerTick = 5;
                for (int i = 0; i < batchesPerTick && !batchQueue.isEmpty(); i++) {
                    List<BlockUpdate> batch = batchQueue.poll();
                    if (batch != null) {
                        for (BlockUpdate update : batch) {
                            update.world.getBlockAt(update.x, update.y, update.z).setType(update.material);
                        }
                    }
                }
                
                if (batchQueue.isEmpty() && processingTask != null) {
                    processingTask.cancel();
                    processingTask = null;
                }
            }, 1L, 1L);
        }
    }
    
    private static class BlockUpdate {
        final int x, y, z;
        final Material material;
        final World world;
        
        BlockUpdate(int x, int y, int z, Material material, World world) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.material = material;
            this.world = world;
        }
    }
    
    public CompletableFuture<Void> deletePlanet(Orb orb) {
        CompletableFuture<Void> future = new CompletableFuture<>();
        
        executor.submit(() -> {
            try {
                World world = Bukkit.getWorld(orb.getWorldName());
                if (world != null) {
                    clearPlanetBlocks(world, orb);
                }
                
                try {
                    List<Orb> children = plugin.getDatabaseManager().getChildOrbs(orb.getId());
                    for (Orb child : children) {
                        if (world != null) {
                            clearPlanetBlocks(world, child);
                        }
                        plugin.getDatabaseManager().deleteOrb(child.getId());
                    }
                } catch (SQLException e) {
                    plugin.logError("Child orb (asteroid/satellite) silme hatası", e);
                }
                
                plugin.getDatabaseManager().deleteOrb(orb.getId());
                future.complete(null);
                
            } catch (Exception e) {
                future.completeExceptionally(e);
            }
        });
        
        return future;
    }
    
    private void clearPlanetBlocks(World world, Orb orb) {
        ensureProcessingTask();
        
        int cx = orb.getCenterX();
        int cy = orb.getCenterY();
        int cz = orb.getCenterZ();
        int radius = orb.getRadius();
        
        List<BlockUpdate> currentBatch = new java.util.ArrayList<>();
        int batchSize = 500;
        
        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    currentBatch.add(new BlockUpdate(cx + x, cy + y, cz + z, Material.AIR, world));
                    
                    if (currentBatch.size() >= batchSize) {
                        try {
                            batchQueue.put(new java.util.ArrayList<>(currentBatch));
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                            return;
                        }
                        currentBatch.clear();
                    }
                }
            }
        }
        
        if (!currentBatch.isEmpty()) {
            try {
                batchQueue.put(new java.util.ArrayList<>(currentBatch));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
    
    public void shutdown() {
        if (processingTask != null && !processingTask.isCancelled()) {
            processingTask.cancel();
        }
        executor.shutdown();
    }
}
