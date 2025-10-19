package com.skyorbs.shapes.impl;

import com.skyorbs.shapes.PlanetShape;

public class RingShape implements PlanetShape {
    
    @Override
    public boolean isBlockPart(int dx, int dy, int dz, int radius, long seed) {
        int thickness = 3;
        double distance = Math.sqrt(dx * dx + dz * dz);
        return Math.abs(dy) <= thickness && distance <= radius;
    }
    
    @Override
    public String getName() {
        return "RING";
    }
    
    @Override
    public double getDeformationFactor() {
        return 0.0;
    }
    
    @Override
    public String getDisplayName() {
        return "Halka";
    }
}
