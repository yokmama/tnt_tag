# Data Model: TNT TAG

**Feature**: TNT TAG - Minecraft Survival Tag Minigame
**Date**: 2025-10-24
**Status**: Phase 1 Design

## Overview

This document defines the core data entities for the TNT TAG plugin. All entities are derived from the feature specification's Key Entities section and functional requirements. The model supports the 6-round survival tag gameplay, arena management, player state tracking, HUD rendering, and statistics persistence.

## Entity Diagrams

### Core Game Entities

```
GameInstance (1) -------- (6) Round
     |
     | (1)
     |
     | (1)
   Arena
     |
     | (0..*)
     |
PlayerGameData (*)-------- (1) Player
     |
     | (1)
     |
  HUDState
     |
     | (0..1) [if TNT holder]
     |
TNTHolderState
```

### Arena Management

```
ArenaSetupSession (*) ------- (1) Administrator (Player with OP)
     |
     | temporary storage
     |
   Arena (after creategame)
```

### Statistics

```
Player (1) -------- (1) GameStatistics
```

---

## Entity Definitions

### 1. Player

**Description**: An individual participating in the game

**Source**: Minecraft Bukkit Player object (org.bukkit.entity.Player)

**Extended By**: PlayerGameData, GameStatistics

**Fields**: Managed by Bukkit (UUID, username, location, inventory, etc.)

**Relationships**:
- Has one `PlayerGameData` (per active game)
- Has one `GameStatistics` (persistent)
- May have one `ArenaSetupSession` (if OP setting up arena)

**State Transitions**:
```
Lobby → In-Game (Alive) → Eliminated (Spectator) → Lobby
  ↑                                                     ↓
  └─────────────────────────────────────────────────────┘
```

---

### 2. PlayerGameData

**Description**: Per-player game state during an active game session

**Lifecycle**: Created when player joins game, destroyed when game ends

**Fields**:

| Field | Type | Description | Validation |
|-------|------|-------------|------------|
| `player` | Player | Reference to Bukkit player | Not null |
| `status` | PlayerStatus (enum) | Current state | One of: ALIVE, ELIMINATED, SPECTATOR |
| `isTNTHolder` | boolean | Whether currently holding TNT | Default: false |
| `roundEliminated` | Integer | Round number when eliminated | Null if alive, 1-6 if eliminated |
| `tagsGiven` | int | TNT transfers to other players | ≥ 0 |
| `tagsReceived` | int | TNT transfers received | ≥ 0 |
| `survivalTime` | long | Total time survived (seconds) | ≥ 0 |
| `hudState` | HUDState | HUD display configuration | Not null |
| `tntHolderState` | TNTHolderState | TNT possession state | Null if not holder |

**State Transitions**:
```java
enum PlayerStatus {
    ALIVE,       // Participating in current round
    ELIMINATED,  // Exploded, now spectator
    SPECTATOR    // Joined mid-game or eliminated
}
```

**Validation Rules**:
- If `status == ELIMINATED`, then `roundEliminated` must be set (1-6)
- If `isTNTHolder == true`, then `status == ALIVE` (eliminated players can't hold TNT)
- If `isTNTHolder == true`, then `tntHolderState` must not be null
- `tagsGiven` ≥ `tagsReceived` (can't receive more than given + initial assignment)

---

### 3. TNTHolderState

**Description**: TNT possession status for a player currently holding TNT

**Lifecycle**: Created when TNT assigned, destroyed when TNT transferred or player explodes

**Fields**:

| Field | Type | Description | Validation |
|-------|------|-------------|------------|
| `player` | Player | Reference to Bukkit player | Not null |
| `fuseTaskId` | int | Bukkit task ID for fuse sound loop | Valid task ID |
| `particleTaskId` | int | Bukkit task ID for particle effects | Valid task ID |
| `tntBlockId` | int | Armor stand entity ID with TNT block | Valid entity ID |
| `bossBarId` | UUID | Boss bar UUID | Not null |
| `receivedAt` | long | Timestamp when TNT was received (millis) | > 0 |
| `transferCooldownUntil` | long | Timestamp when transfer cooldown ends (millis) | > receivedAt |

**Validation Rules**:
- `transferCooldownUntil == receivedAt + 500` (0.5-second cooldown per FR-005)
- All task IDs must be valid (scheduler confirmed registration)
- `tntBlockId` must reference armor stand entity at player's head location

**Cleanup**:
- Cancel all scheduled tasks on TNT transfer or explosion
- Remove boss bar
- Remove armor stand entity
- Reset player speed to Speed I

---

### 4. HUDState

**Description**: Player's current HUD display configuration

**Lifecycle**: Created when player joins game, updated every 0.5-1 second, destroyed when game ends

**Fields**:

| Field | Type | Description | Validation |
|-------|------|-------------|------------|
| `player` | Player | Reference to Bukkit player | Not null |
| `scoreboardContent` | ScoreboardData | Scoreboard lines | Not null |
| `actionBarMessage` | String | Action bar text | Not null, max 256 chars |
| `bossBarVisible` | boolean | Whether boss bar should display | False if not TNT holder |
| `bossBarProgress` | double | Boss bar fill percentage | 0.0 to 1.0 |
| `bossBarColor` | BossBarColor | Boss bar color | RED for TNT holders |
| `bossBarStyle` | BossBarStyle | Boss bar style | SOLID or SEGMENTED |
| `lastUpdated` | long | Timestamp of last update (millis) | > 0 |

**Nested Type: ScoreboardData**:

| Field | Type | Description |
|-------|------|-------------|
| `title` | String | "=== TNT TAG ===" |
| `phase` | GamePhase (enum) | Current phase |
| `lines` | List<String> | Dynamic content lines (max 15) |

**Phase-Specific Content** (from FR-024 to FR-029):

```java
enum GamePhase {
    WAITING,    // "状態: 待機中", "プレイヤー: X/20-25人", "最低20人で開始"
    COUNTDOWN,  // "状態: まもなく開始", "開始まで: X秒"
    IN_GAME,    // "ラウンド: X/6", "残り時間: 0:XX", "生存者: X人", "TNT保持者: X人"
    SPECTATING  // "状態: 観戦中", "脱落ラウンド: X", plus current round info
}
```

**Update Frequencies** (from research.md):
- Scoreboard: Every 20 ticks (1/sec) - async
- Action Bar: Every 10 ticks (2/sec) - async
- Boss Bar: Every 2 ticks (10/sec) - async

**Validation Rules**:
- If `bossBarVisible == true`, then player must be TNT holder
- `actionBarMessage` must match player's current status (holder/non-holder/spectator)
- `scoreboardContent.lines` count ≤ 15 (Minecraft limit)

---

### 5. GameInstance

**Description**: A single game session containing 6 rounds

**Lifecycle**: Created when game starts, destroyed when game ends (after results displayed)

**Fields**:

| Field | Type | Description | Validation |
|-------|------|-------------|------------|
| `id` | UUID | Unique game identifier | Not null |
| `arena` | Arena | Physical game space | Not null |
| `players` | List<PlayerGameData> | All participants | Size: 20-25 (per FR-001) |
| `state` | GameState (enum) | Current game state | See state machine below |
| `currentRound` | Round | Active round | Null if not in-game, otherwise 1-6 |
| `completedRounds` | int | Number of finished rounds | 0-6 |
| `startTime` | long | Game start timestamp (millis) | > 0 |
| `endTime` | long | Game end timestamp (millis) | Null if active, > startTime if ended |

**State Machine**:

```java
enum GameState {
    WAITING,        // <20 players in lobby
    STARTING,       // 10-second countdown
    IN_GAME,        // Active round
    ROUND_ENDING,   // 3-second pause between rounds
    ENDING          // Displaying results
}

State transitions:
WAITING → STARTING (when 20th player joins)
STARTING → IN_GAME (after 10-second countdown)
IN_GAME → ROUND_ENDING (when round timer expires)
ROUND_ENDING → IN_GAME (if rounds < 6 and survivors ≥ 1)
ROUND_ENDING → ENDING (if rounds == 6 or survivors == 0)
ENDING → [destroyed] (after results displayed)
```

**Validation Rules**:
- `players.size() >= 20` when `state == STARTING` (per FR-054)
- `players.size() <= 25` always (per FR-001)
- `currentRound` not null when `state == IN_GAME`
- `completedRounds` matches number of finished Round objects

---

### 6. Round

**Description**: A phase of the game (1 of 6) with specific configuration

**Lifecycle**: Created at round start, destroyed at round end

**Fields**:

| Field | Type | Description | Validation |
|-------|------|-------------|------------|
| `number` | int | Round number | 1-6 |
| `gameInstance` | GameInstance | Parent game | Not null |
| `config` | RoundConfig | Round-specific settings | Not null |
| `tntHolders` | Set<Player> | Current TNT holders | Size per config |
| `alivePlayers` | Set<Player> | Non-eliminated players | Size ≥ TNT holders |
| `startTime` | long | Round start timestamp (millis) | > 0 |
| `duration` | int | Round duration (seconds) | Per config |
| `remainingTime` | int | Countdown timer (seconds) | 0 to duration |
| `exploded` | Set<Player> | Players eliminated this round | Size == tntHolders.size() |

**RoundConfig** (from FR-011 to FR-015):

| Round | TNT Holders | Duration | Glowing All |
|-------|-------------|----------|-------------|
| 1 | 1 player | 40s | false |
| 2 | 25% of alive | 30s | false |
| 3 | 25% of alive | 30s | false |
| 4 | 50% of alive | 25s | false |
| 5 | 33% of alive | 40s | true |
| 6 | 33% of alive | 50s | true |

**Validation Rules**:
- `tntHolders.size()` matches config formula:
  - Round 1: exactly 1
  - Round 2-3: `Math.ceil(alivePlayers.size() * 0.25)`
  - Round 4: `Math.ceil(alivePlayers.size() * 0.5)`
  - Round 5-6: `Math.ceil(alivePlayers.size() * 0.33)`
- `remainingTime` decrements every second from `duration` to 0
- When `remainingTime == 0`, all `tntHolders` added to `exploded`
- `alivePlayers` excludes `exploded` players

---

### 7. Arena

**Description**: Physical game space with boundaries and spawn point

**Lifecycle**: Created via `/tnttag creategame`, persisted in arenas.yml, destroyed via `/tnttag delete`

**Fields**:

| Field | Type | Description | Validation |
|-------|------|-------------|------------|
| `name` | String | Unique arena identifier | 3-32 chars, alphanumeric |
| `world` | World | Minecraft world reference | Valid world |
| `pos1` | Location | First corner of boundary | Not null |
| `pos2` | Location | Second corner of boundary | Not null |
| `centerSpawn` | Location | Calculated center point | Between pos1 and pos2 |
| `radius` | double | Distance from center to edge | > 10 blocks |
| `podiumLocation` | Location | Winner teleport destination | Optional (3 blocks above center) |
| `worldBorder` | WorldBorder | Bukkit world border instance | Not null |
| `currentGame` | GameInstance | Active game | Null if available |

**Validation Rules** (from FR-066 to FR-071):
- `pos1.world == pos2.world` (same world per edge case)
- `pos1.distance(pos2) >= 10` (minimum arena size per edge case)
- `centerSpawn` calculated as midpoint: `((pos1 + pos2) / 2)`
- `radius` calculated as `max(|pos2.x - pos1.x|, |pos2.z - pos1.z|) / 2`
- `worldBorder.center == centerSpawn` and `worldBorder.size == radius * 2`
- `name` must be unique across all arenas

**Persistence** (arenas.yml):
```yaml
arenas:
  arena_name:
    world: "world"
    pos1: {x: 100, y: 64, z: 100}
    pos2: {x: -100, y: 64, z: -100}
    center_spawn: {x: 0, y: 64, z: 0}
    radius: 100.0
    podium: {x: 0, y: 67, z: 0} # Optional
```

---

### 8. ArenaSetupSession

**Description**: Temporary storage for administrator's pos1/pos2 during arena creation

**Lifecycle**: Created when `/tnttag setpos1` executed, destroyed after `/tnttag creategame` or admin disconnect

**Fields**:

| Field | Type | Description | Validation |
|-------|------|-------------|------------|
| `admin` | Player | Administrator (OP) | Must have OP permission |
| `pos1` | Location | First corner position | Null until setpos1 |
| `pos2` | Location | Second corner position | Null until setpos2 |
| `createdAt` | long | Session start timestamp (millis) | > 0 |

**Validation Rules** (from FR-070):
- Both `pos1` and `pos2` must be non-null before arena creation
- Session cleared on admin disconnect (edge case)
- Multiple admins can have independent sessions (FR-071)

---

### 9. GameStatistics

**Description**: Historical player performance data

**Lifecycle**: Created on first game join, updated at game end, persisted indefinitely

**Fields**:

| Field | Type | Description | Validation |
|-------|------|-------------|------------|
| `playerUUID` | UUID | Player unique identifier | Not null |
| `playerName` | String | Last known username | Not null |
| `gamesPlayed` | int | Total games participated | ≥ 0 |
| `totalRoundsSurvived` | int | Sum of rounds survived | ≥ 0 |
| `wins` | int | Games where player won | ≥ 0, ≤ gamesPlayed |
| `tntTagsGiven` | int | Total TNT transfers to others | ≥ 0 |
| `tntTagsReceived` | int | Total TNT transfers received | ≥ 0 |
| `averageSurvivalTime` | double | Mean survival time per game (seconds) | ≥ 0 |
| `lastUpdated` | long | Timestamp of last update (millis) | > 0 |

**Validation Rules**:
- `wins ≤ gamesPlayed`
- `averageSurvivalTime` calculated as `totalSurvivalTime / gamesPlayed`
- Updated atomically at game end (no partial updates)

**Persistence** (YAML per player):
```yaml
# plugins/TNTTag/stats/<uuid>.yml
player:
  uuid: "a1b2c3d4-e5f6-7890-abcd-ef1234567890"
  name: "PlayerName"

stats:
  games_played: 42
  total_rounds_survived: 187
  wins: 3
  tnt_tags_given: 89
  tnt_tags_received: 76
  average_survival_time: 145.3

last_updated: "2025-10-24T21:00:00Z"
```

---

## Data Flow Examples

### Example 1: TNT Transfer

```
1. Player A (TNT holder) hits Player B
   ├─ EntityDamageByEntityEvent fires
   ├─ Validate: A.isTNTHolder && !B.isTNTHolder && !A.isOnCooldown()
   └─ If valid:
      ├─ Create TNTTagEvent (custom event)
      ├─ A.tntHolderState → cleanup (cancel tasks, remove boss bar, TNT block)
      ├─ A.isTNTHolder = false
      ├─ A.tagsGiven++
      ├─ B.isTNTHolder = true
      ├─ B.tagsReceived++
      ├─ B.tntHolderState → create (start tasks, show boss bar, spawn TNT block)
      ├─ A.transferCooldownUntil = now + 500ms
      └─ Update HUD for both players (A: safe, B: warning)
```

### Example 2: Round Countdown Expiration

```
1. Round.remainingTime reaches 0
   ├─ For each player in Round.tntHolders:
   │  ├─ Spawn explosion particles (EXPLOSION_LARGE)
   │  ├─ Play explosion sound
   │  ├─ Apply blindness effect (1s)
   │  ├─ Create smoke ring particles
   │  ├─ PlayerGameData.status = ELIMINATED
   │  ├─ PlayerGameData.roundEliminated = Round.number
   │  ├─ Move to spectator mode
   │  └─ Update HUD to spectator view
   ├─ Round.exploded = Round.tntHolders (copy)
   ├─ Round.alivePlayers.removeAll(Round.exploded)
   └─ If Round.number < 6 && Round.alivePlayers.size() > 0:
      ├─ GameInstance.state = ROUND_ENDING
      ├─ Wait 3 seconds
      └─ Start next round
   Else:
      ├─ GameInstance.state = ENDING
      ├─ Determine winner(s)
      ├─ Spawn victory effects (fireworks, particles, glowing)
      └─ Display results
```

### Example 3: Arena Creation

```
1. Admin executes `/tnttag setpos1`
   ├─ Validate: player.isOp()
   ├─ ArenaSetupSession session = getOrCreate(admin)
   └─ session.pos1 = admin.getLocation()

2. Admin moves and executes `/tnttag setpos2`
   ├─ Validate: player.isOp()
   ├─ ArenaSetupSession session = get(admin)
   └─ session.pos2 = admin.getLocation()

3. Admin executes `/tnttag creategame arena_name`
   ├─ Validate: player.isOp()
   ├─ ArenaSetupSession session = get(admin)
   ├─ Validate: session.pos1 != null && session.pos2 != null
   ├─ Validate: session.pos1.world == session.pos2.world
   ├─ Validate: session.pos1.distance(session.pos2) >= 10
   ├─ Validate: !arenaExists(arena_name)
   └─ If all valid:
      ├─ Arena arena = new Arena(arena_name, session.pos1, session.pos2)
      ├─ arena.centerSpawn = calculateCenter(pos1, pos2)
      ├─ arena.radius = calculateRadius(pos1, pos2)
      ├─ arena.worldBorder = setupWorldBorder(arena)
      ├─ Save to arenas.yml
      ├─ Destroy session
      └─ Confirm to admin
```

---

## Indexes & Queries

### In-Memory Indexes (for performance)

```java
// GameManager
Map<UUID, GameInstance> activeGames;          // By game ID
Map<Arena, GameInstance> gamesByArena;        // By arena
Map<Player, GameInstance> playerToGame;       // Player lookup

// ArenaManager
Map<String, Arena> arenasByName;              // By name (unique)
Map<World, List<Arena>> arenasByWorld;        // By world

// PlayerManager
Map<Player, PlayerGameData> playerData;       // Current game data
Map<UUID, GameStatistics> statsByUUID;        // Persistent stats (cached)

// ArenaSetupSession (transient)
Map<Player, ArenaSetupSession> activeSessions; // By admin player
```

### Common Queries

1. **Get player's current game**: `playerToGame.get(player)`
2. **Get arena's active game**: `gamesByArena.get(arena)`
3. **Get available arenas**: `arenasByName.values().filter(a -> a.currentGame == null)`
4. **Get all alive players in round**: `round.alivePlayers.filter(p -> !round.exploded.contains(p))`
5. **Get TNT holders in round**: `round.tntHolders`
6. **Get player stats**: `statsByUUID.get(player.getUniqueId())`

---

## Summary

**Total Entities**: 9 core entities
**Persistent**: 2 (Arena, GameStatistics)
**Transient**: 7 (GameInstance, Round, PlayerGameData, TNTHolderState, HUDState, ArenaSetupSession, RoundConfig)

**Key Relationships**:
- GameInstance → Arena (many-to-one)
- GameInstance → Round (one-to-many, max 6)
- GameInstance → PlayerGameData (one-to-many, 20-25)
- PlayerGameData → HUDState (one-to-one)
- PlayerGameData → TNTHolderState (one-to-zero-or-one)
- Player → GameStatistics (one-to-one, persistent)
- Administrator → ArenaSetupSession (one-to-zero-or-one, temporary)

**Next Phase**: Define event contracts and command interfaces.
