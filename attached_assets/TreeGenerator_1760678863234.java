package com.skyorbs.features;

import com.skyorbs.biomes.BiomeType;
import com.skyorbs.core.Orb;
import org.bukkit.Material;
import org.bukkit.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TreeGenerator {
    
    public static List<OreGenerator.BlockData> generateTrees(Orb orb, BiomeType biome, World world) {
        List<OreGenerator.BlockData> blocks = new ArrayList<>();
        Random random = new Random(orb.getSeed() + 456);
        
        int cx = orb.getCenterX();
        int cy = orb.getCenterY();
        int cz = orb.getCenterZ();
        int radius = orb.getRadius();
        
        int treeCount = getTreeCount(biome, radius, random);
        
        for (int i = 0; i < treeCount; i++) {
            int angle = random.nextInt(360);
            int dist = random.nextInt(radius - 5);
            
            double rad = Math.toRadians(angle);
            int x = cx + (int)(Math.cos(rad) * dist);
            int z = cz + (int)(Math.sin(rad) * dist);
            int y = cy + (int)Math.sqrt(radius * radius - dist * dist);
            
            generateTree(blocks, x, y, z, biome, random);
        }
        
        return blocks;
    }
    
    private static int getTreeCount(BiomeType biome, int radius, Random random) {
        return switch (biome) {
            case FOREST, JUNGLE -> radius / 8 + random.nextInt(radius / 4);
            case TAIGA, SAVANNA -> radius / 12 + random.nextInt(radius / 6);
            case MEADOW -> radius / 15 + random.nextInt(radius / 10);
            case MUSHROOM_GIANT -> radius / 10 + random.nextInt(radius / 5);
            case CRYSTAL_FOREST -> radius / 12 + random.nextInt(radius / 8);
            default -> 0;
        };
    }
    
    private static void generateTree(List<OreGenerator.BlockData> blocks, int x, int y, int z, BiomeType biome, Random random) {
        Material log = getLogType(biome);
        Material leaves = getLeafType(biome);
        
        int height = 4 + random.nextInt(4);
        
        for (int i = 0; i < height; i++) {
            blocks.add(new OreGenerator.BlockData(x, y + i, z, log));
        }
        
        for (int dx = -2; dx <= 2; dx++) {
            for (int dz = -2; dz <= 2; dz++) {
                for (int dy = 0; dy < 3; dy++) {
                    if (Math.abs(dx) + Math.abs(dz) <= 3) {
                        blocks.add(new OreGenerator.BlockData(
                            x + dx, 
                            y + height - 1 + dy, 
                            z + dz, 
                            leaves
                        ));
                    }
                }
            }
        }
    }
    
    private static Material getLogType(BiomeType biome) {
        return switch (biome) {
            case FOREST, MEADOW -> Material.OAK_LOG;
            case JUNGLE -> Material.JUNGLE_LOG;
            case TAIGA -> Material.SPRUCE_LOG;
            case SAVANNA -> Material.ACACIA_LOG;
            case MUSHROOM_GIANT -> Material.RED_MUSHROOM_BLOCK;
            case CRYSTAL_FOREST -> Material.PURPUR_PILLAR;
            default -> Material.OAK_LOG;
        };
    }
    
    private static Material getLeafType(BiomeType biome) {
        return switch (biome) {
            case FOREST, MEADOW -> Material.OAK_LEAVES;
            case JUNGLE -> Material.JUNGLE_LEAVES;
            case TAIGA -> Material.SPRUCE_LEAVES;
            case SAVANNA -> Material.ACACIA_LEAVES;
            case MUSHROOM_GIANT -> Material.BROWN_MUSHROOM_BLOCK;
            case CRYSTAL_FOREST -> Material.AMETHYST_BLOCK;
            default -> Material.OAK_LEAVES;
        };
    }
}
