// src/main/java/com/skyorbs/features/OreProfile.java
package com.skyorbs.features;

import com.skyorbs.biomes.BiomeType;
import org.bukkit.Material;
import java.util.*;

/**
 * Biome-specific ore generation profiles
 * Each biome has unique ore composition
 */
public class OreProfile {
    
    private final BiomeType biome;
    private final List<OreConfig> ores;
    private final String description;
    
    public OreProfile(BiomeType biome, String description) {
        this.biome = biome;
        this.description = description;
        this.ores = new ArrayList<>();
    }
    
    public OreProfile addOre(Material type, Material deepslateVariant, 
                            double chance, int minVein, int maxVein) {
        ores.add(new OreConfig(type, deepslateVariant, chance, new int[]{minVein, maxVein}));
        return this;
    }
    
    public List<OreConfig> getOres() {
        return ores;
    }
    
    public String getDescription() {
        return description;
    }
    
    public BiomeType getBiome() {
        return biome;
    }
    
    public boolean hasOres() {
        return !ores.isEmpty();
    }
    
    /**
     * Get all biome-specific ore profiles
     */
    public static Map<BiomeType, OreProfile> getAllProfiles() {
        Map<BiomeType, OreProfile> profiles = new HashMap<>();
        
        // ============================================
        // EARTH-LIKE BIOMES - Full ore set
        // ============================================
        
        // FOREST - Balanced, all vanilla ores
        profiles.put(BiomeType.FOREST, new OreProfile(BiomeType.FOREST, "Dengeli dünya benzeri - Tüm madenler")
            .addOre(Material.COAL_ORE, Material.DEEPSLATE_COAL_ORE, 0.35, 3, 8)
            .addOre(Material.IRON_ORE, Material.DEEPSLATE_IRON_ORE, 0.30, 2, 6)
            .addOre(Material.COPPER_ORE, Material.DEEPSLATE_COPPER_ORE, 0.25, 3, 9)
            .addOre(Material.GOLD_ORE, Material.DEEPSLATE_GOLD_ORE, 0.12, 2, 5)
            .addOre(Material.REDSTONE_ORE, Material.DEEPSLATE_REDSTONE_ORE, 0.10, 3, 7)
            .addOre(Material.LAPIS_ORE, Material.DEEPSLATE_LAPIS_ORE, 0.08, 2, 6)
            .addOre(Material.DIAMOND_ORE, Material.DEEPSLATE_DIAMOND_ORE, 0.05, 1, 4)
            .addOre(Material.EMERALD_ORE, Material.DEEPSLATE_EMERALD_ORE, 0.02, 1, 2)
        );
        
        // PLAINS - Similar to forest
        profiles.put(BiomeType.PLAINS, profiles.get(BiomeType.FOREST));
        
        // JUNGLE - More emeralds
        profiles.put(BiomeType.JUNGLE, new OreProfile(BiomeType.JUNGLE, "Orman - Daha fazla zümrüt")
            .addOre(Material.COAL_ORE, Material.DEEPSLATE_COAL_ORE, 0.35, 3, 8)
            .addOre(Material.IRON_ORE, Material.DEEPSLATE_IRON_ORE, 0.30, 2, 6)
            .addOre(Material.COPPER_ORE, Material.DEEPSLATE_COPPER_ORE, 0.25, 3, 9)
            .addOre(Material.GOLD_ORE, Material.DEEPSLATE_GOLD_ORE, 0.12, 2, 5)
            .addOre(Material.REDSTONE_ORE, Material.DEEPSLATE_REDSTONE_ORE, 0.10, 3, 7)
            .addOre(Material.LAPIS_ORE, Material.DEEPSLATE_LAPIS_ORE, 0.08, 2, 6)
            .addOre(Material.DIAMOND_ORE, Material.DEEPSLATE_DIAMOND_ORE, 0.04, 1, 3)
            .addOre(Material.EMERALD_ORE, Material.DEEPSLATE_EMERALD_ORE, 0.08, 1, 3) // 4x more emeralds!
        );
        
        // SAVANNA - More gold and copper
        profiles.put(BiomeType.SAVANNA, new OreProfile(BiomeType.SAVANNA, "Savan - Altın ve bakır zengin")
            .addOre(Material.COAL_ORE, Material.DEEPSLATE_COAL_ORE, 0.30, 3, 7)
            .addOre(Material.IRON_ORE, Material.DEEPSLATE_IRON_ORE, 0.25, 2, 5)
            .addOre(Material.COPPER_ORE, Material.DEEPSLATE_COPPER_ORE, 0.35, 4, 10) // More copper!
            .addOre(Material.GOLD_ORE, Material.DEEPSLATE_GOLD_ORE, 0.20, 3, 7) // More gold!
            .addOre(Material.REDSTONE_ORE, Material.DEEPSLATE_REDSTONE_ORE, 0.08, 2, 5)
            .addOre(Material.LAPIS_ORE, Material.DEEPSLATE_LAPIS_ORE, 0.06, 2, 4)
            .addOre(Material.DIAMOND_ORE, Material.DEEPSLATE_DIAMOND_ORE, 0.04, 1, 3)
        );
        
        // TAIGA - Balanced
        profiles.put(BiomeType.TAIGA, profiles.get(BiomeType.FOREST));
        
        // MEADOW - Balanced
        profiles.put(BiomeType.MEADOW, profiles.get(BiomeType.FOREST));
        
        // ============================================
        // DESERT BIOMES - Less coal, more gold/copper
        // ============================================
        
        // DESERT
        profiles.put(BiomeType.DESERT, new OreProfile(BiomeType.DESERT, "Çöl - Altın ve bakır, az kömür")
            .addOre(Material.COAL_ORE, Material.DEEPSLATE_COAL_ORE, 0.15, 2, 4) // Less coal
            .addOre(Material.IRON_ORE, Material.DEEPSLATE_IRON_ORE, 0.25, 2, 5)
            .addOre(Material.COPPER_ORE, Material.DEEPSLATE_COPPER_ORE, 0.40, 5, 12) // Lots of copper!
            .addOre(Material.GOLD_ORE, Material.DEEPSLATE_GOLD_ORE, 0.25, 3, 8) // Lots of gold!
            .addOre(Material.REDSTONE_ORE, Material.DEEPSLATE_REDSTONE_ORE, 0.12, 3, 6)
            .addOre(Material.LAPIS_ORE, Material.DEEPSLATE_LAPIS_ORE, 0.10, 3, 7)
            .addOre(Material.DIAMOND_ORE, Material.DEEPSLATE_DIAMOND_ORE, 0.04, 1, 3)
        );
        
        // BADLANDS - Gold paradise
        profiles.put(BiomeType.BADLANDS, new OreProfile(BiomeType.BADLANDS, "Kötü Toprak - Altın cenneti")
            .addOre(Material.COAL_ORE, Material.DEEPSLATE_COAL_ORE, 0.20, 2, 5)
            .addOre(Material.IRON_ORE, Material.DEEPSLATE_IRON_ORE, 0.30, 2, 6)
            .addOre(Material.COPPER_ORE, Material.DEEPSLATE_COPPER_ORE, 0.35, 4, 10)
            .addOre(Material.GOLD_ORE, Material.DEEPSLATE_GOLD_ORE, 0.40, 4, 12) // GOLD EVERYWHERE!
            .addOre(Material.REDSTONE_ORE, Material.DEEPSLATE_REDSTONE_ORE, 0.15, 3, 7)
            .addOre(Material.DIAMOND_ORE, Material.DEEPSLATE_DIAMOND_ORE, 0.05, 1, 3)
        );
        
        // CANYON - Similar to badlands
        profiles.put(BiomeType.CANYON, profiles.get(BiomeType.BADLANDS));
        
        // ============================================
        // ICE BIOMES - Diamonds and lapis
        // ============================================
        
        // FROZEN_TUNDRA
        profiles.put(BiomeType.FROZEN_TUNDRA, new OreProfile(BiomeType.FROZEN_TUNDRA, "Donmuş Tundra - Elmas ve lapis")
            .addOre(Material.COAL_ORE, Material.DEEPSLATE_COAL_ORE, 0.30, 3, 7)
            .addOre(Material.IRON_ORE, Material.DEEPSLATE_IRON_ORE, 0.35, 3, 7) // More iron
            .addOre(Material.COPPER_ORE, Material.DEEPSLATE_COPPER_ORE, 0.20, 2, 6)
            .addOre(Material.LAPIS_ORE, Material.DEEPSLATE_LAPIS_ORE, 0.20, 4, 10) // Lots of lapis!
            .addOre(Material.DIAMOND_ORE, Material.DEEPSLATE_DIAMOND_ORE, 0.10, 2, 5) // More diamonds!
            .addOre(Material.EMERALD_ORE, Material.DEEPSLATE_EMERALD_ORE, 0.03, 1, 2)
        );
        
        // ICE_SPIKES - Similar
        profiles.put(BiomeType.ICE_SPIKES, profiles.get(BiomeType.FROZEN_TUNDRA));
        
        // GLACIER - Similar
        profiles.put(BiomeType.GLACIER, profiles.get(BiomeType.FROZEN_TUNDRA));
        
        // ============================================
        // VOLCANIC BIOMES - Ancient debris and gold
        // ============================================
        
        // LAVA_OCEAN - Nether-like
        profiles.put(BiomeType.LAVA_OCEAN, new OreProfile(BiomeType.LAVA_OCEAN, "Lav Okyanusu - Ancient debris ve altın")
            .addOre(Material.GOLD_ORE, Material.DEEPSLATE_GOLD_ORE, 0.30, 3, 8) // Lots of gold
            .addOre(Material.ANCIENT_DEBRIS, null, 0.08, 1, 3) // Ancient debris!
            .addOre(Material.NETHER_QUARTZ_ORE, null, 0.25, 3, 8) // Quartz
            .addOre(Material.NETHER_GOLD_ORE, null, 0.20, 3, 7) // Nether gold
        );
        
        // OBSIDIAN_PLAINS - Very rare ores
        profiles.put(BiomeType.OBSIDIAN_PLAINS, new OreProfile(BiomeType.OBSIDIAN_PLAINS, "Obsidyen - Çok nadir madenler")
            .addOre(Material.GOLD_ORE, Material.DEEPSLATE_GOLD_ORE, 0.15, 2, 4)
            .addOre(Material.ANCIENT_DEBRIS, null, 0.05, 1, 2)
            .addOre(Material.DIAMOND_ORE, Material.DEEPSLATE_DIAMOND_ORE, 0.08, 1, 3)
        );
        
        // MAGMA_CAVES - Similar to lava ocean
        profiles.put(BiomeType.MAGMA_CAVES, profiles.get(BiomeType.LAVA_OCEAN));
        
        // ============================================
        // ALIEN BIOMES - Unique compositions
        // ============================================
        
        // CRYSTAL_FOREST - Diamonds, emeralds, amethyst
        profiles.put(BiomeType.CRYSTAL_FOREST, new OreProfile(BiomeType.CRYSTAL_FOREST, "Kristal Ormanı - Değerli taşlar")
            .addOre(Material.COAL_ORE, Material.DEEPSLATE_COAL_ORE, 0.20, 2, 5)
            .addOre(Material.IRON_ORE, Material.DEEPSLATE_IRON_ORE, 0.15, 2, 4)
            .addOre(Material.LAPIS_ORE, Material.DEEPSLATE_LAPIS_ORE, 0.15, 3, 7)
            .addOre(Material.DIAMOND_ORE, Material.DEEPSLATE_DIAMOND_ORE, 0.15, 2, 6) // Lots of diamonds!
            .addOre(Material.EMERALD_ORE, Material.DEEPSLATE_EMERALD_ORE, 0.12, 2, 5) // Lots of emeralds!
            .addOre(Material.AMETHYST_BLOCK, null, 0.10, 3, 8) // Amethyst clusters!
        );
        
        // VOID - Almost no ores (dead planet)
        profiles.put(BiomeType.VOID, new OreProfile(BiomeType.VOID, "Boşluk - Ölü gezegen, çok az maden")
            .addOre(Material.COAL_ORE, Material.DEEPSLATE_COAL_ORE, 0.05, 1, 2) // Very rare
            .addOre(Material.IRON_ORE, Material.DEEPSLATE_IRON_ORE, 0.03, 1, 2) // Very rare
        );
        
        // CORRUPTED - Exotic ores
        profiles.put(BiomeType.CORRUPTED, new OreProfile(BiomeType.CORRUPTED, "Bozulmuş - Egzotik madenler")
            .addOre(Material.COAL_ORE, Material.DEEPSLATE_COAL_ORE, 0.15, 2, 4)
            .addOre(Material.REDSTONE_ORE, Material.DEEPSLATE_REDSTONE_ORE, 0.25, 4, 10) // Lots of redstone
            .addOre(Material.GOLD_ORE, Material.DEEPSLATE_GOLD_ORE, 0.20, 3, 7)
            .addOre(Material.NETHER_QUARTZ_ORE, null, 0.15, 3, 6)
            .addOre(Material.ANCIENT_DEBRIS, null, 0.04, 1, 2)
        );
        
        // MUSHROOM_GIANT - Minimal ores
        profiles.put(BiomeType.MUSHROOM_GIANT, new OreProfile(BiomeType.MUSHROOM_GIANT, "Dev Mantar - Az maden")
            .addOre(Material.COAL_ORE, Material.DEEPSLATE_COAL_ORE, 0.10, 2, 4)
            .addOre(Material.IRON_ORE, Material.DEEPSLATE_IRON_ORE, 0.08, 1, 3)
            .addOre(Material.COPPER_ORE, Material.DEEPSLATE_COPPER_ORE, 0.12, 2, 5)
        );
        
        // TOXIC_SWAMP - Unusual composition
        profiles.put(BiomeType.TOXIC_SWAMP, new OreProfile(BiomeType.TOXIC_SWAMP, "Zehirli Bataklık - Tuhaf madenler")
            .addOre(Material.COAL_ORE, Material.DEEPSLATE_COAL_ORE, 0.25, 3, 6)
            .addOre(Material.IRON_ORE, Material.DEEPSLATE_IRON_ORE, 0.20, 2, 5)
            .addOre(Material.COPPER_ORE, Material.DEEPSLATE_COPPER_ORE, 0.30, 4, 9)
            .addOre(Material.GOLD_ORE, Material.DEEPSLATE_GOLD_ORE, 0.10, 2, 4)
            .addOre(Material.REDSTONE_ORE, Material.DEEPSLATE_REDSTONE_ORE, 0.20, 3, 7)
        );
        
        // GLOWSTONE_CAVERN - Glowstone and quartz
        profiles.put(BiomeType.GLOWSTONE_CAVERN, new OreProfile(BiomeType.GLOWSTONE_CAVERN, "Parıltı Taşı Mağarası - Quartz ve glowstone")
            .addOre(Material.COAL_ORE, Material.DEEPSLATE_COAL_ORE, 0.15, 2, 4)
            .addOre(Material.NETHER_QUARTZ_ORE, null, 0.35, 4, 10) // Lots of quartz
            .addOre(Material.GOLD_ORE, Material.DEEPSLATE_GOLD_ORE, 0.20, 3, 6)
            .addOre(Material.GLOWSTONE, null, 0.25, 3, 8) // Glowstone deposits
        );
        
        // CHORUS_LAND - End-like
        profiles.put(BiomeType.CHORUS_LAND, new OreProfile(BiomeType.CHORUS_LAND, "Chorus Diyarı - Son benzeri")
            .addOre(Material.COAL_ORE, Material.DEEPSLATE_COAL_ORE, 0.10, 2, 3)
            .addOre(Material.IRON_ORE, Material.DEEPSLATE_IRON_ORE, 0.08, 1, 3)
            .addOre(Material.DIAMOND_ORE, Material.DEEPSLATE_DIAMOND_ORE, 0.06, 1, 2)
        );
        
        // ============================================
        // ADDITIONAL BIOMES
        // ============================================
        
        // BASALTIC - Volcanic
        profiles.put(BiomeType.BASALTIC, new OreProfile(BiomeType.BASALTIC, "Bazaltik - Volkanik")
            .addOre(Material.COAL_ORE, Material.DEEPSLATE_COAL_ORE, 0.20, 2, 5)
            .addOre(Material.IRON_ORE, Material.DEEPSLATE_IRON_ORE, 0.30, 3, 7)
            .addOre(Material.GOLD_ORE, Material.DEEPSLATE_GOLD_ORE, 0.18, 2, 6)
            .addOre(Material.DIAMOND_ORE, Material.DEEPSLATE_DIAMOND_ORE, 0.08, 1, 3)
        );
        
        // CRYSTALLINE - Like crystal forest
        profiles.put(BiomeType.CRYSTALLINE, profiles.get(BiomeType.CRYSTAL_FOREST));
        
        // FUNGAL - Like mushroom
        profiles.put(BiomeType.FUNGAL, profiles.get(BiomeType.MUSHROOM_GIANT));
        
        // CORAL - Underwater vibes
        profiles.put(BiomeType.CORAL, new OreProfile(BiomeType.CORAL, "Mercan - Su altı")
            .addOre(Material.COAL_ORE, Material.DEEPSLATE_COAL_ORE, 0.15, 2, 4)
            .addOre(Material.IRON_ORE, Material.DEEPSLATE_IRON_ORE, 0.20, 2, 5)
            .addOre(Material.COPPER_ORE, Material.DEEPSLATE_COPPER_ORE, 0.30, 4, 9)
            .addOre(Material.LAPIS_ORE, Material.DEEPSLATE_LAPIS_ORE, 0.25, 4, 8)
            .addOre(Material.PRISMARINE_CRYSTALS, null, 0.20, 3, 7)
        );
        
        // TOXIC - Like toxic swamp
        profiles.put(BiomeType.TOXIC, profiles.get(BiomeType.TOXIC_SWAMP));
        
        // AURORA - Icy magical
        profiles.put(BiomeType.AURORA, new OreProfile(BiomeType.AURORA, "Aurora - Büyülü buz")
            .addOre(Material.COAL_ORE, Material.DEEPSLATE_COAL_ORE, 0.20, 2, 5)
            .addOre(Material.IRON_ORE, Material.DEEPSLATE_IRON_ORE, 0.25, 2, 6)
            .addOre(Material.LAPIS_ORE, Material.DEEPSLATE_LAPIS_ORE, 0.30, 4, 10)
            .addOre(Material.DIAMOND_ORE, Material.DEEPSLATE_DIAMOND_ORE, 0.12, 2, 5)
            .addOre(Material.EMERALD_ORE, Material.DEEPSLATE_EMERALD_ORE, 0.08, 1, 3)
        );
        
        // STORMY - Chaotic
        profiles.put(BiomeType.STORMY, new OreProfile(BiomeType.STORMY, "Fırtınalı - Kaotik")
            .addOre(Material.COAL_ORE, Material.DEEPSLATE_COAL_ORE, 0.25, 3, 6)
            .addOre(Material.IRON_ORE, Material.DEEPSLATE_IRON_ORE, 0.28, 3, 7)
            .addOre(Material.COPPER_ORE, Material.DEEPSLATE_COPPER_ORE, 0.25, 3, 8)
            .addOre(Material.REDSTONE_ORE, Material.DEEPSLATE_REDSTONE_ORE, 0.20, 3, 7)
            .addOre(Material.GOLD_ORE, Material.DEEPSLATE_GOLD_ORE, 0.10, 2, 4)
        );
        
        // FOGGY - Mysterious
        profiles.put(BiomeType.FOGGY, new OreProfile(BiomeType.FOGGY, "Sisli - Gizemli")
            .addOre(Material.COAL_ORE, Material.DEEPSLATE_COAL_ORE, 0.20, 2, 5)
            .addOre(Material.IRON_ORE, Material.DEEPSLATE_IRON_ORE, 0.22, 2, 5)
            .addOre(Material.COPPER_ORE, Material.DEEPSLATE_COPPER_ORE, 0.20, 2, 6)
            .addOre(Material.GOLD_ORE, Material.DEEPSLATE_GOLD_ORE, 0.12, 2, 4)
            .addOre(Material.DIAMOND_ORE, Material.DEEPSLATE_DIAMOND_ORE, 0.06, 1, 3)
        );
        
        // CORROSIVE - Harsh
        profiles.put(BiomeType.CORROSIVE, new OreProfile(BiomeType.CORROSIVE, "Aşındırıcı - Sert")
            .addOre(Material.IRON_ORE, Material.DEEPSLATE_IRON_ORE, 0.30, 3, 7)
            .addOre(Material.GOLD_ORE, Material.DEEPSLATE_GOLD_ORE, 0.15, 2, 5)
            .addOre(Material.REDSTONE_ORE, Material.DEEPSLATE_REDSTONE_ORE, 0.25, 3, 8)
            .addOre(Material.ANCIENT_DEBRIS, null, 0.05, 1, 2)
        );
        
        // LUMINOUS - Glowing
        profiles.put(BiomeType.LUMINOUS, profiles.get(BiomeType.GLOWSTONE_CAVERN));
        
        return profiles;
    }
    
    /**
     * Ore configuration
     */
    public static class OreConfig {
        public final Material type;
        public final Material deepslateVariant;
        public final double chance;
        public final int[] veinSize;
        
        public OreConfig(Material type, Material deepslateVariant, double chance, int[] veinSize) {
            this.type = type;
            this.deepslateVariant = deepslateVariant;
            this.chance = chance;
            this.veinSize = veinSize;
        }
    }
}