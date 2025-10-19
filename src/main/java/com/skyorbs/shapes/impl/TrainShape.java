package com.skyorbs.shapes.impl;

import com.skyorbs.shapes.PlanetShape;

public class TrainShape implements PlanetShape {

    @Override
    public boolean isBlockPart(int dx, int dy, int dz, int radius, long seed) {
        // Tren şekli - locomotive, cars, wheels
        double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
        if (distance > radius) return false;

        // Normalize coordinates
        double x = dx / (double)radius;
        double y = dy / (double)radius;
        double z = dz / (double)radius;

        // Train body (vagon gövdesi)
        if (Math.abs(y) <= 0.2) {
            return Math.abs(z) <= 0.4;
        }

        // Train roof (tavan)
        if (y > 0.2 && y < 0.4) {
            return Math.abs(z) <= 0.35;
        }

        // Wheels (tekerlekler)
        double wheelRadius = 0.12;
        if (y < -0.15) {
            // Left wheels
            if (Math.abs(z - 0.25) <= wheelRadius) return true;
            // Right wheels
            if (Math.abs(z + 0.25) <= wheelRadius) return true;
        }

        // Chimney (baca) - locomotive için
        if (x > 0.2 && x < 0.4 && Math.abs(z) <= 0.08 && y > 0.4 && y < 0.6) {
            return true;
        }

        // Coupler (bağlantı)
        if (x < -0.4 && Math.abs(y) <= 0.1 && Math.abs(z) <= 0.05) {
            return true;
        }

        return false;
    }

    @Override
    public String getName() {
        return "TRAIN";
    }

    @Override
    public double getDeformationFactor() {
        return 0.6;
    }

    @Override
    public String getDisplayName() {
        return "Tren";
    }
}