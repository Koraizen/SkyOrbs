package com.skyorbs.events;

import com.skyorbs.SkyOrbs;
import com.skyorbs.core.Orb;
import com.skyorbs.core.PlanetType;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class CelestialEvents {

    private final SkyOrbs plugin;
    private final Random random = new Random();
    private final Map<UUID, ActiveEvent> activeEvents = new HashMap<>();

    public CelestialEvents(SkyOrbs plugin) {
        this.plugin = plugin;
        startEventScheduler();
    }

    private void startEventScheduler() {
        new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    // Check for new events every 5 minutes
                    List<Orb> planets = plugin.getDatabaseManager().getAllOrbs();
                    for (Orb orb : planets) {
                        if (random.nextDouble() < 0.05) { // 5% chance per planet per check
                            triggerRandomEvent(orb);
                        }
                    }
                } catch (Exception e) {
                    plugin.logError("Event scheduler error", e);
                }
            }
        }.runTaskTimer(plugin, 0L, 6000L); // Every 5 minutes
    }

    public void triggerRandomEvent(Orb orb) {
        if (activeEvents.containsKey(orb.getId())) {
            return; // Already has an active event
        }

        CelestialEventType eventType = getRandomEventType(orb);
        if (eventType != null) {
            startEvent(orb, eventType);
        }
    }

    public void triggerSpecificEvent(Orb orb, CelestialEventType eventType) {
        if (!activeEvents.containsKey(orb.getId())) {
            startEvent(orb, eventType);
        }
    }

    private CelestialEventType getRandomEventType(Orb orb) {
        List<CelestialEventType> availableEvents = new ArrayList<>();

        // Base events for all planets
        availableEvents.add(CelestialEventType.METEOR_SHOWER);
        availableEvents.add(CelestialEventType.SOLAR_FLARE);

        // Planet-specific events
        PlanetType type = orb.getPlanetType();
        switch (type) {
            case TERRESTRIAL -> {
                availableEvents.add(CelestialEventType.RAINFALL);
                availableEvents.add(CelestialEventType.MAGNETIC_STORM);
                if (orb.getBiosphereLevel() >= 3) {
                    availableEvents.add(CelestialEventType.BLOSSOMING);
                }
            }
            case GAS -> {
                availableEvents.add(CelestialEventType.GAS_STORM);
                availableEvents.add(CelestialEventType.AURORA);
            }
            case LAVA -> {
                availableEvents.add(CelestialEventType.VOLCANIC_ERUPTION);
                availableEvents.add(CelestialEventType.LAVA_RAIN);
            }
            case ICE -> {
                availableEvents.add(CelestialEventType.BLIZZARD);
                availableEvents.add(CelestialEventType.AURORA);
            }
            case CRYSTAL -> {
                availableEvents.add(CelestialEventType.CRYSTAL_STORM);
                availableEvents.add(CelestialEventType.ENERGY_SURGE);
            }
            case SHADOW -> {
                availableEvents.add(CelestialEventType.SHADOW_FOG);
                availableEvents.add(CelestialEventType.NEBULA);
            }
            case TOXIC -> {
                availableEvents.add(CelestialEventType.TOXIC_FOG);
                availableEvents.add(CelestialEventType.ACID_RAIN);
            }
        }

        // Core level based events
        if (orb.getCoreLevel() >= 5) {
            availableEvents.add(CelestialEventType.CELESTIAL_ALIGNMENT);
        }

        if (orb.getCoreLevel() >= 8) {
            availableEvents.add(CelestialEventType.DIMENSIONAL_RIFT);
        }

        return availableEvents.get(random.nextInt(availableEvents.size()));
    }

    private void startEvent(Orb orb, CelestialEventType eventType) {
        ActiveEvent event = new ActiveEvent(orb, eventType);
        activeEvents.put(orb.getId(), event);

        // Announce event to players on the planet
        announceEvent(orb, eventType);

        // Start event effects
        event.start();

        // Schedule event end
        new BukkitRunnable() {
            @Override
            public void run() {
                endEvent(orb.getId());
            }
        }.runTaskLater(plugin, eventType.getDuration() * 20L); // Convert seconds to ticks
    }

    private void announceEvent(Orb orb, CelestialEventType eventType) {
        String message = String.format("§e🌟 Gezegen §b%s §eüzerinde §a%s §eolayı başladı!",
                                     orb.getName(), eventType.getDisplayName());

        // Send to all players on the planet
        World world = Bukkit.getWorld(orb.getWorldName());
        if (world != null) {
            for (Player player : world.getPlayers()) {
                Location centerLoc = new Location(world, orb.getCenterX(), orb.getCenterY(), orb.getCenterZ());
                double distance = player.getLocation().distance(centerLoc);
                if (distance <= orb.getRadius() + 50) {
                    player.sendMessage(message);
                    player.sendMessage("§7" + eventType.getDescription());
                }
            }
        }
    }

    private void endEvent(UUID orbId) {
        ActiveEvent event = activeEvents.remove(orbId);
        if (event != null) {
            event.end();
            // Announce event end
            try {
                Orb orb = plugin.getDatabaseManager().getOrb(orbId);
                if (orb != null) {
                    String message = String.format("§e🌟 Gezegen §b%s §eüzerindeki §a%s §eolayı sona erdi!",
                                                  orb.getName(), event.eventType.getDisplayName());
                    announceToPlanet(orb, message);
                }
            } catch (Exception e) {
                plugin.logError("Event end announcement error", e);
            }
        }
    }

    private void announceToPlanet(Orb orb, String message) {
        World world = Bukkit.getWorld(orb.getWorldName());
        if (world != null) {
            for (Player player : world.getPlayers()) {
                Location centerLoc = new Location(world, orb.getCenterX(), orb.getCenterY(), orb.getCenterZ());
                double distance = player.getLocation().distance(centerLoc);
                if (distance <= orb.getRadius() + 50) {
                    player.sendMessage(message);
                }
            }
        }
    }

    public boolean hasActiveEvent(UUID orbId) {
        return activeEvents.containsKey(orbId);
    }

    public CelestialEventType getActiveEventType(UUID orbId) {
        ActiveEvent event = activeEvents.get(orbId);
        return event != null ? event.eventType : null;
    }

    public enum CelestialEventType {
        // Universal Events
        METEOR_SHOWER("Göktaşı Yağmuru", "Gökyüzünden göktaşları yağyor!", 300, Material.FIREWORK_ROCKET),
        SOLAR_FLARE("Güneş Patlaması", "Güneşten gelen radyasyon dalgası!", 180, Material.SUNFLOWER),

        // Terrestrial Events
        RAINFALL("Yağmur", "Canlandırıcı yağmur başlıyor!", 600, Material.WATER_BUCKET),
        MAGNETIC_STORM("Manyetik Fırtına", "Manyetik alanlar nadir mineraller çıkarıyor!", 480, Material.IRON_INGOT),
        BLOSSOMING("Çiçek Açma", "Gezegen çiçeklerle kaplanıyor!", 900, Material.POPPY),

        // Gas Events
        GAS_STORM("Gaz Fırtınası", "Şiddetli gaz fırtınaları!", 420, Material.BLUE_WOOL),
        AURORA("Aurora", "Kutup ışıkları dans ediyor!", 720, Material.LIGHT_BLUE_WOOL),

        // Lava Events
        VOLCANIC_ERUPTION("Volkanik Patlama", "Volkanlar uyanıyor!", 360, Material.LAVA_BUCKET),
        LAVA_RAIN("Lav Yağmuru", "Gökyüzünden lav damlaları!", 300, Material.MAGMA_BLOCK),

        // Ice Events
        BLIZZARD("Kar Fırtınası", "Şiddetli kar fırtınası!", 480, Material.SNOW_BLOCK),

        // Crystal Events
        CRYSTAL_STORM("Kristal Fırtınası", "Kristaller gökyüzünden yağıyor!", 540, Material.AMETHYST_SHARD),
        ENERGY_SURGE("Enerji Artışı", "Kristal enerji seviyesi yükseliyor!", 600, Material.AMETHYST_BLOCK),

        // Shadow Events
        SHADOW_FOG("Gölge Sisi", "Gölge sisleri her yeri kaplıyor!", 420, Material.BLACK_WOOL),
        NEBULA("Nebula", "Gezegen nebulaya sarılıyor!", 780, Material.PURPLE_WOOL),

        // Toxic Events
        TOXIC_FOG("Zehirli Sis", "Zehirli sisler yayılıyor!", 360, Material.SLIME_BLOCK),
        ACID_RAIN("Asit Yağmuru", "Asit yağmuru başlıyor!", 480, Material.GREEN_WOOL),

        // Advanced Events
        CELESTIAL_ALIGNMENT("Gök Cisimleri Hizalama", "Nadir kozmik olay!", 1200, Material.NETHER_STAR),
        DIMENSIONAL_RIFT("Boyutsal Yarık", "Başka boyutlardan enerji akıyor!", 900, Material.END_PORTAL_FRAME);

        private final String displayName;
        private final String description;
        private final int duration; // in seconds
        private final Material icon;

        CelestialEventType(String displayName, String description, int duration, Material icon) {
            this.displayName = displayName;
            this.description = description;
            this.duration = duration;
            this.icon = icon;
        }

        public String getDisplayName() { return displayName; }
        public String getDescription() { return description; }
        public int getDuration() { return duration; }
        public Material getIcon() { return icon; }
    }

    private class ActiveEvent {
        private final Orb orb;
        private final CelestialEventType eventType;
        private final long startTime;
        private BukkitRunnable effectTask;

        public ActiveEvent(Orb orb, CelestialEventType eventType) {
            this.orb = orb;
            this.eventType = eventType;
            this.startTime = System.currentTimeMillis();
        }

        public void start() {
            // Start event-specific effects
            effectTask = new BukkitRunnable() {
                @Override
                public void run() {
                    applyEventEffects();
                }
            };
            effectTask.runTaskTimer(plugin, 0L, 20L); // Every second
        }

        public void end() {
            if (effectTask != null) {
                effectTask.cancel();
            }
            // Apply end effects if any
            applyEventEndEffects();
        }

        private void applyEventEffects() {
            World world = Bukkit.getWorld(orb.getWorldName());
            if (world == null) return;

            switch (eventType) {
                case METEOR_SHOWER -> {
                    // Spawn occasional firework effects
                    if (random.nextDouble() < 0.1) {
                        // Simulate meteor effects
                    }
                }
                case RAINFALL -> {
                    // Increase random ticks for plant growth
                    // This would require custom tick handling
                }
                case MAGNETIC_STORM -> {
                    // Occasionally spawn rare ores
                    if (random.nextDouble() < 0.05) {
                        spawnRareOre(orb, world);
                    }
                }
                // Add more event effects...
            }
        }

        private void applyEventEndEffects() {
            // Apply effects when event ends
            switch (eventType) {
                case MAGNETIC_STORM -> {
                    // Give bonus XP for surviving the storm
                    // plugin.getEvolutionManager().gainXpFromActivity(orb, "magnetic_storm_survival", 500);
                }
                case BLOSSOMING -> {
                    // Permanent flora boost
                    // plugin.getBiosphereManager().updateBiosphere(orb, "blossoming");
                }
                // Add more end effects...
            }
        }

        private void spawnRareOre(Orb orb, World world) {
            int x = orb.getCenterX() + random.nextInt(orb.getRadius() * 2) - orb.getRadius();
            int z = orb.getCenterZ() + random.nextInt(orb.getRadius() * 2) - orb.getRadius();
            int y = orb.getCenterY() + random.nextInt(orb.getRadius()) - orb.getRadius() / 2;

            Block block = world.getBlockAt(x, y, z);
            if (block.getType() == Material.STONE || block.getType() == Material.DEEPSLATE) {
                Material ore = random.nextDouble() < 0.8 ? Material.DIAMOND_ORE : Material.EMERALD_ORE;
                block.setType(ore);
            }
        }
    }
}