package com.skyorbs;

import com.skyorbs.commands.GezegenCommand;
import com.skyorbs.config.ConfigManager;
import com.skyorbs.generation.GenerationManager;
import com.skyorbs.shapes.ShapeRegistry;
import com.skyorbs.storage.DatabaseManager;
import com.skyorbs.dungeons.DungeonGenerator;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

public class SkyOrbs extends JavaPlugin {
    
    private static SkyOrbs instance;
    private ConfigManager configManager;
    private DatabaseManager databaseManager;
    private GenerationManager generationManager;
    private ShapeRegistry shapeRegistry;
    private DungeonGenerator dungeonGenerator;
    
    // Performans metrikleri
    private long startupTime;
    private int totalPlanetsGenerated = 0;
    
    @Override
    public void onEnable() {
        startupTime = System.currentTimeMillis();
        instance = this;
        
        logInfo("========================================");
        logInfo("SkyOrbs 2.0.0 aktifleştiriliyor...");
        logInfo("Türkçe Minecraft gezegen oluşturma eklentisi");
        logInfo("========================================");
        
        // Config oluştur
        saveDefaultConfig();
        
        try {
            // 1. Config Manager
            configManager = new ConfigManager(this);
            configManager.validateConfig(); // Config doğrulama
            logSuccess("✓ Konfigürasyon yüklendi ve doğrulandı");
            
            // 2. Database
            databaseManager = new DatabaseManager(this);
            databaseManager.initialize();
            logSuccess("✓ Veritabanı bağlantısı kuruldu");
            
            // 3. Shape Registry
            shapeRegistry = new ShapeRegistry();
            shapeRegistry.registerAllShapes();
            logSuccess("✓ " + shapeRegistry.getShapeCount() + " gezegen şekli kaydedildi");
            
            // 4. Generation Manager
            generationManager = new GenerationManager(this);
            logSuccess("✓ Gezegen üretim sistemi hazır");

            // 4.5. Dungeon Generator
            dungeonGenerator = new DungeonGenerator(this);
            logSuccess("✓ Zindan üretim sistemi hazır");
            
            // 5. Commands
            registerCommands();
            logSuccess("✓ Komutlar kaydedildi");
            
            // 6. Özellik kontrolü
            logFeatureStatus();
            
            // 7. Entegrasyon kontrolü
            checkIntegrations();
            
            // 8. Performans monitörü
            if (configManager.isPerformanceMonitorEnabled()) {
                startPerformanceMonitor();
            }
            
            // 9. Auto-save sistemi
            if (configManager.isAutoSaveEnabled()) {
                startAutoSave();
            }
            
            long loadTime = System.currentTimeMillis() - startupTime;
            logSuccess("========================================");
            logSuccess("SkyOrbs başarıyla aktifleştirildi!");
            logSuccess("17 gezegen şekli, 22 biyom, asteroid/uydu sistemi hazır!");
            logSuccess("Yükleme süresi: " + loadTime + "ms");
            logSuccess("========================================");
            
        } catch (Exception e) {
            logError("Eklenti başlatılırken hata oluştu!", e);
            getServer().getPluginManager().disablePlugin(this);
        }
    }
    
    @Override
    public void onDisable() {
        logInfo("========================================");
        logInfo("SkyOrbs devre dışı bırakılıyor...");
        
        if (generationManager != null) {
            generationManager.shutdown();
            logSuccess("✓ Üretim görevleri sonlandırıldı");
        }
        
        if (databaseManager != null) {
            databaseManager.close();
            logSuccess("✓ Veritabanı bağlantısı kapatıldı");
        }
        
        // İstatistikler
        logInfo("Toplam oluşturulan gezegen: " + totalPlanetsGenerated);
        
        logInfo("SkyOrbs devre dışı bırakıldı.");
        logInfo("========================================");
    }
    
    /**
     * Komutları kaydet
     */
    private void registerCommands() {
        GezegenCommand commandExecutor = new GezegenCommand(this);
        getCommand("gezegen").setExecutor(commandExecutor);
        getCommand("gezegen").setTabCompleter(commandExecutor);
    }
    
    /**
     * Özellik durumlarını logla
     */
    private void logFeatureStatus() {
        logInfo("Özellik Durumu:");
        logInfo("  • Ore Sistemi: " + (configManager.isOreGenerationEnabled() ? "§aAktif" : "§cKapalı"));
        logInfo("  • Ağaç Sistemi: " + (configManager.isTreeGenerationEnabled() ? "§aAktif" : "§cKapalı"));
        logInfo("  • Yapı Sistemi: " + (configManager.isStructureGenerationEnabled() ? "§aAktif" : "§cKapalı"));
        logInfo("  • Hazine Sistemi: " + (configManager.isTreasureGenerationEnabled() ? "§aAktif" : "§cKapalı"));
        logInfo("  • Asteroid Sistemi: " + (configManager.isAsteroidsEnabled() ? "§aAktif" : "§cKapalı"));
        logInfo("  • Uydu Sistemi: " + (configManager.isSatellitesEnabled() ? "§aAktif" : "§cKapalı"));
    }
    
    /**
     * Entegrasyonları kontrol et
     */
    private void checkIntegrations() {
        boolean hasIntegration = false;
        
        if (Bukkit.getPluginManager().isPluginEnabled("Vault") && configManager.isVaultEnabled()) {
            logSuccess("✓ Vault entegrasyonu aktif");
            hasIntegration = true;
        }
        
        if (Bukkit.getPluginManager().isPluginEnabled("WorldGuard") && configManager.isWorldGuardEnabled()) {
            logSuccess("✓ WorldGuard entegrasyonu aktif");
            hasIntegration = true;
        }
        
        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI") && configManager.isPlaceholderAPIEnabled()) {
            logSuccess("✓ PlaceholderAPI entegrasyonu aktif");
            hasIntegration = true;
        }
        
        if (Bukkit.getPluginManager().isPluginEnabled("dynmap") && configManager.isDynmapEnabled()) {
            logSuccess("✓ Dynmap entegrasyonu aktif");
            hasIntegration = true;
        }
        
        if (!hasIntegration) {
            logInfo("Entegrasyon: Yok (opsiyonel)");
        }
    }
    
    /**
     * Performans monitörünü başlat
     */
    private void startPerformanceMonitor() {
        int interval = configManager.getPerformanceLogInterval();
        
        Bukkit.getScheduler().runTaskTimerAsynchronously(this, () -> {
            Runtime runtime = Runtime.getRuntime();
            long usedMemory = (runtime.totalMemory() - runtime.freeMemory()) / 1024 / 1024;
            long maxMemory = runtime.maxMemory() / 1024 / 1024;
            
            double tps = Bukkit.getTPS()[0]; // 1 dakikalık TPS
            
            configManager.sendDebugMessage("performance", 
                String.format("TPS: %.2f | RAM: %dMB/%dMB | Gezegenler: %d", 
                    tps, usedMemory, maxMemory, totalPlanetsGenerated));
            
        }, interval, interval);
        
        logInfo("✓ Performans monitörü başlatıldı");
    }
    
    /**
     * Auto-save sistemini başlat
     */
    private void startAutoSave() {
        int interval = configManager.getAutoSaveInterval() * 20; // Saniye to tick
        
        Bukkit.getScheduler().runTaskTimer(this, () -> {
            try {
                // Database'i kaydet (eğer varsa değişiklikler)
                logInfo("Auto-save: Veri kaydediliyor...");
                // DatabaseManager zaten her işlemde kaydediyor, ek bir şey gerekmiyor
                logInfo("Auto-save: Tamamlandı");
            } catch (Exception e) {
                logError("Auto-save hatası!", e);
            }
        }, interval, interval);
        
        logInfo("✓ Auto-save sistemi başlatıldı (Her " + configManager.getAutoSaveInterval() + " saniye)");
    }
    
    /**
     * Config'i yeniden yükle
     */
    public void reloadPluginConfig() {
        reloadConfig();
        configManager = new ConfigManager(this);
        configManager.validateConfig();
        logSuccess("Konfigürasyon yeniden yüklendi ve doğrulandı");
    }
    
    /**
     * Gezegen sayacını artır
     */
    public void incrementPlanetCount() {
        totalPlanetsGenerated++;
    }
    
    // ============================================
    // GETTER METODLARI
    // ============================================
    
    public static SkyOrbs getInstance() {
        return instance;
    }
    
    public ConfigManager getConfigManager() {
        return configManager;
    }
    
    public DatabaseManager getDatabaseManager() {
        return databaseManager;
    }
    
    public GenerationManager getGenerationManager() {
        return generationManager;
    }
    
    public ShapeRegistry getShapeRegistry() {
        return shapeRegistry;
    }

    public DungeonGenerator getDungeonGenerator() {
        return dungeonGenerator;
    }
    
    public int getTotalPlanetsGenerated() {
        return totalPlanetsGenerated;
    }
    
    public long getUptime() {
        return System.currentTimeMillis() - startupTime;
    }
    
    // ============================================
    // LOGGING METODLARI
    // ============================================
    
    public void logInfo(String message) {
        getLogger().info(message);
    }
    
    public void logSuccess(String message) {
        getLogger().info("§a" + message);
    }
    
    public void logWarning(String message) {
        getLogger().warning(message);
    }
    
    public void logError(String message, Exception e) {
        getLogger().log(Level.SEVERE, message, e);
    }
    
    public void logDebug(String category, String message) {
        if (configManager != null) {
            configManager.sendDebugMessage(category, message);
        }
    }
}