package com.skyorbs.shapes.impl;

import com.skyorbs.shapes.PlanetShape;

public class HatShape implements PlanetShape {

    @Override
    public boolean isBlockPart(int dx, int dy, int dz, int radius, long seed) {
        // Şapka şekli - brim, crown
        double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
        if (distance > radius) return false;

        // Normalize coordinates
        double x = dx / (double)radius;
        double y = dy / (double)radius;
        double z = dz / (double)radius;

        // Hat brim (kenar)
        if (y > -0.1 && y < 0.1) {
            double brimRadius = Math.sqrt(x*x + z*z);
            return brimRadius <= 0.9;
        }

        // Hat crown (üst kısım)
        if (y >= 0.1 && y < 0.6) {
            double crownRadius = 0.5 - (y - 0.1) * 0.3; // Yukarıda daralır
            return Math.sqrt(x*x + z*z) <= crownRadius;
        }

        return false;
    }

    @Override
    public String getName() {
        return "HAT";
    }

    @Override
    public double getDeformationFactor() {
        return 0.4;
    }

    @Override
    public String getDisplayName() {
        return "Şapka";
    }
}