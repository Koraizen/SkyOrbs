package com.skyorbs.utils;

import org.bukkit.Particle;
import org.bukkit.Sound;

/**
 * Global constants for SkyOrbs plugin
 * Prevents magic numbers and centralizes configuration
 */
public class Constants {
    
    // ============================================
    // PLANET SIZE CONSTANTS
    // ============================================
    public static final int MIN_PLANET_RADIUS = 15;
    public static final int MAX_PLANET_RADIUS = 100;
    public static final int DEFAULT_PLANET_RADIUS = 35;
    
    // ============================================
    // GENERATION CONSTANTS
    // ============================================
    public static final double ORE_SCALE_FACTOR = 0.001;
    public static final int MIN_ORE_VEIN_COUNT = 5;
    public static final double HOLLOW_INNER_RADIUS_RATIO = 0.6;
    public static final int DEFAULT_SHELL_THICKNESS = 5;
    
    // ============================================
    // PERFORMANCE CONSTANTS
    // ============================================
    public static final int DEFAULT_BLOCKS_PER_BATCH = 1000;
    public static final int DEFAULT_BATCHES_PER_TICK = 4;
    public static final int MAX_CONCURRENT_GENERATIONS = 2;
    public static final double TPS_THRESHOLD = 18.0;
    
    // ============================================
    // SOUND EFFECTS
    // ============================================
    public static final Sound SOUND_PLANET_CREATE = Sound.ENTITY_ENDER_DRAGON_GROWL;
    public static final Sound SOUND_TELEPORT = Sound.ENTITY_ENDERMAN_TELEPORT;
    public static final Sound SOUND_SUCCESS = Sound.ENTITY_PLAYER_LEVELUP;
    public static final Sound SOUND_ERROR = Sound.ENTITY_VILLAGER_NO;
    public static final Sound SOUND_PROGRESS = Sound.BLOCK_NOTE_BLOCK_PLING;
    
    // Sound volumes and pitches
    public static final float SOUND_VOLUME_NORMAL = 1.0f;
    public static final float SOUND_VOLUME_QUIET = 0.5f;
    public static final float SOUND_PITCH_NORMAL = 1.0f;
    public static final float SOUND_PITCH_HIGH = 1.5f;
    public static final float SOUND_PITCH_LOW = 0.8f;
    
    // ============================================
    // PARTICLE EFFECTS
    // ============================================
    public static final Particle PARTICLE_PLANET_CREATE = Particle.PORTAL;
    public static final Particle PARTICLE_TELEPORT = Particle.DRAGON_BREATH;
    public static final Particle PARTICLE_SUCCESS = Particle.TOTEM_OF_UNDYING;
    public static final Particle PARTICLE_PROGRESS = Particle.END_ROD;
    
    // Particle counts
    public static final int PARTICLE_COUNT_LARGE = 100;
    public static final int PARTICLE_COUNT_MEDIUM = 50;
    public static final int PARTICLE_COUNT_SMALL = 20;
    
    // ============================================
    // MESSAGE SYMBOLS
    // ============================================
    public static final String SYMBOL_SUCCESS = "✓";
    public static final String SYMBOL_ERROR = "✗";
    public static final String SYMBOL_INFO = "!";
    public static final String SYMBOL_WARNING = "⚠";
    public static final String SYMBOL_ARROW = "→";
    public static final String SYMBOL_BULLET = "•";
    
    // Progress bar characters
    public static final String PROGRESS_FILLED = "█";
    public static final String PROGRESS_EMPTY = "░";
    public static final int PROGRESS_BAR_LENGTH = 20;
    
    // ============================================
    // COLOR CODES
    // ============================================
    public static final String COLOR_SUCCESS = "§a";
    public static final String COLOR_ERROR = "§c";
    public static final String COLOR_INFO = "§e";
    public static final String COLOR_WARNING = "§6";
    public static final String COLOR_PRIMARY = "§b";
    public static final String COLOR_SECONDARY = "§7";
    public static final String COLOR_ACCENT = "§d";
    public static final String COLOR_RESET = "§r";
    public static final String COLOR_BOLD = "§l";
    
    // ============================================
    // TIMING CONSTANTS
    // ============================================
    public static final int TICKS_PER_SECOND = 20;
    public static final int TELEPORT_DELAY_TICKS = 5;
    public static final int GENERATION_TIMEOUT_SECONDS = 300; // 5 minutes
    
    // ============================================
    // VALIDATION CONSTANTS
    // ============================================
    public static final int MAX_PLANET_NAME_LENGTH = 32;
    public static final int MIN_PLANET_NAME_LENGTH = 3;
    public static final String PLANET_NAME_PATTERN = "^[a-zA-Z0-9_-]+$";
    
    // Private constructor to prevent instantiation
    private Constants() {
        throw new AssertionError("Cannot instantiate Constants class");
    }
}
