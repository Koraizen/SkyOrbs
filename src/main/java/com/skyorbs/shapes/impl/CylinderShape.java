package com.skyorbs.shapes.impl;

import com.skyorbs.shapes.PlanetShape;

public class CylinderShape implements PlanetShape {
    
    @Override
    public boolean isBlockPart(int dx, int dy, int dz, int radius, long seed) {
        double distance = Math.sqrt(dx * dx + dz * dz);
        int height = radius;
        return distance <= radius && Math.abs(dy) <= height;
    }
    
    @Override
    public String getName() {
        return "CYLINDER";
    }
    
    @Override
    public double getDeformationFactor() {
        return 0.0;
    }
    
    @Override
    public String getDisplayName() {
        return "Silindir";
    }
}
