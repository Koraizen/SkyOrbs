package com.skyorbs.shapes.impl;

import com.skyorbs.shapes.PlanetShape;
import com.skyorbs.utils.NoiseGenerator;

/**
 * CRYSTAL SHAPE - Kristal yapısı
 * Creates planets with crystalline facets
 */
public class CrystalShape implements PlanetShape {

    @Override
    public boolean isBlockPart(int dx, int dy, int dz, int radius, long seed) {
        // Create crystalline facets
        double ax = Math.abs(dx);
        double ay = Math.abs(dy);
        double az = Math.abs(dz);
        
        // Octahedral base with noise
        double noise = NoiseGenerator.getNoise(dx, dy, dz, seed, 0.1);
        double facetDeform = 1 + noise * 0.2;
        
        int manhattan = (int)(ax + ay + az);
        
        return manhattan <= radius * facetDeform;
    }

    @Override
    public String getName() { 
        return "CRYSTAL"; 
    }

    @Override
    public double getDeformationFactor() { 
        return 0.2; 
    }

    @Override
    public String getDisplayName() { 
        return "Kristal"; 
    }
}
