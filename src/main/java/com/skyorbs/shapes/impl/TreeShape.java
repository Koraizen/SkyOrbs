package com.skyorbs.shapes.impl;

import com.skyorbs.shapes.PlanetShape;

public class TreeShape implements PlanetShape {

    @Override
    public boolean isBlockPart(int dx, int dy, int dz, int radius, long seed) {
        // Ağaç şekli - kökler, gövde, yapraklar
        double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
        if (distance > radius) return false;

        // Normalize coordinates
        double x = dx / (double)radius;
        double y = dy / (double)radius;
        double z = dz / (double)radius;

        // Tree structure
        if (y < -0.3) {
            // Roots - aşağıda geniş
            double rootRadius = 0.4 + (-y - 0.3) * 0.5;
            return Math.sqrt(x*x + z*z) <= rootRadius;
        } else if (y < 0.3) {
            // Trunk - orta kısım dar
            double trunkRadius = 0.2;
            return Math.sqrt(x*x + z*z) <= trunkRadius;
        } else {
            // Canopy - yukarıda geniş ve yapraklı
            double canopyRadius = 0.8 - (y - 0.3) * 0.4;
            return Math.sqrt(x*x + z*z) <= canopyRadius;
        }
    }

    @Override
    public String getName() {
        return "TREE";
    }

    @Override
    public double getDeformationFactor() {
        return 0.8;
    }

    @Override
    public String getDisplayName() {
        return "Ağaç";
    }
}