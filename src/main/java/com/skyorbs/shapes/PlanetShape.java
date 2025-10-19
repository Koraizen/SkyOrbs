package com.skyorbs.shapes;

public interface PlanetShape {
    
    boolean isBlockPart(int dx, int dy, int dz, int radius, long seed);
    
    String getName();
    
    double getDeformationFactor();
    
    String getDisplayName();
}
