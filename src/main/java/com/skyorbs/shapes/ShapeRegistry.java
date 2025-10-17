package com.skyorbs.shapes;

import com.skyorbs.shapes.impl.*;

import java.util.*;

public class ShapeRegistry {
    
    private final Map<String, PlanetShape> shapes = new HashMap<>();
    private final Random random = new Random();
    
    public void registerAllShapes() {
        register(new SphereShape());
        register(new BlobShape());
        register(new AsteroidShape());
        register(new EllipsoidShape());
        register(new TorusShape());
        register(new RingShape());
        register(new CylinderShape());
        register(new ConeShape());
        register(new DiamondShape());
        register(new CubeShape());
        register(new PyramidShape());
        register(new OctahedronShape());
        register(new FractalShape());
        register(new SpikyShape());
        register(new CometShape());
        register(new CrescentShape());
        register(new HybridShape());
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
