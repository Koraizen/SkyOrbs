package com.skyorbs.features;

import com.skyorbs.biomes.BiomeType;
import com.skyorbs.core.Orb;
import org.bukkit.Material;
import org.bukkit.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class OreGenerator {

    public static List<BlockData> generateOres(Orb orb, BiomeType biome, World world) {
        List<BlockData> ores = new ArrayList<>();
        Random random = new Random(orb.getSeed() + 123);

        int cx = orb.getCenterX();
        int cy = orb.getCenterY();
        int cz = orb.getCenterZ();
        int radius = orb.getRadius();

        // Configurable ore layers per biome
        List<OreLayer> layers = getOreLayersForBiome(biome);

        for (OreLayer layer : layers) {
            for (OreConfig ore : layer.ores) {
                generateOreType(ores, ore, cx, cy, cz, radius, biome, random, layer.depthRange);
            }
        }

        // Core inclusions for solid planets
        if (!isHollowPlanet(orb)) { // Only for solid planets
            generateCoreInclusions(ores, cx, cy, cz, radius, biome, random);
        }

        return ores;
    }
    
    private static void generateOreType(List<BlockData> ores, OreConfig ore,
                                        int cx, int cy, int cz, int radius,
                                        BiomeType biome, Random random, double[] depthRange) {

        // Calculate depth-based position within the planet
        double minDepth = depthRange[0]; // 0.0 = surface, 1.0 = core
        double maxDepth = depthRange[1];

        int minY = (int)(cy - radius + (radius * 2 * minDepth));
        int maxY = (int)(cy - radius + (radius * 2 * maxDepth));

        if (maxY <= minY) return;

        int veinCount = (int)(radius * radius * ore.chance * 0.1); // Base count

        for (int i = 0; i < veinCount; i++) {
            // Generate position within planet volume
            double angle1 = random.nextDouble() * Math.PI * 2;
            double angle2 = random.nextDouble() * Math.PI;
            double distance = minDepth + random.nextDouble() * (maxDepth - minDepth);

            int x = cx + (int)(Math.sin(angle2) * Math.cos(angle1) * radius * distance);
            int z = cz + (int)(Math.sin(angle2) * Math.sin(angle1) * radius * distance);
            int y = minY + random.nextInt(maxY - minY + 1);

            // Ensure position is inside solid planet volume (not air/structure blocks)
            if (!isInsideSolidVolume(x - cx, y - cy, z - cz, radius)) continue;

            // Generate vein
            generateVein(ores, x, y, z, ore, random, cx, cy, cz, radius);
        }
    }
    
    /**
     * Generate ore vein within solid planet volume
     */
    private static void generateVein(List<BlockData> ores, int startX, int startY, int startZ,
                                     OreConfig ore, Random random, int cx, int cy, int cz, int radius) {

        int veinSize = ore.veinSize[0] + random.nextInt(ore.veinSize[1] - ore.veinSize[0] + 1);

        for (int i = 0; i < veinSize; i++) {
            double angle1 = random.nextDouble() * Math.PI * 2;
            double angle2 = random.nextDouble() * Math.PI;
            double distance = random.nextDouble() * 2.0; // Spread within vein

            int x = startX + (int)(Math.sin(angle2) * Math.cos(angle1) * distance);
            int y = startY + (int)((random.nextDouble() - 0.5) * 2.0);
            int z = startZ + (int)(Math.sin(angle2) * Math.sin(angle1) * distance);

            // Ensure still within solid volume
            if (!isInsideSolidVolume(x - cx, y - cy, z - cz, radius)) continue;

            // Deepslate variant for deeper ores
            Material material = ore.type;
            double depth = Math.sqrt((x-cx)*(x-cx) + (y-cy)*(y-cy) + (z-cz)*(z-cz)) / radius;
            if (depth > 0.6 && ore.deepslateVariant != null) {
                material = ore.deepslateVariant;
            }

            ores.add(new BlockData(x, y, z, material));
        }
    }
    
    private static boolean isInsideSolidVolume(int dx, int dy, int dz, int radius) {
        double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
        // Only generate inside solid volume - never in air or outside planet
        return distance <= radius - 1; // Leave 1 block surface layer
    }

    /**
     * Generate core inclusions (rare ores in planet core)
     */
    private static void generateCoreInclusions(List<BlockData> ores, int cx, int cy, int cz,
                                             int radius, BiomeType biome, Random random) {
        // Core region: central 20% of planet
        double coreRadius = radius * 0.2;
        int inclusionCount = (int)(coreRadius * coreRadius * 0.01); // Rare inclusions

        for (int i = 0; i < inclusionCount; i++) {
            // Generate within core sphere
            double angle1 = random.nextDouble() * Math.PI * 2;
            double angle2 = random.nextDouble() * Math.PI;
            double distance = random.nextDouble() * coreRadius;

            int x = cx + (int)(Math.sin(angle2) * Math.cos(angle1) * distance);
            int y = cy + (int)(Math.cos(angle2) * distance);
            int z = cz + (int)(Math.sin(angle2) * Math.sin(angle1) * distance);

            // Choose rare ore type
            Material inclusionType = switch (biome) {
                case LAVA_OCEAN, MAGMA_CAVES -> Material.ANCIENT_DEBRIS;
                case CRYSTAL_FOREST -> Material.DIAMOND_ORE;
                default -> random.nextDouble() < 0.5 ? Material.ANCIENT_DEBRIS : Material.DIAMOND_ORE;
            };

            ores.add(new BlockData(x, y, z, inclusionType));
        }
    }
    
    /**
     * Configurable ore configuration
     */
    private static class OreConfig {
        final Material type;
        final Material deepslateVariant;
        final double chance;
        final int[] veinSize;

        OreConfig(Material type, Material deepslateVariant, double chance, int[] veinSize) {
            this.type = type;
            this.deepslateVariant = deepslateVariant;
            this.chance = chance;
            this.veinSize = veinSize;
        }
    }

    /**
     * Ore layer configuration per biome
     */
    private static class OreLayer {
        final String name;
        final List<OreConfig> ores;
        final double[] depthRange; // [min, max] from surface inward (0.0 = surface, 1.0 = core)

        OreLayer(String name, List<OreConfig> ores, double[] depthRange) {
            this.name = name;
            this.ores = ores;
            this.depthRange = depthRange;
        }
    }

    /**
     * Get ore layers configuration for each biome
     */
    private static List<OreLayer> getOreLayersForBiome(BiomeType biome) {
        List<OreLayer> layers = new ArrayList<>();

        // Common layer - all biomes
        List<OreConfig> commonOres = List.of(
            new OreConfig(Material.COAL_ORE, Material.DEEPSLATE_COAL_ORE, 0.3, new int[]{2, 6}),
            new OreConfig(Material.IRON_ORE, Material.DEEPSLATE_IRON_ORE, 0.25, new int[]{2, 5}),
            new OreConfig(Material.COPPER_ORE, Material.DEEPSLATE_COPPER_ORE, 0.2, new int[]{3, 7})
        );
        layers.add(new OreLayer("common", commonOres, new double[]{0.2, 0.8}));

        // Rare layer - deeper
        List<OreConfig> rareOres = new ArrayList<>();
        rareOres.add(new OreConfig(Material.GOLD_ORE, Material.DEEPSLATE_GOLD_ORE, 0.08, new int[]{1, 4}));
        rareOres.add(new OreConfig(Material.REDSTONE_ORE, Material.DEEPSLATE_REDSTONE_ORE, 0.06, new int[]{2, 5}));
        rareOres.add(new OreConfig(Material.LAPIS_ORE, Material.DEEPSLATE_LAPIS_ORE, 0.04, new int[]{2, 5}));

        // Biome-specific rare ores
        switch (biome) {
            case LAVA_OCEAN, MAGMA_CAVES -> {
                rareOres.add(new OreConfig(Material.ANCIENT_DEBRIS, null, 0.02, new int[]{1, 3}));
            }
            case CRYSTAL_FOREST -> {
                rareOres.add(new OreConfig(Material.DIAMOND_ORE, Material.DEEPSLATE_DIAMOND_ORE, 0.03, new int[]{1, 4}));
            }
            case MEADOW, FOREST, TAIGA -> {
                rareOres.add(new OreConfig(Material.EMERALD_ORE, Material.DEEPSLATE_EMERALD_ORE, 0.01, new int[]{1, 1}));
            }
            case FROZEN_TUNDRA, GLACIER, ICE_SPIKES -> {
                rareOres.add(new OreConfig(Material.DIAMOND_ORE, Material.DEEPSLATE_DIAMOND_ORE, 0.04, new int[]{1, 4}));
            }
            case BADLANDS, CANYON -> {
                rareOres.add(new OreConfig(Material.GOLD_ORE, Material.DEEPSLATE_GOLD_ORE, 0.15, new int[]{2, 6}));
            }
            case JUNGLE, SAVANNA -> {
                rareOres.add(new OreConfig(Material.COPPER_ORE, Material.DEEPSLATE_COPPER_ORE, 0.12, new int[]{3, 8}));
            }
        }

        layers.add(new OreLayer("rare", rareOres, new double[]{0.5, 0.9}));

        return layers;
    }

    /**
     * Check if planet is hollow (temporary method until Orb class is updated)
     */
    private static boolean isHollowPlanet(Orb orb) {
        // For now, assume planets with certain shapes are hollow
        // This should be replaced with a proper isHollow() method in Orb class
        return false; // Default to solid for now
    }

    public static class BlockData {
        public final int x, y, z;
        public final Material material;

        public BlockData(int x, int y, int z, Material material) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.material = material;
        }
    }
}