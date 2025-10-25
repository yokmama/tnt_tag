package com.example.tnttag.commands;

import com.example.tnttag.TNTTagPlugin;
import com.example.tnttag.commands.admin.*;
import com.example.tnttag.commands.player.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.*;

/**
 * Main command executor for /tnttag
 */
public class TNTTagCommandExecutor implements CommandExecutor, TabCompleter {
    
    private final TNTTagPlugin plugin;
    private final Map<String, SubCommand> subCommands;
    
    public TNTTagCommandExecutor(TNTTagPlugin plugin) {
        this.plugin = plugin;
        this.subCommands = new HashMap<>();
        
        // Register player commands
        subCommands.put("join", new JoinCommand(plugin));
        subCommands.put("leave", new LeaveCommand(plugin));
        subCommands.put("list", new ListCommand(plugin));
        subCommands.put("stats", new StatsCommand(plugin));
        
        // Register admin commands
        subCommands.put("setpos1", new SetPos1Command(plugin));
        subCommands.put("setpos2", new SetPos2Command(plugin));
        subCommands.put("creategame", new CreateGameCommand(plugin));
        subCommands.put("delete", new DeleteGameCommand(plugin));
        subCommands.put("start", new StartGameCommand(plugin));
        subCommands.put("stop", new StopGameCommand(plugin));
        subCommands.put("reload", new ReloadCommand(plugin));
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage("§c使用方法: /tnttag <subcommand>");
            sender.sendMessage("§7利用可能なコマンド: join, leave, list, stats");
            if (sender.isOp()) {
                sender.sendMessage("§7管理者コマンド: setpos1, setpos2, creategame, delete, start, stop, reload");
            }
            return true;
        }
        
        String subCommandName = args[0].toLowerCase();
        SubCommand subCommand = subCommands.get(subCommandName);
        
        if (subCommand == null) {
            sender.sendMessage("§c不明なコマンド: " + subCommandName);
            return true;
        }
        
        // Extract sub-command arguments
        String[] subArgs = Arrays.copyOfRange(args, 1, args.length);
        
        return subCommand.execute(sender, subArgs);
    }
    
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            List<String> completions = new ArrayList<>();
            
            // Player commands
            completions.add("join");
            completions.add("leave");
            completions.add("list");
            completions.add("stats");
            
            // Admin commands (OP only)
            if (sender.isOp()) {
                completions.add("setpos1");
                completions.add("setpos2");
                completions.add("creategame");
                completions.add("delete");
                completions.add("start");
                completions.add("stop");
                completions.add("reload");
            }
            
            // Filter by input
            String input = args[0].toLowerCase();
            completions.removeIf(s -> !s.startsWith(input));
            
            return completions;
        }
        
        if (args.length >= 2) {
            String subCommandName = args[0].toLowerCase();
            SubCommand subCommand = subCommands.get(subCommandName);
            
            if (subCommand != null) {
                String[] subArgs = Arrays.copyOfRange(args, 1, args.length);
                return subCommand.tabComplete(sender, subArgs);
            }
        }
        
        return Collections.emptyList();
    }
}
