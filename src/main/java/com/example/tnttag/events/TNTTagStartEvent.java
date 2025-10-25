package com.example.tnttag.events;

import com.example.tnttag.game.GameInstance;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Event fired when a TNT TAG game starts
 */
public class TNTTagStartEvent extends Event {
    
    private static final HandlerList HANDLERS = new HandlerList();
    
    private final GameInstance game;
    
    public TNTTagStartEvent(GameInstance game) {
        this.game = game;
    }
    
    /**
     * Get the game instance
     */
    public GameInstance getGame() {
        return game;
    }
    
    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }
    
    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
