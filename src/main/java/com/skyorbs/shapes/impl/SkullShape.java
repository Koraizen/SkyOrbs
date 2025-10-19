package com.skyorbs.shapes.impl;

import com.skyorbs.shapes.PlanetShape;

public class SkullShape implements PlanetShape {

    @Override
    public boolean isBlockPart(int dx, int dy, int dz, int radius, long seed) {
        // Kafatası şekli - cranium, eye sockets, nasal cavity, teeth
        double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
        if (distance > radius) return false;

        // Normalize coordinates
        double x = dx / (double)radius;
        double y = dy / (double)radius;
        double z = dz / (double)radius;

        // Main cranium (round head shape)
        double craniumRadius = Math.sqrt(x*x + y*y + z*z);
        if (craniumRadius <= 0.8) {
            // Eye sockets
            if (y > 0.1 && y < 0.4) {
                if ((Math.abs(x - 0.25) <= 0.15 && Math.abs(z) <= 0.1) ||
                    (Math.abs(x + 0.25) <= 0.15 && Math.abs(z) <= 0.1)) {
                    return false; // Hollow eye sockets
                }
            }

            // Nasal cavity
            if (y > -0.1 && y < 0.2 && Math.abs(x) <= 0.1 && z > 0.1) {
                return false; // Nose hole
            }

            // Teeth (bottom jaw)
            if (y < -0.3) {
                for (int i = -2; i <= 2; i++) {
                    double toothX = i * 0.15;
                    if (Math.abs(x - toothX) <= 0.05 && z > 0.2) {
                        return true; // Teeth protrude
                    }
                }
            }

            return true;
        }

        return false;
    }

    @Override
    public String getName() {
        return "SKULL";
    }

    @Override
    public double getDeformationFactor() {
        return 0.5;
    }

    @Override
    public String getDisplayName() {
        return "Kafatası";
    }
}