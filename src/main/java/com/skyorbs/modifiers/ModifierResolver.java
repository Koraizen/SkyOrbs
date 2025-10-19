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
        Set<PlanetModifier> active = new HashSet<PlanetModifier>();
        
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
     * Remove conflicting modifiers - EXTENDED CONFLICT RESOLUTION
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

        // FROZEN and VOLCANIC are opposite
        if (modifiers.contains(PlanetModifier.FROZEN) &&
            modifiers.contains(PlanetModifier.VOLCANIC)) {
            if (random.nextBoolean()) {
                modifiers.remove(PlanetModifier.VOLCANIC);
            } else {
                modifiers.remove(PlanetModifier.FROZEN);
            }
        }

        // AQUATIC and SANDY are somewhat opposite
        if (modifiers.contains(PlanetModifier.AQUATIC) &&
            modifiers.contains(PlanetModifier.SANDY)) {
            if (random.nextDouble() < 0.3) { // 30% chance to keep both (oasis)
                // Keep both for interesting combinations
            } else {
                if (random.nextBoolean()) {
                    modifiers.remove(PlanetModifier.SANDY);
                } else {
                    modifiers.remove(PlanetModifier.AQUATIC);
                }
            }
        }

        // VOID and CELESTIAL are mutually exclusive
        if (modifiers.contains(PlanetModifier.VOID) &&
            modifiers.contains(PlanetModifier.CELESTIAL)) {
            if (random.nextBoolean()) {
                modifiers.remove(PlanetModifier.CELESTIAL);
            } else {
                modifiers.remove(PlanetModifier.VOID);
            }
        }

        // RADIOACTIVE and VEGETATED don't mix well
        if (modifiers.contains(PlanetModifier.RADIOACTIVE) &&
            modifiers.contains(PlanetModifier.VEGETATED)) {
            if (random.nextDouble() < 0.2) { // 20% chance to keep both (mutant plants)
                // Keep both for interesting combinations
            } else {
                modifiers.remove(PlanetModifier.VEGETATED);
            }
        }

        // DIVINE and INFERNAL are mutually exclusive
        if (modifiers.contains(PlanetModifier.DIVINE) &&
            modifiers.contains(PlanetModifier.INFERNAL)) {
            if (random.nextBoolean()) {
                modifiers.remove(PlanetModifier.INFERNAL);
            } else {
                modifiers.remove(PlanetModifier.DIVINE);
            }
        }

        // CHAOTIC and ORDERED are mutually exclusive
        if (modifiers.contains(PlanetModifier.CHAOTIC) &&
            modifiers.contains(PlanetModifier.ORDERED)) {
            if (random.nextBoolean()) {
                modifiers.remove(PlanetModifier.ORDERED);
            } else {
                modifiers.remove(PlanetModifier.CHAOTIC);
            }
        }

        // Limit rare modifiers (max 2 per planet)
        int rareCount = 0;
        PlanetModifier[] toRemove = new PlanetModifier[0];
        for (PlanetModifier mod : modifiers) {
            if (mod.getProbability() < 0.1) { // Rare modifier
                rareCount++;
                if (rareCount > 2) {
                    toRemove = java.util.Arrays.copyOf(toRemove, toRemove.length + 1);
                    toRemove[toRemove.length - 1] = mod;
                }
            }
        }
        for (PlanetModifier mod : toRemove) {
            modifiers.remove(mod);
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
