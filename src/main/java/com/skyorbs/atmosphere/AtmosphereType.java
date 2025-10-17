package com.skyorbs.atmosphere;

import org.bukkit.Color;
import org.bukkit.Particle;
import org.bukkit.potion.PotionEffectType;

/**
 * Different atmosphere types for planets
 * Provides visual and gameplay effects
 */
public enum AtmosphereType {
    
    CLEAR("Clear", "Temiz", 0.4, 
        null, null, false, false),
    
    LUMINOUS("Luminous", "Parlak", 0.2,
        Particle.END_ROD, PotionEffectType.NIGHT_VISION, false, true),
    
    TOXIC("Toxic", "Zehirli", 0.15,
        Particle.DRIPPING_LAVA, PotionEffectType.POISON, true, false),
    
    STORMY("Stormy", "Fırtınalı", 0.1,
        Particle.CLOUD, PotionEffectType.SLOWNESS, true, false),
    
    FOGGY("Foggy", "Sisli", 0.08,
        Particle.SUSPENDED_DEPTH, PotionEffectType.BLINDNESS, true, false),
    
    CORROSIVE("Corrosive", "Aşındırıcı", 0.05,
        Particle.PORTAL, PotionEffectType.WITHER, true, false),
    
    AURORA("Aurora", "Aurora", 0.03,
        Particle.END_ROD, PotionEffectType.REGENERATION, false, true),
    
    RADIOACTIVE("Radioactive", "Radyoaktif", 0.02,
        Particle.ELECTRIC_SPARK, PotionEffectType.WITHER, true, false);
    
    private final String name;
    private final String displayName;
    private final double probability;
    private final Particle particleType;
    private final PotionEffectType effectType;
    private final boolean hasFog;
    private final boolean hasGlow;
    
    AtmosphereType(String name, String displayName, double probability,
                   Particle particleType, PotionEffectType effectType,
                   boolean hasFog, boolean hasGlow) {
        this.name = name;
        this.displayName = displayName;
        this.probability = probability;
        this.particleType = particleType;
        this.effectType = effectType;
        this.hasFog = hasFog;
        this.hasGlow = hasGlow;
    }
    
    public String getName() {
        return name;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public double getProbability() {
        return probability;
    }
    
    public Particle getParticleType() {
        return particleType;
    }
    
    public PotionEffectType getEffectType() {
        return effectType;
    }
    
    public boolean hasFog() {
        return hasFog;
    }
    
    public boolean hasGlow() {
        return hasGlow;
    }
    
    public boolean hasEffects() {
        return effectType != null;
    }
    
    public boolean hasParticles() {
        return particleType != null;
    }
    
    /**
     * Get color for atmosphere particles
     */
    public Color getAtmosphereColor() {
        return switch (this) {
            case LUMINOUS, AURORA -> Color.fromRGB(200, 220, 255);
            case TOXIC -> Color.fromRGB(100, 255, 100);
            case STORMY -> Color.fromRGB(128, 128, 128);
            case FOGGY -> Color.fromRGB(200, 200, 200);
            case CORROSIVE -> Color.fromRGB(255, 200, 50);
            case RADIOACTIVE -> Color.fromRGB(0, 255, 0);
            default -> Color.WHITE;
        };
    }
}
