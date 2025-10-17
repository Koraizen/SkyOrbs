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
                ores.add(new BlockData(x, y, z, ore));
            }
        }
        
        return ores;
    }
    
    private static Material getRandomOre(Random random, BiomeType biome) {
        double rand = random.nextDouble();
        
        if (rand < 0.4) return Material.COAL_ORE;
        if (rand < 0.6) return Material.IRON_ORE;
        if (rand < 0.75) return Material.COPPER_ORE;
        if (rand < 0.85) return Material.GOLD_ORE;
        if (rand < 0.95) return Material.DIAMOND_ORE;
        return Material.EMERALD_ORE;
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
