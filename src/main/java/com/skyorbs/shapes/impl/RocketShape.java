package com.skyorbs.shapes.impl;

import com.skyorbs.shapes.PlanetShape;

public class RocketShape implements PlanetShape {

    @Override
    public boolean isBlockPart(int dx, int dy, int dz, int radius, long seed) {
        // Roket şekli - nose cone, body, fins, engine
        double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
        if (distance > radius) return false;

        // Normalize coordinates
        double x = dx / (double)radius;
        double y = dy / (double)radius;
        double z = dz / (double)radius;

        // Nose cone (üst kısım konik)
        if (y > 0.5) {
            double noseRadius = 0.8 - (y - 0.5) * 1.6; // Uçta sivri
            return Math.sqrt(x*x + z*z) <= noseRadius;
        }

        // Main body (silindirik)
        if (y > -0.3 && y <= 0.5) {
            return Math.sqrt(x*x + z*z) <= 0.4;
        }

        // Engine section (alt kısım geniş)
        if (y <= -0.3) {
            return Math.sqrt(x*x + z*z) <= 0.5;
        }

        // Fins (kanatlar)
        if (Math.abs(y) <= 0.2) {
            if ((Math.abs(x) <= 0.6 && Math.abs(z) <= 0.1) ||
                (Math.abs(x) <= 0.1 && Math.abs(z) <= 0.6)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public String getName() {
        return "ROCKET";
    }

    @Override
    public double getDeformationFactor() {
        return 0.7;
    }

    @Override
    public String getDisplayName() {
        return "Roket";
    }
}