package com.example.tnttag.arena;

import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 * Represents an arena setup session for an admin
 */
public class ArenaSetupSession {
    
    private final Player admin;
    private Location pos1;
    private Location pos2;
    private long lastUpdate;
    
    public ArenaSetupSession(Player admin) {
        this.admin = admin;
        this.lastUpdate = System.currentTimeMillis();
    }
    
    /**
     * Get the admin player
     */
    public Player getAdmin() {
        return admin;
    }
    
    /**
     * Get position 1
     */
    public Location getPos1() {
        return pos1;
    }
    
    /**
     * Set position 1
     */
    public void setPos1(Location pos1) {
        this.pos1 = pos1;
        this.lastUpdate = System.currentTimeMillis();
    }
    
    /**
     * Get position 2
     */
    public Location getPos2() {
        return pos2;
    }
    
    /**
     * Set position 2
     */
    public void setPos2(Location pos2) {
        this.pos2 = pos2;
        this.lastUpdate = System.currentTimeMillis();
    }
    
    /**
     * Check if both positions are set
     */
    public boolean isComplete() {
        return pos1 != null && pos2 != null;
    }
    
    /**
     * Get the last update time
     */
    public long getLastUpdate() {
        return lastUpdate;
    }
    
    /**
     * Check if this session has expired (5 minutes)
     */
    public boolean isExpired() {
        return System.currentTimeMillis() - lastUpdate > 300000; // 5 minutes
    }
}
