package com.skyorbs.shapes.impl;

import com.skyorbs.shapes.PlanetShape;

public class TractorShape implements PlanetShape {

    @Override
    public boolean isBlockPart(int dx, int dy, int dz, int radius, long seed) {
        // Traktör şekli - body, wheels, exhaust, plow
        double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
        if (distance > radius) return false;

        // Normalize coordinates
        double x = dx / (double)radius;
        double y = dy / (double)radius;
        double z = dz / (double)radius;

        // Main body (ana gövde)
        if (Math.abs(y) <= 0.15) {
            return Math.abs(x) <= 0.4 && Math.abs(z) <= 0.3;
        }

        // Cabin (kabin)
        if (y > 0.15 && y < 0.4 && Math.abs(x) <= 0.25) {
            return Math.abs(z) <= 0.25;
        }

        // Large rear wheels (büyük arka tekerlekler)
        double rearWheelRadius = 0.18;
        if (x < -0.2) {
            if (Math.abs(z - 0.25) <= rearWheelRadius && Math.abs(y + 0.15) <= rearWheelRadius) return true;
            if (Math.abs(z + 0.25) <= rearWheelRadius && Math.abs(y + 0.15) <= rearWheelRadius) return true;
        }

        // Small front wheels (küçük ön tekerlekler)
        double frontWheelRadius = 0.12;
        if (x > 0.2) {
            if (Math.abs(z - 0.2) <= frontWheelRadius && Math.abs(y + 0.15) <= frontWheelRadius) return true;
            if (Math.abs(z + 0.2) <= frontWheelRadius && Math.abs(y + 0.15) <= frontWheelRadius) return true;
        }

        // Exhaust pipe (egzoz borusu)
        if (Math.abs(x + 0.35) <= 0.05 && Math.abs(z) <= 0.03 && y > 0.4 && y < 0.6) {
            return true;
        }

        // Plow attachment (saban eki)
        if (x < -0.4 && Math.abs(y + 0.1) <= 0.08) {
            return Math.abs(z) <= 0.4;
        }

        return false;
    }

    @Override
    public String getName() {
        return "TRACTOR";
    }

    @Override
    public double getDeformationFactor() {
        return 0.7;
    }

    @Override
    public String getDisplayName() {
        return "Traktör";
    }
}