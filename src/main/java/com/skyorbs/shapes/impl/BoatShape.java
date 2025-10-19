package com.skyorbs.shapes.impl;

import com.skyorbs.shapes.PlanetShape;

public class BoatShape implements PlanetShape {

    @Override
    public boolean isBlockPart(int dx, int dy, int dz, int radius, long seed) {
        // Tekne şekli - hull, deck, mast, sails
        double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
        if (distance > radius) return false;

        // Normalize coordinates
        double x = dx / (double)radius;
        double y = dy / (double)radius;
        double z = dz / (double)radius;

        // Boat hull (gövde) - V şeklinde
        if (y < 0.0) {
            double hullWidth = 0.6 + y * 0.4; // Aşağıda genişler
            return Math.abs(z) <= hullWidth;
        }

        // Boat deck (güverte)
        if (y >= 0.0 && y < 0.2) {
            return Math.abs(z) <= 0.5;
        }

        // Mast (dikey direk)
        if (Math.abs(z) <= 0.05 && y >= 0.2 && y < 0.8) {
            return Math.abs(x) <= 0.05;
        }

        // Sails (yelkenler)
        if (y >= 0.3 && y < 0.7) {
            // Main sail
            if (Math.abs(z) <= 0.3 && Math.abs(x - 0.1) <= 0.02) return true;
            // Jib sail
            if (Math.abs(z) <= 0.2 && Math.abs(x + 0.1) <= 0.02) return true;
        }

        return false;
    }

    @Override
    public String getName() {
        return "BOAT";
    }

    @Override
    public double getDeformationFactor() {
        return 0.8;
    }

    @Override
    public String getDisplayName() {
        return "Tekne";
    }
}