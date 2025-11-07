package com.example.tnttag.game;

import com.example.tnttag.TNTTagPlugin;
import com.example.tnttag.arena.Arena;
import com.example.tnttag.events.*;
import com.example.tnttag.player.PlayerGameData;
import com.example.tnttag.stats.GameStatistics;
import com.example.tnttag.stats.ResultsManager;
import com.example.tnttag.stats.StatsManager;
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

        // Auto-start logic: check if minimum player count is reached
        int minPlayers = plugin.getConfigManager().getMinPlayers();
        if (players.size() >= minPlayers) {
            // Verify we're still in WAITING state (prevent race conditions)
            if (state == GameState.WAITING) {
                plugin.getLogger().info("最小プレイヤー数に達しました。ゲームを自動開始します (" + players.size() + "/" + maxPlayers + "人)");
                start();
            }
        }

        return true;
    }
    
    /**
     * Remove a player from the game
     */
    public void removePlayer(Player player) {
        players.remove(player);

        // Remove HUD elements
        plugin.getHUDManager().getScoreboardManager().removeScoreboard(player);
        plugin.getHUDManager().getBossBarManager().hideBossBar(player);

        // Remove player data and effects
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
            plugin.getLogger().warning("ゲームは既に開始されています: " + arena.getName());
            return;
        }

        state = GameState.STARTING;
        plugin.getLogger().info("ゲーム開始: " + arena.getName() + " (プレイヤー: " + players.size() + "人)");

        try {
            // Setup arena
            arena.setupWorldBorder();

            // Fire start event
            TNTTagStartEvent event = new TNTTagStartEvent(this);
            Bukkit.getPluginManager().callEvent(event);

            // Start countdown
            startCountdown();
        } catch (Exception e) {
            plugin.getLogger().severe("ゲーム開始エラー: " + arena.getName());
            e.printStackTrace();
            state = GameState.WAITING;
        }
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
                // Check if player count dropped below minimum (countdown cancellation)
                int minPlayers = plugin.getConfigManager().getMinPlayers();
                if (players.size() < minPlayers) {
                    countdownTask.cancel();
                    state = GameState.WAITING;

                    // Notify remaining players
                    String message = plugin.getMessageManager().getMessageWithPrefix("errors.not_enough_players",
                        plugin.getMessageManager().createPlaceholders("min", String.valueOf(minPlayers)));
                    for (Player player : players) {
                        player.sendMessage(message);
                    }

                    plugin.getLogger().info("カウントダウンがキャンセルされました（人数不足: " + players.size() + "/" + minPlayers + "人）");
                    return;
                }

                if (remaining <= 0) {
                    countdownTask.cancel();

                    // Play game start effects
                    for (Player player : players) {
                        plugin.getHUDManager().getTitleManager().sendGameStart(player);
                    }
                    plugin.getEffectManager().playGameStartEffects(arena.getCenterSpawn(), players);

                    startFirstRound();
                    return;
                }

                // Update action bar for all players
                for (Player player : players) {
                    plugin.getHUDManager().getActionBarManager().sendCountdown(player, remaining);
                }

                // Play sounds based on remaining time
                if (remaining >= 4 && remaining <= 10) {
                    // Tick sound (pitch 1.0) for 10-4 seconds
                    for (Player player : players) {
                        player.playSound(player.getLocation(), org.bukkit.Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, 1.0f);
                    }
                } else if (remaining >= 1 && remaining <= 3) {
                    // High-pitch sound (pitch 2.0) for 3-1 seconds
                    for (Player player : players) {
                        player.playSound(player.getLocation(), org.bukkit.Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, 2.0f);
                    }

                    // Display title for 3, 2, 1
                    for (Player player : players) {
                        plugin.getHUDManager().getTitleManager().sendCountdownTitle(player, remaining);
                    }
                }

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
        plugin.getLogger().info("ラウンド " + currentRound + " 開始: 生存者 " + alivePlayers.size() + "人");

        // Check if only one player remains
        if (alivePlayers.size() <= 1) {
            Player winner = alivePlayers.isEmpty() ? null : alivePlayers.iterator().next();
            plugin.getLogger().info("勝者決定: " + (winner != null ? winner.getName() : "なし"));
            endGame(winner, TNTTagEndEvent.EndReason.ALL_ROUNDS_COMPLETE);
            return;
        }

        try {
            // Select TNT holders
            int tntHolderCount = config.calculateTNTHolders(alivePlayers.size());
            Set<Player> tntHolders = selectRandomPlayers(alivePlayers, tntHolderCount);
            plugin.getLogger().info("TNT保持者: " + tntHolderCount + "人選出");

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
        } catch (Exception e) {
            plugin.getLogger().severe("ラウンド開始エラー: ラウンド " + currentRound);
            e.printStackTrace();
            endGame(null, TNTTagEndEvent.EndReason.NO_SURVIVORS);
        }
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
        plugin.getLogger().info("ラウンド " + currentRound + " 終了");

        try {
            // Get TNT holders (victims)
            List<Player> victims = new ArrayList<>(activeRound.getTntHolders());
            plugin.getLogger().info("脱落者: " + victims.size() + "人");

            // Fire explosion event
            TNTExplosionEvent explosionEvent = new TNTExplosionEvent(victims, currentRound);
            Bukkit.getPluginManager().callEvent(explosionEvent);

            // Eliminate victims
            for (Player victim : victims) {
                PlayerGameData data = plugin.getPlayerManager().getPlayerData(victim);
                data.setAlive(false);
                data.setRoundEliminated(currentRound);
                victim.setGameMode(GameMode.SPECTATOR);

                if (plugin.getConfigManager().isDebug()) {
                    plugin.getLogger().info(victim.getName() + " が脱落しました");
                }
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
        } catch (Exception e) {
            plugin.getLogger().severe("ラウンド終了エラー: ラウンド " + currentRound);
            e.printStackTrace();
        }
    }
    
    /**
     * End the game
     */
    public void endGame(Player winner, TNTTagEndEvent.EndReason reason) {
        state = GameState.ENDING;
        plugin.getLogger().info("ゲーム終了: " + arena.getName() + " (理由: " + reason + ", 勝者: " + (winner != null ? winner.getName() : "なし") + ")");

        try {
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

            // Update statistics for all players
            StatsManager statsManager = plugin.getPlayerManager().getStatsManager();

            for (Player player : players) {
                try {
                    PlayerGameData data = plugin.getPlayerManager().getPlayerData(player);
                    GameStatistics stats = statsManager.getStats(player);

                    // Increment games played
                    stats.incrementGamesPlayed();

                    // Add rounds survived
                    stats.addRoundsSurvived(data.getRoundsSurvived());

                    // Track tags
                    stats.addTntTagsGiven(data.getTntTagsGiven());
                    stats.addTntTagsReceived(data.getTntTagsReceived());

                    // Calculate survival time (rounds survived * average round duration)
                    double survivalTime = data.getRoundsSurvived() * 35.0; // Average 35 seconds per round
                    stats.addSurvivalTime(survivalTime);

                    // Check if this player won
                    if (winner != null && player.getUniqueId().equals(winner.getUniqueId())) {
                        stats.incrementWins();
                    }

                    // Save stats
                    statsManager.saveStats(stats);
                } catch (Exception e) {
                    plugin.getLogger().severe("統計保存エラー: " + player.getName());
                    e.printStackTrace();
                }
            }

            // Display results
            ResultsManager resultsManager = new ResultsManager(plugin);
            resultsManager.displayResults(this, winner);

            // Cleanup
            cleanup();

            // Remove this game from GameManager
            plugin.getGameManager().removeGameInstance(this);

            plugin.getLogger().info("ゲームクリーンアップ完了: " + arena.getName());
        } catch (Exception e) {
            plugin.getLogger().severe("ゲーム終了エラー: " + arena.getName());
            e.printStackTrace();
            // Force cleanup even if errors occurred
            try {
                cleanup();
                // Remove this game from GameManager even on error
                plugin.getGameManager().removeGameInstance(this);
            } catch (Exception ex) {
                plugin.getLogger().severe("クリーンアップエラー: " + arena.getName());
                ex.printStackTrace();
            }
        }
    }
    
    /**
     * Cleanup game resources
     */
    private void cleanup() {
        plugin.getLogger().info("クリーンアップ開始: " + arena.getName() + " (プレイヤー数: " + players.size() + ")");

        // Remove world border
        arena.removeWorldBorder();

        // Create a copy of players list to avoid ConcurrentModificationException
        List<Player> playersCopy = new ArrayList<>(players);

        // Remove effects and restore players
        for (Player player : playersCopy) {
            // Remove HUD elements
            plugin.getHUDManager().getScoreboardManager().removeScoreboard(player);
            plugin.getHUDManager().getBossBarManager().hideBossBar(player);

            // Remove effects and restore player state
            plugin.getPlayerManager().removeAllEffects(player);
            player.setGameMode(GameMode.SURVIVAL);
            plugin.getPlayerManager().removePlayerData(player);

            plugin.getLogger().info("プレイヤークリーンアップ完了: " + player.getName());
        }

        // Clear all player references from GameManager
        plugin.getGameManager().clearPlayersFromGame(this);
        plugin.getLogger().info("GameManager.playerGamesマップをクリアしました");

        // Clear references
        players.clear();
        activeRound = null;

        plugin.getLogger().info("全参照をクリアしました: " + arena.getName());
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
