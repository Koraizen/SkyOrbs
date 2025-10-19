package com.skyorbs.shapes.impl;

import com.skyorbs.shapes.PlanetShape;

public class SpaceshipShape implements PlanetShape {

    @Override
    public boolean isBlockPart(int dx, int dy, int dz, int radius, long seed) {
        // Uzay gemisi şekli - saucer, bridge, engines
        double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
        if (distance > radius) return false;

        // Normalize coordinates
        double x = dx / (double)radius;
        double y = dy / (double)radius;
        double z = dz / (double)radius;

        // Main saucer (disk shape)
        if (Math.abs(y) <= 0.2) {
            double saucerRadius = Math.sqrt(x*x + z*z);
            return saucerRadius <= 0.8;
        }

        // Bridge (üst çıkıntı)
        if (y > 0.2 && y < 0.6) {
            double bridgeRadius = Math.sqrt(x*x + z*z);
            return bridgeRadius <= 0.3;
        }

        // Engines (alt çıkıntılar)
        if (y < -0.2 && y > -0.5) {
            // 3 engine pods
            for (int i = 0; i < 3; i++) {
                double angle = i * 2 * Math.PI / 3;
                double engineX = Math.cos(angle) * 0.5;
                double engineZ = Math.sin(angle) * 0.5;
                double engineDistance = Math.sqrt((x - engineX)*(x - engineX) + (z - engineZ)*(z - engineZ));
                if (engineDistance <= 0.15) return true;
            }
        }

        return false;
    }

    @Override
    public String getName() {
        return "SPACESHIP";
    }

    @Override
    public double getDeformationFactor() {
        return 0.6;
    }

    @Override
    public String getDisplayName() {
        return "Uzay Gemisi";
    }
}