package com.example.tnttag.commands.player;

import com.example.tnttag.TNTTagPlugin;
import com.example.tnttag.commands.SubCommand;
import com.example.tnttag.config.MessageManager;
import com.example.tnttag.stats.GameStatistics;
import com.example.tnttag.stats.StatsManager;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class StatsCommand implements SubCommand {

    private final TNTTagPlugin plugin;

    public StatsCommand(TNTTagPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        Player target;

        // Determine target player
        if (args.length == 0) {
            // Show own stats
            if (!(sender instanceof Player)) {
                sender.sendMessage("§cコンソールからはプレイヤー名を指定してください");
                return true;
            }
            target = (Player) sender;
        } else {
            // Show specified player's stats
            target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                // Try to load from file by name
                plugin.getMessageManager().sendMessage((Player) sender, "commands.stats.not_found");
                return true;
            }
        }

        // Get stats
        StatsManager statsManager = plugin.getPlayerManager().getStatsManager();
        GameStatistics stats = statsManager.getStats(target);

        // Display stats
        displayStats(sender, stats);

        return true;
    }

    /**
     * Display statistics to the sender
     */
    private void displayStats(CommandSender sender, GameStatistics stats) {
        MessageManager msg = plugin.getMessageManager();

        sender.sendMessage(msg.getMessage("commands.stats.header",
            MessageManager.createPlaceholders("player", stats.getPlayerName())));

        sender.sendMessage(msg.getMessage("commands.stats.games_played",
            MessageManager.createPlaceholders("count", String.valueOf(stats.getGamesPlayed()))));

        sender.sendMessage(msg.getMessage("commands.stats.wins",
            MessageManager.createPlaceholders("count", String.valueOf(stats.getWins()))));

        sender.sendMessage(msg.getMessage("commands.stats.win_rate",
            MessageManager.createPlaceholders("rate", String.format("%.1f", stats.getWinRate() * 100))));

        sender.sendMessage(msg.getMessage("commands.stats.total_rounds",
            MessageManager.createPlaceholders("count", String.valueOf(stats.getTotalRoundsSurvived()))));

        sender.sendMessage(msg.getMessage("commands.stats.avg_survival",
            MessageManager.createPlaceholders("time", String.format("%.1f", stats.getAverageSurvivalTime()))));

        sender.sendMessage(msg.getMessage("commands.stats.tags_given",
            MessageManager.createPlaceholders("count", String.valueOf(stats.getTntTagsGiven()))));

        sender.sendMessage(msg.getMessage("commands.stats.tags_received",
            MessageManager.createPlaceholders("count", String.valueOf(stats.getTntTagsReceived()))));

        sender.sendMessage(msg.getMessage("commands.stats.footer"));
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length == 1) {
            // Complete player names
            List<String> playerNames = new ArrayList<>();
            for (Player player : Bukkit.getOnlinePlayers()) {
                playerNames.add(player.getName());
            }
            return playerNames;
        }
        return SubCommand.super.tabComplete(sender, args);
    }
}
