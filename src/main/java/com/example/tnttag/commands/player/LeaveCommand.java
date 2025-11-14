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

        // Auto-join is now enabled - players cannot leave without disconnecting
        player.sendMessage("§eサーバー接続中は常にゲームに参加しています。");
        player.sendMessage("§7ゲームから退出するにはサーバーから切断してください。");

        return true;
    }
}
