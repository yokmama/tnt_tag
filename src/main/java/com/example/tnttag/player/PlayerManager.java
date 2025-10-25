package com.example.tnttag.player;

import com.example.tnttag.TNTTagPlugin;
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
    
    public PlayerManager(TNTTagPlugin plugin) {
        this.plugin = plugin;
        this.playerData = new HashMap<>();
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
     */
    public void setTNTHolder(Player player, boolean holder) {
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
            
            // Add glowing effect (red)
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
            
            // Remove glowing if not in glowing round
            // (This will be handled by round logic)
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
        player.removePotionEffect(PotionEffectType.SPEED);
        player.removePotionEffect(PotionEffectType.GLOWING);
        player.removePotionEffect(PotionEffectType.REGENERATION);
        player.removePotionEffect(PotionEffectType.BLINDNESS);
        
        // Remove TNT head
        if (player.getInventory().getHelmet() != null && 
            player.getInventory().getHelmet().getType() == Material.TNT) {
            player.getInventory().setHelmet(null);
        }
    }
    
    /**
     * Save all player statistics
     */
    public void saveAllStats() {
        // TODO: Implement statistics saving in Phase 7
        plugin.getLogger().info("統計の保存（未実装）");
    }
}
