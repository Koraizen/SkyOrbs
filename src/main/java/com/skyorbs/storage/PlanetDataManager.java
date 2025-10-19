package main.java.com.skyorbs.storage;

import com.skyorbs.SkyOrbs;
import com.skyorbs.core.Orb;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Gezegen data yönetimi - Her gezegen için ayrı klasör ve bilgi dosyaları
 */
public class PlanetDataManager {

    private final SkyOrbs plugin;
    private final File planetsDataFolder;
    private final Map<UUID, YamlConfiguration> planetConfigs;

    public PlanetDataManager(SkyOrbs plugin) {
        this.plugin = plugin;
        this.planetsDataFolder = new File(plugin.getDataFolder(), "planets");
        this.planetConfigs = new HashMap<UUID, YamlConfiguration>();

        // Ana klasörü oluştur
        if (!planetsDataFolder.exists()) {
            planetsDataFolder.mkdirs();
        }
    }

    /**
     * Gezegen için klasör oluştur ve temel bilgileri kaydet
     */
    public void createPlanetData(Orb orb) {
        try {
            // Gezegen klasörü oluştur
            File planetFolder = new File(planetsDataFolder, orb.getId().toString());
            if (!planetFolder.exists()) {
                planetFolder.mkdirs();
            }

            // Gezegen bilgileri dosyası
            File infoFile = new File(planetFolder, "info.yml");
            YamlConfiguration config = YamlConfiguration.loadConfiguration(infoFile);

            // Temel bilgiler
            config.set("id", orb.getId().toString());
            config.set("name", orb.getName());
            config.set("world", orb.getWorldName());
            config.set("centerX", orb.getCenterX());
            config.set("centerY", orb.getCenterY());
            config.set("centerZ", orb.getCenterZ());
            config.set("radius", orb.getRadius());
            config.set("shape", orb.getShapeName());
            config.set("biome", orb.getBiomeName());
            config.set("seed", orb.getSeed());
            config.set("created", orb.getCreatedTime());
            config.set("isHollow", orb.isHollow());

            // Ek bilgiler
            if (orb.getPaletteId() != null) {
                config.set("paletteId", orb.getPaletteId());
            }
            if (orb.getAtmosphere() != null) {
                config.set("atmosphere", orb.getAtmosphere().name());
            }
            if (orb.getModifiers() != null && !orb.getModifiers().isEmpty()) {
                config.set("modifiers", orb.getModifiers().stream()
                    .map(mod -> mod.name())
                    .toList());
            }

            // İstatistikler
            config.set("stats.blockCount", calculateEstimatedBlockCount(orb));
            config.set("stats.surfaceArea", calculateSurfaceArea(orb));
            config.set("stats.volume", calculateVolume(orb));

            // Kaydet
            config.save(infoFile);

            // Cache'e ekle
            planetConfigs.put(orb.getId(), config);

            plugin.logDebug("planet_data", "Gezegen data klasörü oluşturuldu: " + planetFolder.getPath());

        } catch (IOException e) {
            plugin.logError("Gezegen data oluşturma hatası", e);
        }
    }

    /**
     * Gezegen bilgilerini güncelle
     */
    public void updatePlanetData(Orb orb) {
        YamlConfiguration config = planetConfigs.get(orb.getId());
        if (config == null) {
            createPlanetData(orb);
            return;
        }

        try {
            File planetFolder = new File(planetsDataFolder, orb.getId().toString());
            File infoFile = new File(planetFolder, "info.yml");

            // Güncel bilgileri yaz
            config.set("name", orb.getName());
            config.set("biome", orb.getBiomeName());
            config.set("lastModified", System.currentTimeMillis());

            config.save(infoFile);

        } catch (IOException e) {
            plugin.logError("Gezegen data güncelleme hatası", e);
        }
    }

    /**
     * Gezegen data'sını yükle
     */
    public YamlConfiguration loadPlanetData(UUID planetId) {
        if (planetConfigs.containsKey(planetId)) {
            return planetConfigs.get(planetId);
        }

        File planetFolder = new File(planetsDataFolder, planetId.toString());
        File infoFile = new File(planetFolder, "info.yml");

        if (infoFile.exists()) {
            YamlConfiguration config = YamlConfiguration.loadConfiguration(infoFile);
            planetConfigs.put(planetId, config);
            return config;
        }

        return null;
    }

    /**
     * Gezegen klasörünü sil
     */
    public void deletePlanetData(UUID planetId) {
        try {
            File planetFolder = new File(planetsDataFolder, planetId.toString());
            if (planetFolder.exists()) {
                // Tüm dosyaları sil
                Files.walk(Paths.get(planetFolder.getPath()))
                    .map(Path::toFile)
                    .forEach(File::delete);

                // Cache'den çıkar
                planetConfigs.remove(planetId);

                plugin.logDebug("planet_data", "Gezegen data klasörü silindi: " + planetFolder.getPath());
            }
        } catch (IOException e) {
            plugin.logError("Gezegen data silme hatası", e);
        }
    }

    /**
     * Gezegen istatistiklerini hesapla
     */
    private long calculateEstimatedBlockCount(Orb orb) {
        int radius = orb.getRadius();
        if (orb.isHollow()) {
            // Hollow planet için kabuk hacmi
            int shellThickness = plugin.getConfig().getInt("modifiers.hollow.shellThickness", 5);
            double outerVolume = (4.0 / 3.0) * Math.PI * Math.pow(radius, 3);
            double innerVolume = (4.0 / 3.0) * Math.PI * Math.pow(radius - shellThickness, 3);
            return (long)((outerVolume - innerVolume) * 1.1);
        } else {
            // Solid planet için tam hacim
            return (long)((4.0 / 3.0) * Math.PI * Math.pow(radius, 3) * 1.1);
        }
    }

    private double calculateSurfaceArea(Orb orb) {
        int radius = orb.getRadius();
        return 4 * Math.PI * radius * radius;
    }

    private double calculateVolume(Orb orb) {
        int radius = orb.getRadius();
        return (4.0 / 3.0) * Math.PI * radius * radius * radius;
    }

    /**
     * Tüm gezegen klasörlerini listele
     */
    public File[] getAllPlanetFolders() {
        return planetsDataFolder.listFiles(File::isDirectory);
    }

    /**
     * Gezegen sayısını döndür
     */
    public int getPlanetCount() {
        File[] folders = getAllPlanetFolders();
        return folders != null ? folders.length : 0;
    }

    /**
     * Cache'i temizle
     */
    public void clearCache() {
        planetConfigs.clear();
    }

    /**
     * Tüm gezegen verilerini yeniden yükle
     */
    public void reloadAllPlanetData() {
        clearCache();
        File[] planetFolders = getAllPlanetFolders();

        if (planetFolders != null) {
            for (File folder : planetFolders) {
                try {
                    UUID planetId = UUID.fromString(folder.getName());
                    loadPlanetData(planetId);
                } catch (IllegalArgumentException e) {
                    plugin.logError("Geçersiz gezegen klasör adı: " + folder.getName(), e);
                }
            }
        }
    }
}