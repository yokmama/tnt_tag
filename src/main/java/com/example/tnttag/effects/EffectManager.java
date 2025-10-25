package com.example.tnttag.effects;

import com.example.tnttag.TNTTagPlugin;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 * Coordinates all visual and audio effects
 */
public class EffectManager {
    
    private final TNTTagPlugin plugin;
    private final SoundEffects soundEffects;
    private final ParticleEffects particleEffects;
    private final FireworkEffects fireworkEffects;
    
    public EffectManager(TNTTagPlugin plugin) {
        this.plugin = plugin;
        this.soundEffects = new SoundEffects(plugin);
        this.particleEffects = new ParticleEffects(plugin);
        this.fireworkEffects = new FireworkEffects(plugin);
    }
    
    /**
     * Get sound effects manager
     */
    public SoundEffects getSoundEffects() {
        return soundEffects;
    }
    
    /**
     * Get particle effects manager
     */
    public ParticleEffects getParticleEffects() {
        return particleEffects;
    }
    
    /**
     * Get firework effects manager
     */
    public FireworkEffects getFireworkEffects() {
        return fireworkEffects;
    }
    
    /**
     * Play complete game start sequence
     */
    public void playGameStartEffects(Location location, Iterable<Player> players) {
        // Visual effects
        particleEffects.playGameStartEffect(location);
        
        // Sound and potion effects for all players
        for (Player player : players) {
            soundEffects.playGameStartSound(player);
            
            // Regeneration effect
            player.addPotionEffect(new PotionEffect(
                PotionEffectType.REGENERATION,
                60, // 3 seconds
                1,
                false,
                true
            ));
        }
    }
    
    /**
     * Play complete round start sequence
     */
    public void playRoundStartEffects(Location location, Iterable<Player> players) {
        // Lightning effect (visual only)
        location.getWorld().strikeLightningEffect(location);
        
        // Particle pillar
        particleEffects.playRoundStartEffect(location);
        
        // Dramatic sound
        soundEffects.playRoundStartSound(location);
    }
    
    /**
     * Play TNT tag effect
     */
    public void playTagEffects(Location location, Player tagger, Player tagged) {
        // Particle effect at tag location
        particleEffects.playTagEffect(location);
        
        // Sound effects
        soundEffects.playTagSound(tagger);
        soundEffects.playTagSound(tagged);
    }
    
    /**
     * Play explosion effects
     */
    public void playExplosionEffects(Location location, Player victim) {
        // Visual explosion
        particleEffects.playExplosionEffect(location);
        
        // Explosion sound
        soundEffects.playExplosionSound(location);
        
        // Blindness effect for victim
        victim.addPotionEffect(new PotionEffect(
            PotionEffectType.BLINDNESS,
            60, // 3 seconds
            0,
            false,
            true
        ));
    }
    
    /**
     * Play victory effects
     */
    public void playVictoryEffects(Location location, Player winner) {
        // Golden particles
        particleEffects.playVictoryEffect(location);
        
        // Victory sound
        soundEffects.playVictorySound(winner);
        
        // Glowing effect
        winner.addPotionEffect(new PotionEffect(
            PotionEffectType.GLOWING,
            200, // 10 seconds
            0,
            false,
            true
        ));
        
        // Launch fireworks
        fireworkEffects.launchVictoryFireworks(location);
    }
    
    /**
     * Play defeat effects
     */
    public void playDefeatEffects(Location location, Player player) {
        // Smoke particles
        particleEffects.playDefeatEffect(location);
        
        // Somber sound
        soundEffects.playDefeatSound(player);
    }
    
    /**
     * Play TNT holder trail (called periodically)
     */
    public void playTNTHolderTrail(Player player) {
        Location location = player.getLocation().add(0, 1, 0);
        particleEffects.playTNTHolderTrail(location);
    }
    
    /**
     * Play warning effects (5 seconds before explosion)
     */
    public void playWarningEffects(Location location) {
        soundEffects.playWarningSound(location);
    }
}
