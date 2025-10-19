package com.skyorbs.generation;

import com.skyorbs.SkyOrbs;

import java.util.Random;

public class PlanetSizeCalculator {
    
    private final SkyOrbs plugin;
    private final Random random = new Random();
    
    public PlanetSizeCalculator(SkyOrbs plugin) {
        this.plugin = plugin;
    }
    
    public int calculateRadius(String sizeCategory) {
        // CONFIG'DEN OKU - Tamamen config kontrollü
        boolean sizeEnabled = plugin.getConfig().getBoolean("generation.planetSize.enabled", true);
        if (!sizeEnabled) {
            // Eski sistem fallback
            int minRadius = plugin.getConfigManager().getMinRadius();
            int maxRadius = plugin.getConfigManager().getMaxRadius();
            int worldMaxHeight = 320;
            int safeMaxRadius = Math.min(maxRadius, worldMaxHeight / 2 - 20);
            return switch (sizeCategory.toUpperCase()) {
                case "SMALL" -> Math.min(minRadius + random.nextInt(40), safeMaxRadius);
                case "MEDIUM" -> Math.min(120 + random.nextInt(60), safeMaxRadius);
                case "LARGE" -> Math.min(180 + random.nextInt(70), safeMaxRadius);
                default -> Math.min(120 + random.nextInt(60), safeMaxRadius);
            };
        }

        // YENİ CONFIG TABANLI SİSTEM
        int minRadius = plugin.getConfig().getInt("generation.planetSize.minRadius", 5);
        int maxRadius = plugin.getConfig().getInt("generation.planetSize.maxRadius", 250);
        int worldHeightLimit = plugin.getConfig().getInt("generation.planetSize.worldHeightLimit", 320);
        int safeBuffer = plugin.getConfig().getInt("generation.planetSize.safeBuffer", 20);
        int safeMaxRadius = Math.min(maxRadius, worldHeightLimit / 2 - safeBuffer);

        return switch (sizeCategory.toUpperCase()) {
            case "SMALL" -> {
                int tinyMin = plugin.getConfig().getInt("generation.planetSize.sizeRanges.tiny.min", 5);
                int tinyMax = plugin.getConfig().getInt("generation.planetSize.sizeRanges.tiny.max", 15);
                yield Math.min(tinyMin + random.nextInt(tinyMax - tinyMin + 1), safeMaxRadius);
            }
            case "MEDIUM" -> {
                int mediumMin = plugin.getConfig().getInt("generation.planetSize.sizeRanges.medium.min", 50);
                int mediumMax = plugin.getConfig().getInt("generation.planetSize.sizeRanges.medium.max", 120);
                yield Math.min(mediumMin + random.nextInt(mediumMax - mediumMin + 1), safeMaxRadius);
            }
            case "LARGE" -> {
                int largeMin = plugin.getConfig().getInt("generation.planetSize.sizeRanges.large.min", 120);
                int largeMax = plugin.getConfig().getInt("generation.planetSize.sizeRanges.large.max", 200);
                yield Math.min(largeMin + random.nextInt(largeMax - largeMin + 1), safeMaxRadius);
            }
            default -> {
                // TAMAMEN RASTGELE BOYUTLAR - Config kontrollü dağılım
                double rand = random.nextDouble();

                double tinyProb = plugin.getConfig().getDouble("generation.planetSize.sizeDistribution.tiny", 0.05);
                double smallProb = plugin.getConfig().getDouble("generation.planetSize.sizeDistribution.small", 0.10);
                double mediumProb = plugin.getConfig().getDouble("generation.planetSize.sizeDistribution.medium", 0.20);
                double largeProb = plugin.getConfig().getDouble("generation.planetSize.sizeDistribution.large", 0.30);

                if (rand < tinyProb) {
                    // Çok küçük gezegenler
                    int tinyMin = plugin.getConfig().getInt("generation.planetSize.sizeRanges.tiny.min", 5);
                    int tinyMax = plugin.getConfig().getInt("generation.planetSize.sizeRanges.tiny.max", 15);
                    yield Math.max(minRadius, tinyMin + random.nextInt(tinyMax - tinyMin + 1));
                } else if (rand < tinyProb + smallProb) {
                    // Küçük gezegenler
                    int smallMin = plugin.getConfig().getInt("generation.planetSize.sizeRanges.small.min", 15);
                    int smallMax = plugin.getConfig().getInt("generation.planetSize.sizeRanges.small.max", 50);
                    yield Math.min(smallMin + random.nextInt(smallMax - smallMin + 1), safeMaxRadius);
                } else if (rand < tinyProb + smallProb + mediumProb) {
                    // Orta gezegenler
                    int mediumMin = plugin.getConfig().getInt("generation.planetSize.sizeRanges.medium.min", 50);
                    int mediumMax = plugin.getConfig().getInt("generation.planetSize.sizeRanges.medium.max", 120);
                    yield Math.min(mediumMin + random.nextInt(mediumMax - mediumMin + 1), safeMaxRadius);
                } else if (rand < tinyProb + smallProb + mediumProb + largeProb) {
                    // Büyük gezegenler
                    int largeMin = plugin.getConfig().getInt("generation.planetSize.sizeRanges.large.min", 120);
                    int largeMax = plugin.getConfig().getInt("generation.planetSize.sizeRanges.large.max", 200);
                    yield Math.min(largeMin + random.nextInt(largeMax - largeMin + 1), safeMaxRadius);
                } else {
                    // Çok büyük gezegenler
                    int hugeMin = plugin.getConfig().getInt("generation.planetSize.sizeRanges.huge.min", 200);
                    int hugeMax = plugin.getConfig().getInt("generation.planetSize.sizeRanges.huge.max", 250);
                    yield Math.min(hugeMin + random.nextInt(hugeMax - hugeMin + 1), safeMaxRadius);
                }
            }
        };
    }
    
    public long estimateBlockCount(int radius) {
        int shellThickness = 7;
        double outerVolume = (4.0 / 3.0) * Math.PI * Math.pow(radius, 3);
        double innerVolume = (4.0 / 3.0) * Math.PI * Math.pow(radius - shellThickness, 3);
        return (long)((outerVolume - innerVolume) * 1.1);
    }
    
    public boolean validateSize(int radius) {
        int minRadius = plugin.getConfigManager().getMinRadius();
        int maxRadius = plugin.getConfigManager().getMaxRadius();
        return radius >= minRadius && radius <= maxRadius;
    }
}
