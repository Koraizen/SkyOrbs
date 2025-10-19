package com.skyorbs.shapes.impl;

import com.skyorbs.shapes.PlanetShape;

public class SnowflakeShape implements PlanetShape {

    @Override
    public boolean isBlockPart(int dx, int dy, int dz, int radius, long seed) {
        // Kar tanesi şekli - 6 kollu yıldız
        double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
        if (distance > radius) return false;

        // Normalize coordinates
        double x = Math.abs(dx) / (double)radius;
        double y = Math.abs(dy) / (double)radius;
        double z = Math.abs(dz) / (double)radius;

        // Snowflake formülü - 6 kollu simetri
        double angle = Math.atan2(z, x);
        double sector = angle / (Math.PI / 3); // 6 sektör
        double sectorAngle = (sector - Math.floor(sector)) * (Math.PI / 3);

        // Ana kollar ve yan dallar
        double mainBranch = Math.cos(sectorAngle * 6);
        double sideBranch = Math.cos(sectorAngle * 12) * 0.3;

        double snowflake = mainBranch + sideBranch;
        return Math.sqrt(x*x + z*z) <= snowflake * 0.5 + 0.2;
    }

    @Override
    public String getName() {
        return "SNOWFLAKE";
    }

    @Override
    public double getDeformationFactor() {
        return 0.4;
    }

    @Override
    public String getDisplayName() {
        return "Kar Tanesi";
    }
}