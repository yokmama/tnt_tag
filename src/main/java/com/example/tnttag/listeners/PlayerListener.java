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
     * Handle player join - reconnect to existing game or auto-join single game instance
     */
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        // Check if this player was in a game before disconnecting
        GameInstance disconnectedGame = disconnectedPlayers.remove(uuid);

        if (disconnectedGame != null) {
            // Handle reconnection
            if (disconnectedGame.getState() != GameState.ENDING) {
                plugin.getLogger().info(player.getName() + " が再接続しました。ゲームに復帰させます。");

                if (plugin.getGameManager().joinGame(player, disconnectedGame)) {
                    PlayerGameData data = plugin.getPlayerManager().getPlayerData(player);
                    player.teleport(disconnectedGame.getArena().getCenterSpawn());

                    if (data.isAlive()) {
                        player.setGameMode(GameMode.ADVENTURE);
                        if (data.isTNTHolder()) {
                            plugin.getPlayerManager().setTNTHolder(player, true);
                        } else {
                            plugin.getPlayerManager().setTNTHolder(player, false);
                        }
                        player.sendMessage("§aゲームに復帰しました！");
                    } else {
                        player.setGameMode(GameMode.SPECTATOR);
                        player.sendMessage("§7観戦モードで復帰しました。");
                    }
                } else {
                    player.sendMessage("§cゲームへの復帰に失敗しました。");
                }
            } else {
                plugin.getLogger().info(player.getName() + " のゲームは既に終了していました。");
            }
            return; // Don't auto-join if reconnecting
        }

        // Auto-join: Get or create single game instance
        GameInstance game = plugin.getGameManager().getSingleGameInstance();

        if (game == null) {
            // No game exists - create one with random arena
            com.example.tnttag.arena.Arena arena = plugin.getArenaManager().getRandomArena();

            if (arena == null) {
                player.sendMessage("§cアリーナが設定されていません。管理者に連絡してください。");
                plugin.getLogger().severe("アリーナが設定されていないため、ゲームを作成できません");
                return;
            }

            game = plugin.getGameManager().createSingleGameInstance(arena);

            if (game == null) {
                player.sendMessage("§cゲームの作成に失敗しました。管理者に連絡してください。");
                plugin.getLogger().severe("ゲームインスタンスの作成に失敗しました");
                return;
            }
        }

        // Join game or become spectator based on game state
        GameState state = game.getState();

        if (state == GameState.WAITING || state == GameState.STARTING) {
            // Join as active player
            if (plugin.getGameManager().joinGame(player, game)) {
                player.sendMessage("§aアリーナ '" + game.getArena().getName() + "' に参加しました");
                plugin.getLogger().info(player.getName() + " が自動参加しました");
            } else {
                // Game is full - become spectator
                player.setGameMode(GameMode.SPECTATOR);
                Location spectatorLoc = game.getArena().getCenterSpawn();
                player.teleport(spectatorLoc);

                player.sendMessage("§7観戦モードでゲームに参加しました");
                plugin.getLogger().info(player.getName() + " は満員のため観戦モードで参加しました");
            }
        } else {
            // Game in progress - join as spectator
            player.setGameMode(GameMode.SPECTATOR);
            Location spectatorLoc = game.getArena().getCenterSpawn();
            player.teleport(spectatorLoc);

            player.sendMessage("§7観戦モードでゲームに参加しました");
            plugin.getLogger().info(player.getName() + " がゲーム進行中のため観戦モードで参加しました");
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
