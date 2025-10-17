package com.skyorbs.commands;

import com.skyorbs.SkyOrbs;
import com.skyorbs.core.Orb;
import org.bukkit.*;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.*;

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
        
        plugin.reloadPluginConfig();
        sender.sendMessage(plugin.getConfigManager().getMessage("configReloaded"));
    }
    
    private void sendHelp(CommandSender sender) {
        sender.sendMessage("§b═══ Gezegen Komutları ═══");
        sender.sendMessage("§e/gezegen create §7- Yeni gezegen oluştur");
        sender.sendMessage("§e/gezegen list §7- Tüm gezegenleri listele");
        sender.sendMessage("§e/gezegen tp <isim> §7- Gezegene ışınlan");
        sender.sendMessage("§e/gezegen info <isim> §7- Gezegen bilgilerini göster");
        sender.sendMessage("§e/gezegen sil <isim> §7- Gezegeni sil (Admin)");
        sender.sendMessage("§e/gezegen reload §7- Konfigürasyonu yenile (Admin)");
    }
    
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return Arrays.asList("create", "list", "tp", "info", "sil", "reload");
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
