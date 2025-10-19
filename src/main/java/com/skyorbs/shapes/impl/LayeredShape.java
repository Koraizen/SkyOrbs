package com.skyorbs.shapes.impl;

import com.skyorbs.shapes.PlanetShape;
import com.skyorbs.utils.NoiseGenerator;

/**
 * LAYERED SHAPE - Katmanlı gezegen
 * Creates planets with visible layers like sedimentary rock
 */
public class LayeredShape implements PlanetShape {

    @Override
    public boolean isBlockPart(int dx, int dy, int dz, int radius, long seed) {
        double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
        
        // Create layers with noise
        double layerNoise = NoiseGenerator.getNoise(dx, dy, dz, seed, 0.05);
        int layerIndex = (int)((distance / radius) * 5); // 5 layers
        
        // Add variation to layer boundaries
        double layerVariation = 1.0 + layerNoise * 0.2;
        
        return distance <= radius * layerVariation;
    }

    @Override
    public String getName() { 
        return "LAYERED"; 
    }

    @Override
    public double getDeformationFactor() { 
        return 0.2; 
    }

    @Override
    public String getDisplayName() { 
        return "Katmanlı"; 
    }
}
