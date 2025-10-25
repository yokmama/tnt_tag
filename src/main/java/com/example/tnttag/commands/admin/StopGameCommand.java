package com.example.tnttag.commands.admin;

import com.example.tnttag.TNTTagPlugin;
import com.example.tnttag.commands.SubCommand;
import com.example.tnttag.config.MessageManager;
import com.example.tnttag.game.GameInstance;
import org.bukkit.command.CommandSender;

public class StopGameCommand implements SubCommand {
    
    private final TNTTagPlugin plugin;
    
    public StopGameCommand(TNTTagPlugin plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (!sender.isOp()) {
            plugin.getMessageManager().sendMessage((org.bukkit.entity.Player)sender, "errors.no_permission");
            return true;
        }
        
        if (args.length == 0) {
            sender.sendMessage("§c使用方法: /tnttag stop <arena>");
            return true;
        }
        
        String arenaName = args[0];
        GameInstance game = plugin.getGameManager().getGame(arenaName);
        
        if (game == null) {
            sender.sendMessage(plugin.getMessageManager().getMessage("commands.stop.not_active"));
            return true;
        }
        
        plugin.getGameManager().stopGame(game);
        sender.sendMessage(plugin.getMessageManager().getMessage("commands.stop.success"));
        
        return true;
    }
}
