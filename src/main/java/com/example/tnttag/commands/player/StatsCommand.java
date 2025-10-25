package com.example.tnttag.commands.player;

import com.example.tnttag.TNTTagPlugin;
import com.example.tnttag.commands.SubCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class StatsCommand implements SubCommand {
    
    private final TNTTagPlugin plugin;
    
    public StatsCommand(TNTTagPlugin plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean execute(CommandSender sender, String[] args) {
        // TODO: Implement in Phase 7 (Statistics)
        sender.sendMessage("§e統計機能は実装予定です (Phase 7)");
        return true;
    }
}
