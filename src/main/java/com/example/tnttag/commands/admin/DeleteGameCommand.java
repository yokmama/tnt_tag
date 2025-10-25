package com.example.tnttag.commands.admin;

import com.example.tnttag.TNTTagPlugin;
import com.example.tnttag.commands.SubCommand;
import com.example.tnttag.config.MessageManager;
import com.example.tnttag.game.GameInstance;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class DeleteGameCommand implements SubCommand {
    
    private final TNTTagPlugin plugin;
    
    public DeleteGameCommand(TNTTagPlugin plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (!sender.isOp()) {
            plugin.getMessageManager().sendMessage((org.bukkit.entity.Player)sender, "errors.no_permission");
            return true;
        }
        
        if (args.length == 0) {
            sender.sendMessage("§c使用方法: /tnttag delete <arena>");
            return true;
        }
        
        String arenaName = args[0];
        
        // Check if arena exists
        if (plugin.getArenaManager().getArena(arenaName) == null) {
            sender.sendMessage(plugin.getMessageManager().getMessage("commands.delete.not_found",
                MessageManager.createPlaceholders("name", arenaName)));
            return true;
        }
        
        // Check if game is active
        GameInstance game = plugin.getGameManager().getGame(arenaName);
        if (game != null) {
            sender.sendMessage(plugin.getMessageManager().getMessage("commands.delete.game_active"));
            return true;
        }
        
        // Delete arena
        if (plugin.getArenaManager().deleteArena(arenaName)) {
            sender.sendMessage(plugin.getMessageManager().getMessage("commands.delete.success",
                MessageManager.createPlaceholders("name", arenaName)));
        } else {
            sender.sendMessage(plugin.getMessageManager().getMessage("errors.internal_error"));
        }
        
        return true;
    }
    
    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length == 1) {
            List<String> arenaNames = new ArrayList<>();
            plugin.getArenaManager().getAllArenas().forEach(arena -> arenaNames.add(arena.getName()));
            return arenaNames;
        }
        return SubCommand.super.tabComplete(sender, args);
    }
}
