package com.example.tnttag.hud;

import com.example.tnttag.TNTTagPlugin;

/**
 * Coordinates all HUD components
 */
public class HUDManager {
    
    private final TNTTagPlugin plugin;
    private final ScoreboardManager scoreboardManager;
    private final ActionBarManager actionBarManager;
    private final BossBarManager bossBarManager;
    private final TitleManager titleManager;
    
    public HUDManager(TNTTagPlugin plugin) {
        this.plugin = plugin;
        this.scoreboardManager = new ScoreboardManager(plugin);
        this.actionBarManager = new ActionBarManager(plugin);
        this.bossBarManager = new BossBarManager(plugin);
        this.titleManager = new TitleManager();
    }
    
    /**
     * Start all HUD update tasks
     */
    public void startAll() {
        scoreboardManager.startUpdateTask();
        actionBarManager.startUpdateTask();
        bossBarManager.startUpdateTask();
        
        plugin.getLogger().info("HUDシステムを開始しました");
    }
    
    /**
     * Stop all HUD update tasks
     */
    public void stopAll() {
        scoreboardManager.stopUpdateTask();
        actionBarManager.stopUpdateTask();
        bossBarManager.stopUpdateTask();
        
        plugin.getLogger().info("HUDシステムを停止しました");
    }
    
    /**
     * Cleanup all HUD components
     */
    public void cleanup() {
        scoreboardManager.cleanup();
        actionBarManager.cleanup();
        bossBarManager.cleanup();
    }
    
    /**
     * Get the scoreboard manager
     */
    public ScoreboardManager getScoreboardManager() {
        return scoreboardManager;
    }
    
    /**
     * Get the action bar manager
     */
    public ActionBarManager getActionBarManager() {
        return actionBarManager;
    }
    
    /**
     * Get the boss bar manager
     */
    public BossBarManager getBossBarManager() {
        return bossBarManager;
    }
    
    /**
     * Get the title manager
     */
    public TitleManager getTitleManager() {
        return titleManager;
    }
}
