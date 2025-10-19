package com.skyorbs.shapes.impl;

import com.skyorbs.shapes.PlanetShape;

public class SurfboardShape implements PlanetShape {

    @Override
    public boolean isBlockPart(int dx, int dy, int dz, int radius, long seed) {
        // Sörf tahtası şekli - board, nose, tail, fins
        double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
        if (distance > radius) return false;

        // Normalize coordinates
        double x = dx / (double)radius;
        double y = dy / (double)radius;
        double z = dz / (double)radius;

        // Main board (ana tahta)
        if (Math.abs(y) <= 0.03) {
            // Wider at nose, narrower at tail
            double width = 0.25 - Math.abs(x) * 0.1;
            return Math.abs(z) <= width && Math.abs(x) <= 0.7;
        }

        // Board rails (kenarlar) - slightly raised
        if (Math.abs(y) <= 0.05) {
            double width = 0.25 - Math.abs(x) * 0.1;
            return Math.abs(z) <= width && Math.abs(x) <= 0.7;
        }

        // Nose rocker (burun eğimi)
        if (x > 0.5) {
            double noseHeight = (x - 0.5) * 0.1;
            if (Math.abs(y) <= noseHeight + 0.03) {
                return Math.abs(z) <= 0.2;
            }
        }

        // Tail (kuyruk)
        if (x < -0.5) {
            double tailHeight = (-x - 0.5) * 0.08;
            if (Math.abs(y) <= tailHeight + 0.03) {
                return Math.abs(z) <= 0.15;
            }
        }

        // Fins (yüzgeçler)
        if (x < -0.4 && Math.abs(y + 0.05) <= 0.08) {
            if (Math.abs(z) <= 0.03) return true; // Center fin
            if (Math.abs(z - 0.08) <= 0.02) return true; // Side fins
            if (Math.abs(z + 0.08) <= 0.02) return true;
        }

        return false;
    }

    @Override
    public String getName() {
        return "SURFBOARD";
    }

    @Override
    public double getDeformationFactor() {
        return 0.7;
    }

    @Override
    public String getDisplayName() {
        return "Sörf Tahtası";
    }
}