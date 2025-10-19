package com.skyorbs.shapes.impl;

import com.skyorbs.shapes.PlanetShape;

/**
 * SPIRAL SHAPE - Spiral gezegen
 * Creates planets with spiral patterns
 */
public class SpiralShape implements PlanetShape {

    @Override
    public boolean isBlockPart(int dx, int dy, int dz, int radius, long seed) {
        double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
        
        if (distance > radius) return false;
        
        // Create spiral pattern
        double angle = Math.atan2(dz, dx);
        double spiralFactor = Math.sin(angle * 4 + dy * 0.3) * 0.2;
        
        return distance <= radius * (1 + spiralFactor);
    }

    @Override
    public String getName() { 
        return "SPIRAL"; 
    }

    @Override
    public double getDeformationFactor() { 
        return 0.2; 
    }

    @Override
    public String getDisplayName() { 
        return "Spiral"; 
    }
}
