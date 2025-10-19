package com.skyorbs.storage;

import com.skyorbs.SkyOrbs;
import com.skyorbs.core.Orb;
import com.skyorbs.core.PlanetType;
import com.skyorbs.modifiers.PlanetModifier;
import com.skyorbs.atmosphere.AtmosphereType;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.sql.*;
import java.util.*;

public class DatabaseManager {
    
    private final SkyOrbs plugin;
    private Connection connection;
    private final String dbPath;
    private final Gson gson = new Gson(); // For JSON serialization
    
    // Database version for migration
    private static final int CURRENT_VERSION = 2;
    
    public DatabaseManager(SkyOrbs plugin) {
        this.plugin = plugin;
        this.dbPath = plugin.getDataFolder() + "/data.db";
    }
    
    public void initialize() throws SQLException {
        File dataFolder = plugin.getDataFolder();
        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }

        connection = DriverManager.getConnection("jdbc:sqlite:" + dbPath);

        // Check database version and migrate if needed
        int currentVersion = getDatabaseVersion();

        // Special case: if version is 1 but orbs table doesn't exist, it's a new database
        if (currentVersion == 1 && !tableExists("orbs")) {
            plugin.logInfo("New database detected, setting version to " + CURRENT_VERSION);
            setDatabaseVersion(CURRENT_VERSION);
            currentVersion = CURRENT_VERSION;
        }

        if (currentVersion < CURRENT_VERSION) {
            plugin.logInfo("Database migration needed: v" + currentVersion + " -> v" + CURRENT_VERSION);
            migrateDatabase(currentVersion);
        }

        createTables();
    }
    
    /**
     * Get current database version
     */
    private int getDatabaseVersion() throws SQLException {
        // Create version table if not exists
        String createVersionTable = """
            CREATE TABLE IF NOT EXISTS schema_version (
                version INTEGER PRIMARY KEY
            )
        """;
        
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(createVersionTable);
        }
        
        // Get version
        String query = "SELECT version FROM schema_version LIMIT 1";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            if (rs.next()) {
                return rs.getInt("version");
            }
        } catch (SQLException e) {
            // Version table exists but empty - v1
            return 1;
        }
        
        // No version table - v1
        return 1;
    }
    
    /**
     * Update database version
     */
    private void setDatabaseVersion(int version) throws SQLException {
        String sql = "INSERT OR REPLACE INTO schema_version (version) VALUES (?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, version);
            pstmt.executeUpdate();
        }
    }
    
    /**
     * Migrate database from old version to new
     */
    private void migrateDatabase(int fromVersion) throws SQLException {
        plugin.logInfo("Starting database migration from version " + fromVersion + "...");
        
        if (fromVersion == 1) {
            migrateV1ToV2();
        }
        
        setDatabaseVersion(CURRENT_VERSION);
        plugin.logSuccess("✓ Database migration completed!");
    }
    
    /**
     * Migrate from v1 to v2 (add new columns)
     */
    private void migrateV1ToV2() throws SQLException {
        plugin.logInfo("Migrating v1 -> v2: Adding new planet features...");
        
        List<String> alterStatements = Arrays.asList(
            "ALTER TABLE orbs ADD COLUMN palette_id TEXT DEFAULT NULL",
            "ALTER TABLE orbs ADD COLUMN modifiers TEXT DEFAULT NULL", // JSON array
            "ALTER TABLE orbs ADD COLUMN atmosphere TEXT DEFAULT 'CLEAR'",
            "ALTER TABLE orbs ADD COLUMN planet_type TEXT DEFAULT 'TERRESTRIAL'",
            "ALTER TABLE orbs ADD COLUMN core_level INTEGER DEFAULT 1",
            "ALTER TABLE orbs ADD COLUMN energy_level REAL DEFAULT 100.0",
            "ALTER TABLE orbs ADD COLUMN xp BIGINT DEFAULT 0",
            "ALTER TABLE orbs ADD COLUMN biosphere_level INTEGER DEFAULT 1",
            "ALTER TABLE orbs ADD COLUMN ecological_balance REAL DEFAULT 1.0"
        );
        
        try (Statement stmt = connection.createStatement()) {
            for (String sql : alterStatements) {
                try {
                    stmt.execute(sql);
                    plugin.logInfo("  ✓ " + sql.substring(0, 50) + "...");
                } catch (SQLException e) {
                    // Column might already exist - skip
                    if (!e.getMessage().contains("duplicate column")) {
                        throw e;
                    }
                }
            }
        }
    }
    
    private void createTables() throws SQLException {
        // Updated orbs table with all new columns
        String orbsTable = """
            CREATE TABLE IF NOT EXISTS orbs (
                id TEXT PRIMARY KEY,
                name TEXT NOT NULL,
                world TEXT NOT NULL,
                centerX INTEGER NOT NULL,
                centerY INTEGER NOT NULL,
                centerZ INTEGER NOT NULL,
                radius INTEGER NOT NULL,
                shape TEXT NOT NULL,
                biome TEXT NOT NULL,
                seed BIGINT NOT NULL,
                created_at BIGINT NOT NULL,
                is_asteroid BOOLEAN DEFAULT 0,
                parent_id TEXT,
                
                -- NEW COLUMNS (v2)
                palette_id TEXT DEFAULT NULL,
                modifiers TEXT DEFAULT NULL,
                atmosphere TEXT DEFAULT 'CLEAR',
                planet_type TEXT DEFAULT 'TERRESTRIAL',
                core_level INTEGER DEFAULT 1,
                energy_level REAL DEFAULT 100.0,
                xp BIGINT DEFAULT 0,
                biosphere_level INTEGER DEFAULT 1,
                ecological_balance REAL DEFAULT 1.0,
                
                FOREIGN KEY (parent_id) REFERENCES orbs(id) ON DELETE CASCADE
            )
        """;
        
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(orbsTable);
        }
    }
    
    public void saveOrb(Orb orb) throws SQLException {
        String sql = """
            INSERT OR REPLACE INTO orbs 
            (id, name, world, centerX, centerY, centerZ, radius, shape, biome, seed, created_at, 
             is_asteroid, parent_id, palette_id, modifiers, atmosphere, planet_type, 
             core_level, energy_level, xp, biosphere_level, ecological_balance)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, orb.getId().toString());
            pstmt.setString(2, orb.getName());
            pstmt.setString(3, orb.getWorldName());
            pstmt.setInt(4, orb.getCenterX());
            pstmt.setInt(5, orb.getCenterY());
            pstmt.setInt(6, orb.getCenterZ());
            pstmt.setInt(7, orb.getRadius());
            pstmt.setString(8, orb.getShapeName());
            pstmt.setString(9, orb.getBiomeName());
            pstmt.setLong(10, orb.getSeed());
            pstmt.setLong(11, orb.getCreatedAt());
            pstmt.setBoolean(12, orb.isAsteroid());
            pstmt.setString(13, orb.getParentId() != null ? orb.getParentId().toString() : null);
            
            // NEW: Save advanced features
            pstmt.setString(14, orb.getPaletteId());
            pstmt.setString(15, serializeModifiers(orb.getModifiers()));
            pstmt.setString(16, orb.getAtmosphere().name());
            pstmt.setString(17, orb.getPlanetType().name());
            pstmt.setInt(18, orb.getCoreLevel());
            pstmt.setDouble(19, orb.getEnergyLevel());
            pstmt.setLong(20, orb.getXp());
            pstmt.setInt(21, orb.getBiosphereLevel());
            pstmt.setDouble(22, orb.getEcologicalBalance());
            
            pstmt.executeUpdate();
        }
    }
    
    public Orb getOrb(UUID id) throws SQLException {
        String sql = "SELECT * FROM orbs WHERE id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, id.toString());
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToOrb(rs);
            }
        }
        return null;
    }
    
    public Orb getOrbByName(String name) throws SQLException {
        String sql = "SELECT * FROM orbs WHERE name = ? COLLATE NOCASE";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, name);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToOrb(rs);
            }
        }
        return null;
    }
    
    public List<Orb> getAllOrbs() throws SQLException {
        List<Orb> orbs = new ArrayList<Orb>();
        String sql = "SELECT * FROM orbs WHERE is_asteroid = 0 ORDER BY created_at DESC";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                orbs.add(mapResultSetToOrb(rs));
            }
        }
        return orbs;
    }
    
    public List<Orb> getChildOrbs(UUID planetId) throws SQLException {
        List<Orb> children = new ArrayList<Orb>();
        String sql = "SELECT * FROM orbs WHERE parent_id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, planetId.toString());
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                children.add(mapResultSetToOrb(rs));
            }
        }
        return children;
    }
    
    public void deleteOrb(UUID id) throws SQLException {
        String sql = "DELETE FROM orbs WHERE id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, id.toString());
            pstmt.executeUpdate();
        }
    }
    
    /**
     * Map database row to Orb object (with new features)
     */
    private Orb mapResultSetToOrb(ResultSet rs) throws SQLException {
        UUID id = UUID.fromString(rs.getString("id"));
        String name = rs.getString("name");
        String world = rs.getString("world");
        int centerX = rs.getInt("centerX");
        int centerY = rs.getInt("centerY");
        int centerZ = rs.getInt("centerZ");
        int radius = rs.getInt("radius");
        String shape = rs.getString("shape");
        String biome = rs.getString("biome");
        long seed = rs.getLong("seed");
        long createdAt = rs.getLong("created_at");
        boolean isAsteroid = rs.getBoolean("is_asteroid");
        
        String parentIdStr = rs.getString("parent_id");
        UUID parentId = parentIdStr != null ? UUID.fromString(parentIdStr) : null;
        
        Orb orb = new Orb(id, name, world, centerX, centerY, centerZ, radius, 
                         shape, biome, seed, createdAt, isAsteroid, parentId);
        
        // Load advanced features (with null checks for backward compatibility)
        try {
            String paletteId = rs.getString("palette_id");
            if (paletteId != null) {
                orb.setPaletteId(paletteId);
            }
            
            String modifiersJson = rs.getString("modifiers");
            if (modifiersJson != null) {
                orb.setModifiers(deserializeModifiers(modifiersJson));
            }
            
            String atmosphereStr = rs.getString("atmosphere");
            if (atmosphereStr != null) {
                orb.setAtmosphere(AtmosphereType.valueOf(atmosphereStr));
            }
            
            String planetTypeStr = rs.getString("planet_type");
            if (planetTypeStr != null) {
                orb.setPlanetType(PlanetType.valueOf(planetTypeStr));
            }
            
            orb.setCoreLevel(rs.getInt("core_level"));
            orb.setEnergyLevel(rs.getDouble("energy_level"));
            orb.addXp(rs.getLong("xp") - orb.getXp()); // Set XP correctly
            orb.setBiosphereLevel(rs.getInt("biosphere_level"));
            orb.setEcologicalBalance(rs.getDouble("ecological_balance"));
            
        } catch (SQLException e) {
            // Columns don't exist (old database) - use defaults
            plugin.logWarning("Old database format detected, using default values for planet: " + name);
        }
        
        return orb;
    }
    
    /**
     * Serialize modifiers to JSON
     */
    private String serializeModifiers(Set<PlanetModifier> modifiers) {
        if (modifiers == null || modifiers.isEmpty()) {
            return null;
        }
        
        List<String> modifierNames = modifiers.stream()
            .map(PlanetModifier::name)
            .toList();
        
        return gson.toJson(modifierNames);
    }
    
    /**
     * Deserialize modifiers from JSON
     */
    private Set<PlanetModifier> deserializeModifiers(String json) {
        if (json == null || json.isEmpty()) {
            return new HashSet<PlanetModifier>();
        }
        
        try {
            List<String> modifierNames = gson.fromJson(json, new TypeToken<List<String>>(){}.getType());
            Set<PlanetModifier> modifiers = new HashSet<PlanetModifier>();
            
            for (String name : modifierNames) {
                try {
                    modifiers.add(PlanetModifier.valueOf(name));
                } catch (IllegalArgumentException e) {
                    plugin.logWarning("Unknown modifier: " + name);
                }
            }
            
            return modifiers;
        } catch (Exception e) {
            plugin.logError("Failed to deserialize modifiers", e);
            return new HashSet<PlanetModifier>();
        }
    }
    
    /**
     * Check if a table exists
     */
    private boolean tableExists(String tableName) throws SQLException {
        String sql = "SELECT name FROM sqlite_master WHERE type='table' AND name=?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, tableName);
            ResultSet rs = pstmt.executeQuery();
            return rs.next();
        }
    }

    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            plugin.logError("Veritabanı kapatılırken hata!", e);
        }
    }
}
