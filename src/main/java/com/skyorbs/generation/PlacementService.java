package com.skyorbs.generation;

import com.skyorbs.SkyOrbs;
import com.skyorbs.core.Orb;
import org.bukkit.Bukkit;

import java.util.List;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Iterator;
import java.util.Map;

public class PlacementService {
    
    private final SkyOrbs plugin;
    private final ConcurrentHashMap<String, Long> reservedLocations = new ConcurrentHashMap<>();
    private final Random random = new Random();
    private static final long RESERVATION_TIMEOUT = 5 * 60 * 1000; // 5 minutes
    
    public PlacementService(SkyOrbs plugin) {
        this.plugin = plugin;
        startCleanupTask();
    }
    
    /**
     * Start scheduled task to clean stale reservations
     */
    private void startCleanupTask() {
        Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, () -> {
            long now = System.currentTimeMillis();
            int cleaned = 0;
            
            // Thread-safe iteration and removal for ConcurrentHashMap
            for (Map.Entry<String, Long> entry : reservedLocations.entrySet()) {
                if (now - entry.getValue() > RESERVATION_TIMEOUT) {
                    if (reservedLocations.remove(entry.getKey(), entry.getValue())) {
                        cleaned++;
                    }
                }
            }
            
            if (cleaned > 0 && plugin.getConfigManager().isDebugEnabled()) {
                plugin.logDebug("placement", "Cleaned " + cleaned + " stale location reservations");
            }
        }, 100L, 6000L); // Run every 5 minutes (6000 ticks)
    }
    
    public PlacementResult findPlacement(int radius, List<Orb> existingOrbs) {
        int maxDistance = plugin.getConfigManager().getMaxDistance();
        int centerX = plugin.getConfigManager().getCenterX();
        int centerZ = plugin.getConfigManager().getCenterZ();
        int minFromSpawn = plugin.getConfigManager().getMinDistanceFromSpawn();
        
        if (plugin.getConfigManager().isClusteringEnabled() && !existingOrbs.isEmpty()) {
            PlacementResult cluster = tryClusterPlacement(radius, existingOrbs, maxDistance, centerX, centerZ, minFromSpawn);
            if (cluster != null) {
                return cluster;
            }
        }
        
        return trySpiralPlacement(radius, existingOrbs, maxDistance, centerX, centerZ, minFromSpawn);
    }
    
    private PlacementResult tryClusterPlacement(int radius, List<Orb> existingOrbs, int maxDistance, int centerX, int centerZ, int minFromSpawn) {
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
            
            if (!validateDistance(candidateX, candidateZ, maxDistance, centerX, centerZ, minFromSpawn)) {
                continue;
            }
            
            if (!checkOverlap(candidateX, candidateZ, radius, existingOrbs)) {
                int y = 100 + random.nextInt(21) - 10; // 90-110 arası, daha güvenli
                return new PlacementResult(candidateX, y, candidateZ, true);
            }
        }
        
        return null;
    }
    
    private PlacementResult trySpiralPlacement(int radius, List<Orb> existingOrbs, int maxDistance, int centerX, int centerZ, int minFromSpawn) {
        int ringStep = 150; // Daha sık halkalar - 200'den 150'ye
        int angleStep = 10; // Daha sık açılar - 15'den 10'a

        // DAHA FAZLA DENEME - 300 halka (config'e göre maxDistance'a kadar)
        for (int ring = 3; ring <= 300; ring++) {
            int ringDist = ring * ringStep;
            if (ringDist > maxDistance) {
                break;
            }

            for (int angle = 0; angle < 360; angle += angleStep) {
                double rad = Math.toRadians(angle);
                int candidateX = centerX + (int)(Math.cos(rad) * ringDist);
                int candidateZ = centerZ + (int)(Math.sin(rad) * ringDist);

                if (!validateDistance(candidateX, candidateZ, maxDistance, centerX, centerZ, minFromSpawn)) {
                    continue;
                }

                if (!checkOverlap(candidateX, candidateZ, radius, existingOrbs)) {
                    int y = 100 + random.nextInt(21) - 10; // 90-110 arası, daha güvenli
                    return new PlacementResult(candidateX, y, candidateZ, true);
                }
            }
        }

        // SON ÇARE - Rastgele konumlar dene (ARTIRILDI)
        for (int attempt = 0; attempt < 10000; attempt++) { // 5000'den 10000'e
            double angle = random.nextDouble() * 2 * Math.PI;
            int distance = minFromSpawn + random.nextInt(maxDistance - minFromSpawn);
            int candidateX = centerX + (int)(Math.cos(angle) * distance);
            int candidateZ = centerZ + (int)(Math.sin(angle) * distance);

            if (!validateDistance(candidateX, candidateZ, maxDistance, centerX, centerZ, minFromSpawn)) {
                continue;
            }

            if (!checkOverlap(candidateX, candidateZ, radius, existingOrbs)) {
                int y = 100 + random.nextInt(21) - 10;
                return new PlacementResult(candidateX, y, candidateZ, true);
            }
        }

        return new PlacementResult(0, 0, 0, false);
    }
    
    private boolean validateDistance(int x, int z, int maxDistance, int centerX, int centerZ, int minFromSpawn) {
        int dx = x - centerX;
        int dz = z - centerZ;
        double distFromCenter = Math.sqrt(dx * dx + dz * dz);
        return distFromCenter <= maxDistance && distFromCenter >= minFromSpawn;
    }
    
    private boolean checkOverlap(int x, int z, int newRadius, List<Orb> existingOrbs) {
        // İlk olarak diğer gezegenlerle çakışma kontrolü
        for (Orb orb : existingOrbs) {
            double distance = orb.getDistanceFrom(x, z);
            int safeDistance = orb.getRadius() + newRadius + 1200; // DAHA FAZLA MESAFE - 1200 blok güvenli mesafe

            if (distance < safeDistance) {
                return true;
            }
        }

        // Rezerve edilmiş konum kontrolü
        String key = x + "," + z;
        if (reservedLocations.containsKey(key)) {
            return true;
        }

        // BLOK KONTROLÜ - Daha az katı kontrol, sadece çok yoğun blok kümeleri varsa red et
        return hasTooManyBlocksAtLocation(x, z, newRadius);
    }

    /**
     * Belirtilen konumda çok fazla blok olup olmadığını kontrol eder (daha toleranslı)
     */
    private boolean hasTooManyBlocksAtLocation(int x, int z, int radius) {
        // Gezegen oluşacak alanda örnekleme yap
        int samplePoints = Math.min(8, radius / 5 + 1); // Daha az örnek
        int step = Math.max(1, radius / samplePoints);
        int blockCount = 0;
        int totalChecks = 0;

        for (int dx = -radius; dx <= radius; dx += step) {
            for (int dz = -radius; dz <= radius; dz += step) {
                double distance = Math.sqrt(dx * dx + dz * dz);
                if (distance <= radius) {
                    // Bu konumda blok kontrolü yap
                    int checkX = x + dx;
                    int checkZ = z + dz;

                    // Yükseklik aralığında kontrol et (yeryüzü seviyesinde)
                    for (int y = 60; y <= 140; y += 20) { // Daha az kontrol, 60-140 arası 20'er
                        totalChecks++;
                        // World instance'ı plugin'den al
                        if (plugin.getServer().getWorlds().size() > 0) {
                            var world = plugin.getServer().getWorlds().get(0); // Ana dünya
                            if (world != null) {
                                var block = world.getBlockAt(checkX, y, checkZ);
                                var type = block.getType();
                                // Hava, su, lava, kar dışında blok varsa say
                                if (!type.isAir() && type != org.bukkit.Material.WATER &&
                                    type != org.bukkit.Material.LAVA && type != org.bukkit.Material.SNOW &&
                                    type != org.bukkit.Material.SNOW_BLOCK) {
                                    blockCount++;
                                }
                            }
                        }
                    }
                }
            }
        }

        // %30'dan fazla blok varsa çakışma var kabul et
        return totalChecks > 0 && (blockCount * 100.0 / totalChecks) > 30.0;
    }
    
    public void reserveLocation(int x, int z) {
        reservedLocations.put(x + "," + z, System.currentTimeMillis());
    }
    
    public void releaseLocation(int x, int z) {
        reservedLocations.remove(x + "," + z);
    }
    
    /**
     * Get count of currently reserved locations
     */
    public int getReservedCount() {
        return reservedLocations.size();
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
