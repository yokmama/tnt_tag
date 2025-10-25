package com.example.tnttag.player;

import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * Stores game data for a player during a game
 */
public class PlayerGameData {
    
    private final UUID playerUUID;
    private boolean isAlive;
    private boolean isTNTHolder;
    private int roundsSurvived;
    private int roundEliminated;
    private int tntTagsGiven;
    private int tntTagsReceived;
    private long lastTagTime;
    
    public PlayerGameData(Player player) {
        this.playerUUID = player.getUniqueId();
        this.isAlive = true;
        this.isTNTHolder = false;
        this.roundsSurvived = 0;
        this.roundEliminated = -1;
        this.tntTagsGiven = 0;
        this.tntTagsReceived = 0;
        this.lastTagTime = 0;
    }
    
    /**
     * Get the player UUID
     */
    public UUID getPlayerUUID() {
        return playerUUID;
    }
    
    /**
     * Check if the player is alive
     */
    public boolean isAlive() {
        return isAlive;
    }
    
    /**
     * Set alive status
     */
    public void setAlive(boolean alive) {
        this.isAlive = alive;
    }
    
    /**
     * Check if the player is a TNT holder
     */
    public boolean isTNTHolder() {
        return isTNTHolder;
    }
    
    /**
     * Set TNT holder status
     */
    public void setTNTHolder(boolean tntHolder) {
        this.isTNTHolder = tntHolder;
    }
    
    /**
     * Get the number of rounds survived
     */
    public int getRoundsSurvived() {
        return roundsSurvived;
    }
    
    /**
     * Increment rounds survived
     */
    public void incrementRoundsSurvived() {
        this.roundsSurvived++;
    }
    
    /**
     * Get the round when eliminated (-1 if still alive)
     */
    public int getRoundEliminated() {
        return roundEliminated;
    }
    
    /**
     * Set the round when eliminated
     */
    public void setRoundEliminated(int round) {
        this.roundEliminated = round;
    }
    
    /**
     * Get the number of TNT tags given
     */
    public int getTntTagsGiven() {
        return tntTagsGiven;
    }
    
    /**
     * Increment TNT tags given
     */
    public void incrementTntTagsGiven() {
        this.tntTagsGiven++;
    }
    
    /**
     * Get the number of TNT tags received
     */
    public int getTntTagsReceived() {
        return tntTagsReceived;
    }
    
    /**
     * Increment TNT tags received
     */
    public void incrementTntTagsReceived() {
        this.tntTagsReceived++;
    }
    
    /**
     * Get the last time this player tagged someone (milliseconds)
     */
    public long getLastTagTime() {
        return lastTagTime;
    }
    
    /**
     * Set the last tag time
     */
    public void setLastTagTime(long time) {
        this.lastTagTime = time;
    }
    
    /**
     * Check if the player is on cooldown
     */
    public boolean isOnCooldown(double cooldownSeconds) {
        long cooldownMillis = (long) (cooldownSeconds * 1000);
        return System.currentTimeMillis() - lastTagTime < cooldownMillis;
    }
}
