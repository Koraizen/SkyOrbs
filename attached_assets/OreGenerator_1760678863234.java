package com.skyorbs.features;

import com.skyorbs.biomes.BiomeType;
import com.skyorbs.core.Orb;
import org.bukkit.Material;
import org.bukkit.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class OreGenerator {
    
    public static List<BlockData> generateOres(Orb orb, BiomeType biome, World world) {
        List<BlockData> ores = new ArrayList<>();
        Random random = new Random(orb.getSeed() + 123);
        
        int cx = orb.getCenterX();
        int cy = orb.getCenterY();
        int cz = orb.getCenterZ();
        int radius = orb.getRadius();
        
        int totalOres = (int)(radius * radius * 0.05);
        
        for (int i = 0; i < totalOres; i++) {
            int x = cx + random.nextInt(radius * 2) - radius;
            int y = cy + random.nextInt(radius * 2) - radius;
            int z = cz + random.nextInt(radius * 2) - radius;
            
            if (isInsidePlanet(x - cx, y - cy, z - cz, radius)) {
                Material ore = getRandomOre(random, biome);
                int veinSize = getVeinSize(ore, random);
                
                generateVein(ores, x, y, z, ore, veinSize, random);
            }
        }
        
        return ores;
    }
    
    private static Material getRandomOre(Random random, BiomeType biome) {
        double rand = random.nextDouble();
        
        switch (biome) {
            case LAVA_OCEAN, MAGMA_CAVES, OBSIDIAN_PLAINS:
                if (rand < 0.3) return Material.GOLD_ORE;
                if (rand < 0.5) return Material.DIAMOND_ORE;
                if (rand < 0.7) return Material.NETHERITE_SCRAP;
                return Material.ANCIENT_DEBRIS;
                
            case CRYSTAL_FOREST, GLOWSTONE_CAVERN:
                if (rand < 0.4) return Material.AMETHYST_BLOCK;
                if (rand < 0.7) return Material.DIAMOND_ORE;
                return Material.EMERALD_ORE;
                
            case FROZEN_TUNDRA, ICE_SPIKES, GLACIER:
                if (rand < 0.4) return Material.IRON_ORE;
                if (rand < 0.7) return Material.LAPIS_ORE;
                if (rand < 0.9) return Material.DIAMOND_ORE;
                return Material.EMERALD_ORE;
                
            default:
                if (rand < 0.4) return Material.COAL_ORE;
                if (rand < 0.6) return Material.IRON_ORE;
                if (rand < 0.75) return Material.COPPER_ORE;
                if (rand < 0.85) return Material.GOLD_ORE;
                if (rand < 0.95) return Material.DIAMOND_ORE;
                return Material.EMERALD_ORE;
        }
    }
    
    private static int getVeinSize(Material ore, Random random) {
        return switch (ore) {
            case COAL_ORE -> 8 + random.nextInt(9);
            case IRON_ORE, COPPER_ORE -> 6 + random.nextInt(7);
            case GOLD_ORE, LAPIS_ORE -> 4 + random.nextInt(5);
            case DIAMOND_ORE, EMERALD_ORE -> 2 + random.nextInt(4);
            case ANCIENT_DEBRIS, NETHERITE_SCRAP -> 1 + random.nextInt(2);
            case AMETHYST_BLOCK -> 3 + random.nextInt(5);
            default -> 4;
        };
    }
    
    private static void generateVein(List<BlockData> ores, int x, int y, int z, Material ore, int size, Random random) {
        for (int i = 0; i < size; i++) {
            int dx = random.nextInt(3) - 1;
            int dy = random.nextInt(3) - 1;
            int dz = random.nextInt(3) - 1;
            
            ores.add(new BlockData(x + dx, y + dy, z + dz, ore));
        }
    }
    
    private static boolean isInsidePlanet(int dx, int dy, int dz, int radius) {
        return dx * dx + dy * dy + dz * dz <= radius * radius;
    }
    
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
