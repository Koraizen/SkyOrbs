package com.skyorbs.palettes;

import org.bukkit.Material;
import java.util.*;

/**
 * Registry for planet palettes - enables 2000+ diverse planet combinations
 * Combines with shapes, modifiers, and atmospheres for massive variety
 */
public class PaletteRegistry {
    
    private final Map<String, PlanetPalette> palettes = new HashMap<>();
    private final List<PlanetPalette> weightedList = new ArrayList<>();
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
            new Material[]{Material.DEEPSLATE, Material.BLACKSTONE, Material.MAGMA_BLOCK},
            new Material[]{Material.MAGMA_BLOCK, Material.LAVA},
            10.0));
        
        // Crystalline (shiny, precious)
        register(new PlanetPalette("crystalline", "Kristal",
            new Material[]{Material.AMETHYST_BLOCK, Material.QUARTZ_BLOCK, Material.PURPUR_BLOCK},
            new Material[]{Material.QUARTZ_BLOCK, Material.CALCITE, Material.DRIPSTONE_BLOCK},
            new Material[]{Material.DIAMOND_BLOCK, Material.EMERALD_BLOCK, Material.AMETHYST_BLOCK},
            new Material[]{Material.GLOWSTONE, Material.SEA_LANTERN},
            8.0));
        
        // Fungal (mushroom, organic)
        register(new PlanetPalette("fungal", "Mantarlı",
            new Material[]{Material.MYCELIUM, Material.BROWN_MUSHROOM_BLOCK, Material.RED_MUSHROOM_BLOCK},
            new Material[]{Material.DIRT, Material.PODZOL, Material.MYCELIUM},
            new Material[]{Material.STONE, Material.DIRT, Material.COARSE_DIRT},
            new Material[]{Material.MUSHROOM_STEM, Material.SHROOMLIGHT},
            9.0));
        
        // Coral (aquatic, vibrant)
        register(new PlanetPalette("coral", "Mercan",
            new Material[]{Material.BRAIN_CORAL_BLOCK, Material.TUBE_CORAL_BLOCK, Material.BUBBLE_CORAL_BLOCK},
            new Material[]{Material.HORN_CORAL_BLOCK, Material.FIRE_CORAL_BLOCK, Material.PRISMARINE},
            new Material[]{Material.PRISMARINE, Material.DARK_PRISMARINE, Material.STONE},
            new Material[]{Material.SEA_LANTERN, Material.PRISMARINE_BRICKS},
            7.0));
        
        // Toxic (poisonous, dangerous)
        register(new PlanetPalette("toxic", "Zehirli",
            new Material[]{Material.GREEN_CONCRETE, Material.LIME_CONCRETE, Material.SLIME_BLOCK},
            new Material[]{Material.GREEN_TERRACOTTA, Material.LIME_TERRACOTTA, Material.MOSS_BLOCK},
            new Material[]{Material.STONE, Material.DEEPSLATE, Material.SCULK},
            new Material[]{Material.SLIME_BLOCK, Material.HONEY_BLOCK},
            6.0));
        
        // Frozen (ice, snow)
        register(new PlanetPalette("frozen", "Donmuş",
            new Material[]{Material.SNOW_BLOCK, Material.PACKED_ICE, Material.ICE},
            new Material[]{Material.PACKED_ICE, Material.BLUE_ICE, Material.ICE},
            new Material[]{Material.BLUE_ICE, Material.PACKED_ICE, Material.STONE},
            new Material[]{Material.FROSTED_ICE, Material.POWDER_SNOW},
            8.0));
        
        // Volcanic (lava, heat)
        register(new PlanetPalette("volcanic", "Volkanik",
            new Material[]{Material.MAGMA_BLOCK, Material.NETHERRACK, Material.BLACKSTONE},
            new Material[]{Material.NETHERRACK, Material.BASALT, Material.BLACKSTONE},
            new Material[]{Material.LAVA, Material.MAGMA_BLOCK, Material.NETHERRACK},
            new Material[]{Material.FIRE, Material.LAVA},
            7.0));
        
        // Verdant (lush, green)
        register(new PlanetPalette("verdant", "Yeşil",
            new Material[]{Material.GRASS_BLOCK, Material.MOSS_BLOCK, Material.FLOWERING_AZALEA_LEAVES},
            new Material[]{Material.DIRT, Material.ROOTED_DIRT, Material.MOSS_BLOCK},
            new Material[]{Material.STONE, Material.DIRT, Material.CLAY},
            new Material[]{Material.AZALEA, Material.FLOWERING_AZALEA},
            10.0));
        
        // Desert (sandy, dry)
        register(new PlanetPalette("desert", "Çöl",
            new Material[]{Material.SAND, Material.RED_SAND, Material.SANDSTONE},
            new Material[]{Material.SANDSTONE, Material.RED_SANDSTONE, Material.SAND},
            new Material[]{Material.STONE, Material.SANDSTONE, Material.TERRACOTTA},
            new Material[]{Material.DEAD_BUSH, Material.CACTUS},
            9.0));
        
        // Metallic (iron, copper)
        register(new PlanetPalette("metallic", "Metalik",
            new Material[]{Material.IRON_BLOCK, Material.COPPER_BLOCK, Material.WEATHERED_COPPER},
            new Material[]{Material.IRON_BLOCK, Material.EXPOSED_COPPER, Material.OXIDIZED_COPPER},
            new Material[]{Material.IRON_BLOCK, Material.GOLD_BLOCK, Material.NETHERITE_BLOCK},
            new Material[]{Material.IRON_BARS, Material.CHAIN},
            5.0));
        
        // Void (dark, mysterious)
        register(new PlanetPalette("void", "Boşluk",
            new Material[]{Material.END_STONE, Material.OBSIDIAN, Material.CRYING_OBSIDIAN},
            new Material[]{Material.OBSIDIAN, Material.END_STONE, Material.BLACKSTONE},
            new Material[]{Material.OBSIDIAN, Material.CRYING_OBSIDIAN, Material.NETHERITE_BLOCK},
            new Material[]{Material.ENDER_CHEST, Material.END_ROD},
            4.0));
        
        // Luminous (glowing, bright)
        register(new PlanetPalette("luminous", "Parlak",
            new Material[]{Material.GLOWSTONE, Material.SEA_LANTERN, Material.SHROOMLIGHT},
            new Material[]{Material.GLOWSTONE, Material.OCHRE_FROGLIGHT, Material.VERDANT_FROGLIGHT},
            new Material[]{Material.GLOWSTONE, Material.PEARLESCENT_FROGLIGHT, Material.QUARTZ_BLOCK},
            new Material[]{Material.TORCH, Material.LANTERN},
            5.0));
        
        // Warped (nether, alien)
        register(new PlanetPalette("warped", "Çarpık",
            new Material[]{Material.WARPED_NYLIUM, Material.WARPED_WART_BLOCK, Material.WARPED_STEM},
            new Material[]{Material.WARPED_NYLIUM, Material.NETHERRACK, Material.SOUL_SOIL},
            new Material[]{Material.NETHERRACK, Material.SOUL_SOIL, Material.BLACKSTONE},
            new Material[]{Material.WARPED_ROOTS, Material.WARPED_FUNGUS},
            6.0));
        
        // Crimson (nether, red)
        register(new PlanetPalette("crimson", "Kızıl",
            new Material[]{Material.CRIMSON_NYLIUM, Material.CRIMSON_STEM, Material.NETHER_WART_BLOCK},
            new Material[]{Material.CRIMSON_NYLIUM, Material.NETHERRACK, Material.SOUL_SAND},
            new Material[]{Material.NETHERRACK, Material.SOUL_SAND, Material.BLACKSTONE},
            new Material[]{Material.CRIMSON_ROOTS, Material.CRIMSON_FUNGUS},
            6.0));
        
        // Sandstone (ancient, carved)
        register(new PlanetPalette("sandstone", "Kumtaşı",
            new Material[]{Material.SANDSTONE, Material.SMOOTH_SANDSTONE, Material.CHISELED_SANDSTONE},
            new Material[]{Material.SANDSTONE, Material.CUT_SANDSTONE, Material.SAND},
            new Material[]{Material.STONE, Material.SANDSTONE, Material.TERRACOTTA},
            new Material[]{Material.SANDSTONE_STAIRS, Material.SANDSTONE_SLAB},
            7.0));
        
        // Terracotta (colorful, layered)
        register(new PlanetPalette("terracotta", "Kil",
            new Material[]{Material.ORANGE_TERRACOTTA, Material.RED_TERRACOTTA, Material.YELLOW_TERRACOTTA},
            new Material[]{Material.BROWN_TERRACOTTA, Material.WHITE_TERRACOTTA, Material.LIGHT_GRAY_TERRACOTTA},
            new Material[]{Material.TERRACOTTA, Material.CLAY, Material.STONE},
            new Material[]{Material.FLOWER_POT, Material.BRICK},
            8.0));
        
        // Concrete (modern, smooth)
        register(new PlanetPalette("concrete", "Beton",
            new Material[]{Material.LIGHT_GRAY_CONCRETE, Material.GRAY_CONCRETE, Material.WHITE_CONCRETE},
            new Material[]{Material.GRAY_CONCRETE, Material.STONE, Material.ANDESITE},
            new Material[]{Material.STONE, Material.DEEPSLATE, Material.BEDROCK},
            new Material[]{Material.CONCRETE_POWDER, Material.GRAVEL},
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
            new Material[]{Material.DEEPSLATE, Material.REINFORCED_DEEPSLATE, Material.SCULK_SHRIEKER},
            new Material[]{Material.SCULK_SENSOR, Material.SCULK_CATALYST},
            3.0));
        
        // Mud (swamp, wet)
        register(new PlanetPalette("mud", "Çamur",
            new Material[]{Material.MUD, Material.MUDDY_MANGROVE_ROOTS, Material.MUD_BRICKS},
            new Material[]{Material.MUD, Material.CLAY, Material.PACKED_MUD},
            new Material[]{Material.CLAY, Material.DIRT, Material.STONE},
            new Material[]{Material.MANGROVE_ROOTS, Material.MOSS_CARPET},
            7.0));
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
