package com.skyorbs.shapes.impl;

import com.skyorbs.shapes.PlanetShape;

public class CrownShape implements PlanetShape {

    @Override
    public boolean isBlockPart(int dx, int dy, int dz, int radius, long seed) {
        // Taç şekli - base, spikes, jewels
        double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
        if (distance > radius) return false;

        // Normalize coordinates
        double x = dx / (double)radius;
        double y = dy / (double)radius;
        double z = dz / (double)radius;

        // Crown base
        if (y >= -0.2 && y <= 0.1) {
            return Math.abs(z) <= 0.8;
        }

        // Crown spikes
        if (y > 0.1 && y <= 0.6) {
            // 5 spikes
            for (int i = 0; i < 5; i++) {
                double spikeX = -0.6 + i * 0.3;
                double spikeWidth = 0.1;
                if (Math.abs(x - spikeX) <= spikeWidth) {
                    double spikeHeight = 0.6 - Math.abs(x - spikeX) * 2;
                    if (y <= spikeHeight) return true;
                }
            }
        }

        // Crown jewels
        if (y >= 0.2 && y <= 0.4) {
            for (int i = 0; i < 5; i++) {
                double jewelX = -0.6 + i * 0.3;
                double jewelDistance = Math.sqrt((x - jewelX)*(x - jewelX) + (y - 0.3)*(y - 0.3));
                if (jewelDistance <= 0.08) return true;
            }
        }

        return false;
    }

    @Override
    public String getName() {
        return "CROWN";
    }

    @Override
    public double getDeformationFactor() {
        return 0.6;
    }

    @Override
    public String getDisplayName() {
        return "Taç";
    }
}