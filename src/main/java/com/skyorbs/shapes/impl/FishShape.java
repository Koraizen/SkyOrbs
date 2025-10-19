package com.skyorbs.shapes.impl;

import com.skyorbs.shapes.PlanetShape;

public class FishShape implements PlanetShape {

    @Override
    public boolean isBlockPart(int dx, int dy, int dz, int radius, long seed) {
        // Balık şekli - body, tail, fins
        double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
        if (distance > radius) return false;

        // Normalize coordinates
        double x = dx / (double)radius;
        double y = dy / (double)radius;
        double z = dz / (double)radius;

        // Fish body - oval shape
        double bodyRadius = Math.sqrt(x*x + y*y);
        if (bodyRadius <= 0.4 && Math.abs(z) <= 0.2) {
            return true;
        }

        // Tail (kuyruk)
        if (x < -0.3) {
            double tailWidth = 0.5 + (x + 0.3) * 2; // Kuyrukta daralır
            return Math.abs(y) <= tailWidth && Math.abs(z) <= 0.1;
        }

        // Dorsal fin (sırt yüzgeci)
        if (Math.abs(x) <= 0.2 && y > 0.2 && y < 0.5) {
            return Math.abs(z) <= 0.05;
        }

        // Pectoral fins (göğüs yüzgeçleri)
        if (Math.abs(y) <= 0.1 && Math.abs(z) <= 0.3 && x > -0.1 && x < 0.2) {
            return true;
        }

        return false;
    }

    @Override
    public String getName() {
        return "FISH";
    }

    @Override
    public double getDeformationFactor() {
        return 0.7;
    }

    @Override
    public String getDisplayName() {
        return "Balık";
    }
}