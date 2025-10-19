package com.skyorbs.shapes.impl;

import com.skyorbs.shapes.PlanetShape;

public class MotorcycleShape implements PlanetShape {

    @Override
    public boolean isBlockPart(int dx, int dy, int dz, int radius, long seed) {
        // Motorsiklet şekli - frame, wheels, handlebars
        double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
        if (distance > radius) return false;

        // Normalize coordinates
        double x = dx / (double)radius;
        double y = dy / (double)radius;
        double z = dz / (double)radius;

        // Main frame (ana şasi)
        if (Math.abs(y) <= 0.08) {
            return Math.abs(x) <= 0.5 && Math.abs(z) <= 0.1;
        }

        // Seat (sele)
        if (y > 0.08 && y < 0.2 && Math.abs(x) <= 0.3) {
            return Math.abs(z) <= 0.15;
        }

        // Handlebars (direksiyon)
        if (x > 0.3 && x < 0.5 && Math.abs(y - 0.1) <= 0.05) {
            return Math.abs(z) <= 0.2;
        }

        // Front wheel (ön tekerlek)
        if (x > 0.4 && Math.abs(y + 0.1) <= 0.12) {
            return Math.abs(z) <= 0.12;
        }

        // Rear wheel (arka tekerlek)
        if (x < -0.3 && Math.abs(y + 0.1) <= 0.12) {
            return Math.abs(z) <= 0.12;
        }

        // Engine (motor)
        if (Math.abs(x) <= 0.2 && y < -0.05 && y > -0.15) {
            return Math.abs(z) <= 0.12;
        }

        return false;
    }

    @Override
    public String getName() {
        return "MOTORCYCLE";
    }

    @Override
    public double getDeformationFactor() {
        return 0.9;
    }

    @Override
    public String getDisplayName() {
        return "Motorsiklet";
    }
}