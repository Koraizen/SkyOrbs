package com.skyorbs.shapes.impl;

import com.skyorbs.shapes.PlanetShape;

import java.util.Random;

public class EllipsoidShape implements PlanetShape {
    
    @Override
    public boolean isBlockPart(int dx, int dy, int dz, int radius, long seed) {
        Random rand = new Random(seed);
        double rx = radius * (0.8 + 0.4 * rand.nextDouble());
        double ry = radius * (0.8 + 0.4 * rand.nextDouble());
        double rz = radius * (0.8 + 0.4 * rand.nextDouble());
        
        double val = (dx * dx) / (rx * rx) + (dy * dy) / (ry * ry) + (dz * dz) / (rz * rz);
        return val <= 1.0;
    }
    
    @Override
    public String getName() {
        return "ELLIPSOID";
    }
    
    @Override
    public double getDeformationFactor() {
        return 0.2;
    }
    
    @Override
    public String getDisplayName() {
        return "Elips";
    }
}
