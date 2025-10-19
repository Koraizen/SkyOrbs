package com.skyorbs.shapes.impl;

import com.skyorbs.shapes.PlanetShape;

public class BirdShape implements PlanetShape {

    @Override
    public boolean isBlockPart(int dx, int dy, int dz, int radius, long seed) {
        // Kuş şekli - body, wings, head, tail
        double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
        if (distance > radius) return false;

        // Normalize coordinates
        double x = dx / (double)radius;
        double y = dy / (double)radius;
        double z = dz / (double)radius;

        // Bird body - oval
        if (Math.abs(y) <= 0.3 && Math.abs(z) <= 0.2) {
            return Math.sqrt(x*x) <= 0.4;
        }

        // Wings (kanatlar)
        if (Math.abs(z) > 0.2 && Math.abs(z) < 0.7 && Math.abs(y) <= 0.2) {
            double wingShape = Math.exp(-Math.abs(z) * 2) * Math.exp(-x*x);
            return wingShape > 0.1;
        }

        // Head (baş)
        if (x > 0.3 && x < 0.6 && Math.abs(y) <= 0.2 && Math.abs(z) <= 0.15) {
            return true;
        }

        // Tail (kuyruk)
        if (x < -0.3 && Math.abs(y) <= 0.4 && Math.abs(z) <= 0.1) {
            return true;
        }

        return false;
    }

    @Override
    public String getName() {
        return "BIRD";
    }

    @Override
    public double getDeformationFactor() {
        return 0.8;
    }

    @Override
    public String getDisplayName() {
        return "Kuş";
    }
}