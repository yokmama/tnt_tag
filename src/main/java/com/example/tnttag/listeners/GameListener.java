package com.example.tnttag.listeners;

import com.example.tnttag.TNTTagPlugin;
import com.example.tnttag.events.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/**
 * Handles game-related custom events
 */
public class GameListener implements Listener {
    
    private final TNTTagPlugin plugin;
    
    public GameListener(TNTTagPlugin plugin) {
        this.plugin = plugin;
    }
    
    /**
     * Handle game start
     */
    @EventHandler
    public void onGameStart(TNTTagStartEvent event) {
        plugin.getLogger().info("ゲーム開始: " + event.getGame().getArena().getName());
        
        // TODO: Play game start effects
        // TODO: Send start messages to players
    }
    
    /**
     * Handle round start
     */
    @EventHandler
    public void onRoundStart(TNTTagRoundStartEvent event) {
        plugin.getLogger().info(
            "ラウンド " + event.getRoundNumber() + " 開始: " + 
            event.getTntHolders().size() + " TNT保持者"
        );
        
        // TODO: Play round start effects
        // TODO: Send round start messages to players
    }
    
    /**
     * Handle TNT transfer
     */
    @EventHandler
    public void onTNTTag(TNTTagEvent event) {
        if (event.isCancelled()) {
            return;
        }
        
        plugin.getLogger().info(
            event.getTagger().getName() + " → " + event.getTagged().getName() + 
            " (ラウンド " + event.getRoundNumber() + ")"
        );
        
        // TODO: Play tag effects
        // TODO: Send tag messages to players
    }
    
    /**
     * Handle explosion
     */
    @EventHandler
    public void onExplosion(TNTExplosionEvent event) {
        plugin.getLogger().info(
            "爆発: " + event.getVictims().size() + " 人が脱落 (ラウンド " + event.getRoundNumber() + ")"
        );
        
        // TODO: Play explosion effects
        // TODO: Send elimination messages to players
    }
    
    /**
     * Handle game end
     */
    @EventHandler
    public void onGameEnd(TNTTagEndEvent event) {
        String winnerName = event.getWinner() != null ? event.getWinner().getName() : "なし";
        plugin.getLogger().info(
            "ゲーム終了: 勝者 = " + winnerName + ", 理由 = " + event.getEndReason()
        );
        
        // TODO: Play victory effects
        // TODO: Display results to players
    }
}
