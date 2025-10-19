package com.skyorbs.shapes.impl;

import com.skyorbs.shapes.PlanetShape;

public class HelicopterShape implements PlanetShape {

    @Override
    public boolean isBlockPart(int dx, int dy, int dz, int radius, long seed) {
        // Helikopter şekli - fuselage, rotors, tail
        double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
        if (distance > radius) return false;

        // Normalize coordinates
        double x = dx / (double)radius;
        double y = dy / (double)radius;
        double z = dz / (double)radius;

        // Fuselage (gövde)
        if (Math.abs(y) <= 0.15) {
            return Math.sqrt(x*x + z*z) <= 0.3;
        }

        // Main rotor (ana rotor)
        if (y > 0.4 && y < 0.5) {
            double rotorRadius = Math.sqrt(x*x + z*z);
            return rotorRadius <= 0.8;
        }

        // Rotor mast (rotor direği)
        if (Math.abs(x) <= 0.05 && Math.abs(z) <= 0.05 && y > 0.15 && y < 0.4) {
            return true;
        }

        // Tail boom (kuyruk boom'u)
        if (x < -0.2 && Math.abs(y) <= 0.08 && Math.abs(z) <= 0.06) {
            return true;
        }

        // Tail rotor (kuyruk rotoru)
        if (x < -0.3 && Math.abs(y) <= 0.05 && Math.abs(z) <= 0.2) {
            return true;
        }

        // Landing skids (inme takozları)
        if (y < -0.1 && Math.abs(y + 0.15) <= 0.05) {
            if (Math.abs(z) <= 0.4 && Math.abs(x) <= 0.25) return true;
        }

        return false;
    }

    @Override
    public String getName() {
        return "HELICOPTER";
    }

    @Override
    public double getDeformationFactor() {
        return 0.8;
    }

    @Override
    public String getDisplayName() {
        return "Helikopter";
    }
}