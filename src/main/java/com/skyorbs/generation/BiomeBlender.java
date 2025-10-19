package com.skyorbs.generation;

import com.skyorbs.core.Orb;
import com.skyorbs.core.PlanetType;
import org.bukkit.Material;
import org.bukkit.util.noise.SimplexOctaveGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BiomeBlender {

    /**
     * Implement soft biome blending algorithm
     */
    public List<AdvancedWorldGen.BlockPlacement> blendBiomes(Orb orb, PlanetType primaryType, List<PlanetType> secondaryTypes) {
        List<AdvancedWorldGen.BlockPlacement> blendedBlocks = new ArrayList<>();
        int cx = orb.getCenterX();
        int cy = orb.getCenterY();
        int cz = orb.getCenterZ();
        int radius = orb.getRadius();
        long seed = orb.getSeed();

        // Create noise generators for blending
        SimplexOctaveGenerator blendNoise = new SimplexOctaveGenerator(seed + 1000, 6);
        blendNoise.setScale(0.005);

        SimplexOctaveGenerator detailBlend = new SimplexOctaveGenerator(seed + 2000, 4);
        detailBlend.setScale(0.02);

        // Generate blended terrain
        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                double distance = Math.sqrt(x * x + z * z);
                if (distance > radius) continue;

                // Calculate blend factors
                double blendValue = blendNoise.noise(x, z, 0.5);
                double detailValue = detailBlend.noise(x, z, 0.5);

                // Determine which biomes to blend at this location
                BiomeBlend blend = calculateBiomeBlend(primaryType, secondaryTypes, blendValue, detailValue, distance, radius);

                // Generate terrain with blended materials
                int height = calculateBlendedHeight(orb, x, z, blend, seed);
                for (int y = -radius; y <= height; y++) {
                    double verticalDistance = Math.sqrt(x * x + y * y + z * z);
                    if (verticalDistance <= radius) {
                        Material material = getBlendedMaterial(blend, y, height, seed + x * 31 + z * 37 + y);
                        blendedBlocks.add(new AdvancedWorldGen.BlockPlacement(cx + x, cy + y, cz + z, material));
                    }
                }
            }
        }

        return blendedBlocks;
    }

    private BiomeBlend calculateBiomeBlend(PlanetType primary, List<PlanetType> secondary, double blendValue, double detailValue, double distance, int radius) {
        // Calculate blend weights based on noise and distance
        double distanceFactor = distance / radius; // 0 at center, 1 at edge

        // Primary biome weight (stronger at center)
        double primaryWeight = Math.max(0.3, 1.0 - distanceFactor * 0.7);

        // Secondary biome weights based on noise
        List<Double> secondaryWeights = new ArrayList<>();
        double remainingWeight = 1.0 - primaryWeight;

        if (!secondary.isEmpty()) {
            double totalSecondaryWeight = 0;
            for (int i = 0; i < secondary.size(); i++) {
                double weight = Math.abs(blendValue + detailValue * 0.5) * (1.0 / secondary.size());
                weight = Math.max(0.1, Math.min(0.4, weight));
                secondaryWeights.add(weight);
                totalSecondaryWeight += weight;
            }

            // Normalize secondary weights
            if (totalSecondaryWeight > 0) {
                for (int i = 0; i < secondaryWeights.size(); i++) {
                    secondaryWeights.set(i, secondaryWeights.get(i) / totalSecondaryWeight * remainingWeight);
                }
            }
        }

        return new BiomeBlend(primary, secondary, primaryWeight, secondaryWeights);
    }

    private int calculateBlendedHeight(Orb orb, int x, int z, BiomeBlend blend, long seed) {
        // Calculate height based on blended biome characteristics
        double height = 0;
        double totalWeight = blend.primaryWeight;

        // Primary biome contribution
        height += getBiomeBaseHeight(blend.primary) * blend.primaryWeight;

        // Secondary biome contributions
        for (int i = 0; i < blend.secondaryTypes.size(); i++) {
            PlanetType secondary = blend.secondaryTypes.get(i);
            double weight = blend.secondaryWeights.get(i);
            height += getBiomeBaseHeight(secondary) * weight;
            totalWeight += weight;
        }

        // Add noise variation
        SimplexOctaveGenerator heightNoise = new SimplexOctaveGenerator(seed, 4);
        heightNoise.setScale(0.01);
        double noiseVariation = heightNoise.noise(x, z, 0.5) * 10;

        return (int) Math.max(-orb.getRadius(), Math.min(orb.getRadius(), height + noiseVariation));
    }

    private double getBiomeBaseHeight(PlanetType type) {
        return switch (type) {
            case TERRESTRIAL -> 20.0;
            case GAS -> 15.0;
            case LAVA -> 25.0;
            case ICE -> 18.0;
            case CRYSTAL -> 22.0;
            case SHADOW -> 12.0;
            case TOXIC -> 16.0;
        };
    }

    private Material getBlendedMaterial(BiomeBlend blend, int y, int surfaceHeight, long seed) {
        Random random = new Random(seed);

        // Determine material based on depth and blended biome properties
        boolean isSurface = y == surfaceHeight;
        boolean isNearSurface = y > surfaceHeight - 3;

        // Calculate which biome's material to use
        double rand = random.nextDouble();
        double cumulativeWeight = blend.primaryWeight;

        if (rand <= cumulativeWeight) {
            // Use primary biome material
            return getBiomeMaterial(blend.primary, y, surfaceHeight, isSurface, isNearSurface, random);
        }

        // Check secondary biomes
        for (int i = 0; i < blend.secondaryTypes.size(); i++) {
            cumulativeWeight += blend.secondaryWeights.get(i);
            if (rand <= cumulativeWeight) {
                return getBiomeMaterial(blend.secondaryTypes.get(i), y, surfaceHeight, isSurface, isNearSurface, random);
            }
        }

        // Fallback to primary
        return getBiomeMaterial(blend.primary, y, surfaceHeight, isSurface, isNearSurface, random);
    }

    private Material getBiomeMaterial(PlanetType type, int y, int surfaceHeight, boolean isSurface, boolean isNearSurface, Random random) {
        switch (type) {
            case TERRESTRIAL -> {
                if (isSurface) return random.nextDouble() < 0.7 ? Material.GRASS_BLOCK : Material.DIRT;
                if (isNearSurface) return Material.DIRT;
                return Material.STONE;
            }
            case GAS -> {
                double layer = (double) y / surfaceHeight;
                if (layer > 0.8) return Material.BLUE_WOOL;
                else if (layer > 0.6) return Material.LIGHT_BLUE_WOOL;
                else if (layer > 0.4) return Material.CYAN_WOOL;
                else return Material.BLUE_WOOL;
            }
            case LAVA -> {
                if (isSurface || isNearSurface) return Material.MAGMA_BLOCK;
                return Material.NETHERRACK;
            }
            case ICE -> {
                if (isSurface) return Material.SNOW_BLOCK;
                if (isNearSurface) return Material.ICE;
                return Material.PACKED_ICE;
            }
            case CRYSTAL -> {
                if (random.nextDouble() < 0.3) return Material.AMETHYST_BLOCK;
                return Material.QUARTZ_BLOCK;
            }
            case SHADOW -> {
                return Material.BLACK_CONCRETE;
            }
            case TOXIC -> {
                if (isSurface) return Material.SLIME_BLOCK;
                return Material.GREEN_CONCRETE;
            }
            default -> {
                return Material.STONE;
            }
        }
    }

    private static class BiomeBlend {
        final PlanetType primary;
        final List<PlanetType> secondaryTypes;
        final double primaryWeight;
        final List<Double> secondaryWeights;

        BiomeBlend(PlanetType primary, List<PlanetType> secondaryTypes, double primaryWeight, List<Double> secondaryWeights) {
            this.primary = primary;
            this.secondaryTypes = secondaryTypes;
            this.primaryWeight = primaryWeight;
            this.secondaryWeights = secondaryWeights;
        }
    }
}