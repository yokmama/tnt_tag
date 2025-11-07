package com.example.tnttag.effects;

import com.example.tnttag.TNTTagPlugin;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Random;

/**
 * Manages firework effects for victory celebration
 */
public class FireworkEffects {
    
    private final TNTTagPlugin plugin;
    private final Random random;
    
    public FireworkEffects(TNTTagPlugin plugin) {
        this.plugin = plugin;
        this.random = new Random();
    }
    
    /**
     * Launch victory fireworks (10 fireworks at 0.5-second intervals)
     */
    public void launchVictoryFireworks(Location location) {
        new BukkitRunnable() {
            int count = 0;
            final Location loc = location.clone();
            
            @Override
            public void run() {
                if (count >= 10) {
                    this.cancel();
                    return;
                }
                
                // Launch a random firework
                spawnRandomFirework(loc);
                count++;
            }
        }.runTaskTimer(plugin, 0L, 10L); // Every 10 ticks = 0.5 seconds
    }
    
    /**
     * Spawn a random colored firework
     */
    public void spawnRandomFirework(Location location) {
        Firework firework = (Firework) location.getWorld().spawnEntity(location, EntityType.FIREWORK_ROCKET);
        FireworkMeta meta = firework.getFireworkMeta();
        
        // Random colors
        Color[] colors = {
            Color.RED, Color.ORANGE, Color.YELLOW, Color.GREEN, 
            Color.BLUE, Color.PURPLE, Color.WHITE, Color.AQUA
        };
        
        Color color1 = colors[random.nextInt(colors.length)];
        Color color2 = colors[random.nextInt(colors.length)];
        
        // Random effect type
        FireworkEffect.Type[] types = {
            FireworkEffect.Type.BALL,
            FireworkEffect.Type.BALL_LARGE,
            FireworkEffect.Type.BURST,
            FireworkEffect.Type.STAR
        };
        
        FireworkEffect.Type type = types[random.nextInt(types.length)];
        
        // Build effect
        FireworkEffect effect = FireworkEffect.builder()
            .withColor(color1)
            .withFade(color2)
            .with(type)
            .trail(random.nextBoolean())
            .flicker(random.nextBoolean())
            .build();
        
        meta.addEffect(effect);
        meta.setPower(random.nextInt(2) + 1); // Power 1-2
        
        firework.setFireworkMeta(meta);
    }
    
    /**
     * Launch a single celebration firework
     */
    public void launchSingleFirework(Location location, Color color) {
        Firework firework = (Firework) location.getWorld().spawnEntity(location, EntityType.FIREWORK_ROCKET);
        FireworkMeta meta = firework.getFireworkMeta();
        
        FireworkEffect effect = FireworkEffect.builder()
            .withColor(color)
            .withFade(Color.WHITE)
            .with(FireworkEffect.Type.BALL_LARGE)
            .trail(true)
            .flicker(true)
            .build();
        
        meta.addEffect(effect);
        meta.setPower(1);
        
        firework.setFireworkMeta(meta);
    }
}
