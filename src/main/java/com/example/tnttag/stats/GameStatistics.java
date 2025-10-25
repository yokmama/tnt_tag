package com.example.tnttag.stats;

import java.util.UUID;

/**
 * Stores statistics for a player
 */
public class GameStatistics {
    
    private final UUID playerUUID;
    private String playerName;
    private int gamesPlayed;
    private int totalRoundsSurvived;
    private int wins;
    private int tntTagsGiven;
    private int tntTagsReceived;
    private double totalSurvivalTime;
    
    public GameStatistics(UUID playerUUID, String playerName) {
        this.playerUUID = playerUUID;
        this.playerName = playerName;
        this.gamesPlayed = 0;
        this.totalRoundsSurvived = 0;
        this.wins = 0;
        this.tntTagsGiven = 0;
        this.tntTagsReceived = 0;
        this.totalSurvivalTime = 0.0;
    }
    
    /**
     * Get player UUID
     */
    public UUID getPlayerUUID() {
        return playerUUID;
    }
    
    /**
     * Get player name
     */
    public String getPlayerName() {
        return playerName;
    }
    
    /**
     * Set player name (updates on each game)
     */
    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }
    
    /**
     * Get games played
     */
    public int getGamesPlayed() {
        return gamesPlayed;
    }
    
    /**
     * Increment games played
     */
    public void incrementGamesPlayed() {
        this.gamesPlayed++;
    }
    
    /**
     * Get total rounds survived
     */
    public int getTotalRoundsSurvived() {
        return totalRoundsSurvived;
    }
    
    /**
     * Add rounds survived
     */
    public void addRoundsSurvived(int rounds) {
        this.totalRoundsSurvived += rounds;
    }
    
    /**
     * Get wins
     */
    public int getWins() {
        return wins;
    }
    
    /**
     * Increment wins
     */
    public void incrementWins() {
        this.wins++;
    }
    
    /**
     * Get TNT tags given
     */
    public int getTntTagsGiven() {
        return tntTagsGiven;
    }
    
    /**
     * Add TNT tags given
     */
    public void addTntTagsGiven(int count) {
        this.tntTagsGiven += count;
    }
    
    /**
     * Get TNT tags received
     */
    public int getTntTagsReceived() {
        return tntTagsReceived;
    }
    
    /**
     * Add TNT tags received
     */
    public void addTntTagsReceived(int count) {
        this.tntTagsReceived += count;
    }
    
    /**
     * Get total survival time (seconds)
     */
    public double getTotalSurvivalTime() {
        return totalSurvivalTime;
    }
    
    /**
     * Add survival time
     */
    public void addSurvivalTime(double seconds) {
        this.totalSurvivalTime += seconds;
    }
    
    /**
     * Get average survival time per game
     */
    public double getAverageSurvivalTime() {
        if (gamesPlayed == 0) {
            return 0.0;
        }
        return totalSurvivalTime / gamesPlayed;
    }
    
    /**
     * Get win rate (0.0 to 1.0)
     */
    public double getWinRate() {
        if (gamesPlayed == 0) {
            return 0.0;
        }
        return (double) wins / gamesPlayed;
    }
}
