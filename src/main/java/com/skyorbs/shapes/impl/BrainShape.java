package com.skyorbs.shapes.impl;

import com.skyorbs.shapes.PlanetShape;

public class BrainShape implements PlanetShape {

    @Override
    public boolean isBlockPart(int dx, int dy, int dz, int radius, long seed) {
        // Beyin şekli - convoluted surface, gyri and sulci
        double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
        if (distance > radius) return false;

        // Normalize coordinates
        double x = dx / (double)radius;
        double y = dy / (double)radius;
        double z = dz / (double)radius;

        // Base brain shape - oval
        double baseRadius = Math.sqrt(x*x + y*y + z*z);
        if (baseRadius > 0.9) return false;

        // Add convolutions (beyin kıvrımları)
        double convolution1 = Math.sin(x * 8) * Math.cos(z * 8) * 0.1;
        double convolution2 = Math.sin(y * 6) * Math.sin(x * 6) * 0.08;
        double convolution3 = Math.cos(z * 10) * Math.sin(y * 10) * 0.06;

        double totalConvolution = convolution1 + convolution2 + convolution3;
        double brainSurface = 0.7 + totalConvolution;

        return baseRadius <= brainSurface;
    }

    @Override
    public String getName() {
        return "BRAIN";
    }

    @Override
    public double getDeformationFactor() {
        return 0.9;
    }

    @Override
    public String getDisplayName() {
        return "Beyin";
    }
}