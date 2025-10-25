package com.example.tnttag.commands.player;

import com.example.tnttag.TNTTagPlugin;
import com.example.tnttag.arena.Arena;
import com.example.tnttag.commands.SubCommand;
import com.example.tnttag.config.MessageManager;
import com.example.tnttag.game.GameInstance;
import org.bukkit.command.CommandSender;

import java.util.Collection;

public class ListCommand implements SubCommand {
    
    private final TNTTagPlugin plugin;
    
    public ListCommand(TNTTagPlugin plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean execute(CommandSender sender, String[] args) {
        Collection<Arena> arenas = plugin.getArenaManager().getAllArenas();
        
        if (arenas.isEmpty()) {
            sender.sendMessage(plugin.getMessageManager().getMessage("commands.list.no_arenas"));
            return true;
        }
        
        sender.sendMessage(plugin.getMessageManager().getMessage("commands.list.header"));
        
        for (Arena arena : arenas) {
            GameInstance game = plugin.getGameManager().getGame(arena);
            
            String state;
            int players = 0;
            int maxPlayers = plugin.getConfigManager().getMaxPlayers();
            
            if (game == null) {
                state = "利用可能";
            } else {
                state = getStateString(game.getState());
                players = game.getPlayers().size();
            }
            
            String message = plugin.getMessageManager().getMessage("commands.list.arena_entry",
                MessageManager.createPlaceholders(
                    "name", arena.getName(),
                    "state", state,
                    "players", String.valueOf(players),
                    "max", String.valueOf(maxPlayers)
                ));
            sender.sendMessage(message);
        }
        
        sender.sendMessage(plugin.getMessageManager().getMessage("commands.list.footer"));
        
        return true;
    }
    
    private String getStateString(com.example.tnttag.game.GameState state) {
        switch (state) {
            case WAITING: return "待機中";
            case STARTING: return "開始中";
            case IN_GAME: return "進行中";
            case ROUND_ENDING: return "ラウンド終了";
            case ENDING: return "終了中";
            default: return "不明";
        }
    }
}
