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

public class AsteroidGenerator {

    private final SkyOrbs plugin;

    public AsteroidGenerator(SkyOrbs plugin) {
        this.plugin = plugin;
    }

    /**
     * Ana gezegen için asteroidler üretir
     */
    public List<Orb> generateAsteroidsForPlanet(Orb planet, World world) {
        List<Orb> asteroids = new ArrayList<>();
        Random random = new Random(planet.getSeed() + 999);

        int minCount = plugin.getConfigManager().getMinAsteroidsPerPlanet();
        int maxCount = plugin.getConfigManager().getMaxAsteroidsPerPlanet();
        int count = minCount + random.nextInt(maxCount - minCount + 1);

        for (int i = 0; i < count; i++) {
            Orb asteroid = createAsteroid(planet, world, random);
            if (asteroid != null) {
                asteroids.add(asteroid);
            }
        }

        return asteroids;
    }

    /**
     * Tek bir asteroid oluşturur
     */
    private Orb createAsteroid(Orb parentPlanet, World world, Random random) {
        int minRadius = plugin.getConfigManager().getMinAsteroidRadius();
        int maxRadius = plugin.getConfigManager().getMaxAsteroidRadius();

        // Boyut dağılımı (küçük %60, orta %30, büyük %10)
        double sizeRand = random.nextDouble();
        int radius;
        if (sizeRand < 0.6) {
            radius = minRadius + random.nextInt(11); // 20-30
        } else if (sizeRand < 0.9) {
            radius = 30 + random.nextInt(11); // 30-40
        } else {
            radius = 40 + random.nextInt(11); // 40-50
        }

        // Şekil seçimi (Asteroid %70, Fractal %20, Spiky %10)
        String shapeName;
        double shapeRand = random.nextDouble();
        if (shapeRand < 0.7) {
            shapeName = "ASTEROID";
        } else if (shapeRand < 0.9) {
            shapeName = "FRACTAL";
        } else {
            shapeName = "SPIKY";
        }

        // Biyom seçimi (rastgele ama mantıklı)
        BiomeType biome = getRandomAsteroidBiome(random);

        // Pozisyon hesaplama
        int[] position = calculateAsteroidPosition(parentPlanet, random);
        int centerX = position[0];
        int centerY = position[1];
        int centerZ = position[2];

        // İsim oluşturma
        String name = "Asteroid-" + UUID.randomUUID().toString().substring(0, 6);

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
            true, // isAsteroid = true
            parentPlanet.getId()
        );
    }

    /**
     * Asteroid pozisyonunu hesaplar
     */
    private int[] calculateAsteroidPosition(Orb parentPlanet, Random random) {
        int parentX = parentPlanet.getCenterX();
        int parentY = parentPlanet.getCenterY();
        int parentZ = parentPlanet.getCenterZ();

        int minDistance = plugin.getConfigManager().getMinAsteroidDistance();
        int maxDistance = plugin.getConfigManager().getMaxAsteroidDistance();

        // Rastgele açı ve mesafe
        double angle = random.nextDouble() * 2 * Math.PI;
        int distance = minDistance + random.nextInt(maxDistance - minDistance + 1);

        int x = parentX + (int)(Math.cos(angle) * distance);
        int z = parentZ + (int)(Math.sin(angle) * distance);
        int y = parentY + random.nextInt(41) - 20; // ±20 varyasyon

        return new int[]{x, y, z};
    }

    /**
     * Asteroid için uygun biyom seçer
     */
    private BiomeType getRandomAsteroidBiome(Random random) {
        // Asteroidler için daha sert biyomlar
        BiomeType[] asteroidBiomes = {
            BiomeType.CANYON,
            BiomeType.BADLANDS,
            BiomeType.ICE_SPIKES,
            BiomeType.LAVA_OCEAN,
            BiomeType.VOID
        };

        return asteroidBiomes[random.nextInt(asteroidBiomes.length)];
    }

    /**
     * Asteroid bloklarını üretir (ore'lar dahil)
     */
    public List<OreGenerator.BlockData> generateAsteroidBlocks(Orb asteroid, BiomeType biome) {
        List<OreGenerator.BlockData> blocks = new ArrayList<>();
        Random random = new Random(asteroid.getSeed());

        int cx = asteroid.getCenterX();
        int cy = asteroid.getCenterY();
        int cz = asteroid.getCenterZ();
        int radius = asteroid.getRadius();

        // Asteroid şekli için shape al
        var shape = plugin.getShapeRegistry().getShape(asteroid.getShapeName());
        if (shape == null) {
            shape = plugin.getShapeRegistry().getShape("ASTEROID");
        }

        // Ana bloklar
        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    if (shape.isBlockPart(x, y, z, radius, asteroid.getSeed())) {
                        Material material = getAsteroidMaterial(biome, random);
                        blocks.add(new OreGenerator.BlockData(cx + x, cy + y, cz + z, material));
                    }
                }
            }
        }

        // Asteroid ore'ları (düşük yoğunluk)
        generateAsteroidOres(blocks, asteroid, biome, random);

        return blocks;
    }

    /**
     * Asteroid malzemesi seçer
     */
    private Material getAsteroidMaterial(BiomeType biome, Random random) {
        // Biyom bazlı temel malzemeler
        Material[] materials = switch (biome) {
            case CANYON, BADLANDS -> new Material[]{Material.STONE, Material.COBBLESTONE, Material.GRAVEL};
            case ICE_SPIKES -> new Material[]{Material.ICE, Material.PACKED_ICE, Material.BLUE_ICE};
            case LAVA_OCEAN -> new Material[]{Material.NETHERRACK, Material.BASALT, Material.BLACKSTONE};
            case VOID -> new Material[]{Material.END_STONE, Material.OBSIDIAN, Material.PURPUR_BLOCK};
            default -> new Material[]{Material.STONE, Material.ANDESITE, Material.DIORITE, Material.GRANITE};
        };

        return materials[random.nextInt(materials.length)];
    }

    /**
     * Asteroid ore'ları üretir (düşük yoğunluk)
     */
    private void generateAsteroidOres(List<OreGenerator.BlockData> blocks, Orb asteroid, BiomeType biome, Random random) {
        int cx = asteroid.getCenterX();
        int cy = asteroid.getCenterY();
        int cz = asteroid.getCenterZ();
        int radius = asteroid.getRadius();

        // Çok düşük ore yoğunluğu
        int oreCount = radius * radius / 100; // Çok az ore

        for (int i = 0; i < oreCount; i++) {
            int x = cx + random.nextInt(radius * 2) - radius;
            int y = cy + random.nextInt(radius * 2) - radius;
            int z = cz + random.nextInt(radius * 2) - radius;

            // Sadece asteroid içinde ise
            double distance = Math.sqrt((x - cx) * (x - cx) + (y - cy) * (y - cy) + (z - cz) * (z - cz));
            if (distance <= radius - 2) {
                Material ore = getAsteroidOre(biome, random);
                if (ore != null) {
                    blocks.add(new OreGenerator.BlockData(x, y, z, ore));
                }
            }
        }
    }

    /**
     * Asteroid ore malzemesi seçer
     */
    private Material getAsteroidOre(BiomeType biome, Random random) {
        // Asteroidlerde nadir bulunan malzemeler
        Material[] ores = {Material.COAL_ORE, Material.IRON_ORE, Material.COPPER_ORE, Material.GOLD_ORE, Material.DIAMOND_ORE};
        return ores[random.nextInt(ores.length)];
    }
}