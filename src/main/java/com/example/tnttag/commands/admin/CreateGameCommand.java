package com.example.tnttag.commands.admin;

import com.example.tnttag.TNTTagPlugin;
import com.example.tnttag.arena.ArenaSetupSession;
import com.example.tnttag.commands.SubCommand;
import com.example.tnttag.config.MessageManager;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CreateGameCommand implements SubCommand {
    
    private final TNTTagPlugin plugin;
    
    public CreateGameCommand(TNTTagPlugin plugin) {
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
            plugin.getMessageManager().sendMessage(player, "errors.no_permission");
            return true;
        }
        
        if (args.length == 0) {
            plugin.getMessageManager().sendMessage(player, "commands.creategame.usage");
            return true;
        }
        
        String arenaName = args[0];
        
        // Check if name is available
        if (!plugin.getArenaManager().isNameAvailable(arenaName)) {
            plugin.getMessageManager().sendMessage(player, "commands.creategame.name_exists");
            return true;
        }
        
        // Get setup session
        ArenaSetupSession session = plugin.getArenaManager().getSetupSession(player);
        
        if (session == null || !session.isComplete()) {
            plugin.getMessageManager().sendMessage(player, "commands.creategame.positions_not_set");
            return true;
        }
        
        Location pos1 = session.getPos1();
        Location pos2 = session.getPos2();
        
        // Validate same world
        if (!pos1.getWorld().equals(pos2.getWorld())) {
            plugin.getMessageManager().sendMessage(player, "commands.creategame.different_worlds");
            return true;
        }
        
        // Validate minimum distance
        double distance = pos1.distance(pos2);
        if (distance < 10) {
            plugin.getMessageManager().sendMessage(player, "commands.creategame.too_small");
            return true;
        }
        
        // Create arena
        if (plugin.getArenaManager().createArena(arenaName, pos1.getWorld(), pos1, pos2)) {
            plugin.getMessageManager().sendMessage(player, "commands.creategame.success",
                MessageManager.createPlaceholders("name", arenaName));
            
            // Clear session
            plugin.getArenaManager().clearSetupSession(player);
        } else {
            plugin.getMessageManager().sendMessage(player, "errors.internal_error");
        }
        
        return true;
    }
}
