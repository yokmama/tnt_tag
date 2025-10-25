package com.example.tnttag.commands.admin;

import com.example.tnttag.TNTTagPlugin;
import com.example.tnttag.arena.ArenaSetupSession;
import com.example.tnttag.commands.SubCommand;
import com.example.tnttag.config.MessageManager;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetPos2Command implements SubCommand {
    
    private final TNTTagPlugin plugin;
    
    public SetPos2Command(TNTTagPlugin plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cこのコマンドはプレイヤーのみ実行できます");
            return true;
        }
        
        Player player = (Player) sender;
        
        if (!player.isOp()) {
            plugin.getMessageManager().sendMessage(player, "commands.setpos2.no_permission");
            return true;
        }
        
        ArenaSetupSession session = plugin.getArenaManager().getSetupSession(player);
        
        if (session == null || session.getPos1() == null) {
            plugin.getMessageManager().sendMessage(player, "commands.setpos2.pos1_not_set");
            return true;
        }
        
        Location loc = player.getLocation();
        session.setPos2(loc);
        
        plugin.getMessageManager().sendMessage(player, "commands.setpos2.success",
            MessageManager.createPlaceholders(
                "x", String.format("%.1f", loc.getX()),
                "y", String.format("%.1f", loc.getY()),
                "z", String.format("%.1f", loc.getZ())
            ));
        
        return true;
    }
}
