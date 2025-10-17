package com.skyorbs.features;

import com.skyorbs.SkyOrbs;
import com.skyorbs.biomes.BiomeType;
import com.skyorbs.core.Orb;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.enchantments.Enchantment;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TreasureGenerator {
    
    public static List<TreasureLocation> generateTreasures(Orb orb, BiomeType biome, World world) {
        SkyOrbs plugin = SkyOrbs.getInstance();

        // Config kontrolü
        if (!plugin.getConfigManager().isTreasureGenerationEnabled()) {
            return new ArrayList<>();
        }

        List<TreasureLocation> treasures = new ArrayList<>();
        // Use deterministic seed based on biome + planet seed for consistent loot
        Random random = new Random(orb.getSeed() + biome.name().hashCode() + 999);

        int cx = orb.getCenterX();
        int cy = orb.getCenterY();
        int cz = orb.getCenterZ();
        int radius = orb.getRadius();

        // Biome-aware treasure density
        double densityMultiplier = plugin.getConfigManager().getTreasureDensityMultiplier();
        double biomeMultiplier = getBiomeTreasureMultiplier(biome);
        int treasureCount = Math.max(1, (int)(radius * 0.015 * densityMultiplier * biomeMultiplier));

        for (int i = 0; i < treasureCount; i++) {
            int x = cx + random.nextInt(radius * 2) - radius;
            int z = cz + random.nextInt(radius * 2) - radius;

            // Underground spawn (5-15 blocks below surface)
            int y = cy + radius - (5 + random.nextInt(10));

            // Determine treasure type based on biome
            TreasureType type = getTreasureType(biome, random);

            treasures.add(new TreasureLocation(x, y, z, biome, type));
        }

        return treasures;
    }
    
    /**
     * Hazine sandığını doldur
     */
    public static void fillTreasureChest(Block block, BiomeType biome, TreasureType type, Random random) {
        if (!(block.getState() instanceof Chest chest)) return;
        
        Inventory inv = chest.getInventory();
        inv.clear();
        
        // Loot tablosuna göre item ekle
        List<ItemStack> loot = generateLoot(biome, type, random);
        
        for (ItemStack item : loot) {
            int slot = random.nextInt(27);
            while (inv.getItem(slot) != null) {
                slot = random.nextInt(27);
            }
            inv.setItem(slot, item);
        }
        
        chest.update();
    }
    
    /**
     * Loot oluştur
     */
    private static List<ItemStack> generateLoot(BiomeType biome, TreasureType type, Random random) {
        List<ItemStack> loot = new ArrayList<>();
        
        switch (type) {
            case COMMON -> {
                // Temel itemler - AMOUNT EN AZ 1 OLACAK
                loot.add(new ItemStack(Material.IRON_INGOT, Math.max(1, 3 + random.nextInt(8))));
                loot.add(new ItemStack(Material.GOLD_INGOT, Math.max(1, 1 + random.nextInt(4))));
                loot.add(new ItemStack(Material.COAL, Math.max(1, 5 + random.nextInt(15))));
                loot.add(new ItemStack(Material.BREAD, Math.max(1, 2 + random.nextInt(6))));

                if (random.nextDouble() < 0.5) {
                    loot.add(new ItemStack(Material.IRON_SWORD, 1));
                }
                if (random.nextDouble() < 0.3) {
                    loot.add(new ItemStack(Material.IRON_PICKAXE, 1));
                }
            }
            
            case UNCOMMON -> {
                // Daha iyi itemler - AMOUNT EN AZ 1
                loot.add(new ItemStack(Material.GOLD_INGOT, Math.max(1, 4 + random.nextInt(8))));
                loot.add(new ItemStack(Material.DIAMOND, Math.max(1, 1 + random.nextInt(3))));
                if (random.nextInt(3) > 0) { // 66% şans
                    loot.add(new ItemStack(Material.EMERALD, Math.max(1, random.nextInt(3))));
                }
                loot.add(new ItemStack(Material.ENCHANTED_BOOK, 1));

                if (random.nextDouble() < 0.6) {
                    ItemStack sword = new ItemStack(Material.IRON_SWORD, 1);
                    sword.addEnchantment(Enchantment.SHARPNESS, 1 + random.nextInt(2));
                    loot.add(sword);
                }

                if (random.nextDouble() < 0.4) {
                    loot.add(new ItemStack(Material.GOLDEN_APPLE, Math.max(1, 1 + random.nextInt(2))));
                }
            }
            
            case RARE -> {
                // Nadir itemler - AMOUNT EN AZ 1
                loot.add(new ItemStack(Material.DIAMOND, Math.max(1, 3 + random.nextInt(5))));
                loot.add(new ItemStack(Material.EMERALD, Math.max(1, 2 + random.nextInt(4))));
                if (random.nextInt(2) > 0) { // 50% şans
                    loot.add(new ItemStack(Material.NETHERITE_SCRAP, Math.max(1, random.nextInt(2))));
                }

                if (random.nextDouble() < 0.7) {
                    ItemStack armor = new ItemStack(
                        random.nextBoolean() ? Material.DIAMOND_CHESTPLATE : Material.DIAMOND_HELMET, 1
                    );
                    armor.addEnchantment(Enchantment.PROTECTION, 2 + random.nextInt(2));
                    loot.add(armor);
                }

                if (random.nextDouble() < 0.5) {
                    ItemStack pickaxe = new ItemStack(Material.DIAMOND_PICKAXE, 1);
                    pickaxe.addEnchantment(Enchantment.EFFICIENCY, 2 + random.nextInt(3));
                    pickaxe.addEnchantment(Enchantment.FORTUNE, 1 + random.nextInt(2));
                    loot.add(pickaxe);
                }

                loot.add(new ItemStack(Material.GOLDEN_APPLE, Math.max(1, 2 + random.nextInt(3))));
                loot.add(new ItemStack(Material.ENCHANTED_BOOK, 1));
            }
            
            case EPIC -> {
                // Epik itemler - AMOUNT EN AZ 1
                loot.add(new ItemStack(Material.DIAMOND, Math.max(1, 5 + random.nextInt(10))));
                loot.add(new ItemStack(Material.EMERALD, Math.max(1, 4 + random.nextInt(8))));
                loot.add(new ItemStack(Material.NETHERITE_INGOT, Math.max(1, 1 + random.nextInt(2))));
                if (random.nextInt(2) > 0) { // 50% şans
                    loot.add(new ItemStack(Material.ENCHANTED_GOLDEN_APPLE, Math.max(1, random.nextInt(2))));
                }

                // Tam set zırh
                ItemStack helmet = new ItemStack(Material.DIAMOND_HELMET, 1);
                helmet.addEnchantment(Enchantment.PROTECTION, 3 + random.nextInt(2));
                helmet.addEnchantment(Enchantment.UNBREAKING, 2 + random.nextInt(2));
                loot.add(helmet);

                ItemStack chestplate = new ItemStack(Material.DIAMOND_CHESTPLATE, 1);
                chestplate.addEnchantment(Enchantment.PROTECTION, 3 + random.nextInt(2));
                loot.add(chestplate);

                // Güçlü silah
                ItemStack sword = new ItemStack(Material.DIAMOND_SWORD, 1);
                sword.addEnchantment(Enchantment.SHARPNESS, 3 + random.nextInt(2));
                sword.addEnchantment(Enchantment.LOOTING, 1 + random.nextInt(2));
                loot.add(sword);

                // Enchanted book
                loot.add(new ItemStack(Material.ENCHANTED_BOOK, 1));
                loot.add(new ItemStack(Material.ENCHANTED_BOOK, 1));
            }
            
            case LEGENDARY -> {
                // Efsanevi itemler - AMOUNT EN AZ 1
                loot.add(new ItemStack(Material.DIAMOND_BLOCK, Math.max(1, 2 + random.nextInt(4))));
                loot.add(new ItemStack(Material.EMERALD_BLOCK, Math.max(1, 1 + random.nextInt(3))));
                loot.add(new ItemStack(Material.NETHERITE_INGOT, Math.max(1, 2 + random.nextInt(3))));
                loot.add(new ItemStack(Material.ENCHANTED_GOLDEN_APPLE, Math.max(1, 2 + random.nextInt(3))));
                loot.add(new ItemStack(Material.TOTEM_OF_UNDYING, 1));

                // Netherite ekipman
                ItemStack netheriteHelmet = new ItemStack(Material.NETHERITE_HELMET, 1);
                netheriteHelmet.addEnchantment(Enchantment.PROTECTION, 4);
                netheriteHelmet.addEnchantment(Enchantment.UNBREAKING, 3);
                netheriteHelmet.addEnchantment(Enchantment.MENDING, 1);
                loot.add(netheriteHelmet);

                ItemStack netheriteChest = new ItemStack(Material.NETHERITE_CHESTPLATE, 1);
                netheriteChest.addEnchantment(Enchantment.PROTECTION, 4);
                netheriteChest.addEnchantment(Enchantment.UNBREAKING, 3);
                loot.add(netheriteChest);

                ItemStack netheriteSword = new ItemStack(Material.NETHERITE_SWORD, 1);
                netheriteSword.addEnchantment(Enchantment.SHARPNESS, 5);
                netheriteSword.addEnchantment(Enchantment.LOOTING, 3);
                netheriteSword.addEnchantment(Enchantment.UNBREAKING, 3);
                loot.add(netheriteSword);

                ItemStack netheritePickaxe = new ItemStack(Material.NETHERITE_PICKAXE, 1);
                netheritePickaxe.addEnchantment(Enchantment.EFFICIENCY, 5);
                netheritePickaxe.addEnchantment(Enchantment.FORTUNE, 3);
                netheritePickaxe.addEnchantment(Enchantment.UNBREAKING, 3);
                loot.add(netheritePickaxe);

                // Özel itemler
                loot.add(new ItemStack(Material.ELYTRA, 1));
                loot.add(new ItemStack(Material.TRIDENT, 1));
                loot.add(new ItemStack(Material.ENCHANTED_BOOK, 1));
                loot.add(new ItemStack(Material.ENCHANTED_BOOK, 1));
                loot.add(new ItemStack(Material.ENCHANTED_BOOK, 1));
            }
        }
        
        // Biyom özel bonuslar
        if (SkyOrbs.getInstance().getConfigManager().areBiomeSpecificTreasuresEnabled()) {
            addBiomeSpecificLoot(loot, biome, random);
        }
        
        return loot;
    }
    
    /**
     * Add biome-specific loot items deterministically
     */
    private static void addBiomeSpecificLoot(List<ItemStack> loot, BiomeType biome, Random random) {
        switch (biome) {
            case DESERT, BADLANDS -> {
                if (random.nextDouble() < 0.6) {
                    loot.add(new ItemStack(Material.SAND, 16 + random.nextInt(32)));
                    loot.add(new ItemStack(Material.CACTUS, 4 + random.nextInt(8)));
                }
            }

            case FROZEN_TUNDRA, ICE_SPIKES, GLACIER -> {
                if (random.nextDouble() < 0.6) {
                    loot.add(new ItemStack(Material.PACKED_ICE, 8 + random.nextInt(16)));
                    loot.add(new ItemStack(Material.BLUE_ICE, 2 + random.nextInt(6)));
                }
            }

            case JUNGLE -> {
                if (random.nextDouble() < 0.6) {
                    loot.add(new ItemStack(Material.BAMBOO, 16 + random.nextInt(32)));
                    loot.add(new ItemStack(Material.COCOA_BEANS, 8 + random.nextInt(16)));
                }
            }

            case LAVA_OCEAN, MAGMA_CAVES -> {
                if (random.nextDouble() < 0.7) {
                    loot.add(new ItemStack(Material.MAGMA_BLOCK, 8 + random.nextInt(16)));
                    loot.add(new ItemStack(Material.FIRE_CHARGE, 4 + random.nextInt(8)));
                }
            }

            case CRYSTAL_FOREST -> {
                if (random.nextDouble() < 0.8) {
                    loot.add(new ItemStack(Material.AMETHYST_SHARD, 8 + random.nextInt(16)));
                    loot.add(new ItemStack(Material.AMETHYST_BLOCK, 2 + random.nextInt(4)));
                }
            }

            case MUSHROOM_GIANT -> {
                if (random.nextDouble() < 0.7) {
                    loot.add(new ItemStack(Material.RED_MUSHROOM, 8 + random.nextInt(16)));
                    loot.add(new ItemStack(Material.BROWN_MUSHROOM, 8 + random.nextInt(16)));
                }
            }

            case GLOWSTONE_CAVERN -> {
                if (random.nextDouble() < 0.7) {
                    loot.add(new ItemStack(Material.GLOWSTONE_DUST, 16 + random.nextInt(32)));
                    loot.add(new ItemStack(Material.GLOWSTONE, 4 + random.nextInt(8)));
                }
            }

            case CHORUS_LAND -> {
                if (random.nextDouble() < 0.6) {
                    loot.add(new ItemStack(Material.CHORUS_FRUIT, 8 + random.nextInt(16)));
                    loot.add(new ItemStack(Material.ENDER_PEARL, 2 + random.nextInt(4)));
                }
            }

            // Additional biome-specific loot for new biomes
            case BASALTIC -> {
                if (random.nextDouble() < 0.5) {
                    loot.add(new ItemStack(Material.BASALT, 12 + random.nextInt(24)));
                    loot.add(new ItemStack(Material.BLACKSTONE, 8 + random.nextInt(16)));
                }
            }

            case CORAL -> {
                if (random.nextDouble() < 0.7) {
                    loot.add(new ItemStack(Material.BRAIN_CORAL, 4 + random.nextInt(8)));
                    loot.add(new ItemStack(Material.TUBE_CORAL, 4 + random.nextInt(8)));
                }
            }

            case TOXIC -> {
                if (random.nextDouble() < 0.6) {
                    loot.add(new ItemStack(Material.POISONOUS_POTATO, 6 + random.nextInt(12)));
                    loot.add(new ItemStack(Material.SPIDER_EYE, 4 + random.nextInt(8)));
                }
            }

            case AURORA -> {
                if (random.nextDouble() < 0.4) {
                    loot.add(new ItemStack(Material.PRISMARINE_CRYSTALS, 8 + random.nextInt(16)));
                    loot.add(new ItemStack(Material.PRISMARINE_SHARD, 6 + random.nextInt(12)));
                }
            }

            case STORMY -> {
                if (random.nextDouble() < 0.5) {
                    loot.add(new ItemStack(Material.NAUTILUS_SHELL, 1));
                    loot.add(new ItemStack(Material.HEART_OF_THE_SEA, 1));
                }
            }

            case FOGGY -> {
                if (random.nextDouble() < 0.5) {
                    loot.add(new ItemStack(Material.PHANTOM_MEMBRANE, 2 + random.nextInt(4)));
                }
            }

            case CORROSIVE -> {
                if (random.nextDouble() < 0.6) {
                    loot.add(new ItemStack(Material.WITHER_ROSE, 3 + random.nextInt(6)));
                    loot.add(new ItemStack(Material.WITHER_SKELETON_SKULL, 1));
                }
            }

            case LUMINOUS -> {
                if (random.nextDouble() < 0.7) {
                    loot.add(new ItemStack(Material.SEA_LANTERN, 4 + random.nextInt(8)));
                    loot.add(new ItemStack(Material.SHROOMLIGHT, 3 + random.nextInt(6)));
                }
            }
        }
    }
    
    /**
     * Get biome-specific treasure multiplier
     */
    private static double getBiomeTreasureMultiplier(BiomeType biome) {
        return switch (biome) {
            case CRYSTAL_FOREST, VOID, LAVA_OCEAN, GLOWSTONE_CAVERN -> 2.0; // Rich biomes
            case TOXIC_SWAMP, CORRUPTED, CHORUS_LAND -> 1.5; // Moderate
            default -> 1.0; // Standard
        };
    }

    /**
     * Determine treasure type based on biome with deterministic seeding
     */
    private static TreasureType getTreasureType(BiomeType biome, Random random) {
        SkyOrbs plugin = SkyOrbs.getInstance();
        double rand = random.nextDouble();

        // Biome-specific legendary chances
        double legendaryMult = plugin.getConfigManager().getLegendaryChanceMultiplier();
        double biomeLegendaryBonus = getBiomeLegendaryBonus(biome);

        if (rand < (0.02 * legendaryMult * biomeLegendaryBonus)) return TreasureType.LEGENDARY;
        if (rand < 0.08) return TreasureType.EPIC;
        if (rand < 0.20) return TreasureType.RARE;
        if (rand < 0.45) return TreasureType.UNCOMMON;
        return TreasureType.COMMON;
    }

    /**
     * Get biome-specific legendary treasure bonus
     */
    private static double getBiomeLegendaryBonus(BiomeType biome) {
        return switch (biome) {
            case CRYSTAL_FOREST -> 3.0; // Crystal planets have best loot
            case VOID, LAVA_OCEAN, GLOWSTONE_CAVERN -> 2.5;
            case CORRUPTED, CHORUS_LAND -> 2.0;
            case TOXIC_SWAMP, MAGMA_CAVES -> 1.5;
            default -> 1.0;
        };
    }
    
    /**
     * Hazine türleri
     */
    public enum TreasureType {
        COMMON,      // %55 - Temel itemler
        UNCOMMON,    // %25 - İyi itemler
        RARE,        // %12 - Nadir itemler
        EPIC,        // %6 - Epik itemler
        LEGENDARY    // %2 - Efsanevi itemler
    }
    
    /**
     * Hazine lokasyonu
     */
    public static class TreasureLocation {
        public final int x, y, z;
        public final BiomeType biome;
        public final TreasureType type;
        
        public TreasureLocation(int x, int y, int z, BiomeType biome, TreasureType type) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.biome = biome;
            this.type = type;
        }
    }
}