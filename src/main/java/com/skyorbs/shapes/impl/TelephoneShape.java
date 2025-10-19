package com.skyorbs.shapes.impl;

import com.skyorbs.shapes.PlanetShape;

public class TelephoneShape implements PlanetShape {

    @Override
    public boolean isBlockPart(int dx, int dy, int dz, int radius, long seed) {
        // Telefon şekli - handset, cord, base
        double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
        if (distance > radius) return false;

        // Normalize coordinates
        double x = dx / (double)radius;
        double y = dy / (double)radius;
        double z = dz / (double)radius;

        // Telephone base (taban)
        if (y > -0.4 && y < -0.1) {
            return Math.abs(x) <= 0.6 && Math.abs(z) <= 0.4;
        }

        // Handset body (ahize gövde)
        if (y >= -0.1 && y < 0.3) {
            return Math.abs(x) <= 0.2 && Math.abs(z) <= 0.1;
        }

        // Handset mouthpiece (mikrofon kısmı)
        if (y >= 0.3 && y < 0.5) {
            return Math.abs(x) <= 0.15 && Math.abs(z) <= 0.08;
        }

        // Cord (kablo) - spiral
        if (Math.abs(x) <= 0.05 && Math.abs(z) <= 0.05 && y >= -0.1 && y < 0.3) {
            return true;
        }

        return false;
    }

    @Override
    public String getName() {
        return "TELEPHONE";
    }

    @Override
    public double getDeformationFactor() {
        return 0.6;
    }

    @Override
    public String getDisplayName() {
        return "Telefon";
    }
}