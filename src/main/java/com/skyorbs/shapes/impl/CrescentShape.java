package com.skyorbs.shapes.impl;

import com.skyorbs.shapes.PlanetShape;

public class CrescentShape implements PlanetShape {
    
    @Override
    public boolean isBlockPart(int dx, int dy, int dz, int radius, long seed) {
        double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
        
        if (distance > radius) {
            return false;
        }
        
        int offsetX = (int) (radius * 0.4);
        double cutDistance = Math.sqrt((dx - offsetX) * (dx - offsetX) + dy * dy + dz * dz);
        
        return cutDistance >= radius * 0.7;
    }
    
    @Override
    public String getName() {
        return "CRESCENT";
    }
    
    @Override
    public double getDeformationFactor() {
        return 0.0;
    }
    
    @Override
    public String getDisplayName() {
        return "Hilal";
    }
}
