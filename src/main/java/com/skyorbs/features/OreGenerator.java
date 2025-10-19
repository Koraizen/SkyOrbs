package com.skyorbs.features;

import com.skyorbs.SkyOrbs;
import com.skyorbs.core.Orb;
import com.skyorbs.core.PlanetType;
import com.skyorbs.biomes.BiomeType;
import com.skyorbs.utils.NoiseGenerator;
import org.bukkit.Material;

import java.util.*;

/**
 * FIXED ORE GENERATOR
 * - Integrates with planet generation (part of structure)
 * - Uses PlanetType for ore profiles
 * - Maps BiomeType to PlanetType correctly
 * - No ores in hollow planets
 */
public class OreGenerator {

    private static final SkyOrbs plugin = SkyOrbs.getInstance();

    // ========================================
    // ORE CONFIG CACHE - PERFORMANCE OPTIMIZATION
    // ========================================
    private static final Map<String, Map<String, Object>> oreConfigCache = new HashMap<>();
    private static final Map<String, List<OreEntry>> filteredOreCache = new HashMap<>();

    // ========================================
    // STONE VARIANTS - ÇEŞİTLİ TAŞ BLOKLARI
    // ========================================
    private static final Material[] STONE_VARIANTS = {
        Material.STONE,
        Material.COBBLESTONE,
        Material.MOSSY_COBBLESTONE,
        Material.STONE_BRICKS,
        Material.CRACKED_STONE_BRICKS,
        Material.MOSSY_STONE_BRICKS,
        Material.ANDESITE,
        Material.POLISHED_ANDESITE,
        Material.DIORITE,
        Material.POLISHED_DIORITE,
        Material.GRANITE,
        Material.POLISHED_GRANITE,
        Material.SANDSTONE,
        Material.CUT_SANDSTONE,
        Material.RED_SANDSTONE,
        Material.CUT_RED_SANDSTONE,
        Material.PRISMARINE,
        Material.PRISMARINE_BRICKS,
        Material.DARK_PRISMARINE,
        Material.NETHERRACK,
        Material.BLACKSTONE,
        Material.POLISHED_BLACKSTONE,
        Material.POLISHED_BLACKSTONE_BRICKS,
        Material.END_STONE,
        Material.END_STONE_BRICKS,
        Material.PURPUR_BLOCK,
        Material.PURPUR_PILLAR,
        Material.QUARTZ_BLOCK,
        Material.QUARTZ_BRICKS,
        Material.QUARTZ_PILLAR,
        Material.SMOOTH_QUARTZ,
        Material.BRICKS,
        Material.NETHER_BRICKS,
        Material.RED_NETHER_BRICKS,
        Material.BASALT,
        Material.POLISHED_BASALT,
        Material.SMOOTH_BASALT
    };
    
    // ========================================
    // ORE ENTRY CLASS FOR BATCH PROBABILITY
    // ========================================
    private static class OreEntry {
        final String oreName;
        final double chance;
        final double cumulativeChance;

        OreEntry(String oreName, double chance, double cumulativeChance) {
            this.oreName = oreName;
            this.chance = chance;
            this.cumulativeChance = cumulativeChance;
        }
    }

    // ========================================
    // BIOME TO PLANET TYPE MAPPING
    // ========================================
    private static final Map<BiomeType, PlanetType> BIOME_TO_PLANET_TYPE = new HashMap<>();
    
    static {
        // TERRESTRIAL (Earth-like)
        BIOME_TO_PLANET_TYPE.put(BiomeType.FOREST, PlanetType.TERRESTRIAL);
        BIOME_TO_PLANET_TYPE.put(BiomeType.PLAINS, PlanetType.TERRESTRIAL);
        BIOME_TO_PLANET_TYPE.put(BiomeType.JUNGLE, PlanetType.TERRESTRIAL);
        BIOME_TO_PLANET_TYPE.put(BiomeType.SAVANNA, PlanetType.TERRESTRIAL);
        BIOME_TO_PLANET_TYPE.put(BiomeType.TAIGA, PlanetType.TERRESTRIAL);
        BIOME_TO_PLANET_TYPE.put(BiomeType.MEADOW, PlanetType.TERRESTRIAL);
        
        // DESERT (treated as terrestrial with different ore profile)
        BIOME_TO_PLANET_TYPE.put(BiomeType.DESERT, PlanetType.TERRESTRIAL);
        BIOME_TO_PLANET_TYPE.put(BiomeType.BADLANDS, PlanetType.TERRESTRIAL);
        BIOME_TO_PLANET_TYPE.put(BiomeType.CANYON, PlanetType.TERRESTRIAL);
        
        // ICE
        BIOME_TO_PLANET_TYPE.put(BiomeType.FROZEN_TUNDRA, PlanetType.ICE);
        BIOME_TO_PLANET_TYPE.put(BiomeType.ICE_SPIKES, PlanetType.ICE);
        BIOME_TO_PLANET_TYPE.put(BiomeType.GLACIER, PlanetType.ICE);
        
        // LAVA
        BIOME_TO_PLANET_TYPE.put(BiomeType.LAVA_OCEAN, PlanetType.LAVA);
        BIOME_TO_PLANET_TYPE.put(BiomeType.OBSIDIAN_PLAINS, PlanetType.LAVA);
        BIOME_TO_PLANET_TYPE.put(BiomeType.MAGMA_CAVES, PlanetType.LAVA);
        BIOME_TO_PLANET_TYPE.put(BiomeType.BASALTIC, PlanetType.LAVA);
        
        // CRYSTAL
        BIOME_TO_PLANET_TYPE.put(BiomeType.CRYSTAL_FOREST, PlanetType.CRYSTAL);
        BIOME_TO_PLANET_TYPE.put(BiomeType.CRYSTALLINE, PlanetType.CRYSTAL);
        
        // SHADOW/VOID
        BIOME_TO_PLANET_TYPE.put(BiomeType.VOID, PlanetType.SHADOW);
        BIOME_TO_PLANET_TYPE.put(BiomeType.CHORUS_LAND, PlanetType.SHADOW);
        
        // TOXIC
        BIOME_TO_PLANET_TYPE.put(BiomeType.CORRUPTED, PlanetType.TOXIC);
        BIOME_TO_PLANET_TYPE.put(BiomeType.TOXIC_SWAMP, PlanetType.TOXIC);
        BIOME_TO_PLANET_TYPE.put(BiomeType.TOXIC, PlanetType.TOXIC);
        
        // GAS (no solid surface, special handling)
        BIOME_TO_PLANET_TYPE.put(BiomeType.MUSHROOM_GIANT, PlanetType.GAS);
        BIOME_TO_PLANET_TYPE.put(BiomeType.GLOWSTONE_CAVERN, PlanetType.CRYSTAL);
        
        // Additional biomes - map to closest type
        BIOME_TO_PLANET_TYPE.put(BiomeType.FUNGAL, PlanetType.TERRESTRIAL);
        BIOME_TO_PLANET_TYPE.put(BiomeType.CORAL, PlanetType.TERRESTRIAL);
        BIOME_TO_PLANET_TYPE.put(BiomeType.AURORA, PlanetType.ICE);
        BIOME_TO_PLANET_TYPE.put(BiomeType.STORMY, PlanetType.TERRESTRIAL);
        BIOME_TO_PLANET_TYPE.put(BiomeType.FOGGY, PlanetType.TERRESTRIAL);
        BIOME_TO_PLANET_TYPE.put(BiomeType.CORROSIVE, PlanetType.TOXIC);
        BIOME_TO_PLANET_TYPE.put(BiomeType.LUMINOUS, PlanetType.CRYSTAL);
    }
    
    /**
     * Get PlanetType from BiomeType
     */
    public static PlanetType getPlanetTypeFromBiome(BiomeType biome) {
        return BIOME_TO_PLANET_TYPE.getOrDefault(biome, PlanetType.TERRESTRIAL);
    }
    
    /**
     * Try to generate ore at this position during planet generation
     * OPTIMIZED: Uses cached configs, depth filtering, and batch probability
     * ENHANCED: Includes stone variants for more variety
     * REALISTIC: Very low ore spawn rates with noise-based distribution
     */
    public static Material tryGenerateOre(int x, int y, int z, double distanceFromCenter,
                                            int radius, BiomeType biome, Random random) {

        // OPTIMIZATION 1: Early exit for surface blocks
        if (distanceFromCenter > radius - 2) return null;

        // Get planet type from biome
        PlanetType planetType = getPlanetTypeFromBiome(biome);
        String configKey = getConfigKeyForPlanetType(planetType, biome);

        // OPTIMIZATION 4: Cache ore configs per planet type
        Map<String, Object> config = oreConfigCache.computeIfAbsent(configKey, key ->
            plugin.getConfigManager().getOreConfigForPlanetType(key));

        // Check if ore generation is enabled for this type
        boolean enabled = (Boolean) config.getOrDefault("enabled", true);
        if (!enabled) return null;

        double densityMultiplier = ((Number) config.getOrDefault("densityMultiplier", 0.000005)).doubleValue();

        // NOISE-BASED DISTRIBUTION: Use 3D noise for realistic ore veins
        double veinNoiseScale = ((Number) config.getOrDefault("veinNoiseScale", 0.02)).doubleValue();
        double veinThreshold = ((Number) config.getOrDefault("veinThreshold", 0.7)).doubleValue();

        // Generate noise value for this position (deterministic based on coordinates)
        double noiseValue = (NoiseGenerator.simplexNoise3D(x * veinNoiseScale, y * veinNoiseScale, z * veinNoiseScale) + 1) / 2; // Normalize to 0-1
        if (noiseValue < veinThreshold) return null; // Not in a vein

        // STONE VARIANTS: Chance to return stone variants instead of ores (PLANET TYPE BASED)
        boolean stoneVariantsEnabled = (Boolean) config.getOrDefault("stoneVariants", true);
        if (stoneVariantsEnabled && random.nextDouble() < 0.7) { // %70 taş variantı - dengeli dağılım
            return getStoneVariant(biome, random);
        }

        @SuppressWarnings("unchecked")
        var oresConfig = (Map<String, Map<String, Object>>) config.get("ores");
        if (oresConfig == null || oresConfig.isEmpty()) return null;

        // CORE RARITY SYSTEM: Ores become rarer and more valuable closer to center
        double depthRatio = distanceFromCenter / radius; // 0 = center, 1 = surface
        double coreMultiplier = calculateCoreMultiplier(depthRatio);

        // OPTIMIZATION 2: Depth-based filtering - cache filtered ore lists
        String cacheKey = configKey + "_" + (int)(depthRatio * 10); // Group by depth ranges
        List<OreEntry> filteredOres = filteredOreCache.computeIfAbsent(cacheKey, key -> {
            List<OreEntry> ores = new ArrayList<>();
            double cumulative = 0.0;

            for (var entry : oresConfig.entrySet()) {
                String oreName = entry.getKey();
                var oreData = entry.getValue();

                boolean oreEnabled = (Boolean) oreData.getOrDefault("enabled", true);
                if (!oreEnabled) continue;

                // OPTIMIZATION 2: Skip ores that don't spawn at this depth
                if (!canOreSpawnAtDepth(oreName, depthRatio)) continue;

                double baseChance = ((Number) oreData.getOrDefault("chance", 0.0)).doubleValue();
                double chance = baseChance * densityMultiplier * coreMultiplier;

                // Rare ores more common in core
                if (isRareOre(oreName) && depthRatio < 0.2) {
                    chance *= 2.0;
                }

                if (chance > 0) {
                    cumulative += chance;
                    ores.add(new OreEntry(oreName, chance, cumulative));
                }
            }
            return ores;
        });

        if (filteredOres.isEmpty()) return null;

        // OPTIMIZATION 3: Batch probability - single roll for ore category
        double totalChance = filteredOres.get(filteredOres.size() - 1).cumulativeChance;
        double roll = random.nextDouble() * totalChance;

        // Binary search for the selected ore
        int low = 0, high = filteredOres.size() - 1;
        while (low <= high) {
            int mid = (low + high) / 2;
            if (roll <= filteredOres.get(mid).cumulativeChance) {
                if (mid == 0 || roll > filteredOres.get(mid - 1).cumulativeChance) {
                    return getOreMaterial(filteredOres.get(mid).oreName, y, radius);
                }
                high = mid - 1;
            } else {
                low = mid + 1;
            }
        }

        return null;
    }
    
    /**
     * Calculate core multiplier based on depth - OVERWORLD STYLE
     * Similar to Minecraft's natural ore distribution
     */
    private static double calculateCoreMultiplier(double depthRatio) {
        if (depthRatio < 0.1) {
            // Very deep core - rare ores
            return 0.5; // Low chance
        } else if (depthRatio < 0.3) {
            // Deep areas - valuable ores
            return 0.8; // Moderate chance
        } else if (depthRatio < 0.6) {
            // Mid-depth - balanced
            return 1.2; // Good chance
        } else {
            // Near surface - common ores
            return 1.5; // High chance
        }
    }
    
    /**
     * Get config key for planet type - PLANET TYPE BASED: Use planet type with biome variations
     */
    private static String getConfigKeyForPlanetType(PlanetType planetType, BiomeType biome) {
        // Special handling for specific biomes with unique ore profiles
        if (biome == BiomeType.CRYSTAL_FOREST || biome == BiomeType.CRYSTALLINE) {
            return biome.name().toLowerCase();
        }

        if (biome == BiomeType.LAVA_OCEAN || biome == BiomeType.MAGMA_CAVES) {
            return biome.name().toLowerCase();
        }

        if (biome == BiomeType.FROZEN_TUNDRA || biome == BiomeType.GLACIER) {
            return biome.name().toLowerCase();
        }

        // Fallback to planet type
        return planetType.name().toLowerCase();
    }
    
    /**
     * Check if ore is rare/valuable
     */
    private static boolean isRareOre(String oreName) {
        return switch (oreName.toUpperCase()) {
            case "DIAMOND", "EMERALD", "ANCIENT_DEBRIS", "NETHERITE", "AMETHYST" -> true;
            default -> false;
        };
    }

    /**
     * OVERWORLD STYLE DEPTH DISTRIBUTION: Similar to Minecraft's ore levels
     */
    private static boolean canOreSpawnAtDepth(String oreName, double depthRatio) {
        return switch (oreName.toUpperCase()) {
            // Surface ores (near surface - most common)
            case "COAL", "IRON", "COPPER" -> depthRatio > 0.3; // Near surface
            // Mid-depth ores (balanced)
            case "GOLD", "REDSTONE", "LAPIS" -> depthRatio > 0.1 && depthRatio < 0.7;
            // Deep ores (rare and valuable)
            case "DIAMOND", "EMERALD" -> depthRatio < 0.5; // Deeper areas
            // Very deep/core ores (extremely rare)
            case "ANCIENT_DEBRIS", "NETHERITE" -> depthRatio < 0.3; // Very deep
            // Nether ores (special biomes only)
            case "NETHER_QUARTZ", "NETHER_GOLD" -> true; // Always available in nether biomes
            // Special ores (biome-specific)
            case "GLOWSTONE", "OBSIDIAN", "SLIME", "PRISMARINE", "NETHERRACK", "AMETHYST" -> true;
            default -> true;
        };
    }

    /**
     * Get a stone variant based on biome type - PLANET TYPE BASED
     */
    private static Material getStoneVariant(BiomeType biome, Random random) {
        // Planet type based stone variants - aesthetic and thematic
        Material[] biomeStones = switch (biome) {
            case LAVA_OCEAN, MAGMA_CAVES, BASALTIC -> new Material[]{
                Material.NETHERRACK, Material.BLACKSTONE, Material.BASALT, Material.POLISHED_BASALT,
                Material.MAGMA_BLOCK, Material.NETHER_BRICKS, Material.RED_NETHER_BRICKS
            };
            case CRYSTAL_FOREST, CRYSTALLINE -> new Material[]{
                Material.QUARTZ_BLOCK, Material.QUARTZ_BRICKS, Material.QUARTZ_PILLAR,
                Material.PURPUR_BLOCK, Material.PURPUR_PILLAR, Material.END_STONE, Material.END_STONE_BRICKS
            };
            case VOID, CHORUS_LAND -> new Material[]{
                Material.END_STONE, Material.END_STONE_BRICKS, Material.PURPUR_BLOCK,
                Material.OBSIDIAN, Material.CRYING_OBSIDIAN
            };
            case DESERT, BADLANDS, CANYON -> new Material[]{
                Material.SANDSTONE, Material.CUT_SANDSTONE, Material.RED_SANDSTONE,
                Material.CUT_RED_SANDSTONE, Material.STONE, Material.COBBLESTONE
            };
            case FROZEN_TUNDRA, GLACIER, ICE_SPIKES -> new Material[]{
                Material.STONE, Material.COBBLESTONE, Material.ANDESITE, Material.POLISHED_ANDESITE,
                Material.DIORITE, Material.POLISHED_DIORITE, Material.GRANITE, Material.POLISHED_GRANITE
            };
            case TOXIC_SWAMP, TOXIC, CORRUPTED -> new Material[]{
                Material.GREEN_CONCRETE, Material.LIME_CONCRETE, Material.PRISMARINE,
                Material.PRISMARINE_BRICKS, Material.DARK_PRISMARINE, Material.SLIME_BLOCK
            };
            default -> STONE_VARIANTS; // All variants for other biomes
        };

        return biomeStones[random.nextInt(biomeStones.length)];
    }
    
    /**
     * Get ore material (with deepslate variants based on depth)
     */
    private static Material getOreMaterial(String oreName, int y, int planetRadius) {
        // Use deepslate variant if below center
        boolean useDeepslate = y < 0 && plugin.getConfigManager().isDeepslateEnabled();
        
        return switch (oreName.toUpperCase()) {
            case "COAL" -> useDeepslate ? Material.DEEPSLATE_COAL_ORE : Material.COAL_ORE;
            case "IRON" -> useDeepslate ? Material.DEEPSLATE_IRON_ORE : Material.IRON_ORE;
            case "COPPER" -> useDeepslate ? Material.DEEPSLATE_COPPER_ORE : Material.COPPER_ORE;
            case "GOLD" -> useDeepslate ? Material.DEEPSLATE_GOLD_ORE : Material.GOLD_ORE;
            case "REDSTONE" -> useDeepslate ? Material.DEEPSLATE_REDSTONE_ORE : Material.REDSTONE_ORE;
            case "LAPIS" -> useDeepslate ? Material.DEEPSLATE_LAPIS_ORE : Material.LAPIS_ORE;
            case "DIAMOND" -> useDeepslate ? Material.DEEPSLATE_DIAMOND_ORE : Material.DIAMOND_ORE;
            case "EMERALD" -> useDeepslate ? Material.DEEPSLATE_EMERALD_ORE : Material.EMERALD_ORE;
            case "ANCIENT_DEBRIS" -> Material.ANCIENT_DEBRIS;
            case "NETHER_QUARTZ" -> Material.NETHER_QUARTZ_ORE;
            case "NETHER_GOLD" -> Material.NETHER_GOLD_ORE;
            case "GLOWSTONE" -> Material.GLOWSTONE;
            case "OBSIDIAN" -> Material.OBSIDIAN;
            case "SLIME" -> Material.SLIME_BLOCK;
            case "AMETHYST" -> Material.AMETHYST_BLOCK;
            case "PRISMARINE" -> Material.PRISMARINE_CRYSTALS;
            case "NETHERRACK" -> Material.NETHERRACK;
            default -> null;
        };
    }
    
    /**
     * Data class for block placement
     */
    public static class BlockData {
        public final int x, y, z;
        public final Material material;
        
        public BlockData(int x, int y, int z, Material material) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.material = material;
        }
    }
}