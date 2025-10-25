package com.example.tnttag;

import com.example.tnttag.config.ConfigManager;
import com.example.tnttag.config.MessageManager;
import com.example.tnttag.game.GameManager;
import com.example.tnttag.player.PlayerManager;
import com.example.tnttag.arena.ArenaManager;
import com.example.tnttag.commands.TNTTagCommandExecutor;
import com.example.tnttag.listeners.PlayerListener;
import com.example.tnttag.listeners.GameListener;
import com.example.tnttag.listeners.TNTTransferListener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

/**
 * TNT TAG - Minecraft Survival Tag Minigame
 * Main plugin class
 * 
 * @author 斉藤ゆうき
 */
public class TNTTagPlugin extends JavaPlugin {
    
    private static TNTTagPlugin instance;
    
    // Managers
    private ConfigManager configManager;
    private MessageManager messageManager;
    private GameManager gameManager;
    private PlayerManager playerManager;
    private ArenaManager arenaManager;
    
    @Override
    public void onEnable() {
        instance = this;
        
        getLogger().info("TNT TAG プラグインを有効化しています...");
        
        // Initialize managers
        try {
            this.configManager = new ConfigManager(this);
            this.messageManager = new MessageManager(this);
            this.arenaManager = new ArenaManager(this);
            this.playerManager = new PlayerManager(this);
            this.gameManager = new GameManager(this);
            
            getLogger().info("マネージャーの初期化完了");
        } catch (Exception e) {
            getLogger().log(Level.SEVERE, "マネージャーの初期化中にエラーが発生しました", e);
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        
        // Register commands
        TNTTagCommandExecutor commandExecutor = new TNTTagCommandExecutor(this);
        getCommand("tnttag").setExecutor(commandExecutor);
        getCommand("tnttag").setTabCompleter(commandExecutor);
        
        // Register event listeners
        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
        getServer().getPluginManager().registerEvents(new GameListener(this), this);
        getServer().getPluginManager().registerEvents(new TNTTransferListener(this), this);
        
        getLogger().info("TNT TAG プラグインが有効化されました！");
    }
    
    @Override
    public void onDisable() {
        getLogger().info("TNT TAG プラグインを無効化しています...");
        
        // Stop all active games
        if (gameManager != null) {
            gameManager.stopAllGames();
        }
        
        // Save arena configurations
        if (arenaManager != null) {
            arenaManager.saveArenas();
        }
        
        // Save player statistics
        if (playerManager != null) {
            playerManager.saveAllStats();
        }
        
        getLogger().info("TNT TAG プラグインが無効化されました");
    }
    
    /**
     * Get plugin instance
     */
    public static TNTTagPlugin getInstance() {
        return instance;
    }
    
    /**
     * Get ConfigManager
     */
    public ConfigManager getConfigManager() {
        return configManager;
    }
    
    /**
     * Get MessageManager
     */
    public MessageManager getMessageManager() {
        return messageManager;
    }
    
    /**
     * Get GameManager
     */
    public GameManager getGameManager() {
        return gameManager;
    }
    
    /**
     * Get PlayerManager
     */
    public PlayerManager getPlayerManager() {
        return playerManager;
    }
    
    /**
     * Get ArenaManager
     */
    public ArenaManager getArenaManager() {
        return arenaManager;
    }
}
