package com.skyorbs.config;

import com.skyorbs.SkyOrbs;
import org.bukkit.configuration.ConfigurationSection;

import java.util.*;

public class ConfigManager {
    
    private final SkyOrbs plugin;
    
    public ConfigManager(SkyOrbs plugin) {
        this.plugin = plugin;
    }
    
    // ============================================
    // GEZEGEN BOYUT AYARLARI
    // ============================================
    
    public int getMinRadius() {
        return plugin.getConfig().getInt("generation.planetSize.minRadius", 80);
    }
    
    public int getMaxRadius() {
        return plugin.getConfig().getInt("generation.planetSize.maxRadius", 250);
    }
    
    public int getAverageRadius() {
        return plugin.getConfig().getInt("generation.planetSize.averageRadius", 150);
    }
    
    // ============================================
    // YERLEŞTİRME AYARLARI
    // ============================================
    
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
    
    // ============================================
    // ŞEKİL AĞIRLIKLARI
    // ============================================
    
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
    
    // ============================================
    // ASTEROID AYARLARI
    // ============================================
    
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
    
    // ============================================
    // UYDU AYARLARI
    // ============================================
    
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
    
    // ============================================
    // PERFORMANS AYARLARI
    // ============================================
    
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
        return plugin.getConfig().getInt("performance.batching.blocksPerBatch", 250);
    }
    
    public int getBatchesPerTick() {
        return plugin.getConfig().getInt("performance.batching.batchesPerTick", 1);
    }
    
    public int getShellThickness() {
        return plugin.getConfig().getInt("performance.generation.shellThickness", 7);
    }
    
    public boolean isChunkPreloadEnabled() {
        return plugin.getConfig().getBoolean("performance.generation.chunkPreload", true);
    }
    
    // ============================================
    // ORE SİSTEMİ AYARLARI
    // ============================================
    
    public boolean isOreGenerationEnabled() {
        return plugin.getConfig().getBoolean("features.ores.enabled", true);
    }
    
    public double getOreMultiplier() {
        return plugin.getConfig().getDouble("features.ores.densityMultiplier", 1.0);
    }
    
    public boolean isDeepslateEnabled() {
        return plugin.getConfig().getBoolean("features.ores.deepslateVariants", true);
    }
    
    public boolean areBiomeBonusesEnabled() {
        return plugin.getConfig().getBoolean("features.ores.biomeBonuses", true);
    }
    
    // ============================================
    // AĞAÇ SİSTEMİ AYARLARI
    // ============================================
    
    public boolean isTreeGenerationEnabled() {
        return plugin.getConfig().getBoolean("features.trees.enabled", true);
    }
    
    public double getTreeDensityMultiplier() {
        return plugin.getConfig().getDouble("features.trees.densityMultiplier", 1.0);
    }
    
    public boolean areTreeSpecialFeaturesEnabled() {
        return plugin.getConfig().getBoolean("features.trees.specialFeatures", true);
    }
    
    // ============================================
    // YAPI SİSTEMİ AYARLARI
    // ============================================
    
    public boolean isStructureGenerationEnabled() {
        return plugin.getConfig().getBoolean("features.structures.enabled", true);
    }
    
    public double getStructureDensityMultiplier() {
        return plugin.getConfig().getDouble("features.structures.densityMultiplier", 1.0);
    }
    
    public boolean areStructureChestsEnabled() {
        return plugin.getConfig().getBoolean("features.structures.includeChests", true);
    }
    
    // ============================================
    // HAZİNE SİSTEMİ AYARLARI
    // ============================================
    
    public boolean isTreasureGenerationEnabled() {
        return plugin.getConfig().getBoolean("features.treasures.enabled", true);
    }
    
    public double getTreasureDensityMultiplier() {
        return plugin.getConfig().getDouble("features.treasures.densityMultiplier", 1.0);
    }
    
    public double getLegendaryChanceMultiplier() {
        return plugin.getConfig().getDouble("features.treasures.legendaryChanceMultiplier", 1.0);
    }
    
    public boolean areBiomeSpecificTreasuresEnabled() {
        return plugin.getConfig().getBoolean("features.treasures.biomeSpecific", true);
    }
    
    // ============================================
    // DEBUG AYARLARI
    // ============================================
    
    public boolean isDebugEnabled() {
        return plugin.getConfig().getBoolean("debug.enabled", false);
    }
    
    public boolean isOreDebugEnabled() {
        return plugin.getConfig().getBoolean("debug.oreGeneration.enabled", false);
    }
    
    public boolean showOreCoordinates() {
        return plugin.getConfig().getBoolean("debug.oreGeneration.showCoordinates", true);
    }
    
    public boolean showVeinSize() {
        return plugin.getConfig().getBoolean("debug.oreGeneration.showVeinSize", true);
    }
    
    public boolean isTreeDebugEnabled() {
        return plugin.getConfig().getBoolean("debug.treeGeneration.enabled", false);
    }
    
    public boolean isStructureDebugEnabled() {
        return plugin.getConfig().getBoolean("debug.structureGeneration.enabled", false);
    }
    
    public boolean isPerformanceMonitorEnabled() {
        return plugin.getConfig().getBoolean("debug.performanceMonitor.enabled", false);
    }
    
    public int getPerformanceLogInterval() {
        return plugin.getConfig().getInt("debug.performanceMonitor.logInterval", 100);
    }
    
    // ============================================
    // LİMİT AYARLARI
    // ============================================
    
    public int getPlanetsPerPlayer() {
        return plugin.getConfig().getInt("limits.planetsPerPlayer", 2);
    }
    
    public boolean canDeleteOwnPlanet() {
        return plugin.getConfig().getBoolean("limits.deleteOwnPlanet", false);
    }
    
    public int getMaxPlanetsPerWorld() {
        return plugin.getConfig().getInt("limits.maxPlanetsPerWorld", 100);
    }
    
    public long getGenerationCooldown() {
        return plugin.getConfig().getLong("limits.generationCooldown", 300);
    }
    
    // ============================================
    // DÜNYA YÖNETİMİ
    // ============================================
    
    public boolean isAutoSaveEnabled() {
        return plugin.getConfig().getBoolean("worldManagement.autoSave.enabled", true);
    }
    
    public int getAutoSaveInterval() {
        return plugin.getConfig().getInt("worldManagement.autoSave.interval", 600);
    }
    
    public boolean isChunkUnloadOptimization() {
        return plugin.getConfig().getBoolean("worldManagement.chunkUnload.enabled", true);
    }
    
    public int getChunkUnloadDelay() {
        return plugin.getConfig().getInt("worldManagement.chunkUnload.delay", 200);
    }
    
    // ============================================
    // ENTEGRASYONLAR
    // ============================================
    
    public boolean isVaultEnabled() {
        return plugin.getConfig().getBoolean("integrations.vault.enabled", false);
    }
    
    public double getPlanetCreationCost() {
        return plugin.getConfig().getDouble("integrations.vault.planetCreationCost", 1000.0);
    }
    
    public boolean isWorldGuardEnabled() {
        return plugin.getConfig().getBoolean("integrations.worldguard.enabled", false);
    }
    
    public boolean isPlaceholderAPIEnabled() {
        return plugin.getConfig().getBoolean("integrations.placeholderapi.enabled", false);
    }
    
    public boolean isDynmapEnabled() {
        return plugin.getConfig().getBoolean("integrations.dynmap.enabled", false);
    }
    
    public boolean showPlanetsOnDynmap() {
        return plugin.getConfig().getBoolean("integrations.dynmap.showPlanets", true);
    }
    
    // ============================================
    // RASTGELE İSİM OLUŞTURMA
    // ============================================
    
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
    
    // ============================================
    // MESAJ SİSTEMİ
    // ============================================
    
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
    
    // ============================================
    // YARDIMCI METODLAR
    // ============================================
    
    /**
     * Debug mesajı gönder
     */
    public void sendDebugMessage(String category, String message) {
        if (!isDebugEnabled()) return;
        
        boolean categoryEnabled = switch (category.toLowerCase()) {
            case "ore" -> isOreDebugEnabled();
            case "tree" -> isTreeDebugEnabled();
            case "structure" -> isStructureDebugEnabled();
            case "performance" -> isPerformanceMonitorEnabled();
            default -> false;
        };
        
        if (categoryEnabled) {
            plugin.logInfo("[DEBUG:" + category.toUpperCase() + "] " + message);
        }
    }
    
    /**
     * Tüm feature'ları kontrol et
     */
    public boolean areAllFeaturesEnabled() {
        return isOreGenerationEnabled() && 
               isTreeGenerationEnabled() && 
               isStructureGenerationEnabled() && 
               isTreasureGenerationEnabled();
    }
    
    /**
     * Config'i validate et
     */
    public void validateConfig() {
        if (getMinRadius() >= getMaxRadius()) {
            plugin.logWarning("minRadius >= maxRadius! Varsayılan değerler kullanılıyor.");
        }
        
        if (getBlocksPerBatch() > 1000) {
            plugin.logWarning("blocksPerBatch çok yüksek! Performans sorunları olabilir.");
        }
        
        if (getThreadPoolCoreSize() > 16) {
            plugin.logWarning("threadPoolCoreSize çok yüksek! CPU kullanımı artabilir.");
        }
        
        if (getShellThickness() < 3 || getShellThickness() > 15) {
            plugin.logWarning("shellThickness ideal değerlerin dışında (3-15).");
        }
    }
}