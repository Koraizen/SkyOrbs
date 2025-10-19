package com.skyorbs.generation;

import com.skyorbs.SkyOrbs;
import com.skyorbs.biomes.BiomeType;
import com.skyorbs.core.Orb;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class SatelliteGenerator {

    private final SkyOrbs plugin;

    public SatelliteGenerator(SkyOrbs plugin) {
        this.plugin = plugin;
    }

    public List<Orb> generateSatellitesForPlanet(Orb planet, World world) {
        List<Orb> satellites = new ArrayList<Orb>();

        if (!plugin.getConfigManager().isSatellitesEnabled()) {
            return satellites;
        }

        Random random = new Random(planet.getSeed() + 54321);

        if (random.nextDouble() > plugin.getConfigManager().getSatelliteProbability()) {
            return satellites;
        }

        int minCount = plugin.getConfigManager().getMinSatellitesPerPlanet();
        int maxCount = plugin.getConfigManager().getMaxSatellitesPerPlanet();
        int count = minCount + random.nextInt(maxCount - minCount + 1);

        for (int i = 0; i < count; i++) {
            int radius = 30 + random.nextInt(41); // 30-70 radius

            int safetyBuffer = 100;
            int minDist = planet.getRadius() + radius + safetyBuffer;
            int maxDist = planet.getRadius() + radius + safetyBuffer + 300;

            double angle = random.nextDouble() * 2 * Math.PI;
            int distance = minDist + random.nextInt(Math.max(1, maxDist - minDist));

            int x = planet.getCenterX() + (int)(Math.cos(angle) * distance);
            int z = planet.getCenterZ() + (int)(Math.sin(angle) * distance);
            int y = planet.getCenterY() + random.nextInt(61) - 30;

            BiomeType biome = BiomeType.getRandomBiomeWeighted(random);

            // Select satellite shape based on biome
            String shapeName = getSatelliteShapeForBiome(biome, random);

            Orb satellite = new Orb(
                UUID.randomUUID(),
                planet.getName() + "_Satellite_" + (i + 1),
                world.getName(),
                x, y, z,
                radius,
                shapeName,
                biome.name(),
                planet.getSeed() + i + 5000,
                System.currentTimeMillis(),
                true,
                planet.getId()
            );

            satellites.add(satellite);
        }

        return satellites;
    }

    /**
     * Generate planetary rings around planets
     */
    public void generateRingsForPlanet(Orb planet, World world) {
        Random random = new Random(planet.getSeed() + 99999);

        // Configurable ring chance
        double ringChance = plugin.getConfig().getDouble("satellites.ring_chance", 0.2);
        if (random.nextDouble() > ringChance) {
            return;
        }

        int ringCount = 1 + random.nextInt(3); // 1-3 rings
        int ringRadius = planet.getRadius() + 50 + random.nextInt(100); // 50-150 blocks from planet surface

        for (int ring = 0; ring < ringCount; ring++) {
            int ringY = planet.getCenterY() + random.nextInt(21) - 10; // ±10 blocks from center
            int ringThickness = 2 + random.nextInt(4); // 2-5 blocks thick

            // Generate ring particles/blocks
            generateRingParticles(planet, world, ringRadius + ring * 20, ringY, ringThickness, random);
        }
    }

    /**
     * Generate ring visual effects
     */
    private void generateRingParticles(Orb planet, World world, int ringRadius, int ringY, int thickness, Random random) {
        int cx = planet.getCenterX();
        int cy = planet.getCenterY();
        int cz = planet.getCenterZ();

        // Create ring blocks for visual effect (sparse for performance)
        int particleCount = Math.min(200, ringRadius * 4); // Limit particles for performance

        for (int i = 0; i < particleCount; i++) {
            double angle = random.nextDouble() * 2 * Math.PI;
            double radiusVariation = ringRadius + random.nextInt(11) - 5; // ±5 variation

            int x = cx + (int)(Math.cos(angle) * radiusVariation);
            int z = cz + (int)(Math.sin(angle) * radiusVariation);
            int y = ringY + random.nextInt(thickness * 2) - thickness;

            // Only place if in air (don't overwrite planet blocks)
            Block block = world.getBlockAt(x, y, z);
            if (block.getType() == Material.AIR || block.getType() == Material.CAVE_AIR) {
                // Choose ring material based on planet biome
                Material ringMaterial = getRingMaterialForBiome(BiomeType.valueOf(planet.getBiomeName()), random);
                block.setType(ringMaterial, false);
            }
        }
    }

    /**
     * Get appropriate satellite shape for biome
     */
    private String getSatelliteShapeForBiome(BiomeType biome, Random random) {
        return switch (biome) {
            case CRYSTAL_FOREST, CRYSTALLINE -> "DIAMOND"; // Crystal satellites
            case LAVA_OCEAN, MAGMA_CAVES -> "SPHERE"; // Smooth volcanic satellites
            case VOID -> "CUBE"; // Geometric void satellites
            case CORRUPTED -> "SPIKY"; // Spiky corrupted satellites
            case FROZEN_TUNDRA, GLACIER -> "OCTAHEDRON"; // Geometric ice satellites
            default -> random.nextBoolean() ? "SPHERE" : "ASTEROID";
        };
    }

    /**
     * Get ring material based on planet biome
     */
    private Material getRingMaterialForBiome(BiomeType biome, Random random) {
        return switch (biome) {
            case CRYSTAL_FOREST, CRYSTALLINE -> random.nextBoolean() ? Material.AMETHYST_BLOCK : Material.QUARTZ_BLOCK;
            case LAVA_OCEAN, MAGMA_CAVES -> random.nextBoolean() ? Material.OBSIDIAN : Material.BLACKSTONE;
            case VOID -> Material.END_STONE;
            case CORRUPTED -> random.nextBoolean() ? Material.WARPED_NYLIUM : Material.CRIMSON_NYLIUM;
            case FROZEN_TUNDRA, GLACIER -> random.nextBoolean() ? Material.PACKED_ICE : Material.BLUE_ICE;
            case LUMINOUS -> random.nextBoolean() ? Material.SEA_LANTERN : Material.GLOWSTONE;
            default -> random.nextBoolean() ? Material.STONE : Material.ANDESITE;
        };
    }
}
