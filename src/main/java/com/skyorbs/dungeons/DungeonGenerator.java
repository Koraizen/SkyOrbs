package com.skyorbs.dungeons;

import com.skyorbs.SkyOrbs;
import com.skyorbs.core.Orb;
import com.skyorbs.core.PlanetType;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.noise.SimplexOctaveGenerator;

import java.util.*;

public class DungeonGenerator {

    private final SkyOrbs plugin;
    private final Random random = new Random();

    public DungeonGenerator(SkyOrbs plugin) {
        this.plugin = plugin;
    }

    /**
     * Generate mini-dungeons within planets
     */
    public List<DungeonRoom> generateDungeons(Orb orb, int count) {
        List<DungeonRoom> dungeons = new ArrayList<>();
        int cx = orb.getCenterX();
        int cy = orb.getCenterY();
        int cz = orb.getCenterZ();
        int radius = orb.getRadius();
        long seed = orb.getSeed();

        SimplexOctaveGenerator dungeonNoise = new SimplexOctaveGenerator(seed + 500, 4);
        dungeonNoise.setScale(0.01);

        for (int i = 0; i < count; i++) {
            // Find suitable location for dungeon
            int attempts = 0;
            boolean placed = false;

            while (!placed && attempts < 50) {
                // Generate position within planet
                double angle = random.nextDouble() * 2 * Math.PI;
                double distance = random.nextDouble() * (radius * 0.8); // Keep away from edges

                int x = cx + (int)(Math.cos(angle) * distance);
                int z = cz + (int)(Math.sin(angle) * distance);
                int y = cy + random.nextInt(radius * 2) - radius;

                // Check if location is valid (inside planet, not too close to surface)
                double distFromCenter = Math.sqrt((x - cx) * (x - cx) + (y - cy) * (y - cy) + (z - cz) * (z - cz));
                if (distFromCenter <= radius * 0.9 && distFromCenter >= radius * 0.3) {
                    DungeonRoom dungeon = generateDungeonRoom(orb, x, y, z, seed + i);
                    if (dungeon != null) {
                        dungeons.add(dungeon);
                        placed = true;
                    }
                }
                attempts++;
            }
        }

        return dungeons;
    }

    private DungeonRoom generateDungeonRoom(Orb orb, int x, int y, int z, long seed) {
        Random roomRandom = new Random(seed);
        DungeonType type = getDungeonTypeForPlanet(orb.getPlanetType(), roomRandom);

        // Room size based on dungeon type
        int width = type.baseSize + roomRandom.nextInt(3);
        int height = type.baseSize + roomRandom.nextInt(2);
        int length = type.baseSize + roomRandom.nextInt(3);

        // Create room bounds
        int minX = x - width/2;
        int maxX = x + width/2;
        int minY = y - height/2;
        int maxY = y + height/2;
        int minZ = z - length/2;
        int maxZ = z + length/2;

        DungeonRoom room = new DungeonRoom(type, minX, minY, minZ, maxX, maxY, maxZ);

        // Generate room structure
        generateRoomStructure(orb, room, roomRandom);

        // Add loot
        generateLoot(orb, room, roomRandom);

        // Add mobs if applicable
        generateMobs(room, roomRandom);

        return room;
    }

    private void generateRoomStructure(Orb orb, DungeonRoom room, Random random) {
        World world = plugin.getServer().getWorld(orb.getWorldName());
        if (world == null) return;

        // Clear area and create walls/floor/ceiling
        for (int x = room.minX; x <= room.maxX; x++) {
            for (int y = room.minY; y <= room.maxY; y++) {
                for (int z = room.minZ; z <= room.maxZ; z++) {
                    Block block = world.getBlockAt(x, y, z);
                    Material material;

                    if (x == room.minX || x == room.maxX || z == room.minZ || z == room.maxZ) {
                        // Walls
                        material = getWallMaterial(room.type, random);
                    } else if (y == room.minY) {
                        // Floor
                        material = getFloorMaterial(room.type, random);
                    } else if (y == room.maxY) {
                        // Ceiling
                        material = getCeilingMaterial(room.type, random);
                    } else {
                        // Air
                        material = Material.AIR;
                    }

                    block.setType(material, false);
                }
            }
        }

        // Add decorative elements
        addDecorations(orb, room, random);
    }

    private void addDecorations(Orb orb, DungeonRoom room, Random random) {
        World world = plugin.getServer().getWorld(orb.getWorldName());
        if (world == null) return;

        int decorationCount = random.nextInt(5) + 3;

        for (int i = 0; i < decorationCount; i++) {
            int x = room.minX + 1 + random.nextInt(room.maxX - room.minX - 1);
            int z = room.minZ + 1 + random.nextInt(room.maxZ - room.minZ - 1);
            int y = room.minY + 1;

            Block floorBlock = world.getBlockAt(x, y, z);
            if (floorBlock.getType() == Material.AIR) {
                Material decoration = getDecorationMaterial(room.type, random);
                floorBlock.setType(decoration, false);
            }
        }
    }

    private void generateLoot(Orb orb, DungeonRoom room, Random random) {
        World world = plugin.getServer().getWorld(orb.getWorldName());
        if (world == null) return;

        // Place treasure chest in center
        int centerX = (room.minX + room.maxX) / 2;
        int centerZ = (room.minZ + room.maxZ) / 2;
        int chestY = room.minY + 1;

        Block chestBlock = world.getBlockAt(centerX, chestY, centerZ);
        chestBlock.setType(Material.CHEST, false);

        if (chestBlock.getState() instanceof Chest chest) {
            List<ItemStack> loot = generateLootItems(room.type, orb.getCoreLevel(), random);
            for (int i = 0; i < Math.min(loot.size(), 27); i++) {
                chest.getInventory().setItem(i, loot.get(i));
            }
        }
    }

    private List<ItemStack> generateLootItems(DungeonType type, int coreLevel, Random random) {
        List<ItemStack> loot = new ArrayList<>();

        // Base loot based on dungeon type
        switch (type) {
            case CRYSTAL_CAVERN -> {
                loot.add(new ItemStack(Material.DIAMOND, random.nextInt(3) + 1));
                loot.add(new ItemStack(Material.EMERALD, random.nextInt(2) + 1));
                if (coreLevel >= 3) {
                    loot.add(new ItemStack(Material.AMETHYST_SHARD, random.nextInt(5) + 3));
                }
            }
            case LAVA_CHAMBER -> {
                loot.add(new ItemStack(Material.GOLD_INGOT, random.nextInt(4) + 2));
                loot.add(new ItemStack(Material.NETHERITE_INGOT, random.nextInt(2)));
                if (coreLevel >= 4) {
                    loot.add(new ItemStack(Material.ANCIENT_DEBRIS, random.nextInt(3) + 1));
                }
            }
            case ICE_TOMB -> {
                loot.add(new ItemStack(Material.DIAMOND, random.nextInt(2) + 1));
                if (coreLevel >= 2) {
                    loot.add(new ItemStack(Material.BLUE_ICE, random.nextInt(8) + 4));
                }
            }
            case SHADOW_VAULT -> {
                loot.add(new ItemStack(Material.OBSIDIAN, random.nextInt(16) + 8));
                if (coreLevel >= 5) {
                    loot.add(new ItemStack(Material.CRYING_OBSIDIAN, random.nextInt(4) + 1));
                }
            }
            case TOXIC_LAB -> {
                loot.add(new ItemStack(Material.SLIME_BALL, random.nextInt(12) + 6));
                if (coreLevel >= 3) {
                    loot.add(new ItemStack(Material.HONEY_BOTTLE, random.nextInt(3) + 1));
                }
            }
        }

        // Add common loot
        loot.add(new ItemStack(Material.IRON_INGOT, random.nextInt(8) + 4));
        loot.add(new ItemStack(Material.GOLD_INGOT, random.nextInt(4) + 2));

        // Add experience bottles based on core level
        int expBottles = Math.min(coreLevel, 3);
        for (int i = 0; i < expBottles; i++) {
            loot.add(new ItemStack(Material.EXPERIENCE_BOTTLE, random.nextInt(3) + 1));
        }

        return loot;
    }

    private void generateMobs(DungeonRoom room, Random random) {
        // This would integrate with Minecraft's mob spawning system
        // For now, we'll just mark that mobs should spawn here
        room.hasMobs = random.nextDouble() < 0.7; // 70% chance of mobs
    }

    /**
     * Generate core crystal at planet center
     */
    public void generateCoreCrystal(Orb orb) {
        World world = plugin.getServer().getWorld(orb.getWorldName());
        if (world == null) return;

        int cx = orb.getCenterX();
        int cy = orb.getCenterY();
        int cz = orb.getCenterZ();

        // Create crystal structure
        int crystalSize = Math.min(3, orb.getCoreLevel());

        for (int x = -crystalSize; x <= crystalSize; x++) {
            for (int y = -crystalSize; y <= crystalSize; y++) {
                for (int z = -crystalSize; z <= crystalSize; z++) {
                    double distance = Math.sqrt(x * x + y * y + z * z);
                    if (distance <= crystalSize) {
                        Block block = world.getBlockAt(cx + x, cy + y, cz + z);
                        if (distance < crystalSize * 0.5) {
                            block.setType(Material.AMETHYST_BLOCK, false);
                        } else {
                            block.setType(Material.BUDDING_AMETHYST, false);
                        }
                    }
                }
            }
        }

        // Create chest with special loot
        Block chestBlock = world.getBlockAt(cx, cy + crystalSize + 1, cz);
        chestBlock.setType(Material.CHEST, false);

        if (chestBlock.getState() instanceof Chest chest) {
            List<ItemStack> specialLoot = generateCoreLoot(orb);
            for (int i = 0; i < Math.min(specialLoot.size(), 27); i++) {
                chest.getInventory().setItem(i, specialLoot.get(i));
            }
        }
    }

    private List<ItemStack> generateCoreLoot(Orb orb) {
        List<ItemStack> loot = new ArrayList<>();
        Random random = new Random(orb.getSeed() + 999);

        // Legendary items based on core level
        if (orb.getCoreLevel() >= 5) {
            loot.add(new ItemStack(Material.NETHERITE_INGOT, random.nextInt(3) + 1));
            loot.add(new ItemStack(Material.DIAMOND_BLOCK, random.nextInt(2) + 1));
        }

        if (orb.getCoreLevel() >= 7) {
            loot.add(new ItemStack(Material.ELYTRA, 1));
        }

        if (orb.getCoreLevel() >= 10) {
            loot.add(new ItemStack(Material.NETHER_STAR, 1));
        }

        // Planet-specific legendary items
        switch (orb.getPlanetType()) {
            case CRYSTAL -> loot.add(new ItemStack(Material.AMETHYST_BLOCK, random.nextInt(16) + 8));
            case LAVA -> loot.add(new ItemStack(Material.ANCIENT_DEBRIS, random.nextInt(8) + 4));
            case ICE -> loot.add(new ItemStack(Material.BLUE_ICE, random.nextInt(32) + 16));
            case SHADOW -> loot.add(new ItemStack(Material.CRYING_OBSIDIAN, random.nextInt(12) + 6));
            case TOXIC -> loot.add(new ItemStack(Material.SLIME_BLOCK, random.nextInt(24) + 12));
        }

        return loot;
    }

    // Material getters
    private Material getWallMaterial(DungeonType type, Random random) {
        return switch (type) {
            case CRYSTAL_CAVERN -> random.nextDouble() < 0.8 ? Material.AMETHYST_BLOCK : Material.QUARTZ_BLOCK;
            case LAVA_CHAMBER -> random.nextDouble() < 0.7 ? Material.NETHERRACK : Material.MAGMA_BLOCK;
            case ICE_TOMB -> random.nextDouble() < 0.9 ? Material.ICE : Material.PACKED_ICE;
            case SHADOW_VAULT -> Material.OBSIDIAN;
            case TOXIC_LAB -> Material.GREEN_CONCRETE;
        };
    }

    private Material getFloorMaterial(DungeonType type, Random random) {
        return switch (type) {
            case CRYSTAL_CAVERN -> Material.QUARTZ_BLOCK;
            case LAVA_CHAMBER -> Material.MAGMA_BLOCK;
            case ICE_TOMB -> Material.PACKED_ICE;
            case SHADOW_VAULT -> Material.CRYING_OBSIDIAN;
            case TOXIC_LAB -> Material.SLIME_BLOCK;
        };
    }

    private Material getCeilingMaterial(DungeonType type, Random random) {
        return getWallMaterial(type, random);
    }

    private Material getDecorationMaterial(DungeonType type, Random random) {
        return switch (type) {
            case CRYSTAL_CAVERN -> Material.AMETHYST_CLUSTER;
            case LAVA_CHAMBER -> Material.LAVA;
            case ICE_TOMB -> Material.SNOW;
            case SHADOW_VAULT -> Material.SOUL_LANTERN;
            case TOXIC_LAB -> Material.GREEN_WOOL;
        };
    }

    private DungeonType getDungeonTypeForPlanet(PlanetType planetType, Random random) {
        return switch (planetType) {
            case TERRESTRIAL -> DungeonType.values()[random.nextInt(DungeonType.values().length)];
            case GAS -> DungeonType.CRYSTAL_CAVERN; // Gas planets have crystal formations
            case LAVA -> DungeonType.LAVA_CHAMBER;
            case ICE -> DungeonType.ICE_TOMB;
            case CRYSTAL -> DungeonType.CRYSTAL_CAVERN;
            case SHADOW -> DungeonType.SHADOW_VAULT;
            case TOXIC -> DungeonType.TOXIC_LAB;
        };
    }

    public enum DungeonType {
        CRYSTAL_CAVERN(5, "Kristal Mağarası"),
        LAVA_CHAMBER(6, "Lava Odası"),
        ICE_TOMB(4, "Buz Mezarlığı"),
        SHADOW_VAULT(5, "Gölge Kasası"),
        TOXIC_LAB(5, "Zehirli Laboratuvar");

        final int baseSize;
        final String displayName;

        DungeonType(int baseSize, String displayName) {
            this.baseSize = baseSize;
            this.displayName = displayName;
        }

        public String getDisplayName() { return displayName; }
    }

    public static class DungeonRoom {
        public final DungeonType type;
        public final int minX, minY, minZ, maxX, maxY, maxZ;
        public boolean hasMobs;

        public DungeonRoom(DungeonType type, int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
            this.type = type;
            this.minX = minX;
            this.minY = minY;
            this.minZ = minZ;
            this.maxX = maxX;
            this.maxY = maxY;
            this.maxZ = maxZ;
            this.hasMobs = false;
        }
    }
}