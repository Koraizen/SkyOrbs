package com.skyorbs.shapes.impl;

import com.skyorbs.shapes.PlanetShape;
import com.skyorbs.SkyOrbs;

public class ButterflyShape implements PlanetShape {

    @Override
    public boolean isBlockPart(int dx, int dy, int dz, int radius, long seed) {
        // Kelebek şekli - simetrik kanatlar (CONFIG KONTROLLÜ)
        double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
        if (distance > radius) return false;

        // CONFIG'DEN ALGORİTMA AYARLARINI OKU
        SkyOrbs plugin = SkyOrbs.getInstance();
        int wingSegments = plugin.getConfig().getInt("generation.shapes.algorithmTweaks.butterfly.wingSegments", 3);
        double bodyThickness = plugin.getConfig().getDouble("generation.shapes.algorithmTweaks.butterfly.bodyThickness", 0.15);
        double wingFactor = plugin.getConfig().getDouble("generation.shapes.algorithmTweaks.butterfly.wingFactor", 0.6);
        boolean symmetry = plugin.getConfig().getBoolean("generation.shapes.algorithmTweaks.butterfly.symmetry", true);

        // Normalize coordinates
        double x = Math.abs(dx) / (double)radius;
        double y = dy / (double)radius;
        double z = dz / (double)radius;

        // Kelebek kanatları - config kontrollü segment sayısı
        double wingPattern = Math.exp(-Math.pow(x, 2) / 0.3) * Math.sin(wingSegments * Math.PI * x);
        double bodyFactor = Math.exp(-Math.pow(x, 2) / 0.1);

        // Y koordinatı için kanat eğimi (config kontrollü)
        double expectedY = wingPattern * wingFactor;

        // Z koordinatı için kalınlık (config kontrollü)
        double thickness = bodyThickness + bodyFactor * 0.1;

        // Config kontrollü kontrol
        return Math.abs(z) <= thickness && Math.abs(y - expectedY) <= 0.3;
    }

    @Override
    public String getName() {
        return "BUTTERFLY";
    }

    @Override
    public double getDeformationFactor() {
        return 0.6;
    }

    @Override
    public String getDisplayName() {
        return "Kelebek";
    }
}