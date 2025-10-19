package com.skyorbs.shapes.impl;

import com.skyorbs.shapes.PlanetShape;

/**
 * GEOMETRIC SHAPE - Geometrik şekil (icosphere benzeri)
 * Creates planets with geometric, polyhedron-like appearance
 */
public class GeometricShape implements PlanetShape {

    @Override
    public boolean isBlockPart(int dx, int dy, int dz, int radius, long seed) {
        double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
        
        // Create geodesic-like pattern
        double ax = Math.abs(dx);
        double ay = Math.abs(dy);
        double az = Math.abs(dz);
        
        // Blend between sphere and polyhedron
        double sphereVal = distance;
        double polyVal = (ax + ay + az) * 0.7;
        
        double blended = sphereVal * 0.6 + polyVal * 0.4;
        
        return blended <= radius;
    }

    @Override
    public String getName() { 
        return "GEOMETRIC"; 
    }

    @Override
    public double getDeformationFactor() { 
        return 0.0; 
    }

    @Override
    public String getDisplayName() { 
        return "Geometrik"; 
    }
}
