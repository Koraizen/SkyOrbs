package com.skyorbs.utils;

import java.util.Random;

public class NoiseGenerator {
    
    private static final int[] PERMUTATION = new int[512];
    
    static {
        Random rand = new Random(0);
        for (int i = 0; i < 256; i++) {
            PERMUTATION[i] = i;
        }
        for (int i = 0; i < 256; i++) {
            int j = rand.nextInt(256);
            int temp = PERMUTATION[i];
            PERMUTATION[i] = PERMUTATION[j];
            PERMUTATION[j] = temp;
        }
        for (int i = 0; i < 256; i++) {
            PERMUTATION[256 + i] = PERMUTATION[i];
        }
    }
    
    public static double getNoise(int x, int y, int z, long seed, double frequency) {
        Random seedRand = new Random(seed);
        int offsetX = seedRand.nextInt(10000);
        int offsetY = seedRand.nextInt(10000);
        int offsetZ = seedRand.nextInt(10000);
        
        double nx = (x + offsetX) * frequency;
        double ny = (y + offsetY) * frequency;
        double nz = (z + offsetZ) * frequency;
        
        return perlinNoise(nx, ny, nz);
    }
    
    private static double perlinNoise(double x, double y, double z) {
        int X = (int) Math.floor(x) & 255;
        int Y = (int) Math.floor(y) & 255;
        int Z = (int) Math.floor(z) & 255;
        
        x -= Math.floor(x);
        y -= Math.floor(y);
        z -= Math.floor(z);
        
        double u = fade(x);
        double v = fade(y);
        double w = fade(z);
        
        int A = PERMUTATION[X] + Y;
        int AA = PERMUTATION[A] + Z;
        int AB = PERMUTATION[A + 1] + Z;
        int B = PERMUTATION[X + 1] + Y;
        int BA = PERMUTATION[B] + Z;
        int BB = PERMUTATION[B + 1] + Z;
        
        return lerp(w,
            lerp(v,
                lerp(u, grad(PERMUTATION[AA], x, y, z), grad(PERMUTATION[BA], x - 1, y, z)),
                lerp(u, grad(PERMUTATION[AB], x, y - 1, z), grad(PERMUTATION[BB], x - 1, y - 1, z))
            ),
            lerp(v,
                lerp(u, grad(PERMUTATION[AA + 1], x, y, z - 1), grad(PERMUTATION[BA + 1], x - 1, y, z - 1)),
                lerp(u, grad(PERMUTATION[AB + 1], x, y - 1, z - 1), grad(PERMUTATION[BB + 1], x - 1, y - 1, z - 1))
            )
        );
    }
    
    private static double fade(double t) {
        return t * t * t * (t * (t * 6 - 15) + 10);
    }
    
    private static double lerp(double t, double a, double b) {
        return a + t * (b - a);
    }
    
    private static double grad(int hash, double x, double y, double z) {
        int h = hash & 15;
        double u = h < 8 ? x : y;
        double v = h < 4 ? y : h == 12 || h == 14 ? x : z;
        return ((h & 1) == 0 ? u : -u) + ((h & 2) == 0 ? v : -v);
    }
}
