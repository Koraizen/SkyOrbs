package com.skyorbs.features;

import com.skyorbs.biomes.BiomeType;
import com.skyorbs.core.Orb;
import org.bukkit.Material;
import org.bukkit.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TreeGenerator {
    
    public static List<OreGenerator.BlockData> generateTrees(Orb orb, BiomeType biome, World world) {
        List<OreGenerator.BlockData> blocks = new ArrayList<>();
        Random random = new Random(orb.getSeed() + 456);
        
        int treeCount = switch (biome) {
            case FOREST, JUNGLE -> orb.getRadius() / 10;
            case TAIGA, SAVANNA -> orb.getRadius() / 15;
            default -> 0;
        };
        
        for (int i = 0; i < treeCount; i++) {
            int x = orb.getCenterX() + random.nextInt(orb.getRadius() * 2) - orb.getRadius();
            int y = orb.getCenterY() + orb.getRadius();
            int z = orb.getCenterZ() + random.nextInt(orb.getRadius() * 2) - orb.getRadius();
            
            blocks.add(new OreGenerator.BlockData(x, y, z, Material.OAK_LOG));
            blocks.add(new OreGenerator.BlockData(x, y + 1, z, Material.OAK_LOG));
            blocks.add(new OreGenerator.BlockData(x, y + 2, z, Material.OAK_LEAVES));
        }
        
        return blocks;
    }
}
