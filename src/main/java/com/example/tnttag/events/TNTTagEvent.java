package com.example.tnttag.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Event fired when TNT is transferred from one player to another
 */
public class TNTTagEvent extends Event implements Cancellable {
    
    private static final HandlerList HANDLERS = new HandlerList();
    private boolean cancelled = false;
    
    private final Player tagger;
    private final Player tagged;
    private final int roundNumber;
    private final int remainingTime;
    
    public TNTTagEvent(Player tagger, Player tagged, int roundNumber, int remainingTime) {
        this.tagger = tagger;
        this.tagged = tagged;
        this.roundNumber = roundNumber;
        this.remainingTime = remainingTime;
    }
    
    /**
     * Get the player who is passing the TNT
     */
    public Player getTagger() {
        return tagger;
    }
    
    /**
     * Get the player who is receiving the TNT
     */
    public Player getTagged() {
        return tagged;
    }
    
    /**
     * Get the current round number
     */
    public int getRoundNumber() {
        return roundNumber;
    }
    
    /**
     * Get the remaining time in the round (seconds)
     */
    public int getRemainingTime() {
        return remainingTime;
    }
    
    @Override
    public boolean isCancelled() {
        return cancelled;
    }
    
    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }
    
    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }
    
    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
