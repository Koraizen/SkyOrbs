package com.skyorbs.shapes.impl;

import com.skyorbs.shapes.PlanetShape;

import java.util.Random;

/**
 * CRATERED SHAPE - Kraterli gezegen
 * Creates planets with impact craters on the surface
 */
public class CrateredShape implements PlanetShape {

    @Override
    public boolean isBlockPart(int dx, int dy, int dz, int radius, long seed) {
        double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
        
        if (distance > radius) return false;
        
        // Create craters using position-based random
        Random random = new Random(seed + dx * 73856093 + dy * 19349663 + dz * 83492791);
        
        // Crater chance based on surface proximity
        double surfaceProximity = Math.abs(distance - radius) / radius;
        
        if (surfaceProximity < 0.1 && random.nextDouble() < 0.3) {
            // This is a crater - carve inward
            double craterDepth = random.nextDouble() * 5;
            return distance <= radius - craterDepth;
        }
        
        return distance <= radius;
    }

    @Override
    public String getName() { 
        return "CRATERED"; 
    }

    @Override
    public double getDeformationFactor() { 
        return 0.3; 
    }

    @Override
    public String getDisplayName() { 
        return "Kraterli"; 
    }
}
