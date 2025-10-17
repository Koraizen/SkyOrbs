package com.skyorbs.ecosystem;

import com.skyorbs.SkyOrbs;
import com.skyorbs.core.Orb;
import com.skyorbs.core.PlanetType;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

public class BiosphereManager {

    private final SkyOrbs plugin;
    private final Map<UUID, BiosphereData> biosphereData = new HashMap<>();

    public BiosphereManager(SkyOrbs plugin) {
        this.plugin = plugin;
        startBiosphereUpdater();
    }

    public void initializeBiosphere(Orb orb) {
        BiosphereData data = new BiosphereData(orb);
        biosphereData.put(orb.getId(), data);
    }

    public void updateBiosphere(Orb orb, String activity) {
        BiosphereData data = biosphereData.get(orb.getId());
        if (data == null) return;

        switch (activity) {
            case "mining" -> {
                // Mining reduces ecological balance
                data.ecologicalBalance = Math.max(0.0, data.ecologicalBalance - 0.1);
                // Reduce flora density
                data.floraDensity = Math.max(0.0, data.floraDensity - 0.05);
            }
            case "planting" -> {
                // Planting improves ecological balance
                data.ecologicalBalance = Math.min(2.0, data.ecologicalBalance + 0.05);
                // Increase flora density
                data.floraDensity = Math.min(1.0, data.floraDensity + 0.02);
            }
            case "farming" -> {
                // Farming has mixed effects
                data.ecologicalBalance = Math.min(2.0, data.ecologicalBalance + 0.02);
            }
            case "exploration" -> {
                // Exploration slightly improves balance through discovery
                data.ecologicalBalance = Math.min(2.0, data.ecologicalBalance + 0.01);
            }
        }

        // Update the orb's ecological balance
        orb.setEcologicalBalance(data.ecologicalBalance);

        // Check for biosphere level changes
        updateBiosphereLevel(orb, data);
    }

    private void updateBiosphereLevel(Orb orb, BiosphereData data) {
        int newLevel = calculateBiosphereLevel(data);
        if (newLevel != orb.getBiosphereLevel()) {
            orb.setBiosphereLevel(newLevel);
            onBiosphereLevelChange(orb, newLevel);
        }
    }

    private int calculateBiosphereLevel(BiosphereData data) {
        double score = (data.floraDensity + data.faunaDensity + data.ecologicalBalance) / 3.0;

        if (score >= 0.8) return 5;
        else if (score >= 0.6) return 4;
        else if (score >= 0.4) return 3;
        else if (score >= 0.2) return 2;
        else return 1;
    }

    private void onBiosphereLevelChange(Orb orb, int newLevel) {
        // Trigger events based on biosphere level changes
        switch (newLevel) {
            case 2 -> {
                // Unlock basic ecosystem features
                spawnInitialFlora(orb);
            }
            case 3 -> {
                // Unlock intermediate features
                spawnIntermediateFlora(orb);
                spawnInitialFauna(orb);
            }
            case 4 -> {
                // Unlock advanced features
                spawnAdvancedFlora(orb);
                spawnIntermediateFauna(orb);
            }
            case 5 -> {
                // Unlock maximum ecosystem features
                spawnMaximumFlora(orb);
                spawnMaximumFauna(orb);
                triggerEcosystemEvent(orb, "blossoming");
            }
        }
    }

    private void spawnInitialFlora(Orb orb) {
        // Spawn basic plants and trees
        World world = plugin.getServer().getWorld(orb.getWorldName());
        if (world == null) return;

        Random random = new Random(orb.getSeed() + 100);
        int spawnCount = orb.getRadius() / 10; // Scale with planet size

        for (int i = 0; i < spawnCount; i++) {
            int x = orb.getCenterX() + random.nextInt(orb.getRadius() * 2) - orb.getRadius();
            int z = orb.getCenterZ() + random.nextInt(orb.getRadius() * 2) - orb.getRadius();
            int y = findSurfaceY(world, x, z, orb);

            if (y != -1) {
                Block block = world.getBlockAt(x, y, z);
                if (block.getType() == Material.GRASS_BLOCK) {
                    // Spawn grass or flowers
                    Material plant = random.nextDouble() < 0.7 ? Material.SHORT_GRASS : Material.DANDELION;
                    world.getBlockAt(x, y + 1, z).setType(plant);
                }
            }
        }
    }

    private void spawnIntermediateFlora(Orb orb) {
        // Spawn bushes, flowers, and small trees
        World world = plugin.getServer().getWorld(orb.getWorldName());
        if (world == null) return;

        Random random = new Random(orb.getSeed() + 200);
        int spawnCount = orb.getRadius() / 8;

        for (int i = 0; i < spawnCount; i++) {
            int x = orb.getCenterX() + random.nextInt(orb.getRadius() * 2) - orb.getRadius();
            int z = orb.getCenterZ() + random.nextInt(orb.getRadius() * 2) - orb.getRadius();
            int y = findSurfaceY(world, x, z, orb);

            if (y != -1) {
                Block block = world.getBlockAt(x, y, z);
                if (block.getType() == Material.GRASS_BLOCK) {
                    Material[] plants = {Material.POPPY, Material.BLUE_ORCHID, Material.ALLIUM, Material.AZURE_BLUET,
                                       Material.RED_TULIP, Material.ORANGE_TULIP, Material.WHITE_TULIP, Material.PINK_TULIP,
                                       Material.DANDELION};
                    Material plant = plants[random.nextInt(plants.length)];
                    world.getBlockAt(x, y + 1, z).setType(plant);
                }
            }
        }
    }

    private void spawnAdvancedFlora(Orb orb) {
        // Spawn rare plants and large vegetation
        World world = plugin.getServer().getWorld(orb.getWorldName());
        if (world == null) return;

        Random random = new Random(orb.getSeed() + 300);
        int spawnCount = orb.getRadius() / 6;

        for (int i = 0; i < spawnCount; i++) {
            int x = orb.getCenterX() + random.nextInt(orb.getRadius() * 2) - orb.getRadius();
            int z = orb.getCenterZ() + random.nextInt(orb.getRadius() * 2) - orb.getRadius();
            int y = findSurfaceY(world, x, z, orb);

            if (y != -1) {
                Block block = world.getBlockAt(x, y, z);
                if (block.getType() == Material.GRASS_BLOCK) {
                    Material[] rarePlants = {Material.LILAC, Material.ROSE_BUSH, Material.PEONY, Material.SUNFLOWER,
                                           Material.LARGE_FERN, Material.TALL_GRASS};
                    Material plant = rarePlants[random.nextInt(rarePlants.length)];
                    world.getBlockAt(x, y + 1, z).setType(plant);

                    // Some plants are 2 blocks tall
                    if (plant == Material.LILAC || plant == Material.ROSE_BUSH || plant == Material.PEONY ||
                        plant == Material.SUNFLOWER || plant == Material.LARGE_FERN || plant == Material.TALL_GRASS) {
                        world.getBlockAt(x, y + 2, z).setType(plant);
                    }
                }
            }
        }
    }

    private void spawnMaximumFlora(Orb orb) {
        // Spawn maximum variety including mushrooms and special plants
        World world = plugin.getServer().getWorld(orb.getWorldName());
        if (world == null) return;

        Random random = new Random(orb.getSeed() + 400);
        int spawnCount = orb.getRadius() / 4;

        for (int i = 0; i < spawnCount; i++) {
            int x = orb.getCenterX() + random.nextInt(orb.getRadius() * 2) - orb.getRadius();
            int z = orb.getCenterZ() + random.nextInt(orb.getRadius() * 2) - orb.getRadius();
            int y = findSurfaceY(world, x, z, orb);

            if (y != -1) {
                Block block = world.getBlockAt(x, y, z);
                if (block.getType() == Material.GRASS_BLOCK || block.getType() == Material.DIRT) {
                    Material[] maxPlants = {Material.BROWN_MUSHROOM, Material.RED_MUSHROOM, Material.CRIMSON_FUNGUS,
                                          Material.WARPED_FUNGUS, Material.WEEPING_VINES, Material.TWISTING_VINES,
                                          Material.GLOW_LICHEN, Material.SPORE_BLOSSOM};
                    Material plant = maxPlants[random.nextInt(maxPlants.length)];
                    world.getBlockAt(x, y + 1, z).setType(plant);
                }
            }
        }
    }

    private void spawnInitialFauna(Orb orb) {
        // This would integrate with Minecraft's mob spawning system
        // For now, just mark that fauna spawning is available
        BiosphereData data = biosphereData.get(orb.getId());
        if (data != null) {
            data.faunaDensity = 0.3;
        }
    }

    private void spawnIntermediateFauna(Orb orb) {
        BiosphereData data = biosphereData.get(orb.getId());
        if (data != null) {
            data.faunaDensity = 0.5;
        }
    }

    private void spawnMaximumFauna(Orb orb) {
        BiosphereData data = biosphereData.get(orb.getId());
        if (data != null) {
            data.faunaDensity = 0.8;
        }
    }

    private void triggerEcosystemEvent(Orb orb, String eventType) {
        // Trigger special ecosystem events
        switch (eventType) {
            case "blossoming" -> {
                // Maximum biosphere achieved - special effects
                plugin.getLogger().info("Planet " + orb.getName() + " has achieved maximum biosphere level!");
            }
        }
    }

    private int findSurfaceY(World world, int x, int z, Orb orb) {
        int centerY = orb.getCenterY();
        int radius = orb.getRadius();

        // Search from top to bottom within planet bounds
        for (int y = centerY + radius; y >= centerY - radius; y--) {
            Block block = world.getBlockAt(x, y, z);
            Block above = world.getBlockAt(x, y + 1, z);

            // Check if this is a surface block (solid below, air above)
            if (block.getType().isSolid() && above.getType() == Material.AIR) {
                return y;
            }
        }

        return -1; // No surface found
    }

    private void startBiosphereUpdater() {
        new BukkitRunnable() {
            @Override
            public void run() {
                // Periodic biosphere updates
                for (BiosphereData data : biosphereData.values()) {
                    // Natural regeneration
                    if (data.ecologicalBalance < 1.0) {
                        data.ecologicalBalance = Math.min(1.0, data.ecologicalBalance + 0.001);
                        data.orb.setEcologicalBalance(data.ecologicalBalance);
                    }

                    // Flora/fauna natural growth
                    if (data.floraDensity < 0.5) {
                        data.floraDensity = Math.min(0.5, data.floraDensity + 0.0005);
                    }

                    if (data.faunaDensity < 0.3) {
                        data.faunaDensity = Math.min(0.3, data.faunaDensity + 0.0002);
                    }
                }
            }
        }.runTaskTimer(plugin, 0L, 1200L); // Every minute
    }

    public BiosphereData getBiosphereData(UUID orbId) {
        return biosphereData.get(orbId);
    }

    public static class BiosphereData {
        public final Orb orb;
        public double floraDensity;
        public double faunaDensity;
        public double ecologicalBalance;

        public BiosphereData(Orb orb) {
            this.orb = orb;
            this.floraDensity = 0.1; // Start with minimal flora
            this.faunaDensity = 0.0; // No fauna initially
            this.ecologicalBalance = 1.0; // Neutral balance
        }
    }
}