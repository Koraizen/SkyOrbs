package com.skyorbs.features;

import com.skyorbs.biomes.BiomeType;
import com.skyorbs.core.Orb;
import org.bukkit.Material;
import org.bukkit.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class StructureGenerator {
    
    public static List<OreGenerator.BlockData> generateStructures(Orb orb, BiomeType biome, World world) {
        List<OreGenerator.BlockData> blocks = new ArrayList<>();
        Random random = new Random(orb.getSeed() + 789);
        
        int cx = orb.getCenterX();
        int cy = orb.getCenterY();
        int cz = orb.getCenterZ();
        int radius = orb.getRadius();
        
        int structureCount = radius / 40 + random.nextInt(3);
        
        for (int i = 0; i < structureCount; i++) {
            int angle = random.nextInt(360);
            int dist = random.nextInt(radius - 10);
            
            double rad = Math.toRadians(angle);
            int x = cx + (int)(Math.cos(rad) * dist);
            int z = cz + (int)(Math.sin(rad) * dist);
            int y = cy + (int)Math.sqrt(radius * radius - dist * dist);
            
            StructureType type = getStructureType(biome, random);
            generateStructure(blocks, x, y, z, type, biome, random);
        }
        
        return blocks;
    }
    
    private static StructureType getStructureType(BiomeType biome, Random random) {
        return switch (biome) {
            case FOREST, PLAINS, MEADOW -> random.nextBoolean() ? StructureType.TOWER : StructureType.RUINS;
            case DESERT, BADLANDS -> StructureType.PYRAMID;
            case FROZEN_TUNDRA, ICE_SPIKES -> StructureType.ICE_SPIKE;
            case CRYSTAL_FOREST -> StructureType.CRYSTAL_SPIRE;
            case LAVA_OCEAN, MAGMA_CAVES -> StructureType.LAVA_SHRINE;
            case VOID, CORRUPTED -> StructureType.ALIEN_MONOLITH;
            default -> StructureType.RUINS;
        };
    }
    
    private static void generateStructure(List<OreGenerator.BlockData> blocks, int x, int y, int z, 
                                          StructureType type, BiomeType biome, Random random) {
        switch (type) {
            case TOWER -> generateTower(blocks, x, y, z, biome);
            case RUINS -> generateRuins(blocks, x, y, z, biome, random);
            case PYRAMID -> generatePyramid(blocks, x, y, z, biome);
            case ICE_SPIKE -> generateIceSpike(blocks, x, y, z);
            case CRYSTAL_SPIRE -> generateCrystalSpire(blocks, x, y, z);
            case LAVA_SHRINE -> generateLavaShrine(blocks, x, y, z);
            case ALIEN_MONOLITH -> generateAlienMonolith(blocks, x, y, z);
        }
    }
    
    private static void generateTower(List<OreGenerator.BlockData> blocks, int x, int y, int z, BiomeType biome) {
        Material material = Material.STONE_BRICKS;
        int height = 8;
        
        for (int i = 0; i < height; i++) {
            blocks.add(new OreGenerator.BlockData(x, y + i, z, material));
            blocks.add(new OreGenerator.BlockData(x + 2, y + i, z, material));
            blocks.add(new OreGenerator.BlockData(x, y + i, z + 2, material));
            blocks.add(new OreGenerator.BlockData(x + 2, y + i, z + 2, material));
        }
        
        for (int dx = 0; dx <= 2; dx++) {
            for (int dz = 0; dz <= 2; dz++) {
                blocks.add(new OreGenerator.BlockData(x + dx, y + height, z + dz, Material.STONE_BRICK_SLAB));
            }
        }
    }
    
    private static void generateRuins(List<OreGenerator.BlockData> blocks, int x, int y, int z, BiomeType biome, Random random) {
        Material material = Material.CRACKED_STONE_BRICKS;
        
        for (int dx = -2; dx <= 2; dx++) {
            for (int dz = -2; dz <= 2; dz++) {
                if (random.nextDouble() < 0.6) {
                    blocks.add(new OreGenerator.BlockData(x + dx, y, z + dz, material));
                    if (random.nextDouble() < 0.3) {
                        blocks.add(new OreGenerator.BlockData(x + dx, y + 1, z + dz, material));
                    }
                }
            }
        }
    }
    
    private static void generatePyramid(List<OreGenerator.BlockData> blocks, int x, int y, int z, BiomeType biome) {
        Material material = Material.SANDSTONE;
        int size = 5;
        
        for (int level = 0; level < size; level++) {
            int width = size - level;
            for (int dx = -width; dx <= width; dx++) {
                for (int dz = -width; dz <= width; dz++) {
                    blocks.add(new OreGenerator.BlockData(x + dx, y + level, z + dz, material));
                }
            }
        }
    }
    
    private static void generateIceSpike(List<OreGenerator.BlockData> blocks, int x, int y, int z) {
        int height = 12;
        for (int i = 0; i < height; i++) {
            int width = Math.max(1, (height - i) / 3);
            for (int dx = -width; dx <= width; dx++) {
                for (int dz = -width; dz <= width; dz++) {
                    blocks.add(new OreGenerator.BlockData(x + dx, y + i, z + dz, Material.PACKED_ICE));
                }
            }
        }
    }
    
    private static void generateCrystalSpire(List<OreGenerator.BlockData> blocks, int x, int y, int z) {
        int height = 10;
        for (int i = 0; i < height; i++) {
            blocks.add(new OreGenerator.BlockData(x, y + i, z, Material.AMETHYST_BLOCK));
            if (i % 2 == 0 && i < height - 2) {
                blocks.add(new OreGenerator.BlockData(x + 1, y + i, z, Material.AMETHYST_CLUSTER));
                blocks.add(new OreGenerator.BlockData(x - 1, y + i, z, Material.AMETHYST_CLUSTER));
                blocks.add(new OreGenerator.BlockData(x, y + i, z + 1, Material.AMETHYST_CLUSTER));
                blocks.add(new OreGenerator.BlockData(x, y + i, z - 1, Material.AMETHYST_CLUSTER));
            }
        }
    }
    
    private static void generateLavaShrine(List<OreGenerator.BlockData> blocks, int x, int y, int z) {
        for (int dx = -2; dx <= 2; dx++) {
            for (int dz = -2; dz <= 2; dz++) {
                blocks.add(new OreGenerator.BlockData(x + dx, y, z + dz, Material.BLACKSTONE));
            }
        }
        blocks.add(new OreGenerator.BlockData(x, y, z, Material.LAVA));
        blocks.add(new OreGenerator.BlockData(x, y + 1, z, Material.NETHERITE_BLOCK));
    }
    
    private static void generateAlienMonolith(List<OreGenerator.BlockData> blocks, int x, int y, int z) {
        int height = 8;
        for (int i = 0; i < height; i++) {
            blocks.add(new OreGenerator.BlockData(x, y + i, z, Material.CRYING_OBSIDIAN));
            blocks.add(new OreGenerator.BlockData(x + 1, y + i, z, Material.OBSIDIAN));
        }
        
        for (int i = 0; i < 3; i++) {
            blocks.add(new OreGenerator.BlockData(x, y + height + i, z, Material.GLOWSTONE));
        }
    }
    
    private enum StructureType {
        TOWER, RUINS, PYRAMID, ICE_SPIKE, CRYSTAL_SPIRE, LAVA_SHRINE, ALIEN_MONOLITH
    }
}
