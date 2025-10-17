package com.skyorbs.storage;

import com.skyorbs.SkyOrbs;
import com.skyorbs.core.Orb;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DatabaseManager {
    
    private final SkyOrbs plugin;
    private Connection connection;
    private final String dbPath;
    
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
        createTables();
    }
    
    private void createTables() throws SQLException {
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
            (id, name, world, centerX, centerY, centerZ, radius, shape, biome, seed, created_at, is_asteroid, parent_id)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
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
        List<Orb> orbs = new ArrayList<>();
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
        List<Orb> children = new ArrayList<>();
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
        
        return new Orb(id, name, world, centerX, centerY, centerZ, radius, shape, biome, seed, createdAt, isAsteroid, parentId);
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
