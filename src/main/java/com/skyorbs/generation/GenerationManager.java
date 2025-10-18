package com.skyorbs.generation;

import com.skyorbs.SkyOrbs;
import com.skyorbs.biomes.BiomeType;
import com.skyorbs.core.Orb;
import com.skyorbs.features.*;
import com.skyorbs.shapes.PlanetShape;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.*;

public class GenerationManager {
    
    private final SkyOrbs plugin;
    private final PlacementService placementService;
    private final PlanetSizeCalculator sizeCalculator;
    private final AsteroidGenerator asteroidGenerator;
    private final SatelliteGenerator satelliteGenerator;
    private final ExecutorService executor;
    
    public GenerationManager(SkyOrbs plugin) {
        this.plugin = plugin;
        this.placementService = new PlacementService(plugin);
        this.sizeCalculator = new PlanetSizeCalculator(plugin);
        this.asteroidGenerator = new AsteroidGenerator(plugin);
        this.satelliteGenerator = new SatelliteGenerator(plugin);
        this.executor = Executors.newCachedThreadPool(); // Daha hızlı thread yönetimi için cached pool
    }
    
    public void createPlanetAsync(World world, Player player) {
        createPlanetAsync(world, player, plugin.getConfigManager().generateRandomName());
    }
    
    public void createPlanetAsync(World world, Player player, String name) {
        player.sendMessage("§eGezegen oluşturuluyor...");

        executor.submit(() -> {
            try {
                List<Orb> existingOrbs = plugin.getDatabaseManager().getAllOrbs();
                int radius = sizeCalculator.calculateRadius("RANDOM");
                PlacementService.PlacementResult placement = placementService.findPlacement(radius, existingOrbs);

                if (!placement.isSuccess()) {
                    Bukkit.getScheduler().runTask(plugin, () ->
                        player.sendMessage("§cUygun yer bulunamadı!")
                    );
                    return;
                }

                // LOCATION RESERVE - Başka gezegen aynı yere oluşmasın
                // LOCATION RESERVE - Başka gezegen aynı yere oluşmasın
                placementService.reserveLocation(placement.getX(), placement.getZ());

                long seed = System.currentTimeMillis();
                Random random = new Random(seed);

                PlanetShape shape = plugin.getShapeRegistry().getRandomShape(
                    plugin.getConfigManager().getShapeWeights()
                );

                BiomeType biome = BiomeType.getRandomBiomeWeighted(random);
                
                // NEW: Select random palette for diversity
                com.skyorbs.palettes.PlanetPalette palette = plugin.getPaletteRegistry().getRandomPalette(random);
                
                // NEW: Resolve modifiers for variety
                com.skyorbs.modifiers.ModifierResolver modifierResolver = new com.skyorbs.modifiers.ModifierResolver(seed);
                java.util.Set<com.skyorbs.modifiers.PlanetModifier> modifiers = modifierResolver.resolveModifiers();
                
                // NEW: Select atmosphere
                com.skyorbs.atmosphere.AtmosphereType atmosphere = plugin.getAtmosphereManager().selectRandomAtmosphere(random);

                Orb orb = new Orb(
                    UUID.randomUUID(),
                    name,
                    world.getName(),
                    placement.getX(),
                    placement.getY(),
                    placement.getZ(),
                    radius,
                    shape.getName(),
                    biome.name(),
                    seed,
                    System.currentTimeMillis(),
                    false,
                    null
                );
                
                // NEW: Set advanced features
                orb.setPaletteId(palette.getId());
                orb.setModifiers(modifiers);
                orb.setAtmosphere(atmosphere);
                
                // NEW: Register atmosphere effects
                plugin.getAtmosphereManager().registerPlanetAtmosphere(orb.getId(), atmosphere);

                // İLK: Hemen teleport et (üst yüzeye) - gerçek konum hesapla
                Location immediateLoc = new Location(world, placement.getX(), placement.getY() + radius + 10, placement.getZ());
                Bukkit.getScheduler().runTask(plugin, () -> {
                    player.teleport(immediateLoc);
                    player.sendMessage("§aGezegen yüzeyine ışınlandınız! Oluşturma devam ediyor...");
                    player.sendMessage("§eİlk bloklar yerleştiriliyor... (Yukarıdan aşağıya)");
                });

                // LOCATION RESERVE - ÖNEMLİ: Başka gezegen aynı yere oluşmasın
                placementService.reserveLocation(placement.getX(), placement.getZ());

                // Progress tracking için toplam adım sayısı
                final int[] progress = {0};
                final int totalSteps = 8; // Shell + Ores + Trees + Structures + Treasures + Dungeons + Asteroids + Satellites

                // Progress display type from config
                String progressType = plugin.getConfig().getString("progress_display.type", "bossbar");

                // BossBar oluştur (if enabled)
                final BossBar[] bossBarRef = new BossBar[1];
                final String[] actionBarRef = {""};

                if ("bossbar".equals(progressType)) {
                    Bukkit.getScheduler().runTask(plugin, () -> {
                        BossBar bossBar = Bukkit.createBossBar("§eGezegen Oluşturuluyor... §f0%", BarColor.BLUE, BarStyle.SEGMENTED_10);
                        bossBar.addPlayer(player);
                        bossBar.setVisible(true);
                        bossBarRef[0] = bossBar;
                    });
                }

                // Progress mesajı gönderici
                Runnable sendProgress = () -> {
                    int percent = (progress[0] * 100) / totalSteps;
                    double progressValue = (double) progress[0] / totalSteps;

                    Bukkit.getScheduler().runTask(plugin, () -> {
                        String progressMsg = String.format("🌍 Gezegen Oluşturuluyor... %d%%", percent);

                        if ("bossbar".equals(progressType) && bossBarRef[0] != null) {
                            bossBarRef[0].setProgress(Math.min(progressValue, 1.0));
                            bossBarRef[0].setTitle(String.format("§eGezegen Oluşturuluyor... §f%d%% §7(%d/%d)", percent, progress[0], totalSteps));
                        } else if ("actionbar".equals(progressType)) {
                            actionBarRef[0] = progressMsg;
                            // Send action bar message
                            player.sendActionBar(progressMsg);
                        } else if ("chat".equals(progressType)) {
                            player.sendMessage("§e" + progressMsg);
                        }
                    });
                };

                // OPTIMIZE EDILDI: Paralel asteroid/satellite üretimi - PROGRESS İLE
                generatePlanetShellAsync(world, orb, shape, biome, () -> {
                    progress[0]++;
                    sendProgress.run();
                    
                    Bukkit.getScheduler().runTask(plugin, () -> {
                        player.sendMessage("§eGezegen kabuğu tamamlandı! Şimdi özellikler ekleniyor...");
                    });

                    try {
                        // ORE GENERATION - GEZEGEN BLOKLARINDAN SONRA!
                        progress[0]++;
                        sendProgress.run();
                        Bukkit.getScheduler().runTask(plugin, () -> {
                            player.sendMessage("§eMadenciler çalışıyor...");
                        });

                        List<OreGenerator.BlockData> ores = OreGenerator.generateOres(orb, biome, world);
                        List<BlockPlacement> oreBlocks = new ArrayList<>();
                        for (OreGenerator.BlockData ore : ores) {
                            oreBlocks.add(new BlockPlacement(ore.x, ore.y, ore.z, ore.material));
                        }
                        placeBlocksInBatches(world, oreBlocks, null, true); // allowReplacement = true for ores

                        // AĞAÇ GENERATION
                        progress[0]++;
                        sendProgress.run();
                        player.sendMessage("§eAğaçlar büyüyor...");

                        List<OreGenerator.BlockData> trees = TreeGenerator.generateTrees(orb, biome, world);
                        List<BlockPlacement> treeBlocks = new ArrayList<>();
                        for (OreGenerator.BlockData tree : trees) {
                            treeBlocks.add(new BlockPlacement(tree.x, tree.y, tree.z, tree.material));
                        }
                        placeBlocksInBatches(world, treeBlocks, null);

                        // YAPI GENERATION
                        progress[0]++;
                        sendProgress.run();
                        player.sendMessage("§eYapılar inşa ediliyor...");

                        List<OreGenerator.BlockData> structures = StructureGenerator.generateStructures(orb, biome, world);
                        List<BlockPlacement> structureBlocks = new ArrayList<>();
                        for (OreGenerator.BlockData structure : structures) {
                            structureBlocks.add(new BlockPlacement(structure.x, structure.y, structure.z, structure.material));
                        }
                        placeBlocksInBatches(world, structureBlocks, null);

                        // HAZİNE GENERATION
                        progress[0]++;
                        sendProgress.run();
                        player.sendMessage("§eHazinelar gizleniyor...");

                        List<TreasureGenerator.TreasureLocation> treasures = TreasureGenerator.generateTreasures(orb, biome, world);
                        for (TreasureGenerator.TreasureLocation treasure : treasures) {
                            // Place chest
                            Block chestBlock = world.getBlockAt(treasure.x, treasure.y, treasure.z);
                            chestBlock.setType(Material.CHEST, false);
                            if (chestBlock.getState() instanceof Chest chest) {
                                TreasureGenerator.fillTreasureChest(chestBlock, treasure.biome, treasure.type, new Random(orb.getSeed() + treasure.x + treasure.y + treasure.z));
                            }
                        }

                        // DUNGEON GENERATION
                        progress[0]++;
                        sendProgress.run();
                        Bukkit.getScheduler().runTask(plugin, () -> {
                            player.sendMessage("§eZindanlar oluşturuluyor...");
                        });

                        // Generate dungeons inside planet
                        int dungeonCount = Math.max(1, radius / 8);
                        Bukkit.getScheduler().runTask(plugin, () -> {
                            List<com.skyorbs.dungeons.DungeonGenerator.DungeonRoom> dungeons = 
                                plugin.getDungeonGenerator().generateDungeons(orb, dungeonCount);
                        });

                        // YENİ: YÜZEY DETAYLARI - Kraterler, kalıntılar, işaretler
                        progress[0]++;
                        sendProgress.run();
                        Bukkit.getScheduler().runTask(plugin, () -> {
                            player.sendMessage("§eYüzey detayları ekleniyor...");
                        });
                        
                        generateSurfaceDetails(world, orb, biome, random);

                        // YENİ: HALKALAR - Bazı gezegenlerde
                        if (random.nextDouble() < 0.25) { // %25 şans
                            progress[0]++;
                            sendProgress.run();
                            Bukkit.getScheduler().runTask(plugin, () -> {
                                player.sendMessage("§eGezegen halkaları oluşturuluyor...");
                            });
                            
                            generatePlanetaryRings(world, orb, random);
                        }

                        // Save main planet
                        plugin.getDatabaseManager().saveOrb(orb);

                        // Increment planet counter
                        plugin.incrementPlanetCount();

                        // Debug log
                        if (plugin.getConfigManager().isDebugEnabled()) {
                            plugin.logDebug("generation", String.format(
                                "Gezegen oluşturuldu: %s | Şekil: %s | Biyom: %s | Yarıçap: %d | Konum: %d,%d,%d",
                                orb.getName(), shape.getName(), biome.name(), radius,
                                orb.getCenterX(), orb.getCenterY(), orb.getCenterZ()
                            ));
                        }

                        // Teleport player - gerçek gezegen konumuna ve progress display'ı kaldır
                        Bukkit.getScheduler().runTaskLater(plugin, () -> {
                            // Yüzey bulma algoritması - güvenli teleport (first solid block)
                            Location surfaceLoc = findSafeSurfaceLocation(world, orb);
                            player.teleport(surfaceLoc);

                            // Progress display'ı kaldır
                            if ("bossbar".equals(progressType) && bossBarRef[0] != null) {
                                bossBarRef[0].setVisible(false);
                                bossBarRef[0].removeAll();
                            }

                            String shapeName = plugin.getShapeRegistry().getShape(orb.getShapeName()).getDisplayName();
                            player.sendMessage("§a§lGezegen Tamamen Oluşturuldu!");
                            player.sendMessage(String.format("§aGezegen: §e%s §7(%s, %s)",
                                orb.getName(), shapeName, orb.getBiomeName()
                            ));
                            player.sendMessage(String.format("§aKonum: §f%d, %d, %d §7| Yarıçap: §f%d",
                                orb.getCenterX(), orb.getCenterY(), orb.getCenterZ(), orb.getRadius()
                            ));

                            // LOCATION RELEASE - İşlem TAMAMEN bittiğinde serbest bırak
                            placementService.releaseLocation(placement.getX(), placement.getZ());
                        }, 5L); // Çok daha erken

                    } catch (Exception e) {
                        plugin.logError("Asteroid/satellite hatası", e);
                    }
                });

                // LOCATION RELEASE - İşlem tamamlandıktan sonra serbest bırak
                // LOCATION RELEASE - Hata durumunda da serbest bırak
                placementService.releaseLocation(placement.getX(), placement.getZ());

            } catch (Exception e) {
                plugin.logError("Gezegen oluşturma hatası", e);
                Bukkit.getScheduler().runTask(plugin, () ->
                    player.sendMessage("§cHata: " + e.getMessage())
                );
            }
        });
    }
    
    /**
     * ÇEŞİTLİ GEZEGEN ÜRETİMİ - Bazı dolu, bazı içi boş!
     * Rastgele olarak içi boş veya dolu gezegenler oluştur
     */
    private void generatePlanetShellAsync(World world, Orb orb, PlanetShape shape, BiomeType biome, Runnable callback) {
        int cx = orb.getCenterX();
        int cy = orb.getCenterY();
        int cz = orb.getCenterZ();
        int radius = orb.getRadius();
        long seed = orb.getSeed();
        Random random = new Random(seed);

        // GEZEGEN TİPİ BELİRLE - Use modifier system
        boolean isHollow = orb.isHollow(); // Check if HOLLOW modifier is present

        // 1. PLANET GENERATION (Async) - ÇEŞİTLİ TİPTE GEZEGENLER!
        CompletableFuture.supplyAsync(() -> {
            List<BlockPlacement> blocks = new ArrayList<>(500000); // Daha büyük kapasite

            if (isHollow) {
                // HOLLOW PLANET - İçi boş, kalın kabuk
                generateHollowPlanet(blocks, cx, cy, cz, radius, seed, shape, biome, random);
            } else {
                // SOLID PLANET - Tam dolu, klasik
                generateSolidPlanet(blocks, cx, cy, cz, radius, seed, shape, biome, random);
            }

            return blocks;
        }, executor).thenAcceptAsync(blocks -> {

            // Progress: Shell tamamlandı
            if (callback != null) {
                Bukkit.getScheduler().runTask(plugin, () -> {
                    // İlk blokları hemen yerleştir (görsel feedback)
                    List<BlockPlacement> firstBlocks = blocks.subList(0, Math.min(100, blocks.size()));
                    for (BlockPlacement bp : firstBlocks) {
                        try {
                            Block block = world.getBlockAt(bp.x, bp.y, bp.z);
                            if (block.getType() == Material.AIR || block.getType() == Material.CAVE_AIR) {
                                block.setType(bp.material, false);
                            }
                        } catch (Exception e) {
                            // Skip
                        }
                    }
                });
            }

            // 5. PLACE PLANET BLOCKS FIRST
            placeBlocksInBatches(world, blocks, callback);

        }, executor);
    }

    /**
     * SOLID PLANET - Tam dolu klasik gezegen
     */
    private void generateSolidPlanet(List<BlockPlacement> blocks, int cx, int cy, int cz, int radius, long seed, PlanetShape shape, BiomeType biome, Random random) {
        // NEW: Get palette for diverse blocks
        com.skyorbs.palettes.PlanetPalette palette = plugin.getPaletteRegistry().getRandomPalette(random);
        
        for (int y = radius; y >= -radius; y--) {
            for (int x = -radius; x <= radius; x++) {
                for (int z = -radius; z <= radius; z++) {
                    double distance = Math.sqrt(x * x + y * y + z * z);

                    if (distance <= radius) {
                        if (shape.isBlockPart(x, y, z, radius, seed)) {
                            int depth = (int)(radius - distance);
                            // NEW: Use palette instead of biome for material selection
                            Material material = palette.getMaterialByDepth(depth, random);
                            blocks.add(new BlockPlacement(cx + x, cy + y, cz + z, material));
                        }
                    }
                }
            }
        }
    }

    /**
     * HOLLOW PLANET - İçi boş, configurable shell thickness, tunnels, and civilization structures
     */
    private void generateHollowPlanet(List<BlockPlacement> blocks, int cx, int cy, int cz, int radius, long seed, PlanetShape shape, BiomeType biome, Random random) {
        // Configurable shell thickness
        int shellThickness = plugin.getConfig().getInt("hollow.shell_thickness", 5);
        
        // NEW: Get palette for diverse blocks
        com.skyorbs.palettes.PlanetPalette palette = plugin.getPaletteRegistry().getRandomPalette(random);

        for (int y = radius; y >= -radius; y--) {
            for (int x = -radius; x <= radius; x++) {
                for (int z = -radius; z <= radius; z++) {
                    double distance = Math.sqrt(x * x + y * y + z * z);

                    // KALIN KABUK - Yüzeyden shellThickness blok içeriye kadar
                    if (distance <= radius && distance >= radius - shellThickness) {
                        if (shape.isBlockPart(x, y, z, radius, seed)) {
                            int depth = (int)(radius - distance);
                            // NEW: Use palette instead of biome
                            Material material = palette.getMaterialByDepth(depth, random);
                            blocks.add(new BlockPlacement(cx + x, cy + y, cz + z, material));
                        }
                    }
                }
            }
        }

        // Generate interior structures for civilization feeling
        generateInteriorStructures(blocks, cx, cy, cz, radius, seed, biome, random);

        // Generate tunnels connecting structures
        generateInteriorTunnels(blocks, cx, cy, cz, radius, seed, biome, random);
    }

    /**
     * Generate interior structures for civilization feeling in hollow planets
     */
    private void generateInteriorStructures(List<BlockPlacement> blocks, int cx, int cy, int cz, int radius, long seed, BiomeType biome, Random random) {
        int structureCount = Math.max(2, radius / 6); // Structures per planet size

        for (int i = 0; i < structureCount; i++) {
            // Generate structure positions within the hollow interior
            double angle1 = random.nextDouble() * Math.PI * 2;
            double angle2 = (random.nextDouble() - 0.5) * Math.PI / 3; // -30 to +30 degrees
            double distance = radius * 0.3 + random.nextDouble() * radius * 0.4; // 30-70% from center

            int structX = cx + (int)(Math.cos(angle1) * Math.cos(angle2) * distance);
            int structY = cy + (int)(Math.sin(angle2) * distance);
            int structZ = cz + (int)(Math.sin(angle1) * Math.cos(angle2) * distance);

            // Generate different types of interior structures
            generateInteriorStructure(blocks, structX, structY, structZ, biome, random);
        }
    }

    /**
     * Generate a single interior structure
     */
    private void generateInteriorStructure(List<BlockPlacement> blocks, int x, int y, int z, BiomeType biome, Random random) {
        int structureType = random.nextInt(4); // 4 different structure types

        switch (structureType) {
            case 0 -> generateInteriorRuins(blocks, x, y, z, biome, random);
            case 1 -> generateInteriorLab(blocks, x, y, z, biome, random);
            case 2 -> generateInteriorTemple(blocks, x, y, z, biome, random);
            case 3 -> generateInteriorHabitat(blocks, x, y, z, biome, random);
        }
    }

    /**
     * Generate interior ruins
     */
    private void generateInteriorRuins(List<BlockPlacement> blocks, int x, int y, int z, BiomeType biome, Random random) {
        Material wallMat = getBiomeWallMaterial(biome);
        Material floorMat = getBiomeFloorMaterial(biome);

        // Small ruined room (5x5x3)
        for (int dx = -2; dx <= 2; dx++) {
            for (int dz = -2; dz <= 2; dz++) {
                // Floor
                blocks.add(new BlockPlacement(x + dx, y, z + dz, floorMat));
                // Partial walls (some broken)
                if (random.nextDouble() < 0.7) {
                    blocks.add(new BlockPlacement(x + dx, y + 1, z + dz, wallMat));
                }
                if (random.nextDouble() < 0.5) {
                    blocks.add(new BlockPlacement(x + dx, y + 2, z + dz, wallMat));
                }
            }
        }

        // Add some debris
        for (int i = 0; i < 3; i++) {
            int debrisX = x + random.nextInt(5) - 2;
            int debrisZ = z + random.nextInt(5) - 2;
            blocks.add(new BlockPlacement(debrisX, y + 1, debrisZ, Material.COBBLESTONE));
        }
    }

    /**
     * Generate interior lab/research facility
     */
    private void generateInteriorLab(List<BlockPlacement> blocks, int x, int y, int z, BiomeType biome, Random random) {
        Material wallMat = Material.IRON_BLOCK;
        Material floorMat = Material.STONE;

        // Lab room (6x6x4)
        for (int dx = -3; dx <= 3; dx++) {
            for (int dz = -3; dz <= 3; dz++) {
                blocks.add(new BlockPlacement(x + dx, y, z + dz, floorMat));
                if (Math.abs(dx) == 3 || Math.abs(dz) == 3) {
                    for (int dy = 1; dy <= 3; dy++) {
                        blocks.add(new BlockPlacement(x + dx, y + dy, z + dz, wallMat));
                    }
                }
            }
        }

        // Lab equipment
        blocks.add(new BlockPlacement(x, y + 1, z, Material.CRAFTING_TABLE));
        blocks.add(new BlockPlacement(x + 1, y + 1, z + 1, Material.BREWING_STAND));
        blocks.add(new BlockPlacement(x - 1, y + 1, z - 1, Material.FURNACE));

        // Add some glowing elements for atmosphere
        if (biome == BiomeType.LUMINOUS || biome == BiomeType.GLOWSTONE_CAVERN) {
            blocks.add(new BlockPlacement(x, y + 3, z, Material.SEA_LANTERN));
        }
    }

    /**
     * Generate interior temple/shrine
     */
    private void generateInteriorTemple(List<BlockPlacement> blocks, int x, int y, int z, BiomeType biome, Random random) {
        Material wallMat = getBiomeWallMaterial(biome);
        Material pillarMat = Material.QUARTZ_BLOCK;

        // Temple platform (7x7)
        for (int dx = -3; dx <= 3; dx++) {
            for (int dz = -3; dz <= 3; dz++) {
                blocks.add(new BlockPlacement(x + dx, y, z + dz, Material.QUARTZ_BLOCK));
            }
        }

        // Corner pillars
        int[][] corners = {{-3, -3}, {3, -3}, {-3, 3}, {3, 3}};
        for (int[] corner : corners) {
            for (int dy = 1; dy <= 4; dy++) {
                blocks.add(new BlockPlacement(x + corner[0], y + dy, z + corner[1], pillarMat));
            }
        }

        // Central altar
        blocks.add(new BlockPlacement(x, y + 1, z, Material.ENCHANTING_TABLE));
        blocks.add(new BlockPlacement(x, y + 2, z, Material.END_ROD));
    }

    /**
     * Generate interior habitat/living quarters
     */
    private void generateInteriorHabitat(List<BlockPlacement> blocks, int x, int y, int z, BiomeType biome, Random random) {
        Material wallMat = getBiomeWallMaterial(biome);
        Material floorMat = getBiomeFloorMaterial(biome);

        // Living quarters (4x6x3)
        for (int dx = -2; dx <= 2; dx++) {
            for (int dz = -3; dz <= 3; dz++) {
                blocks.add(new BlockPlacement(x + dx, y, z + dz, floorMat));
                if (Math.abs(dx) == 2 || Math.abs(dz) == 3) {
                    for (int dy = 1; dy <= 2; dy++) {
                        blocks.add(new BlockPlacement(x + dx, y + dy, z + dz, wallMat));
                    }
                }
            }
        }

        // Furniture
        blocks.add(new BlockPlacement(x - 1, y + 1, z - 2, Material.CHEST));
        blocks.add(new BlockPlacement(x + 1, y + 1, z + 2, Material.CRAFTING_TABLE));
        blocks.add(new BlockPlacement(x, y + 1, z, Material.RED_BED));
    }

    /**
     * Get biome-appropriate wall material
     */
    private Material getBiomeWallMaterial(BiomeType biome) {
        return switch (biome) {
            case CRYSTAL_FOREST, CRYSTALLINE -> Material.AMETHYST_BLOCK;
            case LAVA_OCEAN, MAGMA_CAVES -> Material.NETHER_BRICKS;
            case VOID -> Material.OBSIDIAN;
            case CORRUPTED -> Material.WARPED_PLANKS;
            case TOXIC_SWAMP, TOXIC -> Material.GREEN_CONCRETE;
            case LUMINOUS -> Material.SEA_LANTERN;
            default -> Material.STONE_BRICKS;
        };
    }

    /**
     * Get biome-appropriate floor material
     */
    private Material getBiomeFloorMaterial(BiomeType biome) {
        return switch (biome) {
            case CRYSTAL_FOREST, CRYSTALLINE -> Material.PURPUR_BLOCK;
            case LAVA_OCEAN, MAGMA_CAVES -> Material.BLACKSTONE;
            case VOID -> Material.END_STONE;
            case CORRUPTED -> Material.CRIMSON_PLANKS;
            case TOXIC_SWAMP, TOXIC -> Material.LIME_CONCRETE;
            case LUMINOUS -> Material.GLOWSTONE;
            default -> Material.STONE;
        };
    }

    /**
     * Generate connecting tunnels between interior structures
     */
    private void generateInteriorTunnels(List<BlockPlacement> blocks, int cx, int cy, int cz, int radius, long seed, BiomeType biome, Random random) {
        int tunnelCount = Math.max(3, radius / 5);

        for (int i = 0; i < tunnelCount; i++) {
            // Tunnel starting from surface inward
            double angle1 = random.nextDouble() * Math.PI * 2;
            double angle2 = random.nextDouble() * Math.PI / 2 - Math.PI / 4;
            double startDistance = radius - 2;

            int startX = cx + (int)(Math.cos(angle1) * Math.cos(angle2) * startDistance);
            int startY = cy + (int)(Math.sin(angle2) * startDistance);
            int startZ = cz + (int)(Math.sin(angle1) * Math.cos(angle2) * startDistance);

            // Tunnel direction
            double tunnelAngle1 = random.nextDouble() * Math.PI * 2;
            double tunnelAngle2 = (random.nextDouble() - 0.5) * Math.PI / 3;

            int tunnelLength = 5 + random.nextInt(Math.max(5, radius / 2));

            // Create tunnel
            for (int step = 0; step < tunnelLength; step++) {
                int tunnelX = startX + (int)(Math.cos(tunnelAngle1) * Math.cos(tunnelAngle2) * step);
                int tunnelY = startY + (int)(Math.sin(tunnelAngle2) * step);
                int tunnelZ = startZ + (int)(Math.sin(tunnelAngle1) * Math.cos(tunnelAngle2) * step);

                int tunnelRadius = 1 + random.nextInt(2);

                for (int dx = -tunnelRadius; dx <= tunnelRadius; dx++) {
                    for (int dy = -tunnelRadius; dy <= tunnelRadius; dy++) {
                        for (int dz = -tunnelRadius; dz <= tunnelRadius; dz++) {
                            double dist = Math.sqrt(dx * dx + dy * dy + dz * dz);
                            if (dist <= tunnelRadius) {
                                double planetDist = Math.sqrt((tunnelX + dx - cx) * (tunnelX + dx - cx) +
                                                             (tunnelY + dy - cy) * (tunnelY + dy - cy) +
                                                             (tunnelZ + dz - cz) * (tunnelZ + dz - cz));
                                if (planetDist <= radius - 1) {
                                    blocks.add(new BlockPlacement(tunnelX + dx, tunnelY + dy, tunnelZ + dz, Material.AIR));
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    
    /**
     * Blokları batch'ler halinde yerleştirir (chunk-aware) - OPTIMIZE EDILDI
     */
    private void placeBlocksInBatches(World world, List<BlockPlacement> blocks, Runnable callback) {
        placeBlocksInBatches(world, blocks, callback, false);
    }

    /**
     * Blokları batch'ler halinde yerleştirir (chunk-aware) - OPTIMIZE EDILDI
     * @param allowReplacement Eğer true ise mevcut blokları değiştirir (ore'lar için)
     */
    private void placeBlocksInBatches(World world, List<BlockPlacement> blocks, Runnable callback, boolean allowReplacement) {
        int batchSize = plugin.getConfigManager().getBlocksPerBatch(); // Config'den oku
        int totalBatches = (blocks.size() + batchSize - 1) / batchSize;

        // Chunk'ları önceden yükle (optimize edilmiş)
        Set<ChunkPos> chunksToLoad = new HashSet<>();
        for (BlockPlacement bp : blocks) {
            chunksToLoad.add(new ChunkPos(bp.x >> 4, bp.z >> 4));
        }

        // Chunk yükleme - paralel yükleme
        Bukkit.getScheduler().runTask(plugin, () -> {
            for (ChunkPos pos : chunksToLoad) {
                if (!world.isChunkLoaded(pos.x, pos.z)) {
                    world.loadChunk(pos.x, pos.z, false);
                }
            }
        });

        // Batch placement - config'e göre optimize edilmiş
        int batchesPerTick = plugin.getConfigManager().getBatchesPerTick();
        for (int i = 0; i < totalBatches; i++) {
            int start = i * batchSize;
            int end = Math.min(start + batchSize, blocks.size());
            List<BlockPlacement> batch = blocks.subList(start, end);

            int delay = i / batchesPerTick + 1; // Config'e göre batch sıklığı

            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                for (BlockPlacement bp : batch) {
                    try {
                        Block block = world.getBlockAt(bp.x, bp.y, bp.z);
                        if (allowReplacement || block.getType() == Material.AIR || block.getType() == Material.CAVE_AIR) {
                            block.setType(bp.material, false);
                        }
                    } catch (Exception e) {
                        // Chunk yüklü değilse skip
                    }
                }
            }, delay);
        }

        // Run callback after all batches complete - daha erken callback
        if (callback != null) {
            Bukkit.getScheduler().runTaskLater(plugin, callback, totalBatches / 2 + 5);
        }
    }
    
    public CompletableFuture<Void> deletePlanet(Orb orb) {
        CompletableFuture<Void> future = new CompletableFuture<>();
        
        executor.submit(() -> {
            try {
                // Delete children first
                List<Orb> children = plugin.getDatabaseManager().getChildOrbs(orb.getId());
                for (Orb child : children) {
                    clearPlanetBlocks(orb);
                    plugin.getDatabaseManager().deleteOrb(child.getId());
                }
                
                // Delete main planet
                clearPlanetBlocks(orb);
                plugin.getDatabaseManager().deleteOrb(orb.getId());
                future.complete(null);
                
            } catch (Exception e) {
                future.completeExceptionally(e);
            }
        });
        
        return future;
    }
    
    /**
     * Gezegen bloklarını temizler
     */
    private void clearPlanetBlocks(Orb orb) {
        World world = Bukkit.getWorld(orb.getWorldName());
        if (world == null) return;
        
        int cx = orb.getCenterX();
        int cy = orb.getCenterY();
        int cz = orb.getCenterZ();
        int radius = orb.getRadius();
        
        List<BlockPlacement> airBlocks = new ArrayList<>();
        
        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    double distance = Math.sqrt(x * x + y * y + z * z);
                    if (distance <= radius) {
                        airBlocks.add(new BlockPlacement(cx + x, cy + y, cz + z, Material.AIR));
                    }
                }
            }
        }
        
        placeBlocksInBatches(world, airBlocks, null);
    }
    
    /**
     * Güvenli yüzey konumu bulma algoritması - First solid block teleport
     */
    private Location findSafeSurfaceLocation(World world, Orb orb) {
        int cx = orb.getCenterX();
        int cy = orb.getCenterY();
        int cz = orb.getCenterZ();
        int radius = orb.getRadius();

        // Gezegenin üst kısmından başlayarak aşağı doğru tara - find first solid block
        for (int y = cy + radius; y >= cy - radius; y--) {
            for (int x = cx - 3; x <= cx + 3; x++) {
                for (int z = cz - 3; z <= cz + 3; z++) {
                    Block block = world.getBlockAt(x, y, z);
                    Block below = world.getBlockAt(x, y - 1, z);

                    // Katı blok üzerinde hava varsa güvenli konum (first solid block found)
                    if (block.getType() == Material.AIR && below.getType().isSolid() &&
                        !isDangerousBlock(below.getType())) {
                        return new Location(world, x + 0.5, y, z + 0.5);
                    }
                }
            }
        }

        // Bulunamazsa varsayılan konum (fallback)
        return new Location(world, cx, cy + radius + 5, cz);
    }

    /**
     * Check if block is dangerous for teleportation
     */
    private boolean isDangerousBlock(Material material) {
        return material == Material.LAVA ||
               material == Material.FIRE ||
               material == Material.CACTUS ||
               material == Material.SWEET_BERRY_BUSH ||
               material.name().contains("MAGMA");
    }

    /**
     * YENİ: Yüzey detayları - Kraterler, kalıntılar, işaretler
     */
    private void generateSurfaceDetails(World world, Orb orb, BiomeType biome, Random random) {
        int cx = orb.getCenterX();
        int cy = orb.getCenterY();
        int cz = orb.getCenterZ();
        int radius = orb.getRadius();
        
        List<BlockPlacement> surfaceBlocks = new ArrayList<>();
        
        // 1. KRATERLER - Çarpma izleri
        int craterCount = 3 + random.nextInt(5);
        for (int i = 0; i < craterCount; i++) {
            generateCrater(surfaceBlocks, cx, cy, cz, radius, random);
        }
        
        // 2. YÜZEY YAPILARI - Eski uygarlık kalıntıları
        int ruinCount = 2 + random.nextInt(3);
        for (int i = 0; i < ruinCount; i++) {
            generateSurfaceRuin(surfaceBlocks, cx, cy, cz, radius, biome, random);
        }
        
        // 3. İŞARETLER - Beacon, totem, anıtlar
        int monumentCount = 1 + random.nextInt(2);
        for (int i = 0; i < monumentCount; i++) {
            generateMonument(surfaceBlocks, cx, cy, cz, radius, biome, random);
        }
        
        // 4. YÜZEY DETAYLARI - Kayalar, kristaller
        int detailCount = 10 + random.nextInt(20);
        for (int i = 0; i < detailCount; i++) {
            generateSurfaceDetail(surfaceBlocks, cx, cy, cz, radius, biome, random);
        }
        
        // Blokları yerleştir
        placeBlocksInBatches(world, surfaceBlocks, null);
    }
    
    /**
     * Krater oluştur
     */
    private void generateCrater(List<BlockPlacement> blocks, int cx, int cy, int cz, int radius, Random random) {
        // Yüzeyde rastgele konum
        double angle1 = random.nextDouble() * Math.PI * 2;
        double angle2 = (random.nextDouble() - 0.5) * Math.PI / 2;
        
        int craterX = cx + (int)(Math.cos(angle1) * Math.cos(angle2) * radius);
        int craterY = cy + (int)(Math.sin(angle2) * radius);
        int craterZ = cz + (int)(Math.sin(angle1) * Math.cos(angle2) * radius);
        
        int craterRadius = 3 + random.nextInt(5);
        int craterDepth = 2 + random.nextInt(3);
        
        // Krater çukuru
        for (int x = -craterRadius; x <= craterRadius; x++) {
            for (int z = -craterRadius; z <= craterRadius; z++) {
                double dist = Math.sqrt(x * x + z * z);
                if (dist <= craterRadius) {
                    int depth = (int)(craterDepth * (1 - dist / craterRadius));
                    for (int y = 0; y < depth; y++) {
                        blocks.add(new BlockPlacement(craterX + x, craterY - y, craterZ + z, Material.AIR));
                    }
                    // Krater kenarı - yükseltilmiş
                    if (dist > craterRadius - 2 && dist <= craterRadius) {
                        blocks.add(new BlockPlacement(craterX + x, craterY + 1, craterZ + z, Material.COBBLESTONE));
                    }
                }
            }
        }
    }
    
    /**
     * Yüzey kalıntısı oluştur
     */
    private void generateSurfaceRuin(List<BlockPlacement> blocks, int cx, int cy, int cz, int radius, BiomeType biome, Random random) {
        double angle1 = random.nextDouble() * Math.PI * 2;
        double angle2 = (random.nextDouble() - 0.5) * Math.PI / 2;
        
        int ruinX = cx + (int)(Math.cos(angle1) * Math.cos(angle2) * radius);
        int ruinY = cy + (int)(Math.sin(angle2) * radius);
        int ruinZ = cz + (int)(Math.sin(angle1) * Math.cos(angle2) * radius);
        
        Material ruinMat = getBiomeWallMaterial(biome);
        
        // Küçük yıkık yapı (3x3)
        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                if (random.nextDouble() < 0.6) { // Bazı bloklar eksik
                    blocks.add(new BlockPlacement(ruinX + x, ruinY + 1, ruinZ + z, ruinMat));
                    if (random.nextDouble() < 0.3) {
                        blocks.add(new BlockPlacement(ruinX + x, ruinY + 2, ruinZ + z, ruinMat));
                    }
                }
            }
        }
        
        // Merkeze sandık
        blocks.add(new BlockPlacement(ruinX, ruinY + 1, ruinZ, Material.CHEST));
    }
    
    /**
     * Anıt oluştur
     */
    private void generateMonument(List<BlockPlacement> blocks, int cx, int cy, int cz, int radius, BiomeType biome, Random random) {
        double angle1 = random.nextDouble() * Math.PI * 2;
        double angle2 = (random.nextDouble() - 0.5) * Math.PI / 2;
        
        int monX = cx + (int)(Math.cos(angle1) * Math.cos(angle2) * radius);
        int monY = cy + (int)(Math.sin(angle2) * radius);
        int monZ = cz + (int)(Math.sin(angle1) * Math.cos(angle2) * radius);
        
        // Dikey anıt (totem benzeri)
        Material pillarMat = Material.QUARTZ_PILLAR;
        int height = 5 + random.nextInt(5);
        
        for (int y = 1; y <= height; y++) {
            blocks.add(new BlockPlacement(monX, monY + y, monZ, pillarMat));
        }
        
        // Üste beacon veya glowstone
        blocks.add(new BlockPlacement(monX, monY + height + 1, monZ, Material.BEACON));
        
        // Etrafına platform
        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                if (x != 0 || z != 0) {
                    blocks.add(new BlockPlacement(monX + x, monY + 1, monZ + z, Material.STONE_BRICKS));
                }
            }
        }
    }
    
    /**
     * Yüzey detayı oluştur
     */
    private void generateSurfaceDetail(List<BlockPlacement> blocks, int cx, int cy, int cz, int radius, BiomeType biome, Random random) {
        double angle1 = random.nextDouble() * Math.PI * 2;
        double angle2 = (random.nextDouble() - 0.5) * Math.PI / 2;
        
        int detailX = cx + (int)(Math.cos(angle1) * Math.cos(angle2) * radius);
        int detailY = cy + (int)(Math.sin(angle2) * radius);
        int detailZ = cz + (int)(Math.sin(angle1) * Math.cos(angle2) * radius);
        
        // Biyoma göre detay
        Material detailMat = switch (biome) {
            case CRYSTAL_FOREST, CRYSTALLINE -> Material.AMETHYST_CLUSTER;
            case LAVA_OCEAN, MAGMA_CAVES -> Material.MAGMA_BLOCK;
            case FROZEN_TUNDRA, GLACIER -> Material.ICE;
            case DESERT, BADLANDS -> Material.CACTUS;
            default -> Material.COBBLESTONE;
        };
        
        // Küçük kaya veya kristal kümesi
        blocks.add(new BlockPlacement(detailX, detailY + 1, detailZ, detailMat));
        if (random.nextDouble() < 0.3) {
            blocks.add(new BlockPlacement(detailX, detailY + 2, detailZ, detailMat));
        }
    }
    
    /**
     * YENİ: Gezegen halkaları oluştur
     */
    private void generatePlanetaryRings(World world, Orb orb, Random random) {
        int cx = orb.getCenterX();
        int cy = orb.getCenterY();
        int cz = orb.getCenterZ();
        int radius = orb.getRadius();
        
        List<BlockPlacement> ringBlocks = new ArrayList<>();
        
        // Halka sayısı (1-3)
        int ringCount = 1 + random.nextInt(3);
        
        for (int ringIndex = 0; ringIndex < ringCount; ringIndex++) {
            // Halka parametreleri
            double ringInnerRadius = radius * (1.3 + ringIndex * 0.3);
            double ringOuterRadius = ringInnerRadius + radius * 0.2;
            double ringThickness = 1 + random.nextInt(2);
            
            // Halka malzemesi
            Material ringMat = getRingMaterial(random);
            
            // Halka açısı (ekvator etrafında)
            double tiltAngle = (random.nextDouble() - 0.5) * Math.PI / 6; // ±15 derece
            
            // Halka oluştur
            for (double angle = 0; angle < Math.PI * 2; angle += 0.05) {
                for (double r = ringInnerRadius; r < ringOuterRadius; r += 0.5) {
                    // Rastgele boşluklar (daha gerçekçi)
                    if (random.nextDouble() < 0.3) continue;
                    
                    int x = cx + (int)(Math.cos(angle) * r);
                    int z = cz + (int)(Math.sin(angle) * r);
                    
                    // Y pozisyonu (tilt ile)
                    int y = cy + (int)(Math.sin(tiltAngle) * (x - cx));
                    
                    // Kalınlık
                    for (int dy = 0; dy < ringThickness; dy++) {
                        ringBlocks.add(new BlockPlacement(x, y + dy, z, ringMat));
                    }
                }
            }
        }
        
        // Halka bloklarını yerleştir
        placeBlocksInBatches(world, ringBlocks, null);
    }
    
    /**
     * Halka malzemesi seç
     */
    private Material getRingMaterial(Random random) {
        Material[] ringMaterials = {
            Material.ICE,
            Material.PACKED_ICE,
            Material.SNOW_BLOCK,
            Material.QUARTZ_BLOCK,
            Material.STONE,
            Material.COBBLESTONE,
            Material.ANDESITE,
            Material.DIORITE
        };
        return ringMaterials[random.nextInt(ringMaterials.length)];
    }

    public void shutdown() {
        executor.shutdown();
        try {
            if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
        }
    }
    
    private static class BlockPlacement {
        int x, y, z;
        Material material;
        
        BlockPlacement(int x, int y, int z, Material material) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.material = material;
        }
    }
    
    private static class ChunkPos {
        int x, z;
        
        ChunkPos(int x, int z) {
            this.x = x;
            this.z = z;
        }
        
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ChunkPos chunkPos = (ChunkPos) o;
            return x == chunkPos.x && z == chunkPos.z;
        }
        
        @Override
        public int hashCode() {
            return Objects.hash(x, z);
        }
    }
}