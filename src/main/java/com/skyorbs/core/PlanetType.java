package com.skyorbs.core;

import org.bukkit.Material;
import org.bukkit.Color;

public enum PlanetType {

    TERRESTRIAL("Karasal", Material.GRASS_BLOCK, Color.GREEN, "Dünya benzeri, yaşam dolu gezegen"),
    GAS("Gaz Devi", Material.BLUE_WOOL, Color.BLUE, "Gazdan oluşan dev gezegen"),
    LAVA("Lav", Material.MAGMA_BLOCK, Color.ORANGE, "Lav okyanusları ve volkanik aktivite"),
    ICE("Buz", Material.PACKED_ICE, Color.AQUA, "Donmuş yüzey ve buz fırtınaları"),
    CRYSTAL("Kristal", Material.AMETHYST_BLOCK, Color.PURPLE, "Kristal oluşumları ve mistik enerji"),
    SHADOW("Gölge", Material.BLACK_CONCRETE, Color.BLACK, "Karanlık ve mistik güçler"),
    TOXIC("Zehirli", Material.SLIME_BLOCK, Color.LIME, "Zehirli atmosfer ve tehlikeli flora");

    private final String displayName;
    private final Material iconMaterial;
    private final Color themeColor;
    private final String description;

    PlanetType(String displayName, Material iconMaterial, Color themeColor, String description) {
        this.displayName = displayName;
        this.iconMaterial = iconMaterial;
        this.themeColor = themeColor;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public Material getIconMaterial() {
        return iconMaterial;
    }

    public Color getThemeColor() {
        return themeColor;
    }

    public String getDescription() {
        return description;
    }

    public double getEnergyMultiplier() {
        return switch (this) {
            case TERRESTRIAL -> 1.0;
            case GAS -> 0.8;
            case LAVA -> 1.2;
            case ICE -> 0.9;
            case CRYSTAL -> 1.5;
            case SHADOW -> 1.3;
            case TOXIC -> 1.1;
        };
    }

    public int getMaxBiosphereLevel() {
        return switch (this) {
            case TERRESTRIAL -> 5;
            case GAS -> 1;
            case LAVA -> 2;
            case ICE -> 3;
            case CRYSTAL -> 4;
            case SHADOW -> 3;
            case TOXIC -> 4;
        };
    }

    public boolean supportsLife() {
        return this != GAS;
    }

    public boolean hasAtmosphere() {
        return true; // All planets have atmosphere in this system
    }
}