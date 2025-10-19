package com.skyorbs.shapes.impl;

import com.skyorbs.shapes.PlanetShape;

public class TorusShape implements PlanetShape {
    
    @Override
    public boolean isBlockPart(int dx, int dy, int dz, int radius, long seed) {
        double majorRadius = radius * 0.7;
        double minorRadius = radius * 0.3;
        
        double horizontalDist = Math.sqrt(dx * dx + dz * dz);
        double torusCenterDist = Math.sqrt((horizontalDist - majorRadius) * (horizontalDist - majorRadius) + dy * dy);
        
        return torusCenterDist <= minorRadius;
    }
    
    @Override
    public String getName() {
        return "TORUS";
    }
    
    @Override
    public double getDeformationFactor() {
        return 0.0;
    }
    
    @Override
    public String getDisplayName() {
        return "Çörek";
    }
}
