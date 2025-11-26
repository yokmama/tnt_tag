package com.example.tnttag.game;

import com.example.tnttag.TNTTagPlugin;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Manages TNT items in player inventory for TNT holders
 */
public class TNTItemManager {

    private final TNTTagPlugin plugin;
    private final Map<UUID, ItemStack> savedItems;
    private final NamespacedKey TNT_ITEM_KEY;

    public TNTItemManager(TNTTagPlugin plugin) {
        this.plugin = plugin;
        this.savedItems = new HashMap<>();
        this.TNT_ITEM_KEY = new NamespacedKey(plugin, "tnt_tag_item");
    }

    /**
     * Check if TNT item feature is enabled
     */
    public boolean isEnabled() {
        return plugin.getConfigManager().isTNTItemEnabled();
    }

    /**
     * Get the configured hotbar slot
     */
    private int getSlot() {
        return plugin.getConfigManager().getTNTItemSlot();
    }

    /**
     * Create a custom TNT item with display name and lore
     */
    public ItemStack createTNTItem() {
        ItemStack item = new ItemStack(Material.TNT);
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            // Set display name
            String displayName = plugin.getMessageManager().getMessage("tnt_item.display_name");
            Component nameComponent = LegacyComponentSerializer.legacyAmpersand().deserialize(displayName);
            meta.displayName(nameComponent);

            // Set lore
            List<String> loreStrings = plugin.getMessageManager().getMessageList("tnt_item.lore");
            List<Component> lore = loreStrings.stream()
                .map(s -> LegacyComponentSerializer.legacyAmpersand().deserialize(s))
                .collect(Collectors.toList());
            meta.lore(lore);

            // Set persistent data to identify this as a TNT TAG item
            meta.getPersistentDataContainer().set(TNT_ITEM_KEY, PersistentDataType.BYTE, (byte) 1);

            item.setItemMeta(meta);
        }

        return item;
    }

    /**
     * Check if an item is a TNT TAG item
     */
    public boolean isTNTItem(ItemStack item) {
        if (item == null || item.getType() != Material.TNT) {
            return false;
        }

        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return false;
        }

        return meta.getPersistentDataContainer().has(TNT_ITEM_KEY, PersistentDataType.BYTE);
    }

    /**
     * Give TNT item to a player (save existing item in that slot)
     */
    public void giveTNTItem(Player player) {
        if (!isEnabled()) {
            return;
        }

        int slot = getSlot();

        // Save existing item in the slot
        ItemStack existingItem = player.getInventory().getItem(slot);
        savedItems.put(player.getUniqueId(), existingItem);

        // Give TNT item
        ItemStack tntItem = createTNTItem();
        player.getInventory().setItem(slot, tntItem);

        // Set held item slot to the TNT item
        player.getInventory().setHeldItemSlot(slot);

        if (plugin.getConfigManager().isDebug()) {
            plugin.getLogger().info("TNTアイテムを付与: " + player.getName() + " (スロット " + slot + ")");
        }
    }

    /**
     * Remove TNT item from a player (restore saved item)
     */
    public void removeTNTItem(Player player) {
        if (!isEnabled()) {
            return;
        }

        int slot = getSlot();
        ItemStack currentItem = player.getInventory().getItem(slot);

        // Only remove if it's actually a TNT item
        if (isTNTItem(currentItem)) {
            // Restore saved item (or null to clear the slot)
            ItemStack savedItem = savedItems.remove(player.getUniqueId());
            player.getInventory().setItem(slot, savedItem);

            if (plugin.getConfigManager().isDebug()) {
                plugin.getLogger().info("TNTアイテムを削除: " + player.getName());
            }
        }
    }

    /**
     * Clear all saved items and remove TNT items from all players
     */
    public void clearAll() {
        savedItems.clear();
        if (plugin.getConfigManager().isDebug()) {
            plugin.getLogger().info("TNTアイテム状態をクリア");
        }
    }

    /**
     * Remove a specific player's TNT item and restore their saved item
     */
    public void removePlayer(Player player) {
        removeTNTItem(player);
        savedItems.remove(player.getUniqueId());
    }

    /**
     * Check if a player has a saved item
     */
    public boolean hasSavedItem(UUID playerUUID) {
        return savedItems.containsKey(playerUUID);
    }
}
