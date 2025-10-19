package com.skyorbs.shapes.impl;

import com.skyorbs.shapes.PlanetShape;

public class CloudShape implements PlanetShape {

    @Override
    public boolean isBlockPart(int dx, int dy, int dz, int radius, long seed) {
        // Bulut şekli - yumuşak, yuvarlak
        double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
        if (distance > radius) return false;

        // Normalize coordinates
        double x = dx / (double)radius;
        double y = dy / (double)radius;
        double z = dz / (double)radius;

        // Cloud formülü - yumuşak yuvarlak
        double cloudDensity = Math.exp(-(x*x + y*y + z*z)) +
                             Math.exp(-((x-0.3)*(x-0.3) + (y+0.2)*(y+0.2) + z*z)) * 0.7 +
                             Math.exp(-((x+0.3)*(x+0.3) + (y-0.2)*(y-0.2) + z*z)) * 0.7;

        return cloudDensity > 0.3;
    }

    @Override
    public String getName() {
        return "CLOUD";
    }

    @Override
    public double getDeformationFactor() {
        return 0.2;
    }

    @Override
    public String getDisplayName() {
        return "Bulut";
    }
}