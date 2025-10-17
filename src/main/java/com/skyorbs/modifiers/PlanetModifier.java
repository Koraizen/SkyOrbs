package com.skyorbs.modifiers;

/**
 * Planet modifiers that create diversity
 * Applied during generation to modify planet structure
 */
public enum PlanetModifier {
    
    HOLLOW("Hollow", "İçi Boş", 0.4),           // Empty interior with shell
    ORE_RICH("Ore Rich", "Maden Zengin", 0.3),   // 2x ore density
    MAGMA_CORE("Magma Core", "Magma Çekirdek", 0.2),  // Hot glowing core
    ICY_CORE("Icy Core", "Buz Çekirdek", 0.15),       // Frozen core
    LAYERED("Layered", "Katmanlı", 0.25),       // Multiple material layers
    ANTI_GRAVITY("Anti-Gravity", "Yer Çekimsiz", 0.1), // Floating zones
    DENSE("Dense", "Yoğun", 0.2),               // Extra solid, more blocks
    POROUS("Porous", "Gözenekli", 0.15),        // Many caves and tunnels
    RINGED("Ringed", "Halkalı", 0.2),           // Has ring system
    RADIOACTIVE("Radioactive", "Radyoaktif", 0.05); // Dangerous radiation
    
    private final String name;
    private final String displayName;
    private final double probability;
    
    PlanetModifier(String name, String displayName, double probability) {
        this.name = name;
        this.displayName = displayName;
        this.probability = probability;
    }
    
    public String getName() {
        return name;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public double getProbability() {
        return probability;
    }
}
