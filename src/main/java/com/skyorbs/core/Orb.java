package com.skyorbs.core;

import java.util.UUID;
import java.util.Set;
import java.util.HashSet;
import com.skyorbs.modifiers.PlanetModifier;
import com.skyorbs.atmosphere.AtmosphereType;

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

    // Dynamic Core System
    private int coreLevel;
    private double energyLevel;
    private long xp;
    private PlanetType planetType;
    private int biosphereLevel;
    private double ecologicalBalance;
    
    // Advanced planet features
    private String paletteId;
    private Set<PlanetModifier> modifiers;
    private AtmosphereType atmosphere;
    
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

        // Initialize dynamic core system
        this.coreLevel = 1;
        this.energyLevel = 100.0;
        this.xp = 0;
        this.planetType = PlanetType.TERRESTRIAL; // Default type
        this.biosphereLevel = 1;
        this.ecologicalBalance = 1.0;
        
        // Initialize advanced features
        this.paletteId = null;
        this.modifiers = new HashSet<>();
        this.atmosphere = AtmosphereType.CLEAR;
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
    
    public double getDistanceFromSpawn() {
        return Math.sqrt(centerX * centerX + centerZ * centerZ);
    }

    // Dynamic Core System Getters and Setters
    public int getCoreLevel() {
        return coreLevel;
    }

    public void setCoreLevel(int coreLevel) {
        this.coreLevel = Math.max(1, Math.min(10, coreLevel));
    }

    public double getEnergyLevel() {
        return energyLevel;
    }

    public void setEnergyLevel(double energyLevel) {
        this.energyLevel = Math.max(0.0, Math.min(100.0, energyLevel));
    }

    public long getXp() {
        return xp;
    }

    public void addXp(long xp) {
        this.xp += xp;
        // Check for level up
        int newLevel = calculateCoreLevelFromXp(this.xp);
        if (newLevel > this.coreLevel) {
            setCoreLevel(newLevel);
        }
    }

    public PlanetType getPlanetType() {
        return planetType;
    }

    public void setPlanetType(PlanetType planetType) {
        this.planetType = planetType;
    }

    public int getBiosphereLevel() {
        return biosphereLevel;
    }

    public void setBiosphereLevel(int biosphereLevel) {
        this.biosphereLevel = Math.max(1, Math.min(5, biosphereLevel));
    }

    public double getEcologicalBalance() {
        return ecologicalBalance;
    }

    public void setEcologicalBalance(double ecologicalBalance) {
        this.ecologicalBalance = Math.max(0.0, Math.min(2.0, ecologicalBalance));
    }

    // Helper methods
    private int calculateCoreLevelFromXp(long xp) {
        // Level calculation: level = floor(sqrt(xp / 1000)) + 1
        return (int) Math.floor(Math.sqrt(xp / 1000.0)) + 1;
    }

    public long getXpForNextLevel() {
        int nextLevel = coreLevel + 1;
        return (long) ((nextLevel - 1) * (nextLevel - 1) * 1000);
    }

    public double getEnergyRegenRate() {
        return 0.1 * coreLevel; // Higher levels regenerate faster
    }

    public boolean canUpgrade() {
        return xp >= getXpForNextLevel();
    }
    
    // Advanced features getters and setters
    public String getPaletteId() {
        return paletteId;
    }
    
    public void setPaletteId(String paletteId) {
        this.paletteId = paletteId;
    }
    
    public Set<PlanetModifier> getModifiers() {
        return modifiers;
    }
    
    public void setModifiers(Set<PlanetModifier> modifiers) {
        this.modifiers = modifiers != null ? modifiers : new HashSet<>();
    }
    
    public void addModifier(PlanetModifier modifier) {
        this.modifiers.add(modifier);
    }
    
    public boolean hasModifier(PlanetModifier modifier) {
        return modifiers.contains(modifier);
    }
    
    public AtmosphereType getAtmosphere() {
        return atmosphere;
    }
    
    public void setAtmosphere(AtmosphereType atmosphere) {
        this.atmosphere = atmosphere != null ? atmosphere : AtmosphereType.CLEAR;
    }
    
    public boolean isHollow() {
        return modifiers.contains(PlanetModifier.HOLLOW);
    }
    
    public boolean isOreRich() {
        return modifiers.contains(PlanetModifier.ORE_RICH);
    }
}
