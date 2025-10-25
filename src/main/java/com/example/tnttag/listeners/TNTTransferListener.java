package com.example.tnttag.listeners;

import com.example.tnttag.TNTTagPlugin;
import com.example.tnttag.events.TNTTagEvent;
import com.example.tnttag.game.GameInstance;
import com.example.tnttag.game.GameState;
import com.example.tnttag.game.Round;
import com.example.tnttag.player.PlayerGameData;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

/**
 * Handles TNT transfer between players
 */
public class TNTTransferListener implements Listener {
    
    private final TNTTagPlugin plugin;
    
    public TNTTransferListener(TNTTagPlugin plugin) {
        this.plugin = plugin;
    }
    
    /**
     * Handle player hitting another player (TNT transfer)
     */
    @EventHandler(priority = EventPriority.HIGH)
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        // Check if both are players
        if (!(event.getEntity() instanceof Player)) {
            return;
        }
        if (!(event.getDamager() instanceof Player)) {
            return;
        }
        
        Player attacker = (Player) event.getDamager();
        Player victim = (Player) event.getEntity();
        
        // Get game instance
        GameInstance game = plugin.getGameManager().getPlayerGame(attacker);
        if (game == null) {
            return; // Not in a game
        }
        
        // Check if game is in progress
        if (game.getState() != GameState.IN_GAME) {
            event.setCancelled(true);
            return;
        }
        
        // Cancel damage
        event.setCancelled(true);
        
        // Get active round
        Round round = game.getActiveRound();
        if (round == null) {
            return;
        }
        
        // Get player data
        PlayerGameData attackerData = plugin.getPlayerManager().getPlayerData(attacker);
        PlayerGameData victimData = plugin.getPlayerManager().getPlayerData(victim);
        
        // Check if attacker is TNT holder
        if (!attackerData.isTNTHolder()) {
            return; // Not a TNT holder, can't transfer
        }
        
        // Check if victim is already TNT holder
        if (victimData.isTNTHolder()) {
            return; // Victim already has TNT
        }
        
        // Check cooldown
        double cooldown = plugin.getConfigManager().getTagCooldown();
        if (attackerData.isOnCooldown(cooldown)) {
            return; // On cooldown
        }
        
        // Fire TNT tag event
        TNTTagEvent tagEvent = new TNTTagEvent(
            attacker, 
            victim, 
            round.getRoundNumber(), 
            round.getRemainingTime()
        );
        Bukkit.getPluginManager().callEvent(tagEvent);
        
        if (tagEvent.isCancelled()) {
            return;
        }
        
        // Transfer TNT
        round.transferTNT(attacker, victim);
        
        // Update player data
        attackerData.setTNTHolder(false);
        attackerData.setLastTagTime(System.currentTimeMillis());
        attackerData.incrementTntTagsGiven();
        
        victimData.setTNTHolder(true);
        victimData.incrementTntTagsReceived();
        
        // Update visual effects
        plugin.getPlayerManager().setTNTHolder(attacker, false);
        plugin.getPlayerManager().setTNTHolder(victim, true);
        
        // TODO: Play tag sound and effects
        // TODO: Send messages to players
    }
}
