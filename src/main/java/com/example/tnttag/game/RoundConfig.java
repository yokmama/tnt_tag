package com.example.tnttag.game;

/**
 * Configuration for a specific round
 */
public class RoundConfig {
    
    private final int roundNumber;
    private final int fixedTNTHolders;      // -1 if using ratio
    private final double tntHoldersRatio;   // -1.0 if using fixed count
    private final int duration;             // Duration in seconds
    private final boolean glowing;
    
    public RoundConfig(int roundNumber, int fixedTNTHolders, double tntHoldersRatio, 
                       int duration, boolean glowing) {
        this.roundNumber = roundNumber;
        this.fixedTNTHolders = fixedTNTHolders;
        this.tntHoldersRatio = tntHoldersRatio;
        this.duration = duration;
        this.glowing = glowing;
    }
    
    /**
     * Get the round number (1-6)
     */
    public int getRoundNumber() {
        return roundNumber;
    }
    
    /**
     * Calculate the number of TNT holders for this round based on alive players
     */
    public int calculateTNTHolders(int alivePlayers) {
        if (fixedTNTHolders > 0) {
            return Math.min(fixedTNTHolders, alivePlayers);
        } else if (tntHoldersRatio > 0) {
            return Math.max(1, (int) Math.round(tntHoldersRatio * alivePlayers));
        }
        return 1; // Default fallback
    }
    
    /**
     * Get the duration of this round in seconds
     */
    public int getDuration() {
        return duration;
    }
    
    /**
     * Check if all players should glow in this round
     */
    public boolean isGlowing() {
        return glowing;
    }
    
    /**
     * Check if using fixed TNT holder count
     */
    public boolean isFixedCount() {
        return fixedTNTHolders > 0;
    }
    
    /**
     * Get the fixed TNT holder count (-1 if using ratio)
     */
    public int getFixedTNTHolders() {
        return fixedTNTHolders;
    }
    
    /**
     * Get the TNT holders ratio (-1.0 if using fixed count)
     */
    public double getTntHoldersRatio() {
        return tntHoldersRatio;
    }
}
