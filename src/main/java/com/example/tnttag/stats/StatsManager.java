package com.example.tnttag.stats;

import com.example.tnttag.TNTTagPlugin;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

/**
 * Manages player statistics with YAML persistence
 */
public class StatsManager {
    
    private final TNTTagPlugin plugin;
    private final File statsDir;
    private final Map<UUID, GameStatistics> cache;
    
    public StatsManager(TNTTagPlugin plugin) {
        this.plugin = plugin;
        this.statsDir = new File(plugin.getDataFolder(), "stats");
        this.cache = new HashMap<>();
        
        // Create stats directory
        if (!statsDir.exists()) {
            statsDir.mkdirs();
        }
    }
    
    /**
     * Get or create statistics for a player
     */
    public GameStatistics getStats(Player player) {
        return getStats(player.getUniqueId(), player.getName());
    }
    
    /**
     * Get or create statistics by UUID
     */
    public GameStatistics getStats(UUID uuid, String name) {
        // Check cache first
        if (cache.containsKey(uuid)) {
            GameStatistics stats = cache.get(uuid);
            stats.setPlayerName(name); // Update name in case it changed
            return stats;
        }
        
        // Load from file
        GameStatistics stats = loadStats(uuid, name);
        cache.put(uuid, stats);
        return stats;
    }
    
    /**
     * Load statistics from YAML file
     */
    private GameStatistics loadStats(UUID uuid, String name) {
        File statsFile = new File(statsDir, uuid.toString() + ".yml");
        
        if (!statsFile.exists()) {
            return new GameStatistics(uuid, name);
        }
        
        try {
            FileConfiguration config = YamlConfiguration.loadConfiguration(statsFile);
            
            String playerName = config.getString("player.name", name);
            int gamesPlayed = config.getInt("stats.games_played", 0);
            int totalRoundsSurvived = config.getInt("stats.total_rounds_survived", 0);
            int wins = config.getInt("stats.wins", 0);
            int tntTagsGiven = config.getInt("stats.tnt_tags_given", 0);
            int tntTagsReceived = config.getInt("stats.tnt_tags_received", 0);
            double totalSurvivalTime = config.getDouble("stats.total_survival_time", 0.0);
            
            GameStatistics stats = new GameStatistics(uuid, playerName);
            for (int i = 0; i < gamesPlayed; i++) {
                stats.incrementGamesPlayed();
            }
            stats.addRoundsSurvived(totalRoundsSurvived);
            for (int i = 0; i < wins; i++) {
                stats.incrementWins();
            }
            stats.addTntTagsGiven(tntTagsGiven);
            stats.addTntTagsReceived(tntTagsReceived);
            stats.addSurvivalTime(totalSurvivalTime);
            
            return stats;
            
        } catch (Exception e) {
            plugin.getLogger().log(Level.WARNING, "統計ファイルの読み込みエラー: " + uuid, e);
            return new GameStatistics(uuid, name);
        }
    }
    
    /**
     * Save statistics to YAML file
     */
    public void saveStats(GameStatistics stats) {
        File statsFile = new File(statsDir, stats.getPlayerUUID().toString() + ".yml");
        
        try {
            FileConfiguration config = new YamlConfiguration();
            
            config.set("player.uuid", stats.getPlayerUUID().toString());
            config.set("player.name", stats.getPlayerName());
            
            config.set("stats.games_played", stats.getGamesPlayed());
            config.set("stats.total_rounds_survived", stats.getTotalRoundsSurvived());
            config.set("stats.wins", stats.getWins());
            config.set("stats.tnt_tags_given", stats.getTntTagsGiven());
            config.set("stats.tnt_tags_received", stats.getTntTagsReceived());
            config.set("stats.total_survival_time", stats.getTotalSurvivalTime());
            config.set("stats.average_survival_time", stats.getAverageSurvivalTime());
            config.set("stats.win_rate", stats.getWinRate());
            
            config.set("last_updated", System.currentTimeMillis());
            
            config.save(statsFile);
            
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "統計ファイルの保存エラー: " + stats.getPlayerUUID(), e);
        }
    }
    
    /**
     * Save all cached statistics
     */
    public void saveAll() {
        for (GameStatistics stats : cache.values()) {
            saveStats(stats);
        }
        plugin.getLogger().info(cache.size() + " 個の統計を保存しました");
    }
    
    /**
     * Clear the cache
     */
    public void clearCache() {
        cache.clear();
    }
}
