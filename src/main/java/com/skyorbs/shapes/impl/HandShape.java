package com.skyorbs.shapes.impl;

import com.skyorbs.shapes.PlanetShape;

public class HandShape implements PlanetShape {

    @Override
    public boolean isBlockPart(int dx, int dy, int dz, int radius, long seed) {
        // El şekli - palm, fingers
        double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
        if (distance > radius) return false;

        // Normalize coordinates
        double x = dx / (double)radius;
        double y = dy / (double)radius;
        double z = dz / (double)radius;

        // Palm (avuç içi)
        if (y > -0.3 && y < 0.2) {
            return Math.sqrt(x*x + z*z) <= 0.4;
        }

        // Thumb (baş parmak)
        if (x < -0.1 && x > -0.4 && y > 0.0 && y < 0.5) {
            return Math.abs(z) <= 0.15;
        }

        // Fingers (diğer parmaklar)
        for (int i = 0; i < 4; i++) {
            double fingerX = -0.3 + i * 0.2;
            if (Math.abs(x - fingerX) <= 0.08 && y > 0.1 && y < 0.6) {
                return Math.abs(z) <= 0.12;
            }
        }

        return false;
    }

    @Override
    public String getName() {
        return "HAND";
    }

    @Override
    public double getDeformationFactor() {
        return 0.8;
    }

    @Override
    public String getDisplayName() {
        return "El";
    }
}