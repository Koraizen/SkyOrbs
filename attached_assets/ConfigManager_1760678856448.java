package com.skyorbs.config;

import com.skyorbs.SkyOrbs;
import org.bukkit.configuration.ConfigurationSection;

import java.util.HashMap;
import java.util.Map;

public class ConfigManager {
    
    private final SkyOrbs plugin;
    
    public ConfigManager(SkyOrbs plugin) {
        this.plugin = plugin;
    }
    
    public int getMinRadius() {
        return plugin.getConfig().getInt("generation.planetSize.minRadius", 80);
    }
    
    public int getMaxRadius() {
        return plugin.getConfig().getInt("generation.planetSize.maxRadius", 250);
    }
    
    public int getAverageRadius() {
        return plugin.getConfig().getInt("generation.planetSize.averageRadius", 150);
    }
    
    public int getMaxDistance() {
        return plugin.getConfig().getInt("placement.worldBounds.maxDistance", 3000);
    }
    
    public int getCenterX() {
        return plugin.getConfig().getInt("placement.worldBounds.centerX", 0);
    }
    
    public int getCenterZ() {
        return plugin.getConfig().getInt("placement.worldBounds.centerZ", 0);
    }
    
    public boolean isClusteringEnabled() {
        return plugin.getConfig().getBoolean("placement.clustering.enabled", true);
    }
    
    public int getClusterMinDistance() {
        return plugin.getConfig().getInt("placement.clustering.clusterMinDistance", 400);
    }
    
    public int getClusterMaxDistance() {
        return plugin.getConfig().getInt("placement.clustering.clusterMaxDistance", 1000);
    }
    
    public double getCenterAttraction() {
        return plugin.getConfig().getDouble("placement.attractToCenter.centerAttraction", 0.6);
    }
    
    public Map<String, Double> getShapeWeights() {
        Map<String, Double> weights = new HashMap<>();
        ConfigurationSection section = plugin.getConfig().getConfigurationSection("shapes.weights");
        if (section != null) {
            for (String key : section.getKeys(false)) {
                weights.put(key, section.getDouble(key));
            }
        }
        return weights;
    }
    
    public boolean isAsteroidsEnabled() {
        return plugin.getConfig().getBoolean("asteroids.enabled", true);
    }
    
    public int getMinAsteroidsPerPlanet() {
        return plugin.getConfig().getInt("asteroids.perPlanet.minCount", 1);
    }
    
    public int getMaxAsteroidsPerPlanet() {
        return plugin.getConfig().getInt("asteroids.perPlanet.maxCount", 5);
    }
    
    public int getMinAsteroidRadius() {
        return plugin.getConfig().getInt("asteroids.size.minRadius", 20);
    }
    
    public int getMaxAsteroidRadius() {
        return plugin.getConfig().getInt("asteroids.size.maxRadius", 50);
    }
    
    public int getMinAsteroidDistance() {
        return plugin.getConfig().getInt("asteroids.placement.minDistanceFromPlanet", 300);
    }
    
    public int getMaxAsteroidDistance() {
        return plugin.getConfig().getInt("asteroids.placement.maxDistanceFromPlanet", 800);
    }
    
    public boolean isAsyncGenerationEnabled() {
        return plugin.getConfig().getBoolean("performance.asyncGeneration", true);
    }
    
    public int getThreadPoolCoreSize() {
        return plugin.getConfig().getInt("performance.threadPool.coreSize", 4);
    }
    
    public int getThreadPoolMaxSize() {
        return plugin.getConfig().getInt("performance.threadPool.maxSize", 8);
    }
    
    public String getMessage(String key) {
        String prefix = plugin.getConfig().getString("messages.prefix", "&8[&bSkyOrbs&8]&r");
        String message = plugin.getConfig().getString("messages." + key, key);
        return colorize(prefix + " " + message);
    }
    
    public String getMessageRaw(String key) {
        return colorize(plugin.getConfig().getString("messages." + key, key));
    }
    
    private String colorize(String text) {
        return text.replace("&", "§");
    }
}
