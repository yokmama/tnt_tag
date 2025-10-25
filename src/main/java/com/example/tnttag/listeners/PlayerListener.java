package com.example.tnttag.listeners;

import com.example.tnttag.TNTTagPlugin;
import com.example.tnttag.game.GameInstance;
import com.example.tnttag.game.GameState;
import com.example.tnttag.player.PlayerGameData;
import org.bukkit.GameMode;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerMoveEvent;
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

    /**
     * Handle player death from non-TNT causes (TASK-195)
     */
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        org.bukkit.entity.Player player = event.getEntity();
        GameInstance game = plugin.getGameManager().getPlayerGame(player);

        if (game == null || game.getState() != GameState.IN_GAME) {
            return;
        }

        // Treat as elimination
        PlayerGameData data = plugin.getPlayerManager().getPlayerData(player);
        if (data.isAlive()) {
            plugin.getLogger().warning(player.getName() + " died from non-TNT cause during game");
            data.setAlive(false);
            data.setRoundEliminated(game.getCurrentRound());
            player.setGameMode(GameMode.SPECTATOR);

            // Clear drops to avoid item loss
            event.getDrops().clear();
            event.setKeepInventory(true);
        }
    }

    /**
     * Handle arena boundary violations (TASK-194)
     */
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        // Only check if player actually moved (not just head rotation)
        if (event.getFrom().getBlockX() == event.getTo().getBlockX() &&
            event.getFrom().getBlockY() == event.getTo().getBlockY() &&
            event.getFrom().getBlockZ() == event.getTo().getBlockZ()) {
            return;
        }

        org.bukkit.entity.Player player = event.getPlayer();
        GameInstance game = plugin.getGameManager().getPlayerGame(player);

        if (game == null || game.getState() != GameState.IN_GAME) {
            return;
        }

        // Check if player is outside arena bounds
        if (!game.getArena().contains(event.getTo())) {
            // Teleport back to center
            player.teleport(game.getArena().getCenterSpawn());
            player.sendMessage("§c警告: アリーナ外に出ようとしました！");

            if (plugin.getConfigManager().isDebug()) {
                plugin.getLogger().info(player.getName() + " attempted to leave arena bounds");
            }
        }
    }
}
