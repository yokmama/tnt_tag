package com.example.tnttag.game;

import com.example.tnttag.TNTTagPlugin;
import com.example.tnttag.arena.Arena;
import com.example.tnttag.events.*;
import com.example.tnttag.player.PlayerGameData;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Represents a single game instance
 */
public class GameInstance {
    
    private final UUID id;
    private final TNTTagPlugin plugin;
    private final Arena arena;
    private final Set<Player> players;
    private GameState state;
    private int currentRound;
    private Round activeRound;
    private BukkitTask countdownTask;
    private BukkitTask gameTask;
    
    public GameInstance(TNTTagPlugin plugin, Arena arena) {
        this.id = UUID.randomUUID();
        this.plugin = plugin;
        this.arena = arena;
        this.players = new HashSet<>();
        this.state = GameState.WAITING;
        this.currentRound = 0;
    }
    
    /**
     * Get the game ID
     */
    public UUID getId() {
        return id;
    }
    
    /**
     * Get the arena
     */
    public Arena getArena() {
        return arena;
    }
    
    /**
     * Get all players
     */
    public Set<Player> getPlayers() {
        return new HashSet<>(players);
    }
    
    /**
     * Get alive players
     */
    public Set<Player> getAlivePlayers() {
        return players.stream()
            .filter(p -> plugin.getPlayerManager().getPlayerData(p).isAlive())
            .collect(Collectors.toSet());
    }
    
    /**
     * Get game state
     */
    public GameState getState() {
        return state;
    }
    
    /**
     * Get current round number
     */
    public int getCurrentRound() {
        return currentRound;
    }
    
    /**
     * Get active round
     */
    public Round getActiveRound() {
        return activeRound;
    }
    
    /**
     * Add a player to the game
     */
    public boolean addPlayer(Player player) {
        if (state != GameState.WAITING) {
            return false;
        }
        
        int maxPlayers = plugin.getConfigManager().getMaxPlayers();
        if (players.size() >= maxPlayers) {
            return false;
        }
        
        players.add(player);
        plugin.getPlayerManager().getPlayerData(player); // Initialize data
        
        return true;
    }
    
    /**
     * Remove a player from the game
     */
    public void removePlayer(Player player) {
        players.remove(player);
        plugin.getPlayerManager().removePlayerData(player);
        plugin.getPlayerManager().removeAllEffects(player);
        
        // If player was TNT holder, redistribute TNT
        if (activeRound != null && activeRound.isTNTHolder(player)) {
            activeRound.removeTNTHolder(player);
            redistributeTNT();
        }
    }
    
    /**
     * Start the game
     */
    public void start() {
        if (state != GameState.WAITING) {
            return;
        }
        
        state = GameState.STARTING;
        
        // Setup arena
        arena.setupWorldBorder();
        
        // Fire start event
        TNTTagStartEvent event = new TNTTagStartEvent(this);
        Bukkit.getPluginManager().callEvent(event);
        
        // Start countdown
        startCountdown();
    }
    
    /**
     * Start countdown before first round
     */
    private void startCountdown() {
        int countdown = plugin.getConfigManager().getCountdown();
        
        countdownTask = Bukkit.getScheduler().runTaskTimer(plugin, new Runnable() {
            int remaining = countdown;
            
            @Override
            public void run() {
                if (remaining <= 0) {
                    countdownTask.cancel();
                    startFirstRound();
                    return;
                }
                
                // TODO: Display countdown to players
                remaining--;
            }
        }, 0L, 20L); // Every second
    }
    
    /**
     * Start the first round
     */
    private void startFirstRound() {
        state = GameState.IN_GAME;
        currentRound = 1;
        startRound();
    }
    
    /**
     * Start a new round
     */
    private void startRound() {
        RoundConfig config = plugin.getConfigManager().getRoundConfig(currentRound);
        if (config == null) {
            plugin.getLogger().warning("ラウンド設定が見つかりません: " + currentRound);
            endGame(null, TNTTagEndEvent.EndReason.NO_SURVIVORS);
            return;
        }
        
        Set<Player> alivePlayers = getAlivePlayers();
        
        // Check if only one player remains
        if (alivePlayers.size() <= 1) {
            Player winner = alivePlayers.isEmpty() ? null : alivePlayers.iterator().next();
            endGame(winner, TNTTagEndEvent.EndReason.ALL_ROUNDS_COMPLETE);
            return;
        }
        
        // Select TNT holders
        int tntHolderCount = config.calculateTNTHolders(alivePlayers.size());
        Set<Player> tntHolders = selectRandomPlayers(alivePlayers, tntHolderCount);
        
        // Create round
        activeRound = new Round(config, tntHolders);
        
        // Teleport players to center
        if (plugin.getConfigManager().isSpawnTeleport()) {
            for (Player player : alivePlayers) {
                player.teleport(arena.getCenterSpawn());
            }
        }
        
        // Apply effects
        for (Player player : alivePlayers) {
            if (tntHolders.contains(player)) {
                plugin.getPlayerManager().setTNTHolder(player, true);
            } else {
                plugin.getPlayerManager().setTNTHolder(player, false);
            }
            
            // Apply glowing if configured
            if (config.isGlowing()) {
                plugin.getPlayerManager().applyGlowingEffect(player);
            }
        }
        
        // Fire round start event
        TNTTagRoundStartEvent event = new TNTTagRoundStartEvent(
            this, currentRound, tntHolders, config.getDuration()
        );
        Bukkit.getPluginManager().callEvent(event);
        
        // Start round timer
        startRoundTimer();
    }
    
    /**
     * Start the round timer
     */
    private void startRoundTimer() {
        gameTask = Bukkit.getScheduler().runTaskTimer(plugin, new Runnable() {
            @Override
            public void run() {
                if (activeRound == null) {
                    gameTask.cancel();
                    return;
                }
                
                activeRound.decrementTime();
                
                if (activeRound.hasEnded()) {
                    gameTask.cancel();
                    endRound();
                }
            }
        }, 0L, 20L); // Every second
    }
    
    /**
     * End the current round
     */
    private void endRound() {
        if (activeRound == null) {
            return;
        }
        
        state = GameState.ROUND_ENDING;
        
        // Get TNT holders (victims)
        List<Player> victims = new ArrayList<>(activeRound.getTntHolders());
        
        // Fire explosion event
        TNTExplosionEvent explosionEvent = new TNTExplosionEvent(victims, currentRound);
        Bukkit.getPluginManager().callEvent(explosionEvent);
        
        // Eliminate victims
        for (Player victim : victims) {
            PlayerGameData data = plugin.getPlayerManager().getPlayerData(victim);
            data.setAlive(false);
            data.setRoundEliminated(currentRound);
            victim.setGameMode(GameMode.SPECTATOR);
            
            // TODO: Play explosion effects
        }
        
        // Update survivors
        for (Player player : getAlivePlayers()) {
            PlayerGameData data = plugin.getPlayerManager().getPlayerData(player);
            data.incrementRoundsSurvived();
        }
        
        // Check if game should end
        Set<Player> alivePlayers = getAlivePlayers();
        if (alivePlayers.size() <= 1 || currentRound >= 6) {
            Player winner = alivePlayers.size() == 1 ? alivePlayers.iterator().next() : null;
            endGame(winner, TNTTagEndEvent.EndReason.ALL_ROUNDS_COMPLETE);
            return;
        }
        
        // Schedule next round
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            currentRound++;
            state = GameState.IN_GAME;
            startRound();
        }, 60L); // 3 seconds delay
    }
    
    /**
     * End the game
     */
    public void endGame(Player winner, TNTTagEndEvent.EndReason reason) {
        state = GameState.ENDING;
        
        // Cancel tasks
        if (countdownTask != null) {
            countdownTask.cancel();
        }
        if (gameTask != null) {
            gameTask.cancel();
        }
        
        // Fire end event
        TNTTagEndEvent event = new TNTTagEndEvent(this, winner, reason);
        Bukkit.getPluginManager().callEvent(event);
        
        // Cleanup
        cleanup();
    }
    
    /**
     * Cleanup game resources
     */
    private void cleanup() {
        // Remove world border
        arena.removeWorldBorder();
        
        // Remove effects and restore players
        for (Player player : players) {
            plugin.getPlayerManager().removeAllEffects(player);
            player.setGameMode(GameMode.SURVIVAL);
            plugin.getPlayerManager().removePlayerData(player);
        }
        
        // Clear references
        players.clear();
        activeRound = null;
    }
    
    /**
     * Select random players from a set
     */
    private Set<Player> selectRandomPlayers(Set<Player> from, int count) {
        List<Player> list = new ArrayList<>(from);
        Collections.shuffle(list);
        return new HashSet<>(list.subList(0, Math.min(count, list.size())));
    }
    
    /**
     * Redistribute TNT if a holder disconnects
     */
    private void redistributeTNT() {
        if (activeRound == null || activeRound.getTntHolders().isEmpty()) {
            Set<Player> alivePlayers = getAlivePlayers();
            if (!alivePlayers.isEmpty()) {
                Player newHolder = selectRandomPlayers(alivePlayers, 1).iterator().next();
                activeRound.addTNTHolder(newHolder);
                plugin.getPlayerManager().setTNTHolder(newHolder, true);
            }
        }
    }
}
