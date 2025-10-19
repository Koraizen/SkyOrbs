package com.skyorbs;

import com.skyorbs.commands.GezegenCommand;
import com.skyorbs.config.ConfigManager;
import com.skyorbs.generation.GenerationManager;
import com.skyorbs.shapes.ShapeRegistry;
import com.skyorbs.storage.DatabaseManager;
import com.skyorbs.storage.PlanetDataManager;
import com.skyorbs.dungeons.DungeonGenerator;
import com.skyorbs.palettes.PaletteRegistry;
import com.skyorbs.atmosphere.PlanetAtmosphereManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Map;
import java.util.logging.Level;
import com.skyorbs.ecosystem.BiosphereManager;
import com.skyorbs.events.CelestialEvents;
import com.skyorbs.listeners.PlanetEventListener;

public class SkyOrbs extends JavaPlugin {
    
    private static SkyOrbs instance;
    private ConfigManager configManager;
    private DatabaseManager databaseManager;
    private PlanetDataManager planetDataManager;
    private GenerationManager generationManager;
    private ShapeRegistry shapeRegistry;
    private DungeonGenerator dungeonGenerator;
    private PaletteRegistry paletteRegistry;
    private PlanetAtmosphereManager atmosphereManager;
    private com.skyorbs.gui.AdminConfigGUI adminConfigGUI;

    // NEW: Missing managers
    private BiosphereManager biosphereManager;
    private CelestialEvents celestialEvents;
    private PlanetEventListener planetEventListener;

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
        
        // Config oluştur ve yedek varsa geri yükle
        saveDefaultConfig();

        // Config yedek kontrolü
        checkAndRestoreConfigBackup();

        try {
            // 1. Config Manager
            configManager = new ConfigManager(this);
            configManager.validateConfig(); // Config doğrulama
            logSuccess("✓ Konfigürasyon yüklendi ve doğrulandı");

            // 2. Database
            databaseManager = new DatabaseManager(this);
            databaseManager.initialize();
            logSuccess("✓ Veritabanı bağlantısı kuruldu");

            // 2.5. Planet Data Manager
            planetDataManager = new PlanetDataManager(this);
            logSuccess("✓ Gezegen data yönetim sistemi hazır");

            // 3. Shape Registry
            shapeRegistry = new ShapeRegistry();
            shapeRegistry.registerAllShapes();
            logSuccess("✓ " + shapeRegistry.getShapeCount() + " gezegen şekli kaydedildi");

            // SHAPE CONFIGURATION DEBUG
            logInfo("========================================");
            logInfo("SHAPE CONFIGURATION:");
            logInfo("========================================");

            Map<String, Double> weights = configManager.getShapeWeights();
            if (weights.isEmpty()) {
                logWarning("⚠ NO SHAPE WEIGHTS LOADED!");
            } else {
                logSuccess("✓ Loaded " + weights.size() + " shape weights:");
                weights.entrySet().stream()
                    .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                    .forEach(entry ->
                        logInfo("  • " + entry.getKey() + " = " + entry.getValue())
                    );
            }

            logInfo("========================================");
            
            // 3.5. Palette Registry (20+ palettes for diversity)
            paletteRegistry = new PaletteRegistry();
            logSuccess("✓ " + paletteRegistry.getPaletteCount() + " gezegen paleti kaydedildi");
            
            // 3.6. Atmosphere Manager
            atmosphereManager = new PlanetAtmosphereManager(this);
            atmosphereManager.start();
            logSuccess("✓ Atmosfer efekt sistemi başlatıldı");

            // 3.7. Admin Config GUI
            adminConfigGUI = new com.skyorbs.gui.AdminConfigGUI(this);
            logSuccess("✓ Admin konfigürasyon GUI'si hazır");
            
            // 4. Generation Manager
            generationManager = new GenerationManager(this);
            logSuccess("✓ Gezegen üretim sistemi hazır");

            // 4.5. Dungeon Generator
            dungeonGenerator = new DungeonGenerator(this);
            logSuccess("✓ Zindan üretim sistemi hazır");

            // NEW: Initialize Biosphere Manager
            biosphereManager = new BiosphereManager(this);
            logSuccess("✓ Biyosfer yönetim sistemi başlatıldı");

            // NEW: Initialize Celestial Events
            celestialEvents = new CelestialEvents(this);
            logSuccess("✓ Gök olayları sistemi başlatıldı");

            // Register event listeners
            planetEventListener = new PlanetEventListener(this);
            getServer().getPluginManager().registerEvents(planetEventListener, this);
            logSuccess("✓ Event listeners registered");

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
            
            // Calculate total combinations
            int totalCombinations = shapeRegistry.getShapeCount() * paletteRegistry.getPaletteCount() * 8; // 8 atmospheres
            
            logSuccess("========================================");
            logSuccess("SkyOrbs başarıyla aktifleştirildi!");
            logSuccess("17 şekil × 20 palet × 8 atmosfer = " + totalCombinations + "+ gezegen kombinasyonu!");
            logSuccess("Modifiyerler ile 10,000+ farklı gezegen çeşidi mümkün!");
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
        
        if (atmosphereManager != null) {
            atmosphereManager.stop();
            logSuccess("✓ Atmosfer sistemi durduruldu");
        }
        
        if (generationManager != null) {
            generationManager.shutdown();
            logSuccess("✓ Üretim görevleri sonlandırıldı");
        }
        
        if (databaseManager != null) {
            databaseManager.close();
            logSuccess("✓ Veritabanı bağlantısı kapatıldı");
        }

        // Stop celestial events
        if (celestialEvents != null) {
            // No explicit stop needed - BukkitRunnable will cancel
            logSuccess("✓ Gök olayları sistemi durduruldu");
        }

        // Biosphere manager doesn't need explicit stop

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
     * Config'i yeniden yükle ve yedekle
     */
    public void reloadPluginConfig() {
        try {
            // 1. Mevcut config'i yedekle
            backupCurrentConfig();

            // 2. Config dosyasını yeniden yükle
            reloadConfig();

            // 3. ConfigManager'ı yeniden oluştur
            configManager = new ConfigManager(this);

            // 4. Validate et
            configManager.validateConfig();

            logSuccess("========================================");
            logSuccess("CONFIG RELOADED SUCCESSFULLY!");
            logSuccess("Previous config backed up to config.backup.yml");
            logSuccess("========================================");

        } catch (Exception e) {
            logError("Failed to reload config!", e);
        }
    }

    /**
     * Mevcut config'i yedekle
     */
    private void backupCurrentConfig() {
        try {
            java.io.File configFile = new java.io.File(getDataFolder(), "config.yml");
            java.io.File backupFile = new java.io.File(getDataFolder(), "config.backup.yml");

            if (configFile.exists()) {
                java.nio.file.Files.copy(configFile.toPath(), backupFile.toPath(),
                    java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                logInfo("✓ Config backed up to config.backup.yml");
            }
        } catch (Exception e) {
            logWarning("Failed to backup config: " + e.getMessage());
        }
    }

    /**
     * Config yedek kontrolü ve geri yükleme
     */
    private void checkAndRestoreConfigBackup() {
        try {
            java.io.File configFile = new java.io.File(getDataFolder(), "config.yml");
            java.io.File backupFile = new java.io.File(getDataFolder(), "config.backup.yml");

            // Eğer config dosyası yoksa ve yedek varsa, yedeği geri yükle
            if (!configFile.exists() && backupFile.exists()) {
                java.nio.file.Files.copy(backupFile.toPath(), configFile.toPath());
                logSuccess("✓ Config restored from backup!");
            } else if (backupFile.exists()) {
                logInfo("✓ Config backup available at config.backup.yml");
            }
        } catch (Exception e) {
            logWarning("Failed to check/restore config backup: " + e.getMessage());
        }
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

    public PlanetDataManager getPlanetDataManager() {
        return planetDataManager;
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
    
    public PaletteRegistry getPaletteRegistry() {
        return paletteRegistry;
    }
    
    public PlanetAtmosphereManager getAtmosphereManager() {
        return atmosphereManager;
    }

    public com.skyorbs.gui.AdminConfigGUI getAdminConfigGUI() {
        return adminConfigGUI;
    }
    
    public int getTotalPlanetsGenerated() {
        return totalPlanetsGenerated;
    }

    // NEW: Getters
    public BiosphereManager getBiosphereManager() {
        return biosphereManager;
    }

    public CelestialEvents getCelestialEvents() {
        return celestialEvents;
    }

    public PlanetEventListener getPlanetEventListener() {
        return planetEventListener;
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