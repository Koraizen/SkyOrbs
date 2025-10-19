package com.skyorbs.shapes.impl;

import com.skyorbs.shapes.PlanetShape;
import com.skyorbs.SkyOrbs;

public class DiamondShape implements PlanetShape {

    @Override
    public boolean isBlockPart(int dx, int dy, int dz, int radius, long seed) {
        // Elmas şekli - octahedron (DÜZELTİLMİŞ ALGORİTMA)
        double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
        if (distance > radius) return false;

        // Normalize coordinates
        double x = Math.abs(dx) / (double)radius;
        double y = Math.abs(dy) / (double)radius;
        double z = Math.abs(dz) / (double)radius;

        // Diamond shape - octahedron: max(x,y,z) + (x+y+z)/3 <= 1.0
        // Daha basit ve güvenilir formül
        double maxCoord = Math.max(Math.max(x, y), z);
        double sumCoord = x + y + z;
        return maxCoord + sumCoord / 3.0 <= 1.0;
    }

    @Override
    public String getName() {
        return "DIAMOND";
    }

    @Override
    public double getDeformationFactor() {
        return 0.3;
    }

    @Override
    public String getDisplayName() {
        return "Elmas";
    }
}
