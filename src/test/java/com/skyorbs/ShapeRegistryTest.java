package com.skyorbs;

import com.skyorbs.shapes.PlanetShape;
import com.skyorbs.shapes.ShapeRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ShapeRegistryTest {

    private ShapeRegistry shapeRegistry;

    @BeforeEach
    public void setUp() {
        shapeRegistry = new ShapeRegistry();
        shapeRegistry.registerAllShapes();
    }

    @Test
    public void testShapeRegistryInitialization() {
        assertNotNull(shapeRegistry);
        assertEquals(18, shapeRegistry.getShapeCount());
    }

    @Test
    public void testShapeRetrieval() {
        PlanetShape sphere = shapeRegistry.getShape("SPHERE");
        assertNotNull(sphere);
        assertEquals("SPHERE", sphere.getName());
        assertEquals("Küre", sphere.getDisplayName());
    }

    @Test
    public void testRandomShapeSelection() {
        PlanetShape randomShape = shapeRegistry.getRandomShape(java.util.Map.of(
            "SPHERE", 1.0,
            "BLOB", 0.0
        ));
        assertNotNull(randomShape);
        assertEquals("SPHERE", randomShape.getName());
    }

    @Test
    public void testInvalidShapeRetrieval() {
        PlanetShape invalidShape = shapeRegistry.getShape("INVALID");
        assertNotNull(invalidShape); // Should return default SPHERE
        assertEquals("SPHERE", invalidShape.getName());
    }
}