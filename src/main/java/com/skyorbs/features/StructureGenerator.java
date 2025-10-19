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

        // CONFIG KONTROLLÜ - Build sistemi aktif mi?
        if (!com.skyorbs.SkyOrbs.getInstance().getConfig().getBoolean("buildings.enabled", true)) {
            return blocks;
        }

        // Get planet type from biome
        PlanetType planetType = orb.getPlanetType();

        Random random = new Random(orb.getSeed() + biome.name().hashCode());

        int cx = orb.getCenterX();
        int cy = orb.getCenterY();
        int cz = orb.getCenterZ();
        int radius = orb.getRadius();

        // CONFIG'DEN PLANET TYPE AYARLARINI OKU
        String planetTypeKey = planetType.name().toLowerCase();
        boolean planetEnabled = com.skyorbs.SkyOrbs.getInstance().getConfig()
            .getBoolean("buildings.planetTypes." + planetTypeKey + ".enabled", true);

        if (!planetEnabled) return blocks;

        double baseDensity = com.skyorbs.SkyOrbs.getInstance().getConfig()
            .getDouble("buildings.densityMultiplier", 0.02);
        double planetMultiplier = com.skyorbs.SkyOrbs.getInstance().getConfig()
            .getDouble("buildings.planetTypes." + planetTypeKey + ".densityMultiplier", 1.0);

        int structureCount = (int)(radius * baseDensity * planetMultiplier);

        for (int i = 0; i < structureCount; i++) {
            // Generate positions within planet bounds
            int x = cx + random.nextInt(radius * 2) - radius;
            int z = cz + random.nextInt(radius * 2) - radius;
            int y = cy + radius; // Surface level

            // CONFIG'DEN YAPILARI SEÇ - Gezegen türüne göre
            BuildingType type = getBuildingTypeFromConfig(planetType, random);
    
            // Generate the structure - DEBUG: Her yapı için mesaj
            com.skyorbs.SkyOrbs.getInstance().getLogger().info("Generating building: " + type + " for planet type: " + planetType + " at " + x + "," + y + "," + z);
            generateBuilding(blocks, x, y, z, type, random, biome, planetType);
        }

        return blocks;
    }
    
    /**
     * Generate a single building with planet-specific variations
     */
    private static void generateBuilding(List<OreGenerator.BlockData> blocks, int x, int y, int z,
                                         BuildingType type, Random random, BiomeType biome, PlanetType planetType) {

        switch (type) {
            case VILLAGE -> generateVillageHouse(blocks, x, y, z, random, biome, planetType);
            case CASTLE -> generateCastle(blocks, x, y, z, random, biome, planetType);
            case TEMPLE -> generateTemple(blocks, x, y, z, random, biome, planetType);
            case RUINS -> generateRuins(blocks, x, y, z, random, biome, planetType);
            case CRYSTAL_TOWER -> generateCrystalTower(blocks, x, y, z, random, biome, planetType);
            case ARCANE_LIBRARY -> generateArcaneLibrary(blocks, x, y, z, random, biome, planetType);
            case CRYSTAL_PALACE -> generateCrystalPalace(blocks, x, y, z, random, biome, planetType);
            case FORTRESS -> generateFortress(blocks, x, y, z, random, biome, planetType);
            case LAVA_TEMPLE -> generateLavaTemple(blocks, x, y, z, random, biome, planetType);
            case ICE_PALACE -> generateIcePalace(blocks, x, y, z, random, biome, planetType);
            case IGLOO_VILLAGE -> generateIglooVillage(blocks, x, y, z, random, biome, planetType);
            case DARK_TOWER -> generateDarkTower(blocks, x, y, z, random, biome, planetType);
            case SHADOW_TEMPLE -> generateShadowTemple(blocks, x, y, z, random, biome, planetType);
            case LAB -> generateLab(blocks, x, y, z, random, biome, planetType);
            case QUARANTINE_ZONE -> generateQuarantineZone(blocks, x, y, z, random, biome, planetType);
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
     * CONFIG'DEN BUILDING TİPİ SEÇ - Gezegen türüne göre
     */
    private static BuildingType getBuildingTypeFromConfig(PlanetType planetType, Random random) {
        String planetTypeKey = planetType.name().toLowerCase();
        String configPath = "buildings.planetTypes." + planetTypeKey + ".structures";

        // Config'den olasılıkları oku
        double villageProb = com.skyorbs.SkyOrbs.getInstance().getConfig()
            .getDouble(configPath + ".village", 0.0);
        double castleProb = com.skyorbs.SkyOrbs.getInstance().getConfig()
            .getDouble(configPath + ".castle", 0.0);
        double templeProb = com.skyorbs.SkyOrbs.getInstance().getConfig()
            .getDouble(configPath + ".temple", 0.0);
        double ruinsProb = com.skyorbs.SkyOrbs.getInstance().getConfig()
            .getDouble(configPath + ".ruins", 0.0);
        double crystalTowerProb = com.skyorbs.SkyOrbs.getInstance().getConfig()
            .getDouble(configPath + ".crystal_tower", 0.0);
        double arcaneLibraryProb = com.skyorbs.SkyOrbs.getInstance().getConfig()
            .getDouble(configPath + ".arcane_library", 0.0);
        double crystalPalaceProb = com.skyorbs.SkyOrbs.getInstance().getConfig()
            .getDouble(configPath + ".crystal_palace", 0.0);
        double fortressProb = com.skyorbs.SkyOrbs.getInstance().getConfig()
            .getDouble(configPath + ".fortress", 0.0);
        double lavaTempleProb = com.skyorbs.SkyOrbs.getInstance().getConfig()
            .getDouble(configPath + ".lava_temple", 0.0);
        double icePalaceProb = com.skyorbs.SkyOrbs.getInstance().getConfig()
            .getDouble(configPath + ".ice_palace", 0.0);
        double iglooVillageProb = com.skyorbs.SkyOrbs.getInstance().getConfig()
            .getDouble(configPath + ".igloo_village", 0.0);
        double darkTowerProb = com.skyorbs.SkyOrbs.getInstance().getConfig()
            .getDouble(configPath + ".dark_tower", 0.0);
        double shadowTempleProb = com.skyorbs.SkyOrbs.getInstance().getConfig()
            .getDouble(configPath + ".shadow_temple", 0.0);
        double labProb = com.skyorbs.SkyOrbs.getInstance().getConfig()
            .getDouble(configPath + ".lab", 0.0);
        double quarantineZoneProb = com.skyorbs.SkyOrbs.getInstance().getConfig()
            .getDouble(configPath + ".quarantine_zone", 0.0);

        // Weighted random selection
        double rand = random.nextDouble();
        double total = 0;

        if ((total += villageProb) > rand) return BuildingType.VILLAGE;
        if ((total += castleProb) > rand) return BuildingType.CASTLE;
        if ((total += templeProb) > rand) return BuildingType.TEMPLE;
        if ((total += ruinsProb) > rand) return BuildingType.RUINS;
        if ((total += crystalTowerProb) > rand) return BuildingType.CRYSTAL_TOWER;
        if ((total += arcaneLibraryProb) > rand) return BuildingType.ARCANE_LIBRARY;
        if ((total += crystalPalaceProb) > rand) return BuildingType.CRYSTAL_PALACE;
        if ((total += fortressProb) > rand) return BuildingType.FORTRESS;
        if ((total += lavaTempleProb) > rand) return BuildingType.LAVA_TEMPLE;
        if ((total += icePalaceProb) > rand) return BuildingType.ICE_PALACE;
        if ((total += iglooVillageProb) > rand) return BuildingType.IGLOO_VILLAGE;
        if ((total += darkTowerProb) > rand) return BuildingType.DARK_TOWER;
        if ((total += shadowTempleProb) > rand) return BuildingType.SHADOW_TEMPLE;
        if ((total += labProb) > rand) return BuildingType.LAB;
        if ((total += quarantineZoneProb) > rand) return BuildingType.QUARANTINE_ZONE;

        // Fallback
        return BuildingType.RUINS;
    }
    
    /**
     * Building types - Gezegen türüne göre yapılar
     */
    private enum BuildingType {
        // TERRESTRIAL
        VILLAGE, CASTLE, TEMPLE, RUINS,

        // CRYSTAL
        CRYSTAL_TOWER, ARCANE_LIBRARY, CRYSTAL_PALACE,

        // LAVA
        FORTRESS, LAVA_TEMPLE,

        // ICE
        ICE_PALACE, IGLOO_VILLAGE,

        // SHADOW
        DARK_TOWER, SHADOW_TEMPLE,

        // TOXIC
        LAB, QUARANTINE_ZONE
    }
    
    /**
     * Yeni yapı generation metodları - Gezegen türüne göre
     */
    private static void generateCastle(List<OreGenerator.BlockData> blocks, int x, int y, int z, Random random, BiomeType biome, PlanetType planetType) {
        // Kale - büyük taş yapı
        for (int dx = -6; dx <= 6; dx++) {
            for (int dz = -6; dz <= 6; dz++) {
                blocks.add(new OreGenerator.BlockData(x + dx, y, z + dz, Material.STONE_BRICKS));
            }
        }

        // Kale duvarları
        for (int dy = 1; dy <= 8; dy++) {
            for (int dx = -6; dx <= 6; dx++) {
                blocks.add(new OreGenerator.BlockData(x + dx, y + dy, z - 6, Material.STONE_BRICKS));
                blocks.add(new OreGenerator.BlockData(x + dx, y + dy, z + 6, Material.STONE_BRICKS));
            }
            for (int dz = -5; dz <= 5; dz++) {
                blocks.add(new OreGenerator.BlockData(x - 6, y + dy, z + dz, Material.STONE_BRICKS));
                blocks.add(new OreGenerator.BlockData(x + 6, y + dy, z + dz, Material.STONE_BRICKS));
            }
        }

        // Kale kuleleri
        int[][] towers = {{-6, -6}, {6, -6}, {-6, 6}, {6, 6}};
        for (int[] tower : towers) {
            for (int dy = 9; dy <= 12; dy++) {
                blocks.add(new OreGenerator.BlockData(x + tower[0], y + dy, z + tower[1], Material.STONE_BRICKS));
            }
        }

        // İç mekan
        blocks.add(new OreGenerator.BlockData(x, y + 1, z, Material.CHEST)); // Hazine
        blocks.add(new OreGenerator.BlockData(x + 2, y + 1, z, Material.CRAFTING_TABLE));
    }

    private static void generateTemple(List<OreGenerator.BlockData> blocks, int x, int y, int z, Random random, BiomeType biome, PlanetType planetType) {
        // Tapınak - kutsal yapı
        for (int dx = -4; dx <= 4; dx++) {
            for (int dz = -4; dz <= 4; dz++) {
                blocks.add(new OreGenerator.BlockData(x + dx, y, z + dz, Material.QUARTZ_BLOCK));
            }
        }

        // Tapınak sütunları
        for (int i = 0; i < 4; i++) {
            int colX = x + (i % 2 == 0 ? -3 : 3);
            int colZ = z + (i < 2 ? -3 : 3);
            for (int dy = 1; dy <= 6; dy++) {
                blocks.add(new OreGenerator.BlockData(colX, y + dy, colZ, Material.QUARTZ_PILLAR));
            }
        }

        // Çatı
        for (int dx = -3; dx <= 3; dx++) {
            for (int dz = -3; dz <= 3; dz++) {
                blocks.add(new OreGenerator.BlockData(x + dx, y + 7, z + dz, Material.QUARTZ_STAIRS));
            }
        }

        // İç mekan
        blocks.add(new OreGenerator.BlockData(x, y + 1, z, Material.ENCHANTING_TABLE));
        blocks.add(new OreGenerator.BlockData(x + 1, y + 1, z + 1, Material.BOOKSHELF));
        blocks.add(new OreGenerator.BlockData(x - 1, y + 1, z - 1, Material.CHEST));
    }

    private static void generateCrystalTower(List<OreGenerator.BlockData> blocks, int x, int y, int z, Random random, BiomeType biome, PlanetType planetType) {
        // Kristal kule - yüksek ve parlak
        int height = 15 + random.nextInt(10);
        for (int dy = 0; dy < height; dy++) {
            int size = Math.max(1, 4 - dy / 4);
            for (int dx = -size; dx <= size; dx++) {
                for (int dz = -size; dz <= size; dz++) {
                    Material mat = random.nextBoolean() ? Material.AMETHYST_BLOCK : Material.BUDDING_AMETHYST;
                    blocks.add(new OreGenerator.BlockData(x + dx, y + dy, z + dz, mat));
                }
            }
        }

        // Tepe kristali
        blocks.add(new OreGenerator.BlockData(x, y + height, z, Material.SEA_LANTERN));
        blocks.add(new OreGenerator.BlockData(x, y + height + 1, z, Material.AMETHYST_CLUSTER));
    }

    private static void generateArcaneLibrary(List<OreGenerator.BlockData> blocks, int x, int y, int z, Random random, BiomeType biome, PlanetType planetType) {
        // Büyü kütüphanesi - kitaplar ve büyü eşyaları
        for (int dx = -5; dx <= 5; dx++) {
            for (int dz = -5; dz <= 5; dz++) {
                blocks.add(new OreGenerator.BlockData(x + dx, y, z + dz, Material.BOOKSHELF));
            }
        }

        // İç duvarlar
        for (int dy = 1; dy <= 4; dy++) {
            for (int dx = -5; dx <= 5; dx++) {
                blocks.add(new OreGenerator.BlockData(x + dx, y + dy, z - 5, Material.OAK_PLANKS));
                blocks.add(new OreGenerator.BlockData(x + dx, y + dy, z + 5, Material.OAK_PLANKS));
            }
            for (int dz = -4; dz <= 4; dz++) {
                blocks.add(new OreGenerator.BlockData(x - 5, y + dy, z + dz, Material.OAK_PLANKS));
                blocks.add(new OreGenerator.BlockData(x + 5, y + dy, z + dz, Material.OAK_PLANKS));
            }
        }

        // İç mekan
        blocks.add(new OreGenerator.BlockData(x, y + 1, z, Material.ENCHANTING_TABLE));
        blocks.add(new OreGenerator.BlockData(x + 2, y + 1, z + 2, Material.BREWING_STAND));
        blocks.add(new OreGenerator.BlockData(x - 2, y + 1, z - 2, Material.CHEST));
    }

    private static void generateCrystalPalace(List<OreGenerator.BlockData> blocks, int x, int y, int z, Random random, BiomeType biome, PlanetType planetType) {
        // Kristal saray - büyük ve görkemli
        for (int dx = -8; dx <= 8; dx++) {
            for (int dz = -8; dz <= 8; dz++) {
                blocks.add(new OreGenerator.BlockData(x + dx, y, z + dz, Material.PURPUR_BLOCK));
            }
        }

        // Kristal sütunları
        for (int i = 0; i < 8; i++) {
            double angle = (i * Math.PI * 2) / 8;
            int colX = x + (int)(Math.cos(angle) * 6);
            int colZ = z + (int)(Math.sin(angle) * 6);
            for (int dy = 1; dy <= 8; dy++) {
                blocks.add(new OreGenerator.BlockData(colX, y + dy, colZ, Material.AMETHYST_BLOCK));
            }
        }

        // Çatı
        for (int dx = -6; dx <= 6; dx++) {
            for (int dz = -6; dz <= 6; dz++) {
                blocks.add(new OreGenerator.BlockData(x + dx, y + 9, z + dz, Material.SEA_LANTERN));
            }
        }

        // İç mekan
        blocks.add(new OreGenerator.BlockData(x, y + 1, z, Material.ENCHANTING_TABLE));
        blocks.add(new OreGenerator.BlockData(x + 3, y + 1, z, Material.CHEST));
        blocks.add(new OreGenerator.BlockData(x - 3, y + 1, z, Material.CHEST));
    }

    private static void generateFortress(List<OreGenerator.BlockData> blocks, int x, int y, int z, Random random, BiomeType biome, PlanetType planetType) {
        // Kale - lav gezegenleri için
        for (int dx = -7; dx <= 7; dx++) {
            for (int dz = -7; dz <= 7; dz++) {
                blocks.add(new OreGenerator.BlockData(x + dx, y, z + dz, Material.NETHER_BRICKS));
            }
        }

        // Kale duvarları
        for (int dy = 1; dy <= 6; dy++) {
            for (int dx = -7; dx <= 7; dx++) {
                blocks.add(new OreGenerator.BlockData(x + dx, y + dy, z - 7, Material.NETHER_BRICKS));
                blocks.add(new OreGenerator.BlockData(x + dx, y + dy, z + 7, Material.NETHER_BRICKS));
            }
            for (int dz = -6; dz <= 6; dz++) {
                blocks.add(new OreGenerator.BlockData(x - 7, y + dy, z + dz, Material.NETHER_BRICKS));
                blocks.add(new OreGenerator.BlockData(x + 7, y + dy, z + dz, Material.NETHER_BRICKS));
            }
        }

        // İç mekan
        blocks.add(new OreGenerator.BlockData(x, y + 1, z, Material.CHEST));
        blocks.add(new OreGenerator.BlockData(x + 2, y + 1, z, Material.LAVA));
        blocks.add(new OreGenerator.BlockData(x - 2, y + 1, z, Material.ANCIENT_DEBRIS));
    }

    private static void generateLavaTemple(List<OreGenerator.BlockData> blocks, int x, int y, int z, Random random, BiomeType biome, PlanetType planetType) {
        // Lava tapınağı
        for (int dx = -5; dx <= 5; dx++) {
            for (int dz = -5; dz <= 5; dz++) {
                blocks.add(new OreGenerator.BlockData(x + dx, y, z + dz, Material.MAGMA_BLOCK));
            }
        }

        // Lava sütunları
        for (int i = 0; i < 4; i++) {
            int colX = x + (i % 2 == 0 ? -4 : 4);
            int colZ = z + (i < 2 ? -4 : 4);
            for (int dy = 1; dy <= 5; dy++) {
                blocks.add(new OreGenerator.BlockData(colX, y + dy, colZ, Material.OBSIDIAN));
            }
            blocks.add(new OreGenerator.BlockData(colX, y + 6, colZ, Material.LAVA));
        }

        // İç mekan
        blocks.add(new OreGenerator.BlockData(x, y + 1, z, Material.ENCHANTING_TABLE));
        blocks.add(new OreGenerator.BlockData(x + 1, y + 1, z + 1, Material.CHEST));
    }

    private static void generateIcePalace(List<OreGenerator.BlockData> blocks, int x, int y, int z, Random random, BiomeType biome, PlanetType planetType) {
        // Buz sarayı
        for (int dx = -6; dx <= 6; dx++) {
            for (int dz = -6; dz <= 6; dz++) {
                blocks.add(new OreGenerator.BlockData(x + dx, y, z + dz, Material.PACKED_ICE));
            }
        }

        // Buz sütunları
        for (int i = 0; i < 6; i++) {
            double angle = (i * Math.PI * 2) / 6;
            int colX = x + (int)(Math.cos(angle) * 4);
            int colZ = z + (int)(Math.sin(angle) * 4);
            for (int dy = 1; dy <= 7; dy++) {
                blocks.add(new OreGenerator.BlockData(colX, y + dy, colZ, Material.ICE));
            }
        }

        // Çatı
        for (int dx = -4; dx <= 4; dx++) {
            for (int dz = -4; dz <= 4; dz++) {
                blocks.add(new OreGenerator.BlockData(x + dx, y + 8, z + dz, Material.SNOW_BLOCK));
            }
        }

        // İç mekan
        blocks.add(new OreGenerator.BlockData(x, y + 1, z, Material.ENCHANTING_TABLE));
        blocks.add(new OreGenerator.BlockData(x + 2, y + 1, z + 2, Material.CHEST));
    }

    private static void generateIglooVillage(List<OreGenerator.BlockData> blocks, int x, int y, int z, Random random, BiomeType biome, PlanetType planetType) {
        // İglo köyü - birden fazla iglo
        for (int i = 0; i < 3; i++) {
            int iglooX = x + random.nextInt(11) - 5;
            int iglooZ = z + random.nextInt(11) - 5;

            // Küçük iglo
            for (int dx = -2; dx <= 2; dx++) {
                for (int dz = -2; dz <= 2; dz++) {
                    double distance = Math.sqrt(dx * dx + dz * dz);
                    if (distance <= 2) {
                        blocks.add(new OreGenerator.BlockData(iglooX + dx, y, iglooZ + dz, Material.SNOW_BLOCK));
                        if (distance <= 1.5) {
                            blocks.add(new OreGenerator.BlockData(iglooX + dx, y + 1, iglooZ + dz, Material.SNOW_BLOCK));
                        }
                    }
                }
            }

            // İç mekan
            blocks.add(new OreGenerator.BlockData(iglooX, y + 1, iglooZ, Material.FURNACE));
        }
    }

    private static void generateDarkTower(List<OreGenerator.BlockData> blocks, int x, int y, int z, Random random, BiomeType biome, PlanetType planetType) {
        // Karanlık kule
        int height = 20 + random.nextInt(10);
        for (int dy = 0; dy < height; dy++) {
            int size = Math.max(1, 3 - dy / 6);
            for (int dx = -size; dx <= size; dx++) {
                for (int dz = -size; dz <= size; dz++) {
                    blocks.add(new OreGenerator.BlockData(x + dx, y + dy, z + dz, Material.OBSIDIAN));
                }
            }
        }

        // Tepe
        blocks.add(new OreGenerator.BlockData(x, y + height, z, Material.END_ROD));
        blocks.add(new OreGenerator.BlockData(x, y + height + 1, z, Material.CHEST));
    }

    private static void generateShadowTemple(List<OreGenerator.BlockData> blocks, int x, int y, int z, Random random, BiomeType biome, PlanetType planetType) {
        // Gölge tapınağı
        for (int dx = -4; dx <= 4; dx++) {
            for (int dz = -4; dz <= 4; dz++) {
                blocks.add(new OreGenerator.BlockData(x + dx, y, z + dz, Material.OBSIDIAN));
            }
        }

        // Gölge sütunları
        for (int i = 0; i < 4; i++) {
            int colX = x + (i % 2 == 0 ? -3 : 3);
            int colZ = z + (i < 2 ? -3 : 3);
            for (int dy = 1; dy <= 6; dy++) {
                blocks.add(new OreGenerator.BlockData(colX, y + dy, colZ, Material.CRYING_OBSIDIAN));
            }
        }

        // İç mekan
        blocks.add(new OreGenerator.BlockData(x, y + 1, z, Material.ENCHANTING_TABLE));
        blocks.add(new OreGenerator.BlockData(x + 1, y + 1, z + 1, Material.CHEST));
        blocks.add(new OreGenerator.BlockData(x, y + 2, z, Material.SOUL_LANTERN));
    }

    private static void generateLab(List<OreGenerator.BlockData> blocks, int x, int y, int z, Random random, BiomeType biome, PlanetType planetType) {
        // Laboratuvar
        for (int dx = -4; dx <= 4; dx++) {
            for (int dz = -4; dz <= 4; dz++) {
                blocks.add(new OreGenerator.BlockData(x + dx, y, z + dz, Material.GREEN_CONCRETE));
            }
        }

        // Laboratuvar duvarları
        for (int dy = 1; dy <= 4; dy++) {
            for (int dx = -4; dx <= 4; dx++) {
                blocks.add(new OreGenerator.BlockData(x + dx, y + dy, z - 4, Material.GLASS));
                blocks.add(new OreGenerator.BlockData(x + dx, y + dy, z + 4, Material.GLASS));
            }
            for (int dz = -3; dz <= 3; dz++) {
                blocks.add(new OreGenerator.BlockData(x - 4, y + dy, z + dz, Material.GLASS));
                blocks.add(new OreGenerator.BlockData(x + 4, y + dy, z + dz, Material.GLASS));
            }
        }

        // İç mekan
        blocks.add(new OreGenerator.BlockData(x, y + 1, z, Material.BREWING_STAND));
        blocks.add(new OreGenerator.BlockData(x + 2, y + 1, z + 2, Material.CRAFTING_TABLE));
        blocks.add(new OreGenerator.BlockData(x - 2, y + 1, z - 2, Material.CHEST));
    }

    private static void generateQuarantineZone(List<OreGenerator.BlockData> blocks, int x, int y, int z, Random random, BiomeType biome, PlanetType planetType) {
        // Karantina bölgesi
        for (int dx = -6; dx <= 6; dx++) {
            for (int dz = -6; dz <= 6; dz++) {
                blocks.add(new OreGenerator.BlockData(x + dx, y, z + dz, Material.GREEN_WOOL));
            }
        }

        // Çit
        for (int dy = 1; dy <= 3; dy++) {
            for (int dx = -6; dx <= 6; dx++) {
                blocks.add(new OreGenerator.BlockData(x + dx, y + dy, z - 6, Material.OAK_FENCE));
                blocks.add(new OreGenerator.BlockData(x + dx, y + dy, z + 6, Material.OAK_FENCE));
            }
            for (int dz = -5; dz <= 5; dz++) {
                blocks.add(new OreGenerator.BlockData(x - 6, y + dy, z + dz, Material.OAK_FENCE));
                blocks.add(new OreGenerator.BlockData(x + 6, y + dy, z + dz, Material.OAK_FENCE));
            }
        }

        // İç mekan
        blocks.add(new OreGenerator.BlockData(x, y + 1, z, Material.CHEST));
        blocks.add(new OreGenerator.BlockData(x + 2, y + 1, z, Material.POISONOUS_POTATO));
        blocks.add(new OreGenerator.BlockData(x - 2, y + 1, z, Material.SPIDER_EYE));
    }
}