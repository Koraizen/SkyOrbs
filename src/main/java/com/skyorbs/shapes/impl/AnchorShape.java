package com.skyorbs.shapes.impl;

import com.skyorbs.shapes.PlanetShape;

public class AnchorShape implements PlanetShape {

    @Override
    public boolean isBlockPart(int dx, int dy, int dz, int radius, long seed) {
        // Çapa şekli - shank, stock, flukes
        double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
        if (distance > radius) return false;

        // Normalize coordinates
        double x = dx / (double)radius;
        double y = dy / (double)radius;
        double z = dz / (double)radius;

        // Anchor shank (dikey kısım)
        if (Math.abs(x) <= 0.05 && Math.abs(z) <= 0.05 && y >= -0.8) {
            return true;
        }

        // Anchor stock (yatay kısım)
        if (y >= -0.2 && y <= 0.1 && Math.abs(z) <= 0.05) {
            return Math.abs(x) <= 0.4;
        }

        // Anchor flukes (çengeller)
        if (y < -0.2) {
            // Left fluke
            if (x <= -0.1 && x >= -0.4) {
                double flukeShape = Math.abs(x + 0.25) + Math.abs(y + 0.5);
                return flukeShape <= 0.3 && Math.abs(z) <= 0.05;
            }
            // Right fluke
            if (x >= 0.1 && x <= 0.4) {
                double flukeShape = Math.abs(x - 0.25) + Math.abs(y + 0.5);
                return flukeShape <= 0.3 && Math.abs(z) <= 0.05;
            }
        }

        return false;
    }

    @Override
    public String getName() {
        return "ANCHOR";
    }

    @Override
    public double getDeformationFactor() {
        return 0.7;
    }

    @Override
    public String getDisplayName() {
        return "Çapa";
    }
}