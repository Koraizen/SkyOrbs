package com.skyorbs.features;

import com.skyorbs.biomes.BiomeType;
import com.skyorbs.core.Orb;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

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

        // Only generate trees in hollow planets
        if (!orb.getModifiers().contains(com.skyorbs.modifiers.PlanetModifier.HOLLOW)) {
            return blocks;
        }

        // Get tree config
        TreeConfig config = getTreeConfig(biome);
        if (config == null) return blocks;

        double treeMultiplier = 2.0;
        int treeCount = (int)(radius * config.density * treeMultiplier);

        for (int i = 0; i < treeCount; i++) {
            int x = cx + random.nextInt(radius * 2) - radius;
            int z = cz + random.nextInt(radius * 2) - radius;

            // FIXED: Pass world for ground checking
            int y = findGroundLevel(cx, cy, cz, x, z, radius, world);

            if (y == -1) continue;

            TreeType type = config.getRandomType(random);
            generateTree(blocks, x, y + 1, z, type, random); // +1 to spawn above ground
        }

        return blocks;
    }

    /**
     * IMPROVED: Find ground level with raycast from top to bottom
     * Ensures trees spawn on actual solid blocks
     */
    private static int findGroundLevel(int cx, int cy, int cz, int x, int z, int radius, World world) {
        // Start from top of planet and raycast downward
        int searchStartY = cy + radius + 5;
        int searchEndY = cy - radius;

        Block previousBlock = null;

        for (int y = searchStartY; y >= searchEndY; y--) {
            Block currentBlock = world.getBlockAt(x, y, z);

            // Check if we found ground (solid block with air above)
            if (currentBlock.getType().isSolid() &&
                (previousBlock == null || previousBlock.getType() == Material.AIR || previousBlock.getType() == Material.CAVE_AIR)) {

                // Verify this is actually on planet surface
                double distanceFromCenter = Math.sqrt(
                    (x - cx) * (x - cx) +
                    (y - cy) * (y - cy) +
                    (z - cz) * (z - cz)
                );

                // Must be on or near surface (within 3 blocks of radius)
                if (distanceFromCenter >= radius - 3 && distanceFromCenter <= radius + 1) {
                    return y;
                }
            }

            previousBlock = currentBlock;
        }

        return -1; // No suitable ground found
    }
    
    /**
     * Tek bir ağaç oluşturur
     */
    private static void generateTree(List<OreGenerator.BlockData> blocks, int x, int y, int z, 
                                     TreeType type, Random random) {
        
        int height = type.minHeight + random.nextInt(type.maxHeight - type.minHeight + 1);
        
        // Gövde
        for (int i = 0; i < height; i++) {
            blocks.add(new OreGenerator.BlockData(x, y + i, z, type.logMaterial));
        }
        
        // Yapraklar
        int leafStart = y + height - type.leafLayers;
        for (int ly = 0; ly < type.leafLayers; ly++) {
            int currentY = leafStart + ly;
            int leafRadius = type.leafRadius - (ly / 2);
            
            for (int dx = -leafRadius; dx <= leafRadius; dx++) {
                for (int dz = -leafRadius; dz <= leafRadius; dz++) {
                    if (dx == 0 && dz == 0 && ly < type.leafLayers - 1) continue; // Gövdeyi atla
                    
                    double distance = Math.sqrt(dx * dx + dz * dz);
                    if (distance <= leafRadius + random.nextDouble() * 0.5) {
                        blocks.add(new OreGenerator.BlockData(x + dx, currentY, z + dz, type.leafMaterial));
                    }
                }
            }
        }
        
        // Özel özellikler
        if (type == TreeType.OAK && random.nextDouble() < 0.15) {
            // Arı kovanı ekle
            int side = random.nextInt(4);
            int beeY = y + height - 2;
            switch (side) {
                case 0 -> blocks.add(new OreGenerator.BlockData(x + 1, beeY, z, Material.BEE_NEST));
                case 1 -> blocks.add(new OreGenerator.BlockData(x - 1, beeY, z, Material.BEE_NEST));
                case 2 -> blocks.add(new OreGenerator.BlockData(x, beeY, z + 1, Material.BEE_NEST));
                case 3 -> blocks.add(new OreGenerator.BlockData(x, beeY, z - 1, Material.BEE_NEST));
            }
        }
        
        if (type == TreeType.JUNGLE && random.nextDouble() < 0.25) {
            // Kakao ekle
            int cocoaCount = 1 + random.nextInt(3);
            for (int i = 0; i < cocoaCount; i++) {
                int cocoaY = y + 2 + random.nextInt(height - 3);
                int side = random.nextInt(4);
                switch (side) {
                    case 0 -> blocks.add(new OreGenerator.BlockData(x + 1, cocoaY, z, Material.COCOA));
                    case 1 -> blocks.add(new OreGenerator.BlockData(x - 1, cocoaY, z, Material.COCOA));
                    case 2 -> blocks.add(new OreGenerator.BlockData(x, cocoaY, z + 1, Material.COCOA));
                    case 3 -> blocks.add(new OreGenerator.BlockData(x, cocoaY, z - 1, Material.COCOA));
                }
            }
            
            // Asma yapraklar
            int vineCount = 3 + random.nextInt(5);
            for (int i = 0; i < vineCount; i++) {
                int vineX = x + random.nextInt(5) - 2;
                int vineZ = z + random.nextInt(5) - 2;
                int vineLength = 2 + random.nextInt(4);
                
                for (int vl = 0; vl < vineLength; vl++) {
                    blocks.add(new OreGenerator.BlockData(vineX, y + height - vl, vineZ, Material.VINE));
                }
            }
        }
        
        if (type == TreeType.DARK_OAK && random.nextDouble() < 0.30) {
            // Mantarlar ekle
            for (int i = 0; i < 3; i++) {
                int mushX = x + random.nextInt(5) - 2;
                int mushZ = z + random.nextInt(5) - 2;
                Material mushroom = random.nextBoolean() ? Material.RED_MUSHROOM : Material.BROWN_MUSHROOM;
                blocks.add(new OreGenerator.BlockData(mushX, y, mushZ, mushroom));
            }
        }
        
        if (type == TreeType.CHERRY && random.nextDouble() < 0.40) {
            // Pembe yaprak efekti (ek yapraklar)
            for (int i = 0; i < 5; i++) {
                int petalX = x + random.nextInt(7) - 3;
                int petalY = y + height + random.nextInt(2);
                int petalZ = z + random.nextInt(7) - 3;
                blocks.add(new OreGenerator.BlockData(petalX, petalY, petalZ, Material.PINK_PETALS));
            }
        }
    }
    
    /**
     * Biyom bazlı ağaç konfigürasyonu
     */
    private static TreeConfig getTreeConfig(BiomeType biome) {
        return switch (biome) {
            case FOREST -> new TreeConfig(0.25, 
                new TreeType[]{TreeType.OAK, TreeType.BIRCH, TreeType.DARK_OAK},
                new double[]{0.5, 0.3, 0.2});
            
            case JUNGLE -> new TreeConfig(0.35, 
                new TreeType[]{TreeType.JUNGLE, TreeType.JUNGLE_LARGE},
                new double[]{0.7, 0.3});
            
            case TAIGA -> new TreeConfig(0.20, 
                new TreeType[]{TreeType.SPRUCE, TreeType.PINE},
                new double[]{0.7, 0.3});
            
            case SAVANNA -> new TreeConfig(0.08, 
                new TreeType[]{TreeType.ACACIA},
                new double[]{1.0});
            
            case MEADOW -> new TreeConfig(0.15, 
                new TreeType[]{TreeType.OAK, TreeType.BIRCH, TreeType.CHERRY},
                new double[]{0.4, 0.4, 0.2});
            
            case PLAINS -> new TreeConfig(0.05, 
                new TreeType[]{TreeType.OAK},
                new double[]{1.0});
            
            case MUSHROOM_GIANT -> new TreeConfig(0.12, 
                new TreeType[]{TreeType.GIANT_MUSHROOM_RED, TreeType.GIANT_MUSHROOM_BROWN},
                new double[]{0.5, 0.5});
            
            case TOXIC_SWAMP -> new TreeConfig(0.18, 
                new TreeType[]{TreeType.MANGROVE, TreeType.DARK_OAK},
                new double[]{0.6, 0.4});
            
            default -> null;
        };
    }
    
    /**
     * Ağaç türleri
     */
    public enum TreeType {
        OAK(Material.OAK_LOG, Material.OAK_LEAVES, 4, 6, 2, 4),
        BIRCH(Material.BIRCH_LOG, Material.BIRCH_LEAVES, 5, 7, 2, 4),
        SPRUCE(Material.SPRUCE_LOG, Material.SPRUCE_LEAVES, 6, 10, 2, 5),
        JUNGLE(Material.JUNGLE_LOG, Material.JUNGLE_LEAVES, 4, 6, 2, 4),
        JUNGLE_LARGE(Material.JUNGLE_LOG, Material.JUNGLE_LEAVES, 10, 15, 3, 6),
        ACACIA(Material.ACACIA_LOG, Material.ACACIA_LEAVES, 4, 6, 3, 4),
        DARK_OAK(Material.DARK_OAK_LOG, Material.DARK_OAK_LEAVES, 5, 7, 3, 5),
        MANGROVE(Material.MANGROVE_LOG, Material.MANGROVE_LEAVES, 4, 7, 2, 4),
        CHERRY(Material.CHERRY_LOG, Material.CHERRY_LEAVES, 5, 8, 3, 5),
        PINE(Material.SPRUCE_LOG, Material.SPRUCE_LEAVES, 8, 12, 2, 6),
        GIANT_MUSHROOM_RED(Material.MUSHROOM_STEM, Material.RED_MUSHROOM_BLOCK, 4, 6, 3, 5),
        GIANT_MUSHROOM_BROWN(Material.MUSHROOM_STEM, Material.BROWN_MUSHROOM_BLOCK, 4, 6, 3, 5);

        public final Material logMaterial;
        public final Material leafMaterial;
        public final int minHeight;
        public final int maxHeight;
        public final int leafRadius;
        public final int leafLayers;

        TreeType(Material log, Material leaf, int minH, int maxH, int leafR, int leafL) {
            this.logMaterial = log;
            this.leafMaterial = leaf;
            this.minHeight = minH;
            this.maxHeight = maxH;
            this.leafRadius = leafR;
            this.leafLayers = leafL;
        }
    }
    
    /**
     * Biyom ağaç konfigürasyonu
     */
    public static class TreeConfig {
        public final double density;
        public final TreeType[] types;
        public final double[] weights;

        public TreeConfig(double density, TreeType[] types, double[] weights) {
            this.density = density;
            this.types = types;
            this.weights = weights;
        }

        public TreeType getRandomType(Random random) {
            double total = 0;
            for (double w : weights) total += w;

            double rand = random.nextDouble() * total;
            double current = 0;

            for (int i = 0; i < types.length; i++) {
                current += weights[i];
                if (rand <= current) {
                    return types[i];
                }
            }

            return types[0];
        }
    }
}