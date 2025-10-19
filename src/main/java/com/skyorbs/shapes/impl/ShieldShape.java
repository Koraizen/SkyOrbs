package com.skyorbs.shapes.impl;

import com.skyorbs.shapes.PlanetShape;

public class ShieldShape implements PlanetShape {

    @Override
    public boolean isBlockPart(int dx, int dy, int dz, int radius, long seed) {
        // Kalkan şekli - oval base, pointed top
        double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
        if (distance > radius) return false;

        // Normalize coordinates
        double x = dx / (double)radius;
        double y = dy / (double)radius;
        double z = dz / (double)radius;

        // Shield shape - wider at bottom, pointed at top
        double shieldWidth = 0.8 - y * 0.3; // Bottom'da daha geniş
        double shieldHeight = 1.0;

        // Main shield body
        if (Math.abs(x) <= shieldWidth && y >= -shieldHeight && y <= 0) {
            return Math.abs(z) <= 0.1; // Thin shield
        }

        // Shield boss (merkez çıkıntı)
        double bossDistance = Math.sqrt(x*x + (y+0.3)*(y+0.3));
        if (bossDistance <= 0.15) {
            return true;
        }

        return false;
    }

    @Override
    public String getName() {
        return "SHIELD";
    }

    @Override
    public double getDeformationFactor() {
        return 0.4;
    }

    @Override
    public String getDisplayName() {
        return "Kalkan";
    }
}