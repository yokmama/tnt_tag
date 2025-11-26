package com.example.tnttag.hud;

import com.example.tnttag.TNTTagPlugin;
import com.example.tnttag.game.GameInstance;
import com.example.tnttag.game.GameState;
import com.example.tnttag.game.Round;
import com.example.tnttag.player.PlayerGameData;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

/**
 * Manages action bar display for players
 *
 * Updated: Hide remaining time from TNT holders when below threshold
 * to prevent last-second tag strategies.
 */
public class ActionBarManager {

    /**
     * Threshold in seconds below which remaining time is hidden from TNT holders.
     * This should match BossBarManager.BOSSBAR_HIDE_THRESHOLD.
     */
    private static final int TIME_HIDE_THRESHOLD = 10;

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

        // Run on main thread to avoid async issues
        updateTask = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
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
     * Send countdown message to player's action bar
     */
    public void sendCountdown(Player player, int seconds) {
        String message = plugin.getMessageManager().getMessage("hud.countdown",
            plugin.getMessageManager().createPlaceholders("seconds", String.valueOf(seconds)));
        player.sendActionBar(LegacyComponentSerializer.legacySection().deserialize(message));
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
                boolean belowThreshold = remainingTime < TIME_HIDE_THRESHOLD;

                if (!data.isAlive()) {
                    // Spectating - always show time (spectators can't influence the game)
                    message = "§7観戦中 | §a残り: " + remainingTime + "秒 §7| §e生存者: " + game.getAlivePlayers().size() + "人";

                } else if (data.isTNTHolder()) {
                    // TNT holder - hide time when below threshold
                    if (remainingTime <= 3) {
                        // Explosion countdown (final warning - always show)
                        message = "§4💥 爆発まで " + remainingTime + "... 💥";
                    } else if (belowThreshold) {
                        // Below threshold - hide exact time to prevent last-second strategies
                        message = "§c⚠ TNTを持っています！急いでタッチ！⚠";
                    } else {
                        // Above threshold - show time
                        message = "§c⚠ TNTを持っています！残り: " + remainingTime + "秒 ⚠";
                    }

                } else {
                    // Normal player (non-holder)
                    if (belowThreshold) {
                        // Below threshold - hide time so TNT holders can't ask others
                        message = "§a逃げろ！ §7| §e生存者: " + game.getAlivePlayers().size() + "人";
                    } else {
                        // Above threshold - show time
                        message = "§a残り時間: " + remainingTime + "秒 §7| §e生存者: " + game.getAlivePlayers().size() + "人";
                    }
                }
            }
        }

        // Send action bar with legacy color code support
        player.sendActionBar(LegacyComponentSerializer.legacySection().deserialize(message));
    }
    
    /**
     * Cleanup
     */
    public void cleanup() {
        stopUpdateTask();
    }
}
