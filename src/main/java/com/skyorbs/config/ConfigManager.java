package com.skyorbs.config;

import com.skyorbs.SkyOrbs;
import org.bukkit.configuration.ConfigurationSection;

import java.util.*;

public class ConfigManager {
    
    private final SkyOrbs plugin;
    
    public ConfigManager(SkyOrbs plugin) {
        this.plugin = plugin;
    }
    
    // Gezegen boyut ayarları
    public int getMinRadius() {
        return plugin.getConfig().getInt("generation.planetSize.minRadius", 80);
    }
    
    public int getMaxRadius() {
        return plugin.getConfig().getInt("generation.planetSize.maxRadius", 250);
    }
    
    public int getAverageRadius() {
        return plugin.getConfig().getInt("generation.planetSize.averageRadius", 150);
    }
    
    // Yerleştirme ayarları
    public int getMaxDistance() {
        return plugin.getConfig().getInt("placement.worldBounds.maxDistance", 3000);
    }
    
    public int getCenterX() {
        return plugin.getConfig().getInt("placement.worldBounds.centerX", 0);
    }
    
    public int getCenterZ() {
        return plugin.getConfig().getInt("placement.worldBounds.centerZ", 0);
    }
    
    public int getMinDistanceFromSpawn() {
        return plugin.getConfig().getInt("placement.worldBounds.minDistanceFromSpawn", 1000);
    }
    
    public boolean isClusteringEnabled() {
        return plugin.getConfig().getBoolean("placement.clustering.enabled", true);
    }
    
    public int getClusterMinDistance() {
        return plugin.getConfig().getInt("placement.clustering.clusterMinDistance", 800);
    }
    
    public int getClusterMaxDistance() {
        return plugin.getConfig().getInt("placement.clustering.clusterMaxDistance", 2000);
    }
    
    public double getCenterAttraction() {
        return plugin.getConfig().getDouble("placement.attractToCenter.centerAttraction", 0.3);
    }
    
    // Şekil ağırlıkları
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
    
    // Asteroid ayarları
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
    
    // Uydu ayarları
    public boolean isSatellitesEnabled() {
        return plugin.getConfig().getBoolean("satellites.enabled", true);
    }
    
    public int getMinSatellitesPerPlanet() {
        return plugin.getConfig().getInt("satellites.perPlanet.minCount", 0);
    }
    
    public int getMaxSatellitesPerPlanet() {
        return plugin.getConfig().getInt("satellites.perPlanet.maxCount", 3);
    }
    
    public double getSatelliteProbability() {
        return plugin.getConfig().getDouble("satellites.perPlanet.probability", 0.6);
    }
    
    // Performans ayarları
    public boolean isAsyncGenerationEnabled() {
        return plugin.getConfig().getBoolean("performance.asyncGeneration", true);
    }
    
    public int getThreadPoolCoreSize() {
        return plugin.getConfig().getInt("performance.threadPool.coreSize", 4);
    }
    
    public int getThreadPoolMaxSize() {
        return plugin.getConfig().getInt("performance.threadPool.maxSize", 8);
    }
    
    public int getBlocksPerBatch() {
        return plugin.getConfig().getInt("performance.batching.blocksPerBatch", 500);
    }
    
    public int getBatchesPerTick() {
        return plugin.getConfig().getInt("performance.batching.batchesPerTick", 5);
    }
    
    // Limit ayarları
    public int getPlanetsPerPlayer() {
        return plugin.getConfig().getInt("limits.planetsPerPlayer", 2);
    }
    
    public boolean canDeleteOwnPlanet() {
        return plugin.getConfig().getBoolean("limits.deleteOwnPlanet", false);
    }
    
    // Rastgele isim oluşturma
    public String generateRandomName() {
        List<String> prefixes = plugin.getConfig().getStringList("planetNames.prefixes");
        List<String> suffixes = plugin.getConfig().getStringList("planetNames.suffixes");
        List<String> postfixes = plugin.getConfig().getStringList("planetNames.postfixes");
        List<String> formats = plugin.getConfig().getStringList("planetNames.formats");
        
        if (prefixes.isEmpty() || suffixes.isEmpty() || formats.isEmpty()) {
            return "Gezegen-" + UUID.randomUUID().toString().substring(0, 8);
        }
        
        Random random = new Random();
        String prefix = prefixes.get(random.nextInt(prefixes.size()));
        String suffix = suffixes.get(random.nextInt(suffixes.size()));
        String format = formats.get(random.nextInt(formats.size()));
        
        String name = format.replace("{prefix}", prefix).replace("{suffix}", suffix);
        
        if (name.contains("{postfix}") && !postfixes.isEmpty()) {
            String postfix = postfixes.get(random.nextInt(postfixes.size()));
            name = name.replace("{postfix}", postfix);
        }
        
        if (name.contains("{number}")) {
            int min = plugin.getConfig().getInt("planetNames.numberRange.min", 1);
            int max = plugin.getConfig().getInt("planetNames.numberRange.max", 999);
            int number = min + random.nextInt(max - min + 1);
            name = name.replace("{number}", String.valueOf(number));
        }
        
        return name;
    }
    
    // Mesaj sistemi
    public String getMessage(String key) {
        String prefix = plugin.getConfig().getString("messages.prefix", "&8[&bGezegen&8]&r");
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
