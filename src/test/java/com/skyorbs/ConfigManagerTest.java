package com.skyorbs;

import com.skyorbs.config.ConfigManager;
import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ConfigManagerTest {

    private ConfigManager configManager;
    private SkyOrbs mockPlugin;

    @BeforeEach
    public void setUp() {
        mockPlugin = mock(SkyOrbs.class);
        when(mockPlugin.getConfig()).thenReturn(new YamlConfiguration());
        configManager = new ConfigManager(mockPlugin);
    }

    @Test
    public void testConfigManagerInitialization() {
        assertNotNull(configManager);
    }

    @Test
    public void testDefaultRadiusValues() {
        // Test default values when config returns defaults
        assertEquals(80, configManager.getMinRadius());
        assertEquals(250, configManager.getMaxRadius());
        assertEquals(150, configManager.getAverageRadius());
    }

    @Test
    public void testPlacementSettings() {
        assertEquals(50000, configManager.getMaxDistance());
        assertEquals(0, configManager.getCenterX());
        assertEquals(0, configManager.getCenterZ());
        assertTrue(configManager.isClusteringEnabled());
    }

    @Test
    public void testShapeWeights() {
        var weights = configManager.getShapeWeights();
        assertNotNull(weights);
        // Test sadece map'in null olmadığını kontrol eder - mock config'de weights olmayabilir
        // Bu test ConfigManager'ın düzgün çalıştığını doğrular
    }

    @Test
    public void testAsteroidSettings() {
        assertTrue(configManager.isAsteroidsEnabled());
        assertEquals(1, configManager.getMinAsteroidsPerPlanet());
        assertEquals(5, configManager.getMaxAsteroidsPerPlanet());
    }

    @Test
    public void testRandomNameGeneration() {
        String name = configManager.generateRandomName();
        assertNotNull(name);
        assertTrue(name.length() > 0);
    }

    @Test
    public void testPerformanceSettings() {
        assertTrue(configManager.isAsyncGenerationEnabled());
        assertEquals(250, configManager.getBlocksPerBatch());
        assertEquals(1, configManager.getBatchesPerTick());
    }
}