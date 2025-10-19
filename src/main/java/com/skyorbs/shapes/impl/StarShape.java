package com.skyorbs.shapes.impl;

import com.skyorbs.shapes.PlanetShape;

public class StarShape implements PlanetShape {

    @Override
    public boolean isBlockPart(int dx, int dy, int dz, int radius, long seed) {
        // Yıldız şekli - 5 köşeli yıldız
        double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
        if (distance > radius) return false;

        // Polar koordinatlara dönüştür
        double theta = Math.atan2(dz, dx);
        double phi = Math.acos(dy / distance);

        // Yıldız formülü: r = a / (1 + b * sin(5θ))
        double starRadius = radius * (1.0 / (1.0 + 0.5 * Math.sin(5 * theta)));
        return distance <= starRadius;
    }

    @Override
    public String getName() {
        return "STAR";
    }

    @Override
    public double getDeformationFactor() {
        return 0.3;
    }

    @Override
    public String getDisplayName() {
        return "Yıldız";
    }
}