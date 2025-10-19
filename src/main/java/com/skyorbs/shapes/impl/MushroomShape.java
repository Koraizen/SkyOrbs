package com.skyorbs.shapes.impl;

import com.skyorbs.shapes.PlanetShape;

public class MushroomShape implements PlanetShape {

    @Override
    public boolean isBlockPart(int dx, int dy, int dz, int radius, long seed) {
        // Mantar şekli - şapka ve sap
        double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
        if (distance > radius) return false;

        // Normalize coordinates
        double x = dx / (double)radius;
        double y = dy / (double)radius;
        double z = dz / (double)radius;

        // Mushroom cap (üst kısım geniş)
        if (y > 0) {
            double capRadius = 1.0 - y * 0.5; // Yukarıda daha geniş
            return Math.sqrt(x*x + z*z) <= capRadius;
        }
        // Mushroom stem (alt kısım dar)
        else {
            double stemRadius = 0.3;
            return Math.sqrt(x*x + z*z) <= stemRadius;
        }
    }

    @Override
    public String getName() {
        return "MUSHROOM";
    }

    @Override
    public double getDeformationFactor() {
        return 0.6;
    }

    @Override
    public String getDisplayName() {
        return "Mantar";
    }
}