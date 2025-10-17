package com.skyorbs.shapes.impl;

import com.skyorbs.shapes.PlanetShape;
import com.skyorbs.utils.NoiseGenerator;

import java.util.Random;

public class HybridShape implements PlanetShape {
    
    @Override
    public boolean isBlockPart(int dx, int dy, int dz, int radius, long seed) {
        Random rand = new Random(seed);
        double choice = rand.nextDouble();
        
        double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
        double noise = NoiseGenerator.getNoise(dx, dy, dz, seed, 0.15);
        
        if (choice < 0.5) {
            double sphereDeform = 1 + 0.2 * noise;
            return distance <= radius * sphereDeform;
        } else {
            int manhattan = Math.abs(dx) + Math.abs(dy) + Math.abs(dz);
            double cubeDeform = 1 + 0.15 * noise;
            return manhattan <= radius * cubeDeform;
        }
    }
    
    @Override
    public String getName() {
        return "HYBRID";
    }
    
    @Override
    public double getDeformationFactor() {
        return 0.2;
    }
    
    @Override
    public String getDisplayName() {
        return "Karma";
    }
}
