package com.example.tnttag.arena;

import com.example.tnttag.TNTTagPlugin;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;

/**
 * Manages all game arenas
 */
public class ArenaManager {
    
    private final TNTTagPlugin plugin;
    private final Map<String, Arena> arenas;
    private final Map<UUID, ArenaSetupSession> setupSessions;
    private File arenasFile;
    private FileConfiguration arenasConfig;
    
    public ArenaManager(TNTTagPlugin plugin) {
        this.plugin = plugin;
        this.arenas = new HashMap<>();
        this.setupSessions = new HashMap<>();
        loadArenas();
    }
    
    /**
     * Load arenas from arenas.yml
     */
    public void loadArenas() {
        arenasFile = new File(plugin.getDataFolder(), "arenas.yml");
        
        if (!arenasFile.exists()) {
            plugin.saveResource("arenas.yml", false);
        }
        
        arenasConfig = YamlConfiguration.loadConfiguration(arenasFile);
        arenas.clear();
        
        ConfigurationSection arenasSection = arenasConfig.getConfigurationSection("arenas");
        if (arenasSection == null) {
            plugin.getLogger().info("アリーナが設定されていません");
            return;
        }
        
        for (String arenaName : arenasSection.getKeys(false)) {
            try {
                String path = "arenas." + arenaName;
                
                String worldName = arenasConfig.getString(path + ".world");
                World world = plugin.getServer().getWorld(worldName);
                
                if (world == null) {
                    plugin.getLogger().warning("ワールドが見つかりません: " + worldName + " (アリーナ: " + arenaName + ")");
                    continue;
                }
                
                double pos1X = arenasConfig.getDouble(path + ".pos1.x");
                double pos1Y = arenasConfig.getDouble(path + ".pos1.y");
                double pos1Z = arenasConfig.getDouble(path + ".pos1.z");
                Location pos1 = new Location(world, pos1X, pos1Y, pos1Z);
                
                double pos2X = arenasConfig.getDouble(path + ".pos2.x");
                double pos2Y = arenasConfig.getDouble(path + ".pos2.y");
                double pos2Z = arenasConfig.getDouble(path + ".pos2.z");
                Location pos2 = new Location(world, pos2X, pos2Y, pos2Z);
                
                double centerX = arenasConfig.getDouble(path + ".center_spawn.x");
                double centerY = arenasConfig.getDouble(path + ".center_spawn.y");
                double centerZ = arenasConfig.getDouble(path + ".center_spawn.z");
                Location centerSpawn = new Location(world, centerX, centerY, centerZ);
                
                Arena arena = new Arena(arenaName, world, pos1, pos2, centerSpawn);
                arenas.put(arenaName, arena);
                
                plugin.getLogger().info("アリーナをロード: " + arenaName);
            } catch (Exception e) {
                plugin.getLogger().log(Level.SEVERE, "アリーナのロードエラー: " + arenaName, e);
            }
        }
        
        plugin.getLogger().info(arenas.size() + " 個のアリーナをロードしました");
    }
    
    /**
     * Save all arenas to arenas.yml
     */
    public void saveArenas() {
        try {
            arenasConfig.set("arenas", null); // Clear existing arenas
            
            for (Arena arena : arenas.values()) {
                String path = "arenas." + arena.getName();
                
                arenasConfig.set(path + ".world", arena.getWorld().getName());
                
                Location pos1 = arena.getPos1();
                arenasConfig.set(path + ".pos1.x", pos1.getX());
                arenasConfig.set(path + ".pos1.y", pos1.getY());
                arenasConfig.set(path + ".pos1.z", pos1.getZ());
                
                Location pos2 = arena.getPos2();
                arenasConfig.set(path + ".pos2.x", pos2.getX());
                arenasConfig.set(path + ".pos2.y", pos2.getY());
                arenasConfig.set(path + ".pos2.z", pos2.getZ());
                
                Location center = arena.getCenterSpawn();
                arenasConfig.set(path + ".center_spawn.x", center.getX());
                arenasConfig.set(path + ".center_spawn.y", center.getY());
                arenasConfig.set(path + ".center_spawn.z", center.getZ());
            }
            
            arenasConfig.save(arenasFile);
            plugin.getLogger().info("アリーナを保存しました");
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "アリーナの保存エラー", e);
        }
    }
    
    /**
     * Get an arena by name
     */
    public Arena getArena(String name) {
        return arenas.get(name);
    }

    /**
     * Select a random arena from all registered arenas
     * @return A randomly selected arena, or null if no arenas are registered
     */
    public Arena getRandomArena() {
        Collection<Arena> allArenas = arenas.values();

        if (allArenas.isEmpty()) {
            plugin.getLogger().severe("ランダム選択用のアリーナがありません");
            return null;
        }

        if (allArenas.size() == 1) {
            return allArenas.iterator().next();
        }

        List<Arena> arenaList = new ArrayList<>(allArenas);
        Random random = new Random();
        int index = random.nextInt(arenaList.size());

        Arena selected = arenaList.get(index);
        plugin.getLogger().info("ランダムにアリーナを選択: " + selected.getName());
        return selected;
    }

    /**
     * Get all arenas
     */
    public Collection<Arena> getAllArenas() {
        return new ArrayList<>(arenas.values());
    }
    
    /**
     * Create a new arena
     */
    public boolean createArena(String name, World world, Location pos1, Location pos2) {
        if (arenas.containsKey(name)) {
            return false;
        }
        
        // Calculate center spawn (middle of the arena)
        double centerX = (pos1.getX() + pos2.getX()) / 2;
        double centerY = (pos1.getY() + pos2.getY()) / 2;
        double centerZ = (pos1.getZ() + pos2.getZ()) / 2;
        Location centerSpawn = new Location(world, centerX, centerY, centerZ);
        
        Arena arena = new Arena(name, world, pos1, pos2, centerSpawn);
        arenas.put(name, arena);
        saveArenas();
        
        return true;
    }
    
    /**
     * Delete an arena
     */
    public boolean deleteArena(String name) {
        if (!arenas.containsKey(name)) {
            return false;
        }
        
        arenas.remove(name);
        saveArenas();
        
        return true;
    }
    
    /**
     * Start a setup session for a player
     */
    public ArenaSetupSession startSetupSession(Player player) {
        ArenaSetupSession session = new ArenaSetupSession(player);
        setupSessions.put(player.getUniqueId(), session);
        return session;
    }
    
    /**
     * Get a player's setup session
     */
    public ArenaSetupSession getSetupSession(Player player) {
        return setupSessions.get(player.getUniqueId());
    }
    
    /**
     * Get or create a setup session for a player
     */
    public ArenaSetupSession getOrCreateSetupSession(Player player) {
        ArenaSetupSession session = getSetupSession(player);
        if (session == null || session.isExpired()) {
            session = startSetupSession(player);
        }
        return session;
    }
    
    /**
     * Clear a player's setup session
     */
    public void clearSetupSession(Player player) {
        setupSessions.remove(player.getUniqueId());
    }
    
    /**
     * Check if an arena name is available
     */
    public boolean isNameAvailable(String name) {
        return !arenas.containsKey(name);
    }
}
