package com.example.tnttag.hud;

import com.example.tnttag.TNTTagPlugin;
import com.example.tnttag.game.GameInstance;
import com.example.tnttag.game.Round;
import com.example.tnttag.player.PlayerGameData;
import org.bukkit.Bukkit;
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
 */
public class BossBarManager {
    
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
     * Update all player boss bars
     */
    public void updateAll() {
        for (GameInstance game : plugin.getGameManager().getAllGames()) {
            Round round = game.getActiveRound();
            
            if (round == null) {
                continue;
            }
            
            for (Player player : game.getPlayers()) {
                PlayerGameData data = plugin.getPlayerManager().getPlayerData(player);
                
                if (data.isTNTHolder() && data.isAlive()) {
                    showBossBar(player, round);
                } else {
                    hideBossBar(player);
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
