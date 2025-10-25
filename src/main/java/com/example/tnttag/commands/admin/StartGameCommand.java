package com.example.tnttag.commands.admin;

import com.example.tnttag.TNTTagPlugin;
import com.example.tnttag.arena.Arena;
import com.example.tnttag.commands.SubCommand;
import com.example.tnttag.config.MessageManager;
import com.example.tnttag.game.GameInstance;
import org.bukkit.command.CommandSender;

public class StartGameCommand implements SubCommand {
    
    private final TNTTagPlugin plugin;
    
    public StartGameCommand(TNTTagPlugin plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (!sender.isOp()) {
            plugin.getMessageManager().sendMessage((org.bukkit.entity.Player)sender, "errors.no_permission");
            return true;
        }
        
        if (args.length == 0) {
            sender.sendMessage("§c使用方法: /tnttag start <arena>");
            return true;
        }
        
        String arenaName = args[0];
        Arena arena = plugin.getArenaManager().getArena(arenaName);
        
        if (arena == null) {
            sender.sendMessage(plugin.getMessageManager().getMessage("commands.start.not_found",
                MessageManager.createPlaceholders("name", arenaName)));
            return true;
        }
        
        GameInstance game = plugin.getGameManager().getGame(arena);
        
        if (game == null) {
            game = plugin.getGameManager().createGame(arena);
        }
        
        if (game.getPlayers().isEmpty()) {
            sender.sendMessage(plugin.getMessageManager().getMessage("commands.start.no_players"));
            return true;
        }
        
        plugin.getGameManager().startGame(game);
        sender.sendMessage(plugin.getMessageManager().getMessage("commands.start.success"));
        
        return true;
    }
}
