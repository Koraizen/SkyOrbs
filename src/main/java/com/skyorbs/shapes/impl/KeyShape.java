package com.skyorbs.shapes.impl;

import com.skyorbs.shapes.PlanetShape;

public class KeyShape implements PlanetShape {

    @Override
    public boolean isBlockPart(int dx, int dy, int dz, int radius, long seed) {
        // Anahtar şekli - bow, shank, bit
        double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
        if (distance > radius) return false;

        // Normalize coordinates
        double x = dx / (double)radius;
        double y = dy / (double)radius;
        double z = dz / (double)radius;

        // Key bow (yuvarlak baş)
        double bowCenterX = -0.6;
        double bowCenterY = 0.3;
        double bowDistance = Math.sqrt((x - bowCenterX)*(x - bowCenterX) + (y - bowCenterY)*(y - bowCenterY));
        if (bowDistance <= 0.3) return true;

        // Key shank (uzun kısım)
        if (x >= -0.4 && x <= 0.4 && Math.abs(y) <= 0.1 && Math.abs(z) <= 0.05) return true;

        // Key bit (dişler)
        if (x >= 0.2 && x <= 0.6 && Math.abs(z) <= 0.05) {
            // Alternating teeth
            int toothIndex = (int)((x - 0.2) / 0.1);
            double toothY = (toothIndex % 2 == 0) ? 0.0 : 0.15;
            return Math.abs(y - toothY) <= 0.1;
        }

        return false;
    }

    @Override
    public String getName() {
        return "KEY";
    }

    @Override
    public double getDeformationFactor() {
        return 0.7;
    }

    @Override
    public String getDisplayName() {
        return "Anahtar";
    }
}