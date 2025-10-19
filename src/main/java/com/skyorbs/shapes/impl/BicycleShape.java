package com.skyorbs.shapes.impl;

import com.skyorbs.shapes.PlanetShape;

public class BicycleShape implements PlanetShape {

    @Override
    public boolean isBlockPart(int dx, int dy, int dz, int radius, long seed) {
        // Bisiklet şekli - frame, wheels, handlebars, seat
        double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
        if (distance > radius) return false;

        // Normalize coordinates
        double x = dx / (double)radius;
        double y = dy / (double)radius;
        double z = dz / (double)radius;

        // Main frame triangle (ana üçgen şasi)
        if (Math.abs(z) <= 0.05) {
            // Top tube
            if (Math.abs(y - 0.1) <= 0.03 && x >= -0.2 && x <= 0.2) return true;
            // Seat tube
            if (Math.abs(x + 0.15) <= 0.03 && y >= -0.1 && y <= 0.1) return true;
            // Down tube
            if (Math.abs(x - 0.15) <= 0.03 && y >= -0.1 && y <= 0.1) return true;
        }

        // Seat (sele)
        if (Math.abs(x + 0.15) <= 0.08 && y > 0.1 && y < 0.2) {
            return Math.abs(z) <= 0.1;
        }

        // Handlebars (direksiyon)
        if (Math.abs(x - 0.15) <= 0.08 && Math.abs(y - 0.12) <= 0.03) {
            return Math.abs(z) <= 0.15;
        }

        // Front wheel (ön tekerlek)
        double frontWheelDist = Math.sqrt((x - 0.25)*(x - 0.25) + (y + 0.08)*(y + 0.08));
        if (frontWheelDist <= 0.12) return true;

        // Rear wheel (arka tekerlek)
        double rearWheelDist = Math.sqrt((x + 0.25)*(x + 0.25) + (y + 0.08)*(y + 0.08));
        if (rearWheelDist <= 0.12) return true;

        // Pedals/crank (pedallar)
        if (Math.abs(y) <= 0.05 && Math.abs(z) <= 0.03) {
            if (Math.abs(x) <= 0.08) return true;
        }

        return false;
    }

    @Override
    public String getName() {
        return "BICYCLE";
    }

    @Override
    public double getDeformationFactor() {
        return 0.8;
    }

    @Override
    public String getDisplayName() {
        return "Bisiklet";
    }
}