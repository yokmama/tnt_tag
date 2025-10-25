package com.example.tnttag.game;

import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;

/**
 * Represents a single round in the game
 */
public class Round {
    
    private final RoundConfig config;
    private final Set<Player> tntHolders;
    private int remainingTime;
    private final long startTime;
    
    public Round(RoundConfig config, Set<Player> initialTNTHolders) {
        this.config = config;
        this.tntHolders = new HashSet<>(initialTNTHolders);
        this.remainingTime = config.getDuration();
        this.startTime = System.currentTimeMillis();
    }
    
    /**
     * Get the round configuration
     */
    public RoundConfig getConfig() {
        return config;
    }
    
    /**
     * Get the set of current TNT holders
     */
    public Set<Player> getTntHolders() {
        return new HashSet<>(tntHolders);
    }
    
    /**
     * Check if a player is a TNT holder
     */
    public boolean isTNTHolder(Player player) {
        return tntHolders.contains(player);
    }
    
    /**
     * Add a TNT holder
     */
    public void addTNTHolder(Player player) {
        tntHolders.add(player);
    }
    
    /**
     * Remove a TNT holder
     */
    public void removeTNTHolder(Player player) {
        tntHolders.remove(player);
    }
    
    /**
     * Transfer TNT from one player to another
     */
    public void transferTNT(Player from, Player to) {
        tntHolders.remove(from);
        tntHolders.add(to);
    }
    
    /**
     * Get the remaining time in seconds
     */
    public int getRemainingTime() {
        return remainingTime;
    }
    
    /**
     * Set the remaining time
     */
    public void setRemainingTime(int remainingTime) {
        this.remainingTime = remainingTime;
    }
    
    /**
     * Decrease remaining time by 1 second
     */
    public void decrementTime() {
        if (remainingTime > 0) {
            remainingTime--;
        }
    }
    
    /**
     * Check if the round has ended
     */
    public boolean hasEnded() {
        return remainingTime <= 0;
    }
    
    /**
     * Get the round number
     */
    public int getRoundNumber() {
        return config.getRoundNumber();
    }
    
    /**
     * Get the start time (milliseconds)
     */
    public long getStartTime() {
        return startTime;
    }
    
    /**
     * Get the elapsed time in seconds
     */
    public int getElapsedTime() {
        return (int) ((System.currentTimeMillis() - startTime) / 1000);
    }
}
