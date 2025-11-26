package com.example.tnttag.listeners;

import com.example.tnttag.TNTTagPlugin;
import com.example.tnttag.game.TNTItemManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

/**
 * Prevents TNT item manipulation (drop, move, use)
 */
public class TNTItemListener implements Listener {

    private final TNTTagPlugin plugin;

    public TNTItemListener(TNTTagPlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Prevent dropping TNT item
     */
    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        TNTItemManager manager = plugin.getTNTItemManager();
        if (!manager.isEnabled()) {
            return;
        }

        ItemStack droppedItem = event.getItemDrop().getItemStack();
        if (manager.isTNTItem(droppedItem)) {
            event.setCancelled(true);
            if (plugin.getConfigManager().isDebug()) {
                plugin.getLogger().info("TNTアイテムのドロップをキャンセル: " + event.getPlayer().getName());
            }
        }
    }

    /**
     * Prevent moving TNT item in inventory
     */
    @EventHandler(priority = EventPriority.HIGH)
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }

        TNTItemManager manager = plugin.getTNTItemManager();
        if (!manager.isEnabled()) {
            return;
        }

        // Check if clicked item or cursor item is TNT item
        ItemStack currentItem = event.getCurrentItem();
        ItemStack cursorItem = event.getCursor();

        if (manager.isTNTItem(currentItem) || manager.isTNTItem(cursorItem)) {
            event.setCancelled(true);
            if (plugin.getConfigManager().isDebug()) {
                plugin.getLogger().info("TNTアイテムのインベントリ操作をキャンセル: " + event.getWhoClicked().getName());
            }
        }
    }

    /**
     * Prevent using TNT item (right-click/left-click)
     */
    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerInteract(PlayerInteractEvent event) {
        TNTItemManager manager = plugin.getTNTItemManager();
        if (!manager.isEnabled()) {
            return;
        }

        ItemStack item = event.getItem();
        if (manager.isTNTItem(item)) {
            event.setCancelled(true);
            if (plugin.getConfigManager().isDebug()) {
                plugin.getLogger().info("TNTアイテムの使用をキャンセル: " + event.getPlayer().getName());
            }
        }
    }

    /**
     * Prevent placing TNT item as a block
     */
    @EventHandler(priority = EventPriority.HIGH)
    public void onBlockPlace(BlockPlaceEvent event) {
        TNTItemManager manager = plugin.getTNTItemManager();
        if (!manager.isEnabled()) {
            return;
        }

        ItemStack item = event.getItemInHand();
        if (manager.isTNTItem(item)) {
            event.setCancelled(true);
            if (plugin.getConfigManager().isDebug()) {
                plugin.getLogger().info("TNTアイテムの設置をキャンセル: " + event.getPlayer().getName());
            }
        }
    }
}
