package com.skyorbs;

import com.skyorbs.biomes.BiomeType;
import org.bukkit.Material;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

public class BiomeTypeTest {

    @Test
    public void testBiomeTypeCount() {
        BiomeType[] biomes = BiomeType.values();
        assertTrue(biomes.length >= 22, "Should have at least 22 biome types");
    }

    @Test
    public void testBiomeMaterialGeneration() {
        Random random = new Random(12345);
        BiomeType forest = BiomeType.FOREST;

        // Test surface material
        Material surface = forest.getMaterial(0, random);
        assertNotNull(surface);

        // Test subsurface material
        Material subsurface = forest.getMaterial(3, random);
        assertNotNull(subsurface);

        // Test core material
        Material core = forest.getMaterial(10, random);
        assertNotNull(core);
    }

    @Test
    public void testRandomBiomeSelection() {
        Random random = new Random(54321);
        BiomeType randomBiome = BiomeType.getRandomBiome(random);
        assertNotNull(randomBiome);
        assertTrue(randomBiome.name().length() > 0);
    }

    @Test
    public void testWeightedBiomeSelection() {
        Random random = new Random(11111);
        BiomeType weightedBiome = BiomeType.getRandomBiomeWeighted(random);
        assertNotNull(weightedBiome);
        assertTrue(weightedBiome.name().length() > 0);
    }

    @Test
    public void testBiomeDisplayNames() {
        for (BiomeType biome : BiomeType.values()) {
            assertNotNull(biome.getDisplayName());
            assertTrue(biome.getDisplayName().length() > 0);
        }
    }
}