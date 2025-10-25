package com.example.tnttag.hud;

import com.example.tnttag.TNTTagPlugin;
import com.example.tnttag.game.GameInstance;
import com.example.tnttag.game.GameState;
import com.example.tnttag.game.Round;
import com.example.tnttag.player.PlayerGameData;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

/**
 * Manages action bar display for players
 */
public class ActionBarManager {
    
    private final TNTTagPlugin plugin;
    private BukkitTask updateTask;
    
    public ActionBarManager(TNTTagPlugin plugin) {
        this.plugin = plugin;
    }
    
    /**
     * Start the action bar update task (runs every 10 ticks = 0.5 seconds = 2/sec)
     */
    public void startUpdateTask() {
        if (updateTask != null) {
            updateTask.cancel();
        }
        
        updateTask = Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, () -> {
            updateAll();
        }, 0L, 10L); // Every 0.5 seconds
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
     * Update all player action bars
     */
    public void updateAll() {
        for (GameInstance game : plugin.getGameManager().getAllGames()) {
            for (Player player : game.getPlayers()) {
                updatePlayerActionBar(player, game);
            }
        }
    }
    
    /**
     * Update a specific player's action bar
     */
    public void updatePlayerActionBar(Player player, GameInstance game) {
        PlayerGameData data = plugin.getPlayerManager().getPlayerData(player);
        GameState state = game.getState();
        
        String message = "";
        
        if (state == GameState.WAITING) {
            message = "§7ゲーム開始を待っています...";
            
        } else if (state == GameState.STARTING) {
            message = "§eまもなくゲーム開始！";
            
        } else if (state == GameState.IN_GAME || state == GameState.ROUND_ENDING) {
            Round round = game.getActiveRound();
            
            if (round != null) {
                int remainingTime = round.getRemainingTime();
                
                if (!data.isAlive()) {
                    // Spectating
                    message = "§7観戦中 | §e生存者: " + game.getAlivePlayers().size() + "人";
                    
                } else if (data.isTNTHolder()) {
                    // TNT holder
                    if (remainingTime <= 3) {
                        // Explosion countdown
                        message = "§4💥 爆発まで " + remainingTime + "... 💥";
                    } else {
                        message = "§c⚠ TNTを持っています！他の人にタッチ！⚠";
                    }
                    
                } else {
                    // Normal player
                    message = "§a残り時間: " + remainingTime + "秒 §7| §e生存者: " + game.getAlivePlayers().size() + "人";
                }
            }
        }
        
        // Send action bar
        player.sendActionBar(Component.text(message));
    }
    
    /**
     * Cleanup
     */
    public void cleanup() {
        stopUpdateTask();
    }
}
