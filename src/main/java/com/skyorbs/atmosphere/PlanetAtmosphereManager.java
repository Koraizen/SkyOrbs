package com.skyorbs.atmosphere;

import com.skyorbs.SkyOrbs;
import com.skyorbs.core.Orb;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

/**
 * Manages atmospheric effects for planets
 * Applies particles, status effects, and visual effects to players on planets
 */
public class PlanetAtmosphereManager {
    
    private final SkyOrbs plugin;
    private final Map<UUID, AtmosphereType> planetAtmospheres = new HashMap<UUID, AtmosphereType>();
    private BukkitRunnable atmosphereTask;
    
    public PlanetAtmosphereManager(SkyOrbs plugin) {
        this.plugin = plugin;
    }
    
    /**
     * Start atmosphere effects task
     */
    public void start() {
        atmosphereTask = new BukkitRunnable() {
            @Override
            public void run() {
                processAtmosphereEffects();
            }
        };
        atmosphereTask.runTaskTimer(plugin, 20L, 20L); // Run every second
    }
    
    /**
     * Stop atmosphere effects task
     */
    public void stop() {
        if (atmosphereTask != null) {
            atmosphereTask.cancel();
            atmosphereTask = null;
        }
    }
    
    /**
     * Register atmosphere for a planet
     */
    public void registerPlanetAtmosphere(UUID planetId, AtmosphereType atmosphere) {
        planetAtmospheres.put(planetId, atmosphere);
    }
    
    /**
     * Get atmosphere for a planet
     */
    public AtmosphereType getPlanetAtmosphere(UUID planetId) {
        return planetAtmospheres.getOrDefault(planetId, AtmosphereType.CLEAR);
    }
    
    /**
     * Process atmospheric effects for all online players
     */
    private void processAtmosphereEffects() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            Orb nearestPlanet = findNearestPlanet(player);
            
            if (nearestPlanet != null) {
                AtmosphereType atmosphere = planetAtmospheres.get(nearestPlanet.getId());
                if (atmosphere != null) {
                    applyAtmosphereEffects(player, nearestPlanet, atmosphere);
                }
            }
        }
    }
    
    /**
     * Find nearest planet to player
     */
    private Orb findNearestPlanet(Player player) {
        Location playerLoc = player.getLocation();
        List<Orb> allPlanets;
        
        try {
            allPlanets = plugin.getDatabaseManager().getAllOrbs();
        } catch (Exception e) {
            return null; // Return null if database error
        }
        
        Orb nearest = null;
        double minDistance = Double.MAX_VALUE;
        
        for (Orb orb : allPlanets) {
            if (!orb.getWorldName().equals(playerLoc.getWorld().getName())) continue;
            
            double distance = Math.sqrt(
                Math.pow(playerLoc.getX() - orb.getCenterX(), 2) +
                Math.pow(playerLoc.getY() - orb.getCenterY(), 2) +
                Math.pow(playerLoc.getZ() - orb.getCenterZ(), 2)
            );
            
            // Check if player is within planet's atmosphere radius (1.5x planet radius)
            if (distance <= orb.getRadius() * 1.5 && distance < minDistance) {
                minDistance = distance;
                nearest = orb;
            }
        }
        
        return nearest;
    }
    
    /**
     * Apply atmosphere effects to player
     */
    private void applyAtmosphereEffects(Player player, Orb planet, AtmosphereType atmosphere) {
        // Apply potion effects
        if (atmosphere.hasEffects()) {
            player.addPotionEffect(new PotionEffect(
                atmosphere.getEffectType(),
                100,  // 5 seconds duration (refresh every second)
                atmosphere == AtmosphereType.CORROSIVE ? 2 : 1, // Stronger corrosive
                true,  // Ambient
                true,  // Particles
                true   // Icon
            ));
        }
        
        // Spawn particles around player
        if (atmosphere.hasParticles()) {
            spawnAtmosphereParticles(player, atmosphere);
        }
        
        // Apply glow effect
        if (atmosphere.hasGlow()) {
            player.addPotionEffect(new PotionEffect(
                PotionEffectType.NIGHT_VISION,
                200,  // 10 seconds
                0,
                true,
                false,
                false
            ));
        }
    }
    
    /**
     * Spawn atmospheric particles around player
     */
    private void spawnAtmosphereParticles(Player player, AtmosphereType atmosphere) {
        Location loc = player.getLocation();
        World world = loc.getWorld();
        Random random = new Random();
        
        // Spawn particles in a radius around player
        for (int i = 0; i < 5; i++) {
            double offsetX = (random.nextDouble() - 0.5) * 4;
            double offsetY = (random.nextDouble() - 0.5) * 3;
            double offsetZ = (random.nextDouble() - 0.5) * 4;
            
            Location particleLoc = loc.clone().add(offsetX, offsetY, offsetZ);
            
            world.spawnParticle(
                atmosphere.getParticleType(),
                particleLoc,
                1,     // Count
                0,     // Offset X
                0,     // Offset Y
                0,     // Offset Z
                0.01   // Speed
            );
        }
    }
    
    /**
     * Select random atmosphere based on probabilities
     */
    public AtmosphereType selectRandomAtmosphere(Random random) {
        double totalWeight = 0.0;
        for (AtmosphereType type : AtmosphereType.values()) {
            totalWeight += type.getProbability();
        }
        
        double value = random.nextDouble() * totalWeight;
        double current = 0.0;
        
        for (AtmosphereType type : AtmosphereType.values()) {
            current += type.getProbability();
            if (value <= current) {
                return type;
            }
        }
        
        return AtmosphereType.CLEAR;
    }
}
