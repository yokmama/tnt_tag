package com.example.tnttag.hud;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.bukkit.entity.Player;

import java.time.Duration;

/**
 * Manages title message display
 */
public class TitleManager {
    
    private static final Title.Times DEFAULT_TIMES = Title.Times.times(
        Duration.ofMillis(500),  // fadeIn: 10 ticks
        Duration.ofMillis(3000), // stay: 60 ticks (3 seconds)
        Duration.ofMillis(500)   // fadeOut: 10 ticks
    );
    
    /**
     * Send round start title
     */
    public void sendRoundStart(Player player, int roundNumber) {
        Title title = Title.title(
            Component.text("§e§lROUND " + roundNumber),
            Component.text("§7TNTから逃げろ！"),
            DEFAULT_TIMES
        );
        player.showTitle(title);
    }
    
    /**
     * Send TNT received title
     */
    public void sendTNTReceived(Player player) {
        Title title = Title.title(
            Component.text("§c§lTNTを受け取った！"),
            Component.text("§e他のプレイヤーにタッチ！"),
            DEFAULT_TIMES
        );
        player.showTitle(title);
    }
    
    /**
     * Send TNT passed title
     */
    public void sendTNTPassed(Player player) {
        Title title = Title.title(
            Component.text("§a§lTNTを渡した！"),
            Component.text("§7安全だ！"),
            DEFAULT_TIMES
        );
        player.showTitle(title);
    }
    
    /**
     * Send explosion title
     */
    public void sendExplosion(Player player) {
        Title title = Title.title(
            Component.text("§4§l💥 BOOM! 💥"),
            Component.text("§cあなたは爆発しました"),
            DEFAULT_TIMES
        );
        player.showTitle(title);
    }
    
    /**
     * Send victory title
     */
    public void sendVictory(Player player) {
        Title title = Title.title(
            Component.text("§6§l🏆 VICTORY! 🏆"),
            Component.text("§e最後の生存者！"),
            DEFAULT_TIMES
        );
        player.showTitle(title);
    }
    
    /**
     * Send game start countdown title
     */
    public void sendCountdown(Player player, int seconds) {
        Title title = Title.title(
            Component.text("§e§l" + seconds),
            Component.text("§7ゲーム開始まで..."),
            Title.Times.times(
                Duration.ofMillis(0),
                Duration.ofMillis(1000),
                Duration.ofMillis(0)
            )
        );
        player.showTitle(title);
    }
    
    /**
     * Send game start title
     */
    public void sendGameStart(Player player) {
        Title title = Title.title(
            Component.text("§a§lゲーム開始！"),
            Component.text("§7TNTから逃げろ！"),
            DEFAULT_TIMES
        );
        player.showTitle(title);
    }
}
