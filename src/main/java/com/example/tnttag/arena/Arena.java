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
     */
    public BoundingBox getBoundingBox() {
        double minX = Math.min(pos1.getX(), pos2.getX());
        double minY = Math.min(pos1.getY(), pos2.getY());
        double minZ = Math.min(pos1.getZ(), pos2.getZ());
        double maxX = Math.max(pos1.getX(), pos2.getX());
        double maxY = Math.max(pos1.getY(), pos2.getY());
        double maxZ = Math.max(pos1.getZ(), pos2.getZ());
        
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
     * Get the radius from center to furthest corner
     */
    public double getRadius() {
        double dx = Math.max(
            Math.abs(centerSpawn.getX() - pos1.getX()),
            Math.abs(centerSpawn.getX() - pos2.getX())
        );
        double dz = Math.max(
            Math.abs(centerSpawn.getZ() - pos1.getZ()),
            Math.abs(centerSpawn.getZ() - pos2.getZ())
        );
        return Math.sqrt(dx * dx + dz * dz);
    }
    
    /**
     * Setup world border for this arena
     */
    public void setupWorldBorder() {
        WorldBorder border = world.getWorldBorder();
        border.setCenter(centerSpawn);
        border.setSize(getRadius() * 2);
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
