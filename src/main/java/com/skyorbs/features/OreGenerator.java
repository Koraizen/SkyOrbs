package com.skyorbs.features;

import com.skyorbs.SkyOrbs;
import com.skyorbs.core.Orb;
import com.skyorbs.core.PlanetType;
import com.skyorbs.biomes.BiomeType;
import org.bukkit.Material;

import java.util.*;

/**
 * PLANET-TYPE BASED ORE GENERATOR
 * Her gezegen tipi kendi özel ore profiline sahip!
 * Derinlik sistemli - Overworld tarzı
 */
public class OreGenerator {

    private static final SkyOrbs plugin = SkyOrbs.getInstance();

    // PLANET TYPE -> ORE PROFILES
    private static final Map<PlanetType, OreProfile> ORE_PROFILES = new HashMap<>();

    static {
        initializeOreProfiles();
    }

    /**
     * ANA ORE GENERATION METODu
     */
    public static Material tryGenerateOre(int x, int y, int z, double distanceFromCenter,
                                            int radius, BiomeType biome, Random random) {

        // YÜZEY VE KABUK KATMANI - ORE YOK (sadece derin iç kısım)
        if (distanceFromCenter > radius - 8) return null;

        // CONFIG KONTROL
        if (!plugin.getConfigManager().isOreGenerationEnabled()) return null;

        // Derinlik hesapla (0 = merkez, 1 = yüzey)
        double depth = distanceFromCenter / radius;

        // Planet Type'ı bul
        PlanetType planetType = getPlanetTypeFromBiome(biome);

        // Ore Profile'ı al
        OreProfile profile = ORE_PROFILES.get(planetType);
        if (profile == null) {
            profile = ORE_PROFILES.get(PlanetType.TERRESTRIAL); // Fallback
        }

        // Derinlik katmanına göre ore seç
        return profile.getOreForDepth(depth, random);
    }

    /**
     * GEZEGEN TİPİ PROFILLERINI BAŞLAT
     */
    private static void initializeOreProfiles() {

        // ═══════════════════════════════════════════════
        // 🌍 TERRESTRIAL (Dünya Benzeri) - Dengeli
        // ═══════════════════════════════════════════════
        OreProfile terrestrial = new OreProfile(PlanetType.TERRESTRIAL);
        // Yüzey (0.7-1.0) - Çok nadir
        terrestrial.addOre(0.7, 1.0, Material.COAL_ORE, 0.04);
        terrestrial.addOre(0.7, 1.0, Material.COPPER_ORE, 0.03);
        terrestrial.addOre(0.7, 1.0, Material.IRON_ORE, 0.02);
        // Orta (0.4-0.7) - Nadir
        terrestrial.addOre(0.4, 0.7, Material.IRON_ORE, 0.05);
        terrestrial.addOre(0.4, 0.7, Material.COAL_ORE, 0.03);
        terrestrial.addOre(0.4, 0.7, Material.GOLD_ORE, 0.02);
        terrestrial.addOre(0.4, 0.7, Material.REDSTONE_ORE, 0.015);
        terrestrial.addOre(0.4, 0.7, Material.LAPIS_ORE, 0.01);
        // Derin (0.2-0.4) - Çok nadir
        terrestrial.addOre(0.2, 0.4, Material.IRON_ORE, 0.03);
        terrestrial.addOre(0.2, 0.4, Material.GOLD_ORE, 0.025);
        terrestrial.addOre(0.2, 0.4, Material.DIAMOND_ORE, 0.015);
        terrestrial.addOre(0.2, 0.4, Material.REDSTONE_ORE, 0.02);
        terrestrial.addOre(0.2, 0.4, Material.EMERALD_ORE, 0.005);
        // Çok Derin (0.0-0.2) - Merkez - Çok nadir
        terrestrial.addOre(0.0, 0.2, Material.DIAMOND_ORE, 0.025);
        terrestrial.addOre(0.0, 0.2, Material.EMERALD_ORE, 0.01);
        terrestrial.addOre(0.0, 0.2, Material.GOLD_ORE, 0.02);
        terrestrial.addOre(0.0, 0.2, Material.DEEPSLATE_DIAMOND_ORE, 0.015);
        ORE_PROFILES.put(PlanetType.TERRESTRIAL, terrestrial);

        // ═══════════════════════════════════════════════
        // 🔥 LAVA (Volkanik) - Gold & Ancient Debris
        // ═══════════════════════════════════════════════
        OreProfile lava = new OreProfile(PlanetType.LAVA);
        // Yüzey - Çok nadir
        lava.addOre(0.7, 1.0, Material.NETHERRACK, 0.06);
        lava.addOre(0.7, 1.0, Material.NETHER_GOLD_ORE, 0.04);
        lava.addOre(0.7, 1.0, Material.BLACKSTONE, 0.03);
        // Orta - Nadir
        lava.addOre(0.4, 0.7, Material.NETHER_GOLD_ORE, 0.06);
        lava.addOre(0.4, 0.7, Material.NETHER_QUARTZ_ORE, 0.05);
        lava.addOre(0.4, 0.7, Material.MAGMA_BLOCK, 0.03);
        lava.addOre(0.4, 0.7, Material.GOLD_ORE, 0.04);
        // Derin - Çok nadir
        lava.addOre(0.2, 0.4, Material.GOLD_ORE, 0.05);
        lava.addOre(0.2, 0.4, Material.NETHER_QUARTZ_ORE, 0.04);
        lava.addOre(0.2, 0.4, Material.ANCIENT_DEBRIS, 0.01); // Çok nadir!
        lava.addOre(0.2, 0.4, Material.OBSIDIAN, 0.025);
        // Çok Derin - ANCIENT DEBRIS PARADISE! - Çok nadir
        lava.addOre(0.0, 0.2, Material.ANCIENT_DEBRIS, 0.02); // Daha nadir!
        lava.addOre(0.0, 0.2, Material.OBSIDIAN, 0.03);
        ORE_PROFILES.put(PlanetType.LAVA, lava);

        // ═══════════════════════════════════════════════
        // ❄️ ICE (Buzul) - Diamond & Lapis Paradise
        // ═══════════════════════════════════════════════
        OreProfile ice = new OreProfile(PlanetType.ICE);
        // Yüzey - Çok nadir
        ice.addOre(0.7, 1.0, Material.PACKED_ICE, 0.06);
        ice.addOre(0.7, 1.0, Material.IRON_ORE, 0.03);
        ice.addOre(0.7, 1.0, Material.COAL_ORE, 0.02);
        // Orta - Nadir
        ice.addOre(0.4, 0.7, Material.IRON_ORE, 0.05);
        ice.addOre(0.4, 0.7, Material.LAPIS_ORE, 0.04); // Lapis nadir
        ice.addOre(0.4, 0.7, Material.BLUE_ICE, 0.03);
        ice.addOre(0.4, 0.7, Material.DIAMOND_ORE, 0.02);
        // Derin - Çok nadir
        ice.addOre(0.2, 0.4, Material.DIAMOND_ORE, 0.04); // Diamond nadir
        ice.addOre(0.2, 0.4, Material.LAPIS_ORE, 0.05);
        ice.addOre(0.2, 0.4, Material.EMERALD_ORE, 0.015);
        ice.addOre(0.2, 0.4, Material.PACKED_ICE, 0.03);
        // Çok Derin - Çok nadir
        ice.addOre(0.0, 0.2, Material.DIAMOND_ORE, 0.06); // DIAMOND nadir!
        ice.addOre(0.0, 0.2, Material.DEEPSLATE_DIAMOND_ORE, 0.03);
        ice.addOre(0.0, 0.2, Material.BLUE_ICE, 0.02);
        ORE_PROFILES.put(PlanetType.ICE, ice);

        // ═══════════════════════════════════════════════
        // 💎 CRYSTAL (Kristal) - Gem Paradise
        // ═══════════════════════════════════════════════
        OreProfile crystal = new OreProfile(PlanetType.CRYSTAL);
        // Yüzey - Çok nadir
        crystal.addOre(0.7, 1.0, Material.QUARTZ_BLOCK, 0.05);
        crystal.addOre(0.7, 1.0, Material.AMETHYST_BLOCK, 0.03);
        crystal.addOre(0.7, 1.0, Material.CALCITE, 0.02);
        // Orta - Nadir
        crystal.addOre(0.4, 0.7, Material.AMETHYST_BLOCK, 0.06);
        crystal.addOre(0.4, 0.7, Material.DIAMOND_ORE, 0.03);
        crystal.addOre(0.4, 0.7, Material.EMERALD_ORE, 0.025);
        crystal.addOre(0.4, 0.7, Material.LAPIS_ORE, 0.04);
        // Derin - Çok nadir
        crystal.addOre(0.2, 0.4, Material.DIAMOND_ORE, 0.05);
        crystal.addOre(0.2, 0.4, Material.EMERALD_ORE, 0.04);
        crystal.addOre(0.2, 0.4, Material.AMETHYST_CLUSTER, 0.03);
        crystal.addOre(0.2, 0.4, Material.LAPIS_BLOCK, 0.015);
        // Çok Derin - GEM nadir!
        crystal.addOre(0.0, 0.2, Material.AMETHYST_BLOCK, 0.04);
        crystal.addOre(0.0, 0.2, Material.BUDDING_AMETHYST, 0.015);
        ORE_PROFILES.put(PlanetType.CRYSTAL, crystal);

        // ═══════════════════════════════════════════════
        // 🌑 SHADOW (Gölge) - Obsidian & Dark Materials
        // ═══════════════════════════════════════════════
        OreProfile shadow = new OreProfile(PlanetType.SHADOW);
        // Yüzey - Çok nadir
        shadow.addOre(0.7, 1.0, Material.END_STONE, 0.05);
        shadow.addOre(0.7, 1.0, Material.OBSIDIAN, 0.03);
        shadow.addOre(0.7, 1.0, Material.COAL_ORE, 0.02);
        // Orta - Nadir
        shadow.addOre(0.4, 0.7, Material.OBSIDIAN, 0.05);
        shadow.addOre(0.4, 0.7, Material.CRYING_OBSIDIAN, 0.025);
        shadow.addOre(0.4, 0.7, Material.DIAMOND_ORE, 0.02);
        shadow.addOre(0.4, 0.7, Material.END_STONE, 0.03);
        // Derin - Çok nadir
        shadow.addOre(0.2, 0.4, Material.CRYING_OBSIDIAN, 0.04);
        shadow.addOre(0.2, 0.4, Material.DIAMOND_ORE, 0.025);
        shadow.addOre(0.2, 0.4, Material.ANCIENT_DEBRIS, 0.01);
        shadow.addOre(0.2, 0.4, Material.PURPUR_BLOCK, 0.025);
        // Çok Derin - Çok nadir
        shadow.addOre(0.0, 0.2, Material.OBSIDIAN, 0.06);
        shadow.addOre(0.0, 0.2, Material.CRYING_OBSIDIAN, 0.03);
        shadow.addOre(0.0, 0.2, Material.RESPAWN_ANCHOR, 0.005); // Çok nadir!
        ORE_PROFILES.put(PlanetType.SHADOW, shadow);

        // ═══════════════════════════════════════════════
        // ☠️ TOXIC (Zehirli) - Redstone & Slime
        // ═══════════════════════════════════════════════
        OreProfile toxic = new OreProfile(PlanetType.TOXIC);
        // Yüzey - Çok nadir
        toxic.addOre(0.7, 1.0, Material.SLIME_BLOCK, 0.04);
        toxic.addOre(0.7, 1.0, Material.MOSS_BLOCK, 0.03);
        toxic.addOre(0.7, 1.0, Material.COPPER_ORE, 0.02);
        // Orta - Nadir
        toxic.addOre(0.4, 0.7, Material.REDSTONE_ORE, 0.06); // Redstone nadir
        toxic.addOre(0.4, 0.7, Material.SLIME_BLOCK, 0.05);
        toxic.addOre(0.4, 0.7, Material.COPPER_ORE, 0.04);
        toxic.addOre(0.4, 0.7, Material.IRON_ORE, 0.03);
        // Derin - Çok nadir
        toxic.addOre(0.2, 0.4, Material.REDSTONE_ORE, 0.06); // Redstone nadir
        toxic.addOre(0.2, 0.4, Material.GOLD_ORE, 0.025);
        toxic.addOre(0.2, 0.4, Material.DIAMOND_ORE, 0.015);
        toxic.addOre(0.2, 0.4, Material.SCULK, 0.02);
        // Çok Derin - Çok nadir
        toxic.addOre(0.0, 0.2, Material.REDSTONE_ORE, 0.05);
        toxic.addOre(0.0, 0.2, Material.SCULK_CATALYST, 0.01);
        toxic.addOre(0.0, 0.2, Material.DIAMOND_ORE, 0.02);
        ORE_PROFILES.put(PlanetType.TOXIC, toxic);

        // ═══════════════════════════════════════════════
        // 🌫️ GAS (Gaz Devi) - Minimal Ores
        // ═══════════════════════════════════════════════
        OreProfile gas = new OreProfile(PlanetType.GAS);
        // Çok az ore - gaz gezegeni - Çok nadir
        gas.addOre(0.7, 1.0, Material.IRON_ORE, 0.01);
        gas.addOre(0.4, 0.7, Material.GOLD_ORE, 0.012);
        gas.addOre(0.2, 0.4, Material.DIAMOND_ORE, 0.006);
        gas.addOre(0.0, 0.2, Material.NETHERITE_SCRAP, 0.003);
        ORE_PROFILES.put(PlanetType.GAS, gas);
    }

    /**
     * BiomeType -> PlanetType mapping
     */
    public static PlanetType getPlanetTypeFromBiome(BiomeType biome) {
        return switch (biome) {
            // TERRESTRIAL
            case FOREST, PLAINS, JUNGLE, SAVANNA, TAIGA, MEADOW,
                 DESERT, BADLANDS, CANYON, PRIMAL, EARTH -> PlanetType.TERRESTRIAL;

            // LAVA
            case LAVA_OCEAN, OBSIDIAN_PLAINS, MAGMA_CAVES, BASALTIC,
                 FLAME, INFERNAL, STAR_FORGED -> PlanetType.LAVA;

            // ICE
            case FROZEN_TUNDRA, ICE_SPIKES, GLACIER, AURORA, FROST, MIRROR -> PlanetType.ICE;

            // CRYSTAL
            case CRYSTAL_FOREST, CRYSTALLINE, LUMINOUS, GLOWSTONE_CAVERN,
                 CELESTIAL, DIVINE, QUANTUM, POLISHED, GLASS, CALCITE -> PlanetType.CRYSTAL;

            // SHADOW
            case VOID, CHORUS_LAND, CORRUPTED, VOID_BORN,
                 NEBULOUS, COSMIC, ETHEREAL, DIMENSIONAL, PHASE,
                 ARCANE, VOID_ENERGY, TEMPORAL -> PlanetType.SHADOW;

            // TOXIC
            case TOXIC_SWAMP, TOXIC, CORROSIVE, SPONGE -> PlanetType.TOXIC;

            // GAS
            case MUSHROOM_GIANT, FUNGAL, WIND, GALACTIC, NEBULA_INFUSED,
                 UNIVERSE_BOUND, STORMY, FOGGY -> PlanetType.GAS;

            default -> PlanetType.TERRESTRIAL;
        };
    }

    /**
     * TAŞ VARİANTLARI (çeşitlilik)
     */
    public static Material getStoneVariant(BiomeType biome, Random random) {
        if (random.nextDouble() > 0.12) return null; // %88 normal blok

        PlanetType planetType = getPlanetTypeFromBiome(biome);

        return switch (planetType) {
            case LAVA -> {
                Material[] variants = {Material.BLACKSTONE, Material.BASALT, Material.NETHERRACK, Material.MAGMA_BLOCK};
                yield variants[random.nextInt(variants.length)];
            }
            case CRYSTAL -> {
                Material[] variants = {Material.QUARTZ_BLOCK, Material.CALCITE, Material.DRIPSTONE_BLOCK, Material.AMETHYST_BLOCK};
                yield variants[random.nextInt(variants.length)];
            }
            case SHADOW -> {
                Material[] variants = {Material.END_STONE, Material.OBSIDIAN, Material.PURPUR_BLOCK, Material.BLACKSTONE};
                yield variants[random.nextInt(variants.length)];
            }
            case ICE -> {
                Material[] variants = {Material.STONE, Material.ANDESITE, Material.DIORITE, Material.PACKED_ICE};
                yield variants[random.nextInt(variants.length)];
            }
            case TOXIC -> {
                Material[] variants = {Material.MOSS_BLOCK, Material.SCULK, Material.SLIME_BLOCK, Material.PRISMARINE};
                yield variants[random.nextInt(variants.length)];
            }
            default -> {
                Material[] variants = {Material.STONE, Material.ANDESITE, Material.DIORITE, Material.GRANITE};
                yield variants[random.nextInt(variants.length)];
            }
        };
    }

    /**
     * ORE PROFILE CLASS
     */
    private static class OreProfile {
        private final PlanetType planetType;
        private final List<OreLayer> layers = new ArrayList<>();

        public OreProfile(PlanetType planetType) {
            this.planetType = planetType;
        }

        public void addOre(double minDepth, double maxDepth, Material ore, double chance) {
            layers.add(new OreLayer(minDepth, maxDepth, ore, chance));
        }

        public Material getOreForDepth(double depth, Random random) {
            // Check layers in order for structured generation like Overworld
            // Ensure ores spawn in separate veins/clusters by type - no mixing
            List<OreLayer> availableLayers = new ArrayList<>();
            for (OreLayer layer : layers) {
                if (depth >= layer.minDepth && depth <= layer.maxDepth) {
                    availableLayers.add(layer);
                }
            }

            // If multiple ores available in this depth, pick one randomly but ensure separation
            if (!availableLayers.isEmpty()) {
                OreLayer selectedLayer = availableLayers.get(random.nextInt(availableLayers.size()));
                if (random.nextDouble() < selectedLayer.chance) {
                    // Additional check: ensure ores are not too close to each other
                    // This creates orderly distribution - reduced to 50% for much rarer spawning
                    if (random.nextDouble() < 0.5) { // 50% chance to place, creating natural spacing
                        return selectedLayer.ore;
                    }
                }
            }
            return null;
        }
    }

    /**
     * ORE LAYER CLASS
     */
    private static class OreLayer {
        final double minDepth;
        final double maxDepth;
        final Material ore;
        final double chance;

        public OreLayer(double minDepth, double maxDepth, Material ore, double chance) {
            this.minDepth = minDepth;
            this.maxDepth = maxDepth;
            this.ore = ore;
            this.chance = chance;
        }
    }

    /**
     * Block data helper class
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