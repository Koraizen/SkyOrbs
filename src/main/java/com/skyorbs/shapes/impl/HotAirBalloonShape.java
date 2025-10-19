package com.skyorbs.shapes.impl;

import com.skyorbs.shapes.PlanetShape;

public class HotAirBalloonShape implements PlanetShape {

    @Override
    public boolean isBlockPart(int dx, int dy, int dz, int radius, long seed) {
        // Sıcak hava balonu şekli - envelope, basket, burner
        double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
        if (distance > radius) return false;

        // Normalize coordinates
        double x = dx / (double)radius;
        double y = dy / (double)radius;
        double z = dz / (double)radius;

        // Balloon envelope (balon kabı) - teardrop shape
        if (y > 0.1) {
            double envelopeRadius = Math.sqrt(1 - Math.pow((y - 0.55) / 0.45, 2)) * 0.4;
            double radialDistance = Math.sqrt(x*x + z*z);
            if (radialDistance <= envelopeRadius) return true;
        }

        // Envelope neck (boyun)
        if (y <= 0.1 && y >= -0.1) {
            double neckRadius = 0.15;
            return Math.sqrt(x*x + z*z) <= neckRadius;
        }

        // Basket (sepet)
        if (y <= -0.1 && y >= -0.4) {
            return Math.abs(x) <= 0.2 && Math.abs(z) <= 0.2;
        }

        // Burner (brülör)
        if (y >= -0.05 && y <= 0.05 && Math.abs(x) <= 0.08) {
            return Math.abs(z) <= 0.03;
        }

        // Suspension lines (asma halatları)
        if (y <= 0.1 && y >= -0.1) {
            double lineSpacing = 0.06;
            for (int i = -2; i <= 2; i++) {
                for (int j = -2; j <= 2; j++) {
                    double lineX = i * lineSpacing;
                    double lineZ = j * lineSpacing;
                    if (Math.abs(x - lineX) <= 0.01 && Math.abs(z - lineZ) <= 0.01) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    @Override
    public String getName() {
        return "HOT_AIR_BALLOON";
    }

    @Override
    public double getDeformationFactor() {
        return 0.6;
    }

    @Override
    public String getDisplayName() {
        return "Sıcak Hava Balonu";
    }
}