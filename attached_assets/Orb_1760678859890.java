package com.skyorbs.core;

import java.util.UUID;

public class Orb {
    
    private final UUID id;
    private final String name;
    private final String worldName;
    private final int centerX;
    private final int centerY;
    private final int centerZ;
    private final int radius;
    private final String shapeName;
    private final String biomeName;
    private final long seed;
    private final long createdAt;
    private final boolean isAsteroid;
    private final UUID parentId;
    
    public Orb(UUID id, String name, String worldName, int centerX, int centerY, int centerZ,
               int radius, String shapeName, String biomeName, long seed, long createdAt,
               boolean isAsteroid, UUID parentId) {
        this.id = id;
        this.name = name;
        this.worldName = worldName;
        this.centerX = centerX;
        this.centerY = centerY;
        this.centerZ = centerZ;
        this.radius = radius;
        this.shapeName = shapeName;
        this.biomeName = biomeName;
        this.seed = seed;
        this.createdAt = createdAt;
        this.isAsteroid = isAsteroid;
        this.parentId = parentId;
    }
    
    public UUID getId() {
        return id;
    }
    
    public String getName() {
        return name;
    }
    
    public String getWorldName() {
        return worldName;
    }
    
    public int getCenterX() {
        return centerX;
    }
    
    public int getCenterY() {
        return centerY;
    }
    
    public int getCenterZ() {
        return centerZ;
    }
    
    public int getRadius() {
        return radius;
    }
    
    public String getShapeName() {
        return shapeName;
    }
    
    public String getBiomeName() {
        return biomeName;
    }
    
    public long getSeed() {
        return seed;
    }
    
    public long getCreatedAt() {
        return createdAt;
    }
    
    public boolean isAsteroid() {
        return isAsteroid;
    }
    
    public UUID getParentId() {
        return parentId;
    }
    
    public double getDistanceFrom(int x, int z) {
        int dx = x - centerX;
        int dz = z - centerZ;
        return Math.sqrt(dx * dx + dz * dz);
    }
}
