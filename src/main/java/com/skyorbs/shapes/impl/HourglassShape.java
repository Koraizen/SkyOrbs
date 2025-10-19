package com.skyorbs.shapes.impl;

import com.skyorbs.shapes.PlanetShape;

public class HourglassShape implements PlanetShape {

    @Override
    public boolean isBlockPart(int dx, int dy, int dz, int radius, long seed) {
        // Kum saati şekli - iki koni ters
        double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
        if (distance > radius) return false;

        // Normalize coordinates
        double x = dx / (double)radius;
        double y = dy / (double)radius;
        double z = dz / (double)radius;

        // Hourglass - narrow in middle, wide at ends
        double absY = Math.abs(y);
        double radiusAtY = 0.3 + absY * 0.4; // Orta kısım dar, uçlar geniş

        return Math.sqrt(x*x + z*z) <= radiusAtY;
    }

    @Override
    public String getName() {
        return "HOURGLASS";
    }

    @Override
    public double getDeformationFactor() {
        return 0.6;
    }

    @Override
    public String getDisplayName() {
        return "Kum Saati";
    }
}