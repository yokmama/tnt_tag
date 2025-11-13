package com.example.tnttag.listeners;

import com.example.tnttag.TNTTagPlugin;
import com.example.tnttag.game.GameInstance;
import com.example.tnttag.game.GameState;
import com.example.tnttag.player.PlayerGameData;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Handles general player events
 */
public class PlayerListener implements Listener {

    private final TNTTagPlugin plugin;
    // Store disconnected players' game info: UUID -> GameInstance
    private final Map<UUID, GameInstance> disconnectedPlayers;

    public PlayerListener(TNTTagPlugin plugin) {
        this.plugin = plugin;
        this.disconnectedPlayers = new HashMap<>();
    }

    /**
     * Handle player reconnect - restore to game if they disconnected during a game
     */
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        // Check if this player was in a game before disconnecting
        GameInstance game = disconnectedPlayers.remove(uuid);

        if (game != null) {
            // Check if the game is still running
            if (game.getState() != GameState.ENDING) {
                plugin.getLogger().info(player.getName() + " が再接続しました。ゲームに復帰させます。");

                // Re-add player to the game
                if (plugin.getGameManager().joinGame(player, game)) {
                    // Get player's game data
                    PlayerGameData data = plugin.getPlayerManager().getPlayerData(player);

                    // Teleport to arena center
                    player.teleport(game.getArena().getCenterSpawn());

                    // Restore game state
                    if (data.isAlive()) {
                        player.setGameMode(GameMode.ADVENTURE);

                        // Restore TNT holder status if applicable
                        if (data.isTNTHolder()) {
                            plugin.getPlayerManager().setTNTHolder(player, true);
                        } else {
                            // Give Speed I to non-TNT holders
                            plugin.getPlayerManager().setTNTHolder(player, false);
                        }

                        player.sendMessage("§aゲームに復帰しました！");
                    } else {
                        // Player was eliminated before disconnect
                        player.setGameMode(GameMode.SPECTATOR);
                        player.sendMessage("§7観戦モードで復帰しました。");
                    }
                } else {
                    player.sendMessage("§cゲームへの復帰に失敗しました。");
                }
            } else {
                plugin.getLogger().info(player.getName() + " のゲームは既に終了していました。");
            }
        }
    }

    /**
     * Handle player disconnect - store game info for potential rejoin
     */
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        GameInstance game = plugin.getGameManager().getPlayerGame(player);

        if (game != null) {
            // Store the game instance for rejoin
            disconnectedPlayers.put(player.getUniqueId(), game);

            plugin.getLogger().info(player.getName() + " が切断しました。ゲーム情報を保持します。");

            // Remove from game manager but keep PlayerGameData
            plugin.getGameManager().leaveGame(player);
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

    // NOTE: Arena boundary is now handled by WorldBorder (see Arena.setupWorldBorder)
    // No need for manual PlayerMoveEvent checking - Minecraft handles it automatically
}
