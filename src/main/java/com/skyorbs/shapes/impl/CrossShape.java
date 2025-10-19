package com.skyorbs.shapes.impl;

import com.skyorbs.shapes.PlanetShape;

public class CrossShape implements PlanetShape {

    @Override
    public boolean isBlockPart(int dx, int dy, int dz, int radius, long seed) {
        // Haç şekli - 3D cross
        double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
        if (distance > radius) return false;

        // Normalize coordinates
        double x = Math.abs(dx) / (double)radius;
        double y = Math.abs(dy) / (double)radius;
        double z = Math.abs(dz) / (double)radius;

        // Cross formülü - üç eksende geniş
        double thickness = 0.3; // Haç kalınlığı
        return (x <= thickness && y <= 1.0 && z <= 1.0) ||
               (x <= 1.0 && y <= thickness && z <= 1.0) ||
               (x <= 1.0 && y <= 1.0 && z <= thickness);
    }

    @Override
    public String getName() {
        return "CROSS";
    }

    @Override
    public double getDeformationFactor() {
        return 0.6;
    }

    @Override
    public String getDisplayName() {
        return "Haç";
    }
}