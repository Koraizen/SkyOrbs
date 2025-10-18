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

        // Determine if planet is hollow
        boolean isHollow = orb.isHollow();

        // Calculate shell boundaries for hollow planets
        double innerRadius = isHollow ? radius * 0.6 : 0; // 60% hollow interior
        double shellStart = innerRadius;
        double shellEnd = radius - 1; // Leave surface layer intact

        // Configurable ore layers per biome
        List<OreLayer> layers = getOreLayersForBiome(biome);

        for (OreLayer layer : layers) {
            for (OreConfig ore : layer.ores) {
                generateOreType(ores, ore, cx, cy, cz, radius, shellStart, shellEnd, biome, random, layer.depthRange, isHollow);
            }
        }

        // Core inclusions only for solid planets
        if (!isHollow) {
            generateCoreInclusions(ores, cx, cy, cz, radius, biome, random);
        }

        return ores;
    }
    
    private static void generateOreType(List<BlockData> ores, OreConfig ore,
                                        int cx, int cy, int cz, int radius,
                                        double shellStart, double shellEnd,
                                        BiomeType biome, Random random, double[] depthRange,
                                        boolean isHollow) {

        // Calculate ore spawn count based on volume
        double volume;
        if (isHollow) {
            // Shell volume = outer sphere - inner sphere
            volume = (4.0/3.0) * Math.PI * (Math.pow(shellEnd, 3) - Math.pow(shellStart, 3));
        } else {
            // Full sphere volume
            volume = (4.0/3.0) * Math.PI * Math.pow(radius, 3);
        }

        // FIXED: Daha fazla ore için scale factor artırıldı
        int veinCount = (int)(volume * ore.chance * 0.001); // 10x daha fazla ore!
        veinCount = Math.max(5, veinCount); // En az 5 vein

        for (int i = 0; i < veinCount; i++) {
            // Generate random position within valid region
            double angle1 = random.nextDouble() * Math.PI * 2;
            double angle2 = random.nextDouble() * Math.PI;

            // FIXED: Distance calculation - ore'lar içerde olmalı!
            double distance;
            if (isHollow) {
                // Random distance between shell boundaries
                distance = shellStart + random.nextDouble() * (shellEnd - shellStart);
            } else {
                // SOLID PLANET: Merkeze yakın spawn (0 = merkez, radius = yüzey)
                // depthRange[0] = 0.3 (yüzeye yakın), depthRange[1] = 0.9 (merkeze yakın)
                // Distance 0'dan radius'a kadar olmalı
                double minDistance = radius * (1.0 - depthRange[1]); // Merkeze yakın
                double maxDistance = radius * (1.0 - depthRange[0]); // Yüzeye yakın
                distance = minDistance + random.nextDouble() * (maxDistance - minDistance);
            }

            // FIXED: Doğru koordinat hesaplama
            int x = cx + (int)(Math.sin(angle2) * Math.cos(angle1) * distance);
            int y = cy + (int)(Math.cos(angle2) * distance);
            int z = cz + (int)(Math.sin(angle2) * Math.sin(angle1) * distance);

            // Verify position is in valid region
            double actualDistance = Math.sqrt((x-cx)*(x-cx) + (y-cy)*(y-cy) + (z-cz)*(z-cz));

            if (isHollow) {
                // Must be within shell (between inner and outer radius)
                if (actualDistance < shellStart || actualDistance > shellEnd) {
                    continue;
                }
            } else {
                // FIXED: Must be INSIDE planet (not outside!)
                if (actualDistance > radius - 2) { // 2 blok içerde
                    continue;
                }
            }

            // Generate vein at this position
            generateVein(ores, x, y, z, ore, random, cx, cy, cz, radius, shellStart, shellEnd, isHollow);
        }
    }
    
    private static void generateVein(List<BlockData> ores, int startX, int startY, int startZ,
                                     OreConfig ore, Random random, int cx, int cy, int cz,
                                     int radius, double shellStart, double shellEnd, boolean isHollow) {

        int veinSize = ore.veinSize[0] + random.nextInt(ore.veinSize[1] - ore.veinSize[0] + 1);

        for (int i = 0; i < veinSize; i++) {
            // Small random offset for vein spread
            int offsetX = random.nextInt(3) - 1;
            int offsetY = random.nextInt(3) - 1;
            int offsetZ = random.nextInt(3) - 1;

            int x = startX + offsetX;
            int y = startY + offsetY;
            int z = startZ + offsetZ;

            // Verify each block is in valid region
            double distance = Math.sqrt((x-cx)*(x-cx) + (y-cy)*(y-cy) + (z-cz)*(z-cz));

            if (isHollow) {
                // Must be within shell
                if (distance < shellStart || distance > shellEnd) {
                    continue;
                }
            } else {
                // Must be within planet
                if (distance > radius - 1) {
                    continue;
                }
            }

            // Select material (deepslate variant for deeper ores)
            Material material = ore.type;
            double depthRatio = distance / radius;
            if (depthRatio < 0.4 && ore.deepslateVariant != null) { // Deep = closer to center
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

    private static List<OreLayer> getOreLayersForBiome(BiomeType biome) {
        List<OreLayer> layers = new ArrayList<>();

        // Common layer - all biomes
        List<OreConfig> commonOres = List.of(
            new OreConfig(Material.COAL_ORE, Material.DEEPSLATE_COAL_ORE, 0.3, new int[]{2, 6}),
            new OreConfig(Material.IRON_ORE, Material.DEEPSLATE_IRON_ORE, 0.25, new int[]{2, 5}),
            new OreConfig(Material.COPPER_ORE, Material.DEEPSLATE_COPPER_ORE, 0.2, new int[]{3, 7})
        );
        layers.add(new OreLayer("common", commonOres, new double[]{0.3, 0.9}));

        // Rare layer - deeper
        List<OreConfig> rareOres = new ArrayList<>();
        rareOres.add(new OreConfig(Material.GOLD_ORE, Material.DEEPSLATE_GOLD_ORE, 0.08, new int[]{1, 4}));
        rareOres.add(new OreConfig(Material.REDSTONE_ORE, Material.DEEPSLATE_REDSTONE_ORE, 0.06, new int[]{2, 5}));
        rareOres.add(new OreConfig(Material.LAPIS_ORE, Material.DEEPSLATE_LAPIS_ORE, 0.04, new int[]{2, 5}));

        // Biome-specific bonuses
        switch (biome) {
            case LAVA_OCEAN, MAGMA_CAVES, OBSIDIAN_PLAINS -> {
                rareOres.add(new OreConfig(Material.ANCIENT_DEBRIS, null, 0.03, new int[]{1, 3}));
                rareOres.add(new OreConfig(Material.GOLD_ORE, Material.DEEPSLATE_GOLD_ORE, 0.15, new int[]{2, 6}));
            }
            case CRYSTAL_FOREST, CRYSTALLINE -> {
                rareOres.add(new OreConfig(Material.DIAMOND_ORE, Material.DEEPSLATE_DIAMOND_ORE, 0.05, new int[]{1, 4}));
                rareOres.add(new OreConfig(Material.EMERALD_ORE, Material.DEEPSLATE_EMERALD_ORE, 0.03, new int[]{1, 2}));
            }
            case MEADOW, FOREST, TAIGA, JUNGLE -> {
                rareOres.add(new OreConfig(Material.EMERALD_ORE, Material.DEEPSLATE_EMERALD_ORE, 0.02, new int[]{1, 1}));
            }
            case FROZEN_TUNDRA, GLACIER, ICE_SPIKES -> {
                rareOres.add(new OreConfig(Material.DIAMOND_ORE, Material.DEEPSLATE_DIAMOND_ORE, 0.06, new int[]{1, 4}));
                rareOres.add(new OreConfig(Material.LAPIS_ORE, Material.DEEPSLATE_LAPIS_ORE, 0.08, new int[]{3, 7}));
            }
            case BADLANDS, CANYON, DESERT -> {
                rareOres.add(new OreConfig(Material.GOLD_ORE, Material.DEEPSLATE_GOLD_ORE, 0.18, new int[]{2, 6}));
                rareOres.add(new OreConfig(Material.COPPER_ORE, Material.DEEPSLATE_COPPER_ORE, 0.12, new int[]{3, 8}));
            }
            case BASALTIC -> {
                rareOres.add(new OreConfig(Material.DIAMOND_ORE, Material.DEEPSLATE_DIAMOND_ORE, 0.04, new int[]{1, 3}));
            }
        }

        layers.add(new OreLayer("rare", rareOres, new double[]{0.1, 0.6}));

        return layers;
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