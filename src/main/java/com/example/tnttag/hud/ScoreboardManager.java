package com.example.tnttag.hud;

import com.example.tnttag.TNTTagPlugin;
import com.example.tnttag.game.GameInstance;
import com.example.tnttag.game.GameState;
import com.example.tnttag.game.Round;
import com.example.tnttag.player.PlayerGameData;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Manages scoreboard display for players
 */
public class ScoreboardManager {
    
    private final TNTTagPlugin plugin;
    private final Map<UUID, Scoreboard> playerScoreboards;
    private BukkitTask updateTask;
    
    public ScoreboardManager(TNTTagPlugin plugin) {
        this.plugin = plugin;
        this.playerScoreboards = new HashMap<>();
    }
    
    /**
     * Start the scoreboard update task (runs every 20 ticks = 1 second)
     */
    public void startUpdateTask() {
        if (updateTask != null) {
            updateTask.cancel();
        }

        // Must run on main thread for scoreboard creation
        updateTask = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            updateAll();
        }, 0L, 20L); // Every 1 second
    }
    
    /**
     * Stop the update task
     */
    public void stopUpdateTask() {
        if (updateTask != null) {
            updateTask.cancel();
            updateTask = null;
        }
    }
    
    /**
     * Create a scoreboard for a player
     */
    public void createScoreboard(Player player) {
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        Objective objective = scoreboard.registerNewObjective("tnttag", "dummy", "§c§l=== TNT TAG ===");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        
        player.setScoreboard(scoreboard);
        playerScoreboards.put(player.getUniqueId(), scoreboard);
    }
    
    /**
     * Remove a player's scoreboard
     */
    public void removeScoreboard(Player player) {
        player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
        playerScoreboards.remove(player.getUniqueId());
    }
    
    /**
     * Update all player scoreboards
     */
    public void updateAll() {
        for (GameInstance game : plugin.getGameManager().getAllGames()) {
            for (Player player : game.getPlayers()) {
                updatePlayerScoreboard(player, game);
            }
        }
    }
    
    /**
     * Update a specific player's scoreboard
     */
    public void updatePlayerScoreboard(Player player, GameInstance game) {
        if (!playerScoreboards.containsKey(player.getUniqueId())) {
            createScoreboard(player);
        }
        
        Scoreboard scoreboard = playerScoreboards.get(player.getUniqueId());
        Objective objective = scoreboard.getObjective("tnttag");
        
        if (objective == null) {
            return;
        }
        
        // Clear existing scores
        for (String entry : scoreboard.getEntries()) {
            scoreboard.resetScores(entry);
        }
        
        PlayerGameData data = plugin.getPlayerManager().getPlayerData(player);
        GameState state = game.getState();
        
        int line = 15;
        
        if (state == GameState.WAITING) {
            // Waiting phase
            objective.getScore("§7状態: §e待機中").setScore(line--);
            objective.getScore("§r").setScore(line--);
            objective.getScore("§eプレイヤー: §f" + game.getPlayers().size() + "/" + 
                plugin.getConfigManager().getMaxPlayers() + "人").setScore(line--);
            
        } else if (state == GameState.STARTING) {
            // Countdown phase
            objective.getScore("§7状態: §eまもなく開始").setScore(line--);
            objective.getScore("§r").setScore(line--);
            objective.getScore("§eプレイヤー: §f" + game.getPlayers().size() + "人").setScore(line--);
            
        } else if (state == GameState.IN_GAME || state == GameState.ROUND_ENDING) {
            // In-game phase
            Round round = game.getActiveRound();
            
            if (!data.isAlive()) {
                // Spectating
                objective.getScore("§7状態: §7観戦中").setScore(line--);
                objective.getScore("§r").setScore(line--);
                if (round != null) {
                    objective.getScore("§e現在のラウンド: §f" + round.getRoundNumber() + "/6").setScore(line--);
                    objective.getScore("§e残り時間: §f" + formatTime(round.getRemainingTime())).setScore(line--);
                    objective.getScore("§e生存者: §f" + game.getAlivePlayers().size() + "人").setScore(line--);
                }
            } else {
                // Playing
                objective.getScore("§7状態: §aゲーム中").setScore(line--);
                objective.getScore("§r").setScore(line--);
                
                if (round != null) {
                    objective.getScore("§eラウンド: §f" + round.getRoundNumber() + "/6").setScore(line--);
                    objective.getScore("§e残り時間: §f" + formatTime(round.getRemainingTime())).setScore(line--);
                    objective.getScore("§r ").setScore(line--);
                    objective.getScore("§e生存者: §f" + game.getAlivePlayers().size() + "人").setScore(line--);
                    objective.getScore("§cTNT保持者: §f" + round.getTntHolders().size() + "人").setScore(line--);
                    objective.getScore("§r  ").setScore(line--);
                    
                    if (data.isTNTHolder()) {
                        objective.getScore("§cあなたの状態: §fTNT保持中").setScore(line--);
                    } else {
                        objective.getScore("§aあなたの状態: §f生存中").setScore(line--);
                    }
                }
            }
        }
    }
    
    /**
     * Format time in MM:SS format
     */
    private String formatTime(int seconds) {
        int minutes = seconds / 60;
        int secs = seconds % 60;
        return String.format("%d:%02d", minutes, secs);
    }
    
    /**
     * Cleanup all scoreboards
     */
    public void cleanup() {
        stopUpdateTask();
        
        for (UUID uuid : playerScoreboards.keySet()) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null) {
                removeScoreboard(player);
            }
        }
        
        playerScoreboards.clear();
    }
}
