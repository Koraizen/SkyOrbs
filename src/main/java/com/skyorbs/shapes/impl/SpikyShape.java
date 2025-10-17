package com.skyorbs.shapes.impl;

import com.skyorbs.shapes.PlanetShape;

public class SpikyShape implements PlanetShape {
    
    @Override
    public boolean isBlockPart(int dx, int dy, int dz, int radius, long seed) {
        double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
        
        double angle = Math.atan2(dz, dx);
        double elevation = Math.atan2(dy, Math.sqrt(dx * dx + dz * dz));
        
        double spikePattern = Math.sin(angle * 6) * Math.cos(elevation * 4);
        double spikeExtension = 1 + 0.3 * Math.max(0, spikePattern);
        
        return distance <= radius * spikeExtension;
    }
    
    @Override
    public String getName() {
        return "SPIKY";
    }
    
    @Override
    public double getDeformationFactor() {
        return 0.3;
    }
    
    @Override
    public String getDisplayName() {
        return "Dikenli";
    }
}
