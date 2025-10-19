package com.skyorbs.shapes.impl;

import com.skyorbs.shapes.PlanetShape;

public class ParagliderShape implements PlanetShape {

    @Override
    public boolean isBlockPart(int dx, int dy, int dz, int radius, long seed) {
        // Yamaç paraşütü şekli - wing, lines, harness
        double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
        if (distance > radius) return false;

        // Normalize coordinates
        double x = dx / (double)radius;
        double y = dy / (double)radius;
        double z = dz / (double)radius;

        // Wing canopy (kanopi)
        if (y > 0.2) {
            double wingWidth = 0.8 - (y - 0.2) * 0.6;
            double wingLength = 0.6;
            return Math.abs(x) <= wingLength && Math.abs(z) <= wingWidth;
        }

        // Leading edge (ön kenar)
        if (Math.abs(y - 0.25) <= 0.05) {
            return Math.abs(x) <= 0.6 && Math.abs(z) <= 0.8;
        }

        // Lines (halatlar)
        if (y <= 0.2 && y >= -0.3) {
            double lineSpacing = 0.1;
            for (int i = -3; i <= 3; i++) {
                double lineX = i * lineSpacing;
                if (Math.abs(x - lineX) <= 0.01 && Math.abs(z) <= 0.01) return true;
            }
        }

        // Harness/carabiners (kask/çengeller)
        if (y <= -0.3 && y >= -0.5) {
            return Math.abs(x) <= 0.15 && Math.abs(z) <= 0.1;
        }

        // Speed bar (hız çubuğu)
        if (y <= -0.1 && y >= -0.15) {
            return Math.abs(x) <= 0.4 && Math.abs(z) <= 0.02;
        }

        return false;
    }

    @Override
    public String getName() {
        return "PARAGLIDER";
    }

    @Override
    public double getDeformationFactor() {
        return 0.8;
    }

    @Override
    public String getDisplayName() {
        return "Yamaç Paraşütü";
    }
}