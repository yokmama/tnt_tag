package com.example.tnttag.player;

/**
 * Represents the current phase of the game for HUD display
 */
public enum GamePhase {
    /**
     * Waiting for players to join
     */
    WAITING,
    
    /**
     * Countdown before game starts
     */
    COUNTDOWN,
    
    /**
     * Game is currently in progress
     */
    IN_GAME,
    
    /**
     * Player is spectating
     */
    SPECTATING
}
