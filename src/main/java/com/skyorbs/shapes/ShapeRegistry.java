package com.skyorbs.shapes;

import com.skyorbs.shapes.impl.*;

import java.util.*;

public class ShapeRegistry {
    
    private final Map<String, PlanetShape> shapes = new HashMap<String, PlanetShape>();
    private final Random random = new Random();
    
    public void registerAllShapes() {
        // Basic shapes (10)
        register(new SphereShape());
        register(new HemisphereShape());
        register(new BlobShape());
        register(new AsteroidShape());
        register(new EllipsoidShape());
        register(new TorusShape());
        register(new RingShape());
        register(new CylinderShape());
        register(new ConeShape());
        register(new DiamondShape());

        // Advanced shapes (8)
        register(new CubeShape());
        register(new PyramidShape());
        register(new OctahedronShape());
        register(new FractalShape());
        register(new SpikyShape());
        register(new CometShape());
        register(new CrescentShape());
        register(new HybridShape());
        
        // NEW: Diverse shapes (15 more) - 33 TOTAL!
        register(new LayeredShape());
        register(new CrateredShape());
        register(new HoneycombShape());
        register(new SpiralShape());
        register(new WaveShape());
        register(new CrystalShape());
        register(new OrganicShape());
        register(new GeometricShape());
        register(new NebulaShape());
        register(new AsteroidFieldShape());

        // EXTRA NEW SHAPES for more variety!
        register(new StarShape());
        register(new MoonShape());
        register(new RainbowShape());
        register(new ButterflyShape());
        register(new HeartShape());
    }
    
    public void register(PlanetShape shape) {
        shapes.put(shape.getName(), shape);
    }
    
    public PlanetShape getShape(String name) {
        return shapes.getOrDefault(name, shapes.get("SPHERE"));
    }
    
    public PlanetShape getRandomShape(Map<String, Double> weights) {
        double total = 0.0;
        for (double weight : weights.values()) {
            total += weight;
        }
        
        double rand = random.nextDouble() * total;
        double current = 0.0;
        
        for (Map.Entry<String, Double> entry : weights.entrySet()) {
            current += entry.getValue();
            if (rand <= current) {
                PlanetShape shape = shapes.get(entry.getKey());
                if (shape != null) {
                    return shape;
                }
            }
        }
        
        return shapes.get("SPHERE");
    }
    
    public Set<String> getShapeNames() {
        return shapes.keySet();
    }
    
    public int getShapeCount() {
        return shapes.size();
    }
}
