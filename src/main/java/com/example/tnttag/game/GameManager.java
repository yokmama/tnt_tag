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
            return null; // Game already exists for this arena
        }
        
        GameInstance game = new GameInstance(plugin, arena);
        games.put(arena.getName(), game);
        
        return game;
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
            return false; // Already in a game
        }
        
        if (game.addPlayer(player)) {
            playerGames.put(player.getUniqueId(), game);
            return true;
        }
        
        return false;
    }
    
    /**
     * Remove a player from their game
     */
    public void leaveGame(Player player) {
        GameInstance game = getPlayerGame(player);
        if (game != null) {
            game.removePlayer(player);
            playerGames.remove(player.getUniqueId());
            
            // If game is empty and in waiting state, remove it
            if (game.getPlayers().isEmpty() && game.getState() == GameState.WAITING) {
                removeGame(game);
            }
        }
    }
    
    /**
     * Start a game
     */
    public void startGame(GameInstance game) {
        game.start();
    }
    
    /**
     * Stop a game
     */
    public void stopGame(GameInstance game) {
        game.endGame(null, TNTTagEndEvent.EndReason.FORCE_STOPPED);
        removeGame(game);
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
        for (GameInstance game : new ArrayList<>(games.values())) {
            stopGame(game);
        }
        
        games.clear();
        playerGames.clear();
        
        plugin.getLogger().info("すべてのゲームを停止しました");
    }
}
