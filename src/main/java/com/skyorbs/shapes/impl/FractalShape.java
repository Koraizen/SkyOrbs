package com.skyorbs.shapes.impl;

import com.skyorbs.shapes.PlanetShape;
import com.skyorbs.utils.NoiseGenerator;

public class FractalShape implements PlanetShape {
    
    @Override
    public boolean isBlockPart(int dx, int dy, int dz, int radius, long seed) {
        double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
        
        double noise1 = NoiseGenerator.getNoise(dx, dy, dz, seed, 0.1);
        double noise2 = NoiseGenerator.getNoise(dx, dy, dz, seed + 1000, 0.2);
        double noise3 = NoiseGenerator.getNoise(dx, dy, dz, seed + 2000, 0.4);
        
        double fractal = noise1 * 0.5 + noise2 * 0.3 + noise3 * 0.2;
        double deformation = 1 + 0.4 * fractal;
        
        return distance <= radius * deformation;
    }
    
    @Override
    public String getName() {
        return "FRACTAL";
    }
    
    @Override
    public double getDeformationFactor() {
        return 0.4;
    }
    
    @Override
    public String getDisplayName() {
        return "Fraktal";
    }
}
