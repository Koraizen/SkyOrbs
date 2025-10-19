package com.skyorbs.modifiers;

/**
 * Planet modifiers that create diversity
 * Applied during generation to modify planet structure
 */
public enum PlanetModifier {
    
    HOLLOW("Hollow", "İçi Boş", 0.1),           // Empty interior with shell
    ORE_RICH("Ore Rich", "Maden Zengin", 0.3),   // 2x ore density
    MAGMA_CORE("Magma Core", "Magma Çekirdek", 0.2),  // Hot glowing core
    ICY_CORE("Icy Core", "Buz Çekirdek", 0.15),       // Frozen core
    LAYERED("Layered", "Katmanlı", 0.25),       // Multiple material layers
    ANTI_GRAVITY("Anti-Gravity", "Yer Çekimsiz", 0.1), // Floating zones
    DENSE("Dense", "Yoğun", 0.2),               // Extra solid, more blocks
    POROUS("Porous", "Gözenekli", 0.15),        // Many caves and tunnels
    RINGED("Ringed", "Halkalı", 0.2),           // Has ring system
    RADIOACTIVE("Radioactive", "Radyoaktif", 0.05), // Dangerous radiation
    CRYSTALLIZED("Crystallized", "Kristalleşmiş", 0.25), // Crystal formations
    VEGETATED("Vegetated", "Bitkili", 0.3),     // Extra vegetation
    MINERALIZED("Mineralized", "Mineralleşmiş", 0.2), // Rich minerals
    ERODED("Eroded", "Aşınmış", 0.15),          // Weathered surface
    VOLCANIC("Volcanic", "Volkanik", 0.2),     // Active volcanoes
    AQUATIC("Aquatic", "Su", 0.25),            // Water features
    STORMY("Stormy", "Fırtınalı", 0.1),        // Storm effects
    MAGNETIC("Magnetic", "Manyetik", 0.15),    // Magnetic anomalies
    BIO_LUMinescent("Bio-Luminescent", "Biyolüminesan", 0.1), // Glowing life
    FROZEN("Frozen", "Donmuş", 0.2),           // Ice formations
    SANDY("Sandy", "Kumlu", 0.25),             // Desert-like
    FORESTED("Forested", "Ormansız", 0.3),     // Dense forests
    CAVERN("Cavern", "Mağara", 0.2),           // Cave systems
    METEORIC("Meteoric", "Meteorik", 0.1),     // Meteor impacts
    GEYSER("Geyser", "Geyser", 0.15),          // Hot springs
    TIDAL("Tidal", "Gelgit", 0.2),             // Ocean tides
    WINDY("Windy", "Rüzgarlı", 0.25),          // Wind effects
    SHADOWY("Shadowy", "Gölgeli", 0.1),        // Dark areas
    MIRROR("Mirror", "Ayna", 0.05),            // Reflective surface
    PHASE("Phase", "Faz", 0.1),                // Phase-shifting
    QUANTUM("Quantum", "Kuantum", 0.05),       // Quantum effects
    TEMPORAL("Temporal", "Zamansal", 0.05),    // Time anomalies
    VOID("Void", "Boşluk", 0.1),               // Void energies
    CELESTIAL("Celestial", "Göksel", 0.15),    // Star-like
    NEBULOUS("Nebulous", "Sisli", 0.2),        // Gas clouds
    PLASMA("Plasma", "Plazma", 0.1),           // Plasma storms
    GRAVITATIONAL("Gravitational", "Yerçekimi", 0.15), // Gravity wells
    ELECTRIC("Electric", "Elektrik", 0.2),     // Electric fields
    SONIC("Sonic", "Ses", 0.1),                // Sound waves
    LIGHTNING("Lightning", "Yıldırım", 0.15),  // Lightning storms
    AURORA("Aurora", "Aurora", 0.2),           // Aurora effects
    COSMIC("Cosmic", "Kozmik", 0.1),           // Cosmic energies
    DIMENSIONAL("Dimensional", "Boyutsal", 0.05), // Dimension rifts
    ETHEREAL("Ethereal", "Eterik", 0.1),       // Ethereal mists
    PRIMAL("Primal", "İlkel", 0.2),            // Raw elemental power
    SYMBIOTIC("Symbiotic", "Sembiyotik", 0.15), // Life symbiosis
    MUTAGENIC("Mutagenic", "Mutajenik", 0.1),  // Genetic changes
    RESONANT("Resonant", "Rezonans", 0.2),     // Energy resonance
    HARMONIC("Harmonic", "Harmonik", 0.25),   // Harmonic frequencies
    CHAOTIC("Chaotic", "Kaotik", 0.15),        // Chaotic energies
    ORDERED("Ordered", "Düzenli", 0.2),        // Ordered structures
    ENTROPIC("Entropic", "Entropik", 0.1),     // Entropy effects
    SYNERGISTIC("Synergistic", "Sinerjik", 0.2), // Synergy effects
    CATALYTIC("Catalytic", "Katalitik", 0.15), // Catalyst reactions
    ALCHEMICAL("Alchemical", "Simya", 0.1),    // Alchemical transformations
    MYSTICAL("Mystical", "Mistik", 0.15),      // Mystical energies
    ARCANE("Arcane", "Gizemli", 0.1),          // Arcane magic
    DIVINE("Divine", "İlahî", 0.05),           // Divine presence
    INFERNAL("Infernal", "Cehennemî", 0.1),   // Infernal powers
    CELESTIAL_BLESSED("Celestial Blessed", "Göksel Bereketli", 0.05), // Celestial blessing
    VOID_BORN("Void Born", "Boşluk Doğumlu", 0.1), // Born from void
    STAR_FORGED("Star Forged", "Yıldız Dövülmüş", 0.15), // Forged in stars
    NEBULA_INFUSED("Nebula Infused", "Sis Enjekte", 0.2), // Nebula energies
    GALACTIC("Galactic", "Galaktik", 0.1),     // Galactic powers
    UNIVERSE_BOUND("Universe Bound", "Evren Bağlı", 0.05); // Universe connection
    
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
