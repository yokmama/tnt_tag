package com.example.tnttag.game;

import com.example.tnttag.TNTTagPlugin;
import com.example.tnttag.arena.Arena;
import com.example.tnttag.events.TNTTagEndEvent;
import org.bukkit.entity.Player;

import java.util.*;

/**
 * Manages all active game instances
 */
public class GameManager {
    
    private final TNTTagPlugin plugin;
    private final Map<String, GameInstance> games; // Arena name -> Game
    private final Map<UUID, GameInstance> playerGames; // Player UUID -> Game
    
    public GameManager(TNTTagPlugin plugin) {
        this.plugin = plugin;
        this.games = new HashMap<>();
        this.playerGames = new HashMap<>();
    }
    
    /**
     * Create a new game for an arena
     */
    public GameInstance createGame(Arena arena) {
        if (games.containsKey(arena.getName())) {
            plugin.getLogger().warning("ゲームは既に存在しています: " + arena.getName());
            return null; // Game already exists for this arena
        }

        try {
            GameInstance game = new GameInstance(plugin, arena);
            games.put(arena.getName(), game);
            plugin.getLogger().info("ゲームを作成しました: " + arena.getName());
            return game;
        } catch (Exception e) {
            plugin.getLogger().severe("ゲーム作成エラー: " + arena.getName());
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Get a game by arena name
     */
    public GameInstance getGame(String arenaName) {
        return games.get(arenaName);
    }
    
    /**
     * Get a game by arena
     */
    public GameInstance getGame(Arena arena) {
        return games.get(arena.getName());
    }
    
    /**
     * Get the game a player is in
     */
    public GameInstance getPlayerGame(Player player) {
        return playerGames.get(player.getUniqueId());
    }
    
    /**
     * Check if a player is in a game
     */
    public boolean isInGame(Player player) {
        return playerGames.containsKey(player.getUniqueId());
    }
    
    /**
     * Add a player to a game
     */
    public boolean joinGame(Player player, GameInstance game) {
        if (isInGame(player)) {
            if (plugin.getConfigManager().isDebug()) {
                plugin.getLogger().info(player.getName() + " は既にゲームに参加しています");
            }
            return false; // Already in a game
        }

        try {
            if (game.addPlayer(player)) {
                playerGames.put(player.getUniqueId(), game);
                plugin.getLogger().info(player.getName() + " がゲームに参加しました: " + game.getArena().getName());
                return true;
            }
        } catch (Exception e) {
            plugin.getLogger().severe("プレイヤー参加エラー: " + player.getName());
            e.printStackTrace();
        }

        return false;
    }
    
    /**
     * Remove a player from their game
     */
    public void leaveGame(Player player) {
        GameInstance game = getPlayerGame(player);
        if (game != null) {
            try {
                game.removePlayer(player);
                playerGames.remove(player.getUniqueId());
                plugin.getLogger().info(player.getName() + " がゲームから退出しました: " + game.getArena().getName());

                // If game is empty and in waiting state, remove it
                if (game.getPlayers().isEmpty() && game.getState() == GameState.WAITING) {
                    plugin.getLogger().info("空のゲームを削除します: " + game.getArena().getName());
                    removeGame(game);
                }
            } catch (Exception e) {
                plugin.getLogger().severe("プレイヤー退出エラー: " + player.getName());
                e.printStackTrace();
            }
        }
    }
    
    /**
     * Start a game
     */
    public void startGame(GameInstance game) {
        try {
            game.start();
            plugin.getLogger().info("ゲームを開始しました: " + game.getArena().getName());
        } catch (Exception e) {
            plugin.getLogger().severe("ゲーム開始エラー: " + game.getArena().getName());
            e.printStackTrace();
        }
    }

    /**
     * Stop a game
     */
    public void stopGame(GameInstance game) {
        try {
            game.endGame(null, TNTTagEndEvent.EndReason.FORCE_STOPPED);
            removeGame(game);
            plugin.getLogger().info("ゲームを停止しました: " + game.getArena().getName());
        } catch (Exception e) {
            plugin.getLogger().severe("ゲーム停止エラー: " + game.getArena().getName());
            e.printStackTrace();
        }
    }
    
    /**
     * Remove a game
     */
    private void removeGame(GameInstance game) {
        games.remove(game.getArena().getName());
        
        // Remove all players from player games map
        for (Player player : game.getPlayers()) {
            playerGames.remove(player.getUniqueId());
        }
    }
    
    /**
     * Get all active games
     */
    public Collection<GameInstance> getAllGames() {
        return new ArrayList<>(games.values());
    }
    
    /**
     * Stop all active games
     */
    public void stopAllGames() {
        int count = games.size();
        if (count == 0) {
            return;
        }

        plugin.getLogger().info("すべてのゲームを停止しています (" + count + " 個)...");

        for (GameInstance game : new ArrayList<>(games.values())) {
            try {
                stopGame(game);
            } catch (Exception e) {
                plugin.getLogger().severe("ゲーム停止エラー: " + game.getArena().getName());
                e.printStackTrace();
            }
        }

        games.clear();
        playerGames.clear();

        plugin.getLogger().info("すべてのゲームを停止しました");
    }
}
