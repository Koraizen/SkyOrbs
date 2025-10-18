package com.skyorbs.shapes.impl;

import com.skyorbs.shapes.PlanetShape;

/**
 * HONEYCOMB SHAPE - Bal peteği yapısı
 * Creates planets with hexagonal honeycomb patterns
 */
public class HoneycombShape implements PlanetShape {

    @Override
    public boolean isBlockPart(int dx, int dy, int dz, int radius, long seed) {
        double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
        
        if (distance > radius) return false;
        
        // Create hexagonal pattern
        double hexSize = 10.0;
        double qx = dx / hexSize;
        double qz = dz / hexSize;
        
        // Hexagonal grid coordinates
        double q = qx;
        double r = qz;
        
        // Snap to hex grid
        int hexQ = (int)Math.round(q);
        int hexR = (int)Math.round(r);
        
        // Distance to hex center
        double hexDist = Math.sqrt((q - hexQ) * (q - hexQ) + (r - hexR) * (r - hexR));
        
        // Create walls between hexagons
        if (hexDist > 0.4) return false;
        
        return distance <= radius;
    }

    @Override
    public String getName() { 
        return "HONEYCOMB"; 
    }

    @Override
    public double getDeformationFactor() { 
        return 0.0; 
    }

    @Override
    public String getDisplayName() { 
        return "Bal Peteği"; 
    }
}
