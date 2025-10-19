package com.skyorbs.shapes.impl;

import com.skyorbs.shapes.PlanetShape;

public class SnowboardShape implements PlanetShape {

    @Override
    public boolean isBlockPart(int dx, int dy, int dz, int radius, long seed) {
        // Snowboard şekli - board, bindings, edges
        double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
        if (distance > radius) return false;

        // Normalize coordinates
        double x = dx / (double)radius;
        double y = dy / (double)radius;
        double z = dz / (double)radius;

        // Main board (ana tahta)
        if (Math.abs(y) <= 0.02) {
            // Tapered shape - wider in middle
            double width = 0.2 + (0.3 - Math.abs(x) * 0.6);
            return Math.abs(z) <= width && Math.abs(x) <= 0.8;
        }

        // Board edges (kenarlar) - slightly raised
        if (Math.abs(y) <= 0.04) {
            double width = 0.2 + (0.3 - Math.abs(x) * 0.6);
            return Math.abs(z) <= width && Math.abs(x) <= 0.8;
        }

        // Nose and tail (burun ve kuyruk) - upturned
        if (Math.abs(x) > 0.6) {
            double tipAngle = (Math.abs(x) - 0.6) * 0.5;
            if (y > tipAngle - 0.02 && y < tipAngle + 0.02) {
                return Math.abs(z) <= 0.15;
            }
        }

        // Bindings (bağlamalar)
        double bindingWidth = 0.08;
        double bindingLength = 0.06;
        // Front binding
        if (x > 0.2 && x < 0.32 && Math.abs(y - 0.05) <= 0.03) {
            return Math.abs(z) <= bindingWidth;
        }
        // Rear binding
        if (x < -0.2 && x > -0.32 && Math.abs(y - 0.05) <= 0.03) {
            return Math.abs(z) <= bindingWidth;
        }

        // Binding straps (bağlama kayışları)
        if (Math.abs(y - 0.08) <= 0.02) {
            if (Math.abs(x - 0.26) <= bindingLength && Math.abs(z) <= 0.12) return true;
            if (Math.abs(x + 0.26) <= bindingLength && Math.abs(z) <= 0.12) return true;
        }

        return false;
    }

    @Override
    public String getName() {
        return "SNOWBOARD";
    }

    @Override
    public double getDeformationFactor() {
        return 0.8;
    }

    @Override
    public String getDisplayName() {
        return "Kar Tahtası";
    }
}