package com.example.tnttag.effects;

import com.example.tnttag.TNTTagPlugin;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

/**
 * Manages particle effects with distance-based optimization
 */
public class ParticleEffects {
    
    private final TNTTagPlugin plugin;
    private final double maxRadius;
    
    public ParticleEffects(TNTTagPlugin plugin) {
        this.plugin = plugin;
        this.maxRadius = plugin.getConfigManager().getParticleRadius();
    }
    
    /**
     * Spawn particles with distance-based optimization
     * Only players within maxRadius will see particles, with count reduced by distance
     */
    public void spawnParticleOptimized(Location location, Particle particle, int baseCount) {
        for (Player player : location.getWorld().getPlayers()) {
            double distance = player.getLocation().distance(location);
            
            if (distance > maxRadius) {
                continue; // Skip distant players
            }
            
            // Reduce particle count by distance (linear falloff)
            int count = (int) (baseCount * (1.0 - (distance / maxRadius)));
            count = Math.max(1, count);
            
            player.spawnParticle(particle, location, count);
        }
    }
    
    /**
     * Spawn particles with data (e.g., colored dust)
     */
    public <T> void spawnParticleOptimized(Location location, Particle particle, int baseCount, T data) {
        for (Player player : location.getWorld().getPlayers()) {
            double distance = player.getLocation().distance(location);
            
            if (distance > maxRadius) {
                continue;
            }
            
            int count = (int) (baseCount * (1.0 - (distance / maxRadius)));
            count = Math.max(1, count);
            
            player.spawnParticle(particle, location, count, data);
        }
    }
    
    /**
     * Game start effects (green particles + happy villagers)
     */
    public void playGameStartEffect(Location location) {
        spawnParticleOptimized(location, Particle.HAPPY_VILLAGER, 50);
        spawnParticleOptimized(location, Particle.TOTEM_OF_UNDYING, 30);
        
        // Create a circle of particles
        for (int i = 0; i < 360; i += 20) {
            double radians = Math.toRadians(i);
            double x = location.getX() + Math.cos(radians) * 3;
            double z = location.getZ() + Math.sin(radians) * 3;
            Location particleLoc = new Location(location.getWorld(), x, location.getY() + 1, z);
            spawnParticleOptimized(particleLoc, Particle.HAPPY_VILLAGER, 3);
        }
    }
    
    /**
     * Round start effects (particle pillar)
     */
    public void playRoundStartEffect(Location location) {
        // Vertical pillar of END_ROD particles
        for (int y = 0; y < 10; y++) {
            Location pillarLoc = location.clone().add(0, y * 0.5, 0);
            spawnParticleOptimized(pillarLoc, Particle.END_ROD, 20);
        }
        
        // Explosion effect at top
        Location topLoc = location.clone().add(0, 5, 0);
        spawnParticleOptimized(topLoc, Particle.FLASH, 5);
    }
    
    /**
     * TNT holder trail effect (smoke + flame)
     */
    public void playTNTHolderTrail(Location location) {
        spawnParticleOptimized(location, Particle.SMOKE, 5);
        spawnParticleOptimized(location, Particle.FLAME, 3);
        
        // Red dust for visibility
        Particle.DustOptions redDust = new Particle.DustOptions(Color.RED, 1.0f);
        spawnParticleOptimized(location, Particle.DUST, 3, redDust);
    }
    
    /**
     * Tag success effect
     */
    public void playTagEffect(Location location) {
        spawnParticleOptimized(location, Particle.CRIT, 30);
        spawnParticleOptimized(location, Particle.ENCHANT, 20);
        
        // Yellow flash
        Particle.DustOptions yellowDust = new Particle.DustOptions(Color.YELLOW, 2.0f);
        spawnParticleOptimized(location, Particle.DUST, 15, yellowDust);
    }
    
    /**
     * Explosion effects (large explosion + smoke)
     */
    public void playExplosionEffect(Location location) {
        // Main explosion
        spawnParticleOptimized(location, Particle.EXPLOSION_EMITTER, 5);
        spawnParticleOptimized(location, Particle.EXPLOSION, 20);
        
        // Smoke clouds
        for (int i = 0; i < 8; i++) {
            double angle = Math.toRadians(i * 45);
            double x = location.getX() + Math.cos(angle) * 2;
            double z = location.getZ() + Math.sin(angle) * 2;
            Location smokeLoc = new Location(location.getWorld(), x, location.getY() + 1, z);
            spawnParticleOptimized(smokeLoc, Particle.LARGE_SMOKE, 15);
        }
        
        // Smoke rings (vertical)
        for (int y = 0; y < 3; y++) {
            for (int i = 0; i < 360; i += 30) {
                double radians = Math.toRadians(i);
                double x = location.getX() + Math.cos(radians) * (1 + y * 0.5);
                double z = location.getZ() + Math.sin(radians) * (1 + y * 0.5);
                Location ringLoc = new Location(location.getWorld(), x, location.getY() + y, z);
                spawnParticleOptimized(ringLoc, Particle.SMOKE, 2);
            }
        }
    }
    
    /**
     * Victory effects (golden particles + sparkles)
     */
    public void playVictoryEffect(Location location) {
        spawnParticleOptimized(location, Particle.TOTEM_OF_UNDYING, 100);
        spawnParticleOptimized(location, Particle.HAPPY_VILLAGER, 50);
        spawnParticleOptimized(location, Particle.END_ROD, 30);
        
        // Golden dust
        Particle.DustOptions goldDust = new Particle.DustOptions(Color.YELLOW, 2.0f);
        spawnParticleOptimized(location, Particle.DUST, 50, goldDust);
        
        // Spiral effect
        for (int i = 0; i < 360; i += 15) {
            double radians = Math.toRadians(i);
            double radius = 2.0;
            double x = location.getX() + Math.cos(radians) * radius;
            double z = location.getZ() + Math.sin(radians) * radius;
            double y = location.getY() + (i / 360.0) * 3;
            Location spiralLoc = new Location(location.getWorld(), x, y, z);
            spawnParticleOptimized(spiralLoc, Particle.END_ROD, 3);
        }
    }
    
    /**
     * Defeat effects (smoke clouds)
     */
    public void playDefeatEffect(Location location) {
        spawnParticleOptimized(location, Particle.LARGE_SMOKE, 50);
        spawnParticleOptimized(location, Particle.CLOUD, 30);
        
        // Gray dust
        Particle.DustOptions grayDust = new Particle.DustOptions(Color.GRAY, 1.5f);
        spawnParticleOptimized(location, Particle.DUST, 30, grayDust);
    }
}
