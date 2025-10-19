package com.skyorbs.shapes.impl;

import com.skyorbs.shapes.PlanetShape;

public class CometShape implements PlanetShape {
    
    @Override
    public boolean isBlockPart(int dx, int dy, int dz, int radius, long seed) {
        double headDistance = Math.sqrt(dx * dx + dy * dy + dz * dz);
        
        if (headDistance <= radius * 0.6) {
            return true;
        }
        
        if (dx > 0) {
            double tailLength = radius * 2;
            double tailWidth = radius * 0.3 * (1 - dx / tailLength);
            
            if (dx <= tailLength) {
                double crossDist = Math.sqrt(dy * dy + dz * dz);
                return crossDist <= tailWidth;
            }
        }
        
        return false;
    }
    
    @Override
    public String getName() {
        return "COMET";
    }
    
    @Override
    public double getDeformationFactor() {
        return 0.2;
    }
    
    @Override
    public String getDisplayName() {
        return "Kuyruklu Yıldız";
    }
}
