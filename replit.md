# SkyOrbs - Minecraft Planet Generation Plugin

## Overview

SkyOrbs is a Minecraft Paper plugin (version 1.21.4) that generates unique planetary worlds within a single Minecraft dimension. The plugin creates diverse planets with different shapes, biomes, and orbital features, all configurable through YAML files. Players can create personal planets, teleport between them, and explore procedurally generated celestial bodies with asteroids and satellites. The entire user interface and configuration is in Turkish (Türkçe).

**Core Purpose:** Provide a skyblock-like experience where instead of traditional islands, players explore and inhabit procedurally generated planets with varied geometries, biome combinations, and orbital mechanics.

**Current Status (Oct 17, 2025):** ✅ Version 2.0.0 Complete - Fully functional with all 17 shapes, 22 biomes, database persistence, and Turkish UI. JAR compiled and ready for deployment.

## User Preferences

Preferred communication style: Simple, everyday language.

## System Architecture

### Plugin Architecture
- **Framework:** Paper API 1.21.4 (Spigot/Bukkit not supported)
- **Language:** Java 21+
- **Build System:** Maven 3.8+
- **Architecture Pattern:** Command-driven with modular generator systems

### Core Components

#### 1. Planet Generation System
- **PlanetGenerator:** Main orchestrator for planet creation
  - Selects random planet types, biomes, and dimensions from weighted pools
  - Coordinates with specialized generators for different planet shapes
  - Manages planet metadata and persistence
  
- **17+ Planet Shape Generators:** Each shape (Sphere, Ameba, Asteroid, Ellipse, Donut, Ring, Cylinder, Cone, Diamond, Cube, Pyramid, Octahedron, Fractal, Spiky, Comet, Crescent, Hybrid) implements unique geometric algorithms
  - Rationale: Variety creates unique exploration experiences and visual interest
  - Trade-off: More shapes = more complex codebase, but increases replayability

- **PlanetSizeCalculator:** Determines planet radius based on configurable min/max bounds
  - Dynamic sizing ensures planets fit within allocated space without collisions

#### 2. Biome and Terrain System
- **22 Biome Types:** Forest, Desert, Glacier, Volcanic, Crystal Forest, Void, and 16+ others
- **BiomeManager:** Applies 1-3 biome blends per planet
  - Creates transition zones between biomes for realistic terrain
  - Each biome affects block composition and visual aesthetics

- **OreDistributor:** Handles mineral placement within planet structure
- **StructureGenerator:** Places decorative or functional structures on planet surfaces

#### 3. Orbital Mechanics
- **Asteroid System:** 1-5 asteroids generated per planet
  - Positioned in orbital patterns around parent planet
  - Fully configurable density and distribution

- **Satellite System:** 0-3 satellites per planet (probability-based)
  - Smaller companion celestial bodies
  - Can have their own biomes and compositions

#### 4. Spatial Management
- **Smart Placement Algorithm:**
  - Planets spawn 800-2000 blocks apart (prevents overlap)
  - Minimum 1000 blocks from world spawn point
  - Ensures each planet has adequate "breathing room" for asteroids/satellites

- **Coordinate System:** All planets exist in a single world dimension with calculated spacing
  - Alternative considered: Multi-world approach (rejected due to performance overhead)

#### 5. Player Interaction Layer
- **Command System:** Turkish-language commands
  - `/gezegen create` - Generate new planet and teleport player
  - `/gezegen list` - Display all existing planets
  - `/gezegen tp <name>` - Teleport to specific planet
  - `/gezegen info <name>` - Show planet details and distances to nearby planets
  - `/gezegen sil <name>` - Delete planet (admin only)
  - `/gezegen reload` - Reload configuration (admin only)

- **TeleportHandler:** Manages player transportation between planets
  - Proposed: 5-second "landing animation" with particle effects and slow fall
  - Prevents fall damage and provides immersive transition

#### 6. Configuration System
- **config.yml Driven Design:** All game parameters externalized
  - Planet size ranges (min/max radius)
  - Inter-planet distances
  - Shape weights (probability distribution for each of 17 shapes)
  - Biome distributions
  - Asteroid/satellite spawn rates
  - 1000+ Turkish and sci-fi themed planet name pool
  - All UI messages in Turkish

- **Hot-Reload Support:** `/gezegen reload` allows runtime configuration updates without server restart

### Data Persistence
- **SQLite Database:** All planet data stored in `data.db` file
  - Stores: Planet UUID, name, location, owner, shape type, biomes, dimensions, timestamps
  - Parent-child relationships for asteroids and satellites (foreign key constraints)
  - DatabaseManager handles CRUD operations with prepared statements
  - Async-safe operations using thread pool executor

### Design Patterns Used
1. **Factory Pattern:** Planet shape generators created based on type selection
2. **Strategy Pattern:** Different biome application strategies per planet type
3. **Command Pattern:** Player commands abstracted into discrete handlers
4. **Builder Pattern:** Implied in complex planet construction process

## External Dependencies

### Core Minecraft Dependencies
- **Paper API 1.21.4:** Primary server framework
  - Chosen over Spigot/Bukkit for better performance and modern API features
  - Provides world manipulation, chunk generation, and event handling

### Build and Development Tools
- **Maven 3.8+:** Dependency management and build automation
  - Standard for Minecraft plugin development
  - Handles Paper API dependency resolution

### Java Runtime
- **JDK 21:** Modern Java features and performance improvements
  - Required for Paper 1.21.4 compatibility
  - Enables use of records, pattern matching, and other modern Java features

### Actual Dependencies
- **SQLite JDBC 3.44.0.0:** Database persistence layer
- **Gson 2.10.1:** JSON serialization (future use)
- **SLF4J 2.0.9:** Logging framework

### Potential Future Integrations
- **Particle Effects System:** For planet landing animations (planned)
- **Economy Integration:** Planet trading/claiming (planned)
- **Vault Permission System:** Enhanced permission control (planned)

### Configuration Format
- **YAML:** Primary configuration and data storage format
  - Human-readable for server administrators
  - Nested structure supports complex planet definitions and name pools