package com.example.tnttag.hud;

import com.example.tnttag.TNTTagPlugin;
import com.example.tnttag.game.GameInstance;
import com.example.tnttag.game.Round;
import com.example.tnttag.player.PlayerGameData;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Manages boss bar display for TNT holders
 *
 * Updated: Hide boss bar from TNT holders when remaining time is below threshold
 * to prevent last-second tag strategies and encourage active gameplay.
 */
public class BossBarManager {

    /**
     * Threshold in seconds below which boss bar is hidden from TNT holders.
     * When remaining time is less than this value, TNT holders cannot see the timer,
     * encouraging them to pass the TNT earlier rather than waiting until the last moment.
     */
    private static final int BOSSBAR_HIDE_THRESHOLD = 10;

    private final TNTTagPlugin plugin;
    private final Map<UUID, BossBar> playerBossBars;
    private BukkitTask updateTask;
    
    public BossBarManager(TNTTagPlugin plugin) {
        this.plugin = plugin;
        this.playerBossBars = new HashMap<>();
    }
    
    /**
     * Start the boss bar update task (runs every 2 ticks = 0.1 seconds = 10/sec)
     */
    public void startUpdateTask() {
        if (updateTask != null) {
            updateTask.cancel();
        }

        // Run on main thread to avoid async issues
        updateTask = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            updateAll();
        }, 0L, 2L); // Every 0.1 seconds
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
     * Update all player boss bars based on remaining time and player state.
     *
     * When remaining time is at or above the threshold:
     * - TNT holders see the boss bar (current behavior)
     * - Non-holders do not see the boss bar
     *
     * When remaining time is below the threshold:
     * - TNT holders do NOT see the boss bar (to prevent last-second strategies)
     * - Non-holders (alive) see the boss bar (to know when explosion is near)
     * - Spectators see the boss bar (for viewing experience)
     */
    public void updateAll() {
        for (GameInstance game : plugin.getGameManager().getAllGames()) {
            Round round = game.getActiveRound();

            if (round == null) {
                continue;
            }

            int remainingTime = round.getRemainingTime();
            boolean belowThreshold = remainingTime < BOSSBAR_HIDE_THRESHOLD;

            for (Player player : game.getPlayers()) {
                PlayerGameData data = plugin.getPlayerManager().getPlayerData(player);
                boolean isTNTHolder = data.isTNTHolder();
                boolean isAlive = data.isAlive();
                boolean isSpectator = player.getGameMode() == GameMode.SPECTATOR;

                if (belowThreshold) {
                    // Below threshold: Hide from TNT holders, show to non-holders and spectators
                    if (isTNTHolder && isAlive) {
                        hideBossBar(player);
                    } else if (isAlive || isSpectator) {
                        showBossBar(player, round);
                    } else {
                        hideBossBar(player);
                    }
                } else {
                    // At or above threshold: Original behavior (TNT holders only)
                    if (isTNTHolder && isAlive) {
                        showBossBar(player, round);
                    } else {
                        hideBossBar(player);
                    }
                }
            }
        }
    }
    
    /**
     * Show boss bar to a player
     */
    private void showBossBar(Player player, Round round) {
        BossBar bossBar = playerBossBars.get(player.getUniqueId());
        
        if (bossBar == null) {
            bossBar = Bukkit.createBossBar(
                "⚠ TNTを持っています! ⚠",
                BarColor.RED,
                BarStyle.SOLID
            );
            bossBar.addPlayer(player);
            playerBossBars.put(player.getUniqueId(), bossBar);
        }
        
        // Update progress
        double progress = (double) round.getRemainingTime() / round.getConfig().getDuration();
        progress = Math.max(0.0, Math.min(1.0, progress)); // Clamp to 0-1
        bossBar.setProgress(progress);
    }
    
    /**
     * Hide boss bar from a player
     */
    public void hideBossBar(Player player) {
        BossBar bossBar = playerBossBars.remove(player.getUniqueId());
        
        if (bossBar != null) {
            bossBar.removePlayer(player);
            bossBar.removeAll();
        }
    }
    
    /**
     * Cleanup all boss bars
     */
    public void cleanup() {
        stopUpdateTask();
        
        for (BossBar bossBar : playerBossBars.values()) {
            bossBar.removeAll();
        }
        
        playerBossBars.clear();
    }
}
