# Ore Generation Fix - TODO

- [x] Fix inconsistent distance checks in OreGenerator.java for solid planets
- [x] Change generateOreType check from radius-2 to radius-1 to match generateVein
- [x] Ensure ores spawn strictly inside planets, not on surface or outside

## Summary
Fixed ore generation to ensure all ores form inside planets as part of the planet generation, not separately or outside.
