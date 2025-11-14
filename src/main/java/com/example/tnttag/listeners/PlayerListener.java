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
     * Handle player join - all players automatically participate in the game
     */
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        // Get or create single game instance
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

        GameState state = game.getState();

        // Check if player was disconnected during active game
        PlayerGameData savedData = disconnectedPlayers.containsKey(uuid) ?
            plugin.getPlayerManager().getPlayerData(player) : null;

        if (savedData != null && state != GameState.WAITING && state != GameState.ENDING) {
            // Reconnection - restore previous state
            disconnectedPlayers.remove(uuid);
            player.teleport(game.getArena().getCenterSpawn());

            if (savedData.isAlive()) {
                player.setGameMode(GameMode.ADVENTURE);
                if (savedData.isTNTHolder()) {
                    plugin.getPlayerManager().setTNTHolder(player, true);
                } else {
                    plugin.getPlayerManager().setTNTHolder(player, false);
                }
                player.sendMessage("§aゲームに復帰しました！");
            } else {
                player.setGameMode(GameMode.SPECTATOR);
                player.sendMessage("§7観戦モードで復帰しました。");
            }
            plugin.getLogger().info(player.getName() + " が再接続しました");
        } else {
            // New join or game ended
            disconnectedPlayers.remove(uuid);

            if (state == GameState.WAITING || state == GameState.STARTING) {
                // Initialize as active participant
                PlayerGameData data = plugin.getPlayerManager().getPlayerData(player);
                data.setAlive(true);
                player.setGameMode(GameMode.ADVENTURE);
                player.teleport(game.getArena().getCenterSpawn());
                player.sendMessage("§aアリーナ '" + game.getArena().getName() + "' へようこそ");
                plugin.getLogger().info(player.getName() + " がゲームに参加しました");

                // Check if we should auto-start
                game.checkAutoStart();
            } else {
                // Game in progress - join as spectator
                PlayerGameData data = plugin.getPlayerManager().getPlayerData(player);
                data.setAlive(false);
                player.setGameMode(GameMode.SPECTATOR);
                player.teleport(game.getArena().getCenterSpawn());
                player.sendMessage("§7観戦モードでゲームに参加しました");
                plugin.getLogger().info(player.getName() + " が観戦者として参加しました");
            }
        }
    }

    /**
     * Handle player disconnect - store game state for potential rejoin
     */
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        GameInstance game = plugin.getGameManager().getSingleGameInstance();

        if (game != null && game.getState() != GameState.WAITING && game.getState() != GameState.ENDING) {
            // Store UUID to indicate player was in active game
            disconnectedPlayers.put(player.getUniqueId(), game);
            plugin.getLogger().info(player.getName() + " が切断しました。ゲーム情報を保持します。");
        }

        // Clean up HUD elements
        plugin.getHUDManager().getScoreboardManager().removeScoreboard(player);
        plugin.getHUDManager().getBossBarManager().hideBossBar(player);

        // Note: PlayerGameData is kept for reconnection
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
