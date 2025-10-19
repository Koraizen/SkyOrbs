package com.skyorbs.shapes.impl;

import com.skyorbs.shapes.PlanetShape;
import com.skyorbs.SkyOrbs;

public class RainbowShape implements PlanetShape {

    @Override
    public boolean isBlockPart(int dx, int dy, int dz, int radius, long seed) {
        // Gökkuşağı şekli - yay şeklinde (CONFIG KONTROLLÜ)
        double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
        if (distance > radius) return false;

        // CONFIG'DEN ALGORİTMA AYARLARINI OKU
        SkyOrbs plugin = SkyOrbs.getInstance();
        double arcAngle = plugin.getConfig().getDouble("generation.shapes.algorithmTweaks.rainbow.arcAngle", 120.0);
        double innerRadius = plugin.getConfig().getDouble("generation.shapes.algorithmTweaks.rainbow.innerRadius", 0.4);
        double outerRadius = plugin.getConfig().getDouble("generation.shapes.algorithmTweaks.rainbow.outerRadius", 0.9);
        double thickness = plugin.getConfig().getDouble("generation.shapes.algorithmTweaks.rainbow.thickness", 0.15);
        double heightVariation = plugin.getConfig().getDouble("generation.shapes.algorithmTweaks.rainbow.heightVariation", 0.4);

        // Normalize coordinates
        double x = dx / (double)radius;
        double y = dy / (double)radius;
        double z = dz / (double)radius;

        // 2D yay kontrolü (XZ düzleminde)
        double angle = Math.atan2(z, x);
        double arcRadius = Math.sqrt(x * x + z * z);

        // Gökkuşağı yayı - config kontrollü açı
        double maxAngle = Math.toRadians(arcAngle / 2);
        if (angle < -maxAngle || angle > maxAngle) return false;

        // Y koordinatı için eğim - yay şeklinde kıvrım (config kontrollü)
        double arcHeight = Math.sin(angle) * heightVariation;
        double expectedY = arcHeight;

        // Config kontrollü kontrol
        return arcRadius >= innerRadius && arcRadius <= outerRadius &&
               Math.abs(y - expectedY) <= thickness;
    }

    @Override
    public String getName() {
        return "RAINBOW";
    }

    @Override
    public double getDeformationFactor() {
        return 0.5;
    }

    @Override
    public String getDisplayName() {
        return "Gökkuşağı";
    }
}