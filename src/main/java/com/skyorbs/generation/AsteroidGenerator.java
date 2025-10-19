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

public class AsteroidGenerator {

    private final SkyOrbs plugin;

    public AsteroidGenerator(SkyOrbs plugin) {
        this.plugin = plugin;
    }

    public List<Orb> generateAsteroidsForPlanet(Orb planet, World world) {
        List<Orb> asteroids = new ArrayList<Orb>();

        if (!plugin.getConfigManager().isAsteroidsEnabled()) {
            return asteroids;
        }

        Random random = new Random(planet.getSeed() + 12345);

        int minCount = plugin.getConfigManager().getMinAsteroidsPerPlanet();
        int maxCount = plugin.getConfigManager().getMaxAsteroidsPerPlanet();
        int count = minCount + random.nextInt(maxCount - minCount + 1);

        for (int i = 0; i < count; i++) {
            int radius = plugin.getConfigManager().getMinAsteroidRadius() +
                        random.nextInt(plugin.getConfigManager().getMaxAsteroidRadius() -
                                     plugin.getConfigManager().getMinAsteroidRadius() + 1);

            double angle = random.nextDouble() * 2 * Math.PI;
            int distance = plugin.getConfigManager().getMinAsteroidDistance() +
                          random.nextInt(plugin.getConfigManager().getMaxAsteroidDistance() -
                                       plugin.getConfigManager().getMinAsteroidDistance());

            int x = planet.getCenterX() + (int)(Math.cos(angle) * distance);
            int z = planet.getCenterZ() + (int)(Math.sin(angle) * distance);
            int y = planet.getCenterY() + random.nextInt(41) - 20;

            BiomeType biome = BiomeType.getRandomBiomeWeighted(random);

            // Select asteroid shape and add visual effects
            String shapeName = getAsteroidShapeForBiome(biome, random);

            Orb asteroid = new Orb(
                UUID.randomUUID(),
                planet.getName() + "_Asteroid_" + (i + 1),
                world.getName(),
                x, y, z,
                radius,
                shapeName,
                biome.name(),
                planet.getSeed() + i + 1000,
                System.currentTimeMillis(),
                true,
                planet.getId()
            );

            asteroids.add(asteroid);

            // Add asteroid field effects (debris around asteroid)
            if (random.nextDouble() < 0.3) { // 30% chance for asteroid field
                generateAsteroidField(planet, world, x, y, z, radius, random);
            }
        }

        return asteroids;
    }

    /**
     * Generate asteroid field debris around asteroid
     */
    private void generateAsteroidField(Orb planet, World world, int ax, int ay, int az, int asteroidRadius, Random random) {
        int debrisCount = 5 + random.nextInt(15); // 5-20 debris pieces

        for (int i = 0; i < debrisCount; i++) {
            // Generate debris within 50-100 blocks of asteroid
            double angle = random.nextDouble() * 2 * Math.PI;
            double distance = 50 + random.nextDouble() * 50;

            int x = ax + (int)(Math.cos(angle) * distance);
            int z = az + (int)(Math.sin(angle) * distance);
            int y = ay + random.nextInt(21) - 10; // ±10 blocks

            // Only place if not too close to planet
            double planetDist = Math.sqrt((x - planet.getCenterX()) * (x - planet.getCenterX()) +
                                        (y - planet.getCenterY()) * (y - planet.getCenterY()) +
                                        (z - planet.getCenterZ()) * (z - planet.getCenterZ()));

            if (planetDist > planet.getRadius() + 20) { // At least 20 blocks from planet surface
                Block block = world.getBlockAt(x, y, z);
                if (block.getType() == Material.AIR || block.getType() == Material.CAVE_AIR) {
                    Material debrisMaterial = getDebrisMaterial(random);
                    block.setType(debrisMaterial, false);
                }
            }
        }
    }

    /**
     * Get appropriate asteroid shape for biome
     */
    private String getAsteroidShapeForBiome(BiomeType biome, Random random) {
        return switch (biome) {
            case CRYSTAL_FOREST, CRYSTALLINE -> random.nextBoolean() ? "DIAMOND" : "OCTAHEDRON";
            case LAVA_OCEAN, MAGMA_CAVES -> "SPHERE"; // Smooth volcanic asteroids
            case VOID -> "CUBE"; // Geometric void asteroids
            case CORRUPTED -> "SPIKY"; // Spiky corrupted asteroids
            case FROZEN_TUNDRA, GLACIER -> "FRACTAL"; // Irregular ice asteroids
            default -> "ASTEROID"; // Default irregular shape
        };
    }

    /**
     * Get random debris material for asteroid fields
     */
    private Material getDebrisMaterial(Random random) {
        Material[] debrisMaterials = {
            Material.STONE, Material.COBBLESTONE, Material.ANDESITE, Material.DIORITE, Material.GRANITE,
            Material.IRON_ORE, Material.COAL_ORE, Material.GOLD_ORE, Material.REDSTONE_ORE
        };
        return debrisMaterials[random.nextInt(debrisMaterials.length)];
    }
}
