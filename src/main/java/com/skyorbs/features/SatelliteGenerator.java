package com.skyorbs.features;

import com.skyorbs.SkyOrbs;
import com.skyorbs.biomes.BiomeType;
import com.skyorbs.core.Orb;
import org.bukkit.Material;
import org.bukkit.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class SatelliteGenerator {

    private final SkyOrbs plugin;

    public SatelliteGenerator(SkyOrbs plugin) {
        this.plugin = plugin;
    }

    /**
     * Ana gezegen için uydular üretir
     */
    public List<Orb> generateSatellitesForPlanet(Orb planet, World world) {
        List<Orb> satellites = new ArrayList<>();
        Random random = new Random(planet.getSeed() + 777);

        int minCount = plugin.getConfigManager().getMinSatellitesPerPlanet();
        int maxCount = plugin.getConfigManager().getMaxSatellitesPerPlanet();

        // Olasılık kontrolü
        double probability = plugin.getConfigManager().getSatelliteProbability();
        if (random.nextDouble() > probability) {
            return satellites; // Uydu yok
        }

        int count = minCount + random.nextInt(maxCount - minCount + 1);

        for (int i = 0; i < count; i++) {
            Orb satellite = createSatellite(planet, world, random);
            if (satellite != null) {
                satellites.add(satellite);
            }
        }

        return satellites;
    }

    /**
     * Tek bir uydu oluşturur
     */
    private Orb createSatellite(Orb parentPlanet, World world, Random random) {
        // Uydu boyutu ana gezegenden küçük
        int parentRadius = parentPlanet.getRadius();
        int minRadius = Math.max(15, parentRadius / 4);
        int maxRadius = Math.max(25, parentRadius / 2);
        int radius = minRadius + random.nextInt(maxRadius - minRadius + 1);

        // Şekil seçimi (daha yuvarlak uydular)
        String shapeName;
        double shapeRand = random.nextDouble();
        if (shapeRand < 0.5) {
            shapeName = "SPHERE";
        } else if (shapeRand < 0.8) {
            shapeName = "ELLIPSOID";
        } else {
            shapeName = "BLOB";
        }

        // Biyom seçimi (uzay temalı)
        BiomeType biome = getRandomSatelliteBiome(random);

        // Pozisyon hesaplama (daha uzak)
        int[] position = calculateSatellitePosition(parentPlanet, random);
        int centerX = position[0];
        int centerY = position[1];
        int centerZ = position[2];

        // İsim oluşturma
        String name = "Satellite-" + UUID.randomUUID().toString().substring(0, 6);

        // Seed oluşturma
        long seed = random.nextLong();

        return new Orb(
            UUID.randomUUID(),
            name,
            world.getName(),
            centerX,
            centerY,
            centerZ,
            radius,
            shapeName,
            biome.name(),
            seed,
            System.currentTimeMillis(),
            false, // isAsteroid = false (uydu)
            parentPlanet.getId()
        );
    }

    /**
     * Uydu pozisyonunu hesaplar (asteroidlerden daha uzak)
     */
    private int[] calculateSatellitePosition(Orb parentPlanet, Random random) {
        int parentX = parentPlanet.getCenterX();
        int parentY = parentPlanet.getCenterY();
        int parentZ = parentPlanet.getCenterZ();

        // Uydular daha uzak (800-1500 blok)
        int minDistance = 800;
        int maxDistance = 1500;
        int distance = minDistance + random.nextInt(maxDistance - minDistance + 1);

        // Rastgele açı
        double angle = random.nextDouble() * 2 * Math.PI;

        int x = parentX + (int)(Math.cos(angle) * distance);
        int z = parentZ + (int)(Math.sin(angle) * distance);
        int y = parentY + random.nextInt(101) - 50; // ±50 varyasyon

        return new int[]{x, y, z};
    }

    /**
     * Uydu için uygun biyom seçer
     */
    private BiomeType getRandomSatelliteBiome(Random random) {
        // Uydular için daha egzotik biyomlar
        BiomeType[] satelliteBiomes = {
            BiomeType.CRYSTAL_FOREST,
            BiomeType.VOID,
            BiomeType.ICE_SPIKES,
            BiomeType.LAVA_OCEAN
        };

        return satelliteBiomes[random.nextInt(satelliteBiomes.length)];
    }

    /**
     * Uydu bloklarını üretir
     */
    public List<OreGenerator.BlockData> generateSatelliteBlocks(Orb satellite, BiomeType biome) {
        List<OreGenerator.BlockData> blocks = new ArrayList<>();
        Random random = new Random(satellite.getSeed());

        int cx = satellite.getCenterX();
        int cy = satellite.getCenterY();
        int cz = satellite.getCenterZ();
        int radius = satellite.getRadius();

        // Uydu şekli için shape al
        var shape = plugin.getShapeRegistry().getShape(satellite.getShapeName());
        if (shape == null) {
            shape = plugin.getShapeRegistry().getShape("SPHERE");
        }

        // Ana bloklar
        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    if (shape.isBlockPart(x, y, z, radius, satellite.getSeed())) {
                        Material material = getSatelliteMaterial(biome, random);
                        blocks.add(new OreGenerator.BlockData(cx + x, cy + y, cz + z, material));
                    }
                }
            }
        }

        // Uydu özellikleri (daha nadir)
        generateSatelliteFeatures(blocks, satellite, biome, random);

        return blocks;
    }

    /**
     * Uydu malzemesi seçer
     */
    private Material getSatelliteMaterial(BiomeType biome, Random random) {
        // Biyom bazlı egzotik malzemeler
        Material[] materials = switch (biome) {
            case CRYSTAL_FOREST -> new Material[]{Material.AMETHYST_BLOCK, Material.QUARTZ_BLOCK, Material.PURPUR_BLOCK};
            case VOID -> new Material[]{Material.END_STONE, Material.OBSIDIAN, Material.PURPUR_BLOCK};
            case ICE_SPIKES -> new Material[]{Material.PACKED_ICE, Material.BLUE_ICE, Material.ICE};
            case LAVA_OCEAN -> new Material[]{Material.MAGMA_BLOCK, Material.OBSIDIAN, Material.BLACKSTONE};
            default -> new Material[]{Material.STONE, Material.ANDESITE, Material.DIORITE};
        };

        return materials[random.nextInt(materials.length)];
    }

    /**
     * Uydu özel özellikleri üretir
     */
    private void generateSatelliteFeatures(List<OreGenerator.BlockData> blocks, Orb satellite, BiomeType biome, Random random) {
        int cx = satellite.getCenterX();
        int cy = satellite.getCenterY();
        int cz = satellite.getCenterZ();
        int radius = satellite.getRadius();

        // Özel yapılar (çok nadir)
        int featureCount = Math.max(1, radius / 10);

        for (int i = 0; i < featureCount; i++) {
            int x = cx + random.nextInt(radius * 2) - radius;
            int y = cy + random.nextInt(radius * 2) - radius;
            int z = cz + random.nextInt(radius * 2) - radius;

            // Sadece uydu içinde ise
            double distance = Math.sqrt((x - cx) * (x - cx) + (y - cy) * (y - cy) + (z - cz) * (z - cz));
            if (distance <= radius - 1) {
                Material feature = getSatelliteFeature(biome, random);
                if (feature != null) {
                    blocks.add(new OreGenerator.BlockData(x, y, z, feature));
                }
            }
        }
    }

    /**
     * Uydu özel malzemesi seçer
     */
    private Material getSatelliteFeature(BiomeType biome, Random random) {
        // Özel malzemeler
        Material[] features = switch (biome) {
            case CRYSTAL_FOREST -> new Material[]{Material.AMETHYST_CLUSTER, Material.SMALL_AMETHYST_BUD};
            case VOID -> new Material[]{Material.END_ROD, Material.CHORUS_PLANT};
            case ICE_SPIKES -> new Material[]{Material.BLUE_ICE, Material.PACKED_ICE};
            case LAVA_OCEAN -> new Material[]{Material.LAVA, Material.MAGMA_BLOCK};
            default -> null;
        };

        return features != null ? features[random.nextInt(features.length)] : null;
    }
}