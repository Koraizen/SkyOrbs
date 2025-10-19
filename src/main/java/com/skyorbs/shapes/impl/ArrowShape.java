package com.skyorbs.shapes.impl;

import com.skyorbs.shapes.PlanetShape;

public class ArrowShape implements PlanetShape {

    @Override
    public boolean isBlockPart(int dx, int dy, int dz, int radius, long seed) {
        // Ok şekli - 3D arrow
        double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
        if (distance > radius) return false;

        // Normalize coordinates
        double x = dx / (double)radius;
        double y = dy / (double)radius;
        double z = dz / (double)radius;

        // Arrow formülü - baş kısmı geniş, kuyruk kısmı dar
        double arrowLength = Math.abs(y);
        double arrowWidth = 0.8 - 0.6 * arrowLength; // Uçta geniş, kuyrukta dar

        return Math.abs(x) <= arrowWidth && Math.abs(z) <= arrowWidth;
    }

    @Override
    public String getName() {
        return "ARROW";
    }

    @Override
    public double getDeformationFactor() {
        return 0.7;
    }

    @Override
    public String getDisplayName() {
        return "Ok";
    }
}