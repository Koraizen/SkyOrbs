package com.skyorbs;

import com.skyorbs.commands.SkyOrbsCommand;
import com.skyorbs.config.ConfigManager;
import com.skyorbs.generation.GenerationManager;
import com.skyorbs.shapes.ShapeRegistry;
import com.skyorbs.storage.DatabaseManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

public class SkyOrbs extends JavaPlugin {
    
    private static SkyOrbs instance;
    private ConfigManager configManager;
    private DatabaseManager databaseManager;
    private GenerationManager generationManager;
    private ShapeRegistry shapeRegistry;
    
    @Override
    public void onEnable() {
        instance = this;
        
        logInfo("SkyOrbs 2.0.0 aktifleştiriliyor...");
        logInfo("Türkçe destekli uzay gezegenleri oluşturma eklentisi");
        
        saveDefaultConfig();
        
        try {
            configManager = new ConfigManager(this);
            logInfo("✓ Konfigürasyon yüklendi");
            
            databaseManager = new DatabaseManager(this);
            databaseManager.initialize();
            logInfo("✓ Veritabanı bağlantısı kuruldu");
            
            shapeRegistry = new ShapeRegistry();
            shapeRegistry.registerAllShapes();
            logInfo("✓ " + shapeRegistry.getShapeCount() + " gezegen şekli kaydedildi");
            
            generationManager = new GenerationManager(this);
            logInfo("✓ Gezegen üretim sistemi hazır");
            
            registerCommands();
            logInfo("✓ Komutlar kaydedildi");
            
            logSuccess("SkyOrbs başarıyla aktifleştirildi!");
            logSuccess("15+ gezegen şekli, 30+ biyom, asteroid sistemi hazır!");
            
        } catch (Exception e) {
            logError("Eklenti başlatılırken hata oluştu!", e);
            getServer().getPluginManager().disablePlugin(this);
        }
    }
    
    @Override
    public void onDisable() {
        logInfo("SkyOrbs devre dışı bırakılıyor...");
        
        if (generationManager != null) {
            generationManager.shutdown();
            logInfo("✓ Üretim görevleri sonlandırıldı");
        }
        
        if (databaseManager != null) {
            databaseManager.close();
            logInfo("✓ Veritabanı bağlantısı kapatıldı");
        }
        
        logInfo("SkyOrbs devre dışı bırakıldı. Hoşçakalın!");
    }
    
    private void registerCommands() {
        SkyOrbsCommand commandExecutor = new SkyOrbsCommand(this);
        getCommand("skyorbs").setExecutor(commandExecutor);
        getCommand("skyorbs").setTabCompleter(commandExecutor);
    }
    
    public void reloadPluginConfig() {
        reloadConfig();
        configManager = new ConfigManager(this);
        logInfo("Konfigürasyon yeniden yüklendi");
    }
    
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
}
