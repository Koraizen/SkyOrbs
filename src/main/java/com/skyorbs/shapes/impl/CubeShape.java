package com.skyorbs.shapes.impl;

import com.skyorbs.shapes.PlanetShape;

public class CubeShape implements PlanetShape {
    
    @Override
    public boolean isBlockPart(int dx, int dy, int dz, int radius, long seed) {
        return Math.abs(dx) <= radius && Math.abs(dy) <= radius && Math.abs(dz) <= radius;
    }
    
    @Override
    public String getName() {
        return "CUBE";
    }
    
    @Override
    public double getDeformationFactor() {
        return 0.0;
    }
    
    @Override
    public String getDisplayName() {
        return "Küp";
    }
}
