package com.skyorbs.shapes.impl;

import com.skyorbs.shapes.PlanetShape;

public class PyramidShape implements PlanetShape {
    
    @Override
    public boolean isBlockPart(int dx, int dy, int dz, int radius, long seed) {
        if (dy < 0 || dy > radius) {
            return false;
        }
        
        int levelSize = radius - dy;
        return Math.abs(dx) <= levelSize && Math.abs(dz) <= levelSize;
    }
    
    @Override
    public String getName() {
        return "PYRAMID";
    }
    
    @Override
    public double getDeformationFactor() {
        return 0.0;
    }
    
    @Override
    public String getDisplayName() {
        return "Piramit";
    }
}
