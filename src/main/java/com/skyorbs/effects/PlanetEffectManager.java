package com.skyorbs.effects;

import com.skyorbs.SkyOrbs;
import com.skyorbs.biomes.BiomeType;
import com.skyorbs.core.Orb;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

public class PlanetEffectManager {

    private final SkyOrbs plugin;
    private final Map<UUID, PlanetAura> activeAuras = new HashMap<>();
    private final Map<UUID, PlanetSoundscape> activeSoundscapes = new HashMap<>();
    private final Random random = new Random();

    public PlanetEffectManager(SkyOrbs plugin) {
        this.plugin = plugin;
        startEffectUpdater();
    }

    public void applyPlanetEffects(Player player, Orb orb) {
        UUID playerId = player.getUniqueId();

        // Remove old effects
        removePlanetEffects(playerId);

        // Apply new effects based on biome and atmosphere
        BiomeType biome = BiomeType.valueOf(orb.getBiomeName());
        applyAtmosphericEffects(player, biome);
        applyAura(player, orb);
        applySoundscape(player, orb);
        applyParticles(player, biome);
        applySkyEffects(player, biome);
    }

    public void removePlanetEffects(UUID playerId) {
        if (activeAuras.containsKey(playerId)) {
            activeAuras.get(playerId).stop();
            activeAuras.remove(playerId);
        }

        if (activeSoundscapes.containsKey(playerId)) {
            activeSoundscapes.get(playerId).stop();
            activeSoundscapes.remove(playerId);
        }
    }

    private void applyAura(Player player, Orb orb) {
        PlanetAura aura = new PlanetAura(player, orb);
        activeAuras.put(player.getUniqueId(), aura);
        aura.start();
    }

    private void applySoundscape(Player player, Orb orb) {
        PlanetSoundscape soundscape = new PlanetSoundscape(player, orb);
        activeSoundscapes.put(player.getUniqueId(), soundscape);
        soundscape.start();
    }

    private void applyAtmosphericEffects(Player player, BiomeType biome) {
        // Apply atmosphere effects based on biome
        switch (biome) {
            case TOXIC_SWAMP, TOXIC -> {
                // Toxic atmosphere
                player.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 100, 1, false, false, false));
                player.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 100, 0, false, false, false));
            }
            case LUMINOUS, GLOWSTONE_CAVERN -> {
                // Glowing atmosphere
                player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 300, 0, false, false, false));
                player.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 300, 0, false, false, false));
            }
            case STORMY -> {
                // Stormy atmosphere
                player.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 80, 1, false, false, false));
            }
            case CORROSIVE -> {
                // Corrosive atmosphere
                player.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 60, 0, false, false, false));
                player.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 100, 2, false, false, false));
            }
            case AURORA -> {
                // Aurora atmosphere
                player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 120, 0, false, false, false));
            }
            case FOGGY -> {
                // Foggy atmosphere
                player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 40, 0, false, false, false));
            }
        }
    }

    private void applyParticles(Player player, BiomeType biome) {
        Location loc = player.getLocation();

        new BukkitRunnable() {
            int ticks = 0;

            @Override
            public void run() {
                if (!player.isOnline() || ticks >= 1200) { // 60 seconds
                    cancel();
                    return;
                }

                // Biome-specific atmospheric particles
                switch (biome) {
                    case TOXIC_SWAMP, TOXIC -> {
                        // Toxic green particles
                        player.getWorld().spawnParticle(Particle.DRIPPING_LAVA,
                            loc.clone().add(0, 1, 0), 3, 0.5, 0.5, 0.5, 0.01);
                    }
                    case LUMINOUS, GLOWSTONE_CAVERN -> {
                        // Glowing particles
                        player.getWorld().spawnParticle(Particle.END_ROD,
                            loc.clone().add(0, 1, 0), 5, 0.5, 0.5, 0.5, 0.01);
                    }
                    case STORMY -> {
                        // Storm particles
                        player.getWorld().spawnParticle(Particle.CLOUD,
                            loc.clone().add(0, 1, 0), 10, 0.5, 0.5, 0.5, 0.1);
                    }
                    case CORROSIVE -> {
                        // Corrosive particles
                        player.getWorld().spawnParticle(Particle.PORTAL,
                            loc.clone().add(0, 1, 0), 3, 0.3, 0.3, 0.3, 0.01);
                    }
                    case AURORA -> {
                        // Aurora particles
                        player.getWorld().spawnParticle(Particle.END_ROD,
                            loc.clone().add(0, 1, 0), 8, 0.5, 0.5, 0.5, 0.02);
                    }
                    case FOGGY -> {
                        // Fog particles
                        player.getWorld().spawnParticle(Particle.WHITE_ASH,
                            loc.clone().add(0, 1, 0), 15, 0.5, 0.5, 0.5, 0.05);
                    }
                    case CRYSTAL_FOREST, CRYSTALLINE -> {
                        // Crystal particles
                        player.getWorld().spawnParticle(Particle.DRAGON_BREATH,
                            loc.clone().add(0, 1, 0), 5, 0.5, 0.5, 0.5, 0.01);
                    }
                    case LAVA_OCEAN, MAGMA_CAVES -> {
                        // Lava particles
                        player.getWorld().spawnParticle(Particle.FLAME,
                            loc.clone().add(0, 0.5, 0), 3, 0.3, 0.3, 0.3, 0.01);
                    }
                    case FROZEN_TUNDRA, ICE_SPIKES, GLACIER -> {
                        // Ice particles
                        player.getWorld().spawnParticle(Particle.SNOWFLAKE,
                            loc.clone().add(0, 1, 0), 10, 0.5, 0.5, 0.5, 0.1);
                    }
                }

                ticks += 20; // Every second
            }
        }.runTaskTimer(plugin, 0L, 20L);
    }

    private void applySkyEffects(Player player, BiomeType biome) {
        // Apply atmospheric sky effects based on biome
        switch (biome) {
            case TOXIC_SWAMP, TOXIC, CORROSIVE -> {
                // Toxic/corrosive atmosphere - fog and weather
                player.setPlayerWeather(WeatherType.DOWNFALL);
                // Fog effect (would need client mods for full effect)
            }
            case STORMY -> {
                // Stormy atmosphere
                player.setPlayerWeather(WeatherType.DOWNFALL);
            }
            case FOGGY -> {
                // Foggy atmosphere
                player.setPlayerWeather(WeatherType.DOWNFALL);
            }
            case LUMINOUS, AURORA -> {
                // Clear glowing atmosphere
                player.setPlayerWeather(WeatherType.CLEAR);
            }
            case LAVA_OCEAN, MAGMA_CAVES -> {
                // Volcanic atmosphere
                player.setPlayerWeather(WeatherType.DOWNFALL);
            }
            case FROZEN_TUNDRA, ICE_SPIKES, GLACIER -> {
                // Icy atmosphere
                player.setPlayerWeather(WeatherType.DOWNFALL);
            }
            default -> {
                // Clear atmosphere for other biomes
                player.setPlayerWeather(WeatherType.CLEAR);
            }
        }
    }

    private void startEffectUpdater() {
        new BukkitRunnable() {
            @Override
            public void run() {
                // Update effects for all online players
                for (Player player : Bukkit.getOnlinePlayers()) {
                    // Check if player is on a planet
                    // This would need integration with player location tracking
                }
            }
        }.runTaskTimer(plugin, 0L, 100L); // Every 5 seconds
    }

    // Inner classes for effect management
    private static class PlanetAura {
        private final Player player;
        private final Orb orb;
        private BukkitRunnable task;

        public PlanetAura(Player player, Orb orb) {
            this.player = player;
            this.orb = orb;
        }

        public void start() {
            task = new BukkitRunnable() {
                @Override
                public void run() {
                    if (!player.isOnline()) {
                        stop();
                        return;
                    }

                    // Apply aura effects based on biome
                    try {
                        BiomeType biome = BiomeType.valueOf(orb.getBiomeName());

                        switch (biome) {
                            case CRYSTAL_FOREST, CRYSTALLINE -> {
                                // Crystal aura - night vision
                                player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 300, 0, false, false, false));
                            }
                            case LUMINOUS, GLOWSTONE_CAVERN -> {
                                // Luminous aura - glowing effect
                                player.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 300, 0, false, false, false));
                            }
                            case AURORA -> {
                                // Aurora aura - regeneration
                                player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 120, 0, false, false, false));
                            }
                        }
                    } catch (IllegalArgumentException e) {
                        // Invalid biome name, skip aura effects
                    }
                }
            };
            task.runTaskTimer(SkyOrbs.getInstance(), 0L, 200L); // Every 10 seconds
        }

        public void stop() {
            if (task != null) {
                task.cancel();
            }
        }
    }

    private static class PlanetSoundscape {
        private final Player player;
        private final Orb orb;
        private BukkitRunnable task;

        public PlanetSoundscape(Player player, Orb orb) {
            this.player = player;
            this.orb = orb;
        }

        public void start() {
            task = new BukkitRunnable() {
                @Override
                public void run() {
                    if (!player.isOnline()) {
                        stop();
                        return;
                    }

                    // Play ambient sounds based on biome
                    BiomeType biome;
                    try {
                        biome = BiomeType.valueOf(orb.getBiomeName());
                    } catch (IllegalArgumentException e) {
                        return; // Invalid biome, skip sound
                    }
                    Location loc = player.getLocation();

                    Sound sound = getAmbientSound(biome);
                    if (sound != null) {
                        player.playSound(loc, sound, SoundCategory.AMBIENT, 0.3f, 1.0f);
                    }
                }
            };
            task.runTaskTimer(SkyOrbs.getInstance(), 0L, 400L); // Every 20 seconds
        }

        public void stop() {
            if (task != null) {
                task.cancel();
            }
        }

        private Sound getAmbientSound(BiomeType biome) {
            return switch (biome) {
                case CRYSTAL_FOREST, CRYSTALLINE -> Sound.BLOCK_AMETHYST_BLOCK_CHIME;
                case LAVA_OCEAN, MAGMA_CAVES -> Sound.BLOCK_LAVA_AMBIENT;
                case FROZEN_TUNDRA, ICE_SPIKES, GLACIER -> Sound.BLOCK_SNOW_BREAK;
                case TOXIC_SWAMP, TOXIC -> Sound.BLOCK_SLIME_BLOCK_BREAK;
                case CORRUPTED -> Sound.AMBIENT_NETHER_WASTES_LOOP;
                case GLOWSTONE_CAVERN, LUMINOUS -> Sound.BLOCK_AMETHYST_BLOCK_RESONATE;
                case AURORA -> Sound.BLOCK_BELL_RESONATE;
                case STORMY -> Sound.WEATHER_RAIN;
                case FOGGY -> Sound.AMBIENT_NETHER_WASTES_LOOP;
                case CORROSIVE -> Sound.BLOCK_LAVA_POP;
                case CHORUS_LAND -> Sound.BLOCK_CHORUS_FLOWER_GROW;
                default -> null;
            };
        }
    }
}