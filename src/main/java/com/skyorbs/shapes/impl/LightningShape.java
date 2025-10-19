package com.skyorbs.shapes.impl;

import com.skyorbs.shapes.PlanetShape;

public class LightningShape implements PlanetShape {

    @Override
    public boolean isBlockPart(int dx, int dy, int dz, int radius, long seed) {
        // Yıldırım şekli - zigzag
        double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
        if (distance > radius) return false;

        // Normalize coordinates
        double x = dx / (double)radius;
        double y = dy / (double)radius;
        double z = dz / (double)radius;

        // Lightning bolt formülü - zigzag pattern
        double lightningPath = Math.abs(x - Math.sin(y * 10) * 0.3);
        return lightningPath <= 0.2 && Math.abs(z) <= 0.1;
    }

    @Override
    public String getName() {
        return "LIGHTNING";
    }

    @Override
    public double getDeformationFactor() {
        return 0.9;
    }

    @Override
    public String getDisplayName() {
        return "Yıldırım";
    }
}