package com.skyorbs.shapes.impl;

import com.skyorbs.shapes.PlanetShape;

public class DragonShape implements PlanetShape {

    @Override
    public boolean isBlockPart(int dx, int dy, int dz, int radius, long seed) {
        // Ejderha şekli - vücut, kanatlar, kuyruk
        double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
        if (distance > radius) return false;

        // Normalize coordinates
        double x = dx / (double)radius;
        double y = dy / (double)radius;
        double z = dz / (double)radius;

        // Dragon body - S şeklinde kıvrımlı
        double bodyCurve = Math.sin(x * 3) * 0.2;
        double bodyY = y + bodyCurve;

        // Wings - kanatlar
        if (Math.abs(z) > 0.3 && Math.abs(x) < 0.6) {
            double wingShape = Math.exp(-Math.abs(z) * 2) * Math.exp(-x * x);
            if (wingShape > 0.1) return true;
        }

        // Main body
        return Math.abs(bodyY) <= 0.3 && Math.abs(z) <= 0.4;
    }

    @Override
    public String getName() {
        return "DRAGON";
    }

    @Override
    public double getDeformationFactor() {
        return 0.8;
    }

    @Override
    public String getDisplayName() {
        return "Ejderha";
    }
}