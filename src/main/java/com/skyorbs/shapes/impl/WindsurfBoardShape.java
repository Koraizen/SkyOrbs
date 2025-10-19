package com.skyorbs.shapes.impl;

import com.skyorbs.shapes.PlanetShape;

public class WindsurfBoardShape implements PlanetShape {

    @Override
    public boolean isBlockPart(int dx, int dy, int dz, int radius, long seed) {
        // Rüzgar sörfü tahtası şekli - board, mast, sail
        double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
        if (distance > radius) return false;

        // Normalize coordinates
        double x = dx / (double)radius;
        double y = dy / (double)radius;
        double z = dz / (double)radius;

        // Main board (ana tahta)
        if (Math.abs(y) <= 0.03) {
            // Wider at back, narrower at front
            double width = 0.25 - x * 0.1;
            return Math.abs(z) <= width && x >= -0.6 && x <= 0.4;
        }

        // Mast (direk)
        if (Math.abs(x + 0.2) <= 0.03 && Math.abs(z) <= 0.03 && y >= 0.03 && y <= 0.8) {
            return true;
        }

        // Sail (yelken)
        if (y >= 0.3 && y <= 0.7) {
            // Main sail body
            if (Math.abs(x + 0.1) <= 0.02 && Math.abs(z) <= 0.3) return true;
            // Sail curve
            double sailCurve = Math.sin((y - 0.3) * Math.PI / 0.4) * 0.1;
            if (Math.abs(x - sailCurve) <= 0.02 && Math.abs(z) <= 0.3) return true;
        }

        // Boom (boom)
        if (y >= 0.2 && y <= 0.25 && Math.abs(z) <= 0.35) {
            return Math.abs(x + 0.1) <= 0.02;
        }

        // Daggerboard (sürekli)
        if (Math.abs(x) <= 0.02 && Math.abs(z) <= 0.02 && y <= -0.05) {
            return true;
        }

        return false;
    }

    @Override
    public String getName() {
        return "WINDSURF_BOARD";
    }

    @Override
    public double getDeformationFactor() {
        return 0.8;
    }

    @Override
    public String getDisplayName() {
        return "Rüzgar Sörfü Tahtası";
    }
}