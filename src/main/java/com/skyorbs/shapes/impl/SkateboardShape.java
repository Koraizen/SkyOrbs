package com.skyorbs.shapes.impl;

import com.skyorbs.shapes.PlanetShape;

public class SkateboardShape implements PlanetShape {

    @Override
    public boolean isBlockPart(int dx, int dy, int dz, int radius, long seed) {
        // Skateboard şekli - deck, wheels, trucks
        double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
        if (distance > radius) return false;

        // Normalize coordinates
        double x = dx / (double)radius;
        double y = dy / (double)radius;
        double z = dz / (double)radius;

        // Main deck (ana tabla)
        if (Math.abs(y) <= 0.03) {
            return Math.abs(x) <= 0.6 && Math.abs(z) <= 0.15;
        }

        // Deck taper (kenar eğimi)
        if (Math.abs(y) <= 0.05) {
            double taper = 1.0 - Math.abs(x) * 0.3;
            return Math.abs(z) <= 0.15 * taper && Math.abs(x) <= 0.6;
        }

        // Trucks (kamyonlar)
        double truckWidth = 0.08;
        double truckLength = 0.12;
        // Front truck
        if (x > 0.3 && x < 0.42 && Math.abs(y + 0.06) <= 0.04) {
            return Math.abs(z) <= truckWidth;
        }
        // Rear truck
        if (x < -0.3 && x > -0.42 && Math.abs(y + 0.06) <= 0.04) {
            return Math.abs(z) <= truckWidth;
        }

        // Wheels (tekerlekler)
        double wheelRadius = 0.04;
        // Front wheels
        if (x > 0.35 && x < 0.4) {
            if (Math.abs(z - 0.1) <= wheelRadius && Math.abs(y + 0.08) <= wheelRadius) return true;
            if (Math.abs(z + 0.1) <= wheelRadius && Math.abs(y + 0.08) <= wheelRadius) return true;
        }
        // Rear wheels
        if (x < -0.35 && x > -0.4) {
            if (Math.abs(z - 0.1) <= wheelRadius && Math.abs(y + 0.08) <= wheelRadius) return true;
            if (Math.abs(z + 0.1) <= wheelRadius && Math.abs(y + 0.08) <= wheelRadius) return true;
        }

        return false;
    }

    @Override
    public String getName() {
        return "SKATEBOARD";
    }

    @Override
    public double getDeformationFactor() {
        return 0.9;
    }

    @Override
    public String getDisplayName() {
        return "Kaykay";
    }
}