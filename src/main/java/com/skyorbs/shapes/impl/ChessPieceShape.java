package com.skyorbs.shapes.impl;

import com.skyorbs.shapes.PlanetShape;

public class ChessPieceShape implements PlanetShape {

    @Override
    public boolean isBlockPart(int dx, int dy, int dz, int radius, long seed) {
        // Satranç taşı şekli - base, body, head
        double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
        if (distance > radius) return false;

        // Normalize coordinates
        double x = dx / (double)radius;
        double y = dy / (double)radius;
        double z = dz / (double)radius;

        // Chess piece - wide base, narrow neck, detailed head
        if (y < -0.5) {
            // Base - geniş
            return Math.sqrt(x*x + z*z) <= 0.6;
        } else if (y < 0.0) {
            // Body - orta
            return Math.sqrt(x*x + z*z) <= 0.4;
        } else {
            // Head - detaylı şekil
            double headRadius = Math.sqrt(x*x + z*z);
            // Cross shape on top
            if (Math.abs(x) <= 0.2 && Math.abs(z) <= 0.2) return true;
            if (Math.abs(x) <= 0.4 && Math.abs(z) <= 0.1) return true;
            if (Math.abs(x) <= 0.1 && Math.abs(z) <= 0.4) return true;
            return headRadius <= 0.3;
        }
    }

    @Override
    public String getName() {
        return "CHESS_PIECE";
    }

    @Override
    public double getDeformationFactor() {
        return 0.5;
    }

    @Override
    public String getDisplayName() {
        return "Satranç Taşı";
    }
}