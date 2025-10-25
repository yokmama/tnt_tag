# Implementation Tasks: TNT TAG Plugin

**Feature**: TNT TAG - Minecraft Survival Tag Minigame
**Date**: 2025-10-25
**Status**: Ready for Implementation

---

## Task Organization

Tasks are organized into 8 phases:
1. **Phase 1: Setup** - Project structure and build configuration
2. **Phase 2: Foundational** - Core plugin infrastructure
3. **Phase 3: User Story 1** - Core Tag Gameplay (P1)
4. **Phase 4: User Story 2** - Multi-Round Progression (P1)
5. **Phase 5: User Story 3** - HUD System (P2)
6. **Phase 6: User Story 4** - Arena Management (P2)
7. **Phase 7: User Story 5** - Statistics (P3)
8. **Phase 8: Polish & Cross-Cutting** - Documentation and optimization

**Priority Levels**: P1 (Critical), P2 (Important), P3 (Nice-to-have)

---

## Phase 1: Setup

### Project Structure and Build Configuration

- [ ] [TASK-001] [P1] Create Maven project structure with standard directories (src/main/java, src/main/resources, src/test/java)
- [ ] [TASK-002] [P1] Configure pom.xml with Paper API 1.20.x dependency and Java 17 compiler settings
- [ ] [TASK-003] [P1] Add MockBukkit test dependency (version 3.9.0) to pom.xml for unit testing
- [ ] [TASK-004] [P1] Add Testcontainers dependency to pom.xml for integration testing (optional)
- [ ] [TASK-005] [P1] Create base package structure: com.example.tnttag with subdirectories (commands, game, player, arena, hud, effects, events, listeners, stats, config)
- [ ] [TASK-006] [P1] Create .gitignore with standard Java/Maven entries (target/, *.class, *.jar, .idea/, buildtools/)

---

## Phase 2: Foundational Infrastructure

### Core Plugin Setup

- [ ] [TASK-007] [P1] Create src/main/resources/plugin.yml with plugin metadata (name, version, main, api-version: 1.20, author)
- [ ] [TASK-008] [P1] Implement TNTTagPlugin.java main class extending JavaPlugin with onEnable() and onDisable() lifecycle methods
- [ ] [TASK-009] [P1] Create ConfigManager.java in config/ package to load and manage config.yml using FileConfiguration API
- [ ] [TASK-010] [P1] Create MessageManager.java in config/ package to load messages_ja_JP.yml with placeholder support
- [ ] [TASK-011] [P1] Create src/main/resources/config.yml with default game settings (min_players: 20, max_players: 25, rounds: 6, tag_cooldown: 0.5)
- [ ] [TASK-012] [P1] Create src/main/resources/messages_ja_JP.yml with Japanese message templates for game events
- [ ] [TASK-013] [P1] Register event listeners in TNTTagPlugin.onEnable() using getServer().getPluginManager().registerEvents()

### Custom Events

- [ ] [TASK-014] [P1] [US1] Create TNTTagEvent.java in events/ package extending Event and implementing Cancellable (FR-001)
- [ ] [TASK-015] [P1] [US1] Create TNTTagStartEvent.java in events/ package for game start notifications
- [ ] [TASK-016] [P1] [US2] Create TNTTagRoundStartEvent.java in events/ package with round number and TNT holder list (FR-016)
- [ ] [TASK-017] [P1] [US2] Create TNTExplosionEvent.java in events/ package with victims list and round number (FR-018)
- [ ] [TASK-018] [P1] [US1] Create TNTTagEndEvent.java in events/ package with EndReason enum (ALL_ROUNDS_COMPLETE, NO_SURVIVORS, FORCE_STOPPED)

---

## Phase 3: User Story 1 - Core Tag Gameplay (Priority: P1)

### Game State Management

- [ ] [TASK-019] [P1] [US1] Create GameState.java enum in game/ package (WAITING, STARTING, IN_GAME, ROUND_ENDING, ENDING)
- [ ] [TASK-020] [P1] [US1] Create GameManager.java in game/ package as singleton to manage all active game instances
- [ ] [TASK-021] [P1] [US1] Create GameInstance.java in game/ package with fields: UUID id, Arena arena, Set<Player> players, GameState state, int currentRound (FR-001)
- [ ] [TASK-022] [P1] [US1] Implement GameInstance.start() method to initialize game and fire TNTTagStartEvent
- [ ] [TASK-023] [P1] [US1] Implement GameInstance.stop() method to clean up game state and fire TNTTagEndEvent

### Player State Management

- [ ] [TASK-024] [P1] [US1] Create PlayerGameData.java in player/ package with fields: UUID playerUUID, boolean isAlive, boolean isTNTHolder, int roundssurvived (FR-001)
- [ ] [TASK-025] [P1] [US1] Create PlayerManager.java in player/ package to track PlayerGameData for all active players
- [ ] [TASK-026] [P1] [US1] Create TNTHolderState.java in player/ package with fields: Player player, long cooldownExpiry (FR-005)
- [ ] [TASK-027] [P1] [US1] Implement PlayerManager.setTNTHolder() method to apply Speed II effect and visual indicators (FR-003)
- [ ] [TASK-028] [P1] [US1] Implement PlayerManager.removeTNTHolder() method to remove Speed II and restore Speed I (FR-004)

### TNT Transfer Mechanics

- [ ] [TASK-029] [P1] [US1] Create TNTTransferListener.java in listeners/ package to listen for EntityDamageByEntityEvent
- [ ] [TASK-030] [P1] [US1] Implement TNTTransferListener.onEntityDamage() to detect player-to-player hits and cancel damage (FR-010)
- [ ] [TASK-031] [P1] [US1] Implement TNT transfer logic: check if attacker is TNT holder, victim is not holder, and cooldown expired (FR-001, FR-005)
- [ ] [TASK-032] [P1] [US1] Fire TNTTagEvent when TNT transfer occurs with tagger, tagged, roundNumber, remainingTime (FR-001)
- [ ] [TASK-033] [P1] [US1] Apply 0.5-second cooldown to tagger after successful transfer using System.currentTimeMillis() (FR-005)
- [ ] [TASK-034] [P1] [US1] Swap Speed effects: remove Speed II from tagger, add Speed I; add Speed II to tagged, remove Speed I (FR-003, FR-004)

### Visual TNT Indicators

- [ ] [TASK-035] [P1] [US1] Implement TNT block head replacement for TNT holders using Player.getInventory().setHelmet(new ItemStack(Material.TNT)) (FR-006)
- [ ] [TASK-036] [P1] [US1] Add red glowing effect to TNT holders using Player.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, ...)) (FR-007)
- [ ] [TASK-037] [P1] [US1] Create EffectManager.java in effects/ package to coordinate all visual effects
- [ ] [TASK-038] [P1] [US1] Create ParticleEffects.java in effects/ package with spawnParticleOptimized() method using distance-based reduction (20-block radius)
- [ ] [TASK-039] [P1] [US1] Implement smoke and flame particle trail for TNT holders using SMOKE_NORMAL and FLAME particles (FR-008)

### Explosion Mechanics

- [ ] [TASK-040] [P1] [US1] Implement countdown timer in GameInstance using BukkitScheduler.runTaskTimer() with 1-tick (0.05s) precision
- [ ] [TASK-041] [P1] [US1] Create explosion handler in GameInstance to process TNT holders when timer reaches zero (FR-011)
- [ ] [TASK-042] [P1] [US1] Fire TNTExplosionEvent with list of victims and round number before elimination (FR-018)
- [ ] [TASK-043] [P1] [US1] Set eliminated players' GameMode to SPECTATOR (FR-013)
- [ ] [TASK-044] [P1] [US1] Play explosion particle effect at victim locations using ParticleEffects.spawnExplosionEffect() (FR-012)
- [ ] [TASK-045] [P1] [US1] Ensure no block destruction or damage to other players from explosions (FR-014, FR-015)
- [ ] [TASK-046] [P1] [US1] Update PlayerGameData.isAlive = false and increment roundsEliminated for victims

### PVP Damage Prevention

- [ ] [TASK-047] [P1] [US1] Create PlayerListener.java in listeners/ package to handle general player events
- [ ] [TASK-048] [P1] [US1] Implement PlayerListener.onEntityDamageByEntity() to cancel all PVP damage using event.setCancelled(true) (FR-010)
- [ ] [TASK-049] [P1] [US1] Ensure damage cancellation does NOT prevent TNT transfer detection in TNTTransferListener

---

## Phase 4: User Story 2 - Multi-Round Progression (Priority: P1)

### Round Configuration

- [ ] [TASK-050] [P1] [US2] Create RoundConfig.java in game/ package with fields: int roundNumber, double tntHolderRatio, int duration, boolean glowing (FR-016)
- [ ] [TASK-051] [P1] [US2] Create Round.java in game/ package with fields: RoundConfig config, Set<Player> tntHolders, int remainingTime, long startTime
- [ ] [TASK-052] [P1] [US2] Define 6 RoundConfig instances in config.yml matching specification (Round 1: 1 holder, 40s; Round 2-3: 25%, 30s; Round 4: 50%, 25s; Round 5-6: 33%, 40/50s, glowing) (FR-016)
- [ ] [TASK-053] [P1] [US2] Implement ConfigManager.loadRoundConfigs() method to parse round configurations from YAML

### Round Lifecycle

- [ ] [TASK-054] [P1] [US2] Implement GameInstance.startRound() method to initialize new Round instance and select TNT holders (FR-016)
- [ ] [TASK-055] [P1] [US2] Implement TNT holder selection logic: Round 1 selects 1 player, other rounds use tntHolderRatio * alivePlayers with Math.max(1, ...) (FR-016)
- [ ] [TASK-056] [P1] [US2] Apply glowing effect to ALL players in rounds 5 and 6 using PotionEffect(PotionEffectType.GLOWING, ...) (FR-017)
- [ ] [TASK-057] [P1] [US2] Fire TNTTagRoundStartEvent with gameInstance, roundNumber, tntHolders, duration (FR-016)
- [ ] [TASK-058] [P1] [US2] Teleport all alive players to arena center spawn point at round start using Player.teleport() (FR-019)
- [ ] [TASK-059] [P1] [US2] Apply Speed I effect to all non-TNT holders at round start (FR-004)
- [ ] [TASK-060] [P1] [US2] Apply Speed II effect to all TNT holders at round start (FR-003)

### Round Progression

- [ ] [TASK-061] [P1] [US2] Implement GameInstance.tickRound() method called every tick (0.05s) to update remainingTime
- [ ] [TASK-062] [P1] [US2] Implement GameInstance.endRound() method to process explosions and check for game end conditions (FR-018)
- [ ] [TASK-063] [P1] [US2] Check if only 1 player remains alive after round end → declare winner and end game (FR-020)
- [ ] [TASK-064] [P1] [US2] Check if all players eliminated → end game with NO_SURVIVORS reason (edge case)
- [ ] [TASK-065] [P1] [US2] Check if currentRound == 6 → end game with ALL_ROUNDS_COMPLETE reason (FR-020)
- [ ] [TASK-066] [P1] [US2] Implement automatic progression to next round after 3-second delay using BukkitScheduler.runTaskLater()
- [ ] [TASK-067] [P1] [US2] Increment GameInstance.currentRound counter after each round completion

### Game End Conditions

- [ ] [TASK-068] [P1] [US2] Implement GameInstance.checkWinCondition() to detect single survivor after round 6 (FR-020)
- [ ] [TASK-069] [P1] [US2] Fire TNTTagEndEvent with winner (nullable), endReason, finalRound, survivors list
- [ ] [TASK-070] [P1] [US2] Handle edge case: multiple survivors after round 6 → rank by roundsEliminated descending (lower is better)
- [ ] [TASK-071] [P1] [US2] Clean up game state: remove all potion effects, restore GameMode.SURVIVAL, clear TNT heads

---

## Phase 5: User Story 3 - HUD System (Priority: P2)

### HUD State Management

- [ ] [TASK-072] [P2] [US3] Create GamePhase.java enum in player/ package (WAITING, COUNTDOWN, IN_GAME, SPECTATING) (FR-024)
- [ ] [TASK-073] [P2] [US3] Create HUDState.java in player/ package with fields: GamePhase phase, Map<String, String> content
- [ ] [TASK-074] [P2] [US3] Create HUDManager.java in hud/ package to coordinate all HUD components
- [ ] [TASK-075] [P2] [US3] Implement HUDManager.updatePlayerState() to refresh HUDState based on PlayerGameData

### Scoreboard System

- [ ] [TASK-076] [P2] [US3] Create ScoreboardManager.java in hud/ package using Bukkit Scoreboard API
- [ ] [TASK-077] [P2] [US3] Implement ScoreboardManager.createScoreboard() to create Objective with DisplaySlot.SIDEBAR
- [ ] [TASK-078] [P2] [US3] Implement phase-specific scoreboard content: WAITING shows "状態: 待機中", "プレイヤー: X/20-25人" (FR-025)
- [ ] [TASK-079] [P2] [US3] Implement IN_GAME scoreboard content: "ラウンド: X/6", "残り時間: 0:XX", "生存者: X人", "TNT保持者: X人", player status (FR-027)
- [ ] [TASK-080] [P2] [US3] Implement SPECTATING scoreboard content: "状態: 観戦中", "現在のラウンド: X/6", "生存者: X人" (FR-028)
- [ ] [TASK-081] [P2] [US3] Schedule scoreboard updates every 20 ticks (1 second) using BukkitScheduler.runTaskTimerAsynchronously() (SC-017)
- [ ] [TASK-082] [P2] [US3] Implement ScoreboardManager.updateAll() to refresh scoreboards for all players in game

### Action Bar System

- [ ] [TASK-083] [P2] [US3] Create ActionBarManager.java in hud/ package using Player.spigot().sendMessage() with ChatMessageType.ACTION_BAR
- [ ] [TASK-084] [P2] [US3] Implement phase-specific action bar content: WAITING shows "ゲーム開始を待っています..." (FR-030)
- [ ] [TASK-085] [P2] [US3] Implement IN_GAME action bar for non-TNT holders: "残り時間: X秒 | 生存者: X人" (FR-031)
- [ ] [TASK-086] [P2] [US3] Implement IN_GAME action bar for TNT holders: "⚠ TNTを持っています！他の人にタッチ！⚠" (FR-032)
- [ ] [TASK-087] [P2] [US3] Implement explosion countdown in action bar: "💥 爆発まで 3... 2... 1... 💥" when remainingTime <= 3 seconds (FR-033)
- [ ] [TASK-088] [P2] [US3] Schedule action bar updates every 10 ticks (0.5 seconds = 2/sec) using BukkitScheduler.runTaskTimerAsynchronously() (SC-019)
- [ ] [TASK-089] [P2] [US3] Implement ActionBarManager.updateAll() to refresh action bars for all players

### Boss Bar System

- [ ] [TASK-090] [P2] [US3] Create BossBarManager.java in hud/ package using Bukkit.createBossBar() API
- [ ] [TASK-091] [P2] [US3] Implement boss bar creation with title "⚠ TNTを持っています! ⚠", BarColor.RED, BarStyle.SOLID (FR-034)
- [ ] [TASK-092] [P2] [US3] Show boss bar ONLY to TNT holders using bossBar.addPlayer() (FR-034)
- [ ] [TASK-093] [P2] [US3] Update boss bar progress as remainingTime / totalRoundDuration (1.0 = full, 0.0 = empty) (FR-035)
- [ ] [TASK-094] [P2] [US3] Schedule boss bar updates every 2 ticks (0.1 seconds = 10/sec) using BukkitScheduler.runTaskTimerAsynchronously() (SC-029)
- [ ] [TASK-095] [P2] [US3] Implement BossBarManager.updateProgress() to refresh all active boss bars
- [ ] [TASK-096] [P2] [US3] Remove boss bar from player when they pass TNT to someone else using bossBar.removePlayer()

### Title Messages

- [ ] [TASK-097] [P2] [US3] Create TitleManager.java in hud/ package using Player.sendTitle() API
- [ ] [TASK-098] [P2] [US3] Implement TitleManager.sendRoundStart() with "ROUND X" title, "TNTから逃げろ！" subtitle, 3-second display (FR-036)
- [ ] [TASK-099] [P2] [US3] Implement TitleManager.sendTNTReceived() with "TNTを受け取った！" title, "他のプレイヤーにタッチ！" subtitle (FR-037)
- [ ] [TASK-100] [P2] [US3] Implement TitleManager.sendTNTPassed() with "TNTを渡した！" title, "安全だ！" subtitle (FR-038)
- [ ] [TASK-101] [P2] [US3] Implement TitleManager.sendExplosion() with "💥 BOOM! 💥" title, "あなたは爆発しました" subtitle (FR-039)
- [ ] [TASK-102] [P2] [US3] Implement TitleManager.sendVictory() with "🏆 VICTORY! 🏆" title, "最後の生存者！" subtitle (FR-040)
- [ ] [TASK-103] [P2] [US3] Set title timings: fadeIn 10 ticks, stay 60 ticks (3 seconds), fadeOut 10 ticks (FR-041)

### HUD Performance Optimization

- [ ] [TASK-104] [P2] [US3] Implement async updates for all HUD components to avoid main thread blocking (SC-027)
- [ ] [TASK-105] [P2] [US3] Add null checks and player validation before sending HUD updates to prevent errors on disconnect
- [ ] [TASK-106] [P2] [US3] Implement HUD cleanup in GameInstance.stop() to remove scoreboards, boss bars, and clear titles

---

## Phase 6: User Story 4 - Arena Management (Priority: P2)

### Arena Data Model

- [ ] [TASK-107] [P2] [US4] Create Arena.java in arena/ package with fields: String name, World world, Location pos1, Location pos2, Location centerSpawn (FR-049)
- [ ] [TASK-108] [P2] [US4] Implement Arena.getBoundingBox() method to calculate cuboid region from pos1 and pos2
- [ ] [TASK-109] [P2] [US4] Implement Arena.contains(Location) method to check if location is within arena boundaries
- [ ] [TASK-110] [P2] [US4] Implement Arena.getRadius() method to calculate radius from center to furthest corner

### Arena Setup Session

- [ ] [TASK-111] [P2] [US4] Create ArenaSetupSession.java in arena/ package with fields: Player admin, Location pos1, Location pos2, long lastUpdate (FR-050)
- [ ] [TASK-112] [P2] [US4] Create ArenaManager.java in arena/ package to manage all arenas and setup sessions
- [ ] [TASK-113] [P2] [US4] Implement ArenaManager.startSetupSession() to create new session for admin player
- [ ] [TASK-114] [P2] [US4] Implement session timeout: clear session after 5 minutes of inactivity (edge case)

### Arena Setup Commands

- [ ] [TASK-115] [P2] [US4] Create SetPos1Command.java in commands/admin/ package implementing CommandExecutor
- [ ] [TASK-116] [P2] [US4] Implement SetPos1Command.onCommand() to store player's current location as pos1 in setup session (FR-050)
- [ ] [TASK-117] [P2] [US4] Check OP permission using player.isOp(), send error if not OP: "§cこのコマンドを実行する権限がありません" (FR-076)
- [ ] [TASK-118] [P2] [US4] Send success message: "§aPosition 1 set at §e{x}, {y}, {z}" (FR-050)
- [ ] [TASK-119] [P2] [US4] Create SetPos2Command.java in commands/admin/ package with identical structure to SetPos1Command
- [ ] [TASK-120] [P2] [US4] Implement SetPos2Command.onCommand() to store player's current location as pos2 in setup session (FR-051)
- [ ] [TASK-121] [P2] [US4] Check that pos1 is already set in session, send error if not: "§cPosition 1が設定されていません" (FR-052)

### Arena Creation

- [ ] [TASK-122] [P2] [US4] Create CreateGameCommand.java in commands/admin/ package for /tnttag creategame <arena_name>
- [ ] [TASK-123] [P2] [US4] Implement CreateGameCommand.onCommand() to validate setup session has both pos1 and pos2 (FR-053)
- [ ] [TASK-124] [P2] [US4] Validate pos1 and pos2 are in the same world, send error if not: "§c両地点は同じワールドである必要があります" (edge case)
- [ ] [TASK-125] [P2] [US4] Validate distance between pos1 and pos2 >= 10 blocks, send error if too small: "§cアリーナが小さすぎます（最小: 10ブロック）" (edge case)
- [ ] [TASK-126] [P2] [US4] Validate arena name is unique, send error if duplicate: "§cそのアリーナ名は既に使用されています" (edge case)
- [ ] [TASK-127] [P2] [US4] Calculate centerSpawn as midpoint between pos1 and pos2 at Y-level of higher position (FR-054)
- [ ] [TASK-128] [P2] [US4] Create Arena instance and add to ArenaManager.arenas map (FR-055)
- [ ] [TASK-129] [P2] [US4] Save arena configuration to arenas.yml using ConfigManager (FR-055)
- [ ] [TASK-130] [P2] [US4] Send success message: "§aアリーナ '§e{name}§a' を作成しました！" (FR-056)
- [ ] [TASK-131] [P2] [US4] Clear setup session after successful arena creation (FR-057)

### Arena WorldBorder Setup

- [ ] [TASK-132] [P2] [US4] Implement Arena.setupWorldBorder() method using WorldBorder API
- [ ] [TASK-133] [P2] [US4] Set WorldBorder center to arena centerSpawn location
- [ ] [TASK-134] [P2] [US4] Set WorldBorder size to diameter (radius * 2) to contain entire arena
- [ ] [TASK-135] [P2] [US4] Set WorldBorder warning distance to 5 blocks for visual feedback (red screen effect)
- [ ] [TASK-136] [P2] [US4] Call setupWorldBorder() in GameInstance.start() to activate boundary for game

### Arena Management Commands

- [ ] [TASK-137] [P2] [US4] Create DeleteGameCommand.java in commands/admin/ for /tnttag delete <arena_name> (FR-058)
- [ ] [TASK-138] [P2] [US4] Implement delete command: check arena exists, check no active game, remove from ArenaManager, delete from arenas.yml (FR-059)
- [ ] [TASK-139] [P2] [US4] Create StartGameCommand.java in commands/admin/ for /tnttag start <arena_name> (FR-060)
- [ ] [TASK-140] [P2] [US4] Implement start command: validate arena exists, check min players in lobby, force-start game even if < min_players (FR-061)
- [ ] [TASK-141] [P2] [US4] Create StopGameCommand.java in commands/admin/ for /tnttag stop <arena_name> (FR-072)
- [ ] [TASK-142] [P2] [US4] Implement stop command: validate game is active, call GameInstance.stop() with FORCE_STOPPED reason, teleport players to spawn (FR-073)
- [ ] [TASK-143] [P2] [US4] Create ReloadCommand.java in commands/admin/ for /tnttag reload (FR-074)
- [ ] [TASK-144] [P2] [US4] Implement reload command: reload config.yml, messages_ja_JP.yml, arenas.yml without restarting plugin (FR-075)
- [ ] [TASK-145] [P2] [US4] Add OP permission check to all admin commands (delete, start, stop, reload) with error message (FR-077, FR-078)

### Player Commands

- [ ] [TASK-146] [P2] [US4] Create JoinCommand.java in commands/player/ for /tnttag join [arena_name] (FR-062)
- [ ] [TASK-147] [P2] [US4] Implement join command: validate arena exists, check game state is WAITING, check max players not exceeded, add player to game
- [ ] [TASK-148] [P2] [US4] Teleport player to arena lobby/waiting area and send confirmation message
- [ ] [TASK-149] [P2] [US4] Create LeaveCommand.java in commands/player/ for /tnttag leave (FR-063)
- [ ] [TASK-150] [P2] [US4] Implement leave command: validate player is in game, remove from GameInstance, teleport to spawn, clear potion effects
- [ ] [TASK-151] [P2] [US4] Create ListCommand.java in commands/player/ for /tnttag list (FR-065)
- [ ] [TASK-152] [P2] [US4] Implement list command: display all arenas with format "§e{name}§7 - §a[{state}]§7 ({players}/{maxPlayers} players)" (FR-048)

### Arena Persistence

- [ ] [TASK-153] [P2] [US4] Create src/main/resources/arenas.yml template file
- [ ] [TASK-154] [P2] [US4] Implement ArenaManager.saveArenas() to serialize all arenas to arenas.yml with pos1, pos2, centerSpawn coordinates
- [ ] [TASK-155] [P2] [US4] Implement ArenaManager.loadArenas() to deserialize arenas from YAML on plugin startup
- [ ] [TASK-156] [P2] [US4] Call loadArenas() in TNTTagPlugin.onEnable() after ConfigManager initialization
- [ ] [TASK-157] [P2] [US4] Call saveArenas() in TNTTagPlugin.onDisable() to persist changes on shutdown

---

## Phase 7: User Story 5 - Game Results and Statistics (Priority: P3)

### Results Display

- [ ] [TASK-158] [P3] [US5] Create ResultsManager.java in game/ package to generate end-game results
- [ ] [TASK-159] [P3] [US5] Implement ResultsManager.generateResults() to rank players by roundsEliminated (ascending = better) (FR-042)
- [ ] [TASK-160] [P3] [US5] Handle tie-breaking: if roundsEliminated equal, use total TNT tags given (descending) (edge case)
- [ ] [TASK-161] [P3] [US5] Format results message with header "========== GAME RESULT ==========" and "🏆 最終結果 🏆" (FR-043)
- [ ] [TASK-162] [P3] [US5] Display top 3 players with rank, name, and achievement (e.g., "全ラウンド生存！", "ラウンドX まで生存") (FR-044)
- [ ] [TASK-163] [P3] [US5] Display breakdown: "生存ラウンド数:" section showing elimination counts per round (FR-045)
- [ ] [TASK-164] [P3] [US5] Display total participants count at bottom (FR-046)
- [ ] [TASK-165] [P3] [US5] Send results to all players (alive and spectators) in GameInstance.stop() using player.sendMessage()

### Statistics Tracking

- [ ] [TASK-166] [P3] [US5] Create GameStatistics.java in stats/ package with fields: UUID playerUUID, int gamesPlayed, int totalRoundsSurvived, int wins, int tntTagsGiven, int tntTagsReceived, double averageSurvivalTime (FR-047)
- [ ] [TASK-167] [P3] [US5] Create StatsManager.java in stats/ package to manage player statistics
- [ ] [TASK-168] [P3] [US5] Implement per-player YAML file storage in plugins/TNTTag/stats/<uuid>.yml
- [ ] [TASK-169] [P3] [US5] Implement in-memory cache (Map<UUID, GameStatistics>) to avoid disk I/O during gameplay
- [ ] [TASK-170] [P3] [US5] Update statistics in GameInstance.stop(): increment gamesPlayed, totalRoundsSurvived, wins (if winner)
- [ ] [TASK-171] [P3] [US5] Track tntTagsGiven and tntTagsReceived in TNTTagEvent listener by incrementing counters
- [ ] [TASK-172] [P3] [US5] Calculate averageSurvivalTime as total time alive across all games / gamesPlayed
- [ ] [TASK-173] [P3] [US5] Implement StatsManager.saveStats() to write cache to YAML files asynchronously
- [ ] [TASK-174] [P3] [US5] Call saveStats() in TNTTagPlugin.onDisable() and after each game end

### Stats Command

- [ ] [TASK-175] [P3] [US5] Create StatsCommand.java in commands/player/ for /tnttag stats [player] (FR-064)
- [ ] [TASK-176] [P3] [US5] Implement stats command: if no argument, show sender's stats; if argument, show target player's stats
- [ ] [TASK-177] [P3] [US5] Format stats message with player name, games played, wins, win rate, average survival time, total rounds survived
- [ ] [TASK-178] [P3] [US5] Display TNT tags given/received ratio and total counts
- [ ] [TASK-179] [P3] [US5] Handle case where player has no stats: send "§cプレイヤーの統計が見つかりません"
- [ ] [TASK-180] [P3] [US5] Implement asynchronous stats loading to avoid blocking main thread when reading YAML files

---

## Phase 8: Polish & Cross-Cutting Concerns

### Visual Effects

- [ ] [TASK-181] [P2] Create SoundEffects.java in effects/ package to manage all sound playback
- [ ] [TASK-182] [P2] Implement game start effects: countdown sounds (BLOCK_NOTE_BLOCK_PLING), title display, green particles (VILLAGER_HAPPY), Regeneration effect (FR-024)
- [ ] [TASK-183] [P2] Implement round start effects: lightning strikes (world.strikeLightningEffect()), dramatic sound (ENTITY_LIGHTNING_BOLT_THUNDER), particle pillars (END_ROD) (FR-026)
- [ ] [TASK-184] [P2] Create FireworkEffects.java in effects/ package for victory fireworks
- [ ] [TASK-185] [P2] Implement victory effects: launch 10 fireworks at 0.5-second intervals using BukkitRunnable, golden particles (TOTEM), glowing effect, podium teleport (FR-042)
- [ ] [TASK-186] [P2] Implement defeat effects: smoke clouds (SMOKE_LARGE), somber sound (ENTITY_WITHER_SPAWN) for eliminated players
- [ ] [TASK-187] [P2] Implement elimination effects: explosion particles (EXPLOSION_LARGE), smoke rings (SMOKE_NORMAL), blindness effect (PotionEffectType.BLINDNESS, 3 seconds)
- [ ] [TASK-188] [P2] Implement TNT holder warning sounds: play導火線音 (ENTITY_TNT_PRIMED) in loop for TNT holders
- [ ] [TASK-189] [P2] Implement explosion countdown sounds: warning sound (BLOCK_ANVIL_LAND) at 5 seconds remaining

### Edge Case Handling

- [ ] [TASK-190] [P2] Handle player disconnect during game: mark as eliminated, redistribute TNT if holder (FR-009)
- [ ] [TASK-191] [P2] Handle simultaneous TNT transfers: use event priority and timestamp to determine order (edge case)
- [ ] [TASK-192] [P2] Handle fractional TNT holder calculations: use Math.max(1, Math.round(ratio * alivePlayers)) (edge case)
- [ ] [TASK-193] [P2] Handle zero survivors after round: end game immediately with NO_SURVIVORS reason (edge case)
- [ ] [TASK-194] [P2] Handle arena boundary violations: teleport player back to centerSpawn with warning message (edge case)
- [ ] [TASK-195] [P2] Handle player death from non-TNT causes: treat as elimination if in active game (edge case)
- [ ] [TASK-196] [P2] Handle WorldBorder edge case: different worlds have independent borders, use per-arena world configuration

### Command Registration

- [ ] [TASK-197] [P1] Register all commands in plugin.yml with proper aliases and usage strings
- [ ] [TASK-198] [P1] Set command executors in TNTTagPlugin.onEnable() using getCommand().setExecutor()
- [ ] [TASK-199] [P1] Implement tab completion for arena names in join, delete, start, stop commands using TabCompleter interface
- [ ] [TASK-200] [P1] Add permission checks to command executors with clear error messages

### Configuration Validation

- [ ] [TASK-201] [P2] Implement ConfigManager.validate() to check all required fields exist in config.yml
- [ ] [TASK-202] [P2] Validate min_players <= max_players, send warning to console if invalid
- [ ] [TASK-203] [P2] Validate all 6 round configurations have required fields (duration, tntHolderRatio or fixed count)
- [ ] [TASK-204] [P2] Provide default values for missing configuration entries with console warning

### Logging and Debugging

- [ ] [TASK-205] [P2] Add INFO-level logging for major game events (game start/end, round start/end, winner)
- [ ] [TASK-206] [P2] Add WARN-level logging for edge cases (player disconnect, configuration issues, arena errors)
- [ ] [TASK-207] [P2] Add DEBUG-level logging (configurable in config.yml) for TNT transfers, effect triggers, HUD updates
- [ ] [TASK-208] [P2] Implement error handling with try-catch blocks around async tasks, log exceptions with stack traces

### Performance Optimization

- [ ] [TASK-209] [P2] Implement particle distance culling: only render particles for players within 20-block radius (Decision 6)
- [ ] [TASK-210] [P2] Implement particle count reduction based on distance: farther players see fewer particles
- [ ] [TASK-211] [P2] Cache frequently accessed data: player positions updated every 0.5 seconds, distance calculations cached
- [ ] [TASK-212] [P2] Use async tasks for all non-critical operations: stats saving, config loading, HUD updates
- [ ] [TASK-213] [P2] Optimize TNT holder checks: use HashSet for O(1) lookup instead of List iteration

### Documentation

- [ ] [TASK-214] [P3] Update README.md with plugin description, features, installation instructions, command reference
- [ ] [TASK-215] [P3] Add Javadoc comments to all public methods and classes with @param, @return, @throws tags
- [ ] [TASK-216] [P3] Create example arenas.yml with sample arena configuration
- [ ] [TASK-217] [P3] Create example config.yml with comments explaining each setting
- [ ] [TASK-218] [P3] Create example messages_ja_JP.yml with all message keys and Japanese translations

### Cleanup and Finalization

- [ ] [TASK-219] [P1] Implement GameInstance cleanup in stop(): cancel all schedulers, remove boss bars, clear scoreboards, restore player states
- [ ] [TASK-220] [P1] Implement TNTTagPlugin.onDisable(): stop all active games, save all statistics, save arena configurations, cancel all async tasks
- [ ] [TASK-221] [P2] Test hot-reload functionality: /tnttag reload should update config without breaking active games
- [ ] [TASK-222] [P2] Verify no memory leaks: ensure all listeners are unregistered, all schedulers cancelled, all references cleared

---

## Task Summary by Priority

**P1 (Critical - Must Have)**: 71 tasks
- Phase 1: Setup (6 tasks)
- Phase 2: Foundational (13 tasks)
- Phase 3: User Story 1 - Core Tag Gameplay (31 tasks)
- Phase 4: User Story 2 - Multi-Round Progression (18 tasks)
- Phase 8: Command registration and cleanup (3 tasks)

**P2 (Important - Should Have)**: 126 tasks
- Phase 5: User Story 3 - HUD System (35 tasks)
- Phase 6: User Story 4 - Arena Management (51 tasks)
- Phase 8: Effects, edge cases, configuration, optimization (40 tasks)

**P3 (Nice-to-Have)**: 25 tasks
- Phase 7: User Story 5 - Statistics (23 tasks)
- Phase 8: Documentation (5 tasks)

**Total Tasks**: 222

---

## Dependencies and Ordering

**Recommended Implementation Order**:

1. **Phase 1 → Phase 2** (Setup and foundational infrastructure)
2. **Phase 3** (Core gameplay must work before anything else)
3. **Phase 4** (Multi-round depends on core gameplay)
4. **Phase 6** (Arena management needed for testing)
5. **Phase 5** (HUD can be added once core game works)
6. **Phase 7** (Statistics are lowest priority)
7. **Phase 8** (Polish and optimization throughout)

**Key Dependencies**:
- TASK-021 (GameInstance) must complete before TASK-029 (TNTTransferListener)
- TASK-051 (Round) must complete before TASK-054 (startRound)
- TASK-107 (Arena) must complete before TASK-122 (CreateGameCommand)
- TASK-075 (HUDManager) must complete before TASK-081 (scoreboard updates)
- All Phase 2 tasks must complete before any user story implementation

---

## Notes

- **No explicit test requirements**: The specification does not mandate unit or integration tests, but the quickstart.md recommends MockBukkit and Testcontainers. Consider adding test tasks if quality assurance is required.

- **Technology stack**: Java 17, Paper API 1.20.x (Spigot compatible), Maven build, YAML storage. See research.md for detailed technology decisions.

- **Performance targets**: Support 20-25 concurrent players with <50ms tick time. Async schedulers and particle optimization are critical.

- **Event-driven architecture**: All game state changes should fire custom events (TNTTagEvent, TNTTagStartEvent, etc.) for extensibility.

- **Configuration externalization**: All game parameters, messages, and arena definitions should be in YAML files with hot-reload support.

- **Edge case handling**: The specification defines 19 edge cases covering disconnects, simultaneous events, fractional calculations, and boundary violations. Ensure all are implemented.

---

**Ready for Implementation**: All tasks are defined with clear acceptance criteria tied to functional requirements. Begin with Phase 1 setup tasks.
