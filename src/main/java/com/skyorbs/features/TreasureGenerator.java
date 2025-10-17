package com.skyorbs.features;

import com.skyorbs.biomes.BiomeType;
import com.skyorbs.core.Orb;
import org.bukkit.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TreasureGenerator {
    
    public static List<TreasureLocation> generateTreasures(Orb orb, BiomeType biome, World world) {
        List<TreasureLocation> treasures = new ArrayList<>();
        Random random = new Random(orb.getSeed() + 999);
        
        int treasureCount = Math.max(1, orb.getRadius() / 50);
        
        for (int i = 0; i < treasureCount; i++) {
            int x = orb.getCenterX() + random.nextInt(orb.getRadius()) - orb.getRadius() / 2;
            int y = orb.getCenterY();
            int z = orb.getCenterZ() + random.nextInt(orb.getRadius()) - orb.getRadius() / 2;
            
            treasures.add(new TreasureLocation(x, y, z, biome));
        }
        
        return treasures;
    }
    
    public static class TreasureLocation {
        public final int x, y, z;
        public final BiomeType biome;
        
        public TreasureLocation(int x, int y, int z, BiomeType biome) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.biome = biome;
        }
    }
}
