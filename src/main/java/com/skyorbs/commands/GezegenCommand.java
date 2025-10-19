package com.skyorbs.commands;

import com.skyorbs.SkyOrbs;
import com.skyorbs.core.Orb;
import org.bukkit.*;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.*;
import com.skyorbs.biomes.BiomeType;
import com.skyorbs.features.OreGenerator;

public class GezegenCommand implements CommandExecutor, TabCompleter {
    
    private final SkyOrbs plugin;
    
    public GezegenCommand(SkyOrbs plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sendHelp(sender);
            return true;
        }
        
        switch (args[0].toLowerCase()) {
            case "create", "oluştur" -> handleCreate(sender);
            case "list", "liste" -> handleList(sender);
            case "tp", "teleport", "ışınlan" -> handleTeleport(sender, args);
            case "sil", "delete" -> handleDelete(sender, args);
            case "info", "bilgi" -> handleInfo(sender, args);
            case "reload", "yenile" -> handleReload(sender);
            case "test", "debug" -> handleTest(sender);
            case "oreconfig", "ore" -> handleOreConfig(sender, args);
            case "config", "ayar" -> handleConfig(sender, args);
            default -> sendHelp(sender);
        }
        
        return true;
    }
    
    private void handleCreate(CommandSender sender) {
        if (!sender.hasPermission("skyorbs.create")) {
            sender.sendMessage(plugin.getConfigManager().getMessage("noPermission"));
            return;
        }
        
        if (!(sender instanceof Player player)) {
            sender.sendMessage(plugin.getConfigManager().getMessage("playerOnly"));
            return;
        }
        
        World world = player.getWorld();
        plugin.getGenerationManager().createPlanetAsync(world, player);
    }
    
    private void handleList(CommandSender sender) {
        try {
            List<Orb> orbs = plugin.getDatabaseManager().getAllOrbs();
            
            if (orbs.isEmpty()) {
                sender.sendMessage(plugin.getConfigManager().getMessageRaw("listEmpty"));
                return;
            }
            
            sender.sendMessage(plugin.getConfigManager().getMessageRaw("listHeader")
                .replace("{count}", String.valueOf(orbs.size())));
            
            for (Orb orb : orbs) {
                String shapeName = plugin.getShapeRegistry().getShape(orb.getShapeName()).getDisplayName();
                double distFromSpawn = orb.getDistanceFromSpawn();
                
                sender.sendMessage(String.format("§e%s §7- §f%s §7| §f%d,%d,%d §7(Spawn'dan §f%.0f §7blok)",
                    orb.getName(), shapeName, orb.getCenterX(), orb.getCenterY(), orb.getCenterZ(), distFromSpawn));
            }
            
        } catch (SQLException e) {
            sender.sendMessage("§cVeritabanı hatası!");
        }
    }
    
    private void handleTeleport(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(plugin.getConfigManager().getMessage("playerOnly"));
            return;
        }
        
        if (args.length < 2) {
            sender.sendMessage("§cKullanım: /gezegen tp <gezegen-ismi>");
            return;
        }
        
        String name = args[1];
        
        try {
            Orb orb = plugin.getDatabaseManager().getOrbByName(name);
            
            if (orb == null) {
                sender.sendMessage(plugin.getConfigManager().getMessageRaw("planetNotFound")
                    .replace("{name}", name));
                return;
            }
            
            World world = Bukkit.getWorld(orb.getWorldName());
            if (world == null) {
                sender.sendMessage("§cGezegen dünyası bulunamadı!");
                return;
            }
            
            Location loc = new Location(world, orb.getCenterX(), orb.getCenterY() + orb.getRadius() + 10, orb.getCenterZ());
            player.teleport(loc);
            
            sender.sendMessage(plugin.getConfigManager().getMessageRaw("teleported")
                .replace("{name}", orb.getName()));
            
        } catch (SQLException e) {
            sender.sendMessage("§cVeritabanı hatası!");
        }
    }
    
    private void handleDelete(CommandSender sender, String[] args) {
        if (!sender.hasPermission("skyorbs.admin")) {
            sender.sendMessage(plugin.getConfigManager().getMessage("noPermission"));
            return;
        }
        
        if (args.length < 2) {
            sender.sendMessage("§cKullanım: /gezegen sil <gezegen-ismi>");
            return;
        }
        
        String name = args[1];
        
        try {
            Orb orb = plugin.getDatabaseManager().getOrbByName(name);
            
            if (orb == null) {
                sender.sendMessage(plugin.getConfigManager().getMessageRaw("planetNotFound")
                    .replace("{name}", name));
                return;
            }
            
            sender.sendMessage("§eGezegen siliniyor...");
            
            plugin.getGenerationManager().deletePlanet(orb)
                .thenRun(() -> {
                    sender.sendMessage(plugin.getConfigManager().getMessageRaw("planetDeleted")
                        .replace("{name}", orb.getName()));
                });
            
        } catch (SQLException e) {
            sender.sendMessage("§cVeritabanı hatası!");
        }
    }
    
    private void handleInfo(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage("§cKullanım: /gezegen info <gezegen-ismi>");
            return;
        }
        
        String name = args[1];
        
        try {
            Orb orb = plugin.getDatabaseManager().getOrbByName(name);
            
            if (orb == null) {
                sender.sendMessage(plugin.getConfigManager().getMessageRaw("planetNotFound")
                    .replace("{name}", name));
                return;
            }
            
            List<Orb> allOrbs = plugin.getDatabaseManager().getAllOrbs();
            
            sender.sendMessage("§b═══ Gezegen Bilgileri ═══");
            sender.sendMessage("§7İsim: §e" + orb.getName());
            sender.sendMessage("§7Şekil: §f" + plugin.getShapeRegistry().getShape(orb.getShapeName()).getDisplayName());
            sender.sendMessage("§7Biyom: §f" + orb.getBiomeName());
            sender.sendMessage("§7Yarıçap: §f" + orb.getRadius() + " blok");
            sender.sendMessage("§7Merkez: §f" + orb.getCenterX() + ", " + orb.getCenterY() + ", " + orb.getCenterZ());
            sender.sendMessage("§7Spawn'dan Uzaklık: §f" + String.format("%.0f", orb.getDistanceFromSpawn()) + " blok");
            
            sender.sendMessage("§7Yakındaki Gezegenler:");
            int nearbyCount = 0;
            for (Orb other : allOrbs) {
                if (!other.getId().equals(orb.getId())) {
                    double distance = orb.getDistanceFrom(other.getCenterX(), other.getCenterZ());
                    if (distance < 3000 && nearbyCount < 5) {
                        sender.sendMessage(String.format("  §8• §e%s §7- §f%.0f blok uzakta", other.getName(), distance));
                        nearbyCount++;
                    }
                }
            }
            
            if (nearbyCount == 0) {
                sender.sendMessage("  §7(Yakında gezegen yok)");
            }
            
        } catch (SQLException e) {
            sender.sendMessage("§cVeritabanı hatası!");
        }
    }
    
    private void handleReload(CommandSender sender) {
        if (!sender.hasPermission("skyorbs.admin")) {
            sender.sendMessage(plugin.getConfigManager().getMessage("noPermission"));
            return;
        }

        sender.sendMessage("§eConfig yeniden yükleniyor...");

        try {
            plugin.reloadPluginConfig();
            sender.sendMessage(plugin.getConfigManager().getMessage("configReloaded"));

            // Debug: Shape weights'i göster
            Map<String, Double> weights = plugin.getConfigManager().getShapeWeights();
            sender.sendMessage("§aYüklenen şekil sayısı: §e" + weights.size());

        } catch (Exception e) {
            sender.sendMessage("§cConfig yüklenemedi! Konsolu kontrol edin.");
            plugin.logError("Config reload failed", e);
        }
    }

    private void handleOreConfig(CommandSender sender, String[] args) {
        if (!sender.hasPermission("skyorbs.admin")) {
            sender.sendMessage("§cYetkiniz yok!");
            return;
        }

        if (args.length < 2) {
            sendOreConfigHelp(sender);
            return;
        }

        String subCommand = args[1].toLowerCase();

        switch (subCommand) {
            case "list" -> handleOreConfigList(sender, args);
            case "set" -> handleOreConfigSet(sender, args);
            case "enable" -> handleOreConfigEnable(sender, args);
            case "disable" -> handleOreConfigDisable(sender, args);
            case "multiplier" -> handleOreConfigMultiplier(sender, args);
            case "reset" -> handleOreConfigReset(sender, args);
            default -> sendOreConfigHelp(sender);
        }
    }

    private void handleOreConfigList(CommandSender sender, String[] args) {
        sender.sendMessage("§b═══ Gezegen Tipi Ore Konfigürasyonları ═══");

        Set<String> planetTypes = plugin.getConfigManager().getConfiguredPlanetTypes();
        if (planetTypes.isEmpty()) {
            sender.sendMessage("§7Konfigüre edilmiş gezegen tipi yok.");
            return;
        }

        for (String planetType : planetTypes) {
            Map<String, Object> config = plugin.getConfigManager().getOreConfigForPlanetType(planetType);
            boolean enabled = (Boolean) config.getOrDefault("enabled", true);
            double multiplier = (Double) config.getOrDefault("densityMultiplier", 1.0);

            sender.sendMessage(String.format("§e%s §7- %s §7| Çarpan: §f%.1fx",
                planetType.toUpperCase(),
                enabled ? "§aAktif" : "§cPasif",
                multiplier));

            @SuppressWarnings("unchecked")
            Map<String, Map<String, Object>> ores = (Map<String, Map<String, Object>>) config.get("ores");
            if (ores != null && !ores.isEmpty()) {
                sender.sendMessage("§7  Madenler:");
                ores.forEach((oreName, oreConfig) -> {
                    boolean oreEnabled = (Boolean) oreConfig.getOrDefault("enabled", true);
                    double chance = (Double) oreConfig.getOrDefault("chance", 0.0);
                    int minVein = (Integer) oreConfig.getOrDefault("minVein", 1);
                    int maxVein = (Integer) oreConfig.getOrDefault("maxVein", 1);

                    sender.sendMessage(String.format("    §8• §f%s §7- %s §7| Şans: §e%.3f §7| Damar: §e%d-%d",
                        oreName, oreEnabled ? "§aAktif" : "§cPasif", chance, minVein, maxVein));
                });
            }
        }
    }

    private void handleOreConfigSet(CommandSender sender, String[] args) {
        if (args.length < 6) {
            sender.sendMessage("§cKullanım: /gezegen ore set <gezegen-tipi> <maden> <özellik> <değer>");
            sender.sendMessage("§7Örnek: /gezegen ore set terrestrial coal chance 0.5");
            return;
        }

        String planetType = args[2].toLowerCase();
        String oreType = args[3].toLowerCase();
        String property = args[4].toLowerCase();
        String valueStr = args[5];

        try {
            Object value;
            switch (property) {
                case "chance" -> value = Double.parseDouble(valueStr);
                case "minvein", "maxvein" -> value = Integer.parseInt(valueStr);
                case "enabled" -> value = Boolean.parseBoolean(valueStr);
                default -> {
                    sender.sendMessage("§cGeçersiz özellik! Kullanılabilir: chance, minVein, maxVein, enabled");
                    return;
                }
            }

            plugin.getConfigManager().setOreConfigForPlanetType(planetType, oreType, property, value);
            sender.sendMessage(String.format("§a%s gezegeninde %s madeni için %s = %s olarak ayarlandı!",
                planetType.toUpperCase(), oreType.toUpperCase(), property, value));

        } catch (NumberFormatException e) {
            sender.sendMessage("§cGeçersiz sayı formatı!");
        }
    }

    private void handleOreConfigEnable(CommandSender sender, String[] args) {
        if (args.length < 3) {
            sender.sendMessage("§cKullanım: /gezegen ore enable <gezegen-tipi>");
            return;
        }

        String planetType = args[2].toLowerCase();
        plugin.getConfigManager().setPlanetTypeOreEnabled(planetType, true);
        sender.sendMessage("§a" + planetType.toUpperCase() + " gezegeninde ore üretimi aktifleştirildi!");
    }

    private void handleOreConfigDisable(CommandSender sender, String[] args) {
        if (args.length < 3) {
            sender.sendMessage("§cKullanım: /gezegen ore disable <gezegen-tipi>");
            return;
        }

        String planetType = args[2].toLowerCase();
        plugin.getConfigManager().setPlanetTypeOreEnabled(planetType, false);
        sender.sendMessage("§c" + planetType.toUpperCase() + " gezegeninde ore üretimi devre dışı bırakıldı!");
    }

    private void handleOreConfigMultiplier(CommandSender sender, String[] args) {
        if (args.length < 4) {
            sender.sendMessage("§cKullanım: /gezegen ore multiplier <gezegen-tipi> <çarpan>");
            return;
        }

        String planetType = args[2].toLowerCase();
        try {
            double multiplier = Double.parseDouble(args[3]);
            plugin.getConfigManager().setPlanetTypeOreMultiplier(planetType, multiplier);
            sender.sendMessage(String.format("§a%s gezegeninde ore yoğunluğu çarpanı %.1fx olarak ayarlandı!",
                planetType.toUpperCase(), multiplier));
        } catch (NumberFormatException e) {
            sender.sendMessage("§cGeçersiz çarpan değeri!");
        }
    }

    private void handleOreConfigReset(CommandSender sender, String[] args) {
        if (args.length < 3) {
            sender.sendMessage("§cKullanım: /gezegen ore reset <gezegen-tipi>");
            return;
        }

        String planetType = args[2].toLowerCase();
        String path = "features.ores.planetTypes." + planetType;
        plugin.getConfig().set(path, null);
        plugin.saveConfig();
        sender.sendMessage("§e" + planetType.toUpperCase() + " gezegeninin ore konfigürasyonu sıfırlandı!");
    }

    private void handleConfig(CommandSender sender, String[] args) {
        if (!sender.hasPermission("skyorbs.admin")) {
            sender.sendMessage("§cYetkiniz yok!");
            return;
        }

        if (args.length < 2) {
            sendConfigHelp(sender);
            return;
        }

        String subCommand = args[1].toLowerCase();

        switch (subCommand) {
            case "list" -> handleConfigList(sender, args);
            case "set" -> handleConfigSet(sender, args);
            case "reset" -> handleConfigReset(sender, args);
            case "reload" -> handleConfigReload(sender);
            case "save" -> handleConfigSave(sender);
            case "backup" -> handleConfigBackup(sender);
            default -> sendConfigHelp(sender);
        }
    }

    private void handleConfigList(CommandSender sender, String[] args) {
        sender.sendMessage("§b═══ Sistem Konfigürasyonları ═══");

        // Generation settings
        sender.sendMessage("§e1. GEZEGEN BOYUTU:");
        sender.sendMessage("  • Min yarıçap: §a" + plugin.getConfigManager().getMinRadius());
        sender.sendMessage("  • Max yarıçap: §a" + plugin.getConfigManager().getMaxRadius());
        sender.sendMessage("  • Ortalama yarıçap: §a" + plugin.getConfigManager().getAverageRadius());

        // Placement settings
        sender.sendMessage("§e2. YERLEŞTİRME:");
        sender.sendMessage("  • Max uzaklık: §a" + plugin.getConfigManager().getMaxDistance());
        sender.sendMessage("  • Kümeleme aktif: §a" + (plugin.getConfigManager().isClusteringEnabled() ? "Evet" : "Hayır"));
        sender.sendMessage("  • Küme min uzaklık: §a" + plugin.getConfigManager().getClusterMinDistance());
        sender.sendMessage("  • Küme max uzaklık: §a" + plugin.getConfigManager().getClusterMaxDistance());

        // Performance settings
        sender.sendMessage("§e3. PERFORMANS:");
        sender.sendMessage("  • Batch boyutu: §a" + plugin.getConfigManager().getBlocksPerBatch());
        sender.sendMessage("  • Tick başına batch: §a" + plugin.getConfigManager().getBatchesPerTick());
        sender.sendMessage("  • Shell kalınlığı: §a" + plugin.getConfigManager().getShellThickness());

        // Feature settings
        sender.sendMessage("§e4. ÖZELLİKLER:");
        sender.sendMessage("  • Ore üretimi: §a" + (plugin.getConfigManager().isOreGenerationEnabled() ? "Aktif" : "Pasif"));
        sender.sendMessage("  • Ağaç üretimi: §a" + (plugin.getConfigManager().isTreeGenerationEnabled() ? "Aktif" : "Pasif"));
        sender.sendMessage("  • Yapı üretimi: §a" + (plugin.getConfigManager().isStructureGenerationEnabled() ? "Aktif" : "Pasif"));
        sender.sendMessage("  • Hazine üretimi: §a" + (plugin.getConfigManager().isTreasureGenerationEnabled() ? "Aktif" : "Pasif"));
        sender.sendMessage("  • Asteroid üretimi: §a" + (plugin.getConfigManager().isAsteroidsEnabled() ? "Aktif" : "Pasif"));

        // Limits
        sender.sendMessage("§e5. LİMİTLER:");
        sender.sendMessage("  • Oyuncu başına gezegen: §a" + plugin.getConfigManager().getPlanetsPerPlayer());
        sender.sendMessage("  • Dünya başına max gezegen: §a" + plugin.getConfigManager().getMaxPlanetsPerWorld());
        sender.sendMessage("  • Üretim cooldown: §a" + plugin.getConfigManager().getGenerationCooldown() + "s");
    }

    private void handleConfigSet(CommandSender sender, String[] args) {
        if (args.length < 4) {
            sender.sendMessage("§cKullanım: /gezegen config set <kategori> <ayar> <değer>");
            sender.sendMessage("§7Kategoriler: generation, placement, performance, features, limits");
            return;
        }

        String category = args[2].toLowerCase();
        String setting = args[3].toLowerCase();
        String valueStr = args.length > 4 ? args[4] : "";

        try {
            String configPath = getConfigPath(category, setting);
            if (configPath == null) {
                sender.sendMessage("§cGeçersiz kategori veya ayar!");
                return;
            }

            Object value;
            if (valueStr.equalsIgnoreCase("true") || valueStr.equalsIgnoreCase("false")) {
                value = Boolean.parseBoolean(valueStr);
            } else {
                try {
                    value = Integer.parseInt(valueStr);
                } catch (NumberFormatException e) {
                    try {
                        value = Double.parseDouble(valueStr);
                    } catch (NumberFormatException e2) {
                        value = valueStr; // String value
                    }
                }
            }

            plugin.getConfig().set(configPath, value);
            plugin.saveConfig();

            sender.sendMessage(String.format("§a%s.%s = %s olarak ayarlandı!", category, setting, value));

            // Special handling for caches
            if (category.equals("generation") && setting.equals("shapes")) {
                plugin.getConfigManager().clearCaches();
                sender.sendMessage("§eŞekil ağırlıkları cache'i temizlendi!");
            }

        } catch (Exception e) {
            sender.sendMessage("§cAyar değiştirilemedi: " + e.getMessage());
        }
    }

    private void handleConfigReset(CommandSender sender, String[] args) {
        if (args.length < 3) {
            sender.sendMessage("§cKullanım: /gezegen config reset <kategori>");
            return;
        }

        String category = args[2].toLowerCase();
        String path = getCategoryPath(category);

        if (path == null) {
            sender.sendMessage("§cGeçersiz kategori!");
            return;
        }

        plugin.getConfig().set(path, null);
        plugin.saveConfig();

        sender.sendMessage("§e" + category.toUpperCase() + " kategorisi varsayılan ayarlara sıfırlandı!");

        // Clear caches if needed
        if (category.equals("generation")) {
            plugin.getConfigManager().clearCaches();
        }
    }

    private void handleConfigReload(CommandSender sender) {
        try {
            plugin.reloadPluginConfig();
            sender.sendMessage("§aKonfigürasyon yeniden yüklendi!");
        } catch (Exception e) {
            sender.sendMessage("§cKonfigürasyon yüklenemedi: " + e.getMessage());
        }
    }

    private void handleConfigSave(CommandSender sender) {
        if (!sender.hasPermission("skyorbs.admin")) {
            sender.sendMessage("§cYetkiniz yok!");
            return;
        }

        try {
            plugin.saveConfig();
            sender.sendMessage("§aKonfigürasyon diske kaydedildi!");
        } catch (Exception e) {
            sender.sendMessage("§cKonfigürasyon kaydedilemedi: " + e.getMessage());
        }
    }

    private void handleConfigBackup(CommandSender sender) {
        if (!sender.hasPermission("skyorbs.admin")) {
            sender.sendMessage("§cYetkiniz yok!");
            return;
        }

        try {
            // Create backup filename with timestamp
            String timestamp = java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));
            String backupFileName = "config_backup_" + timestamp + ".yml";

            // Save current config to backup file
            plugin.getConfig().save(new java.io.File(plugin.getDataFolder(), "backups/" + backupFileName));

            sender.sendMessage("§aKonfigürasyon yedeği oluşturuldu: §f" + backupFileName);
        } catch (Exception e) {
            sender.sendMessage("§cYedek oluşturulamadı: " + e.getMessage());
        }
    }

    private String getConfigPath(String category, String setting) {
        return switch (category) {
            case "generation" -> switch (setting) {
                case "minradius" -> "generation.planetSize.minRadius";
                case "maxradius" -> "generation.planetSize.maxRadius";
                case "averageradius" -> "generation.planetSize.averageRadius";
                default -> null;
            };
            case "placement" -> switch (setting) {
                case "maxdistance" -> "placement.worldBounds.maxDistance";
                case "clustermindistance" -> "placement.clustering.clusterMinDistance";
                case "clustermaxdistance" -> "placement.clustering.clusterMaxDistance";
                case "clustering" -> "placement.clustering.enabled";
                default -> null;
            };
            case "performance" -> switch (setting) {
                case "blocksperbatch" -> "performance.batching.blocksPerBatch";
                case "batchespertick" -> "performance.batching.batchesPerTick";
                case "shellthickness" -> "performance.generation.shellThickness";
                default -> null;
            };
            case "features" -> switch (setting) {
                case "oregeneration" -> "features.ores.enabled";
                case "treegeneration" -> "features.trees.enabled";
                case "structuregeneration" -> "features.structures.enabled";
                case "treasuregeneration" -> "features.treasures.enabled";
                case "asteroids" -> "asteroids.enabled";
                default -> null;
            };
            case "limits" -> switch (setting) {
                case "planetsperplayer" -> "limits.planetsPerPlayer";
                case "maxplanetsperworld" -> "limits.maxPlanetsPerWorld";
                case "generationcooldown" -> "limits.generationCooldown";
                default -> null;
            };
            default -> null;
        };
    }

    private String getCategoryPath(String category) {
        return switch (category) {
            case "generation" -> "generation";
            case "placement" -> "placement";
            case "performance" -> "performance";
            case "features" -> "features";
            case "limits" -> "limits";
            default -> null;
        };
    }

    private void sendConfigHelp(CommandSender sender) {
        sender.sendMessage("§b═══ Sistem Konfigürasyon Komutları ═══");
        sender.sendMessage("§e/gezegen config list §7- Tüm ayarları listele");
        sender.sendMessage("§e/gezegen config set <kat> <ayar> <değer> §7- Ayar değiştir");
        sender.sendMessage("§e/gezegen config reset <kategori> §7- Kategoriyi varsayılana sıfırla");
        sender.sendMessage("§e/gezegen config reload §7- Konfigürasyonu yeniden yükle");
        sender.sendMessage("§e/gezegen config save §7- Konfigürasyonu diske kaydet");
        sender.sendMessage("§e/gezegen config backup §7- Konfigürasyon yedeği al");
        sender.sendMessage("§7");
        sender.sendMessage("§7Kategoriler: generation, placement, performance, features, limits");
        sender.sendMessage("§7Örnek: /gezegen config set performance blocksperbatch 500");
    }

    private void sendOreConfigHelp(CommandSender sender) {
        sender.sendMessage("§b═══ Ore Konfigürasyon Komutları ═══");
        sender.sendMessage("§e/gezegen ore list §7- Konfigüre edilmiş gezegenleri listele");
        sender.sendMessage("§e/gezegen ore set <tip> <maden> <özellik> <değer> §7- Maden ayarı değiştir");
        sender.sendMessage("§e/gezegen ore enable <tip> §7- Gezegen için ore üretimini aktifleştir");
        sender.sendMessage("§e/gezegen ore disable <tip> §7- Gezegen için ore üretimini devre dışı bırak");
        sender.sendMessage("§e/gezegen ore multiplier <tip> <çarpan> §7- Yoğunluk çarpanını ayarla");
        sender.sendMessage("§e/gezegen ore reset <tip> §7- Gezegeni varsayılan ayarlara sıfırla");
        sender.sendMessage("§7");
        sender.sendMessage("§7Özellikler: chance (0.0-1.0), minVein, maxVein, enabled (true/false)");
        sender.sendMessage("§7Gezegen tipleri: terrestrial, lava, crystal, ice, shadow, toxic, gas");
    }

    private void handleTest(CommandSender sender) {
        if (!sender.hasPermission("skyorbs.admin")) {
            sender.sendMessage("§cYetkiniz yok!");
            return;
        }

        sender.sendMessage("§b========================================");
        sender.sendMessage("§b§lSYSTEM TEST REPORT");
        sender.sendMessage("§b========================================");

        // 1. Config Test
        sender.sendMessage("§e1. CONFIG TEST:");
        sender.sendMessage("  • minRadius: §a" + plugin.getConfigManager().getMinRadius());
        sender.sendMessage("  • maxRadius: §a" + plugin.getConfigManager().getMaxRadius());
        sender.sendMessage("  • blocksPerBatch: §a" + plugin.getConfigManager().getBlocksPerBatch());

        // 2. Shape Registry Test
        sender.sendMessage("§e2. SHAPE REGISTRY TEST:");
        sender.sendMessage("  • Registered shapes: §a" + plugin.getShapeRegistry().getShapeCount());

        Map<String, Double> weights = plugin.getConfigManager().getShapeWeights();
        sender.sendMessage("  • Loaded weights: §a" + weights.size());

        // 3. Feature Test
        sender.sendMessage("§e3. FEATURE TEST:");
        sender.sendMessage("  • Ore Generation: " +
            (plugin.getConfigManager().isOreGenerationEnabled() ? "§aEnabled" : "§cDisabled"));
        sender.sendMessage("  • Hollow Planet Ores: " +
            (plugin.getConfig().getBoolean("features.ores.hollowPlanetSpawn", false) ? "§cEnabled (BAD)" : "§aDisabled (GOOD)"));

        // 4. Database Test
        sender.sendMessage("§e4. DATABASE TEST:");
        try {
            List<Orb> orbs = plugin.getDatabaseManager().getAllOrbs();
            sender.sendMessage("  • Total planets: §a" + orbs.size());
        } catch (Exception e) {
            sender.sendMessage("  §c✗ Database error: " + e.getMessage());
        }

        // 5. ORE PROFILE TEST
        sender.sendMessage("§e5. ORE PROFILE TEST:");

        // Test each planet type
        String[] testTypes = {"terrestrial", "lava", "crystal", "ice", "shadow", "toxic"};
        for (String type : testTypes) {
            var config = plugin.getConfigManager().getOreConfigForPlanetType(type);
            boolean enabled = (Boolean) config.getOrDefault("enabled", false);

            @SuppressWarnings("unchecked")
            var ores = (java.util.Map<String, java.util.Map<String, Object>>) config.get("ores");
            int oreCount = ores != null ? ores.size() : 0;

            sender.sendMessage("  • " + type.toUpperCase() + ": " +
                (enabled ? "§aEnabled" : "§cDisabled") + " §7| Ores: §e" + oreCount);
        }

        // 6. BIOME TO PLANET TYPE MAPPING TEST
        sender.sendMessage("§e6. BIOME MAPPING TEST:");
        sender.sendMessage("  • LAVA_OCEAN -> " + OreGenerator.getPlanetTypeFromBiome(BiomeType.LAVA_OCEAN).name());
        sender.sendMessage("  • CRYSTAL_FOREST -> " + OreGenerator.getPlanetTypeFromBiome(BiomeType.CRYSTAL_FOREST).name());
        sender.sendMessage("  • FROZEN_TUNDRA -> " + OreGenerator.getPlanetTypeFromBiome(BiomeType.FROZEN_TUNDRA).name());
        sender.sendMessage("  • VOID -> " + OreGenerator.getPlanetTypeFromBiome(BiomeType.VOID).name());

        sender.sendMessage("§b========================================");
    }
    
    private void sendHelp(CommandSender sender) {
        sender.sendMessage("§b═══ Gezegen Komutları ═══");
        sender.sendMessage("§e/gezegen create §7- Yeni gezegen oluştur");
        sender.sendMessage("§e/gezegen list §7- Tüm gezegenleri listele");
        sender.sendMessage("§e/gezegen tp <isim> §7- Gezegene ışınlan");
        sender.sendMessage("§e/gezegen info <isim> §7- Gezegen bilgilerini göster");
        sender.sendMessage("§e/gezegen sil <isim> §7- Gezegeni sil (Admin)");
        sender.sendMessage("§e/gezegen reload §7- Konfigürasyonu yenile (Admin)");
        sender.sendMessage("§e/gezegen ore §7- Ore konfigürasyon komutları (Admin)");
        sender.sendMessage("§e/gezegen config §7- Sistem konfigürasyon komutları (Admin)");
        sender.sendMessage("§e/gezegen test §7- Sistem testi (Admin)");
    }
    
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return Arrays.asList("create", "list", "tp", "info", "sil", "reload", "ore", "config", "test");
        }

        if (args.length == 2 && args[0].equalsIgnoreCase("ore")) {
            return Arrays.asList("list", "set", "enable", "disable", "multiplier", "reset");
        }

        if (args.length == 2 && args[0].equalsIgnoreCase("config")) {
            return Arrays.asList("list", "set", "reset", "reload", "save", "backup");
        }
        
        if (args.length == 2 && (args[0].equalsIgnoreCase("tp") || args[0].equalsIgnoreCase("info") || args[0].equalsIgnoreCase("sil"))) {
            try {
                List<Orb> orbs = plugin.getDatabaseManager().getAllOrbs();
                return orbs.stream().map(Orb::getName).toList();
            } catch (SQLException e) {
                return new ArrayList<>();
            }
        }
        
        return new ArrayList<>();
    }
}
