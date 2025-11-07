package com.example.tnttag.hud;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
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
            LegacyComponentSerializer.legacySection().deserialize("§e§lROUND " + roundNumber),
            LegacyComponentSerializer.legacySection().deserialize("§7TNTから逃げろ！"),
            DEFAULT_TIMES
        );
        player.showTitle(title);
    }
    
    /**
     * Send TNT received title
     */
    public void sendTNTReceived(Player player) {
        Title title = Title.title(
            LegacyComponentSerializer.legacySection().deserialize("§c§lTNTを受け取った！"),
            LegacyComponentSerializer.legacySection().deserialize("§e他のプレイヤーにタッチ！"),
            DEFAULT_TIMES
        );
        player.showTitle(title);
    }
    
    /**
     * Send TNT passed title
     */
    public void sendTNTPassed(Player player) {
        Title title = Title.title(
            LegacyComponentSerializer.legacySection().deserialize("§a§lTNTを渡した！"),
            LegacyComponentSerializer.legacySection().deserialize("§7安全だ！"),
            DEFAULT_TIMES
        );
        player.showTitle(title);
    }
    
    /**
     * Send explosion title
     */
    public void sendExplosion(Player player) {
        Title title = Title.title(
            LegacyComponentSerializer.legacySection().deserialize("§4§l💥 BOOM! 💥"),
            LegacyComponentSerializer.legacySection().deserialize("§cあなたは爆発しました"),
            DEFAULT_TIMES
        );
        player.showTitle(title);
    }
    
    /**
     * Send victory title
     */
    public void sendVictory(Player player) {
        Title title = Title.title(
            LegacyComponentSerializer.legacySection().deserialize("§6§l🏆 VICTORY! 🏆"),
            LegacyComponentSerializer.legacySection().deserialize("§e最後の生存者！"),
            DEFAULT_TIMES
        );
        player.showTitle(title);
    }
    
    /**
     * Send game start countdown title
     */
    public void sendCountdown(Player player, int seconds) {
        Title title = Title.title(
            LegacyComponentSerializer.legacySection().deserialize("§e§l" + seconds),
            LegacyComponentSerializer.legacySection().deserialize("§7ゲーム開始まで..."),
            Title.Times.times(
                Duration.ofMillis(0),
                Duration.ofMillis(1000),
                Duration.ofMillis(0)
            )
        );
        player.showTitle(title);
    }

    /**
     * Send countdown title for 3, 2, 1
     */
    public void sendCountdownTitle(Player player, int number) {
        Title title = Title.title(
            LegacyComponentSerializer.legacySection().deserialize("§e§l" + number),
            Component.empty(),
            Title.Times.times(
                Duration.ofMillis(200),  // fadeIn: 10 ticks
                Duration.ofMillis(400),  // stay: 20 ticks
                Duration.ofMillis(200)   // fadeOut: 10 ticks
            )
        );
        player.showTitle(title);
    }
    
    /**
     * Send game start title
     */
    public void sendGameStart(Player player) {
        Title title = Title.title(
            LegacyComponentSerializer.legacySection().deserialize("§a§lゲーム開始！"),
            LegacyComponentSerializer.legacySection().deserialize("§7TNTから逃げろ！"),
            DEFAULT_TIMES
        );
        player.showTitle(title);
    }
}
