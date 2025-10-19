package com.skyorbs.shapes.impl;

import com.skyorbs.shapes.PlanetShape;

public class KiteShape implements PlanetShape {

    @Override
    public boolean isBlockPart(int dx, int dy, int dz, int radius, long seed) {
        // Uçurtma şekli - diamond shape with tail
        double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
        if (distance > radius) return false;

        // Normalize coordinates
        double x = dx / (double)radius;
        double y = dy / (double)radius;
        double z = dz / (double)radius;

        // Diamond kite shape
        double absX = Math.abs(x);
        double absZ = Math.abs(z);

        // Top triangle
        if (y > 0) {
            double topWidth = 0.4 - y * 0.8;
            if (absX <= topWidth && absZ <= topWidth) return true;
        }

        // Bottom triangle
        if (y <= 0) {
            double bottomWidth = 0.4 + y * 0.8;
            if (absX <= bottomWidth && absZ <= bottomWidth) return true;
        }

        // Tail (kuyruk)
        if (y < -0.3) {
            double tailWidth = 0.05;
            double tailLength = (-y - 0.3) * 0.5;
            if (absZ <= tailWidth && absX <= tailLength) return true;
        }

        // Cross spars (çapraz çubuklar)
        if (Math.abs(y) <= 0.02) {
            if (absX <= 0.4 && absZ <= 0.02) return true;
            if (absZ <= 0.4 && absX <= 0.02) return true;
        }

        return false;
    }

    @Override
    public String getName() {
        return "KITE";
    }

    @Override
    public double getDeformationFactor() {
        return 0.7;
    }

    @Override
    public String getDisplayName() {
        return "Uçurtma";
    }
}