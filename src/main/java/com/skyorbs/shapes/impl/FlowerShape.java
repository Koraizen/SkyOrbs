package com.skyorbs.shapes.impl;

import com.skyorbs.shapes.PlanetShape;

public class FlowerShape implements PlanetShape {

    @Override
    public boolean isBlockPart(int dx, int dy, int dz, int radius, long seed) {
        // Çiçek şekli - 3D flower
        double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
        if (distance > radius) return false;

        // Normalize coordinates
        double x = dx / (double)radius;
        double y = dy / (double)radius;
        double z = dz / (double)radius;

        // Flower formülü - taç yaprakları
        double angle = Math.atan2(z, x);
        double petalRadius = 1.0 + 0.5 * Math.cos(6 * angle); // 6 taç yaprağı
        double radialDistance = Math.sqrt(x*x + z*z);

        return radialDistance <= petalRadius * 0.8 && Math.abs(y) <= 0.6;
    }

    @Override
    public String getName() {
        return "FLOWER";
    }

    @Override
    public double getDeformationFactor() {
        return 0.5;
    }

    @Override
    public String getDisplayName() {
        return "Çiçek";
    }
}