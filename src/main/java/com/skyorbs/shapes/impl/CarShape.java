package com.skyorbs.shapes.impl;

import com.skyorbs.shapes.PlanetShape;

public class CarShape implements PlanetShape {

    @Override
    public boolean isBlockPart(int dx, int dy, int dz, int radius, long seed) {
        // Araba şekli - body, wheels, windows
        double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
        if (distance > radius) return false;

        // Normalize coordinates
        double x = dx / (double)radius;
        double y = dy / (double)radius;
        double z = dz / (double)radius;

        // Car body (gövde)
        if (Math.abs(y) <= 0.2) {
            double bodyWidth = 0.6;
            double bodyLength = 0.8;
            return Math.abs(x) <= bodyLength && Math.abs(z) <= bodyWidth;
        }

        // Car roof (tavan)
        if (y > 0.2 && y < 0.5) {
            double roofWidth = 0.5;
            double roofLength = 0.6;
            return Math.abs(x) <= roofLength && Math.abs(z) <= roofWidth;
        }

        // Wheels (tekerlekler)
        double wheelRadius = 0.15;
        // Front wheels
        if (x > 0.3 && x < 0.5) {
            if ((Math.abs(z - 0.4) <= wheelRadius && Math.abs(y + 0.2) <= wheelRadius) ||
                (Math.abs(z + 0.4) <= wheelRadius && Math.abs(y + 0.2) <= wheelRadius)) {
                return true;
            }
        }
        // Rear wheels
        if (x < -0.3 && x > -0.5) {
            if ((Math.abs(z - 0.4) <= wheelRadius && Math.abs(y + 0.2) <= wheelRadius) ||
                (Math.abs(z + 0.4) <= wheelRadius && Math.abs(y + 0.2) <= wheelRadius)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public String getName() {
        return "CAR";
    }

    @Override
    public double getDeformationFactor() {
        return 0.7;
    }

    @Override
    public String getDisplayName() {
        return "Araba";
    }
}