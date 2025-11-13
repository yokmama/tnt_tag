package com.example.tnttag.player;

import com.example.tnttag.TNTTagPlugin;
import com.example.tnttag.stats.StatsManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Manages player game data and effects
 */
public class PlayerManager {
    
    private final TNTTagPlugin plugin;
    private final Map<UUID, PlayerGameData> playerData;
    private final StatsManager statsManager;

    public PlayerManager(TNTTagPlugin plugin) {
        this.plugin = plugin;
        this.playerData = new HashMap<>();
        this.statsManager = new StatsManager(plugin);
    }
    
    /**
     * Get or create player game data
     */
    public PlayerGameData getPlayerData(Player player) {
        return playerData.computeIfAbsent(player.getUniqueId(), uuid -> new PlayerGameData(player));
    }
    
    /**
     * Remove player data
     */
    public void removePlayerData(Player player) {
        playerData.remove(player.getUniqueId());
    }
    
    /**
     * Clear all player data
     */
    public void clearAllPlayerData() {
        playerData.clear();
    }
    
    /**
     * Set a player as TNT holder
     * Default behavior: assumes non-glowing round (removes glowing when holder = false)
     */
    public void setTNTHolder(Player player, boolean holder) {
        setTNTHolder(player, holder, false);
    }

    /**
     * Set a player as TNT holder with glowing round control
     * @param player The player to modify
     * @param holder True if player should be TNT holder
     * @param isGlowingRound True if this is a glowing round (5 or 6), false otherwise
     */
    public void setTNTHolder(Player player, boolean holder, boolean isGlowingRound) {
        PlayerGameData data = getPlayerData(player);
        data.setTNTHolder(holder);

        if (holder) {
            // Add TNT head
            player.getInventory().setHelmet(new ItemStack(Material.TNT));

            // Add Speed II effect
            player.addPotionEffect(new PotionEffect(
                PotionEffectType.SPEED,
                Integer.MAX_VALUE,
                1, // Speed II (level 2)
                false,
                false
            ));

            // Add glowing effect (TNT holders always glow)
            player.addPotionEffect(new PotionEffect(
                PotionEffectType.GLOWING,
                Integer.MAX_VALUE,
                0,
                false,
                false
            ));
        } else {
            // Remove TNT head
            if (player.getInventory().getHelmet() != null &&
                player.getInventory().getHelmet().getType() == Material.TNT) {
                player.getInventory().setHelmet(null);
            }

            // Remove Speed II, add Speed I
            player.removePotionEffect(PotionEffectType.SPEED);
            player.addPotionEffect(new PotionEffect(
                PotionEffectType.SPEED,
                Integer.MAX_VALUE,
                0, // Speed I (level 1)
                false,
                false
            ));

            // IMPORTANT: Only remove glowing if NOT in a glowing round
            // In rounds 5 and 6, all players should keep glowing even without TNT
            if (!isGlowingRound) {
                player.removePotionEffect(PotionEffectType.GLOWING);
            }
        }
    }
    
    /**
     * Apply glowing effect to all players (for rounds 5 and 6)
     */
    public void applyGlowingEffect(Player player) {
        player.addPotionEffect(new PotionEffect(
            PotionEffectType.GLOWING,
            Integer.MAX_VALUE,
            0,
            false,
            false
        ));
    }
    
    /**
     * Remove all game effects from a player
     */
    public void removeAllEffects(Player player) {
        try {
            // Check if player is online
            if (!player.isOnline()) {
                plugin.getLogger().warning("プレイヤーがオフラインのためエフェクトを削除できません: " + player.getName());
                return;
            }

            // Remove all potion effects
            player.removePotionEffect(PotionEffectType.SPEED);
            player.removePotionEffect(PotionEffectType.GLOWING);
            player.removePotionEffect(PotionEffectType.REGENERATION);
            player.removePotionEffect(PotionEffectType.BLINDNESS);

            // Remove TNT head (and any helmet)
            // IMPORTANT: Always clear helmet regardless of type to ensure TNT is removed
            if (player.getInventory().getHelmet() != null) {
                ItemStack helmet = player.getInventory().getHelmet();
                plugin.getLogger().info("ヘルメット削除: " + player.getName() + " (タイプ: " + helmet.getType() + ")");
                player.getInventory().setHelmet(null);
            }

            plugin.getLogger().info("エフェクト削除完了: " + player.getName());
        } catch (Exception e) {
            plugin.getLogger().severe("エフェクト削除エラー: " + player.getName());
            e.printStackTrace();
        }
    }
    
    /**
     * Get the stats manager
     */
    public StatsManager getStatsManager() {
        return statsManager;
    }

    /**
     * Save all player statistics
     */
    public void saveAllStats() {
        statsManager.saveAll();
    }
}
