package com.example.tnttag.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.List;

/**
 * Event fired when TNT explodes and eliminates players
 */
public class TNTExplosionEvent extends Event {
    
    private static final HandlerList HANDLERS = new HandlerList();
    
    private final List<Player> victims;
    private final int roundNumber;
    
    public TNTExplosionEvent(List<Player> victims, int roundNumber) {
        this.victims = victims;
        this.roundNumber = roundNumber;
    }
    
    /**
     * Get the list of players eliminated by this explosion
     */
    public List<Player> getVictims() {
        return victims;
    }
    
    /**
     * Get the round number when the explosion occurred
     */
    public int getRoundNumber() {
        return roundNumber;
    }
    
    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }
    
    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
