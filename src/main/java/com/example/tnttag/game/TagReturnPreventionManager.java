package com.example.tnttag.game;

import com.example.tnttag.TNTTagPlugin;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages tag return prevention state for TNT TAG game.
 *
 * When a player receives TNT from another player, they cannot immediately
 * tag it back to the same player for a configurable duration.
 */
public class TagReturnPreventionManager {

    private final TNTTagPlugin plugin;

    // Map of player UUID -> (target UUID -> expiration timestamp in millis)
    private final Map<UUID, Map<UUID, Long>> preventionStates = new ConcurrentHashMap<>();

    public TagReturnPreventionManager(TNTTagPlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Check if tag return prevention is enabled in config.
     */
    public boolean isEnabled() {
        return plugin.getConfigManager().isTagReturnPreventionEnabled();
    }

    /**
     * Get the prevention duration in milliseconds.
     */
    private long getDurationMillis() {
        double seconds = plugin.getConfigManager().getTagReturnPreventionDuration();
        return (long) (seconds * 1000);
    }

    /**
     * Add a prevention entry when TNT is transferred from giver to receiver.
     * The receiver cannot tag back to giver for the configured duration.
     *
     * @param receiverUUID UUID of the player who received TNT
     * @param giverUUID UUID of the player who gave TNT
     */
    public void addPrevention(UUID receiverUUID, UUID giverUUID) {
        if (!isEnabled()) return;

        long durationMillis = getDurationMillis();
        if (durationMillis <= 0) return;

        long expirationTime = System.currentTimeMillis() + durationMillis;
        preventionStates.computeIfAbsent(receiverUUID, k -> new ConcurrentHashMap<>())
                       .put(giverUUID, expirationTime);
    }

    /**
     * Check if a player is prevented from tagging a specific target.
     *
     * @param playerUUID UUID of the player attempting to tag
     * @param targetUUID UUID of the target player
     * @return true if the tag is currently prevented
     */
    public boolean isPreventionActive(UUID playerUUID, UUID targetUUID) {
        if (!isEnabled()) return false;

        Map<UUID, Long> playerPreventions = preventionStates.get(playerUUID);
        if (playerPreventions == null) return false;

        Long expirationTime = playerPreventions.get(targetUUID);
        if (expirationTime == null) return false;

        long currentTime = System.currentTimeMillis();
        if (currentTime >= expirationTime) {
            // Prevention expired, clean up
            playerPreventions.remove(targetUUID);
            if (playerPreventions.isEmpty()) {
                preventionStates.remove(playerUUID);
            }
            return false;
        }

        return true;
    }

    /**
     * Clear all prevention states. Called on round start.
     */
    public void clearAll() {
        preventionStates.clear();
    }

    /**
     * Remove all prevention entries related to a specific player.
     * Called when a player leaves the game.
     *
     * @param playerUUID UUID of the player who left
     */
    public void removePlayer(UUID playerUUID) {
        // Remove as subject
        preventionStates.remove(playerUUID);

        // Remove as target from all other players
        for (Map<UUID, Long> playerMap : preventionStates.values()) {
            playerMap.remove(playerUUID);
        }

        // Clean up empty maps
        preventionStates.entrySet().removeIf(entry -> entry.getValue().isEmpty());
    }

    /**
     * Show feedback to the player when their tag attempt is prevented.
     * Displays an action bar message and plays a sound.
     *
     * @param player The player whose tag was prevented
     */
    public void showPreventionFeedback(Player player) {
        // Display action bar message
        String message = plugin.getMessageManager().getMessage("tag.return_prevented");
        var component = LegacyComponentSerializer.legacyAmpersand().deserialize(message);
        player.sendActionBar(component);

        // Play sound effect
        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1.0f, 0.5f);
    }
}
