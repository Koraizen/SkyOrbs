package com.skyorbs.shapes.impl;

import com.skyorbs.shapes.PlanetShape;

public class HouseShape implements PlanetShape {

    @Override
    public boolean isBlockPart(int dx, int dy, int dz, int radius, long seed) {
        // Ev şekli - walls, roof, door, windows
        double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
        if (distance > radius) return false;

        // Normalize coordinates
        double x = dx / (double)radius;
        double y = dy / (double)radius;
        double z = dz / (double)radius;

        // House base (temel)
        if (y < -0.3) {
            return Math.abs(x) <= 0.6 && Math.abs(z) <= 0.6;
        }

        // House walls (duvarlar)
        if (y >= -0.3 && y < 0.3) {
            return Math.abs(x) <= 0.5 && Math.abs(z) <= 0.5;
        }

        // House roof (çatı)
        if (y >= 0.3 && y < 0.7) {
            double roofWidth = 0.5 - (y - 0.3) * 0.5; // Çatı yukarıda daralır
            return Math.abs(x) <= roofWidth && Math.abs(z) <= roofWidth;
        }

        // Door (kapı)
        if (Math.abs(x) <= 0.1 && y >= -0.3 && y < 0.1 && z > 0.4) {
            return false; // Kapı boşluğu
        }

        // Windows (pencereler)
        if (y > -0.1 && y < 0.2) {
            // Front windows
            if (z > 0.3 && Math.abs(x - 0.25) <= 0.08) return false;
            if (z > 0.3 && Math.abs(x + 0.25) <= 0.08) return false;
            // Side windows
            if (Math.abs(z) <= 0.08 && Math.abs(x - 0.35) <= 0.08) return false;
            if (Math.abs(z) <= 0.08 && Math.abs(x + 0.35) <= 0.08) return false;
        }

        return true;
    }

    @Override
    public String getName() {
        return "HOUSE";
    }

    @Override
    public double getDeformationFactor() {
        return 0.5;
    }

    @Override
    public String getDisplayName() {
        return "Ev";
    }
}