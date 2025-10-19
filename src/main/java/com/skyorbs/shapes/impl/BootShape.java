package com.skyorbs.shapes.impl;

import com.skyorbs.shapes.PlanetShape;

public class BootShape implements PlanetShape {

    @Override
    public boolean isBlockPart(int dx, int dy, int dz, int radius, long seed) {
        // Bot şekli - sole, shaft, heel
        double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
        if (distance > radius) return false;

        // Normalize coordinates
        double x = dx / (double)radius;
        double y = dy / (double)radius;
        double z = dz / (double)radius;

        // Boot shaft (üst kısım)
        if (y > 0.0) {
            return Math.abs(x) <= 0.3 && Math.abs(z) <= 0.2;
        }

        // Boot body (orta kısım)
        if (y > -0.4 && y <= 0.0) {
            double bodyWidth = 0.4 - y * 0.2; // Aşağıda genişler
            return Math.abs(x) <= bodyWidth && Math.abs(z) <= 0.25;
        }

        // Boot sole (taban)
        if (y <= -0.4) {
            return Math.abs(x) <= 0.5 && Math.abs(z) <= 0.3;
        }

        // Heel (topuk)
        if (x < -0.2 && y < -0.2 && Math.abs(z) <= 0.2) {
            return true;
        }

        return false;
    }

    @Override
    public String getName() {
        return "BOOT";
    }

    @Override
    public double getDeformationFactor() {
        return 0.6;
    }

    @Override
    public String getDisplayName() {
        return "Bot";
    }
}