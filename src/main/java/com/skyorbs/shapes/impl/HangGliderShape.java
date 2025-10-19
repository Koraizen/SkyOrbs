package com.skyorbs.shapes.impl;

import com.skyorbs.shapes.PlanetShape;

public class HangGliderShape implements PlanetShape {

    @Override
    public boolean isBlockPart(int dx, int dy, int dz, int radius, long seed) {
        // Delta kanat şekli - wing, keel, hang point
        double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
        if (distance > radius) return false;

        // Normalize coordinates
        double x = dx / (double)radius;
        double y = dy / (double)radius;
        double z = dz / (double)radius;

        // Wing (kanat) - delta shape
        if (y > 0) {
            double wingWidth = y * 0.8; // Wider at back
            double wingLength = 0.6;
            return Math.abs(x) <= wingLength && Math.abs(z) <= wingWidth;
        }

        // Leading edge (ön kenar)
        if (Math.abs(y) <= 0.05) {
            return Math.abs(x) <= 0.6 && Math.abs(z) <= 0.8;
        }

        // Keel (omurga)
        if (Math.abs(z) <= 0.03 && y <= 0) {
            return Math.abs(x) <= 0.5;
        }

        // Hang point (asma noktası)
        if (Math.abs(x) <= 0.08 && Math.abs(z) <= 0.08 && y <= -0.1 && y >= -0.2) {
            return true;
        }

        // Control bar (kontrol çubuğu)
        if (y <= -0.2 && y >= -0.25) {
            return Math.abs(x) <= 0.3 && Math.abs(z) <= 0.02;
        }

        // Downtubes (destek çubukları)
        if (Math.abs(x - 0.2) <= 0.02 && y <= -0.1) {
            return Math.abs(z) <= 0.02;
        }
        if (Math.abs(x + 0.2) <= 0.02 && y <= -0.1) {
            return Math.abs(z) <= 0.02;
        }

        return false;
    }

    @Override
    public String getName() {
        return "HANG_GLIDER";
    }

    @Override
    public double getDeformationFactor() {
        return 0.8;
    }

    @Override
    public String getDisplayName() {
        return "Delta Kanat";
    }
}