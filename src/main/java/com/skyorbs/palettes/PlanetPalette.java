package com.skyorbs.palettes;

import org.bukkit.Material;
import java.util.Random;

/**
 * Defines a planet's block composition for diverse visual variety
 * Enables 2000+ planet combinations through modular palette system
 */
public class PlanetPalette {

    private final String id;
    private final String displayName;
    private final Material[] surfaceBlocks;
    private final Material[] subsurfaceBlocks;
    private final Material[] coreBlocks;
    private final Material[] decorationBlocks;
    private final double weight;
    
    public PlanetPalette(String id, String displayName, 
                        Material[] surfaceBlocks, 
                        Material[] subsurfaceBlocks,
                        Material[] coreBlocks,
                        Material[] decorationBlocks,
                        double weight) {
        this.id = id;
        this.displayName = displayName;
        this.surfaceBlocks = surfaceBlocks;
        this.subsurfaceBlocks = subsurfaceBlocks;
        this.coreBlocks = coreBlocks;
        this.decorationBlocks = decorationBlocks;
        this.weight = weight;
    }
    
    public String getId() {
        return id;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public double getWeight() {
        return weight;
    }
    
    /**
     * Get material based on depth from surface
     * @param depth 0 = surface, higher = deeper
     * @param random Random for variation
     * @return Material for this depth
     */
    public Material getMaterialByDepth(int depth, Random random) {
        if (depth == 0) {
            // Surface layer
            return surfaceBlocks[random.nextInt(surfaceBlocks.length)];
        } else if (depth < 5) {
            // Subsurface layer (0-5 blocks deep)
            return subsurfaceBlocks[random.nextInt(subsurfaceBlocks.length)];
        } else {
            // Core layer (5+ blocks deep)
            return coreBlocks[random.nextInt(coreBlocks.length)];
        }
    }

    /**
     * Get material based on depth and regional noise for coherent regions
     * @param depth 0 = surface, higher = deeper
     * @param noiseValue Noise value between 0-1 for regional coherence
     * @param random Random for variation
     * @return Material for this depth and region
     */
    public Material getMaterialByDepthAndNoise(int depth, double noiseValue, Random random) {
        // Use noise to select consistent materials within regions
        int surfaceIndex = (int)(noiseValue * surfaceBlocks.length);
        int subsurfaceIndex = (int)(noiseValue * subsurfaceBlocks.length);
        int coreIndex = (int)(noiseValue * coreBlocks.length);

        // Ensure indices are within bounds
        surfaceIndex = Math.min(surfaceIndex, surfaceBlocks.length - 1);
        subsurfaceIndex = Math.min(subsurfaceIndex, subsurfaceBlocks.length - 1);
        coreIndex = Math.min(coreIndex, coreBlocks.length - 1);

        if (depth == 0) {
            // Surface layer - use noise for regional consistency
            return surfaceBlocks[surfaceIndex];
        } else if (depth < 5) {
            // Subsurface layer (0-5 blocks deep) - use noise for regional consistency
            return subsurfaceBlocks[subsurfaceIndex];
        } else {
            // Core layer (5+ blocks deep) - use noise for regional consistency
            return coreBlocks[coreIndex];
        }
    }
    
    /**
     * Get random decoration block for surface features
     */
    public Material getDecorationBlock(Random random) {
        if (decorationBlocks.length == 0) return null;
        return decorationBlocks[random.nextInt(decorationBlocks.length)];
    }
    
    /**
     * Get surface material
     */
    public Material getPrimarySurface() {
        return surfaceBlocks[0];
    }
    
    /**
     * Get core material
     */
    public Material getPrimaryCore() {
        return coreBlocks[0];
    }
}
