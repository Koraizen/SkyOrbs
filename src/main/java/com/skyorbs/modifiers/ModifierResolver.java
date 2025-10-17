package com.skyorbs.modifiers;

import java.util.*;

/**
 * Resolves which modifiers apply to a planet
 * Enables massive diversity through modifier combinations
 */
public class ModifierResolver {
    
    private final Random random;
    
    public ModifierResolver(long seed) {
        this.random = new Random(seed);
    }
    
    /**
     * Roll for modifiers based on probabilities
     * Each modifier is rolled independently
     * @return Set of active modifiers for this planet
     */
    public Set<PlanetModifier> resolveModifiers() {
        Set<PlanetModifier> active = new HashSet<>();
        
        for (PlanetModifier modifier : PlanetModifier.values()) {
            if (random.nextDouble() < modifier.getProbability()) {
                active.add(modifier);
            }
        }
        
        // Ensure incompatible modifiers don't coexist
        resolveConflicts(active);
        
        return active;
    }
    
    /**
     * Remove conflicting modifiers
     */
    private void resolveConflicts(Set<PlanetModifier> modifiers) {
        // MAGMA_CORE and ICY_CORE are mutually exclusive
        if (modifiers.contains(PlanetModifier.MAGMA_CORE) && 
            modifiers.contains(PlanetModifier.ICY_CORE)) {
            // Keep random one
            if (random.nextBoolean()) {
                modifiers.remove(PlanetModifier.ICY_CORE);
            } else {
                modifiers.remove(PlanetModifier.MAGMA_CORE);
            }
        }
        
        // DENSE and POROUS are opposite
        if (modifiers.contains(PlanetModifier.DENSE) && 
            modifiers.contains(PlanetModifier.POROUS)) {
            if (random.nextBoolean()) {
                modifiers.remove(PlanetModifier.POROUS);
            } else {
                modifiers.remove(PlanetModifier.DENSE);
            }
        }
    }
    
    /**
     * Get ore density multiplier
     */
    public double getOreDensityMultiplier(Set<PlanetModifier> modifiers) {
        double multiplier = 1.0;
        
        if (modifiers.contains(PlanetModifier.ORE_RICH)) {
            multiplier *= 2.0;
        }
        
        if (modifiers.contains(PlanetModifier.DENSE)) {
            multiplier *= 1.5;
        }
        
        if (modifiers.contains(PlanetModifier.POROUS)) {
            multiplier *= 0.7;
        }
        
        return multiplier;
    }
    
    /**
     * Get shell thickness for hollow planets
     */
    public int getShellThickness(Set<PlanetModifier> modifiers, int baseThickness) {
        if (modifiers.contains(PlanetModifier.HOLLOW)) {
            if (modifiers.contains(PlanetModifier.DENSE)) {
                return baseThickness + 2;
            }
            return baseThickness;
        }
        return -1; // Not hollow
    }
    
    /**
     * Check if planet should have tunnels
     */
    public boolean shouldHaveTunnels(Set<PlanetModifier> modifiers) {
        return modifiers.contains(PlanetModifier.POROUS) || 
               modifiers.contains(PlanetModifier.HOLLOW);
    }
    
    /**
     * Get number of tunnels
     */
    public int getTunnelCount(Set<PlanetModifier> modifiers) {
        if (modifiers.contains(PlanetModifier.POROUS)) {
            return 5 + random.nextInt(5); // 5-10 tunnels
        }
        if (modifiers.contains(PlanetModifier.HOLLOW)) {
            return 2 + random.nextInt(3); // 2-5 tunnels
        }
        return 0;
    }
}
