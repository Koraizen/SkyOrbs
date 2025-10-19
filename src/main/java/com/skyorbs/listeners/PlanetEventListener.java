package com.skyorbs.listeners;

import com.skyorbs.SkyOrbs;
import com.skyorbs.core.Orb;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Listens to player actions and grants planet XP
 */
public class PlanetEventListener implements Listener {
    
    private final SkyOrbs plugin;
    
    // Track player's current planet
    private final Map<UUID, UUID> playerPlanetMap = new HashMap<>();
    
    public PlanetEventListener(SkyOrbs plugin) {
        this.plugin = plugin;
    }
    
    /**
     * Track which planet player is on
     */
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        // Only check every 5 blocks
        Location from = event.getFrom();
        Location to = event.getTo();
        if (to == null || from.distanceSquared(to) < 25) return;
        
        Player player = event.getPlayer();
        Orb nearestPlanet = findNearestPlanet(player);
        
        if (nearestPlanet != null) {
            playerPlanetMap.put(player.getUniqueId(), nearestPlanet.getId());
        } else {
            playerPlanetMap.remove(player.getUniqueId());
        }
    }
    
    /**
     * Grant XP for mining
     */
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (event.isCancelled()) return;
        
        Player player = event.getPlayer();
        UUID planetId = playerPlanetMap.get(player.getUniqueId());
        
        if (planetId == null) return;
        
        try {
            Orb planet = plugin.getDatabaseManager().getOrb(planetId);
            if (planet == null) return;
            
            // Grant XP based on block type
            int xp = calculateBlockBreakXP(event.getBlock().getType());
            planet.addXp(xp);
            
            // Update biosphere (mining reduces balance)
            plugin.getBiosphereManager().updateBiosphere(planet, "mining");
            
            // Save changes
            plugin.getDatabaseManager().saveOrb(planet);
            
            // Check for level up
            if (planet.canUpgrade()) {
                player.sendMessage("§a§l✨ Gezegen " + planet.getName() + " seviye atladı!");
                player.sendMessage("§eYeni seviye: §6" + planet.getCoreLevel());
            }
            
        } catch (Exception e) {
            plugin.logError("Error granting planet XP", e);
        }
    }
    
    /**
     * Grant XP for planting
     */
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (event.isCancelled()) return;
        
        Player player = event.getPlayer();
        UUID planetId = playerPlanetMap.get(player.getUniqueId());
        
        if (planetId == null) return;
        
        try {
            Orb planet = plugin.getDatabaseManager().getOrb(planetId);
            if (planet == null) return;
            
            // Grant XP for planting
            int xp = calculateBlockPlaceXP(event.getBlock().getType());
            if (xp > 0) {
                planet.addXp(xp);
                
                // Update biosphere (planting improves balance)
                plugin.getBiosphereManager().updateBiosphere(planet, "planting");
                
                // Save changes
                plugin.getDatabaseManager().saveOrb(planet);
            }
            
        } catch (Exception e) {
            plugin.logError("Error granting planet XP", e);
        }
    }
    
    /**
     * Find nearest planet to player
     */
    private Orb findNearestPlanet(Player player) {
        Location loc = player.getLocation();
        Orb nearestOrb = null;
        double minDistance = Double.MAX_VALUE;

        try {
            for (Orb orb : plugin.getDatabaseManager().getAllOrbs()) {
                if (!orb.getWorldName().equals(loc.getWorld().getName())) continue;

                double distance = Math.sqrt(
                    Math.pow(loc.getX() - orb.getCenterX(), 2) +
                    Math.pow(loc.getY() - orb.getCenterY(), 2) +
                    Math.pow(loc.getZ() - orb.getCenterZ(), 2)
                );

                // Within planet radius + 50 blocks and closer than current nearest
                if (distance <= orb.getRadius() + 50 && distance < minDistance) {
                    minDistance = distance;
                    nearestOrb = orb;
                }
            }
        } catch (Exception e) {
            // Ignore
        }

        return nearestOrb;
    }
    
    /**
     * Calculate XP for block break
     */
    private int calculateBlockBreakXP(org.bukkit.Material material) {
        return switch (material) {
            case DIAMOND_ORE, DEEPSLATE_DIAMOND_ORE -> 50;
            case EMERALD_ORE, DEEPSLATE_EMERALD_ORE -> 75;
            case ANCIENT_DEBRIS -> 100;
            case GOLD_ORE, DEEPSLATE_GOLD_ORE -> 20;
            case IRON_ORE, DEEPSLATE_IRON_ORE -> 10;
            case COAL_ORE, DEEPSLATE_COAL_ORE -> 5;
            case STONE, DEEPSLATE -> 1;
            default -> 0;
        };
    }
    
    /**
     * Calculate XP for block place
     */
    private int calculateBlockPlaceXP(org.bukkit.Material material) {
        // Only grant XP for plants/trees
        if (material.name().contains("SAPLING")) return 10;
        if (material.name().contains("LEAVES")) return 2;
        if (material == org.bukkit.Material.GRASS_BLOCK) return 5;
        if (material.name().contains("FLOWER")) return 3;
        
        return 0;
    }
}