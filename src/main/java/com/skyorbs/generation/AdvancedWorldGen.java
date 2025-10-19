package com.skyorbs.generation;

import com.skyorbs.SkyOrbs;
import com.skyorbs.core.Orb;
import com.skyorbs.core.PlanetType;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.util.noise.SimplexOctaveGenerator;

import java.util.*;

public class AdvancedWorldGen {

    private final SkyOrbs plugin;
    private final Random random = new Random();

    public AdvancedWorldGen(SkyOrbs plugin) {
        this.plugin = plugin;
    }

    /**
     * Advanced procedural generation using Perlin + Voronoi hybrid
     */
    public List<BlockPlacement> generateAdvancedTerrain(Orb orb, PlanetType planetType) {
        List<BlockPlacement> blocks = new ArrayList<BlockPlacement>();
        int cx = orb.getCenterX();
        int cy = orb.getCenterY();
        int cz = orb.getCenterZ();
        int radius = orb.getRadius();
        long seed = orb.getSeed();

        // Initialize noise generators
        SimplexOctaveGenerator perlinNoise = new SimplexOctaveGenerator(seed, 8);
        perlinNoise.setScale(0.01);

        SimplexOctaveGenerator detailNoise = new SimplexOctaveGenerator(seed + 1, 4);
        detailNoise.setScale(0.05);

        // Generate terrain using hybrid approach
        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                // Check if within planet bounds (basic sphere)
                double distance = Math.sqrt(x * x + z * z);
                if (distance > radius) continue;

                // Calculate height using Perlin noise
                double perlinValue = perlinNoise.noise(x, z, 0.5);
                double detailValue = detailNoise.noise(x, z, 0.5);
                double combinedNoise = (perlinValue * 0.7) + (detailValue * 0.3);

                // Apply Voronoi-like features for interesting terrain
                double voronoiFeature = calculateVoronoiFeature(x, z, seed);

                // Calculate final height
                int height = (int) (combinedNoise * radius * 0.5);
                height = Math.max(-radius, Math.min(radius, height));

                // Add voronoi influence
                height += (int) (voronoiFeature * radius * 0.2);

                // Generate vertical column
                for (int y = -radius; y <= height; y++) {
                    double verticalDistance = Math.sqrt(x * x + y * y + z * z);
                    if (verticalDistance <= radius) {
                        Material material = getTerrainMaterial(planetType, y, height, combinedNoise, seed);
                        blocks.add(new BlockPlacement(cx + x, cy + y, cz + z, material));
                    }
                }
            }
        }

        return blocks;
    }

    /**
     * Calculate Voronoi-like features for terrain variation
     */
    private double calculateVoronoiFeature(int x, int z, long seed) {
        // Simple voronoi-like calculation for terrain features
        double minDistance = Double.MAX_VALUE;

        // Sample nearby points
        for (int dx = -2; dx <= 2; dx++) {
            for (int dz = -2; dz <= 2; dz++) {
                int px = (x / 16) * 16 + dx * 8;
                int pz = (z / 16) * 16 + dz * 8;

                Random pointRandom = new Random(seed + px * 31 + pz);
                int offsetX = pointRandom.nextInt(8) - 4;
                int offsetZ = pointRandom.nextInt(8) - 4;

                double distance = Math.sqrt((x - (px + offsetX)) * (x - (px + offsetX)) +
                                          (z - (pz + offsetZ)) * (z - (pz + offsetZ)));
                minDistance = Math.min(minDistance, distance);
            }
        }

        return Math.max(0, 1.0 - minDistance / 8.0);
    }

    /**
     * Get terrain material based on planet type and depth
     */
    private Material getTerrainMaterial(PlanetType planetType, int y, int surfaceHeight, double noise, long seed) {
        Random materialRandom = new Random(seed + y * 17 + surfaceHeight);

        switch (planetType) {
            case TERRESTRIAL -> {
                if (y == surfaceHeight) {
                    return materialRandom.nextDouble() < 0.7 ? Material.GRASS_BLOCK : Material.DIRT;
                } else if (y > surfaceHeight - 3) {
                    return Material.DIRT;
                } else {
                    return Material.STONE;
                }
            }
            case GAS -> {
                // Gas planets have layered gas materials
                double layer = (double) y / surfaceHeight;
                if (layer > 0.8) return Material.BLUE_WOOL;
                else if (layer > 0.6) return Material.LIGHT_BLUE_WOOL;
                else if (layer > 0.4) return Material.CYAN_WOOL;
                else return Material.BLUE_WOOL;
            }
            case LAVA -> {
                if (y >= surfaceHeight - 2) {
                    return materialRandom.nextDouble() < 0.8 ? Material.MAGMA_BLOCK : Material.LAVA;
                } else {
                    return Material.NETHERRACK;
                }
            }
            case ICE -> {
                if (y == surfaceHeight) {
                    return Material.SNOW_BLOCK;
                } else if (y > surfaceHeight - 5) {
                    return Material.ICE;
                } else {
                    return Material.PACKED_ICE;
                }
            }
            case CRYSTAL -> {
                double crystalChance = Math.abs(noise) * 0.5;
                if (materialRandom.nextDouble() < crystalChance) {
                    return Material.AMETHYST_BLOCK;
                } else {
                    return Material.QUARTZ_BLOCK;
                }
            }
            case SHADOW -> {
                return Material.BLACK_CONCRETE;
            }
            case TOXIC -> {
                if (y == surfaceHeight) {
                    return Material.SLIME_BLOCK;
                } else {
                    return Material.GREEN_CONCRETE;
                }
            }
            default -> {
                return Material.STONE;
            }
        }
    }

    /**
     * Generate multi-layered ore maps
     */
    public List<BlockPlacement> generateOreLayers(Orb orb, PlanetType planetType) {
        List<BlockPlacement> ores = new ArrayList<BlockPlacement>();
        int cx = orb.getCenterX();
        int cy = orb.getCenterY();
        int cz = orb.getCenterZ();
        int radius = orb.getRadius();
        long seed = orb.getSeed();

        // Define ore layers with depth ranges
        Map<OreLayer, Double> oreLayers = getOreConfiguration(planetType);

        for (Map.Entry<OreLayer, Double> entry : oreLayers.entrySet()) {
            OreLayer layer = entry.getKey();
            double density = entry.getValue();

            SimplexOctaveGenerator oreNoise = new SimplexOctaveGenerator(seed + layer.hashCode(), 4);
            oreNoise.setScale(0.02);

            // Generate ores within the layer's depth range
            for (int x = -radius; x <= radius; x++) {
                for (int z = -radius; z <= radius; z++) {
                    for (int y = layer.minDepth; y <= layer.maxDepth; y++) {
                        double distance = Math.sqrt(x * x + y * y + z * z);
                        if (distance > radius) continue;

                        // Check if we're in the right material
                        if (!isValidOreLocation(planetType, y)) continue;

                        // Use noise to determine ore placement
                        double noiseValue = oreNoise.noise(x, y, z);
                        if (Math.abs(noiseValue) < density) {
                            ores.add(new BlockPlacement(cx + x, cy + y, cz + z, layer.material));
                        }
                    }
                }
            }
        }

        return ores;
    }

    /**
     * Check if location is valid for ore placement
     */
    private boolean isValidOreLocation(PlanetType planetType, int y) {
        switch (planetType) {
            case TERRESTRIAL -> {
                return y < 0; // Underground only
            }
            case LAVA -> {
                return y < 10; // Near surface in lava planets
            }
            case CRYSTAL -> {
                return true; // Crystals can be anywhere
            }
            case ICE -> {
                return y < -5; // Deep under ice
            }
            default -> {
                return y < 0; // Underground by default
            }
        }
    }

    /**
     * Get ore configuration for planet type
     */
    private Map<OreLayer, Double> getOreConfiguration(PlanetType planetType) {
        Map<OreLayer, Double> layers = new HashMap<OreLayer, Double>();

        switch (planetType) {
            case TERRESTRIAL -> {
                layers.put(new OreLayer(Material.COAL_ORE, -64, 128), 0.15);
                layers.put(new OreLayer(Material.IRON_ORE, -64, 72), 0.12);
                layers.put(new OreLayer(Material.COPPER_ORE, 0, 96), 0.10);
                layers.put(new OreLayer(Material.GOLD_ORE, -64, 32), 0.04);
                layers.put(new OreLayer(Material.DIAMOND_ORE, -64, 16), 0.008);
                layers.put(new OreLayer(Material.EMERALD_ORE, -16, 320), 0.005);
            }
            case LAVA -> {
                layers.put(new OreLayer(Material.NETHERITE_BLOCK, -64, -32), 0.02);
                layers.put(new OreLayer(Material.ANCIENT_DEBRIS, -64, -16), 0.01);
                layers.put(new OreLayer(Material.GOLD_ORE, -32, 64), 0.08);
            }
            case CRYSTAL -> {
                layers.put(new OreLayer(Material.DIAMOND_ORE, -64, 64), 0.05);
                layers.put(new OreLayer(Material.EMERALD_ORE, -32, 96), 0.03);
                layers.put(new OreLayer(Material.LAPIS_ORE, 0, 32), 0.10);
            }
            case ICE -> {
                layers.put(new OreLayer(Material.DIAMOND_ORE, -64, -16), 0.015);
                layers.put(new OreLayer(Material.GOLD_ORE, -32, 16), 0.06);
            }
            // Add more planet-specific ore configs...
        }

        return layers;
    }

    /**
     * Ore layer definition
     */
    private static class OreLayer {
        final Material material;
        final int minDepth;
        final int maxDepth;

        OreLayer(Material material, int minDepth, int maxDepth) {
            this.material = material;
            this.minDepth = minDepth;
            this.maxDepth = maxDepth;
        }

        @Override
        public int hashCode() {
            return material.hashCode() + minDepth * 31 + maxDepth * 37;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof OreLayer)) return false;
            OreLayer other = (OreLayer) obj;
            return material == other.material && minDepth == other.minDepth && maxDepth == other.maxDepth;
        }
    }

    /**
     * Block placement helper class
     */
    public static class BlockPlacement {
        public final int x, y, z;
        public final Material material;

        public BlockPlacement(int x, int y, int z, Material material) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.material = material;
        }
    }
}