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

        Arena arena;

        if (args.length == 0) {
            // No arena specified - use random arena or single game instance
            GameInstance existingGame = plugin.getGameManager().getSingleGameInstance();

            if (existingGame != null) {
                // Use existing game instance
                arena = existingGame.getArena();
                sender.sendMessage("§a既存のゲームインスタンスを開始します (マップ: " + arena.getName() + ")");
            } else {
                // Select random arena
                arena = plugin.getArenaManager().getRandomArena();

                if (arena == null) {
                    sender.sendMessage("§cアリーナが設定されていません。まず /tnttag creategame でアリーナを作成してください。");
                    return true;
                }

                sender.sendMessage("§aランダムに '" + arena.getName() + "' が選択されました");
            }
        } else {
            // Arena name specified
            String arenaName = args[0];
            arena = plugin.getArenaManager().getArena(arenaName);

            if (arena == null) {
                sender.sendMessage(plugin.getMessageManager().getMessage("commands.start.not_found",
                    MessageManager.createPlaceholders("name", arenaName)));
                return true;
            }

            sender.sendMessage("§aマップ '" + arena.getName() + "' でゲームを開始します");
        }

        // Get or create single game instance
        GameInstance game = plugin.getGameManager().getSingleGameInstance();

        if (game == null) {
            game = plugin.getGameManager().createSingleGameInstance(arena);
        }

        // Check minimum players
        int playerCount = game.getPlayers().size();
        int minPlayers = plugin.getConfigManager().getMinPlayers();

        if (playerCount < minPlayers) {
            sender.sendMessage("§c最小人数に達していません（現在: " + playerCount + "人、必要: " + minPlayers + "人）");
            return true;
        }

        // Check if game is already running
        if (game.getState() != com.example.tnttag.game.GameState.WAITING) {
            sender.sendMessage("§cゲームは既に進行中です");
            return true;
        }

        plugin.getGameManager().startGame(game);
        sender.sendMessage(plugin.getMessageManager().getMessage("commands.start.success"));

        return true;
    }
}
