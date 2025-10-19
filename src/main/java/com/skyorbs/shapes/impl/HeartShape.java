package com.skyorbs.shapes.impl;

import com.skyorbs.shapes.PlanetShape;

public class HeartShape implements PlanetShape {

    @Override
    public boolean isBlockPart(int dx, int dy, int dz, int radius, long seed) {
        // Kalp şekli - 3D kalp formülü
        double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
        if (distance > radius) return false;

        // Normalize coordinates
        double x = dx / (double)radius;
        double y = dy / (double)radius;
        double z = dz / (double)radius;

        // 3D kalp formülü
        double heart = Math.pow(x*x + 2.25*y*y + z*z - 1, 3) - x*x*z*z*z;
        return heart <= 0;
    }

    @Override
    public String getName() {
        return "HEART";
    }

    @Override
    public double getDeformationFactor() {
        return 0.4;
    }

    @Override
    public String getDisplayName() {
        return "Kalp";
    }
}