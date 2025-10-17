package com.skyorbs.biomes;

import org.bukkit.Material;

import java.util.Random;

public enum BiomeType {

    // Earth-like biomes (6)
    FOREST("Orman", Material.GRASS_BLOCK, Material.OAK_LOG, Material.DIRT),
    PLAINS("Düzlük", Material.GRASS_BLOCK, Material.DIRT, Material.STONE),
    JUNGLE("Orman", Material.JUNGLE_LOG, Material.JUNGLE_LEAVES, Material.MOSS_BLOCK),
    SAVANNA("Savana", Material.GRASS_BLOCK, Material.ACACIA_LOG, Material.COARSE_DIRT),
    TAIGA("Tayga", Material.SPRUCE_LOG, Material.SNOW_BLOCK, Material.STONE),
    MEADOW("Çayır", Material.GRASS_BLOCK, Material.FLOWERING_AZALEA_LEAVES, Material.MOSS_BLOCK),

    // Desert biomes (3)
    DESERT("Çöl", Material.SAND, Material.SANDSTONE, Material.RED_SAND),
    BADLANDS("Kötü Toprak", Material.RED_SAND, Material.TERRACOTTA, Material.CLAY),
    CANYON("Kanyon", Material.ORANGE_TERRACOTTA, Material.RED_SANDSTONE, Material.STONE),

    // Ice biomes (3)
    FROZEN_TUNDRA("Donmuş Tundra", Material.SNOW_BLOCK, Material.ICE, Material.PACKED_ICE),
    ICE_SPIKES("Buz Dikenleri", Material.PACKED_ICE, Material.BLUE_ICE, Material.SNOW_BLOCK),
    GLACIER("Buzul", Material.BLUE_ICE, Material.ICE, Material.STONE),

    // Volcanic biomes (3)
    LAVA_OCEAN("Lav Okyanusu", Material.MAGMA_BLOCK, Material.NETHERRACK, Material.BLACKSTONE),
    OBSIDIAN_PLAINS("Obsidyen Düzlüğü", Material.OBSIDIAN, Material.CRYING_OBSIDIAN, Material.BLACKSTONE),
    MAGMA_CAVES("Magma Mağaraları", Material.NETHERRACK, Material.MAGMA_BLOCK, Material.BASALT),

    // Alien biomes (7)
    CRYSTAL_FOREST("Kristal Ormanı", Material.AMETHYST_BLOCK, Material.PURPUR_BLOCK, Material.QUARTZ_BLOCK),
    VOID("Boşluk", Material.END_STONE, Material.OBSIDIAN, Material.CRYING_OBSIDIAN),
    CORRUPTED("Bozulmuş", Material.WARPED_NYLIUM, Material.CRIMSON_NYLIUM, Material.SOUL_SOIL),
    MUSHROOM_GIANT("Dev Mantar", Material.RED_MUSHROOM_BLOCK, Material.BROWN_MUSHROOM_BLOCK, Material.MYCELIUM),
    TOXIC_SWAMP("Zehirli Bataklık", Material.SLIME_BLOCK, Material.MOSS_BLOCK, Material.SCULK),
    GLOWSTONE_CAVERN("Parıltı Taşı Mağarası", Material.GLOWSTONE, Material.SHROOMLIGHT, Material.SEA_LANTERN),
    CHORUS_LAND("Chorus Diyarı", Material.CHORUS_PLANT, Material.PURPUR_BLOCK, Material.END_STONE),

    // Additional diverse biomes (10 more for 20 total)
    BASALTIC("Bazaltik", Material.BASALT, Material.BLACKSTONE, Material.DEEPSLATE),
    CRYSTALLINE("Kristalimsi", Material.AMETHYST_BLOCK, Material.QUARTZ_BLOCK, Material.DIAMOND_BLOCK),
    FUNGAL("Mantarlı", Material.MYCELIUM, Material.RED_MUSHROOM_BLOCK, Material.BROWN_MUSHROOM_BLOCK),
    CORAL("Mercan", Material.BRAIN_CORAL_BLOCK, Material.TUBE_CORAL_BLOCK, Material.HORN_CORAL_BLOCK),
    TOXIC("Zehirli", Material.GREEN_CONCRETE, Material.LIME_CONCRETE, Material.SLIME_BLOCK),
    AURORA("Aurora", Material.PACKED_ICE, Material.BLUE_ICE, Material.LIGHT_BLUE_CONCRETE),
    STORMY("Fırtınalı", Material.GRAY_CONCRETE, Material.LIGHT_GRAY_CONCRETE, Material.STONE),
    FOGGY("Sisli", Material.WHITE_CONCRETE, Material.LIGHT_GRAY_CONCRETE, Material.COBBLESTONE),
    CORROSIVE("Aşındırıcı", Material.YELLOW_CONCRETE, Material.ORANGE_CONCRETE, Material.RED_CONCRETE),
    LUMINOUS("Parıltılı", Material.SEA_LANTERN, Material.GLOWSTONE, Material.SHROOMLIGHT);
    
    private final String displayName;
    private final Material primary;
    private final Material secondary;
    private final Material tertiary;
    
    BiomeType(String displayName, Material primary, Material secondary, Material tertiary) {
        this.displayName = displayName;
        this.primary = primary;
        this.secondary = secondary;
        this.tertiary = tertiary;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public Material getMaterial(int depth, Random random) {
        if (depth == 0) {
            return primary;
        } else if (depth < 3) {
            return random.nextDouble() < 0.7 ? secondary : primary;
        } else if (depth < 6) {
            return random.nextDouble() < 0.5 ? tertiary : secondary;
        } else {
            return tertiary;
        }
    }
    
    public static BiomeType getRandomBiome(Random random) {
        BiomeType[] values = values();
        return values[random.nextInt(values.length)];
    }
    
    public static BiomeType getRandomBiomeWeighted(Random random) {
        double rand = random.nextDouble();

        if (rand < 0.25) {
            // Earth-like biomes (6)
            BiomeType[] earthLike = {FOREST, PLAINS, JUNGLE, SAVANNA, TAIGA, MEADOW};
            return earthLike[random.nextInt(earthLike.length)];
        } else if (rand < 0.40) {
            // Desert biomes (3)
            BiomeType[] desertLike = {DESERT, BADLANDS, CANYON};
            return desertLike[random.nextInt(desertLike.length)];
        } else if (rand < 0.55) {
            // Ice biomes (3)
            BiomeType[] ice = {FROZEN_TUNDRA, ICE_SPIKES, GLACIER};
            return ice[random.nextInt(ice.length)];
        } else if (rand < 0.70) {
            // Volcanic biomes (3)
            BiomeType[] volcanic = {LAVA_OCEAN, OBSIDIAN_PLAINS, MAGMA_CAVES};
            return volcanic[random.nextInt(volcanic.length)];
        } else if (rand < 0.85) {
            // Alien biomes (7)
            BiomeType[] alien = {CRYSTAL_FOREST, VOID, CORRUPTED, MUSHROOM_GIANT, TOXIC_SWAMP, GLOWSTONE_CAVERN, CHORUS_LAND};
            return alien[random.nextInt(alien.length)];
        } else {
            // Additional diverse biomes (10)
            BiomeType[] diverse = {BASALTIC, CRYSTALLINE, FUNGAL, CORAL, TOXIC, AURORA, STORMY, FOGGY, CORROSIVE, LUMINOUS};
            return diverse[random.nextInt(diverse.length)];
        }
    }
}
