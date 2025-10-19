package com.skyorbs.shapes.impl;

import com.skyorbs.shapes.PlanetShape;

public class ShellShape implements PlanetShape {

    @Override
    public boolean isBlockPart(int dx, int dy, int dz, int radius, long seed) {
        // Deniz kabuğu şekli - spiral
        double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
        if (distance > radius) return false;

        // Normalize coordinates
        double x = dx / (double)radius;
        double y = dy / (double)radius;
        double z = dz / (double)radius;

        // Spiral shell formülü
        double angle = Math.atan2(z, x);
        double radiusAtAngle = Math.exp(angle * 0.3) * 0.8; // Spiral genişleme
        double radialDistance = Math.sqrt(x*x + z*z);

        return radialDistance <= radiusAtAngle && Math.abs(y) <= 0.4;
    }

    @Override
    public String getName() {
        return "SHELL";
    }

    @Override
    public double getDeformationFactor() {
        return 0.7;
    }

    @Override
    public String getDisplayName() {
        return "Kabuk";
    }
}