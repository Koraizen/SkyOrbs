package com.skyorbs.shapes.impl;

import com.skyorbs.shapes.PlanetShape;

public class TeapotShape implements PlanetShape {

    @Override
    public boolean isBlockPart(int dx, int dy, int dz, int radius, long seed) {
        // Çaydanlık şekli - body, spout, handle, lid
        double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
        if (distance > radius) return false;

        // Normalize coordinates
        double x = dx / (double)radius;
        double y = dy / (double)radius;
        double z = dz / (double)radius;

        // Teapot body - spherical bottom, cylindrical top
        if (y < 0.2) {
            double bodyRadius = Math.sqrt(x*x + z*z);
            return bodyRadius <= 0.5;
        }

        // Teapot neck (boyun)
        if (y >= 0.2 && y < 0.6) {
            return Math.abs(x) <= 0.15 && Math.abs(z) <= 0.15;
        }

        // Spout (hortum)
        if (z > 0.1 && z < 0.5 && Math.abs(y - 0.3) <= 0.1) {
            return Math.abs(x) <= 0.1;
        }

        // Handle (kulak)
        if (z < -0.1 && z > -0.4 && Math.abs(y - 0.3) <= 0.1) {
            return Math.abs(x) <= 0.1;
        }

        // Lid (kapak)
        if (y >= 0.6) {
            double lidRadius = Math.sqrt(x*x + z*z);
            return lidRadius <= 0.2;
        }

        return false;
    }

    @Override
    public String getName() {
        return "TEAPOT";
    }

    @Override
    public double getDeformationFactor() {
        return 0.7;
    }

    @Override
    public String getDisplayName() {
        return "Çaydanlık";
    }
}