package com.skyorbs.features;

import com.skyorbs.biomes.BiomeType;
import com.skyorbs.core.Orb;
import org.bukkit.Material;
import org.bukkit.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class StructureGenerator {
    
    public static List<OreGenerator.BlockData> generateStructures(Orb orb, BiomeType biome, World world) {
        List<OreGenerator.BlockData> blocks = new ArrayList<>();
        Random random = new Random(orb.getSeed() + 789);
        
        int structureCount = orb.getRadius() / 40;
        
        for (int i = 0; i < structureCount; i++) {
            int x = orb.getCenterX() + random.nextInt(orb.getRadius()) - orb.getRadius() / 2;
            int y = orb.getCenterY() + orb.getRadius() / 2;
            int z = orb.getCenterZ() + random.nextInt(orb.getRadius()) - orb.getRadius() / 2;
            
            blocks.add(new OreGenerator.BlockData(x, y, z, Material.STONE_BRICKS));
            blocks.add(new OreGenerator.BlockData(x, y + 1, z, Material.STONE_BRICKS));
        }
        
        return blocks;
    }
}
