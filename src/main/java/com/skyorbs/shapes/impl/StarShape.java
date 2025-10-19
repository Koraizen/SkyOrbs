package com.skyorbs.shapes.impl;

import com.skyorbs.shapes.PlanetShape;
import com.skyorbs.SkyOrbs;

public class StarShape implements PlanetShape {

    @Override
    public boolean isBlockPart(int dx, int dy, int dz, int radius, long seed) {
        // Yıldız şekli - 5 kollu yıldız (CONFIG KONTROLLÜ)
        double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
        if (distance > radius) return false;

        // CONFIG'DEN ALGORİTMA AYARLARINI OKU
        SkyOrbs plugin = SkyOrbs.getInstance();
        int points = plugin.getConfig().getInt("generation.shapes.algorithmTweaks.star.points", 5);
        double innerRadius = plugin.getConfig().getDouble("generation.shapes.algorithmTweaks.star.innerRadius", 0.4);
        double outerRadius = plugin.getConfig().getDouble("generation.shapes.algorithmTweaks.star.outerRadius", 1.0);
        double heightFactor = plugin.getConfig().getDouble("generation.shapes.algorithmTweaks.star.heightFactor", 0.6);
        boolean centerSolid = plugin.getConfig().getBoolean("generation.shapes.algorithmTweaks.star.centerSolid", true);

        // Normalize coordinates
        double x = dx / (double)radius;
        double y = dy / (double)radius;
        double z = dz / (double)radius;

        // 2D yıldız kontrolü (XZ düzleminde)
        double angle = Math.atan2(z, x);
        double starRadius = Math.sqrt(x * x + z * z);

        // Yıldız formülü: r = a / (1 + b * sin(nθ))
        double a = outerRadius;
        double b = innerRadius;
        double expectedRadius = a / (1 + b * Math.sin(points * angle));

        // Y koordinatı için yükseklik kontrolü
        double thickness = 0.3;

        // Yıldız şekli kontrolü - config kontrollü
        boolean inStar = starRadius <= expectedRadius && Math.abs(y) <= heightFactor;

        // Yıldız merkezi için ekstra kontrol (config kontrollü)
        boolean inCenter = false;
        if (centerSolid) {
            double centerRadius = Math.sqrt(x * x + y * y + z * z);
            inCenter = centerRadius <= 0.4;
        }

        return inStar || inCenter;
    }

    @Override
    public String getName() {
        return "STAR";
    }

    @Override
    public double getDeformationFactor() {
        return 0.4;
    }

    @Override
    public String getDisplayName() {
        return "Yıldız";
    }
}