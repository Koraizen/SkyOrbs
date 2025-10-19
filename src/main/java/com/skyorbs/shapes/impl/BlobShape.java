package com.skyorbs.shapes.impl;

import com.skyorbs.shapes.PlanetShape;
import com.skyorbs.utils.NoiseGenerator;

public class BlobShape implements PlanetShape {
    
    @Override
    public boolean isBlockPart(int dx, int dy, int dz, int radius, long seed) {
        double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
        double noise = NoiseGenerator.getNoise(dx, dy, dz, seed, 0.15);
        double deformation = 1 + 0.3 * noise;
        return distance <= radius * deformation;
    }
    
    @Override
    public String getName() {
        return "BLOB";
    }
    
    @Override
    public double getDeformationFactor() {
        return 0.3;
    }
    
    @Override
    public String getDisplayName() {
        return "Ameba";
    }
}
