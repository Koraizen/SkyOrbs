package com.skyorbs.generation;

import com.skyorbs.SkyOrbs;
import com.skyorbs.biomes.BiomeType;
import com.skyorbs.core.Orb;
import com.skyorbs.features.*;
import com.skyorbs.shapes.PlanetShape;
import org.bukkit.*;
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
    
    public GenerationManager(SkyOrbs plugin) {
        this.plugin = plugin;
        this.placementService = new PlacementService(plugin);
        this.sizeCalculator = new PlanetSizeCalculator(plugin);
        this.asteroidGenerator = new AsteroidGenerator(plugin);
        this.satelliteGenerator = new SatelliteGenerator(plugin);
        this.executor = Executors.newFixedThreadPool(plugin.getConfigManager().getThreadPoolCoreSize());
    }
    
    public CompletableFuture<Orb> createPlanet(World world) {
        return createPlanet(world, plugin.getConfigManager().generateRandomName());
    }
    
    public CompletableFuture<Orb> createPlanet(World world, String name) {
        CompletableFuture<Orb> future = new CompletableFuture<>();
        
        executor.submit(() -> {
            try {
                List<Orb> existingOrbs = plugin.getDatabaseManager().getAllOrbs();
                
                int radius = sizeCalculator.calculateRadius("RANDOM");
                
                PlacementService.PlacementResult placement = placementService.findPlacement(radius, existingOrbs);
                
                if (!placement.isSuccess()) {
                    future.completeExceptionally(new RuntimeException("Uygun yer bulunamadı!"));
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
                
                plugin.getDatabaseManager().saveOrb(orb);
                
                List<Orb> asteroids = asteroidGenerator.generateAsteroidsForPlanet(orb, world);
                for (Orb asteroid : asteroids) {
                    plugin.getDatabaseManager().saveOrb(asteroid);
                }
                
                List<Orb> satellites = satelliteGenerator.generateSatellitesForPlanet(orb, world);
                for (Orb satellite : satellites) {
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
        int cx = orb.getCenterX();
        int cy = orb.getCenterY();
        int cz = orb.getCenterZ();
        int radius = orb.getRadius();
        long seed = orb.getSeed();
        Random random = new Random(seed);
        
        Bukkit.getScheduler().runTask(plugin, () -> {
            for (int x = -radius; x <= radius; x++) {
                for (int y = -radius; y <= radius; y++) {
                    for (int z = -radius; z <= radius; z++) {
                        if (shape.isBlockPart(x, y, z, radius, seed)) {
                            double distance = Math.sqrt(x * x + y * y + z * z);
                            int depth = (int)(radius - distance);
                            
                            Material material = biome.getMaterial(depth, random);
                            world.getBlockAt(cx + x, cy + y, cz + z).setType(material);
                        }
                    }
                }
            }
        });
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
}
