package com.example.tnttag.commands.player;

import com.example.tnttag.TNTTagPlugin;
import com.example.tnttag.commands.SubCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class LeaveCommand implements SubCommand {
    
    private final TNTTagPlugin plugin;
    
    public LeaveCommand(TNTTagPlugin plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cこのコマンドはプレイヤーのみ実行できます");
            return true;
        }
        
        Player player = (Player) sender;
        
        if (!plugin.getGameManager().isInGame(player)) {
            plugin.getMessageManager().sendMessage(player, "commands.leave.not_in_game");
            return true;
        }
        
        plugin.getGameManager().leaveGame(player);
        plugin.getMessageManager().sendMessage(player, "commands.leave.success");
        
        return true;
    }
}
