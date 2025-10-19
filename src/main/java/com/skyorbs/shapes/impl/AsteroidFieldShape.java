package com.skyorbs.shapes.impl;

import com.skyorbs.shapes.PlanetShape;
import com.skyorbs.utils.NoiseGenerator;

import java.util.Random;

/**
 * ASTEROID_FIELD SHAPE - Asteroid alanı (çoklu küçük kümeler)
 * Creates multiple small asteroid clusters instead of a single body
 */
public class AsteroidFieldShape implements PlanetShape {

    @Override
    public boolean isBlockPart(int dx, int dy, int dz, int radius, long seed) {
        Random random = new Random(seed);
        
        // Create multiple small asteroid clusters
        int clusterCount = 5;
        
        for (int i = 0; i < clusterCount; i++) {
            Random clusterRandom = new Random(seed + i * 1000);
            
            // Cluster center offset
            int offsetX = clusterRandom.nextInt(radius) - radius/2;
            int offsetY = clusterRandom.nextInt(radius) - radius/2;
            int offsetZ = clusterRandom.nextInt(radius) - radius/2;
            
            int cdx = dx - offsetX;
            int cdy = dy - offsetY;
            int cdz = dz - offsetZ;
            
            double clusterDist = Math.sqrt(cdx * cdx + cdy * cdy + cdz * cdz);
            double clusterSize = radius * 0.3;
            
            // Rough asteroid surface
            double noise = NoiseGenerator.getNoise(cdx, cdy, cdz, seed + i, 0.3);
            double roughness = 0.6 + 0.4 * Math.abs(noise);
            
            if (clusterDist <= clusterSize * roughness) {
                return true;
            }
        }
        
        return false;
    }

    @Override
    public String getName() { 
        return "ASTEROID_FIELD"; 
    }

    @Override
    public double getDeformationFactor() { 
        return 0.5; 
    }

    @Override
    public String getDisplayName() { 
        return "Asteroid Alanı"; 
    }
}
