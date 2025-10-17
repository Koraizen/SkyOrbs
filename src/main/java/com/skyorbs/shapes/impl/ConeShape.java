package com.skyorbs.shapes.impl;

import com.skyorbs.shapes.PlanetShape;

public class ConeShape implements PlanetShape {
    
    @Override
    public boolean isBlockPart(int dx, int dy, int dz, int radius, long seed) {
        double distance = Math.sqrt(dx * dx + dz * dz);
        int height = radius;
        
        if (dy < -height || dy > height) {
            return false;
        }
        
        double factor = 1.0 - (Math.abs(dy) / (double) height);
        return distance <= radius * factor;
    }
    
    @Override
    public String getName() {
        return "CONE";
    }
    
    @Override
    public double getDeformationFactor() {
        return 0.0;
    }
    
    @Override
    public String getDisplayName() {
        return "Koni";
    }
}
