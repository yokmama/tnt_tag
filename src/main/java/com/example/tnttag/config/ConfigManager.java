package com.example.tnttag.config;

import com.example.tnttag.TNTTagPlugin;
import com.example.tnttag.game.RoundConfig;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

/**
 * Manages plugin configuration
 */
public class ConfigManager {
    
    private final TNTTagPlugin plugin;
    private FileConfiguration config;
    private List<RoundConfig> roundConfigs;
    
    public ConfigManager(TNTTagPlugin plugin) {
        this.plugin = plugin;
        plugin.saveDefaultConfig();
        this.config = plugin.getConfig();
        this.roundConfigs = loadRoundConfigs();
        validate();
    }
    
    /**
     * Reload configuration from file
     */
    public void reload() {
        plugin.reloadConfig();
        this.config = plugin.getConfig();
        this.roundConfigs = loadRoundConfigs();
        validate();
        plugin.getLogger().info("設定をリロードしました");
    }

    /**
     * Validate configuration settings
     */
    public void validate() {
        boolean hasErrors = false;

        // Validate player counts
        int minPlayers = getMinPlayers();
        int maxPlayers = getMaxPlayers();

        if (minPlayers <= 0) {
            plugin.getLogger().warning("game.min_players must be > 0, using default: 20");
            hasErrors = true;
        }

        if (maxPlayers <= 0) {
            plugin.getLogger().warning("game.max_players must be > 0, using default: 25");
            hasErrors = true;
        }

        if (minPlayers > maxPlayers) {
            plugin.getLogger().warning("game.min_players (" + minPlayers + ") > game.max_players (" + maxPlayers + "), using defaults");
            hasErrors = true;
        }

        // Validate round count
        if (getTotalRounds() != 6) {
            plugin.getLogger().warning("game.rounds should be 6 (found: " + getTotalRounds() + "), game may not work as intended");
        }

        // Validate tag cooldown
        if (getTagCooldown() < 0) {
            plugin.getLogger().warning("game.tag_cooldown must be >= 0, using default: 0.5");
            hasErrors = true;
        }

        // Validate round configurations
        if (roundConfigs.size() != 6) {
            plugin.getLogger().severe("Expected 6 round configurations, found: " + roundConfigs.size());
            hasErrors = true;
        }

        for (int i = 0; i < roundConfigs.size(); i++) {
            RoundConfig round = roundConfigs.get(i);
            int roundNum = i + 1;

            if (round.getDuration() <= 0) {
                plugin.getLogger().warning("Round " + roundNum + " duration must be > 0 (found: " + round.getDuration() + ")");
                hasErrors = true;
            }

            if (round.getFixedTNTHolders() == -1 && round.getTntHoldersRatio() == -1.0) {
                plugin.getLogger().warning("Round " + roundNum + " must have either tnt_holders or tnt_holders_ratio defined");
                hasErrors = true;
            }

            if (round.getTntHoldersRatio() > 0 && (round.getTntHoldersRatio() < 0.01 || round.getTntHoldersRatio() > 1.0)) {
                plugin.getLogger().warning("Round " + roundNum + " tnt_holders_ratio should be between 0.01 and 1.0 (found: " + round.getTntHoldersRatio() + ")");
            }
        }

        // Validate effects settings
        if (getParticleRadius() < 0) {
            plugin.getLogger().warning("effects.particle_radius must be >= 0, using default: 20.0");
            hasErrors = true;
        }

        if (getSoundVolume() < 0 || getSoundVolume() > 1.0) {
            plugin.getLogger().warning("effects.sound_volume should be between 0.0 and 1.0 (found: " + getSoundVolume() + ")");
        }

        if (hasErrors) {
            plugin.getLogger().warning("設定に問題があります。デフォルト値を使用する項目があります。");
        } else {
            plugin.getLogger().info("設定の検証が完了しました");
        }
    }
    
    /**
     * Load round configurations from config.yml
     */
    private List<RoundConfig> loadRoundConfigs() {
        List<RoundConfig> configs = new ArrayList<>();
        
        for (int round = 1; round <= 6; round++) {
            String path = "round_settings.round_" + round;
            
            try {
                int tntHolders = config.getInt(path + ".tnt_holders", -1);
                double tntHoldersRatio = config.getDouble(path + ".tnt_holders_ratio", -1.0);
                int duration = config.getInt(path + ".duration");
                boolean glowing = config.getBoolean(path + ".glowing", false);
                
                RoundConfig roundConfig = new RoundConfig(
                    round,
                    tntHolders,
                    tntHoldersRatio,
                    duration,
                    glowing
                );
                
                configs.add(roundConfig);
            } catch (Exception e) {
                plugin.getLogger().log(Level.SEVERE, "ラウンド " + round + " の設定読み込みエラー", e);
            }
        }
        
        return configs;
    }
    
    /**
     * Get round configuration for specific round
     */
    public RoundConfig getRoundConfig(int roundNumber) {
        if (roundNumber < 1 || roundNumber > roundConfigs.size()) {
            return null;
        }
        return roundConfigs.get(roundNumber - 1);
    }
    
    /**
     * Get all round configurations
     */
    public List<RoundConfig> getAllRoundConfigs() {
        return new ArrayList<>(roundConfigs);
    }
    
    // Game settings getters
    public int getMinPlayers() {
        return config.getInt("game.min_players", 20);
    }
    
    public int getMaxPlayers() {
        return config.getInt("game.max_players", 25);
    }
    
    public int getTotalRounds() {
        return config.getInt("game.rounds", 6);
    }
    
    public int getCountdown() {
        return config.getInt("game.countdown", 10);
    }
    
    public double getTagCooldown() {
        return config.getDouble("game.tag_cooldown", 0.5);
    }
    
    public int getExplosionCountdown() {
        return config.getInt("game.explosion_countdown", 3);
    }
    
    public boolean isPvpEnabled() {
        return config.getBoolean("game.pvp_enabled", false);
    }
    
    // Arena settings
    public boolean isSpawnTeleport() {
        return config.getBoolean("arena.spawn_teleport", true);
    }
    
    public boolean usePresetMap() {
        return config.getBoolean("arena.use_preset_map", true);
    }
    
    // Effects settings
    public double getParticleRadius() {
        return config.getDouble("effects.particle_radius", 20.0);
    }
    
    public float getSoundVolume() {
        return (float) config.getDouble("effects.sound_volume", 1.0);
    }
    
    // Debug mode
    public boolean isDebug() {
        return config.getBoolean("plugin.debug", false);
    }
    
    /**
     * Get the underlying FileConfiguration
     */
    public FileConfiguration getConfig() {
        return config;
    }
}
