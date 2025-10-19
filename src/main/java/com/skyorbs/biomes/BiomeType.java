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

    // Additional diverse biomes (25+ more for massive variety)
    BASALTIC("Bazaltik", Material.BASALT, Material.BLACKSTONE, Material.DEEPSLATE),
    CRYSTALLINE("Kristalimsi", Material.AMETHYST_BLOCK, Material.QUARTZ_BLOCK, Material.DIAMOND_BLOCK),
    FUNGAL("Mantarlı", Material.MYCELIUM, Material.RED_MUSHROOM_BLOCK, Material.BROWN_MUSHROOM_BLOCK),
    CORAL("Mercan", Material.BRAIN_CORAL_BLOCK, Material.TUBE_CORAL_BLOCK, Material.HORN_CORAL_BLOCK),
    TOXIC("Zehirli", Material.GREEN_CONCRETE, Material.LIME_CONCRETE, Material.SLIME_BLOCK),
    AURORA("Aurora", Material.PACKED_ICE, Material.BLUE_ICE, Material.LIGHT_BLUE_CONCRETE),
    STORMY("Fırtınalı", Material.GRAY_CONCRETE, Material.LIGHT_GRAY_CONCRETE, Material.STONE),
    FOGGY("Sisli", Material.WHITE_CONCRETE, Material.LIGHT_GRAY_CONCRETE, Material.COBBLESTONE),
    CORROSIVE("Aşındırıcı", Material.YELLOW_CONCRETE, Material.ORANGE_CONCRETE, Material.RED_CONCRETE),
    LUMINOUS("Parıltılı", Material.SEA_LANTERN, Material.GLOWSTONE, Material.SHROOMLIGHT),

    // NEW: Extended biomes for 10,000+ combinations
    NEBULOUS("Sisli", Material.WHITE_CONCRETE, Material.LIGHT_GRAY_CONCRETE, Material.GRAY_CONCRETE),
    COSMIC("Kozmik", Material.END_STONE, Material.PURPUR_BLOCK, Material.OBSIDIAN),
    ETHEREAL("Eterik", Material.WHITE_WOOL, Material.LIGHT_GRAY_WOOL, Material.GRAY_WOOL),
    PRIMAL("İlkel", Material.STONE, Material.COBBLESTONE, Material.MOSSY_COBBLESTONE),
    CELESTIAL("Göksel", Material.QUARTZ_BLOCK, Material.SEA_LANTERN, Material.GLOWSTONE),
    VOID_BORN("Boşluk Doğumlu", Material.OBSIDIAN, Material.CRYING_OBSIDIAN, Material.END_STONE),
    STAR_FORGED("Yıldız Dövülmüş", Material.IRON_BLOCK, Material.GOLD_BLOCK, Material.DIAMOND_BLOCK),
    NEBULA_INFUSED("Sis Enjekte", Material.PURPLE_CONCRETE, Material.MAGENTA_CONCRETE, Material.PINK_CONCRETE),
    GALACTIC("Galaktik", Material.LAPIS_BLOCK, Material.DIAMOND_BLOCK, Material.EMERALD_BLOCK),
    UNIVERSE_BOUND("Evren Bağlı", Material.BEACON, Material.SEA_LANTERN, Material.CONDUIT),

    // Elemental biomes
    FLAME("Alev", Material.NETHERRACK, Material.MAGMA_BLOCK, Material.FIRE),
    FROST("Kırağı", Material.ICE, Material.PACKED_ICE, Material.BLUE_ICE),
    STORM("Fırtına", Material.STONE, Material.COBBLESTONE, Material.GRAVEL),
    EARTH("Toprak", Material.DIRT, Material.GRASS_BLOCK, Material.STONE),
    WATER("Su", Material.WATER, Material.ICE, Material.PRISMARINE),
    WIND("Rüzgar", Material.AIR, Material.WHITE_WOOL, Material.STRING),

    // Mystical biomes
    ARCANE("Gizemli", Material.PURPUR_BLOCK, Material.END_STONE, Material.OBSIDIAN),
    DIVINE("İlahî", Material.GOLD_BLOCK, Material.DIAMOND_BLOCK, Material.BEACON),
    INFERNAL("Cehennemî", Material.NETHERRACK, Material.NETHER_BRICKS, Material.MAGMA_BLOCK),
    CELESTIAL_BLESSED("Göksel Bereketli", Material.QUARTZ_BLOCK, Material.GOLD_BLOCK, Material.DIAMOND_BLOCK),
    VOID_ENERGY("Boşluk Enerjisi", Material.OBSIDIAN, Material.CRYING_OBSIDIAN, Material.RESPAWN_ANCHOR),

    // Rare special biomes
    QUANTUM("Kuantum", Material.AMETHYST_BLOCK, Material.BUDDING_AMETHYST, Material.AMETHYST_CLUSTER),
    TEMPORAL("Zamansal", Material.CLOCK, Material.COMPASS, Material.RECOVERY_COMPASS),
    DIMENSIONAL("Boyutsal", Material.END_PORTAL_FRAME, Material.END_STONE, Material.OBSIDIAN),
    MIRROR("Ayna", Material.GLASS, Material.WHITE_STAINED_GLASS, Material.LIGHT_GRAY_STAINED_GLASS),
    PHASE("Faz", Material.SOUL_SAND, Material.SOUL_SOIL, Material.SOUL_CAMPFIRE),

    // Exotic material biomes
    HONEYCOMB("Bal Peteği", Material.HONEY_BLOCK, Material.HONEYCOMB_BLOCK, Material.BEEHIVE),
    SPONGE("Sünger", Material.SPONGE, Material.WET_SPONGE, Material.PRISMARINE),
    BONE("Kemik", Material.BONE_BLOCK, Material.WHITE_CONCRETE, Material.LIGHT_GRAY_CONCRETE),
    BAMBOO("Bambu", Material.BAMBOO_BLOCK, Material.GREEN_CONCRETE, Material.JUNGLE_WOOD),
    CHERRY("Kiraz", Material.PINK_CONCRETE, Material.PINK_TERRACOTTA, Material.WHITE_CONCRETE),
    CALCITE("Kalsit", Material.CALCITE, Material.QUARTZ_BLOCK, Material.DRIPSTONE_BLOCK),
    DRIPSTONE("Damla Taşı", Material.DRIPSTONE_BLOCK, Material.STONE, Material.CALCITE),
    OXIDIZED("Oksitlenmiş", Material.OXIDIZED_COPPER, Material.WEATHERED_COPPER, Material.EXPOSED_COPPER),
    COBBLED("Kırık Taş", Material.COBBLESTONE, Material.MOSSY_COBBLESTONE, Material.STONE),
    POLISHED("Parlatılmış", Material.POLISHED_ANDESITE, Material.POLISHED_DIORITE, Material.POLISHED_GRANITE),
    BRICKS("Tuğla", Material.BRICKS, Material.STONE_BRICKS, Material.MOSSY_STONE_BRICKS),
    WOOL("Yün", Material.WHITE_WOOL, Material.LIGHT_GRAY_WOOL, Material.GRAY_WOOL),
    GLASS("Cam", Material.GLASS, Material.WHITE_STAINED_GLASS, Material.LIGHT_GRAY_STAINED_GLASS),
    PACKED_MUD("Sıkıştırılmış Çamur", Material.PACKED_MUD, Material.MUD_BRICKS, Material.MUD),
    REINFORCED("Takviyeli", Material.REINFORCED_DEEPSLATE, Material.DEEPSLATE, Material.DEEPSLATE_BRICKS);
    
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

        if (rand < 0.15) {
            // Earth-like biomes (6) - Common
            BiomeType[] earthLike = {FOREST, PLAINS, JUNGLE, SAVANNA, TAIGA, MEADOW};
            return earthLike[random.nextInt(earthLike.length)];
        } else if (rand < 0.25) {
            // Desert biomes (3) - Common
            BiomeType[] desertLike = {DESERT, BADLANDS, CANYON};
            return desertLike[random.nextInt(desertLike.length)];
        } else if (rand < 0.35) {
            // Ice biomes (3) - Common
            BiomeType[] ice = {FROZEN_TUNDRA, ICE_SPIKES, GLACIER};
            return ice[random.nextInt(ice.length)];
        } else if (rand < 0.45) {
            // Volcanic biomes (3) - Common
            BiomeType[] volcanic = {LAVA_OCEAN, OBSIDIAN_PLAINS, MAGMA_CAVES};
            return volcanic[random.nextInt(volcanic.length)];
        } else if (rand < 0.55) {
            // Alien biomes (7) - Uncommon
            BiomeType[] alien = {CRYSTAL_FOREST, VOID, CORRUPTED, MUSHROOM_GIANT, TOXIC_SWAMP, GLOWSTONE_CAVERN, CHORUS_LAND};
            return alien[random.nextInt(alien.length)];
        } else if (rand < 0.65) {
            // Original diverse biomes (10) - Uncommon
            BiomeType[] diverse = {BASALTIC, CRYSTALLINE, FUNGAL, CORAL, TOXIC, AURORA, STORMY, FOGGY, CORROSIVE, LUMINOUS};
            return diverse[random.nextInt(diverse.length)];
        } else if (rand < 0.75) {
            // Extended biomes (10) - Rare
            BiomeType[] extended = {NEBULOUS, COSMIC, ETHEREAL, PRIMAL, CELESTIAL, VOID_BORN, STAR_FORGED, NEBULA_INFUSED, GALACTIC, UNIVERSE_BOUND};
            return extended[random.nextInt(extended.length)];
        } else if (rand < 0.85) {
            // Elemental biomes (6) - Rare
            BiomeType[] elemental = {FLAME, FROST, STORM, EARTH, WATER, WIND};
            return elemental[random.nextInt(elemental.length)];
        } else if (rand < 0.92) {
            // Mystical biomes (5) - Very Rare
            BiomeType[] mystical = {ARCANE, DIVINE, INFERNAL, CELESTIAL_BLESSED, VOID_ENERGY};
            return mystical[random.nextInt(mystical.length)];
        } else if (rand < 0.96) {
            // Special biomes (5) - Ultra Rare
            BiomeType[] special = {QUANTUM, TEMPORAL, DIMENSIONAL, MIRROR, PHASE};
            return special[random.nextInt(special.length)];
        } else {
            // Exotic material biomes (15) - Legendary
            BiomeType[] exotic = {HONEYCOMB, SPONGE, BONE, BAMBOO, CHERRY, CALCITE, DRIPSTONE, OXIDIZED, COBBLED, POLISHED, BRICKS, WOOL, GLASS, PACKED_MUD, REINFORCED};
            return exotic[random.nextInt(exotic.length)];
        }
    }
}
