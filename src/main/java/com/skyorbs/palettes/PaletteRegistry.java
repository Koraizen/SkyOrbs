package com.skyorbs.palettes;

import org.bukkit.Material;
import java.util.*;

/**
 * Registry for planet palettes - enables 2000+ diverse planet combinations
 * Combines with shapes, modifiers, and atmospheres for massive variety
 */
public class PaletteRegistry {
    
    private final Map<String, PlanetPalette> palettes = new HashMap<String, PlanetPalette>();
    private final List<PlanetPalette> weightedList = new ArrayList<PlanetPalette>();
    private double totalWeight = 0.0;
    
    public PaletteRegistry() {
        registerDefaultPalettes();
        buildWeightedList();
    }
    
    /**
     * Register 20+ diverse palettes for maximum variety
     */
    private void registerDefaultPalettes() {
        // Basaltic (volcanic, dark)
        register(new PlanetPalette("basaltic", "Bazaltik",
            new Material[]{Material.BASALT, Material.BLACKSTONE, Material.SMOOTH_BASALT},
            new Material[]{Material.BLACKSTONE, Material.DEEPSLATE, Material.BASALT},
            new Material[]{Material.BASALT, Material.BLACKSTONE, Material.SMOOTH_BASALT},
            new Material[]{Material.MAGMA_BLOCK, Material.LAVA},
            10.0));
        
        // Crystalline (shiny, precious)
        register(new PlanetPalette("crystalline", "Kristal",
            new Material[]{Material.AMETHYST_BLOCK, Material.QUARTZ_BLOCK, Material.PURPUR_BLOCK},
            new Material[]{Material.QUARTZ_BLOCK, Material.CALCITE, Material.DRIPSTONE_BLOCK},
            new Material[]{Material.AMETHYST_BLOCK, Material.QUARTZ_BLOCK, Material.PURPUR_BLOCK},
            new Material[]{Material.GLOWSTONE, Material.SEA_LANTERN},
            8.0));
        
        // Fungal (mushroom, organic)
        register(new PlanetPalette("fungal", "Mantarlı",
            new Material[]{Material.MYCELIUM, Material.BROWN_MUSHROOM_BLOCK, Material.RED_MUSHROOM_BLOCK},
            new Material[]{Material.DIRT, Material.PODZOL, Material.MYCELIUM},
            new Material[]{Material.MYCELIUM, Material.BROWN_MUSHROOM_BLOCK, Material.RED_MUSHROOM_BLOCK},
            new Material[]{Material.MUSHROOM_STEM, Material.SHROOMLIGHT},
            9.0));
        
        // Coral (aquatic, vibrant)
        register(new PlanetPalette("coral", "Mercan",
            new Material[]{Material.BRAIN_CORAL_BLOCK, Material.TUBE_CORAL_BLOCK, Material.BUBBLE_CORAL_BLOCK},
            new Material[]{Material.HORN_CORAL_BLOCK, Material.FIRE_CORAL_BLOCK, Material.PRISMARINE},
            new Material[]{Material.BRAIN_CORAL_BLOCK, Material.TUBE_CORAL_BLOCK, Material.BUBBLE_CORAL_BLOCK},
            new Material[]{Material.SEA_LANTERN, Material.PRISMARINE_BRICKS},
            7.0));
        
        // Toxic (poisonous, dangerous)
        register(new PlanetPalette("toxic", "Zehirli",
            new Material[]{Material.GREEN_CONCRETE, Material.LIME_CONCRETE, Material.SLIME_BLOCK},
            new Material[]{Material.GREEN_TERRACOTTA, Material.LIME_TERRACOTTA, Material.MOSS_BLOCK},
            new Material[]{Material.GREEN_CONCRETE, Material.LIME_CONCRETE, Material.SLIME_BLOCK},
            new Material[]{Material.SLIME_BLOCK, Material.HONEY_BLOCK},
            6.0));
        
        // Frozen (ice, snow)
        register(new PlanetPalette("frozen", "Donmuş",
            new Material[]{Material.SNOW_BLOCK, Material.PACKED_ICE, Material.ICE},
            new Material[]{Material.PACKED_ICE, Material.BLUE_ICE, Material.ICE},
            new Material[]{Material.SNOW_BLOCK, Material.PACKED_ICE, Material.ICE},
            new Material[]{Material.FROSTED_ICE, Material.POWDER_SNOW},
            8.0));
        
        // Volcanic (lava, heat)
        register(new PlanetPalette("volcanic", "Volkanik",
            new Material[]{Material.MAGMA_BLOCK, Material.NETHERRACK, Material.BLACKSTONE},
            new Material[]{Material.NETHERRACK, Material.BASALT, Material.BLACKSTONE},
            new Material[]{Material.MAGMA_BLOCK, Material.NETHERRACK, Material.BLACKSTONE},
            new Material[]{Material.FIRE, Material.LAVA},
            7.0));
        
        // Verdant (lush, green)
        register(new PlanetPalette("verdant", "Yeşil",
            new Material[]{Material.GRASS_BLOCK, Material.MOSS_BLOCK, Material.FLOWERING_AZALEA_LEAVES},
            new Material[]{Material.DIRT, Material.ROOTED_DIRT, Material.MOSS_BLOCK},
            new Material[]{Material.GRASS_BLOCK, Material.MOSS_BLOCK, Material.FLOWERING_AZALEA_LEAVES},
            new Material[]{Material.AZALEA, Material.FLOWERING_AZALEA},
            10.0));
        
        // Desert (sandy, dry)
        register(new PlanetPalette("desert", "Çöl",
            new Material[]{Material.SAND, Material.RED_SAND, Material.SANDSTONE},
            new Material[]{Material.SANDSTONE, Material.RED_SANDSTONE, Material.SAND},
            new Material[]{Material.SAND, Material.RED_SAND, Material.SANDSTONE},
            new Material[]{Material.DEAD_BUSH, Material.CACTUS},
            9.0));
        
        // Metallic (iron, copper)
        register(new PlanetPalette("metallic", "Metalik",
            new Material[]{Material.IRON_BLOCK, Material.COPPER_BLOCK, Material.WEATHERED_COPPER},
            new Material[]{Material.IRON_BLOCK, Material.EXPOSED_COPPER, Material.OXIDIZED_COPPER},
            new Material[]{Material.IRON_BLOCK, Material.COPPER_BLOCK, Material.WEATHERED_COPPER},
            new Material[]{Material.IRON_BARS, Material.CHAIN},
            5.0));
        
        // Void (dark, mysterious)
        register(new PlanetPalette("void", "Boşluk",
            new Material[]{Material.END_STONE, Material.OBSIDIAN, Material.CRYING_OBSIDIAN},
            new Material[]{Material.OBSIDIAN, Material.END_STONE, Material.BLACKSTONE},
            new Material[]{Material.OBSIDIAN, Material.END_STONE, Material.BLACKSTONE},
            new Material[]{Material.ENDER_CHEST, Material.END_ROD},
            4.0));
        
        // Luminous (glowing, bright)
        register(new PlanetPalette("luminous", "Parlak",
            new Material[]{Material.GLOWSTONE, Material.SEA_LANTERN, Material.SHROOMLIGHT},
            new Material[]{Material.GLOWSTONE, Material.OCHRE_FROGLIGHT, Material.VERDANT_FROGLIGHT},
            new Material[]{Material.GLOWSTONE, Material.SEA_LANTERN, Material.SHROOMLIGHT},
            new Material[]{Material.TORCH, Material.LANTERN},
            5.0));
        
        // Warped (nether, alien)
        register(new PlanetPalette("warped", "Çarpık",
            new Material[]{Material.WARPED_NYLIUM, Material.WARPED_WART_BLOCK, Material.WARPED_STEM},
            new Material[]{Material.WARPED_NYLIUM, Material.NETHERRACK, Material.SOUL_SOIL},
            new Material[]{Material.WARPED_NYLIUM, Material.WARPED_WART_BLOCK, Material.WARPED_STEM},
            new Material[]{Material.WARPED_ROOTS, Material.WARPED_FUNGUS},
            6.0));
        
        // Crimson (nether, red)
        register(new PlanetPalette("crimson", "Kızıl",
            new Material[]{Material.CRIMSON_NYLIUM, Material.CRIMSON_STEM, Material.NETHER_WART_BLOCK},
            new Material[]{Material.CRIMSON_NYLIUM, Material.NETHERRACK, Material.SOUL_SAND},
            new Material[]{Material.CRIMSON_NYLIUM, Material.CRIMSON_STEM, Material.NETHER_WART_BLOCK},
            new Material[]{Material.CRIMSON_ROOTS, Material.CRIMSON_FUNGUS},
            6.0));
        
        // Sandstone (ancient, carved)
        register(new PlanetPalette("sandstone", "Kumtaşı",
            new Material[]{Material.SANDSTONE, Material.SMOOTH_SANDSTONE, Material.CHISELED_SANDSTONE},
            new Material[]{Material.SANDSTONE, Material.CUT_SANDSTONE, Material.SAND},
            new Material[]{Material.SANDSTONE, Material.SMOOTH_SANDSTONE, Material.CHISELED_SANDSTONE},
            new Material[]{Material.SANDSTONE_STAIRS, Material.SANDSTONE_SLAB},
            7.0));
        
        // Terracotta (colorful, layered)
        register(new PlanetPalette("terracotta", "Kil",
            new Material[]{Material.ORANGE_TERRACOTTA, Material.RED_TERRACOTTA, Material.YELLOW_TERRACOTTA},
            new Material[]{Material.BROWN_TERRACOTTA, Material.WHITE_TERRACOTTA, Material.LIGHT_GRAY_TERRACOTTA},
            new Material[]{Material.ORANGE_TERRACOTTA, Material.RED_TERRACOTTA, Material.YELLOW_TERRACOTTA},
            new Material[]{Material.FLOWER_POT, Material.BRICK},
            8.0));
        
        // Concrete (modern, smooth)
        register(new PlanetPalette("concrete", "Beton",
            new Material[]{Material.LIGHT_GRAY_CONCRETE, Material.GRAY_CONCRETE, Material.WHITE_CONCRETE},
            new Material[]{Material.GRAY_CONCRETE, Material.STONE, Material.ANDESITE},
            new Material[]{Material.LIGHT_GRAY_CONCRETE, Material.GRAY_CONCRETE, Material.WHITE_CONCRETE},
            new Material[]{Material.GRAVEL, Material.COBBLESTONE},
            6.0));
        
        // Prismarine (ocean, ancient)
        register(new PlanetPalette("prismarine", "Prizmarin",
            new Material[]{Material.PRISMARINE, Material.PRISMARINE_BRICKS, Material.DARK_PRISMARINE},
            new Material[]{Material.PRISMARINE, Material.DARK_PRISMARINE, Material.STONE},
            new Material[]{Material.OBSIDIAN, Material.DARK_PRISMARINE, Material.STONE},
            new Material[]{Material.SEA_LANTERN, Material.PRISMARINE_SLAB},
            5.0));
        
        // Sculk (deep dark, mysterious)
        register(new PlanetPalette("sculk", "Sculk",
            new Material[]{Material.SCULK, Material.SCULK_VEIN, Material.SCULK_CATALYST},
            new Material[]{Material.SCULK, Material.DEEPSLATE, Material.SCULK_CATALYST},
            new Material[]{Material.SCULK, Material.SCULK_VEIN, Material.SCULK_CATALYST},
            new Material[]{Material.SCULK_SENSOR, Material.SCULK_CATALYST},
            3.0));
        
        // Mud (swamp, wet)
        register(new PlanetPalette("mud", "Çamur",
            new Material[]{Material.MUD, Material.MUDDY_MANGROVE_ROOTS, Material.MUD_BRICKS},
            new Material[]{Material.MUD, Material.CLAY, Material.PACKED_MUD},
            new Material[]{Material.MUD, Material.MUDDY_MANGROVE_ROOTS, Material.MUD_BRICKS},
            new Material[]{Material.MANGROVE_ROOTS, Material.MOSS_CARPET},
            7.0));

        // Rainbow (colorful, vibrant)
        register(new PlanetPalette("rainbow", "Gökkuşağı",
            new Material[]{Material.RED_CONCRETE, Material.ORANGE_CONCRETE, Material.YELLOW_CONCRETE},
            new Material[]{Material.GREEN_CONCRETE, Material.BLUE_CONCRETE, Material.PURPLE_CONCRETE},
            new Material[]{Material.RED_CONCRETE, Material.ORANGE_CONCRETE, Material.YELLOW_CONCRETE},
            new Material[]{Material.WHITE_WOOL, Material.GLASS},
            4.0));

        // Netherrack (hellish, fiery)
        register(new PlanetPalette("netherrack", "Cehennem",
            new Material[]{Material.NETHERRACK, Material.NETHER_BRICKS, Material.RED_NETHER_BRICKS},
            new Material[]{Material.NETHERRACK, Material.NETHER_BRICKS, Material.SOUL_SAND},
            new Material[]{Material.NETHERRACK, Material.NETHER_BRICKS, Material.RED_NETHER_BRICKS},
            new Material[]{Material.FIRE, Material.SOUL_FIRE},
            6.0));

        // Endstone (end dimension, mysterious)
        register(new PlanetPalette("endstone", "End",
            new Material[]{Material.END_STONE, Material.END_STONE_BRICKS, Material.PURPUR_BLOCK},
            new Material[]{Material.END_STONE, Material.PURPUR_BLOCK, Material.END_STONE_BRICKS},
            new Material[]{Material.END_STONE, Material.END_STONE_BRICKS, Material.PURPUR_BLOCK},
            new Material[]{Material.END_ROD, Material.ENDER_CHEST},
            5.0));

        // Honey (sweet, sticky)
        register(new PlanetPalette("honey", "Bal",
            new Material[]{Material.HONEY_BLOCK, Material.HONEYCOMB_BLOCK, Material.YELLOW_CONCRETE},
            new Material[]{Material.HONEY_BLOCK, Material.YELLOW_TERRACOTTA, Material.ORANGE_TERRACOTTA},
            new Material[]{Material.YELLOW_TERRACOTTA, Material.ORANGE_CONCRETE, Material.STONE},
            new Material[]{Material.BEEHIVE, Material.BEE_NEST},
            4.0));

        // Bone (skeletal, creepy)
        register(new PlanetPalette("bone", "Kemik",
            new Material[]{Material.BONE_BLOCK, Material.WHITE_CONCRETE, Material.LIGHT_GRAY_CONCRETE},
            new Material[]{Material.BONE_BLOCK, Material.GRAY_CONCRETE, Material.WHITE_TERRACOTTA},
            new Material[]{Material.GRAY_TERRACOTTA, Material.STONE, Material.DEEPSLATE},
            new Material[]{Material.SKELETON_SKULL, Material.WITHER_SKELETON_SKULL},
            3.0));

        // Sponge (ocean, absorbent)
        register(new PlanetPalette("sponge", "Sünger",
            new Material[]{Material.SPONGE, Material.WET_SPONGE, Material.YELLOW_CONCRETE},
            new Material[]{Material.SPONGE, Material.PRISMARINE, Material.SEA_LANTERN},
            new Material[]{Material.PRISMARINE, Material.DARK_PRISMARINE, Material.STONE},
            new Material[]{Material.KELP, Material.SEAGRASS},
            4.0));

        // Tuff (volcanic, rocky)
        register(new PlanetPalette("tuff", "Tuf",
            new Material[]{Material.TUFF, Material.CALCITE, Material.DRIPSTONE_BLOCK},
            new Material[]{Material.TUFF, Material.ANDESITE, Material.STONE},
            new Material[]{Material.DEEPSLATE, Material.TUFF, Material.STONE},
            new Material[]{Material.POINTED_DRIPSTONE, Material.AMETHYST_CLUSTER},
            6.0));

        // Cherry (sakura-like, pink)
        register(new PlanetPalette("cherry", "Kiraz",
            new Material[]{Material.PINK_CONCRETE, Material.PINK_TERRACOTTA, Material.WHITE_CONCRETE},
            new Material[]{Material.PINK_TERRACOTTA, Material.MAGENTA_TERRACOTTA, Material.WHITE_TERRACOTTA},
            new Material[]{Material.WHITE_TERRACOTTA, Material.STONE, Material.CLAY},
            new Material[]{Material.CHERRY_LEAVES, Material.PINK_PETALS},
            5.0));

        // Bamboo (tropical, fast-growing)
        register(new PlanetPalette("bamboo", "Bambu",
            new Material[]{Material.BAMBOO_BLOCK, Material.GREEN_CONCRETE, Material.JUNGLE_WOOD},
            new Material[]{Material.BAMBOO_BLOCK, Material.GREEN_TERRACOTTA, Material.DIRT},
            new Material[]{Material.GREEN_TERRACOTTA, Material.STONE, Material.CLAY},
            new Material[]{Material.BAMBOO, Material.JUNGLE_LEAVES},
            6.0));

        // Calcite (cave, crystalline)
        register(new PlanetPalette("calcite", "Kalsit",
            new Material[]{Material.CALCITE, Material.QUARTZ_BLOCK, Material.DRIPSTONE_BLOCK},
            new Material[]{Material.CALCITE, Material.STONE, Material.DEEPSLATE},
            new Material[]{Material.CALCITE, Material.QUARTZ_BLOCK, Material.DRIPSTONE_BLOCK},
            new Material[]{Material.AMETHYST_CLUSTER, Material.POINTED_DRIPSTONE},
            5.0));

        // Dripstone (cave, wet)
        register(new PlanetPalette("dripstone", "Damla Taşı",
            new Material[]{Material.DRIPSTONE_BLOCK, Material.STONE, Material.CALCITE},
            new Material[]{Material.DRIPSTONE_BLOCK, Material.DEEPSLATE, Material.STONE},
            new Material[]{Material.DEEPSLATE, Material.DRIPSTONE_BLOCK, Material.WATER},
            new Material[]{Material.POINTED_DRIPSTONE, Material.WATER},
            4.0));

        // Oxidized (aged copper)
        register(new PlanetPalette("oxidized", "Oksitlenmiş",
            new Material[]{Material.OXIDIZED_COPPER, Material.WEATHERED_COPPER, Material.EXPOSED_COPPER},
            new Material[]{Material.OXIDIZED_COPPER, Material.COPPER_BLOCK, Material.IRON_BLOCK},
            new Material[]{Material.OXIDIZED_COPPER, Material.WEATHERED_COPPER, Material.EXPOSED_COPPER},
            new Material[]{Material.LIGHTNING_ROD, Material.CHAIN},
            4.0));

        // Cobbled (rough, natural)
        register(new PlanetPalette("cobbled", "Kırık Taş",
            new Material[]{Material.COBBLESTONE, Material.MOSSY_COBBLESTONE, Material.STONE},
            new Material[]{Material.COBBLESTONE, Material.STONE, Material.ANDESITE},
            new Material[]{Material.COBBLESTONE, Material.MOSSY_COBBLESTONE, Material.STONE},
            new Material[]{Material.MOSS_CARPET, Material.VINE},
            8.0));

        // Polished (smooth, refined)
        register(new PlanetPalette("polished", "Parlatılmış",
            new Material[]{Material.POLISHED_ANDESITE, Material.POLISHED_DIORITE, Material.POLISHED_GRANITE},
            new Material[]{Material.POLISHED_ANDESITE, Material.STONE, Material.ANDESITE},
            new Material[]{Material.POLISHED_ANDESITE, Material.POLISHED_DIORITE, Material.POLISHED_GRANITE},
            new Material[]{Material.STONE_SLAB, Material.STONE_STAIRS},
            7.0));

        // Bricks (constructed, urban)
        register(new PlanetPalette("bricks", "Tuğla",
            new Material[]{Material.BRICKS, Material.STONE_BRICKS, Material.MOSSY_STONE_BRICKS},
            new Material[]{Material.BRICKS, Material.STONE_BRICKS, Material.COBBLESTONE},
            new Material[]{Material.BRICKS, Material.STONE_BRICKS, Material.MOSSY_STONE_BRICKS},
            new Material[]{Material.BRICK_STAIRS, Material.BRICK_SLAB},
            6.0));

        // Wool (soft, colorful)
        register(new PlanetPalette("wool", "Yün",
            new Material[]{Material.WHITE_WOOL, Material.LIGHT_GRAY_WOOL, Material.GRAY_WOOL},
            new Material[]{Material.WHITE_WOOL, Material.STONE, Material.DIRT},
            new Material[]{Material.WHITE_WOOL, Material.LIGHT_GRAY_WOOL, Material.GRAY_WOOL},
            new Material[]{Material.WHITE_CARPET, Material.WHITE_BED},
            3.0));

        // Glass (transparent, fragile)
        register(new PlanetPalette("glass", "Cam",
            new Material[]{Material.GLASS, Material.WHITE_STAINED_GLASS, Material.LIGHT_GRAY_STAINED_GLASS},
            new Material[]{Material.GLASS, Material.STONE, Material.SAND},
            new Material[]{Material.GLASS, Material.WHITE_STAINED_GLASS, Material.LIGHT_GRAY_STAINED_GLASS},
            new Material[]{Material.GLASS_PANE, Material.IRON_BARS},
            2.0));

        // Packed Mud (adobe-like)
        register(new PlanetPalette("packed_mud", "Sıkıştırılmış Çamur",
            new Material[]{Material.PACKED_MUD, Material.MUD_BRICKS, Material.MUD},
            new Material[]{Material.PACKED_MUD, Material.CLAY, Material.DIRT},
            new Material[]{Material.PACKED_MUD, Material.MUD_BRICKS, Material.MUD},
            new Material[]{Material.MUD_BRICK_SLAB, Material.MUD_BRICK_STAIRS},
            5.0));

        // Reinforced Deepslate (ultra-hard)
        register(new PlanetPalette("reinforced", "Takviyeli",
            new Material[]{Material.REINFORCED_DEEPSLATE, Material.DEEPSLATE, Material.DEEPSLATE_BRICKS},
            new Material[]{Material.REINFORCED_DEEPSLATE, Material.DEEPSLATE, Material.STONE},
            new Material[]{Material.REINFORCED_DEEPSLATE, Material.DEEPSLATE, Material.DEEPSLATE_BRICKS},
            new Material[]{Material.DEEPSLATE_BRICKS, Material.DEEPSLATE_BRICK_SLAB},
            2.0));
    }
    
    private void register(PlanetPalette palette) {
        palettes.put(palette.getId(), palette);
    }
    
    private void buildWeightedList() {
        weightedList.clear();
        totalWeight = 0.0;
        
        for (PlanetPalette palette : palettes.values()) {
            totalWeight += palette.getWeight();
            weightedList.add(palette);
        }
    }
    
    /**
     * Get random palette based on weights
     */
    public PlanetPalette getRandomPalette(Random random) {
        double value = random.nextDouble() * totalWeight;
        double current = 0.0;
        
        for (PlanetPalette palette : weightedList) {
            current += palette.getWeight();
            if (value <= current) {
                return palette;
            }
        }
        
        return weightedList.get(0);
    }
    
    /**
     * Get palette by ID
     */
    public PlanetPalette getPalette(String id) {
        return palettes.get(id);
    }
    
    /**
     * Get all palette IDs
     */
    public Set<String> getPaletteIds() {
        return palettes.keySet();
    }
    
    /**
     * Get total number of palettes
     */
    public int getPaletteCount() {
        return palettes.size();
    }
}
