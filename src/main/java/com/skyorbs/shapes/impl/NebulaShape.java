package com.skyorbs.shapes.impl;

import com.skyorbs.shapes.PlanetShape;
import com.skyorbs.utils.NoiseGenerator;

/**
 * NEBULA SHAPE - Nebula bulutu
 * Creates wispy, cloud-like celestial bodies
 */
public class NebulaShape implements PlanetShape {

    @Override
    public boolean isBlockPart(int dx, int dy, int dz, int radius, long seed) {
        double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
        
        // Very wispy, cloud-like
        double n1 = NoiseGenerator.getNoise(dx, dy, dz, seed, 0.05);
        double n2 = NoiseGenerator.getNoise(dx, dy, dz, seed + 50, 0.1);
        double n3 = NoiseGenerator.getNoise(dx, dy, dz, seed + 100, 0.2);
        
        double nebula = Math.abs(n1) * 0.5 + Math.abs(n2) * 0.3 + Math.abs(n3) * 0.2;
        
        // Very irregular, sparse structure
        return distance <= radius * (0.7 + nebula * 0.8) && nebula > 0.3;
    }

    @Override
    public String getName() { 
        return "NEBULA"; 
    }

    @Override
    public double getDeformationFactor() { 
        return 0.8; 
    }

    @Override
    public String getDisplayName() { 
        return "Nebula"; 
    }
}
