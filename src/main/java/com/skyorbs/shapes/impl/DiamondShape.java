package com.skyorbs.shapes.impl;

import com.skyorbs.shapes.PlanetShape;

public class DiamondShape implements PlanetShape {

    @Override
    public boolean isBlockPart(int dx, int dy, int dz, int radius, long seed) {
        // Elmas şekli - octahedron
        double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
        if (distance > radius) return false;

        // Normalize coordinates
        double x = Math.abs(dx) / (double)radius;
        double y = Math.abs(dy) / (double)radius;
        double z = Math.abs(dz) / (double)radius;

        // Diamond shape - distance from center in octahedron
        return x + y + z <= 1.0;
    }

    @Override
    public String getName() {
        return "DIAMOND";
    }

    @Override
    public double getDeformationFactor() {
        return 0.3;
    }

    @Override
    public String getDisplayName() {
        return "Elmas";
    }
}
