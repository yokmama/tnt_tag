# Command Contracts: TNT TAG Command Interface

**Feature**: TNT TAG - Minecraft Survival Tag Minigame
**Date**: 2025-10-24
**Status**: Phase 1 Design

## Overview

This document defines the command interface for the TNT TAG plugin. All commands use the base command `/tnttag` with subcommands for different operations. Commands are categorized into Player Commands (no OP required) and Admin Commands (OP required per FR-076).

## Command Structure

```
/tnttag <subcommand> [args...]
```

**Main Command Registration** (plugin.yml):
```yaml
commands:
  tnttag:
    description: TNT TAG minigame commands
    usage: /tnttag <subcommand>
    aliases: [tnt, tag]
```

---

## Player Commands (No OP Required)

### 1. `/tnttag join [arena]`

**Description**: Join a game in the specified arena (or auto-select if no arena specified)

**Permission**: `tnttag.play` (default: true, per FR-078)

**Usage**:
```
/tnttag join              # Auto-join any available arena
/tnttag join arena_name   # Join specific arena
```

**Arguments**:
| Argument | Type | Required | Description |
|----------|------|----------|-------------|
| `arena` | String | No | Arena name to join (auto-select if omitted) |

**Behavior**:
1. If no arena specified:
   - Find first available arena (no active game or in WAITING state)
   - If none available, send error message
2. If arena specified:
   - Validate arena exists (from arenas.yml)
   - Check if game is joinable (WAITING or STARTING state)
3. Add player to game instance
4. Teleport player to arena lobby/spawn
5. Update scoreboard to show waiting state
6. If 20th player joins, start 10-second countdown (per FR-054)

**Success Response**:
```
§aYou joined TNT TAG in arena: §6{arena_name}
§7Players: §e{current}/{max}
§7Status: §aWaiting for players...
```

**Error Responses**:
| Error | Message |
|-------|---------|
| Already in game | `§cYou are already in a game! Use /tnttag leave first.` |
| Arena not found | `§cArena '{arena}' does not exist. Use /tnttag list to see available arenas.` |
| Arena full | `§cArena '{arena}' is full (25/25 players).` |
| Game in progress | `§cGame in '{arena}' already started. Wait for next game.` |
| No available arenas | `§cNo arenas available. Contact an administrator.` |

---

### 2. `/tnttag leave`

**Description**: Leave current game and return to spawn

**Permission**: `tnttag.play` (default: true)

**Usage**:
```
/tnttag leave
```

**Arguments**: None

**Behavior**:
1. Validate player is in a game
2. If player is TNT holder:
   - Transfer TNT to random nearby player (if any)
   - Or select new random TNT holder if no nearby players
3. Remove player from game instance
4. Update player counts for remaining players' scoreboards
5. Mark player as eliminated in stats (if game was IN_GAME)
6. Teleport player to server spawn
7. Clear HUD (scoreboard, action bar, boss bar)

**Success Response**:
```
§aYou left the TNT TAG game.
```

**Error Responses**:
| Error | Message |
|-------|---------|
| Not in game | `§cYou are not in a game.` |

**Edge Cases** (from spec):
- If player leaves while holding TNT, TNT is reassigned (FR-061 handling)
- Player marked as eliminated in stats

---

### 3. `/tnttag stats [player]`

**Description**: View game statistics for yourself or another player

**Permission**: `tnttag.stats` (default: true)

**Usage**:
```
/tnttag stats             # View your own stats
/tnttag stats PlayerName  # View another player's stats
```

**Arguments**:
| Argument | Type | Required | Description |
|----------|------|----------|-------------|
| `player` | String | No | Player name (defaults to command sender) |

**Behavior**:
1. If no player specified, use command sender
2. Lookup player UUID (from username)
3. Load stats from `plugins/TNTTag/stats/<uuid>.yml`
4. Format and display stats

**Success Response**:
```
§6════ TNT TAG Stats: §e{player} §6════
§7Games Played: §a{games_played}
§7Total Rounds Survived: §a{total_rounds}
§7Wins: §a{wins}
§7TNT Tags Given: §e{tags_given}
§7TNT Tags Received: §e{tags_received}
§7Average Survival Time: §b{avg_time}s
§7Last Updated: §7{timestamp}
```

**Error Responses**:
| Error | Message |
|-------|---------|
| Player not found | `§cPlayer '{player}' not found.` |
| No stats | `§c{player} has not played TNT TAG yet.` |

---

### 4. `/tnttag list`

**Description**: Show all created arenas with their status and player counts (per FR-065)

**Permission**: `tnttag.play` (default: true)

**Usage**:
```
/tnttag list
```

**Arguments**: None

**Behavior**:
1. Load all arenas from arenas.yml
2. For each arena, check for active game
3. Determine status (waiting/in-game/available)
4. Display formatted list

**Success Response**:
```
§6════ TNT TAG Arenas ════
§e{arena_1} §7- §a[Available] §7(0/25 players)
§e{arena_2} §7- §e[Waiting] §7(15/25 players)
§e{arena_3} §7- §c[In Game] §7(22/25 players) - Round 3/6
§e{arena_4} §7- §a[Available] §7(0/25 players)
§6═══════════════════════════
§7Click an arena name to join!
```

**Status Types**:
| Status | Color | Condition |
|--------|-------|-----------|
| Available | §a (Green) | No active game, 0 players |
| Waiting | §e (Yellow) | Game in WAITING/STARTING state, 1-19 players |
| In Game | §c (Red) | Game in IN_GAME state, shows current round |

**Error Responses**:
| Error | Message |
|-------|---------|
| No arenas | `§cNo arenas have been created yet. Contact an administrator.` |

**Interactive Elements** (optional):
- Arena names as clickable text components (runs `/tnttag join {arena}`)
- Hover tooltip shows arena world, size, last game time

---

## Admin Commands (OP Required)

### 5. `/tnttag setpos1`

**Description**: Set the first corner position for arena boundary

**Permission**: OP required (per FR-076)

**Usage**:
```
/tnttag setpos1
```

**Arguments**: None (uses player's current location)

**Behavior**:
1. Validate player has OP permission
2. Get or create `ArenaSetupSession` for player
3. Store player's current location as `pos1`
4. Persist session in memory (cleared on disconnect per edge case)

**Success Response**:
```
§aPosition 1 set at §e{x}, {y}, {z} §ain world §e{world}
§7Now move to the opposite corner and use §f/tnttag setpos2
```

**Error Responses**:
| Error | Message |
|-------|---------|
| No OP permission | `§cこのコマンドを実行する権限がありません (OP required)` |

**Visual Feedback** (optional):
- Spawn temporary particle effect at pos1 location (e.g., VILLAGER_HAPPY)
- Send action bar with coordinates

---

### 6. `/tnttag setpos2`

**Description**: Set the second corner position for arena boundary

**Permission**: OP required

**Usage**:
```
/tnttag setpos2
```

**Arguments**: None (uses player's current location)

**Behavior**:
1. Validate player has OP permission
2. Get existing `ArenaSetupSession` for player
3. Validate pos1 is already set
4. Store player's current location as `pos2`
5. Calculate and display arena dimensions

**Success Response**:
```
§aPosition 2 set at §e{x}, {y}, {z} §ain world §e{world}
§7Arena dimensions: §e{width}x{height}x{depth}
§7Center spawn: §e{center_x}, {center_y}, {center_z}
§7Radius: §e{radius} blocks
§7Ready to create! Use §f/tnttag creategame <name>
```

**Error Responses**:
| Error | Message |
|-------|---------|
| No OP permission | `§cこのコマンドを実行する権限がありません (OP required)` |
| Pos1 not set | `§cYou must set position 1 first with /tnttag setpos1` |

**Visual Feedback** (optional):
- Draw particle outline between pos1 and pos2 (temporary)
- Highlight center spawn point

---

### 7. `/tnttag creategame <arena_name>`

**Description**: Create a new game arena using saved positions (per FR-068)

**Permission**: OP required

**Usage**:
```
/tnttag creategame <name>
```

**Arguments**:
| Argument | Type | Required | Description |
|----------|------|----------|-------------|
| `arena_name` | String | Yes | Unique arena identifier (3-32 chars, alphanumeric) |

**Behavior**:
1. Validate player has OP permission
2. Get `ArenaSetupSession` for player
3. Validate both pos1 and pos2 are set (per FR-070)
4. Validate pos1 and pos2 are in same world (per edge case)
5. Validate distance >= 10 blocks (per edge case)
6. Validate arena name is unique (per edge case)
7. Calculate center spawn and radius (per FR-069)
8. Create Arena object
9. Setup WorldBorder
10. Save to arenas.yml
11. Clear ArenaSetupSession

**Success Response**:
```
§a✓ Arena '§e{arena_name}§a' created successfully!
§7World: §e{world}
§7Center: §e{x}, {y}, {z}
§7Radius: §e{radius} blocks
§7Boundary: §e{min_x}, {min_z} §7to §e{max_x}, {max_z}
§7Players can now join with §f/tnttag join {arena_name}
```

**Error Responses**:
| Error | Message |
|-------|---------|
| No OP permission | `§cこのコマンドを実行する権限がありません (OP required)` |
| Pos1 not set | `§cYou must set position 1 first with /tnttag setpos1` |
| Pos2 not set | `§cYou must set position 2 first with /tnttag setpos2` |
| Different worlds | `§cPositions must be in the same world!` |
| Too close | `§cPositions must be at least 10 blocks apart!` |
| Name already exists | `§cArena '{arena_name}' already exists!` |
| Invalid name | `§cArena name must be 3-32 alphanumeric characters.` |

**Validation Rules** (from FR-070):
- Both pos1 and pos2 must be set
- Same world (from spec edge case)
- Distance >= 10 blocks (from spec edge case)
- Arena name unique (from spec edge case)
- Name: 3-32 chars, alphanumeric only

---

### 8. `/tnttag delete <arena>`

**Description**: Remove an arena (per FR-072)

**Permission**: OP required

**Usage**:
```
/tnttag delete <arena_name>
```

**Arguments**:
| Argument | Type | Required | Description |
|----------|------|----------|-------------|
| `arena_name` | String | Yes | Arena to delete |

**Behavior**:
1. Validate player has OP permission
2. Validate arena exists
3. Check if arena has active game
4. If active game, force-stop it (per `/tnttag stop` logic)
5. Remove arena from memory
6. Delete from arenas.yml
7. Clean up WorldBorder

**Success Response**:
```
§aArena '§e{arena_name}§a' has been deleted.
```

**Error Responses**:
| Error | Message |
|-------|---------|
| No OP permission | `§cこのコマンドを実行する権限がありません (OP required)` |
| Arena not found | `§cArena '{arena_name}' does not exist.` |

**Warning** (if active game):
```
§6⚠ Arena has an active game with {player_count} players.
§6Stopping game and deleting arena...
```

---

### 9. `/tnttag start <arena>`

**Description**: Force-start a game in the specified arena (per FR-073)

**Permission**: OP required

**Usage**:
```
/tnttag start <arena_name>
```

**Arguments**:
| Argument | Type | Required | Description |
|----------|------|----------|-------------|
| `arena_name` | String | Yes | Arena to start game |

**Behavior**:
1. Validate player has OP permission
2. Validate arena exists
3. Check if arena has players waiting
4. Override minimum player requirement (20) - force start with any number
5. Skip countdown, immediately start round 1
6. Fire TNTTagStartEvent

**Success Response**:
```
§aForce-starting game in arena '§e{arena_name}§a' with {player_count} players.
```

**Error Responses**:
| Error | Message |
|-------|---------|
| No OP permission | `§cこのコマンドを実行する権限がありません (OP required)` |
| Arena not found | `§cArena '{arena_name}' does not exist.` |
| No players | `§cNo players in arena '{arena_name}'.` |
| Already started | `§cGame in '{arena_name}' is already running.` |

**Warning** (if < 4 players):
```
§6⚠ Warning: Only {player_count} players. Game may not function correctly with < 4 players.
```

---

### 10. `/tnttag stop <arena>`

**Description**: Force-stop a running game (per FR-074)

**Permission**: OP required

**Usage**:
```
/tnttag stop <arena_name>
```

**Arguments**:
| Argument | Type | Required | Description |
|----------|------|----------|-------------|
| `arena_name` | String | Yes | Arena to stop game |

**Behavior**:
1. Validate player has OP permission
2. Validate arena exists
3. Check if arena has active game
4. Cancel all scheduled tasks (timers, HUD updates, effects)
5. Fire TNTTagEndEvent with reason = FORCE_STOPPED
6. Teleport all players to spawn
7. Clear all HUD elements
8. Reset arena state

**Success Response**:
```
§aGame in arena '§e{arena_name}§a' has been stopped.
§7{player_count} players returned to spawn.
```

**Error Responses**:
| Error | Message |
|-------|---------|
| No OP permission | `§cこのコマンドを実行する権限がありません (OP required)` |
| Arena not found | `§cArena '{arena_name}' does not exist.` |
| No active game | `§cNo game is running in '{arena_name}'.` |

**Cleanup Tasks**:
- Cancel BukkitScheduler tasks
- Remove boss bars
- Clear scoreboards
- Remove armor stands (TNT blocks)
- Reset player speeds to normal
- Clear TNTHolderState objects

---

### 11. `/tnttag reload`

**Description**: Reload configuration files (per FR-075)

**Permission**: OP required

**Usage**:
```
/tnttag reload
```

**Arguments**: None

**Behavior**:
1. Validate player has OP permission
2. Reload config.yml (game settings)
3. Reload arenas.yml (arena definitions)
4. Reload messages_ja_JP.yml (localized messages)
5. Rebuild in-memory arena cache
6. Apply new settings to active games (where safe)

**Success Response**:
```
§aConfiguration reloaded successfully!
§7- config.yml
§7- arenas.yml
§7- messages_ja_JP.yml
§7Active games: {active_count}
```

**Error Responses**:
| Error | Message |
|-------|---------|
| No OP permission | `§cこのコマンドを実行する権限がありません (OP required)` |
| Config error | `§cFailed to reload {file}: {error_message}` |

**Warning** (if active games):
```
§6⚠ {active_count} games are currently running.
§6Some settings will apply after game ends.
```

**Hot-Reload Limitations**:
- Active games keep current round configurations
- New games use updated config
- Arena changes (pos1/pos2) require game restart
- HUD update frequencies can be changed live

---

## Command Aliases

**Short Aliases** (plugin.yml):
```yaml
commands:
  tnttag:
    aliases: [tnt, tag]
```

**Usage**:
- `/tnt join` → `/tnttag join`
- `/tag stats` → `/tnttag stats`

---

## Tab Completion

### Completions by Subcommand

```java
/tnttag <TAB>          → join, leave, stats, list (+ admin if OP)
/tnttag join <TAB>     → <arena names>
/tnttag stats <TAB>    → <online player names>
/tnttag delete <TAB>   → <arena names>
/tnttag start <TAB>    → <arena names>
/tnttag stop <TAB>     → <arena names with active games>
```

### Implementation Example

```java
@Override
public List<String> onTabComplete(CommandSender sender, Command command,
                                   String alias, String[] args) {
    if (args.length == 1) {
        List<String> completions = Arrays.asList("join", "leave", "stats", "list");
        if (sender.isOp()) {
            completions.addAll(Arrays.asList("setpos1", "setpos2", "creategame",
                                             "delete", "start", "stop", "reload"));
        }
        return StringUtil.copyPartialMatches(args[0], completions, new ArrayList<>());
    }

    if (args.length == 2) {
        switch (args[0].toLowerCase()) {
            case "join":
            case "delete":
            case "start":
            case "stop":
                return StringUtil.copyPartialMatches(args[1],
                    arenaManager.getArenaNames(), new ArrayList<>());

            case "stats":
                return null; // Default player names
        }
    }

    return Collections.emptyList();
}
```

---

## Permission Nodes

**Definition** (plugin.yml):
```yaml
permissions:
  tnttag.play:
    description: Allows joining games and viewing stats
    default: true

  tnttag.stats:
    description: Allows viewing statistics
    default: true

  tnttag.admin:
    description: Allows all admin commands
    default: op
    children:
      tnttag.admin.setup: true
      tnttag.admin.control: true

  tnttag.admin.setup:
    description: Arena creation commands
    default: op

  tnttag.admin.control:
    description: Game control commands
    default: op
```

**Permission Hierarchy**:
```
tnttag.admin
  ├── tnttag.admin.setup
  │   ├── setpos1
  │   ├── setpos2
  │   ├── creategame
  │   └── delete
  └── tnttag.admin.control
      ├── start
      ├── stop
      └── reload
```

**Enforcement**:
- Commands check `player.isOp()` directly (per FR-076)
- Permission nodes provided for future permission plugin integration
- Non-OP players get clear error message (per FR-077)

---

## Command Execution Flow

### Player Command Flow

```
/tnttag join arena_name
     ↓
Validate command syntax
     ↓
Check permission (tnttag.play)
     ↓
Validate arena exists
     ↓
Check game state (joinable?)
     ↓
Add player to game
     ↓
Update player HUD
     ↓
Broadcast join message
     ↓
Check if 20th player → start countdown
```

### Admin Command Flow (Arena Creation)

```
/tnttag setpos1
     ↓
Check OP permission
     ↓
Create/Update ArenaSetupSession
     ↓
Save pos1 location
     ↓
Confirm to admin

(Later...)

/tnttag setpos2
     ↓
Check OP permission
     ↓
Get ArenaSetupSession
     ↓
Validate pos1 exists
     ↓
Save pos2 location
     ↓
Display dimensions

(Finally...)

/tnttag creategame name
     ↓
Check OP permission
     ↓
Validate session complete
     ↓
Validate positions (world, distance)
     ↓
Validate name unique
     ↓
Create Arena object
     ↓
Setup WorldBorder
     ↓
Save to arenas.yml
     ↓
Clear session
     ↓
Confirm creation
```

---

## Error Handling Standards

### User-Friendly Error Messages

All errors should:
1. Start with `§c` (red color)
2. Clearly state what went wrong
3. Suggest corrective action when possible
4. Use Japanese for Japanese messages (per FR locale)

### Examples

**Good**:
```
§cArena 'test' does not exist. Use /tnttag list to see available arenas.
§cこのコマンドを実行する権限がありません (OP required)
§cYou must set position 2 first with /tnttag setpos2
```

**Bad**:
```
Error: null
Arena validation failed
Invalid input
```

---

## Summary

**Total Commands**: 11
- **Player Commands**: 4 (join, leave, stats, list)
- **Admin Commands**: 7 (setpos1, setpos2, creategame, delete, start, stop, reload)

**Permission Requirements**:
- Player commands: `tnttag.play` (default: true)
- Admin commands: OP permission (enforced via `player.isOp()`)

**Command Aliases**: `/tnt`, `/tag` (shorthand for `/tnttag`)

**Tab Completion**: Enabled for all subcommands and arena names

**Localization**: Error messages support Japanese (ja_JP) and English

**Next**: Create developer quickstart guide
