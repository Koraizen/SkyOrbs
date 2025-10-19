package com.skyorbs.shapes.impl;

import com.skyorbs.shapes.PlanetShape;

public class GuitarShape implements PlanetShape {

    @Override
    public boolean isBlockPart(int dx, int dy, int dz, int radius, long seed) {
        // Gitar şekli - body, neck, headstock
        double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
        if (distance > radius) return false;

        // Normalize coordinates
        double x = dx / (double)radius;
        double y = dy / (double)radius;
        double z = dz / (double)radius;

        // Guitar body - oval shape
        if (y < 0.2) {
            double bodyRadius = Math.sqrt(x*x + (y+0.3)*(y+0.3));
            if (bodyRadius <= 0.5) return true;
        }

        // Guitar neck - long thin rectangle
        if (y >= 0.2 && y <= 0.8 && Math.abs(x) <= 0.1) {
            return Math.abs(z) <= 0.05;
        }

        // Headstock - wider at top
        if (y > 0.8) {
            return Math.abs(x) <= 0.2 && Math.abs(z) <= 0.05;
        }

        // Strings (thin lines)
        if (Math.abs(z) <= 0.02 && y >= 0.2) {
            return Math.abs(x) <= 0.08;
        }

        return false;
    }

    @Override
    public String getName() {
        return "GUITAR";
    }

    @Override
    public double getDeformationFactor() {
        return 0.8;
    }

    @Override
    public String getDisplayName() {
        return "Gitar";
    }
}