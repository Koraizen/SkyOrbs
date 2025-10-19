package com.skyorbs.shapes.impl;

import com.skyorbs.shapes.PlanetShape;
import com.skyorbs.SkyOrbs;

public class MoonShape implements PlanetShape {

    @Override
    public boolean isBlockPart(int dx, int dy, int dz, int radius, long seed) {
        // Ay şekli - kraterli yüzey (CONFIG KONTROLLÜ)
        double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
        if (distance > radius) return false;

        // CONFIG'DEN ALGORİTMA AYARLARINI OKU
        SkyOrbs plugin = SkyOrbs.getInstance();
        int craterCount = plugin.getConfig().getInt("generation.shapes.algorithmTweaks.moon.craterCount", 8);
        double craterDepth = plugin.getConfig().getDouble("generation.shapes.algorithmTweaks.moon.craterDepth", 0.3);
        double surfaceVariation = plugin.getConfig().getDouble("generation.shapes.algorithmTweaks.moon.surfaceVariation", 0.2);
        boolean flatTop = plugin.getConfig().getBoolean("generation.shapes.algorithmTweaks.moon.flatTop", true);

        // Normalize coordinates
        double x = dx / (double)radius;
        double y = dy / (double)radius;
        double z = dz / (double)radius;

        // Temel küre şekli
        double sphereRadius = Math.sqrt(x * x + y * y + z * z);

        // Krater etkisi - config kontrollü krater sayısı
        double craterEffect = 0.0;
        for (int i = 0; i < craterCount; i++) {
            double craterX = Math.sin(seed * 0.1 + i) * 0.7;
            double craterZ = Math.cos(seed * 0.1 + i) * 0.7;
            double craterDist = Math.sqrt((x - craterX) * (x - craterX) + (z - craterZ) * (z - craterZ));
            if (craterDist < 0.3) {
                craterEffect = Math.max(craterEffect, craterDepth * (1.0 - craterDist / 0.3));
            }
        }

        // Ay yüzeyi - config kontrollü düzleştirme
        if (flatTop && y > surfaceVariation) {
            return sphereRadius <= 1.0 - craterEffect;
        } else {
            return sphereRadius <= 1.0;
        }
    }

    @Override
    public String getName() {
        return "MOON";
    }

    @Override
    public double getDeformationFactor() {
        return 0.2;
    }

    @Override
    public String getDisplayName() {
        return "Ay";
    }
}