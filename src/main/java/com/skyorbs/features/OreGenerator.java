package com.skyorbs.features;

import com.skyorbs.SkyOrbs;
import com.skyorbs.core.Orb;
import com.skyorbs.core.PlanetType;
import org.bukkit.Material;
import org.bukkit.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class OreGenerator {

    /**
     * ANA FONKSİYON: SOLID PLANETLERDE SADECE ORE OLUŞTURUR
     * AÇIK ALANDA ORE OLUŞTURMAZ!
     */
    public static List<BlockData> generateOres(Orb orb, PlanetType planetType, World world) {
        List<BlockData> ores = new ArrayList<>();

        // 1. KONTROL: HOLLOW PLANET İSE ÇIK (ORE OLUŞTURMA)
        if (orb.isHollow()) {
            return ores; // BOŞ LİSTE DÖNDÜR
        }

        // 2. CONFIG'DEN AYARLARI AL
        SkyOrbs plugin = SkyOrbs.getInstance();
        Map<String, Object> config = plugin.getConfigManager().getOreConfigForPlanetType(planetType.name().toLowerCase());

        // 3. ORE OLUŞUMU AKTİF Mİ KONTROL ET
        boolean enabled = (Boolean) config.getOrDefault("enabled", true);
        if (!enabled) {
            return ores; // BOŞ LİSTE DÖNDÜR
        }

        // 4. YOĞUNLUK ÇARPANI
        double densityMultiplier = (Double) config.getOrDefault("densityMultiplier", 1.0);

        // 5. ORE LİSTESİNİ AL
        @SuppressWarnings("unchecked")
        Map<String, Map<String, Object>> oresConfig = (Map<String, Map<String, Object>>) config.get("ores");

        if (oresConfig != null && !oresConfig.isEmpty()) {
            Random random = new Random(orb.getSeed() + 123);

            // 6. HER ORE İÇİN DAMAR OLUŞTUR
            for (Map.Entry<String, Map<String, Object>> entry : oresConfig.entrySet()) {
                String oreName = entry.getKey();
                Map<String, Object> oreData = entry.getValue();

                // ORE AKTİF Mİ?
                boolean oreEnabled = (Boolean) oreData.getOrDefault("enabled", true);
                if (!oreEnabled) continue;

                // ORE PARAMETRELERİ
                double baseChance = ((Number) oreData.getOrDefault("chance", 0.0)).doubleValue();
                double chance = baseChance * densityMultiplier;

                int minVein = ((Number) oreData.getOrDefault("minVein", 1)).intValue();
                int maxVein = ((Number) oreData.getOrDefault("maxVein", 1)).intValue();

                // ORE MALZEMESİ
                Material material = getOreMaterial(oreName);
                if (material == null) continue;

                // DAMAR OLUŞTUR
                generateOreVeins(ores, material, chance, minVein, maxVein, orb, random);
            }
        }

        return ores;
    }

    /**
     * ORE DAMARLARI OLUŞTUR - GEZEGEN İÇİNDE SADECE!
     */
    private static void generateOreVeins(List<BlockData> ores, Material material, double chance,
                                       int minVein, int maxVein, Orb orb, Random random) {

        int cx = orb.getCenterX();
        int cy = orb.getCenterY();
        int cz = orb.getCenterZ();
        int radius = orb.getRadius();

        // DAMAR SAYISI HESAPLA
        double volume = (4.0/3.0) * Math.PI * Math.pow(radius, 3);
        int veinCount = (int)(volume * chance * 0.001);
        veinCount = Math.max(3, veinCount); // EN AZ 3 DAMAR

        for (int i = 0; i < veinCount; i++) {
            // RASTGELE POZİSYON - GEZEGEN İÇİNDE!
            double angle1 = random.nextDouble() * Math.PI * 2;
            double angle2 = random.nextDouble() * Math.PI;
            double distance = 2 + random.nextDouble() * (radius - 4); // YÜZEYDEN UZAK TUT

            int x = cx + (int)(Math.sin(angle2) * Math.cos(angle1) * distance);
            int y = cy + (int)(Math.cos(angle2) * distance);
            int z = cz + (int)(Math.sin(angle2) * Math.sin(angle1) * distance);

            // DAMAR BOYUTU
            int veinSize = minVein + random.nextInt(maxVein - minVein + 1);

            // DAMAR BLOKLARINI OLUŞTUR
            for (int j = 0; j < veinSize; j++) {
                int offsetX = random.nextInt(3) - 1;
                int offsetY = random.nextInt(3) - 1;
                int offsetZ = random.nextInt(3) - 1;

                int blockX = x + offsetX;
                int blockY = y + offsetY;
                int blockZ = z + offsetZ;

                // GEZEGEN İÇİNDE Mİ KONTROL ET
                double blockDistance = Math.sqrt((blockX-cx)*(blockX-cx) + (blockY-cy)*(blockY-cy) + (blockZ-cz)*(blockZ-cz));
                if (blockDistance <= radius - 1) { // YÜZEYDE DEĞİL, İÇERDE
                    ores.add(new BlockData(blockX, blockY, blockZ, material));
                }
            }
        }
    }

    /**
     * ORE ADINDAN MATERIAL AL
     */
    private static Material getOreMaterial(String oreName) {
        return switch (oreName.toUpperCase()) {
            case "COAL" -> Material.COAL_ORE;
            case "IRON" -> Material.IRON_ORE;
            case "COPPER" -> Material.COPPER_ORE;
            case "GOLD" -> Material.GOLD_ORE;
            case "REDSTONE" -> Material.REDSTONE_ORE;
            case "LAPIS" -> Material.LAPIS_ORE;
            case "DIAMOND" -> Material.DIAMOND_ORE;
            case "EMERALD" -> Material.EMERALD_ORE;
            case "ANCIENT_DEBRIS" -> Material.ANCIENT_DEBRIS;
            case "NETHER_QUARTZ" -> Material.NETHER_QUARTZ_ORE;
            case "NETHER_GOLD" -> Material.NETHER_GOLD_ORE;
            case "GLOWSTONE" -> Material.GLOWSTONE;
            case "OBSIDIAN" -> Material.OBSIDIAN;
            case "SLIME" -> Material.SLIME_BLOCK;
            case "AMETHYST" -> Material.AMETHYST_BLOCK;
            case "PRISMARINE" -> Material.PRISMARINE_CRYSTALS;
            case "NETHERRACK" -> Material.NETHERRACK;
            default -> null;
        };
    }

    public static class BlockData {
        public final int x, y, z;
        public final Material material;

        public BlockData(int x, int y, int z, Material material) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.material = material;
        }
    }
}