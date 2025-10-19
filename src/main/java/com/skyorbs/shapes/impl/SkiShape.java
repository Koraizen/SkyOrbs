package com.skyorbs.shapes.impl;

import com.skyorbs.shapes.PlanetShape;

public class SkiShape implements PlanetShape {

    @Override
    public boolean isBlockPart(int dx, int dy, int dz, int radius, long seed) {
        // Kayak şekli - ski, bindings, edges
        double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
        if (distance > radius) return false;

        // Normalize coordinates
        double x = dx / (double)radius;
        double y = dy / (double)radius;
        double z = dz / (double)radius;

        // Main ski (ana kayak)
        if (Math.abs(y) <= 0.02) {
            // Tapered shape - wider at center, narrower at ends
            double width = 0.08 + (0.12 - Math.abs(x) * 0.2);
            return Math.abs(z) <= width && Math.abs(x) <= 0.6;
        }

        // Ski edges (kenarlar) - raised
        if (Math.abs(y) <= 0.04) {
            double width = 0.08 + (0.12 - Math.abs(x) * 0.2);
            return Math.abs(z) <= width && Math.abs(x) <= 0.6;
        }

        // Ski tips (uçlar) - upturned
        if (Math.abs(x) > 0.5) {
            double tipAngle = (Math.abs(x) - 0.5) * 0.3;
            if (y > tipAngle - 0.02 && y < tipAngle + 0.02) {
                return Math.abs(z) <= 0.06;
            }
        }

        // Bindings (bağlamalar)
        double bindingWidth = 0.06;
        double bindingLength = 0.04;
        if (Math.abs(x) <= 0.1 && Math.abs(y - 0.05) <= 0.03) {
            return Math.abs(z) <= bindingWidth;
        }

        // Binding straps (kayışlar)
        if (Math.abs(y - 0.08) <= 0.02) {
            if (Math.abs(x) <= 0.12 && Math.abs(z) <= 0.08) return true;
        }

        return false;
    }

    @Override
    public String getName() {
        return "SKI";
    }

    @Override
    public double getDeformationFactor() {
        return 0.9;
    }

    @Override
    public String getDisplayName() {
        return "Kayak";
    }
}