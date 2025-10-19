package com.skyorbs.shapes.impl;

import com.skyorbs.shapes.PlanetShape;

public class SubmarineShape implements PlanetShape {

    @Override
    public boolean isBlockPart(int dx, int dy, int dz, int radius, long seed) {
        // Denizaltı şekli - hull, conning tower, propellers
        double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
        if (distance > radius) return false;

        // Normalize coordinates
        double x = dx / (double)radius;
        double y = dy / (double)radius;
        double z = dz / (double)radius;

        // Main hull (ana gövde) - silindir
        if (Math.abs(y) <= 0.15) {
            return Math.sqrt(x*x + z*z) <= 0.4;
        }

        // Nose cone (burun)
        if (x > 0.3) {
            double noseRadius = 0.4 - (x - 0.3) * 2;
            return Math.sqrt((x - 0.3)*(x - 0.3) + y*y + z*z) <= noseRadius;
        }

        // Tail cone (kuyruk)
        if (x < -0.3) {
            double tailRadius = 0.4 + (x + 0.3) * 2;
            return Math.sqrt((x + 0.3)*(x + 0.3) + y*y + z*z) <= tailRadius;
        }

        // Conning tower (köprü)
        if (Math.abs(x) <= 0.2 && y > 0.15 && y < 0.4) {
            return Math.abs(z) <= 0.15;
        }

        // Propellers (pervaneler)
        if (x < -0.4) {
            double propRadius = 0.12;
            // Top propeller
            if (Math.abs(y - 0.08) <= propRadius && Math.abs(z) <= propRadius) return true;
            // Bottom propeller
            if (Math.abs(y + 0.08) <= propRadius && Math.abs(z) <= propRadius) return true;
        }

        return false;
    }

    @Override
    public String getName() {
        return "SUBMARINE";
    }

    @Override
    public double getDeformationFactor() {
        return 0.7;
    }

    @Override
    public String getDisplayName() {
        return "Denizaltı";
    }
}