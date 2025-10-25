package com.example.tnttag.effects;

import com.example.tnttag.TNTTagPlugin;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

/**
 * Manages sound effects for the game
 */
public class SoundEffects {
    
    private final TNTTagPlugin plugin;
    private final float volume;
    
    public SoundEffects(TNTTagPlugin plugin) {
        this.plugin = plugin;
        this.volume = plugin.getConfigManager().getSoundVolume();
    }
    
    /**
     * Play game start countdown sound
     */
    public void playCountdownSound(Player player) {
        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, volume, 1.0f);
    }
    
    /**
     * Play game start sound
     */
    public void playGameStartSound(Player player) {
        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, volume, 1.0f);
    }
    
    /**
     * Play round start sound (dramatic)
     */
    public void playRoundStartSound(Location location) {
        location.getWorld().playSound(location, Sound.ENTITY_LIGHTNING_BOLT_THUNDER, volume, 0.8f);
        location.getWorld().playSound(location, Sound.ENTITY_ENDER_DRAGON_GROWL, volume, 1.2f);
    }
    
    /**
     * Play TNT tag sound (when TNT is transferred)
     */
    public void playTagSound(Player player) {
        player.playSound(player.getLocation(), Sound.ENTITY_ITEM_PICKUP, volume, 0.8f);
        player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_LAND, volume * 0.5f, 1.5f);
    }
    
    /**
     * Play TNT holder loop sound (導火線)
     */
    public void playTNTHolderSound(Player player) {
        player.playSound(player.getLocation(), Sound.ENTITY_TNT_PRIMED, volume * 0.3f, 1.5f);
    }
    
    /**
     * Play warning sound (5 seconds before explosion)
     */
    public void playWarningSound(Location location) {
        location.getWorld().playSound(location, Sound.BLOCK_ANVIL_LAND, volume, 1.0f);
    }
    
    /**
     * Play explosion sound
     */
    public void playExplosionSound(Location location) {
        location.getWorld().playSound(location, Sound.ENTITY_GENERIC_EXPLODE, volume * 1.5f, 0.8f);
        location.getWorld().playSound(location, Sound.ENTITY_DRAGON_FIREBALL_EXPLODE, volume, 1.0f);
    }
    
    /**
     * Play victory sound
     */
    public void playVictorySound(Player player) {
        player.playSound(player.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, volume, 1.0f);
        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, volume, 1.2f);
    }
    
    /**
     * Play defeat sound
     */
    public void playDefeatSound(Player player) {
        player.playSound(player.getLocation(), Sound.ENTITY_WITHER_SPAWN, volume * 0.5f, 0.8f);
    }
    
    /**
     * Play firework launch sound
     */
    public void playFireworkSound(Location location) {
        location.getWorld().playSound(location, Sound.ENTITY_FIREWORK_ROCKET_LAUNCH, volume, 1.0f);
    }
}
