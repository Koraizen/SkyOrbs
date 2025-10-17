package com.skyorbs.generation;

import com.skyorbs.SkyOrbs;
import com.skyorbs.biomes.BiomeType;
import com.skyorbs.core.Orb;
import org.bukkit.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class SatelliteGenerator {
    
    private final SkyOrbs plugin;
    
    public SatelliteGenerator(SkyOrbs plugin) {
        this.plugin = plugin;
    }
    
    public List<Orb> generateSatellitesForPlanet(Orb planet, World world) {
        List<Orb> satellites = new ArrayList<>();
        
        if (!plugin.getConfigManager().isSatellitesEnabled()) {
            return satellites;
        }
        
        Random random = new Random(planet.getSeed() + 54321);
        
        if (random.nextDouble() > plugin.getConfigManager().getSatelliteProbability()) {
            return satellites;
        }
        
        int minCount = plugin.getConfigManager().getMinSatellitesPerPlanet();
        int maxCount = plugin.getConfigManager().getMaxSatellitesPerPlanet();
        int count = minCount + random.nextInt(maxCount - minCount + 1);
        
        int minRadius = 30;
        int maxRadius = 70;
        
        for (int i = 0; i < count; i++) {
            int radius = minRadius + random.nextInt(maxRadius - minRadius + 1);
            
            int safetyBuffer = 100;
            int minDist = planet.getRadius() + radius + safetyBuffer;
            int maxDist = planet.getRadius() + radius + safetyBuffer + 300;
            
            double angle = random.nextDouble() * 2 * Math.PI;
            int distance = minDist + random.nextInt(Math.max(1, maxDist - minDist));
            
            int x = planet.getCenterX() + (int)(Math.cos(angle) * distance);
            int z = planet.getCenterZ() + (int)(Math.sin(angle) * distance);
            int y = planet.getCenterY() + random.nextInt(61) - 30;
            
            BiomeType biome = BiomeType.getRandomBiomeWeighted(random);
            
            Orb satellite = new Orb(
                UUID.randomUUID(),
                planet.getName() + "_Uydu_" + (i + 1),
                world.getName(),
                x, y, z,
                radius,
                "SPHERE",
                biome.name(),
                planet.getSeed() + i + 5000,
                System.currentTimeMillis(),
                true,
                planet.getId()
            );
            
            satellites.add(satellite);
        }
        
        return satellites;
    }
}
