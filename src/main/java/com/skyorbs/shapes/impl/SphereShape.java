package com.skyorbs.shapes.impl;

import com.skyorbs.shapes.PlanetShape;

public class SphereShape implements PlanetShape {
    
    @Override
    public boolean isBlockPart(int dx, int dy, int dz, int radius, long seed) {
        return dx * dx + dy * dy + dz * dz <= radius * radius;
    }
    
    @Override
    public String getName() {
        return "SPHERE";
    }
    
    @Override
    public double getDeformationFactor() {
        return 0.0;
    }
    
    @Override
    public String getDisplayName() {
        return "Küre";
    }
}
