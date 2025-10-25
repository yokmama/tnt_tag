package com.example.tnttag.game;

/**
 * Represents the current state of a game
 */
public enum GameState {
    /**
     * Waiting for players to join
     */
    WAITING,
    
    /**
     * Countdown before game starts
     */
    STARTING,
    
    /**
     * Game is currently in progress
     */
    IN_GAME,
    
    /**
     * Round has ended, transitioning to next round
     */
    ROUND_ENDING,
    
    /**
     * Game has ended
     */
    ENDING
}
