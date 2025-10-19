package com.skyorbs.shapes.impl;

import com.skyorbs.shapes.PlanetShape;
import com.skyorbs.utils.NoiseGenerator;

public class AsteroidShape implements PlanetShape {
    
    @Override
    public boolean isBlockPart(int dx, int dy, int dz, int radius, long seed) {
        double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
        double noise = NoiseGenerator.getNoise(dx, dy, dz, seed, 0.2);
        double roughness = 0.6 + 0.4 * Math.abs(noise);
        return distance <= radius * roughness;
    }
    
    @Override
    public String getName() {
        return "ASTEROID";
    }
    
    @Override
    public double getDeformationFactor() {
        return 0.5;
    }
    
    @Override
    public String getDisplayName() {
        return "Asteroid";
    }
}
