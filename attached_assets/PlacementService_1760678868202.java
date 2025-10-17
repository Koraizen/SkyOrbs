package com.skyorbs.generation;

import com.skyorbs.SkyOrbs;
import com.skyorbs.core.Orb;

import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class PlacementService {
    
    private final SkyOrbs plugin;
    private final Set<String> reservedLocations = new HashSet<>();
    private final Random random = new Random();
    
    public PlacementService(SkyOrbs plugin) {
        this.plugin = plugin;
    }
    
    public PlacementResult findPlacement(int radius, List<Orb> existingOrbs) {
        int maxDistance = plugin.getConfigManager().getMaxDistance();
        int centerX = plugin.getConfigManager().getCenterX();
        int centerZ = plugin.getConfigManager().getCenterZ();
        
        if (plugin.getConfigManager().isClusteringEnabled() && !existingOrbs.isEmpty()) {
            PlacementResult cluster = tryClusterPlacement(radius, existingOrbs, maxDistance, centerX, centerZ);
            if (cluster != null) {
                return cluster;
            }
        }
        
        return trySpiralPlacement(radius, existingOrbs, maxDistance, centerX, centerZ);
    }
    
    private PlacementResult tryClusterPlacement(int radius, List<Orb> existingOrbs, int maxDistance, int centerX, int centerZ) {
        int minDist = plugin.getConfigManager().getClusterMinDistance();
        int maxDist = plugin.getConfigManager().getClusterMaxDistance();
        double centerAttraction = plugin.getConfigManager().getCenterAttraction();
        
        for (int attempt = 0; attempt < 50; attempt++) {
            Orb parent = existingOrbs.get(random.nextInt(existingOrbs.size()));
            
            double angle = random.nextDouble() * 2 * Math.PI;
            int distance = minDist + random.nextInt(maxDist - minDist);
            
            int candidateX = parent.getCenterX() + (int)(Math.cos(angle) * distance);
            int candidateZ = parent.getCenterZ() + (int)(Math.sin(angle) * distance);
            
            if (centerAttraction > 0) {
                candidateX = (int)(candidateX * (1 - centerAttraction) + centerX * centerAttraction);
                candidateZ = (int)(candidateZ * (1 - centerAttraction) + centerZ * centerAttraction);
            }
            
            if (!validateDistance(candidateX, candidateZ, maxDistance, centerX, centerZ)) {
                continue;
            }
            
            if (!checkOverlap(candidateX, candidateZ, radius, existingOrbs)) {
                int y = 120 + random.nextInt(41) - 20;
                return new PlacementResult(candidateX, y, candidateZ, true);
            }
        }
        
        return null;
    }
    
    private PlacementResult trySpiralPlacement(int radius, List<Orb> existingOrbs, int maxDistance, int centerX, int centerZ) {
        int ringStep = 200;
        int angleStep = 15;
        
        for (int ring = 0; ring <= 15; ring++) {
            int ringDist = ring * ringStep;
            if (ringDist > maxDistance) {
                break;
            }
            
            for (int angle = 0; angle < 360; angle += angleStep) {
                double rad = Math.toRadians(angle);
                int candidateX = centerX + (int)(Math.cos(rad) * ringDist);
                int candidateZ = centerZ + (int)(Math.sin(rad) * ringDist);
                
                if (!checkOverlap(candidateX, candidateZ, radius, existingOrbs)) {
                    int y = 120 + random.nextInt(41) - 20;
                    return new PlacementResult(candidateX, y, candidateZ, true);
                }
            }
        }
        
        return new PlacementResult(0, 0, 0, false);
    }
    
    private boolean validateDistance(int x, int z, int maxDistance, int centerX, int centerZ) {
        int dx = x - centerX;
        int dz = z - centerZ;
        return Math.sqrt(dx * dx + dz * dz) <= maxDistance;
    }
    
    private boolean checkOverlap(int x, int z, int newRadius, List<Orb> existingOrbs) {
        for (Orb orb : existingOrbs) {
            double distance = orb.getDistanceFrom(x, z);
            int safeDistance = orb.getRadius() + newRadius + 250;
            
            if (distance < safeDistance) {
                return true;
            }
        }
        
        String key = x + "," + z;
        return reservedLocations.contains(key);
    }
    
    public void reserveLocation(int x, int z) {
        reservedLocations.add(x + "," + z);
    }
    
    public void releaseLocation(int x, int z) {
        reservedLocations.remove(x + "," + z);
    }
    
    public static class PlacementResult {
        private final int x, y, z;
        private final boolean success;
        
        public PlacementResult(int x, int y, int z, boolean success) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.success = success;
        }
        
        public int getX() { return x; }
        public int getY() { return y; }
        public int getZ() { return z; }
        public boolean isSuccess() { return success; }
    }
}
