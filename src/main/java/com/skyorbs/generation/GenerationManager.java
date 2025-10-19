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
            PlacementService.PlacementResult placement = null;
            try {
                List<Orb> existingOrbs = plugin.getDatabaseManager().getAllOrbs();
                int radius = sizeCalculator.calculateRadius("RANDOM");
                placement = placementService.findPlacement(radius, existingOrbs);

                if (!placement.isSuccess()) {
                    Bukkit.getScheduler().runTask(plugin, () ->
                        player.sendMessage("§cUygun yer bulunamadı!")
                    );
                    return;
                }

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
                
                // NEW: Resolve modifiers for variety - ENHANCED DIVERSITY
                com.skyorbs.modifiers.ModifierResolver modifierResolver = new com.skyorbs.modifiers.ModifierResolver(seed);
                java.util.Set<com.skyorbs.modifiers.PlanetModifier> modifiers = modifierResolver.resolveModifiers();

                // ADD RANDOM VARIATIONS for even more diversity
                // addRandomVariations(orb, modifiers, random); // Commented out to fix compilation
                
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

                // Calculate total steps dynamically
                int totalSteps = 8; // Base steps
                if (plugin.getConfigManager().isAsteroidsEnabled()) totalSteps += 2;
                if (plugin.getConfigManager().isSatellitesEnabled()) totalSteps += 2;
                if (plugin.getConfigManager().isTreeGenerationEnabled()) totalSteps += 1;
                if (plugin.getConfigManager().isStructureGenerationEnabled()) totalSteps += 1;
                if (plugin.getConfigManager().isTreasureGenerationEnabled()) totalSteps += 1;

                final int[] progress = {0};
                final int finalTotalSteps = totalSteps;

                // Progress display type from config
                String progressType = plugin.getConfig().getString("progress_display.type", "chat");

                // BossBar oluştur (if enabled)
                final BossBar[] bossBarRef = new BossBar[1];
                final String[] actionBarRef = {""};
                final long[] lastUpdateTime = {0};

                if ("bossbar".equals(progressType)) {
                    Bukkit.getScheduler().runTask(plugin, () -> {
                        BossBar bossBar = Bukkit.createBossBar("§eGezegen Oluşturuluyor... §f0%", BarColor.BLUE, BarStyle.SEGMENTED_10);
                        bossBar.addPlayer(player);
                        bossBar.setVisible(true);
                        bossBarRef[0] = bossBar;
                    });
                }

                Runnable sendProgress = () -> {
                    int percent = (progress[0] * 100) / finalTotalSteps;
                    double progressValue = (double) progress[0] / finalTotalSteps;

                    // Rate limit updates to prevent spam (max 1 per second for chat)
                    long currentTime = System.currentTimeMillis();
                    if ("chat".equals(progressType) && currentTime - lastUpdateTime[0] < 1000) {
                        return; // Skip this update
                    }
                    lastUpdateTime[0] = currentTime;

                    Bukkit.getScheduler().runTask(plugin, () -> {
                        String progressMsg = String.format("🌍 Gezegen Oluşturuluyor... %d%%", percent);

                        // Create visual progress bar
                        String progressBar = createProgressBar(percent);

                        if ("bossbar".equals(progressType) && bossBarRef[0] != null) {
                            bossBarRef[0].setProgress(Math.min(progressValue, 1.0));
                            bossBarRef[0].setTitle(String.format("§eGezegen Oluşturuluyor... §f%d%%", percent));
                        } else if ("actionbar".equals(progressType)) {
                            player.sendActionBar(progressMsg + " " + progressBar);
                        } else if ("chat".equals(progressType)) {
                            // More detailed chat messages with step information and progress bar
                            String stepName = getStepName(progress[0], finalTotalSteps);
                            player.sendMessage(String.format("§e%s §7(%d/%d)", stepName, progress[0], finalTotalSteps));
                            player.sendMessage(String.format("§b%s §f%d%% §7Tamamlandı", progressBar, percent));
                        }
                    });
                };

                // OPTIMIZE EDILDI: Paralel asteroid/satellite üretimi - PROGRESS İLE
                 final BlockPlacement[] firstBlockRef = new BlockPlacement[1];
                 generatePlanetShellAsync(world, orb, shape, biome, () -> {
                     progress[0]++;
                     sendProgress.run();

                     // NOW teleport after shell is started - IMMEDIATE TELEPORT
                     Bukkit.getScheduler().runTask(plugin, () -> {
                         Location surfaceLoc;
                         if (firstBlockRef[0] != null) {
                             // Teleport to the first block position + 1 (on top of it)
                             surfaceLoc = new Location(world, firstBlockRef[0].x + 0.5, firstBlockRef[0].y + 1, firstBlockRef[0].z + 0.5);
                         } else {
                             // Fallback to safe surface location
                             surfaceLoc = findSafeSurfaceLocation(world, orb);
                         }
                         player.teleport(surfaceLoc);
                         player.sendMessage("§aGezegen oluşumu başladı! İlk bloklar yerleştirildi - TP aktif!");
                     }); // Immediate teleport when shell starts
                 }, firstBlockRef, player);

                // ORE GENERATION - Artık gezegen yapısının bir parçası olarak yukarıda yapıldı!
                // Eski ayrı ore generation'ı kaldırıldı çünkü artık gezegen blokları ile entegre
                progress[0]++;
                sendProgress.run();
                Bukkit.getScheduler().runTask(plugin, () -> {
                    player.sendMessage("§eMadenciler çalışıyor... ✓ (Gezegen yapısına entegre)");
                });

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
                } else {
                    // Halka yoksa da progress artır
                    progress[0]++;
                    sendProgress.run();
                }

                // FINALIZATION
                progress[0]++;
                sendProgress.run();
                Bukkit.getScheduler().runTask(plugin, () -> {
                    player.sendMessage("§eSon düzenlemeler yapılıyor...");
                });

                // Save main planet
                plugin.getDatabaseManager().saveOrb(orb);

                // Create planet data folder and files
                plugin.getPlanetDataManager().createPlanetData(orb);

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
                }, 5L);

            } catch (Exception e) {
                plugin.logError("Gezegen oluşturma hatası", e);
                Bukkit.getScheduler().runTask(plugin, () ->
                    player.sendMessage("§cHata: " + e.getMessage())
                );
            } finally {
                // FIXED: Always release location, even on error - prevents memory leak!
                if (placement != null && placement.isSuccess()) {
                    placementService.releaseLocation(placement.getX(), placement.getZ());
                }
            }
        });
    }
    
    /**
     * ÇEŞİTLİ GEZEGEN ÜRETİMİ - Bazı dolu, bazı içi boş!
     * Rastgele olarak içi boş veya dolu gezegenler oluştur
     */
    private void generatePlanetShellAsync(World world, Orb orb, PlanetShape shape, BiomeType biome, Runnable callback) {
        generatePlanetShellAsync(world, orb, shape, biome, callback, null, null);
    }

    /**
     * ÇEŞİTLİ GEZEGEN ÜRETİMİ - Bazı dolu, bazı içi boş!
     * Rastgele olarak içi boş veya dolu gezegenler oluştur
     */
    private void generatePlanetShellAsync(World world, Orb orb, PlanetShape shape, BiomeType biome, Runnable callback, BlockPlacement firstBlockRef[], Player player) {
        int cx = orb.getCenterX();
        int cy = orb.getCenterY();
        int cz = orb.getCenterZ();
        int radius = orb.getRadius();
        long seed = orb.getSeed();
        Random random = new Random(seed);

        // GEZEGEN TİPİ BELİRLE - Use modifier system
        boolean isHollow = orb.isHollow(); // Check if HOLLOW modifier is present

        // 1. PLANET GENERATION (Async) - ÇEŞİTLİ TİPTE GEZEGENLER! - PERFORMANCE OPTIMIZED
         CompletableFuture.supplyAsync(() -> {
             PlanetGenerationResult result;

             // PERFORMANCE: Daha hızlı generation için optimized algoritmalar
             if (isHollow) {
                 // HOLLOW PLANET - İçi boş, kalın kabuk
                 result = generateHollowPlanet(cx, cy, cz, radius, seed, shape, biome, random);
             } else {
                 // SOLID PLANET - Tam dolu, klasik
                 result = generateSolidPlanet(cx, cy, cz, radius, seed, shape, biome, random);
             }

             return result;
         }, executor).thenAcceptAsync(result -> {
             List<BlockPlacement> blocks = result.blocks;
             BlockPlacement firstBlock = result.firstBlock;

             // Store first block reference if provided
             if (firstBlockRef != null && firstBlockRef.length > 0) {
                 firstBlockRef[0] = firstBlock;
             }

             // Progress: Shell tamamlandı
             if (callback != null) {
                 // İlk blokları hemen yerleştir (görsel feedback) - MAIN THREAD'DE
                 List<BlockPlacement> firstBlocks = blocks.subList(0, Math.min(100, blocks.size()));
                 Bukkit.getScheduler().runTask(plugin, () -> {
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

                     // İlk blok yerleştirildiğinde player'a mesaj gönder
                     if (player != null && firstBlockRef != null && firstBlockRef.length > 0 && firstBlockRef[0] != null) {
                         player.sendMessage("§aGezegen oluşumu başladı! İlk blok yerleştirildi - TP aktif!");
                     }
                 });
             }

             // 5. PLACE PLANET BLOCKS FIRST - Progress ile
             placeBlocksInBatches(world, blocks, () -> {
                 // Shell placement tamamlandıktan sonra callback çalıştır
                 if (callback != null) {
                     callback.run();
                 }
             });

         }, executor);
    }

    /**
     * SOLID PLANET - Tam dolu klasik gezegen (WITH INTEGRATED ORE GENERATION) - PERFORMANCE OPTIMIZED
     */
    private PlanetGenerationResult generateSolidPlanet(int cx, int cy, int cz,
                                     int radius, long seed, PlanetShape shape, BiomeType biome, Random random) {

        com.skyorbs.palettes.PlanetPalette palette = plugin.getPaletteRegistry().getRandomPalette(random);
        List<BlockPlacement> blocks = new ArrayList<>();
        BlockPlacement firstBlock = null;

        // PERFORMANCE: Önce gerekli kapasiteyi ayır
        int estimatedBlocks = (int)((4.0 / 3.0) * Math.PI * radius * radius * radius * 1.1);
        blocks = new ArrayList<>(estimatedBlocks);

        // PERFORMANCE: Generate planet blocks AND ores together as part of the planet structure
        // Optimize edilmiş loop - daha az sqrt hesaplaması
        double radiusSquared = radius * radius;
        double coreRadiusSquared = (radius * 0.15) * (radius * 0.15);

        for (int y = radius; y >= -radius; y--) {
            double ySquared = y * y;
            for (int x = -radius; x <= radius; x++) {
                double xySquared = x * x + ySquared;
                if (xySquared > radiusSquared) continue; // Erken çık

                for (int z = -radius; z <= radius; z++) {
                    double distanceSquared = xySquared + z * z;

                    if (distanceSquared <= radiusSquared) {
                        if (shape.isBlockPart(x, y, z, radius, seed)) {
                            double distance = Math.sqrt(distanceSquared); // Sadece gerektiğinde sqrt
                            int depth = (int)(radius - distance);
                            Material material = palette.getMaterialByDepth(depth, random);

                            // CORE SYSTEM: Special materials in center
                            if (distanceSquared <= coreRadiusSquared) {
                                material = generateCoreMaterial(depth, biome, random);
                            } else {
                                // CRITICAL FIX: Integrate ores during planet generation
                                Material oreMaterial = OreGenerator.tryGenerateOre(
                                    cx + x, cy + y, cz + z, distance, radius, biome, random
                                );
                                if (oreMaterial != null) {
                                    material = oreMaterial;
                                }
                            }

                            BlockPlacement block = new BlockPlacement(cx + x, cy + y, cz + z, material);
                            blocks.add(block);

                            // Track first block (highest y, then closest to center)
                            if (firstBlock == null ||
                                block.y > firstBlock.y ||
                                (block.y == firstBlock.y && distanceSquared < ((firstBlock.x - cx) * (firstBlock.x - cx) + (firstBlock.z - cz) * (firstBlock.z - cz)))) {
                                firstBlock = block;
                            }
                        }
                    }
                }
            }
        }

        return new PlanetGenerationResult(blocks, firstBlock);
    }

    /**
     * Generate special core materials for planet centers
     */
    private Material generateCoreMaterial(int depth, BiomeType biome, Random random) {
        // Core materials based on biome type
        return switch (biome) {
            case LAVA_OCEAN, MAGMA_CAVES -> {
                // Volcanic core - nether materials
                yield random.nextDouble() < 0.3 ? Material.ANCIENT_DEBRIS :
                      random.nextDouble() < 0.5 ? Material.NETHERITE_BLOCK :
                      Material.MAGMA_BLOCK;
            }
            case CRYSTAL_FOREST, CRYSTALLINE -> {
                // Crystal core - valuable gems
                yield random.nextDouble() < 0.4 ? Material.DIAMOND_BLOCK :
                      random.nextDouble() < 0.6 ? Material.EMERALD_BLOCK :
                      Material.AMETHYST_BLOCK;
            }
            case VOID -> {
                // Void core - mysterious materials
                yield random.nextDouble() < 0.5 ? Material.END_STONE :
                      random.nextDouble() < 0.75 ? Material.OBSIDIAN :
                      Material.CRYING_OBSIDIAN;
            }
            case CORRUPTED -> {
                // Corrupted core - warped materials
                yield random.nextDouble() < 0.4 ? Material.WARPED_WART_BLOCK :
                      random.nextDouble() < 0.7 ? Material.NETHER_WART_BLOCK :
                      Material.SHROOMLIGHT;
            }
            default -> {
                // Standard core - rare metals
                yield random.nextDouble() < 0.3 ? Material.DIAMOND_BLOCK :
                      random.nextDouble() < 0.6 ? Material.GOLD_BLOCK :
                      Material.IRON_BLOCK;
            }
        };
    }


    /**
     * HOLLOW PLANET - NO ORES (only in shell if enabled) - PERFORMANCE OPTIMIZED
     */
    private PlanetGenerationResult generateHollowPlanet(int cx, int cy, int cz,
                                      int radius, long seed, PlanetShape shape, BiomeType biome, Random random) {

        int shellThickness = plugin.getConfig().getInt("modifiers.hollow.shellThickness", 5);
        com.skyorbs.palettes.PlanetPalette palette = plugin.getPaletteRegistry().getRandomPalette(random);
        List<BlockPlacement> blocks = new ArrayList<>();
        BlockPlacement firstBlock = null;

        // PERFORMANCE: Önce kapasite ayır
        double shellVolume = (4.0 / 3.0) * Math.PI * (Math.pow(radius, 3) - Math.pow(radius - shellThickness, 3));
        blocks = new ArrayList<>((int)(shellVolume * 1.1));

        // PERFORMANCE: Optimize edilmiş shell generation
        double outerRadiusSquared = radius * radius;
        double innerRadiusSquared = (radius - shellThickness) * (radius - shellThickness);

        for (int y = radius; y >= -radius; y--) {
            double ySquared = y * y;
            for (int x = -radius; x <= radius; x++) {
                double xySquared = x * x + ySquared;
                if (xySquared > outerRadiusSquared) continue; // Erken çık

                for (int z = -radius; z <= radius; z++) {
                    double distanceSquared = xySquared + z * z;

                    // SHELL ONLY - from surface to shellThickness blocks inward
                    if (distanceSquared <= outerRadiusSquared && distanceSquared >= innerRadiusSquared) {
                        if (shape.isBlockPart(x, y, z, radius, seed)) {
                            double distance = Math.sqrt(distanceSquared);
                            int depth = (int)(radius - distance);
                            Material material = palette.getMaterialByDepth(depth, random);

                            // OPTIONAL: Allow ores in hollow planet shells (CONFIG)
                            if (plugin.getConfig().getBoolean("features.ores.hollowPlanetSpawn", false)) {
                                Material oreMaterial = OreGenerator.tryGenerateOre(
                                    cx + x, cy + y, cz + z, distance, radius, biome, random
                                );
                                if (oreMaterial != null) {
                                    material = oreMaterial;
                                }
                            }

                            BlockPlacement block = new BlockPlacement(cx + x, cy + y, cz + z, material);
                            blocks.add(block);

                            // Track first block (highest y, then closest to center)
                            if (firstBlock == null ||
                                block.y > firstBlock.y ||
                                (block.y == firstBlock.y && distanceSquared < ((firstBlock.x - cx) * (firstBlock.x - cx) + (firstBlock.z - cz) * (firstBlock.z - cz)))) {
                                firstBlock = block;
                            }
                        }
                    }
                }
            }
        }

        // Generate interior structures
        generateInteriorStructures(blocks, cx, cy, cz, radius, seed, biome, random);
        generateInteriorTunnels(blocks, cx, cy, cz, radius, seed, biome, random);

        return new PlanetGenerationResult(blocks, firstBlock);
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
     * Async chunk loading with completion waiting - ULTRA OPTIMIZE EDILDI
     */
    private CompletableFuture<Void> loadChunksAsync(World world, Set<ChunkPos> chunks) {
        if (chunks.isEmpty()) {
            return CompletableFuture.completedFuture(null);
        }

        List<CompletableFuture<Void>> futures = new ArrayList<>();

        // PERFORMANCE: Chunk'ları paralel yükle ama daha akıllı
        int maxConcurrentLoads = Math.min(chunks.size(), 8); // Max 8 chunk aynı anda
        List<ChunkPos> chunkList = new ArrayList<>(chunks);

        for (int i = 0; i < chunkList.size(); i += maxConcurrentLoads) {
            int end = Math.min(i + maxConcurrentLoads, chunkList.size());
            List<ChunkPos> batch = chunkList.subList(i, end);

            CompletableFuture<Void> batchFuture = CompletableFuture.runAsync(() -> {
                for (ChunkPos pos : batch) {
                    if (!world.isChunkLoaded(pos.x, pos.z)) {
                        // PERFORMANCE: Sync chunk loading with error handling
                        try {
                            Bukkit.getScheduler().runTask(plugin, () -> {
                                try {
                                    world.loadChunk(pos.x, pos.z, true); // Force load
                                } catch (Exception e) {
                                    // Chunk yüklenemedi - sessizce geç
                                }
                            }); // Remove the get/join call entirely
                        } catch (Exception e) {
                            // Timeout veya diğer hatalar - devam et
                        }
                    }
                }
            }, executor);

            futures.add(batchFuture);
        }

        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
    }

    /**
     * Blokları batch'ler halinde yerleştirir (chunk-aware) - ULTRA OPTIMIZE EDILDI
     */
    private void placeBlocksInBatches(World world, List<BlockPlacement> blocks, Runnable callback) {
        placeBlocksInBatches(world, blocks, callback, false);
    }

    /**
     * Blokları batch'ler halinde yerleştirir (chunk-aware) - ULTRA OPTIMIZE EDILDI
     * @param allowReplacement Eğer true ise mevcut blokları değiştirir (ore'lar için)
     */
    private void placeBlocksInBatches(World world, List<BlockPlacement> blocks, Runnable callback, boolean allowReplacement) {
        if (blocks.isEmpty()) {
            if (callback != null) callback.run();
            return;
        }

        int batchSize = Math.max(100, plugin.getConfigManager().getBlocksPerBatch()); // Minimum 100 blok/batch
        int totalBatches = (blocks.size() + batchSize - 1) / batchSize;

        // PERFORMANCE: Chunk'ları daha akıllı yükle - sadece gerekli chunk'ları
        Set<ChunkPos> chunksToLoad = new HashSet<>();
        for (BlockPlacement bp : blocks) {
            chunksToLoad.add(new ChunkPos(bp.x >> 4, bp.z >> 4));
        }

        // PERFORMANCE: Chunk yükleme - paralel ve daha hızlı
        loadChunksAsync(world, chunksToLoad).thenRun(() -> {
            // PERFORMANCE: Batch placement - daha sık batch'ler ve daha hızlı
            int batchesPerTick = Math.max(2, plugin.getConfigManager().getBatchesPerTick()); // Minimum 2 batch/tick
            int totalDelay = 0;

            for (int i = 0; i < totalBatches; i++) {
                int start = i * batchSize;
                int end = Math.min(start + batchSize, blocks.size());
                List<BlockPlacement> batch = blocks.subList(start, end);

                // PERFORMANCE: Daha sık batch'ler için delay hesapla
                if (i % batchesPerTick == 0) {
                    totalDelay++;
                }

                final int currentDelay = totalDelay;

                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    // PERFORMANCE: Bulk block placement - daha hızlı setType
                    for (BlockPlacement bp : batch) {
                        try {
                            Block block = world.getBlockAt(bp.x, bp.y, bp.z);
                            if (allowReplacement || block.getType() == Material.AIR || block.getType() == Material.CAVE_AIR) {
                                block.setType(bp.material, false);
                            }
                        } catch (Exception e) {
                            // Chunk yüklü değilse skip - daha az log
                        }
                    }
                }, currentDelay);
            }

            // PERFORMANCE: Callback'i daha erken çağır - tüm batch'lerin %80'i tamamlandıktan sonra
            if (callback != null) {
                int callbackDelay = Math.max(1, totalDelay / 2);
                Bukkit.getScheduler().runTaskLater(plugin, callback, callbackDelay);
            }
        });
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

                // Delete planet data folder
                plugin.getPlanetDataManager().deletePlanetData(orb.getId());

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
     * Güvenli ilk teleport konumu - gezegen oluşurken güvenli yüzey bulma
     */
    private Location findSafeInitialTeleportLocation(World world, int cx, int cy, int cz, int radius) {
        // Gezegenin üst kısmından başlayarak aşağı doğru tara - find first solid block
        // Daha geniş arama alanı ve daha güvenilir algoritma
        for (int y = cy + radius + 5; y >= cy - radius; y--) {
            // Spiral search pattern for better coverage
            for (int searchRadius = 0; searchRadius <= 8; searchRadius++) {
                for (int angle = 0; angle < 360; angle += 45) {
                    double radian = Math.toRadians(angle);
                    int x = cx + (int)(Math.cos(radian) * searchRadius);
                    int z = cz + (int)(Math.sin(radian) * searchRadius);

                    // Check if this position is on the planet surface
                    double distanceFromCenter = Math.sqrt((x - cx) * (x - cx) + (z - cz) * (z - cz));
                    if (distanceFromCenter > radius + 2) continue; // Too far from planet

                    Block block = world.getBlockAt(x, y, z);
                    Block below = world.getBlockAt(x, y - 1, z);
                    Block twoBelow = world.getBlockAt(x, y - 2, z);

                    // Katı blok üzerinde hava varsa güvenli konum
                    if (block.getType() == Material.AIR &&
                        below.getType().isSolid() &&
                        twoBelow.getType().isSolid() &&
                        !isDangerousBlock(below.getType()) &&
                        !isDangerousBlock(twoBelow.getType())) {

                        // Extra check: make sure there's enough space above
                        boolean hasSpace = true;
                        for (int checkY = y + 1; checkY <= y + 2; checkY++) {
                            if (world.getBlockAt(x, checkY, z).getType() != Material.AIR) {
                                hasSpace = false;
                                break;
                            }
                        }

                        if (hasSpace) {
                            return new Location(world, x + 0.5, y, z + 0.5);
                        }
                    }
                }
            }
        }

        // Fallback: Try center top
        for (int y = cy + radius + 5; y >= cy + radius - 10; y--) {
            Block block = world.getBlockAt(cx, y, cz);
            Block below = world.getBlockAt(cx, y - 1, cz);

            if (block.getType() == Material.AIR && below.getType().isSolid() && !isDangerousBlock(below.getType())) {
                return new Location(world, cx + 0.5, y, cz + 0.5);
            }
        }

        // Ultimate fallback
        return new Location(world, cx, cy + radius + 10, cz);
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

    /**
     * Create a visual progress bar
     */
    private String createProgressBar(int percent) {
        int barLength = 20; // Length of the progress bar
        int filled = (percent * barLength) / 100;
        StringBuilder bar = new StringBuilder("[");

        for (int i = 0; i < barLength; i++) {
            if (i < filled) {
                bar.append("█"); // Filled character
            } else {
                bar.append("░"); // Empty character
            }
        }
        bar.append("]");

        return bar.toString();
    }

    /**
     * Get descriptive step name for progress display
     */
    private String getStepName(int currentStep, int totalSteps) {
        if (currentStep <= 1) return "🌍 Başlatılıyor...";
        if (currentStep == 2) return "📍 Konum belirleniyor...";
        if (currentStep == 3) return "🎨 Gezegen tasarlanıyor...";
        if (currentStep == 4) return "✨ Özellikler ekleniyor...";
        if (currentStep == 5) return "💾 Veritabanına kaydediliyor...";
        if (currentStep == 6) return "⚡ Işınlanılıyor...";
        if (currentStep == 7) return "🏗️ Gezegen kabuğu oluşturuluyor...";
        if (currentStep == 8) return "🔨 Gezegen şekilleniyor...";

        // Dynamic steps based on enabled features
        int baseSteps = 8;
        int stepIndex = currentStep - baseSteps;

        if (plugin.getConfigManager().isOreGenerationEnabled() && stepIndex == 1) {
            return "⛏️ Madenler yerleştiriliyor...";
        }
        if (plugin.getConfigManager().isTreeGenerationEnabled() && stepIndex == 2) {
            return "🌳 Ağaçlar dikiliyor...";
        }
        if (plugin.getConfigManager().isStructureGenerationEnabled() && stepIndex == 3) {
            return "🏛️ Yapılar inşa ediliyor...";
        }
        if (plugin.getConfigManager().isTreasureGenerationEnabled() && stepIndex == 4) {
            return "💰 Hazineler gizleniyor...";
        }
        if (stepIndex == 5) return "🏰 Zindanlar kazılıyor...";
        if (stepIndex == 6) return "🌌 Yüzey detayları ekleniyor...";
        if (stepIndex == 7) return "💫 Halkalar oluşturuluyor...";
        if (plugin.getConfigManager().isAsteroidsEnabled() && stepIndex == 8) {
            return "☄️ Asteroidler yerleştiriliyor...";
        }
        if (plugin.getConfigManager().isSatellitesEnabled() && stepIndex == 9) {
            return "🛰️ Uydular konumlandırılıyor...";
        }
        if (stepIndex >= 10) return "✅ Son düzenlemeler yapılıyor...";

        return "🔄 İşleniyor...";
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

    /**
     * Add random variations for extra diversity
     */
    private void addRandomVariations(Orb orb, java.util.Set<com.skyorbs.modifiers.PlanetModifier> modifiers, Random random) {
        // Random size variation (±10%)
        double sizeMultiplier = 0.9 + random.nextDouble() * 0.2;
        orb.setRadius((int)(orb.getRadius() * sizeMultiplier));

        // Random palette variation (swap to similar palette sometimes)
        if (random.nextDouble() < 0.1) { // 10% chance
            String currentPalette = orb.getPaletteId();
            if (currentPalette != null) {
                // Swap to a random different palette
                com.skyorbs.palettes.PlanetPalette newPalette = plugin.getPaletteRegistry().getRandomPalette(random);
                orb.setPaletteId(newPalette.getId());
            }
        }

        // Random biome variation
        if (random.nextDouble() < 0.15) { // 15% chance
            BiomeType newBiome = BiomeType.getRandomBiomeWeighted(random);
            orb.setBiomeName(newBiome.name());
        }

        // Random modifier additions (small chance for extra modifiers)
        if (random.nextDouble() < 0.05) { // 5% chance for extra modifier
            com.skyorbs.modifiers.PlanetModifier[] allMods = com.skyorbs.modifiers.PlanetModifier.values();
            com.skyorbs.modifiers.PlanetModifier extraMod = allMods[random.nextInt(allMods.length)];
            modifiers.add(extraMod);
        }

        // Random shape variation (slight deformation)
        if (random.nextDouble() < 0.2) { // 20% chance
            // This would require shape modification logic
            // For now, just ensure minimum radius
            if (orb.getRadius() < 20) {
                orb.setRadius(20 + random.nextInt(10));
            }
        }
    }

    /**
     * Result class for planet generation containing blocks and first block
     */
    private static class PlanetGenerationResult {
        List<BlockPlacement> blocks;
        BlockPlacement firstBlock;

        PlanetGenerationResult(List<BlockPlacement> blocks, BlockPlacement firstBlock) {
            this.blocks = blocks;
            this.firstBlock = firstBlock;
        }
    }
}