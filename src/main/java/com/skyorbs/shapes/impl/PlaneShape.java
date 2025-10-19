package com.skyorbs.shapes.impl;

import com.skyorbs.shapes.PlanetShape;

public class PlaneShape implements PlanetShape {

    @Override
    public boolean isBlockPart(int dx, int dy, int dz, int radius, long seed) {
        // Uçak şekli - fuselage, wings, tail, engines
        double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
        if (distance > radius) return false;

        // Normalize coordinates
        double x = dx / (double)radius;
        double y = dy / (double)radius;
        double z = dz / (double)radius;

        // Fuselage (gövde)
        if (Math.abs(y) <= 0.1 && Math.abs(z) <= 0.08) {
            return Math.abs(x) <= 0.7;
        }

        // Wings (kanatlar)
        if (Math.abs(z) > 0.08 && Math.abs(z) < 0.4 && Math.abs(y) <= 0.05) {
            double wingLength = 0.6;
            return Math.abs(x) <= wingLength;
        }

        // Tail (kuyruk)
        if (x < -0.5) {
            // Vertical stabilizer
            if (Math.abs(z) <= 0.05 && y > 0.1 && y < 0.3) {
                return true;
            }
            // Horizontal stabilizer
            if (Math.abs(y) <= 0.05 && Math.abs(z) <= 0.2) {
                return true;
            }
        }

        // Engines (motorlar)
        double engineRadius = 0.08;
        // Under wings
        if (Math.abs(z - 0.25) <= engineRadius && Math.abs(y + 0.08) <= engineRadius && Math.abs(x) <= 0.4) {
            return true;
        }
        if (Math.abs(z + 0.25) <= engineRadius && Math.abs(y + 0.08) <= engineRadius && Math.abs(x) <= 0.4) {
            return true;
        }

        return false;
    }

    @Override
    public String getName() {
        return "PLANE";
    }

    @Override
    public double getDeformationFactor() {
        return 0.9;
    }

    @Override
    public String getDisplayName() {
        return "Uçak";
    }
}