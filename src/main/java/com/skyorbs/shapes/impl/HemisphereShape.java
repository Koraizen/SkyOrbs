package com.skyorbs.shapes.impl;

import com.skyorbs.shapes.PlanetShape;

public class HemisphereShape implements PlanetShape {

    @Override
    public boolean isBlockPart(int dx, int dy, int dz, int radius, long seed) {
        // Küre formülü + yarım küre için y <= 0 koşulu (YUKARIDAN AŞAĞI!)
        // ÜST TARAFI DA DOLDUR - tam yarım küre!
        return dx * dx + dy * dy + dz * dz <= radius * radius && dy <= radius/2;
    }

    @Override
    public String getName() {
        return "HEMISPHERE";
    }

    @Override
    public double getDeformationFactor() {
        return 0.0;
    }

    @Override
    public String getDisplayName() {
        return "Yarım Küre";
    }
}