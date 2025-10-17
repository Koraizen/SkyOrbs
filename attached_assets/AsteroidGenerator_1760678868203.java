package com.skyorbs.generation;

import com.skyorbs.SkyOrbs;
import com.skyorbs.biomes.BiomeType;
import com.skyorbs.core.Orb;
import com.skyorbs.shapes.PlanetShape;
import com.skyorbs.shapes.impl.AsteroidShape;
import com.skyorbs.shapes.impl.FractalShape;
import org.bukkit.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class AsteroidGenerator {
    
    private final SkyOrbs plugin;
    
    public AsteroidGenerator(SkyOrbs plugin) {
        this.plugin = plugin;
    }
    
    public List<Orb> generateAsteroidsForPlanet(Orb planet, World world) {
        List<Orb> asteroids = new ArrayList<>();
        
        if (!plugin.getConfigManager().isAsteroidsEnabled()) {
            return asteroids;
        }
        
        Random random = new Random(planet.getSeed() + 12345);
        
        int minCount = plugin.getConfigManager().getMinAsteroidsPerPlanet();
        int maxCount = plugin.getConfigManager().getMaxAsteroidsPerPlanet();
        int count = minCount + random.nextInt(maxCount - minCount + 1);
        
        int minRadius = plugin.getConfigManager().getMinAsteroidRadius();
        int maxRadius = plugin.getConfigManager().getMaxAsteroidRadius();
        int minDist = plugin.getConfigManager().getMinAsteroidDistance();
        int maxDist = plugin.getConfigManager().getMaxAsteroidDistance();
        
        for (int i = 0; i < count; i++) {
            int radius = minRadius + random.nextInt(maxRadius - minRadius + 1);
            
            double angle = random.nextDouble() * 2 * Math.PI;
            int distance = minDist + random.nextInt(maxDist - minDist);
            
            int x = planet.getCenterX() + (int)(Math.cos(angle) * distance);
            int z = planet.getCenterZ() + (int)(Math.sin(angle) * distance);
            int y = planet.getCenterY() + random.nextInt(41) - 20;
            
            PlanetShape shape = random.nextDouble() < 0.7 ? new AsteroidShape() : new FractalShape();
            BiomeType biome = BiomeType.ASTEROID;
            
            Orb asteroid = new Orb(
                UUID.randomUUID(),
                planet.getName() + "_Asteroid_" + (i + 1),
                world.getName(),
                x, y, z,
                radius,
                shape.getName(),
                biome != null ? biome.name() : "ASTEROID",
                planet.getSeed() + i + 1000,
                System.currentTimeMillis(),
                true,
                planet.getId()
            );
            
            asteroids.add(asteroid);
        }
        
        return asteroids;
    }
}
