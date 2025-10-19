package com.skyorbs.features;

import com.skyorbs.biomes.BiomeType;
import com.skyorbs.core.Orb;
import com.skyorbs.core.PlanetType;
import org.bukkit.Material;
import org.bukkit.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class StructureGenerator {
    
    public static List<OreGenerator.BlockData> generateStructures(Orb orb, BiomeType biome, World world) {
        List<OreGenerator.BlockData> blocks = new ArrayList<>();

        // Get planet type from biome
        PlanetType planetType = OreGenerator.getPlanetTypeFromBiome(biome);

        Random random = new Random(orb.getSeed() + biome.name().hashCode());

        int cx = orb.getCenterX();
        int cy = orb.getCenterY();
        int cz = orb.getCenterZ();
        int radius = orb.getRadius();

        // Get biome-specific structure configuration
        StructureConfig config = getStructureConfig(biome, planetType);
        if (config == null) return blocks;

        double densityMultiplier = com.skyorbs.SkyOrbs.getInstance()
            .getConfigManager().getStructureDensityMultiplier();
        int structureCount = (int)(radius * config.density * densityMultiplier);

        for (int i = 0; i < structureCount; i++) {
            // Generate positions within planet bounds
            int x = cx + random.nextInt(radius * 2) - radius;
            int z = cz + random.nextInt(radius * 2) - radius;
            int y = cy + radius; // Surface level

            // Select structure type based on biome AND planet type
            StructureType type = config.getRandomType(random);

            // Generate the structure
            generateStructure(blocks, x, y, z, type, random, biome, planetType);
        }

        return blocks;
    }
    
    /**
     * Generate a single structure with biome-specific variations
     */
    private static void generateStructure(List<OreGenerator.BlockData> blocks, int x, int y, int z,
                                          StructureType type, Random random, BiomeType biome, PlanetType planetType) {
        
        switch (type) {
            case DESERT_TEMPLE -> generateDesertTemple(blocks, x, y, z, random, biome, planetType);
            case JUNGLE_TEMPLE -> generateJungleTemple(blocks, x, y, z, random, biome, planetType);
            case VILLAGE_HOUSE -> generateVillageHouse(blocks, x, y, z, random, biome, planetType);
            case IGLOO -> generateIgloo(blocks, x, y, z, random, biome, planetType);
            case NETHER_FORTRESS -> generateNetherFortress(blocks, x, y, z, random, biome, planetType);
            case END_TOWER -> generateEndTower(blocks, x, y, z, random, biome, planetType);
            case CRYSTAL_SPIRE -> generateCrystalSpire(blocks, x, y, z, random, biome, planetType);
            case MUSHROOM_HUT -> generateMushroomHut(blocks, x, y, z, random, biome, planetType);
            case RUINS -> generateRuins(blocks, x, y, z, random, biome, planetType);
            case ALTAR -> generateAltar(blocks, x, y, z, random, biome, planetType);
        }
    }
    
    /**
     * Desert temple with biome-specific variations
     */
    private static void generateDesertTemple(List<OreGenerator.BlockData> blocks, int x, int y, int z, Random random, BiomeType biome, PlanetType planetType) {
        // Temel (9x9)
        for (int dx = -4; dx <= 4; dx++) {
            for (int dz = -4; dz <= 4; dz++) {
                blocks.add(new OreGenerator.BlockData(x + dx, y, z + dz, Material.SANDSTONE));
            }
        }
        
        // Duvarlar
        for (int dy = 1; dy <= 5; dy++) {
            for (int dx = -4; dx <= 4; dx++) {
                blocks.add(new OreGenerator.BlockData(x + dx, y + dy, z - 4, Material.SANDSTONE));
                blocks.add(new OreGenerator.BlockData(x + dx, y + dy, z + 4, Material.SANDSTONE));
            }
            for (int dz = -3; dz <= 3; dz++) {
                blocks.add(new OreGenerator.BlockData(x - 4, y + dy, z + dz, Material.SANDSTONE));
                blocks.add(new OreGenerator.BlockData(x + 4, y + dy, z + dz, Material.SANDSTONE));
            }
        }
        
        // Çatı
        for (int dx = -3; dx <= 3; dx++) {
            for (int dz = -3; dz <= 3; dz++) {
                blocks.add(new OreGenerator.BlockData(x + dx, y + 6, z + dz, Material.ORANGE_TERRACOTTA));
            }
        }
        
        // Hazine odası (yeraltı)
        for (int dx = -2; dx <= 2; dx++) {
            for (int dz = -2; dz <= 2; dz++) {
                blocks.add(new OreGenerator.BlockData(x + dx, y - 3, z + dz, Material.STONE_BRICKS));
            }
        }
        
        // Treasure chest with biome-specific loot
        blocks.add(new OreGenerator.BlockData(x, y - 2, z, Material.CHEST));

        // Biome-specific traps and decorations
        if (biome == BiomeType.TOXIC_SWAMP || biome == BiomeType.TOXIC) {
            // Toxic biome - poison traps
            blocks.add(new OreGenerator.BlockData(x + 1, y - 2, z + 1, Material.POISONOUS_POTATO));
            blocks.add(new OreGenerator.BlockData(x - 1, y - 2, z - 1, Material.POISONOUS_POTATO));
        } else {
            // Standard traps
            blocks.add(new OreGenerator.BlockData(x + 1, y - 2, z + 1, Material.TNT));
            blocks.add(new OreGenerator.BlockData(x - 1, y - 2, z - 1, Material.TNT));
        }
    }
    
    /**
     * Orman tapınağı
     */
    private static void generateJungleTemple(List<OreGenerator.BlockData> blocks, int x, int y, int z, Random random, BiomeType biome, PlanetType planetType) {
        // Piramit tabanı (11x11)
        for (int dx = -5; dx <= 5; dx++) {
            for (int dz = -5; dz <= 5; dz++) {
                blocks.add(new OreGenerator.BlockData(x + dx, y, z + dz, Material.MOSSY_COBBLESTONE));
            }
        }
        
        // Piramit katları
        for (int level = 1; level <= 6; level++) {
            int size = 6 - level;
            for (int dx = -size; dx <= size; dx++) {
                for (int dz = -size; dz <= size; dz++) {
                    Material mat = random.nextBoolean() ? Material.MOSSY_COBBLESTONE : Material.COBBLESTONE;
                    blocks.add(new OreGenerator.BlockData(x + dx, y + level, z + dz, mat));
                }
            }
        }
        
        // Hazine sandığı
        blocks.add(new OreGenerator.BlockData(x, y + 1, z, Material.CHEST));
        
        // Asma yapraklar
        for (int i = 0; i < 10; i++) {
            int vineX = x + random.nextInt(11) - 5;
            int vineZ = z + random.nextInt(11) - 5;
            int vineLength = 2 + random.nextInt(4);
            for (int vl = 0; vl < vineLength; vl++) {
                blocks.add(new OreGenerator.BlockData(vineX, y + 7 - vl, vineZ, Material.VINE));
            }
        }
    }
    
    /**
     * Köy evi
     */
    private static void generateVillageHouse(List<OreGenerator.BlockData> blocks, int x, int y, int z, Random random, BiomeType biome, PlanetType planetType) {
        // Temel (7x7)
        for (int dx = -3; dx <= 3; dx++) {
            for (int dz = -3; dz <= 3; dz++) {
                blocks.add(new OreGenerator.BlockData(x + dx, y, z + dz, Material.OAK_PLANKS));
            }
        }
        
        // Duvarlar
        for (int dy = 1; dy <= 3; dy++) {
            for (int dx = -3; dx <= 3; dx++) {
                blocks.add(new OreGenerator.BlockData(x + dx, y + dy, z - 3, Material.OAK_LOG));
                blocks.add(new OreGenerator.BlockData(x + dx, y + dy, z + 3, Material.OAK_LOG));
            }
            for (int dz = -2; dz <= 2; dz++) {
                blocks.add(new OreGenerator.BlockData(x - 3, y + dy, z + dz, Material.OAK_LOG));
                blocks.add(new OreGenerator.BlockData(x + 3, y + dy, z + dz, Material.OAK_LOG));
            }
        }
        
        // Çatı
        for (int dx = -3; dx <= 3; dx++) {
            for (int dz = -3; dz <= 3; dz++) {
                blocks.add(new OreGenerator.BlockData(x + dx, y + 4, z + dz, Material.OAK_STAIRS));
            }
        }
        
        // Kapı
        blocks.add(new OreGenerator.BlockData(x, y + 1, z - 3, Material.OAK_DOOR));
        blocks.add(new OreGenerator.BlockData(x, y + 2, z - 3, Material.OAK_DOOR));
        
        // İç mobilyalar
        blocks.add(new OreGenerator.BlockData(x - 2, y + 1, z - 2, Material.CRAFTING_TABLE));
        blocks.add(new OreGenerator.BlockData(x + 2, y + 1, z + 2, Material.CHEST));
        blocks.add(new OreGenerator.BlockData(x, y + 1, z, Material.RED_BED));
    }
    
    /**
     * İglo
     */
    private static void generateIgloo(List<OreGenerator.BlockData> blocks, int x, int y, int z, Random random, BiomeType biome, PlanetType planetType) {
        // Kubbe şekli (6x6 taban)
        for (int dx = -3; dx <= 3; dx++) {
            for (int dz = -3; dz <= 3; dz++) {
                double distance = Math.sqrt(dx * dx + dz * dz);
                if (distance <= 3) {
                    blocks.add(new OreGenerator.BlockData(x + dx, y, z + dz, Material.SNOW_BLOCK));
                    
                    if (distance <= 2.5) {
                        blocks.add(new OreGenerator.BlockData(x + dx, y + 1, z + dz, Material.SNOW_BLOCK));
                    }
                    if (distance <= 1.5) {
                        blocks.add(new OreGenerator.BlockData(x + dx, y + 2, z + dz, Material.SNOW_BLOCK));
                    }
                }
            }
        }
        
        // Giriş
        blocks.add(new OreGenerator.BlockData(x, y + 1, z - 3, Material.AIR));
        
        // İçeride mobilya
        blocks.add(new OreGenerator.BlockData(x, y + 1, z, Material.FURNACE));
        blocks.add(new OreGenerator.BlockData(x - 1, y + 1, z + 1, Material.CHEST));
    }
    
    /**
     * Nether kalesi
     */
    private static void generateNetherFortress(List<OreGenerator.BlockData> blocks, int x, int y, int z, Random random, BiomeType biome, PlanetType planetType) {
        // Temel platform
        for (int dx = -5; dx <= 5; dx++) {
            for (int dz = -5; dz <= 5; dz++) {
                blocks.add(new OreGenerator.BlockData(x + dx, y, z + dz, Material.NETHER_BRICKS));
            }
        }
        
        // Kuleler (4 köşe)
        int[][] corners = {{-5, -5}, {5, -5}, {-5, 5}, {5, 5}};
        for (int[] corner : corners) {
            for (int dy = 1; dy <= 8; dy++) {
                blocks.add(new OreGenerator.BlockData(x + corner[0], y + dy, z + corner[1], Material.NETHER_BRICKS));
                blocks.add(new OreGenerator.BlockData(x + corner[0] + (corner[0] > 0 ? -1 : 1), y + dy, z + corner[1], Material.NETHER_BRICKS));
                blocks.add(new OreGenerator.BlockData(x + corner[0], y + dy, z + corner[1] + (corner[1] > 0 ? -1 : 1), Material.NETHER_BRICKS));
            }
            // Kule tepesi
            blocks.add(new OreGenerator.BlockData(x + corner[0], y + 9, z + corner[1], Material.NETHER_BRICK_FENCE));
        }
        
        // Merkez yapı
        for (int dy = 1; dy <= 5; dy++) {
            for (int dx = -2; dx <= 2; dx++) {
                blocks.add(new OreGenerator.BlockData(x + dx, y + dy, z - 2, Material.NETHER_BRICKS));
                blocks.add(new OreGenerator.BlockData(x + dx, y + dy, z + 2, Material.NETHER_BRICKS));
            }
        }
        
        // Spawner
        blocks.add(new OreGenerator.BlockData(x, y + 1, z, Material.SPAWNER));
        
        // Lava dekorasyonu
        blocks.add(new OreGenerator.BlockData(x + 2, y + 1, z, Material.LAVA));
        blocks.add(new OreGenerator.BlockData(x - 2, y + 1, z, Material.LAVA));
    }
    
    /**
     * End kulesi
     */
    private static void generateEndTower(List<OreGenerator.BlockData> blocks, int x, int y, int z, Random random, BiomeType biome, PlanetType planetType) {
        // Yüksek kule (15 blok)
        for (int dy = 0; dy <= 15; dy++) {
            for (int dx = -2; dx <= 2; dx++) {
                for (int dz = -2; dz <= 2; dz++) {
                    if (Math.abs(dx) == 2 || Math.abs(dz) == 2) {
                        blocks.add(new OreGenerator.BlockData(x + dx, y + dy, z + dz, Material.END_STONE_BRICKS));
                    }
                }
            }
        }
        
        // Tepe kristali
        blocks.add(new OreGenerator.BlockData(x, y + 16, z, Material.END_ROD));
        blocks.add(new OreGenerator.BlockData(x, y + 17, z, Material.PURPUR_BLOCK));
        
        // Chorus bitkileri
        for (int i = 0; i < 5; i++) {
            int chX = x + random.nextInt(9) - 4;
            int chZ = z + random.nextInt(9) - 4;
            int height = 2 + random.nextInt(4);
            for (int h = 0; h < height; h++) {
                blocks.add(new OreGenerator.BlockData(chX, y + h, chZ, Material.CHORUS_PLANT));
            }
        }
    }
    
    /**
     * Kristal diken
     */
    private static void generateCrystalSpire(List<OreGenerator.BlockData> blocks, int x, int y, int z, Random random, BiomeType biome, PlanetType planetType) {
        int height = 10 + random.nextInt(8);
        
        for (int dy = 0; dy < height; dy++) {
            int size = Math.max(1, 3 - dy / 3);
            for (int dx = -size; dx <= size; dx++) {
                for (int dz = -size; dz <= size; dz++) {
                    Material mat = random.nextBoolean() ? Material.AMETHYST_BLOCK : Material.BUDDING_AMETHYST;
                    blocks.add(new OreGenerator.BlockData(x + dx, y + dy, z + dz, mat));
                }
            }
        }
        
        // Parlayan tepe
        blocks.add(new OreGenerator.BlockData(x, y + height, z, Material.SEA_LANTERN));
    }
    
    /**
     * Mantar kulübesi
     */
    private static void generateMushroomHut(List<OreGenerator.BlockData> blocks, int x, int y, int z, Random random, BiomeType biome, PlanetType planetType) {
        // Kök
        blocks.add(new OreGenerator.BlockData(x, y, z, Material.MUSHROOM_STEM));
        blocks.add(new OreGenerator.BlockData(x, y + 1, z, Material.MUSHROOM_STEM));
        blocks.add(new OreGenerator.BlockData(x, y + 2, z, Material.MUSHROOM_STEM));
        
        // Şapka
        Material capMaterial = random.nextBoolean() ? Material.RED_MUSHROOM_BLOCK : Material.BROWN_MUSHROOM_BLOCK;
        for (int dx = -2; dx <= 2; dx++) {
            for (int dz = -2; dz <= 2; dz++) {
                blocks.add(new OreGenerator.BlockData(x + dx, y + 3, z + dz, capMaterial));
            }
        }
    }
    
    /**
     * Harabeler
     */
    private static void generateRuins(List<OreGenerator.BlockData> blocks, int x, int y, int z, Random random, BiomeType biome, PlanetType planetType) {
        // Yarım duvarlar
        for (int i = 0; i < 10; i++) {
            int rX = x + random.nextInt(9) - 4;
            int rZ = z + random.nextInt(9) - 4;
            int height = 1 + random.nextInt(3);
            
            Material mat = random.nextBoolean() ? Material.STONE_BRICKS : Material.CRACKED_STONE_BRICKS;
            for (int dy = 0; dy < height; dy++) {
                blocks.add(new OreGenerator.BlockData(rX, y + dy, rZ, mat));
            }
        }
        
        // Sandık
        blocks.add(new OreGenerator.BlockData(x, y, z, Material.CHEST));
    }
    
    /**
     * Sunak
     */
    private static void generateAltar(List<OreGenerator.BlockData> blocks, int x, int y, int z, Random random, BiomeType biome, PlanetType planetType) {
        // Platform (5x5)
        for (int dx = -2; dx <= 2; dx++) {
            for (int dz = -2; dz <= 2; dz++) {
                blocks.add(new OreGenerator.BlockData(x + dx, y, z + dz, Material.QUARTZ_BLOCK));
            }
        }
        
        // Merkez sütun
        for (int dy = 1; dy <= 3; dy++) {
            blocks.add(new OreGenerator.BlockData(x, y + dy, z, Material.QUARTZ_PILLAR));
        }
        
        // Ateş
        blocks.add(new OreGenerator.BlockData(x, y + 4, z, Material.FIRE));
        
        // Köşe meşaleler
        blocks.add(new OreGenerator.BlockData(x - 2, y + 1, z - 2, Material.TORCH));
        blocks.add(new OreGenerator.BlockData(x + 2, y + 1, z - 2, Material.TORCH));
        blocks.add(new OreGenerator.BlockData(x - 2, y + 1, z + 2, Material.TORCH));
        blocks.add(new OreGenerator.BlockData(x + 2, y + 1, z + 2, Material.TORCH));
    }
    
    /**
     * Get structure config based on BOTH biome and planet type
     */
    private static StructureConfig getStructureConfig(BiomeType biome, PlanetType planetType) {
        // LAVA planets - only lava structures
        if (planetType == PlanetType.LAVA) {
            return new StructureConfig(0.008,
                new StructureType[]{StructureType.NETHER_FORTRESS, StructureType.RUINS},
                new double[]{0.7, 0.3});
        }

        // ICE planets - only ice structures
        if (planetType == PlanetType.ICE) {
            return new StructureConfig(0.010,
                new StructureType[]{StructureType.IGLOO},
                new double[]{1.0});
        }

        // CRYSTAL planets - crystal structures
        if (planetType == PlanetType.CRYSTAL) {
            return new StructureConfig(0.025,
                new StructureType[]{StructureType.CRYSTAL_SPIRE, StructureType.ALTAR},
                new double[]{0.7, 0.3});
        }

        // SHADOW planets - end structures
        if (planetType == PlanetType.SHADOW) {
            return new StructureConfig(0.010,
                new StructureType[]{StructureType.END_TOWER},
                new double[]{1.0});
        }

        // TERRESTRIAL - biome-specific
        return switch (biome) {
            case DESERT, BADLANDS -> new StructureConfig(0.015,
                new StructureType[]{StructureType.DESERT_TEMPLE, StructureType.RUINS},
                new double[]{0.7, 0.3});

            case JUNGLE -> new StructureConfig(0.012,
                new StructureType[]{StructureType.JUNGLE_TEMPLE, StructureType.RUINS},
                new double[]{0.8, 0.2});

            case PLAINS, SAVANNA, MEADOW -> new StructureConfig(0.020,
                new StructureType[]{StructureType.VILLAGE_HOUSE, StructureType.RUINS},
                new double[]{0.6, 0.4});

            default -> null;
        };
    }
    
    /**
     * Yapı türleri
     */
    private enum StructureType {
        DESERT_TEMPLE,
        JUNGLE_TEMPLE,
        VILLAGE_HOUSE,
        IGLOO,
        NETHER_FORTRESS,
        END_TOWER,
        CRYSTAL_SPIRE,
        MUSHROOM_HUT,
        RUINS,
        ALTAR
    }
    
    /**
     * Biyom yapı konfigürasyonu
     */
    private static class StructureConfig {
        final double density;
        final StructureType[] types;
        final double[] weights;
        
        StructureConfig(double density, StructureType[] types, double[] weights) {
            this.density = density;
            this.types = types;
            this.weights = weights;
        }
        
        StructureType getRandomType(Random random) {
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