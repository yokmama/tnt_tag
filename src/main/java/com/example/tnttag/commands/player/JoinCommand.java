package com.example.tnttag.commands.player;

import com.example.tnttag.TNTTagPlugin;
import com.example.tnttag.arena.Arena;
import com.example.tnttag.commands.SubCommand;
import com.example.tnttag.config.MessageManager;
import com.example.tnttag.game.GameInstance;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * /tnttag join <arena>
 */
public class JoinCommand implements SubCommand {
    
    private final TNTTagPlugin plugin;
    
    public JoinCommand(TNTTagPlugin plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cこのコマンドはプレイヤーのみ実行できます");
            return true;
        }
        
        Player player = (Player) sender;
        
        if (args.length == 0) {
            player.sendMessage("§c使用方法: /tnttag join <arena>");
            return true;
        }
        
        String arenaName = args[0];
        Arena arena = plugin.getArenaManager().getArena(arenaName);
        
        if (arena == null) {
            plugin.getMessageManager().sendMessage(player, "commands.join.arena_not_found",
                MessageManager.createPlaceholders("arena", arenaName));
            return true;
        }
        
        // Check if already in a game
        if (plugin.getGameManager().isInGame(player)) {
            plugin.getMessageManager().sendMessage(player, "commands.join.already_in_game");
            return true;
        }
        
        // Get or create game
        GameInstance game = plugin.getGameManager().getGame(arena);
        if (game == null) {
            game = plugin.getGameManager().createGame(arena);
        }
        
        // Auto-join is now enabled - players are automatically in the game
        player.sendMessage("§eプレイヤーはサーバー接続時に自動的にゲームに参加しています。");
        player.sendMessage("§7このコマンドは不要です。");
        
        return true;
    }
    
    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length == 1) {
            List<String> arenaNames = new ArrayList<>();
            for (Arena arena : plugin.getArenaManager().getAllArenas()) {
                arenaNames.add(arena.getName());
            }
            return arenaNames;
        }
        return SubCommand.super.tabComplete(sender, args);
    }
}
