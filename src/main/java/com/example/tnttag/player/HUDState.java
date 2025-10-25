package com.example.tnttag.player;

import java.util.HashMap;
import java.util.Map;

/**
 * Stores HUD state for a player
 */
public class HUDState {
    
    private GamePhase phase;
    private final Map<String, String> content;
    
    public HUDState() {
        this.phase = GamePhase.WAITING;
        this.content = new HashMap<>();
    }
    
    /**
     * Get the current game phase
     */
    public GamePhase getPhase() {
        return phase;
    }
    
    /**
     * Set the game phase
     */
    public void setPhase(GamePhase phase) {
        this.phase = phase;
    }
    
    /**
     * Get HUD content by key
     */
    public String getContent(String key) {
        return content.get(key);
    }
    
    /**
     * Set HUD content
     */
    public void setContent(String key, String value) {
        content.put(key, value);
    }
    
    /**
     * Get all content
     */
    public Map<String, String> getAllContent() {
        return new HashMap<>(content);
    }
    
    /**
     * Clear all content
     */
    public void clearContent() {
        content.clear();
    }
}
