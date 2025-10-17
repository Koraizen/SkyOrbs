package com.skyorbs.features;

import com.skyorbs.biomes.BiomeType;
import com.skyorbs.core.Orb;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TreasureGenerator {
    
    public static List<TreasureLocation> generateTreasures(Orb orb, BiomeType biome, World world) {
        List<TreasureLocation> treasures = new ArrayList<>();
        Random random = new Random(orb.getSeed() + 999);
        
        int cx = orb.getCenterX();
        int cy = orb.getCenterY();
        int cz = orb.getCenterZ();
        int radius = orb.getRadius();
        
        int treasureCount = Math.max(1, radius / 50);
        
        for (int i = 0; i < treasureCount; i++) {
            int x = cx + random.nextInt(radius * 2) - radius;
            int y = cy + random.nextInt(radius * 2) - radius;
            int z = cz + random.nextInt(radius * 2) - radius;
            
            if (isInsidePlanet(x - cx, y - cy, z - cz, radius)) {
                TreasureTier tier = getTreasureTier(random);
                treasures.add(new TreasureLocation(x, y, z, tier, biome));
            }
        }
        
        return treasures;
    }
    
    private static TreasureTier getTreasureTier(Random random) {
        double rand = random.nextDouble();
        if (rand < 0.5) return TreasureTier.COMMON;
        if (rand < 0.8) return TreasureTier.UNCOMMON;
        if (rand < 0.95) return TreasureTier.RARE;
        return TreasureTier.EPIC;
    }
    
    public static void placeChest(World world, int x, int y, int z, TreasureTier tier, BiomeType biome) {
        Block block = world.getBlockAt(x, y, z);
        block.setType(Material.CHEST);
        
        if (block.getState() instanceof Chest chest) {
            Inventory inv = chest.getInventory();
            Random random = new Random();
            
            List<ItemStack> loot = generateLoot(tier, biome, random);
            for (ItemStack item : loot) {
                inv.addItem(item);
            }
            
            chest.update();
        }
    }
    
    private static List<ItemStack> generateLoot(TreasureTier tier, BiomeType biome, Random random) {
        List<ItemStack> loot = new ArrayList<>();
        
        switch (tier) {
            case COMMON -> {
                loot.add(new ItemStack(Material.COAL, 16 + random.nextInt(32)));
                loot.add(new ItemStack(Material.IRON_INGOT, 8 + random.nextInt(16)));
                loot.add(new ItemStack(Material.BREAD, 4 + random.nextInt(8)));
            }
            case UNCOMMON -> {
                loot.add(new ItemStack(Material.IRON_INGOT, 16 + random.nextInt(32)));
                loot.add(new ItemStack(Material.GOLD_INGOT, 8 + random.nextInt(16)));
                loot.add(new ItemStack(Material.DIAMOND, 2 + random.nextInt(4)));
                loot.add(new ItemStack(Material.ENDER_PEARL, 4 + random.nextInt(8)));
            }
            case RARE -> {
                loot.add(new ItemStack(Material.DIAMOND, 8 + random.nextInt(16)));
                loot.add(new ItemStack(Material.EMERALD, 4 + random.nextInt(8)));
                loot.add(new ItemStack(Material.NETHERITE_SCRAP, 1 + random.nextInt(3)));
                loot.add(new ItemStack(Material.ENCHANTED_GOLDEN_APPLE, 1 + random.nextInt(2)));
            }
            case EPIC -> {
                loot.add(new ItemStack(Material.DIAMOND_BLOCK, 4 + random.nextInt(8)));
                loot.add(new ItemStack(Material.EMERALD_BLOCK, 2 + random.nextInt(4)));
                loot.add(new ItemStack(Material.NETHERITE_INGOT, 1 + random.nextInt(3)));
                loot.add(new ItemStack(Material.ELYTRA, 1));
                loot.add(new ItemStack(Material.ENCHANTED_GOLDEN_APPLE, 4));
            }
        }
        
        addBiomeSpecificLoot(loot, biome, tier, random);
        
        return loot;
    }
    
    private static void addBiomeSpecificLoot(List<ItemStack> loot, BiomeType biome, TreasureTier tier, Random random) {
        switch (biome) {
            case CRYSTAL_FOREST -> loot.add(new ItemStack(Material.AMETHYST_SHARD, 8 + random.nextInt(16)));
            case LAVA_OCEAN, MAGMA_CAVES -> loot.add(new ItemStack(Material.ANCIENT_DEBRIS, 1 + random.nextInt(2)));
            case FROZEN_TUNDRA, ICE_SPIKES -> loot.add(new ItemStack(Material.BLUE_ICE, 16 + random.nextInt(32)));
            case MUSHROOM_GIANT -> loot.add(new ItemStack(Material.RED_MUSHROOM, 16 + random.nextInt(32)));
            default -> loot.add(new ItemStack(Material.GOLD_NUGGET, 16 + random.nextInt(32)));
        }
    }
    
    private static boolean isInsidePlanet(int dx, int dy, int dz, int radius) {
        return dx * dx + dy * dy + dz * dz <= radius * radius * 0.8;
    }
    
    public enum TreasureTier {
        COMMON, UNCOMMON, RARE, EPIC
    }
    
    public static class TreasureLocation {
        public final int x, y, z;
        public final TreasureTier tier;
        public final BiomeType biome;
        
        public TreasureLocation(int x, int y, int z, TreasureTier tier, BiomeType biome) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.tier = tier;
            this.biome = biome;
        }
    }
}
