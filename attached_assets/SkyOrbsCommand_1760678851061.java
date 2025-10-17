package com.skyorbs.commands;

import com.skyorbs.SkyOrbs;
import com.skyorbs.core.Orb;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SkyOrbsCommand implements CommandExecutor, TabCompleter {
    
    private final SkyOrbs plugin;
    
    public SkyOrbsCommand(SkyOrbs plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sendHelp(sender);
            return true;
        }
        
        switch (args[0].toLowerCase()) {
            case "create" -> handleCreate(sender, args);
            case "list" -> handleList(sender);
            case "tp", "teleport" -> handleTeleport(sender, args);
            case "delete", "remove" -> handleDelete(sender, args);
            case "info" -> handleInfo(sender, args);
            case "reload" -> handleReload(sender);
            default -> sendHelp(sender);
        }
        
        return true;
    }
    
    private void handleCreate(CommandSender sender, String[] args) {
        if (!sender.hasPermission("skyorbs.create")) {
            sender.sendMessage(plugin.getConfigManager().getMessage("noPermission"));
            return;
        }
        
        if (!(sender instanceof Player player)) {
            sender.sendMessage(plugin.getConfigManager().getMessage("playerOnly"));
            return;
        }
        
        if (args.length < 2) {
            sender.sendMessage("§cKullanım: /skyorbs create <isim>");
            return;
        }
        
        String name = args[1];
        World world = player.getWorld();
        
        sender.sendMessage(plugin.getConfigManager().getMessage("generating"));
        
        plugin.getGenerationManager().createPlanet(world, name)
            .thenAccept(orb -> {
                String message = plugin.getConfigManager().getMessageRaw("planetCreated")
                    .replace("{id}", orb.getId().toString());
                sender.sendMessage(message);
                
                String info = String.format("§aGezegen: §e%s §7(%s, %s)", 
                    orb.getName(),
                    plugin.getShapeRegistry().getShape(orb.getShapeName()).getDisplayName(),
                    orb.getBiomeName()
                );
                sender.sendMessage(info);
                sender.sendMessage(String.format("§aKonum: §f%d, %d, %d §7| Yarıçap: §f%d", 
                    orb.getCenterX(), orb.getCenterY(), orb.getCenterZ(), orb.getRadius()
                ));
            })
            .exceptionally(e -> {
                sender.sendMessage("§cHata: " + e.getMessage());
                plugin.logError("Gezegen oluşturma hatası", (Exception) e);
                return null;
            });
    }
    
    private void handleList(CommandSender sender) {
        if (!sender.hasPermission("skyorbs.use")) {
            sender.sendMessage(plugin.getConfigManager().getMessage("noPermission"));
            return;
        }
        
        try {
            List<Orb> orbs = plugin.getDatabaseManager().getAllOrbs();
            
            if (orbs.isEmpty()) {
                sender.sendMessage("§eHenüz hiç gezegen oluşturulmamış.");
                return;
            }
            
            String header = plugin.getConfigManager().getMessageRaw("listHeader")
                .replace("{count}", String.valueOf(orbs.size()));
            sender.sendMessage(header);
            
            for (Orb orb : orbs) {
                String shapeName = plugin.getShapeRegistry().getShape(orb.getShapeName()) != null
                    ? plugin.getShapeRegistry().getShape(orb.getShapeName()).getDisplayName()
                    : orb.getShapeName();
                
                String entry = plugin.getConfigManager().getMessageRaw("listEntry")
                    .replace("{name}", orb.getName())
                    .replace("{shape}", shapeName)
                    .replace("{x}", String.valueOf(orb.getCenterX()))
                    .replace("{y}", String.valueOf(orb.getCenterY()))
                    .replace("{z}", String.valueOf(orb.getCenterZ()));
                sender.sendMessage(entry);
            }
            
        } catch (SQLException e) {
            sender.sendMessage("§cVeritabanı hatası!");
            plugin.logError("Liste hatası", e);
        }
    }
    
    private void handleTeleport(CommandSender sender, String[] args) {
        if (!sender.hasPermission("skyorbs.tp")) {
            sender.sendMessage(plugin.getConfigManager().getMessage("noPermission"));
            return;
        }
        
        if (!(sender instanceof Player player)) {
            sender.sendMessage(plugin.getConfigManager().getMessage("playerOnly"));
            return;
        }
        
        if (args.length < 2) {
            sender.sendMessage("§cKullanım: /skyorbs tp <gezegen-ismi>");
            return;
        }
        
        String name = args[1];
        
        try {
            Orb orb = plugin.getDatabaseManager().getOrbByName(name);
            
            if (orb == null) {
                String message = plugin.getConfigManager().getMessageRaw("planetNotFound")
                    .replace("{name}", name);
                sender.sendMessage(message);
                return;
            }
            
            World world = Bukkit.getWorld(orb.getWorldName());
            if (world == null) {
                sender.sendMessage("§cGezegen dünyası bulunamadı!");
                return;
            }
            
            Location loc = new Location(world, orb.getCenterX(), orb.getCenterY() + orb.getRadius() + 10, orb.getCenterZ());
            player.teleport(loc);
            
            String message = plugin.getConfigManager().getMessageRaw("teleported")
                .replace("{name}", orb.getName());
            sender.sendMessage(message);
            
        } catch (SQLException e) {
            sender.sendMessage("§cVeritabanı hatası!");
            plugin.logError("Teleport hatası", e);
        }
    }
    
    private void handleDelete(CommandSender sender, String[] args) {
        if (!sender.hasPermission("skyorbs.delete")) {
            sender.sendMessage(plugin.getConfigManager().getMessage("noPermission"));
            return;
        }
        
        if (args.length < 2) {
            sender.sendMessage("§cKullanım: /skyorbs delete <gezegen-ismi>");
            return;
        }
        
        String name = args[1];
        
        try {
            Orb orb = plugin.getDatabaseManager().getOrbByName(name);
            
            if (orb == null) {
                String message = plugin.getConfigManager().getMessageRaw("planetNotFound")
                    .replace("{name}", name);
                sender.sendMessage(message);
                return;
            }
            
            sender.sendMessage("§eGezegen siliniyor...");
            
            plugin.getGenerationManager().deletePlanet(orb)
                .thenRun(() -> {
                    String message = plugin.getConfigManager().getMessageRaw("planetDeleted")
                        .replace("{name}", orb.getName());
                    sender.sendMessage(message);
                })
                .exceptionally(e -> {
                    sender.sendMessage("§cHata: " + e.getMessage());
                    return null;
                });
            
        } catch (SQLException e) {
            sender.sendMessage("§cVeritabanı hatası!");
            plugin.logError("Silme hatası", e);
        }
    }
    
    private void handleInfo(CommandSender sender, String[] args) {
        if (!sender.hasPermission("skyorbs.use")) {
            sender.sendMessage(plugin.getConfigManager().getMessage("noPermission"));
            return;
        }
        
        if (args.length < 2) {
            sender.sendMessage("§cKullanım: /skyorbs info <gezegen-ismi>");
            return;
        }
        
        String name = args[1];
        
        try {
            Orb orb = plugin.getDatabaseManager().getOrbByName(name);
            
            if (orb == null) {
                String message = plugin.getConfigManager().getMessageRaw("planetNotFound")
                    .replace("{name}", name);
                sender.sendMessage(message);
                return;
            }
            
            sender.sendMessage("§b=== Gezegen Bilgileri ===");
            sender.sendMessage("§7İsim: §e" + orb.getName());
            sender.sendMessage("§7Şekil: §f" + plugin.getShapeRegistry().getShape(orb.getShapeName()).getDisplayName());
            sender.sendMessage("§7Biyom: §f" + orb.getBiomeName());
            sender.sendMessage("§7Yarıçap: §f" + orb.getRadius() + " blok");
            sender.sendMessage("§7Merkez: §f" + orb.getCenterX() + ", " + orb.getCenterY() + ", " + orb.getCenterZ());
            sender.sendMessage("§7Dünya: §f" + orb.getWorldName());
            sender.sendMessage("§7ID: §8" + orb.getId());
            
        } catch (SQLException e) {
            sender.sendMessage("§cVeritabanı hatası!");
            plugin.logError("Info hatası", e);
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
        sender.sendMessage("§b=== SkyOrbs Yardım ===");
        sender.sendMessage("§e/skyorbs create <isim> §7- Yeni gezegen oluştur");
        sender.sendMessage("§e/skyorbs list §7- Tüm gezegenleri listele");
        sender.sendMessage("§e/skyorbs tp <isim> §7- Gezegene ışınlan");
        sender.sendMessage("§e/skyorbs delete <isim> §7- Gezegeni sil");
        sender.sendMessage("§e/skyorbs info <isim> §7- Gezegen bilgilerini göster");
        sender.sendMessage("§e/skyorbs reload §7- Konfigürasyonu yeniden yükle");
    }
    
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return Arrays.asList("create", "list", "tp", "delete", "info", "reload");
        }
        
        if (args.length == 2 && (args[0].equalsIgnoreCase("tp") || args[0].equalsIgnoreCase("delete") || args[0].equalsIgnoreCase("info"))) {
            try {
                List<Orb> orbs = plugin.getDatabaseManager().getAllOrbs();
                List<String> names = new ArrayList<>();
                for (Orb orb : orbs) {
                    names.add(orb.getName());
                }
                return names;
            } catch (SQLException e) {
                return new ArrayList<>();
            }
        }
        
        return new ArrayList<>();
    }
}
