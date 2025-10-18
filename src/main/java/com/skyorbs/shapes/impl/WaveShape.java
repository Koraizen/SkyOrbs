package com.skyorbs.shapes.impl;

import com.skyorbs.shapes.PlanetShape;

/**
 * WAVE SHAPE - Dalgalı gezegen
 * Creates planets with wave-like surface patterns
 */
public class WaveShape implements PlanetShape {

    @Override
    public boolean isBlockPart(int dx, int dy, int dz, int radius, long seed) {
        double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
        
        // Create wave pattern
        double wave1 = Math.sin(dx * 0.2) * Math.cos(dz * 0.2);
        double wave2 = Math.sin(dy * 0.15 + dx * 0.1);
        double waveDeform = 1 + (wave1 + wave2) * 0.15;
        
        return distance <= radius * waveDeform;
    }

    @Override
    public String getName() { 
        return "WAVE"; 
    }

    @Override
    public double getDeformationFactor() { 
        return 0.15; 
    }

    @Override
    public String getDisplayName() { 
        return "Dalgalı"; 
    }
}
