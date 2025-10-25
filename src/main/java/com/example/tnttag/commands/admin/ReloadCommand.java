package com.example.tnttag.commands.admin;

import com.example.tnttag.TNTTagPlugin;
import com.example.tnttag.commands.SubCommand;
import org.bukkit.command.CommandSender;

public class ReloadCommand implements SubCommand {
    
    private final TNTTagPlugin plugin;
    
    public ReloadCommand(TNTTagPlugin plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (!sender.isOp()) {
            plugin.getMessageManager().sendMessage((org.bukkit.entity.Player)sender, "errors.no_permission");
            return true;
        }
        
        // Reload configurations
        plugin.getConfigManager().reload();
        plugin.getMessageManager().reload();
        plugin.getArenaManager().loadArenas();
        
        sender.sendMessage(plugin.getMessageManager().getMessage("commands.reload.success"));
        
        return true;
    }
}
