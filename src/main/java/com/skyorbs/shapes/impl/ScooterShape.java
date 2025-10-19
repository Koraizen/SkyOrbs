package com.skyorbs.shapes.impl;

import com.skyorbs.shapes.PlanetShape;

public class ScooterShape implements PlanetShape {

    @Override
    public boolean isBlockPart(int dx, int dy, int dz, int radius, long seed) {
        // Scooter şekli - platform, handlebar, wheel
        double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
        if (distance > radius) return false;

        // Normalize coordinates
        double x = dx / (double)radius;
        double y = dy / (double)radius;
        double z = dz / (double)radius;

        // Main platform (ana platform)
        if (Math.abs(y) <= 0.05) {
            return Math.abs(x) <= 0.3 && Math.abs(z) <= 0.2;
        }

        // Handlebar post (direksiyon direği)
        if (Math.abs(x - 0.25) <= 0.03 && Math.abs(z) <= 0.03 && y >= 0.05 && y <= 0.25) {
            return true;
        }

        // Handlebars (direksiyon)
        if (y >= 0.25 && y <= 0.3 && Math.abs(x - 0.25) <= 0.08) {
            return Math.abs(z) <= 0.15;
        }

        // Single wheel (tek tekerlek)
        double wheelDist = Math.sqrt(x*x + (y + 0.1)*(y + 0.1));
        if (wheelDist <= 0.12) return true;

        // Footrests (ayak dayama)
        if (Math.abs(y + 0.02) <= 0.03 && Math.abs(z) <= 0.25 && Math.abs(x) <= 0.15) {
            return true;
        }

        return false;
    }

    @Override
    public String getName() {
        return "SCOOTER";
    }

    @Override
    public double getDeformationFactor() {
        return 0.6;
    }

    @Override
    public String getDisplayName() {
        return "Scooter";
    }
}