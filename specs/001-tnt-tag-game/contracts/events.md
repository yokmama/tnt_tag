# Event Contracts: TNT TAG Custom Events

**Feature**: TNT TAG - Minecraft Survival Tag Minigame
**Date**: 2025-10-24
**Status**: Phase 1 Design

## Overview

This document defines the custom Bukkit events fired by the TNT TAG plugin. All events extend `org.bukkit.event.Event` and follow Bukkit event conventions. These events allow other plugins to hook into TNT TAG gameplay and enable internal decoupling between plugin components.

## Event Lifecycle

```
Game Flow → Events Fired
─────────────────────────────────────────────────────
1. Game Start       → TNTTagStartEvent
2. Round Start (×6) → TNTTagRoundStartEvent
3. TNT Transfer (n) → TNTTagEvent
4. Round End (×6)   → TNTExplosionEvent
5. Game End         → TNTTagEndEvent
```

---

## Event Definitions

### 1. TNTTagStartEvent

**Description**: Fired when a game begins (transitions from STARTING to IN_GAME state)

**Timing**: After 10-second countdown completes, before round 1 starts

**Purpose**:
- Notify other plugins of game start
- Allow custom effects/rewards on game start
- Enable stat tracking systems

**Java Signature**:
```java
package com.example.tnttag.events;

import com.example.tnttag.game.GameInstance;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class TNTTagStartEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private final GameInstance game;

    public TNTTagStartEvent(GameInstance game) {
        this.game = game;
    }

    public GameInstance getGame() {
        return game;
    }

    public int getPlayerCount() {
        return game.getPlayers().size();
    }

    public String getArenaName() {
        return game.getArena().getName();
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
```

**Cancellable**: No (game already started)

**Properties**:
| Property | Type | Description |
|----------|------|-------------|
| `game` | GameInstance | The game instance that started |
| `playerCount` | int | Number of participants (20-25) |
| `arenaName` | String | Arena name where game is running |

**Usage Example**:
```java
@EventHandler
public void onGameStart(TNTTagStartEvent event) {
    logger.info("TNT TAG game started in arena: " + event.getArenaName());
    logger.info("Players: " + event.getPlayerCount());

    // Custom logic: award participation tokens
    for (PlayerGameData player : event.getGame().getPlayers()) {
        awardToken(player.getPlayer(), "game_start");
    }
}
```

---

### 2. TNTTagRoundStartEvent

**Description**: Fired at the start of each round (1-6)

**Timing**: After players teleport to center spawn, before TNT holders are selected

**Purpose**:
- Track round progression
- Apply round-specific custom effects
- Announce round start in Discord/chat integrations

**Java Signature**:
```java
package com.example.tnttag.events;

import com.example.tnttag.game.GameInstance;
import com.example.tnttag.game.Round;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class TNTTagRoundStartEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private final GameInstance game;
    private final Round round;

    public TNTTagRoundStartEvent(GameInstance game, Round round) {
        this.game = game;
        this.round = round;
    }

    public GameInstance getGame() {
        return game;
    }

    public Round getRound() {
        return round;
    }

    public int getRoundNumber() {
        return round.getNumber();
    }

    public int getAlivePlayers() {
        return round.getAlivePlayers().size();
    }

    public int getTNTHolderCount() {
        return round.getTntHolders().size();
    }

    public int getDuration() {
        return round.getDuration();
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
```

**Cancellable**: No (round progression is deterministic)

**Properties**:
| Property | Type | Description |
|----------|------|-------------|
| `game` | GameInstance | Parent game instance |
| `round` | Round | The round that is starting |
| `roundNumber` | int | Round number (1-6) |
| `alivePlayers` | int | Count of non-eliminated players |
| `tntHolderCount` | int | Number of TNT holders for this round |
| `duration` | int | Round duration in seconds |

**Usage Example**:
```java
@EventHandler
public void onRoundStart(TNTTagRoundStartEvent event) {
    if (event.getRoundNumber() == 6) {
        broadcast("Final round! " + event.getAlivePlayers() + " players remaining!");
    }

    // Custom difficulty scaling
    if (event.getAlivePlayers() < 5) {
        applyBonusEffect(event.getRound().getAlivePlayers());
    }
}
```

---

### 3. TNTTagEvent

**Description**: Fired when TNT transfers from one player to another (successful tag)

**Timing**: After hit detection, validation, and cooldown check pass, before TNT state updates

**Purpose**:
- Track tag statistics
- Award points for successful tags
- Custom sound/particle effects
- Anti-cheat integration (detect suspicious tag patterns)

**Java Signature**:
```java
package com.example.tnttag.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class TNTTagEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private final Player tagger;   // Player passing TNT
    private final Player tagged;   // Player receiving TNT
    private final int roundNumber;
    private final int remainingTime;
    private boolean cancelled = false;

    public TNTTagEvent(Player tagger, Player tagged, int roundNumber, int remainingTime) {
        this.tagger = tagger;
        this.tagged = tagged;
        this.roundNumber = roundNumber;
        this.remainingTime = remainingTime;
    }

    public Player getTagger() {
        return tagger;
    }

    public Player getTagged() {
        return tagged;
    }

    public int getRoundNumber() {
        return roundNumber;
    }

    public int getRemainingTime() {
        return remainingTime;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
```

**Cancellable**: Yes (allows anti-cheat to block suspicious tags)

**Properties**:
| Property | Type | Description |
|----------|------|-------------|
| `tagger` | Player | Player passing TNT (currently holding) |
| `tagged` | Player | Player receiving TNT (being tagged) |
| `roundNumber` | int | Current round (1-6) |
| `remainingTime` | int | Seconds left in round |
| `cancelled` | boolean | Whether transfer is cancelled |

**Cancellation Behavior**:
- If cancelled, TNT remains with `tagger`
- No state changes occur
- No cooldown applied
- No stats updated

**Usage Example**:
```java
@EventHandler
public void onTNTTag(TNTTagEvent event) {
    // Anti-cheat: Detect rapid successive tags (teleport hacks)
    if (isTagTooFast(event.getTagger(), event.getTagged())) {
        event.setCancelled(true);
        alertModerators(event.getTagger(), "Suspicious tag detected");
        return;
    }

    // Award skill points for late-game saves
    if (event.getRemainingTime() < 5) {
        awardPoints(event.getTagger(), 10, "clutch_save");
    }
}
```

---

### 4. TNTExplosionEvent

**Description**: Fired when round countdown reaches zero and TNT holders explode

**Timing**: At round end (remainingTime == 0), before players move to spectator mode

**Purpose**:
- Track elimination statistics
- Apply custom death effects
- Trigger achievements (e.g., "Survived round 6")
- Death messages in chat

**Java Signature**:
```java
package com.example.tnttag.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.List;

public class TNTExplosionEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private final List<Player> victims;      // Players eliminated
    private final int roundNumber;
    private final String arenaName;

    public TNTExplosionEvent(List<Player> victims, int roundNumber, String arenaName) {
        this.victims = List.copyOf(victims); // Immutable copy
        this.roundNumber = roundNumber;
        this.arenaName = arenaName;
    }

    public List<Player> getVictims() {
        return victims;
    }

    public int getVictimCount() {
        return victims.size();
    }

    public int getRoundNumber() {
        return roundNumber;
    }

    public String getArenaName() {
        return arenaName;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
```

**Cancellable**: No (explosion is core game mechanic)

**Properties**:
| Property | Type | Description |
|----------|------|-------------|
| `victims` | List<Player> | Players eliminated (held TNT) |
| `victimCount` | int | Number of eliminated players |
| `roundNumber` | int | Round where elimination occurred (1-6) |
| `arenaName` | String | Arena name |

**Usage Example**:
```java
@EventHandler
public void onTNTExplosion(TNTExplosionEvent event) {
    for (Player victim : event.getVictims()) {
        // Custom death message
        victim.sendMessage("§c💥 You exploded in round " + event.getRoundNumber() + "!");

        // Award consolation prize
        if (event.getRoundNumber() >= 4) {
            giveItem(victim, Material.DIAMOND, 1);
        }
    }

    // Achievement: Last man standing in round 5
    if (event.getRoundNumber() == 5 && event.getVictimCount() == 1) {
        triggerAchievement(event.getVictims().get(0), "sole_survivor_r5");
    }
}
```

---

### 5. TNTTagEndEvent

**Description**: Fired when the game concludes (all 6 rounds complete or no survivors)

**Timing**: After final round ends, before results display and player teleport

**Purpose**:
- Determine winners
- Award prizes/currency
- Update leaderboards
- Log game results

**Java Signature**:
```java
package com.example.tnttag.events;

import com.example.tnttag.game.GameInstance;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.List;

public class TNTTagEndEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private final GameInstance game;
    private final List<Player> winners;      // Null if no survivors
    private final EndReason reason;

    public enum EndReason {
        ALL_ROUNDS_COMPLETE,     // Normal: 6 rounds finished
        NO_SURVIVORS,            // Early: Everyone eliminated
        FORCE_STOPPED            // Admin used /tnttag stop
    }

    public TNTTagEndEvent(GameInstance game, List<Player> winners, EndReason reason) {
        this.game = game;
        this.winners = winners != null ? List.copyOf(winners) : null;
        this.reason = reason;
    }

    public GameInstance getGame() {
        return game;
    }

    public List<Player> getWinners() {
        return winners;
    }

    public int getWinnerCount() {
        return winners != null ? winners.size() : 0;
    }

    public boolean hasWinner() {
        return winners != null && !winners.isEmpty();
    }

    public EndReason getReason() {
        return reason;
    }

    public String getArenaName() {
        return game.getArena().getName();
    }

    public int getTotalPlayers() {
        return game.getPlayers().size();
    }

    public long getGameDuration() {
        return game.getEndTime() - game.getStartTime();
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
```

**Cancellable**: No (game already ended)

**Properties**:
| Property | Type | Description |
|----------|------|-------------|
| `game` | GameInstance | Completed game instance |
| `winners` | List<Player> | Surviving players (null if none) |
| `winnerCount` | int | Number of winners (0 if none) |
| `hasWinner` | boolean | Whether any player won |
| `reason` | EndReason | Why game ended |
| `arenaName` | String | Arena where game ran |
| `totalPlayers` | int | Total participants |
| `gameDuration` | long | Game length in milliseconds |

**EndReason Enum**:
| Value | Description |
|-------|-------------|
| `ALL_ROUNDS_COMPLETE` | Normal game end: 6 rounds finished with survivors |
| `NO_SURVIVORS` | Early end: All players eliminated before round 6 |
| `FORCE_STOPPED` | Admin manually stopped game via command |

**Usage Example**:
```java
@EventHandler
public void onGameEnd(TNTTagEndEvent event) {
    if (event.hasWinner()) {
        for (Player winner : event.getWinners()) {
            // Award grand prize
            giveCurrency(winner, 1000);
            broadcast("§6🏆 " + winner.getName() + " won TNT TAG!");

            // Update leaderboard
            leaderboard.recordWin(winner);
        }
    } else {
        broadcast("§cNo winners! Everyone exploded!");
    }

    // Log to database
    logGameResult(event.getGame(), event.getWinners(), event.getGameDuration());
}
```

---

## Event Priority Guidelines

### Recommended Listener Priorities

```java
// Internal plugin listeners (process core logic first)
@EventHandler(priority = EventPriority.NORMAL)
public void onTNTTag(TNTTagEvent event) { ... }

// Integration plugins (economy, stats) - run after core
@EventHandler(priority = EventPriority.HIGH)
public void onGameEnd(TNTTagEndEvent event) { ... }

// Logging/monitoring - run last
@EventHandler(priority = EventPriority.MONITOR)
public void logGameEnd(TNTTagEndEvent event) { ... }
```

### Priority Levels

| Priority | Use Case | Example |
|----------|----------|---------|
| LOWEST | Early validation/cancellation | Anti-cheat checks |
| LOW | Pre-processing | Custom effects preparation |
| NORMAL | Core plugin logic | TNT TAG internal handlers |
| HIGH | Post-processing | Economy rewards, stats |
| HIGHEST | Final modifications | Override default behavior |
| MONITOR | Read-only logging | Database logging, analytics |

**Important**: Never cancel events at `MONITOR` priority (for logging only).

---

## Event Call Graph

### Game Start Sequence

```
TNTTagStartEvent
     ↓
TNTTagRoundStartEvent (Round 1)
     ↓
TNTTagEvent (×N tags)
     ↓
TNTExplosionEvent (Round 1 end)
     ↓
TNTTagRoundStartEvent (Round 2)
     ↓
... (repeat for rounds 2-6)
     ↓
TNTExplosionEvent (Final round)
     ↓
TNTTagEndEvent
```

### TNT Transfer Sequence

```
EntityDamageByEntityEvent (Bukkit)
     ↓
Validation (range, cooldown, holder status)
     ↓
TNTTagEvent (custom) ──┐ Cancelled?
     ↓                  └─→ Abort, no state change
Apply state changes (holder swap)
     ↓
Update HUD (both players)
     ↓
Effects (sound, particles, title)
```

---

## Integration Examples

### Example 1: Economy Plugin

```java
@EventHandler(priority = EventPriority.HIGH)
public void onGameEnd(TNTTagEndEvent event) {
    if (!event.hasWinner()) return;

    for (Player winner : event.getWinners()) {
        // Award coins based on participation
        int baseReward = 100;
        int participationBonus = event.getTotalPlayers() * 5;
        economy.depositPlayer(winner, baseReward + participationBonus);
    }

    // Consolation prize for top 3
    List<PlayerGameData> topPlayers = event.getGame().getTopPlayers(3);
    for (int i = 0; i < topPlayers.size(); i++) {
        int consolation = 50 - (i * 10);
        economy.depositPlayer(topPlayers.get(i).getPlayer(), consolation);
    }
}
```

### Example 2: Discord Bot Integration

```java
@EventHandler(priority = EventPriority.MONITOR)
public void announceGameStart(TNTTagStartEvent event) {
    String message = String.format(
        "🎮 TNT TAG game started in **%s** with **%d players**!",
        event.getArenaName(),
        event.getPlayerCount()
    );
    discordBot.sendMessage(gameAnnouncementChannel, message);
}

@EventHandler(priority = EventPriority.MONITOR)
public void announceWinner(TNTTagEndEvent event) {
    if (!event.hasWinner()) {
        discordBot.sendMessage(gameAnnouncementChannel, "💥 Everyone exploded!");
        return;
    }

    String winners = event.getWinners().stream()
        .map(Player::getName)
        .collect(Collectors.joining(", "));

    String message = String.format(
        "🏆 Winners: **%s** (Game duration: %d seconds)",
        winners,
        event.getGameDuration() / 1000
    );
    discordBot.sendMessage(gameAnnouncementChannel, message);
}
```

### Example 3: Custom Achievements

```java
@EventHandler
public void trackAchievements(TNTTagEvent event) {
    // Achievement: "Hot Potato" - Pass TNT 5 times in one round
    incrementTagCount(event.getTagger());
    if (getTagCount(event.getTagger()) >= 5) {
        unlockAchievement(event.getTagger(), "hot_potato");
    }

    // Achievement: "Last Second Save" - Tag with <2s remaining
    if (event.getRemainingTime() < 2) {
        unlockAchievement(event.getTagger(), "last_second_save");
    }
}

@EventHandler
public void trackSurvival(TNTExplosionEvent event) {
    // Achievement: "Round 6 Survivor"
    if (event.getRoundNumber() == 6) {
        // Get players who survived
        GameInstance game = getGameForArena(event.getArenaName());
        for (Player survivor : game.getCurrentRound().getAlivePlayers()) {
            unlockAchievement(survivor, "round_6_survivor");
        }
    }
}
```

---

## Summary

**Total Custom Events**: 5
**Cancellable Events**: 1 (TNTTagEvent)
**Event Call Frequency** (per game):
- TNTTagStartEvent: 1
- TNTTagRoundStartEvent: 6 (one per round)
- TNTTagEvent: Variable (N tags, typically 50-200 per game)
- TNTExplosionEvent: 6 (one per round)
- TNTTagEndEvent: 1

**Best Practices**:
1. Use `EventPriority.MONITOR` for read-only logging
2. Only cancel `TNTTagEvent` if absolutely necessary (validation only)
3. Don't modify event data at `MONITOR` priority
4. Keep event handlers lightweight (async work if needed)
5. Always null-check `TNTTagEndEvent.getWinners()` (may be null)

**Next**: Define command interface contracts
