package com.example.tnttag.events;

import com.example.tnttag.game.GameInstance;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.Set;

/**
 * Event fired when a new round starts
 */
public class TNTTagRoundStartEvent extends Event {
    
    private static final HandlerList HANDLERS = new HandlerList();
    
    private final GameInstance game;
    private final int roundNumber;
    private final Set<Player> tntHolders;
    private final int duration;
    
    public TNTTagRoundStartEvent(GameInstance game, int roundNumber, Set<Player> tntHolders, int duration) {
        this.game = game;
        this.roundNumber = roundNumber;
        this.tntHolders = tntHolders;
        this.duration = duration;
    }
    
    /**
     * Get the game instance
     */
    public GameInstance getGame() {
        return game;
    }
    
    /**
     * Get the round number (1-6)
     */
    public int getRoundNumber() {
        return roundNumber;
    }
    
    /**
     * Get the set of initial TNT holders for this round
     */
    public Set<Player> getTntHolders() {
        return tntHolders;
    }
    
    /**
     * Get the round duration in seconds
     */
    public int getDuration() {
        return duration;
    }
    
    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }
    
    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
