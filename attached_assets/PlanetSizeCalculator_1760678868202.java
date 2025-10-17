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
        int minRadius = plugin.getConfigManager().getMinRadius();
        int maxRadius = plugin.getConfigManager().getMaxRadius();
        
        return switch (sizeCategory.toUpperCase()) {
            case "SMALL" -> minRadius + random.nextInt(40);
            case "MEDIUM" -> 120 + random.nextInt(60);
            case "LARGE" -> 180 + random.nextInt(70);
            default -> {
                double rand = random.nextDouble();
                if (rand < 0.4) {
                    yield minRadius + random.nextInt(40);
                } else if (rand < 0.8) {
                    yield 120 + random.nextInt(60);
                } else {
                    yield 180 + random.nextInt(70);
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
    
    public String getSizeCategory(int radius) {
        if (radius < 120) return "SMALL";
        if (radius < 180) return "MEDIUM";
        return "LARGE";
    }
}
