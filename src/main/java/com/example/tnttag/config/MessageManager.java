package com.example.tnttag.config;

import com.example.tnttag.TNTTagPlugin;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

/**
 * Manages localized messages
 */
public class MessageManager {
    
    private final TNTTagPlugin plugin;
    private FileConfiguration messages;
    private String prefix;
    
    public MessageManager(TNTTagPlugin plugin) {
        this.plugin = plugin;
        loadMessages();
    }
    
    /**
     * Load messages from messages_ja_JP.yml
     */
    private void loadMessages() {
        File messagesFile = new File(plugin.getDataFolder(), "messages_ja_JP.yml");
        
        if (!messagesFile.exists()) {
            plugin.saveResource("messages_ja_JP.yml", false);
        }
        
        this.messages = YamlConfiguration.loadConfiguration(messagesFile);
        this.prefix = colorize(messages.getString("prefix", "&8[&cTNT TAG&8]&r "));
    }
    
    /**
     * Reload messages from file
     */
    public void reload() {
        loadMessages();
        plugin.getLogger().info("メッセージをリロードしました");
    }
    
    /**
     * Get a message by key with optional placeholders
     */
    public String getMessage(String key, Map<String, String> placeholders) {
        String message = messages.getString(key);
        
        if (message == null) {
            plugin.getLogger().warning("メッセージキーが見つかりません: " + key);
            return key;
        }
        
        // Replace placeholders
        if (placeholders != null) {
            for (Map.Entry<String, String> entry : placeholders.entrySet()) {
                message = message.replace("%" + entry.getKey() + "%", entry.getValue());
            }
        }
        
        return colorize(message);
    }
    
    /**
     * Get a message by key without placeholders
     */
    public String getMessage(String key) {
        return getMessage(key, null);
    }
    
    /**
     * Get a message with prefix
     */
    public String getMessageWithPrefix(String key, Map<String, String> placeholders) {
        return prefix + getMessage(key, placeholders);
    }
    
    /**
     * Get a message with prefix (no placeholders)
     */
    public String getMessageWithPrefix(String key) {
        return prefix + getMessage(key);
    }
    
    /**
     * Send a message to a player
     */
    public void sendMessage(Player player, String key, Map<String, String> placeholders) {
        player.sendMessage(getMessageWithPrefix(key, placeholders));
    }
    
    /**
     * Send a message to a player (no placeholders)
     */
    public void sendMessage(Player player, String key) {
        sendMessage(player, key, null);
    }
    
    /**
     * Create a placeholder map
     */
    public static Map<String, String> createPlaceholders(String... keyValues) {
        Map<String, String> map = new HashMap<>();
        for (int i = 0; i < keyValues.length - 1; i += 2) {
            map.put(keyValues[i], keyValues[i + 1]);
        }
        return map;
    }
    
    /**
     * Colorize a string with color codes
     */
    private String colorize(String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }
    
    /**
     * Get the message prefix
     */
    public String getPrefix() {
        return prefix;
    }
}
