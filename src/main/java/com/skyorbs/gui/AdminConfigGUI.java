package com.skyorbs.gui;

import com.skyorbs.SkyOrbs;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class AdminConfigGUI {

    private final SkyOrbs plugin;

    public AdminConfigGUI(SkyOrbs plugin) {
        this.plugin = plugin;
    }

    public void openAdminConfigGUI(Player player) {
        Inventory gui = Bukkit.createInventory(null, 54, "§8[§cAdmin Konfigürasyon§8]");

        // Generation Settings (Top Row)
        gui.setItem(0, createGenerationSettingsItem());
        gui.setItem(1, createPlacementSettingsItem());
        gui.setItem(2, createPerformanceSettingsItem());
        gui.setItem(3, createFeatureSettingsItem());
        gui.setItem(4, createLimitSettingsItem());

        // Ore Configuration (Second Row)
        gui.setItem(9, createOreConfigItem("terrestrial"));
        gui.setItem(10, createOreConfigItem("lava"));
        gui.setItem(11, createOreConfigItem("crystal"));
        gui.setItem(12, createOreConfigItem("ice"));
        gui.setItem(13, createOreConfigItem("shadow"));
        gui.setItem(14, createOreConfigItem("toxic"));
        gui.setItem(15, createOreConfigItem("gas"));

        // Quick Actions (Third Row)
        gui.setItem(18, createReloadConfigItem());
        gui.setItem(19, createResetAllItem());
        gui.setItem(20, createExportConfigItem());
        gui.setItem(21, createImportConfigItem());
        gui.setItem(22, createSaveConfigItem());
        gui.setItem(23, createBackupConfigItem());

        // System Info (Fourth Row)
        gui.setItem(27, createSystemInfoItem());
        gui.setItem(28, createPerformanceStatsItem());
        gui.setItem(29, createPlanetStatsItem());

        // Close button
        gui.setItem(49, createCloseItem());

        player.openInventory(gui);
    }

    private ItemStack createGenerationSettingsItem() {
        ItemStack item = new ItemStack(Material.GRASS_BLOCK);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§a§lÜretim Ayarları");
        List<String> lore = new ArrayList<>();
        lore.add("§7Gezegen boyutları ve şekilleri");
        lore.add("");
        lore.add("§7Min yarıçap: §e" + plugin.getConfigManager().getMinRadius());
        lore.add("§7Max yarıçap: §e" + plugin.getConfigManager().getMaxRadius());
        lore.add("§7Ortalama yarıçap: §e" + plugin.getConfigManager().getAverageRadius());
        lore.add("");
        lore.add("§eTıklayarak düzenle!");
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack createPlacementSettingsItem() {
        ItemStack item = new ItemStack(Material.COMPASS);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§b§lYerleştirme Ayarları");
        List<String> lore = new ArrayList<>();
        lore.add("§7Gezegen konumlandırma kuralları");
        lore.add("");
        lore.add("§7Max uzaklık: §e" + plugin.getConfigManager().getMaxDistance());
        lore.add("§7Kümeleme: §e" + (plugin.getConfigManager().isClusteringEnabled() ? "Aktif" : "Pasif"));
        lore.add("§7Küme min uzaklık: §e" + plugin.getConfigManager().getClusterMinDistance());
        lore.add("");
        lore.add("§eTıklayarak düzenle!");
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack createPerformanceSettingsItem() {
        ItemStack item = new ItemStack(Material.REDSTONE);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§c§lPerformans Ayarları");
        List<String> lore = new ArrayList<>();
        lore.add("§7Batch boyutu ve hız ayarları");
        lore.add("");
        lore.add("§7Batch boyutu: §e" + plugin.getConfigManager().getBlocksPerBatch());
        lore.add("§7Tick başına batch: §e" + plugin.getConfigManager().getBatchesPerTick());
        lore.add("§7Shell kalınlığı: §e" + plugin.getConfigManager().getShellThickness());
        lore.add("");
        lore.add("§eTıklayarak düzenle!");
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack createFeatureSettingsItem() {
        ItemStack item = new ItemStack(Material.LEVER);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§d§lÖzellik Ayarları");
        List<String> lore = new ArrayList<>();
        lore.add("§7Üretim özelliklerinin aç/kapa");
        lore.add("");
        lore.add("§7Ore üretimi: " + (plugin.getConfigManager().isOreGenerationEnabled() ? "§aAktif" : "§cPasif"));
        lore.add("§7Ağaç üretimi: " + (plugin.getConfigManager().isTreeGenerationEnabled() ? "§aAktif" : "§cPasif"));
        lore.add("§7Yapı üretimi: " + (plugin.getConfigManager().isStructureGenerationEnabled() ? "§aAktif" : "§cPasif"));
        lore.add("§7Hazine üretimi: " + (plugin.getConfigManager().isTreasureGenerationEnabled() ? "§aAktif" : "§cPasif"));
        lore.add("");
        lore.add("§eTıklayarak düzenle!");
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack createLimitSettingsItem() {
        ItemStack item = new ItemStack(Material.BARRIER);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§4§lLimit Ayarları");
        List<String> lore = new ArrayList<>();
        lore.add("§7Oyuncu ve sistem limitleri");
        lore.add("");
        lore.add("§7Oyuncu başına gezegen: §e" + plugin.getConfigManager().getPlanetsPerPlayer());
        lore.add("§7Dünya başına max gezegen: §e" + plugin.getConfigManager().getMaxPlanetsPerWorld());
        lore.add("§7Üretim cooldown: §e" + plugin.getConfigManager().getGenerationCooldown() + "s");
        lore.add("");
        lore.add("§eTıklayarak düzenle!");
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack createOreConfigItem(String planetType) {
        Material material = switch (planetType) {
            case "terrestrial" -> Material.GRASS_BLOCK;
            case "lava" -> Material.LAVA_BUCKET;
            case "crystal" -> Material.AMETHYST_SHARD;
            case "ice" -> Material.ICE;
            case "shadow" -> Material.OBSIDIAN;
            case "toxic" -> Material.SLIME_BALL;
            case "gas" -> Material.GLASS;
            default -> Material.STONE;
        };

        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§6§l" + planetType.toUpperCase() + " Ore Ayarları");

        List<String> lore = new ArrayList<>();
        lore.add("§7" + planetType + " gezegen tipi için ore konfigürasyonu");

        var config = plugin.getConfigManager().getOreConfigForPlanetType(planetType);
        boolean enabled = (Boolean) config.getOrDefault("enabled", true);
        double multiplier = (Double) config.getOrDefault("densityMultiplier", 1.0);

        lore.add("");
        lore.add("§7Durum: " + (enabled ? "§aAktif" : "§cPasif"));
        lore.add("§7Yoğunluk çarpanı: §e" + String.format("%.1fx", multiplier));

        @SuppressWarnings("unchecked")
        var ores = (java.util.Map<String, java.util.Map<String, Object>>) config.get("ores");
        if (ores != null && !ores.isEmpty()) {
            lore.add("");
            lore.add("§7Konfigüre edilmiş madenler:");
            ores.forEach((oreName, oreData) -> {
                boolean oreEnabled = (Boolean) oreData.getOrDefault("enabled", true);
                double chance = ((Number) oreData.getOrDefault("chance", 0.0)).doubleValue();
                lore.add("§8• §f" + oreName + " §7(" + (oreEnabled ? "§aAktif" : "§cPasif") + "§7) - §e" + String.format("%.3f", chance));
            });
        }

        lore.add("");
        lore.add("§eTıklayarak düzenle!");

        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack createReloadConfigItem() {
        ItemStack item = new ItemStack(Material.KNOWLEDGE_BOOK);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§a§lKonfigürasyonu Yenile");
        List<String> lore = new ArrayList<>();
        lore.add("§7Konfigürasyon dosyasını yeniden yükle");
        lore.add("§7Değişiklikleri uygula");
        lore.add("§7Cache'leri temizle");
        lore.add("");
        lore.add("§eTıklayarak yenile!");
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack createResetAllItem() {
        ItemStack item = new ItemStack(Material.TNT);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§4§lTümünü Sıfırla");
        List<String> lore = new ArrayList<>();
        lore.add("§7Tüm ayarları varsayılan değerlere sıfırla");
        lore.add("§4§lBu işlem geri alınamaz!");
        lore.add("");
        lore.add("§cSağ tıkla onay için");
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack createExportConfigItem() {
        ItemStack item = new ItemStack(Material.WRITABLE_BOOK);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§9§lKonfigürasyonu Dışa Aktar");
        List<String> lore = new ArrayList<>();
        lore.add("§7Mevcut konfigürasyonu dosyaya kaydet");
        lore.add("§7Yedekleme için kullan");
        lore.add("");
        lore.add("§eTıklayarak dışa aktar!");
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack createImportConfigItem() {
        ItemStack item = new ItemStack(Material.ENCHANTED_BOOK);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§9§lKonfigürasyonu İçe Aktar");
        List<String> lore = new ArrayList<>();
        lore.add("§7Dışarıdan konfigürasyon yükle");
        lore.add("§7Yedekten geri yükleme");
        lore.add("");
        lore.add("§eTıklayarak içe aktar!");
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack createSaveConfigItem() {
        ItemStack item = new ItemStack(Material.WRITTEN_BOOK);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§6§lKonfigürasyonu Kaydet");
        List<String> lore = new ArrayList<>();
        lore.add("§7Mevcut ayarları diske kaydet");
        lore.add("§7Değişiklikleri kalıcı hale getir");
        lore.add("");
        lore.add("§eTıklayarak kaydet!");
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack createBackupConfigItem() {
        ItemStack item = new ItemStack(Material.BOOKSHELF);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§8§lKonfigürasyon Yedekle");
        List<String> lore = new ArrayList<>();
        lore.add("§7Konfigürasyonun yedeğini al");
        lore.add("§7Acil durumlar için");
        lore.add("");
        lore.add("§eTıklayarak yedekle!");
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack createSystemInfoItem() {
        ItemStack item = new ItemStack(Material.CLOCK);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§b§lSistem Bilgileri");
        List<String> lore = new ArrayList<>();
        lore.add("§7Sunucu ve eklenti bilgileri");
        lore.add("");
        lore.add("§7Bukkit versiyonu: §e" + Bukkit.getVersion());
        lore.add("§7Eklenti versiyonu: §e" + plugin.getDescription().getVersion());
        lore.add("§7Çevrimiçi oyuncular: §e" + Bukkit.getOnlinePlayers().size());
        lore.add("");
        lore.add("§7Tıklayarak detayları görüntüle!");
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack createPerformanceStatsItem() {
        ItemStack item = new ItemStack(Material.LIGHTNING_ROD);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§e§lPerformans İstatistikleri");
        List<String> lore = new ArrayList<>();
        lore.add("§7CPU, RAM ve TPS bilgileri");
        lore.add("");
        Runtime runtime = Runtime.getRuntime();
        long totalMemory = runtime.totalMemory() / 1024 / 1024;
        long freeMemory = runtime.freeMemory() / 1024 / 1024;
        long usedMemory = totalMemory - freeMemory;

        lore.add("§7Kullanılan RAM: §e" + usedMemory + "MB");
        lore.add("§7Toplam RAM: §e" + totalMemory + "MB");
        lore.add("§7Serbest RAM: §e" + freeMemory + "MB");
        lore.add("");
        lore.add("§7Tıklayarak canlı istatistikleri görüntüle!");
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack createPlanetStatsItem() {
        ItemStack item = new ItemStack(Material.GLOBE_BANNER_PATTERN);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§6§lGezegen İstatistikleri");
        List<String> lore = new ArrayList<>();
        lore.add("§7Toplam gezegen ve tür bilgileri");

        try {
            Set<String> planetTypes = plugin.getConfigManager().getConfiguredPlanetTypes();
            int totalPlanets = plugin.getDatabaseManager().getAllOrbs().size();

            lore.add("");
            lore.add("§7Toplam gezegen: §e" + totalPlanets);
            lore.add("§7Konfigüre edilmiş türler: §e" + planetTypes.size());

            if (!planetTypes.isEmpty()) {
                lore.add("");
                lore.add("§7Türler:");
                for (String type : planetTypes) {
                    lore.add("§8• §f" + type);
                }
            }

        } catch (Exception e) {
            lore.add("");
            lore.add("§cVeritabanı hatası!");
        }

        lore.add("");
        lore.add("§7Tıklayarak detayları görüntüle!");
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack createCloseItem() {
        ItemStack item = new ItemStack(Material.BARRIER);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§c§lKapat");
        List<String> lore = new ArrayList<>();
        lore.add("§7Konfigürasyon panelini kapat");
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }
}