package com.skyorbs.shapes.impl;

import com.skyorbs.shapes.PlanetShape;

public class DonutShape implements PlanetShape {

    @Override
    public boolean isBlockPart(int dx, int dy, int dz, int radius, long seed) {
        // Halka şekli (donut/torus)
        double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
        if (distance > radius) return false;

        // Torus formülü
        double majorRadius = radius * 0.7; // Ana yarıçap
        double minorRadius = radius * 0.3; // Küçük yarıçap

        // XY düzleminde halka
        double xyDistance = Math.sqrt(dx * dx + dy * dy);
        double torusDistance = Math.sqrt(
            Math.pow(xyDistance - majorRadius, 2) + dz * dz
        );

        return torusDistance <= minorRadius;
    }

    @Override
    public String getName() {
        return "DONUT";
    }

    @Override
    public double getDeformationFactor() {
        return 0.5;
    }

    @Override
    public String getDisplayName() {
        return "Halka";
    }
}