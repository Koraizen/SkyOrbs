package com.skyorbs.listeners;

import com.skyorbs.SkyOrbs;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class GUIClickListener implements Listener {

    private final SkyOrbs plugin;

    public GUIClickListener(SkyOrbs plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        String title = event.getView().getTitle();

        if (title.contains("Admin Konfigürasyon")) {
            event.setCancelled(true); // Cancel item movement

            if (event.getCurrentItem() == null) return;

            // Handle clicks here
            String itemName = event.getCurrentItem().getItemMeta().getDisplayName();

            if (itemName.contains("Konfigürasyonu Yenile")) {
                plugin.reloadPluginConfig();
                event.getWhoClicked().sendMessage("§aKonfigürasyon yenilendi!");
                event.getWhoClicked().closeInventory();
            }
            // ... diğer butonlar için handler'lar ekle
        }
    }
}