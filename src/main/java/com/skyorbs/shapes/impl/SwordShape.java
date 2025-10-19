package com.skyorbs.shapes.impl;

import com.skyorbs.shapes.PlanetShape;

public class SwordShape implements PlanetShape {

    @Override
    public boolean isBlockPart(int dx, int dy, int dz, int radius, long seed) {
        // Kılıç şekli - kabza, crossguard, blade
        double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
        if (distance > radius) return false;

        // Normalize coordinates
        double x = dx / (double)radius;
        double y = dy / (double)radius;
        double z = dz / (double)dz;

        // Sword parts
        if (y < -0.6) {
            // Handle (kabza) - kalın
            return Math.abs(x) <= 0.15 && Math.abs(z) <= 0.15;
        } else if (y < -0.4) {
            // Crossguard (koruyucu) - geniş
            return Math.abs(x) <= 0.4 && Math.abs(z) <= 0.1;
        } else {
            // Blade (kılıç ağzı) - uzun ve ince
            double bladeWidth = 0.1 - (y + 0.4) * 0.05; // Uçta daha ince
            return Math.abs(x) <= bladeWidth && Math.abs(z) <= 0.05;
        }
    }

    @Override
    public String getName() {
        return "SWORD";
    }

    @Override
    public double getDeformationFactor() {
        return 0.9;
    }

    @Override
    public String getDisplayName() {
        return "Kılıç";
    }
}