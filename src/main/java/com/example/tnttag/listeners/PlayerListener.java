package com.example.tnttag.listeners;

import com.example.tnttag.TNTTagPlugin;
import com.example.tnttag.game.GameInstance;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Handles general player events
 */
public class PlayerListener implements Listener {
    
    private final TNTTagPlugin plugin;
    
    public PlayerListener(TNTTagPlugin plugin) {
        this.plugin = plugin;
    }
    
    /**
     * Handle player disconnect
     */
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        GameInstance game = plugin.getGameManager().getPlayerGame(event.getPlayer());
        
        if (game != null) {
            plugin.getGameManager().leaveGame(event.getPlayer());
        }
    }
    
    /**
     * Cancel PVP damage in TNT TAG games
     */
    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        // This is handled in TNTTransferListener, but we ensure damage is cancelled
        if (!(event.getEntity() instanceof org.bukkit.entity.Player)) {
            return;
        }
        
        if (!(event.getDamager() instanceof org.bukkit.entity.Player)) {
            return;
        }
        
        org.bukkit.entity.Player victim = (org.bukkit.entity.Player) event.getEntity();
        
        // Check if victim is in a game
        GameInstance game = plugin.getGameManager().getPlayerGame(victim);
        
        if (game != null) {
            // Cancel damage in TNT TAG games
            event.setCancelled(true);
        }
    }
}
