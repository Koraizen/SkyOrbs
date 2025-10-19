package com.skyorbs.shapes.impl;

import com.skyorbs.shapes.PlanetShape;

public class ButterflyShape implements PlanetShape {

    @Override
    public boolean isBlockPart(int dx, int dy, int dz, int radius, long seed) {
        // Kelebek şekli
        double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
        if (distance > radius) return false;

        // Normalize coordinates
        double x = Math.abs(dx) / (double)radius;
        double y = dy / (double)radius;
        double z = dz / (double)radius;

        // Butterfly wing formülü
        double wing = Math.exp(-x*x) * Math.cos(8*x) * Math.exp(-y*y*0.5);
        return Math.abs(z) <= wing * 0.8;
    }

    @Override
    public String getName() {
        return "BUTTERFLY";
    }

    @Override
    public double getDeformationFactor() {
        return 0.8;
    }

    @Override
    public String getDisplayName() {
        return "Kelebek";
    }
}