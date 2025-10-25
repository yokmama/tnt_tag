package com.example.tnttag.events;

import com.example.tnttag.game.GameInstance;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Event fired when a TNT TAG game ends
 */
public class TNTTagEndEvent extends Event {
    
    private static final HandlerList HANDLERS = new HandlerList();
    
    private final GameInstance game;
    private final Player winner;
    private final EndReason endReason;
    
    public TNTTagEndEvent(GameInstance game, Player winner, EndReason endReason) {
        this.game = game;
        this.winner = winner;
        this.endReason = endReason;
    }
    
    /**
     * Get the game instance
     */
    public GameInstance getGame() {
        return game;
    }
    
    /**
     * Get the winner (null if no winner)
     */
    public Player getWinner() {
        return winner;
    }
    
    /**
     * Get the reason the game ended
     */
    public EndReason getEndReason() {
        return endReason;
    }
    
    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }
    
    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
    
    public enum EndReason {
        ALL_ROUNDS_COMPLETE,
        NO_SURVIVORS,
        FORCE_STOPPED
    }
}
