package com.skyorbs.core;

import java.util.HashMap;
import java.util.Map;

public class PlanetEvolution {

    private final Orb orb;

    public PlanetEvolution(Orb orb) {
        this.orb = orb;
    }

    public void evolve() {
        if (!orb.canUpgrade()) {
            return;
        }

        int oldLevel = orb.getCoreLevel();
        orb.setCoreLevel(oldLevel + 1);

        // Apply evolution effects
        applyEvolutionEffects(oldLevel, orb.getCoreLevel());

        // Reset XP for next level
        orb.addXp(-orb.getXpForNextLevel());
    }

    private void applyEvolutionEffects(int oldLevel, int newLevel) {
        // Size increase
        if (newLevel >= 3) {
            // Planet grows larger
        }

        // New features unlock
        if (newLevel >= 2) {
            // Unlock atmosphere effects
        }

        if (newLevel >= 4) {
            // Unlock advanced biomes
        }

        if (newLevel >= 6) {
            // Unlock special events
        }

        if (newLevel >= 8) {
            // Unlock legendary features
        }

        if (newLevel >= 10) {
            // Maximum evolution - special title
        }
    }

    public Map<String, Object> getEvolutionRequirements() {
        Map<String, Object> requirements = new HashMap<>();
        requirements.put("currentLevel", orb.getCoreLevel());
        requirements.put("currentXp", orb.getXp());
        requirements.put("xpForNextLevel", orb.getXpForNextLevel());
        requirements.put("canUpgrade", orb.canUpgrade());
        requirements.put("progressPercent", (double) orb.getXp() / orb.getXpForNextLevel() * 100);
        return requirements;
    }

    public String getEvolutionDescription() {
        int level = orb.getCoreLevel();
        return switch (level) {
            case 1 -> "Temel çekirdek - yaşamın başlangıcı";
            case 2 -> "Gelişen çekirdek - atmosfer oluşuyor";
            case 3 -> "Olgun çekirdek - biyom çeşitliliği";
            case 4 -> "Güçlü çekirdek - özel yetenekler";
            case 5 -> "İleri çekirdek - ekolojik denge";
            case 6 -> "Gelişmiş çekirdek - olay sistemi";
            case 7 -> "Üstün çekirdek - nadir kaynaklar";
            case 8 -> "Efsanevi çekirdek - mistik güçler";
            case 9 -> "Ultimate çekirdek - maksimum potansiyel";
            case 10 -> "Kozmik çekirdek - evrenin gücü";
            default -> "Bilinmeyen evrim seviyesi";
        };
    }

    public void gainXpFromActivity(String activity, int baseXp) {
        double multiplier = getXpMultiplier();
        int finalXp = (int) (baseXp * multiplier);
        orb.addXp(finalXp);
    }

    private double getXpMultiplier() {
        double multiplier = 1.0;

        // Planet type bonus
        multiplier *= orb.getPlanetType().getEnergyMultiplier();

        // Biosphere level bonus
        multiplier *= (1.0 + (orb.getBiosphereLevel() - 1) * 0.1);

        // Ecological balance bonus
        if (orb.getEcologicalBalance() > 1.0) {
            multiplier *= 1.2;
        } else if (orb.getEcologicalBalance() < 0.8) {
            multiplier *= 0.8;
        }

        return multiplier;
    }
}