package com.skyorbs.shapes.impl;

import com.skyorbs.shapes.PlanetShape;
import com.skyorbs.utils.NoiseGenerator;

/**
 * ORGANIC SHAPE - Organik yapı
 * Creates planets with organic, natural-looking forms
 */
public class OrganicShape implements PlanetShape {

    @Override
    public boolean isBlockPart(int dx, int dy, int dz, int radius, long seed) {
        double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
        
        // Multiple noise layers for organic feel
        double n1 = NoiseGenerator.getNoise(dx, dy, dz, seed, 0.1);
        double n2 = NoiseGenerator.getNoise(dx, dy, dz, seed + 100, 0.2);
        double n3 = NoiseGenerator.getNoise(dx, dy, dz, seed + 200, 0.4);
        
        double organic = n1 * 0.5 + n2 * 0.3 + n3 * 0.2;
        double deform = 1 + organic * 0.4;
        
        return distance <= radius * deform;
    }

    @Override
    public String getName() { 
        return "ORGANIC"; 
    }

    @Override
    public double getDeformationFactor() { 
        return 0.4; 
    }

    @Override
    public String getDisplayName() { 
        return "Organik"; 
    }
}
