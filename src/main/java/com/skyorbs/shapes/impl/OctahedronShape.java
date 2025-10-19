package com.skyorbs.shapes.impl;

import com.skyorbs.shapes.PlanetShape;

public class OctahedronShape implements PlanetShape {
    
    @Override
    public boolean isBlockPart(int dx, int dy, int dz, int radius, long seed) {
        int manhattan = Math.abs(dx) + Math.abs(dy) + Math.abs(dz);
        return manhattan <= radius;
    }
    
    @Override
    public String getName() {
        return "OCTAHEDRON";
    }
    
    @Override
    public double getDeformationFactor() {
        return 0.0;
    }
    
    @Override
    public String getDisplayName() {
        return "Sekizyüzlü";
    }
}
