package com.skyorbs.gui;

import com.skyorbs.SkyOrbs;
import com.skyorbs.core.Orb;
import com.skyorbs.core.PlanetEvolution;
import com.skyorbs.core.PlanetType;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PlanetPanelGUI {

    private final SkyOrbs plugin;

    public PlanetPanelGUI(SkyOrbs plugin) {
        this.plugin = plugin;
    }

    public void openPlanetPanel(Player player, Orb orb) {
        Inventory gui = Bukkit.createInventory(null, 54, "§8[§bGezegen Paneli§8] §f" + orb.getName());

        // Core Information Section (Top Row)
        gui.setItem(4, createCoreInfoItem(orb));
        gui.setItem(3, createEvolutionInfoItem(orb));
        gui.setItem(5, createEnergyInfoItem(orb));

        // Planet Stats Section (Second Row)
        gui.setItem(9, createTypeInfoItem(orb));
        gui.setItem(10, createBiosphereInfoItem(orb));
        gui.setItem(11, createEcologyInfoItem(orb));
        gui.setItem(12, createSizeInfoItem(orb));

        // Actions Section (Third Row)
        gui.setItem(18, createTeleportItem(orb));
        gui.setItem(19, createLaboratoryItem(orb));
        gui.setItem(20, createAchievementsItem(orb));
        gui.setItem(21, createSettingsItem(orb));

        // Quick Actions (Fourth Row)
        gui.setItem(27, createRenameItem(orb));
        gui.setItem(28, createShareItem(orb));
        gui.setItem(29, createBackupItem(orb));
        gui.setItem(30, createDeleteItem(orb));

        // Planet Theme Section (Fifth Row)
        gui.setItem(36, createThemeItem(orb));
        gui.setItem(37, createEffectsItem(orb));
        gui.setItem(38, createEventsItem(orb));

        // Admin Section (Bottom Row - if player has permission)
        if (player.hasPermission("skyorbs.admin")) {
            gui.setItem(45, createAdminScanItem(orb));
            gui.setItem(46, createAdminRepairItem(orb));
            gui.setItem(47, createAdminDebugItem(orb));
        }

        // Close button
        gui.setItem(49, createCloseItem());

        player.openInventory(gui);
    }

    private ItemStack createCoreInfoItem(Orb orb) {
        ItemStack item = new ItemStack(getCoreMaterial(orb.getCoreLevel()));
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§6§lÇekirdek Bilgileri");
        List<String> lore = new ArrayList<>();
        lore.add("§7Seviye: §e" + orb.getCoreLevel() + " §7/ §e10");
        lore.add("§7XP: §a" + orb.getXp() + " §7/ §a" + orb.getXpForNextLevel());
        lore.add("");
        lore.add("§7Sonraki seviye için gereken XP:");
        lore.add("§c" + (orb.getXpForNextLevel() - orb.getXp()));
        if (orb.canUpgrade()) {
            lore.add("");
            lore.add("§a§lYükseltmeye hazır!");
        }
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack createEvolutionInfoItem(Orb orb) {
        ItemStack item = new ItemStack(Material.ENCHANTED_BOOK);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§b§lEvrim Bilgileri");
        List<String> lore = new ArrayList<>();
        PlanetEvolution evolution = new PlanetEvolution(orb);
        lore.add("§7" + evolution.getEvolutionDescription());
        lore.add("");
        Map<String, Object> reqs = evolution.getEvolutionRequirements();
        lore.add("§7İlerleme: §e" + String.format("%.1f", reqs.get("progressPercent")) + "%");
        if ((Boolean) reqs.get("canUpgrade")) {
            lore.add("");
            lore.add("§a§lTıklayarak yükselt!");
        }
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack createEnergyInfoItem(Orb orb) {
        ItemStack item = new ItemStack(Material.REDSTONE);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§c§lEnerji Seviyesi");
        List<String> lore = new ArrayList<>();
        lore.add("§7Enerji: §c" + String.format("%.1f", orb.getEnergyLevel()) + "%");
        lore.add("§7Rejenerasyon: §a+" + String.format("%.2f", orb.getEnergyRegenRate()) + "/dk");
        double energyPercent = orb.getEnergyLevel() / 100.0;
        if (energyPercent > 0.8) {
            lore.add("§aEnerji dolu!");
        } else if (energyPercent > 0.5) {
            lore.add("§eOrta seviye enerji");
        } else if (energyPercent > 0.2) {
            lore.add("§6Düşük enerji");
        } else {
            lore.add("§cKritik enerji seviyesi!");
        }
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack createTypeInfoItem(Orb orb) {
        ItemStack item = new ItemStack(orb.getPlanetType().getIconMaterial());
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§9§lGezegen Tipi");
        List<String> lore = new ArrayList<>();
        lore.add("§7Tip: §9" + orb.getPlanetType().getDisplayName());
        lore.add("");
        lore.add("§7" + orb.getPlanetType().getDescription());
        lore.add("");
        lore.add("§7Enerji Çarpanı: §e" + String.format("%.1f", orb.getPlanetType().getEnergyMultiplier()) + "x");
        lore.add("§7Max Biyosfer: §a" + orb.getPlanetType().getMaxBiosphereLevel());
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack createBiosphereInfoItem(Orb orb) {
        ItemStack item = new ItemStack(Material.OAK_SAPLING);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§2§lBiyosfer");
        List<String> lore = new ArrayList<>();
        lore.add("§7Seviye: §2" + orb.getBiosphereLevel() + " §7/ §25");
        lore.add("");
        String levelDesc = switch (orb.getBiosphereLevel()) {
            case 1 -> "§7Temel yaşam formları";
            case 2 -> "§7Gelişen ekosistem";
            case 3 -> "§7Çeşitli flora ve fauna";
            case 4 -> "§7Zengin biyolojik çeşitlilik";
            case 5 -> "§7Maksimum ekolojik denge";
            default -> "§7Bilinmeyen seviye";
        };
        lore.add(levelDesc);
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack createEcologyInfoItem(Orb orb) {
        ItemStack item = new ItemStack(Material.COMPOSTER);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§a§lEkolojik Denge");
        List<String> lore = new ArrayList<>();
        double balance = orb.getEcologicalBalance();
        lore.add("§7Denge: §a" + String.format("%.2f", balance));
        lore.add("");
        if (balance > 1.5) {
            lore.add("§aMükemmel ekolojik denge!");
        } else if (balance > 1.0) {
            lore.add("§eİyi ekolojik durum");
        } else if (balance > 0.8) {
            lore.add("§6Denge bozuluyor");
        } else if (balance > 0.5) {
            lore.add("§cKritik ekolojik durum!");
        } else {
            lore.add("§4Çevresel felaket!");
        }
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack createSizeInfoItem(Orb orb) {
        ItemStack item = new ItemStack(Material.BARRIER);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§f§lGezegen Boyutu");
        List<String> lore = new ArrayList<>();
        lore.add("§7Yarıçap: §f" + orb.getRadius() + " blok");
        lore.add("§7Hacim: §f" + String.format("%.0f", (4.0/3.0) * Math.PI * Math.pow(orb.getRadius(), 3)) + " blok³");
        lore.add("§7Konum: §f" + orb.getCenterX() + ", " + orb.getCenterY() + ", " + orb.getCenterZ());
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack createTeleportItem(Orb orb) {
        ItemStack item = new ItemStack(Material.ENDER_PEARL);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§5§lIşınlanma");
        List<String> lore = new ArrayList<>();
        lore.add("§7Gezegene ışınlan");
        lore.add("");
        lore.add("§eTıklayarak ışınlan!");
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack createLaboratoryItem(Orb orb) {
        ItemStack item = new ItemStack(Material.BREWING_STAND);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§d§lLaboratuvar");
        List<String> lore = new ArrayList<>();
        lore.add("§7Gezegen laboratuvarı");
        lore.add("§7Yükseltmeler ve araştırmalar");
        lore.add("");
        lore.add("§eTıklayarak aç!");
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack createAchievementsItem(Orb orb) {
        ItemStack item = new ItemStack(Material.GOLDEN_APPLE);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§6§lBaşarımlar");
        List<String> lore = new ArrayList<>();
        lore.add("§7Gezegen başarımları");
        lore.add("§7Özel ödüller ve unvanlar");
        lore.add("");
        lore.add("§eTıklayarak görüntüle!");
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack createSettingsItem(Orb orb) {
        ItemStack item = new ItemStack(Material.COMPARATOR);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§3§lAyarlar");
        List<String> lore = new ArrayList<>();
        lore.add("§7Gezegen ayarları");
        lore.add("§7İsim, izinler, tema");
        lore.add("");
        lore.add("§eTıklayarak aç!");
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack createRenameItem(Orb orb) {
        ItemStack item = new ItemStack(Material.NAME_TAG);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§f§lYeniden Adlandır");
        List<String> lore = new ArrayList<>();
        lore.add("§7Gezegen adını değiştir");
        lore.add("§7Token gerektirir");
        lore.add("");
        lore.add("§eTıklayarak aç!");
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack createShareItem(Orb orb) {
        ItemStack item = new ItemStack(Material.WRITABLE_BOOK);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§9§lPaylaş");
        List<String> lore = new ArrayList<>();
        lore.add("§7Gezegeni diğer oyuncularla paylaş");
        lore.add("§7Davetiye gönder");
        lore.add("");
        lore.add("§eTıklayarak aç!");
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack createBackupItem(Orb orb) {
        ItemStack item = new ItemStack(Material.BOOKSHELF);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§8§lYedekle");
        List<String> lore = new ArrayList<>();
        lore.add("§7Gezegeni yedekle");
        lore.add("§7Veri kaybına karşı koru");
        lore.add("");
        lore.add("§eTıklayarak yedekle!");
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack createDeleteItem(Orb orb) {
        ItemStack item = new ItemStack(Material.TNT);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§4§lSil");
        List<String> lore = new ArrayList<>();
        lore.add("§7Gezegeni sil");
        lore.add("§4§lBu işlem geri alınamaz!");
        lore.add("");
        lore.add("§cSağ tıkla onay için");
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack createThemeItem(Orb orb) {
        ItemStack item = new ItemStack(Material.PAINTING);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§d§lTema");
        List<String> lore = new ArrayList<>();
        lore.add("§7Gezegen temasını değiştir");
        lore.add("§7Renkler, efektler, müzik");
        lore.add("");
        lore.add("§eTıklayarak aç!");
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack createEffectsItem(Orb orb) {
        ItemStack item = new ItemStack(Material.BEACON);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§b§lEfektler");
        List<String> lore = new ArrayList<>();
        lore.add("§7Aktif gezegen efektleri");
        lore.add("§7Aura, partiküller, sesler");
        lore.add("");
        lore.add("§eTıklayarak görüntüle!");
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack createEventsItem(Orb orb) {
        ItemStack item = new ItemStack(Material.FIREWORK_ROCKET);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§e§lOlaylar");
        List<String> lore = new ArrayList<>();
        lore.add("§7Aktif gök olayları");
        lore.add("§7Fırtınalar, yağmurlar, patlamalar");
        lore.add("");
        lore.add("§eTıklayarak görüntüle!");
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack createAdminScanItem(Orb orb) {
        ItemStack item = new ItemStack(Material.SPYGLASS);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§c§l[ADMIN] Tara");
        List<String> lore = new ArrayList<>();
        lore.add("§7Bozuk chunkları tara");
        lore.add("§7Sorunları tespit et");
        lore.add("");
        lore.add("§cAdmin komutu");
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack createAdminRepairItem(Orb orb) {
        ItemStack item = new ItemStack(Material.ANVIL);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§c§l[ADMIN] Onar");
        List<String> lore = new ArrayList<>();
        lore.add("§7Gezegeni onar");
        lore.add("§7Biyom ve ore rejenerasyonu");
        lore.add("");
        lore.add("§cAdmin komutu");
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack createAdminDebugItem(Orb orb) {
        ItemStack item = new ItemStack(Material.COMMAND_BLOCK);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§c§l[ADMIN] Debug");
        List<String> lore = new ArrayList<>();
        lore.add("§7Debug bilgileri");
        lore.add("§7Detaylı loglar");
        lore.add("");
        lore.add("§cAdmin komutu");
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack createCloseItem() {
        ItemStack item = new ItemStack(Material.BARRIER);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§c§lKapat");
        List<String> lore = new ArrayList<>();
        lore.add("§7Paneli kapat");
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    private Material getCoreMaterial(int level) {
        return switch (level) {
            case 1 -> Material.COAL_BLOCK;
            case 2 -> Material.IRON_BLOCK;
            case 3 -> Material.GOLD_BLOCK;
            case 4 -> Material.DIAMOND_BLOCK;
            case 5 -> Material.EMERALD_BLOCK;
            case 6 -> Material.NETHERITE_BLOCK;
            case 7 -> Material.AMETHYST_BLOCK;
            case 8 -> Material.CRYING_OBSIDIAN;
            case 9 -> Material.ANCIENT_DEBRIS;
            case 10 -> Material.NETHER_STAR;
            default -> Material.STONE;
        };
    }
}