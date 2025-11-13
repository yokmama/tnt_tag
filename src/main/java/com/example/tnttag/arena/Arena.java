package com.example.tnttag.arena;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.util.BoundingBox;

/**
 * Represents a game arena
 */
public class Arena {
    
    private final String name;
    private final World world;
    private final Location pos1;
    private final Location pos2;
    private final Location centerSpawn;
    
    public Arena(String name, World world, Location pos1, Location pos2, Location centerSpawn) {
        this.name = name;
        this.world = world;
        this.pos1 = pos1;
        this.pos2 = pos2;
        this.centerSpawn = centerSpawn;
    }
    
    /**
     * Get the arena name
     */
    public String getName() {
        return name;
    }
    
    /**
     * Get the world
     */
    public World getWorld() {
        return world;
    }
    
    /**
     * Get position 1 (corner)
     */
    public Location getPos1() {
        return pos1.clone();
    }
    
    /**
     * Get position 2 (opposite corner)
     */
    public Location getPos2() {
        return pos2.clone();
    }
    
    /**
     * Get the center spawn location
     */
    public Location getCenterSpawn() {
        return centerSpawn.clone();
    }
    
    /**
     * Get the bounding box for this arena
     * IMPORTANT: pos1 and pos2 are player coordinates (with decimals),
     * but we need to expand to full block boundaries.
     * Example: If pos1.getX() = 63.290, the block is at X=63,
     * and its boundary should be 63.0~64.0
     */
    public BoundingBox getBoundingBox() {
        // Get block coordinates (floor for each position)
        int blockX1 = pos1.getBlockX();
        int blockY1 = pos1.getBlockY();
        int blockZ1 = pos1.getBlockZ();
        int blockX2 = pos2.getBlockX();
        int blockY2 = pos2.getBlockY();
        int blockZ2 = pos2.getBlockZ();

        // Find min and max block coordinates
        int minBlockX = Math.min(blockX1, blockX2);
        int minBlockY = Math.min(blockY1, blockY2);
        int minBlockZ = Math.min(blockZ1, blockZ2);
        int maxBlockX = Math.max(blockX1, blockX2);
        int maxBlockY = Math.max(blockY1, blockY2);
        int maxBlockZ = Math.max(blockZ1, blockZ2);

        // Convert to bounding box with full block boundaries
        // Block N spans from N.0 to (N+1).0
        double minX = minBlockX;
        double minY = minBlockY;
        double minZ = minBlockZ;
        double maxX = maxBlockX + 1.0;
        double maxY = maxBlockY + 1.0;
        double maxZ = maxBlockZ + 1.0;

        return new BoundingBox(minX, minY, minZ, maxX, maxY, maxZ);
    }
    
    /**
     * Check if a location is within this arena
     */
    public boolean contains(Location location) {
        if (!location.getWorld().equals(world)) {
            return false;
        }
        return getBoundingBox().contains(location.toVector());
    }
    
    /**
     * Setup world border for this arena
     * IMPORTANT: Must match the BoundingBox logic exactly
     * Uses full block boundaries with margin for player hitbox
     */
    public void setupWorldBorder() {
        // Get the bounding box (which uses full block boundaries)
        BoundingBox bounds = getBoundingBox();

        // Calculate the actual arena dimensions in blocks
        double width = bounds.getMaxX() - bounds.getMinX();
        double depth = bounds.getMaxZ() - bounds.getMinZ();

        // World border is square, so use the larger dimension
        // Add margin (0.3 * 2 = 0.6 for player hitbox on both sides, round up to 1.0 for safety)
        double borderSize = Math.max(width, depth) + 1.0;

        WorldBorder border = world.getWorldBorder();
        border.setCenter(centerSpawn);
        border.setSize(borderSize);
        border.setWarningDistance(5);
    }
    
    /**
     * Remove world border
     */
    public void removeWorldBorder() {
        WorldBorder border = world.getWorldBorder();
        border.reset();
    }
}
