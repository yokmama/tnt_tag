# Feature Specification: TNT TAG - Minecraft Survival Tag Minigame

**Feature Branch**: `001-tnt-tag-game`
**Created**: 2025-10-24
**Status**: Draft
**Input**: User description: "TNT TAG - Minecraft survival tag minigame"

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Core Tag Gameplay (Priority: P1)

Players participate in a multiplayer tag game where they must avoid being the TNT holder when the countdown ends, as TNT holders explode and are eliminated.

**Why this priority**: This is the fundamental gameplay mechanic that defines the entire minigame. Without this, there is no game.

**Independent Test**: Can be fully tested by joining a game with minimum players (4 for testing, 20 for production), playing one round, and verifying that TNT holders explode at countdown end while others survive. Delivers the core value of the survival tag experience.

**Acceptance Scenarios**:

1. **Given** 20-25 players are in the lobby, **When** the game starts, **Then** round 1 begins with 1 randomly selected TNT holder and a 40-second countdown
2. **Given** a player is holding TNT, **When** they hit another player, **Then** the TNT transfers to the hit player and the original holder becomes safe
3. **Given** the countdown reaches zero, **When** time expires, **Then** all current TNT holders explode and are eliminated while non-holders proceed to the next round
4. **Given** a player is holding TNT, **When** they move, **Then** they move 20% faster than non-TNT holders (Speed II vs Speed I)
5. **Given** a TNT transfer occurs, **When** the new holder receives TNT, **Then** their movement speed increases and the previous holder's speed decreases

---

### User Story 2 - Multi-Round Progression (Priority: P1)

Players progress through 6 rounds with increasing difficulty, where more TNT holders are selected and explosion times vary, with the goal of surviving all rounds.

**Why this priority**: The progression system creates escalating tension and challenge. A single-round game would lack depth and replayability. This is essential for the full game experience.

**Independent Test**: Can be tested by completing all 6 rounds and verifying that TNT holder counts, explosion times, and special effects (glowing) match the round specifications. Delivers complete game arc from start to finish.

**Acceptance Scenarios**:

1. **Given** round 1 ends with survivors, **When** round 2 starts, **Then** 25% of remaining players become TNT holders with a 30-second countdown
2. **Given** round 4 starts, **When** TNT holders are selected, **Then** 50% of remaining players receive TNT with a 25-second countdown
3. **Given** round 5 starts, **When** the round begins, **Then** all players (including non-TNT holders) receive the glowing effect
4. **Given** all 6 rounds complete, **When** survivors remain, **Then** the game ends and displays final results with rankings
5. **Given** all players are eliminated before round 6, **When** no survivors remain, **Then** the game ends immediately

---

### User Story 3 - Player Feedback and Awareness with HUD (Priority: P2)

Players receive clear visual, audio, and text feedback through a comprehensive HUD system that displays their status, game phase, remaining time, and other critical information throughout the entire game flow from lobby to game end.

**Why this priority**: While core gameplay works without extensive feedback, players need clear information to understand and enjoy the game. A well-designed HUD significantly enhances user experience and reduces confusion about game state. This isn't required for basic functionality but is critical for player engagement.

**Independent Test**: Can be tested by joining a game, observing HUD elements (scoreboard, action bar, titles, boss bar) through all phases (waiting, countdown, in-game rounds, elimination, victory), listening for sound effects, and watching visual effects. Delivers comprehensive player awareness and engagement.

**Acceptance Scenarios**:

**Lobby/Waiting Phase:**
1. **Given** a player joins an arena, **When** they enter the waiting lobby, **Then** they see a scoreboard showing "=== TNT TAG ===", "状態: 待機中", "プレイヤー: X/20-25人", and "最低20人で開始"
2. **Given** a player is in the waiting lobby, **When** they look at the action bar, **Then** they see "ゲーム開始を待っています... (X/20人)"

**Pre-Game Countdown Phase:**
3. **Given** 20 players have joined, **When** the 10-second countdown starts, **Then** the scoreboard updates to "状態: まもなく開始" and the action bar shows "ゲーム開始まで: X秒"
4. **Given** the countdown is active, **When** each second passes, **Then** a countdown sound plays (tick at 10-4, special sound at 3-1) and the action bar updates in real-time
5. **Given** the countdown reaches 0, **When** the game starts, **Then** all players see title "ゲーム開始!" / "全6ラウンド - 生き残れ!", hear triumphant sound, and green particle bursts appear around them with brief Regeneration effect

**Round Start Phase:**
6. **Given** a round begins, **When** players are teleported to center, **Then** they see a title "ROUND X" / "TNTから逃げろ!", hear dramatic sound (ender dragon growl), see lightning visual effect at center, and ascending particle pillars for 2 seconds
7. **Given** round 1 starts, **When** the TNT holder is selected, **Then** the scoreboard updates to show "ラウンド: 1/6", "残り時間: 0:40", "生存者: 20人", "TNT保持者: 1人"

**During Gameplay:**
8. **Given** a player receives TNT, **When** the transfer occurs, **Then** they see a title message "TNTを受け取った!" and hear a collision sound, and a boss bar appears showing "⚠ TNTを持っています! ⚠" with red color
9. **Given** a player is holding TNT, **When** they look at the action bar, **Then** they see "⚠ 他のプレイヤーにタッチして渡せ! ⚠"
10. **Given** a player is NOT holding TNT, **When** a round is active, **Then** their action bar shows "TNTから逃げろ! | 残り時間: X秒 | 生存者: X人"
11. **Given** a round is active, **When** a player checks the scoreboard, **Then** they see current round number, remaining time, survivor count, and TNT holder count updated every second
12. **Given** 10 seconds remain in the countdown, **When** time decreases, **Then** TNT blocks blink faster, warning sounds play, and the boss bar (if holding TNT) changes to flashing animation
13. **Given** a player passes TNT to another, **When** the transfer succeeds, **Then** their boss bar disappears and action bar returns to normal state

**Elimination Phase:**
14. **Given** a player explodes, **When** elimination occurs, **Then** they see "💥 BOOM! 💥" / "あなたは爆発しました", large explosion particles spawn at their location, smoke ring expands outward, they briefly get blindness effect (1 second), then transition to spectator mode with scoreboard showing "状態: 観戦中" and "脱落ラウンド: X"

**Spectator Phase:**
15. **Given** a player is eliminated, **When** they are in spectator mode, **Then** their action bar shows "観戦中 | 現在のラウンド: X/6 | 残り生存者: X人" and they can see other players' perspectives

**Victory Phase:**
16. **Given** a player survives all rounds, **When** the game ends, **Then** they see "🏆 VICTORY! 🏆" / "最後の生存者!", hear victory sound, fireworks launch at their location every 0.5 seconds for 5 seconds (10 total), golden particles continuously spawn around them, they receive Glowing effect with gold color for 10 seconds, and are teleported to an elevated podium (if available)
17. **Given** all players are eliminated before round 6, **When** the game ends, **Then** all spectators see title "全員脱落" / "次のゲームに期待!", hear somber sound, and smoke particle clouds spawn at center for 3 seconds

---

### User Story 4 - Arena Management (Priority: P2)

Administrators can create, configure, and manage multiple arena spaces where games take place, with proper spawn points and boundaries.

**Why this priority**: While a single hardcoded arena could work for MVP, the ability to manage multiple arenas is important for scalability and server variety. It's not critical for initial testing but needed for production use.

**Independent Test**: Can be tested by creating an arena, setting spawn points, configuring boundaries, and starting a game in that arena. Delivers multi-arena capability.

**Acceptance Scenarios**:

1. **Given** an administrator (OP) is at a location, **When** they execute `/tnttag setpos1`, **Then** the first corner position is saved for that administrator
2. **Given** a non-OP player tries to execute `/tnttag setpos1`, **When** the command is entered, **Then** the system denies the command with a permission error message
3. **Given** an administrator has set pos1, **When** they move to another location and execute `/tnttag setpos2`, **Then** the second corner position is saved
4. **Given** both pos1 and pos2 are set, **When** the administrator executes `/tnttag creategame <name>`, **Then** a new arena is created with those boundaries and a calculated center spawn point
5. **Given** pos2 is not set, **When** an administrator tries to create an arena, **Then** the system rejects the command with an error message
6. **Given** multiple arenas exist, **When** a player executes `/tnttag list`, **Then** they see a list of all arenas showing name, status (waiting/in-game/available), current player count, and current round number if in-game
7. **Given** a game starts in an arena, **When** a round begins, **Then** all players teleport to the arena's automatically calculated center spawn point
8. **Given** an arena has boundaries set, **When** a player tries to leave the defined area, **Then** they are prevented from crossing the boundary

---

### User Story 5 - Game Results and Statistics (Priority: P3)

Players view detailed end-game results showing rankings, survival statistics, and performance metrics after each game concludes.

**Why this priority**: This adds polish and competitive elements but isn't essential for core gameplay. Players can enjoy the game without detailed statistics, making this a nice-to-have enhancement.

**Independent Test**: Can be tested by completing a game and verifying the results display shows winner, rankings by survival round, and participation statistics. Delivers competitive engagement and player progression tracking.

**Acceptance Scenarios**:

1. **Given** a game ends, **When** results are displayed, **Then** players see rankings from 1st place (survived all rounds) to last place (eliminated first)
2. **Given** results are shown, **When** a player views their stats, **Then** they see which round they survived to and their final placement
3. **Given** multiple players survive to the same round, **When** rankings are calculated, **Then** players eliminated in the same round receive the same placement tier
4. **Given** a player completes a game, **When** they use the stats command, **Then** they see their historical performance across all games played

---

### Edge Cases

- What happens when only TNT holders remain (all non-holders eliminated)?
  - Game should end immediately with all remaining players as losers, or TNT holders become winners by default
- What happens when a player disconnects while holding TNT?
  - TNT should transfer to a random nearby player, or if no nearby players exist, select a new random TNT holder
- What happens when exactly 2 players remain and both are TNT holders?
  - Both explode simultaneously, game ends with no winner
- What happens when tag occurs at the exact moment countdown reaches zero?
  - Original TNT holder should explode (tag not processed if simultaneous with explosion)
- What happens when a player tries to tag while on cooldown?
  - Tag is ignored, TNT remains with current holder, no feedback to prevent spam
- What happens when a player joins mid-game?
  - Player is placed in spectator mode and can join the next game
- What happens when a player leaves mid-game?
  - If they held TNT, it transfers to random nearby player; they are marked as eliminated
- What happens when game starts with fewer than minimum players (20)?
  - Game does not start; countdown resets and waits for more players
- What happens in round 2-6 when calculating TNT holder percentages results in fractional players?
  - Round up to nearest integer (e.g., 25% of 15 players = 3.75 → 4 TNT holders)
- What happens when an administrator sets pos1 but disconnects before setting pos2?
  - The pos1 setting is cleared when the administrator disconnects; they must start over
- What happens when an administrator tries to create an arena with a name that already exists?
  - System rejects the command and displays an error message indicating the name is already in use
- What happens when pos1 and pos2 are set in different worlds?
  - System rejects the arena creation and displays an error requiring both positions to be in the same world
- What happens when pos1 and pos2 are the same location?
  - System rejects the arena creation and requires a minimum distance between positions (e.g., at least 10 blocks apart)
- What happens when a non-OP player tries to execute an admin command?
  - System denies the command immediately and displays a clear permission error message (e.g., "このコマンドを実行する権限がありません")
- What happens when an OP player loses OP status while setting up an arena (after setpos1 but before creategame)?
  - The stored pos1/pos2 positions remain saved, but the creategame command will be denied until OP status is restored
- What happens when `/tnttag list` is executed but no arenas have been created yet?
  - System displays a message indicating no arenas are available (e.g., "利用可能なアリーナがありません")
- What happens when multiple players survive to the end simultaneously?
  - All survivors receive victory effects (title, fireworks, particles, glowing) at the same time, and all are teleported to podium area together (if available)
- What happens when a player disconnects during the victory celebration (while fireworks are launching)?
  - Fireworks continue for the remaining duration even if the winner disconnects; other players still see the effects
- What happens when the arena has no designated podium area for winners?
  - Winners remain at their current location and receive all other victory effects (fireworks, particles, glowing) without teleportation
- What happens when too many particle effects occur simultaneously (multiple explosions in same round)?
  - System prioritizes particle rendering for nearby players (within 20 blocks) and may reduce particle density for distant effects to maintain performance
- What happens when a player has particles disabled in their client settings?
  - Audio cues and title messages still display correctly; visual particle effects simply don't render on that client

## Requirements *(mandatory)*

### Functional Requirements

#### Core Game Mechanics

- **FR-001**: System MUST support 20-25 simultaneous players in a single game instance
- **FR-002**: System MUST conduct exactly 6 rounds per game session
- **FR-003**: System MUST randomly select TNT holders at the start of each round according to round-specific rules (round 1: 1 holder, rounds 2-3: 25%, round 4: 50%, rounds 5-6: 33%)
- **FR-004**: System MUST allow TNT holders to transfer TNT by hitting (left-clicking) non-TNT holders
- **FR-005**: System MUST apply a 0.5-second cooldown after each TNT transfer to prevent rapid consecutive tags
- **FR-006**: System MUST grant Speed II effect to TNT holders and Speed I effect to non-TNT holders
- **FR-007**: System MUST explode all TNT holders when the round countdown reaches zero
- **FR-008**: System MUST eliminate exploded players from further rounds (move to spectator mode)
- **FR-009**: System MUST prevent TNT explosions from damaging blocks or non-TNT-holding players
- **FR-010**: System MUST disable player-vs-player damage except for TNT transfer mechanics

#### Round Configuration

- **FR-011**: System MUST enforce round 1 with 1 TNT holder and 40-second countdown
- **FR-012**: System MUST enforce rounds 2-3 with 25% of remaining players as TNT holders and 30-second countdown
- **FR-013**: System MUST enforce round 4 with 50% of remaining players as TNT holders and 25-second countdown
- **FR-014**: System MUST enforce rounds 5-6 with 33% of remaining players as TNT holders, 40/50-second countdowns respectively, and glowing effect on all players
- **FR-015**: System MUST apply glowing effect to all players (not just TNT holders) during rounds 5 and 6

#### Visual and Audio Feedback

**TNT Holder Effects:**
- **FR-016**: System MUST display TNT block on TNT holder's head
- **FR-017**: System MUST display red glowing effect on TNT holders (in addition to round-specific glowing)
- **FR-018**: System MUST display particle effects (smoke, fire sparks) around TNT holders
- **FR-019**: System MUST increase TNT block blink rate as countdown approaches zero
- **FR-020**: System MUST play fuse sound loop while players hold TNT
- **FR-021**: System MUST play collision sound when TNT transfers between players
- **FR-022**: System MUST play warning sound when 5 seconds remain in countdown
- **FR-023**: System MUST play explosion sound when TNT holders explode

**Game Start Effects:**
- **FR-024**: System MUST play countdown sounds during the 10-second pre-game countdown (tick sound at 10, 9, 8... and special sound at 3, 2, 1)
- **FR-025**: System MUST display title "ゲーム開始!" with subtitle "全6ラウンド - 生き残れ!" when game transitions from countdown to round 1
- **FR-026**: System MUST play triumphant start sound (note block pling or level up sound) when game begins
- **FR-027**: System MUST spawn green particle burst effects (VILLAGER_HAPPY particles) around all players when game starts
- **FR-028**: System MUST give all players a brief Regeneration I effect (3 seconds) at game start

**Round Start Effects:**
- **FR-029**: System MUST play dramatic sound (ender dragon growl or wither spawn) at the start of each round
- **FR-030**: System MUST spawn lightning visual effects (without damage) at the center spawn point when round starts
- **FR-031**: System MUST display ascending particle pillars (FLAME or END_ROD) at spawn location for 2 seconds

**Game End Effects - Victory (Survivors):**
- **FR-032**: System MUST launch fireworks at winner's location every 0.5 seconds for 5 seconds (10 fireworks total)
- **FR-033**: System MUST use randomized firework colors (red, gold, yellow, lime) with burst and star effects
- **FR-034**: System MUST play victory sound (UI_TOAST_CHALLENGE_COMPLETE) when displaying victory title
- **FR-035**: System MUST spawn golden particle effects (TOTEM or VILLAGER_HAPPY in yellow) continuously around winners for 5 seconds
- **FR-036**: System MUST give winners Glowing effect with gold color for 10 seconds after game ends
- **FR-037**: System MUST teleport winners to a podium area (elevated by 3 blocks) if available in the arena

**Game End Effects - Defeat (All Eliminated):**
- **FR-038**: System MUST display title "全員脱落" with subtitle "次のゲームに期待!" when all players are eliminated
- **FR-039**: System MUST play somber sound (ENTITY_WITHER_DEATH or BLOCK_ANVIL_LAND) for game over
- **FR-040**: System MUST spawn smoke particle clouds (SMOKE_LARGE) at the center for 3 seconds

**Elimination Effects:**
- **FR-041**: System MUST spawn large explosion particle effect (EXPLOSION_LARGE) at eliminated player's location
- **FR-042**: System MUST create a smoke ring particle effect expanding outward from explosion center
- **FR-043**: System MUST play explosion sound audible to all players in the arena
- **FR-044**: System MUST briefly apply blindness effect (1 second) to the eliminated player for dramatic impact

#### User Interface and HUD System

**Scoreboard (Sidebar):**
- **FR-024**: System MUST display a persistent scoreboard on the right side showing game title "=== TNT TAG ==="
- **FR-025**: System MUST update scoreboard to show game state in waiting phase: "状態: 待機中", "プレイヤー: X/20-25人", "最低20人で開始"
- **FR-026**: System MUST update scoreboard to show game state in countdown phase: "状態: まもなく開始", "開始まで: X秒"
- **FR-027**: System MUST update scoreboard during active rounds to show: "ラウンド: X/6", "残り時間: 0:XX", "生存者: X人", "TNT保持者: X人"
- **FR-028**: System MUST update scoreboard for eliminated players to show: "状態: 観戦中", "脱落ラウンド: X", plus current round information
- **FR-029**: System MUST refresh scoreboard information at least once per second

**Action Bar (Above Hotbar):**
- **FR-030**: System MUST display action bar in waiting lobby: "ゲーム開始を待っています... (X/20人)"
- **FR-031**: System MUST display action bar during countdown: "ゲーム開始まで: X秒"
- **FR-032**: System MUST display action bar for non-TNT holders during rounds: "TNTから逃げろ! | 残り時間: X秒 | 生存者: X人"
- **FR-033**: System MUST display action bar for TNT holders: "⚠ 他のプレイヤーにタッチして渡せ! ⚠"
- **FR-034**: System MUST display action bar for spectators: "観戦中 | 現在のラウンド: X/6 | 残り生存者: X人"
- **FR-035**: System MUST update action bar in real-time (at least 2 times per second)

**Boss Bar (Top of Screen):**
- **FR-036**: System MUST display a red boss bar for TNT holders showing "⚠ TNTを持っています! ⚠"
- **FR-037**: System MUST update boss bar progress to reflect remaining time in the round (100% at start, 0% at explosion)
- **FR-038**: System MUST change boss bar to flashing animation when 10 seconds or less remain
- **FR-039**: System MUST remove boss bar immediately when TNT is transferred to another player
- **FR-040**: System MUST hide boss bar for non-TNT holders

**Title Messages (Center Screen):**
- **FR-041**: System MUST display title "ROUND X" with subtitle "TNTから逃げろ!" at the start of each round
- **FR-042**: System MUST display title "TNTを受け取った!" with subtitle "他のプレイヤーにタッチ!" when a player receives TNT
- **FR-043**: System MUST display title "💥 BOOM! 💥" with subtitle "あなたは爆発しました" when a player explodes
- **FR-044**: System MUST display title "🏆 VICTORY! 🏆" with subtitle "最後の生存者!" to winners
- **FR-045**: System MUST display title messages for 3 seconds with fade-in and fade-out animations

**End-Game Results Screen:**
- **FR-046**: System MUST display end-game results showing rankings, survival statistics, and participation breakdown
- **FR-047**: System MUST show winner(s) at the top with special highlighting (gold color, bold text)
- **FR-048**: System MUST group players by elimination round with survival statistics

#### Arena Management

- **FR-049**: System MUST support multiple named arenas with independent configurations
- **FR-050**: System MUST teleport all players to the central spawn point at the start of each round
- **FR-051**: System MUST enforce arena boundaries using world border or similar mechanism
- **FR-052**: System MUST prevent players from breaking or placing blocks in arenas
- **FR-053**: Arenas MUST be created and configured by administrators using pre-built maps

#### Game Flow

- **FR-054**: System MUST wait for minimum 20 players in lobby before starting game countdown
- **FR-055**: System MUST start 10-second countdown once minimum players are present
- **FR-056**: System MUST begin round 1 after countdown completes
- **FR-057**: System MUST wait 3 seconds between round end and next round start
- **FR-058**: System MUST end the game when 6 rounds complete or when fewer than 1 player remains
- **FR-059**: System MUST declare the last surviving player (or players) as winner(s)
- **FR-060**: System MUST move disconnected or eliminated players to spectator mode
- **FR-061**: System MUST prevent mid-game joins (players join next game instead)

#### Commands and Permissions

**Player Commands:**
- **FR-062**: System MUST provide `/tnttag join [arena]` command for players to join games
- **FR-063**: System MUST provide `/tnttag leave` command for players to exit games
- **FR-064**: System MUST provide `/tnttag stats [player]` command to view statistics
- **FR-065**: System MUST provide `/tnttag list` command to show all created arenas with their status (waiting/in-game/available), player count, and current round if in-game

**Admin Commands for Arena Setup (OP Required):**
- **FR-066**: System MUST provide `/tnttag setpos1` command to set the first corner position of the arena boundary
- **FR-067**: System MUST provide `/tnttag setpos2` command to set the second corner position of the arena boundary
- **FR-068**: System MUST provide `/tnttag creategame <arena_name>` command to create a new game arena using the two positions set by setpos1 and setpos2
- **FR-069**: System MUST calculate the center spawn point automatically based on the two corner positions when creating an arena
- **FR-070**: System MUST validate that both pos1 and pos2 are set before allowing arena creation
- **FR-071**: System MUST store pos1 and pos2 per administrator (multiple admins can set positions independently)

**Admin Commands for Game Control (OP Required):**
- **FR-072**: System MUST provide `/tnttag delete <arena>` command to remove an arena
- **FR-073**: System MUST provide `/tnttag start <arena>` command to force-start a game
- **FR-074**: System MUST provide `/tnttag stop <arena>` command to force-stop a running game
- **FR-075**: System MUST provide `/tnttag reload` command to reload configuration files

**Permissions:**
- **FR-076**: System MUST require OP (operator) permission for all admin commands (setpos1, setpos2, creategame, delete, start, stop, reload)
- **FR-077**: System MUST deny admin command execution with an error message if the user does not have OP permission
- **FR-078**: System MUST allow all players to execute player commands (join, leave, stats, list) without OP permission

### Key Entities

- **Player**: An individual participating in the game, with status (alive/eliminated/spectator), current TNT holder flag, movement speed, survival statistics, and associated HUD state
- **Game Instance**: A single game session containing 6 rounds, a list of participating players, current round number, game state (waiting/starting/in-game/ending), and arena reference
- **Round**: A phase of the game with a specific round number (1-6), TNT holder count/ratio, duration, glowing effect flag, countdown timer, and list of TNT holders
- **Arena**: A physical game space with a name, world reference, two corner positions (pos1, pos2) defining the rectangular boundary, and an automatically calculated center spawn point
- **Arena Setup Session**: Temporary storage for an administrator's pos1 and pos2 coordinates during arena creation, cleared after successful arena creation
- **TNT Holder State**: A player's TNT possession status including fuse sound loop, particle effects, visual TNT block, glowing effect, movement speed buff, and active boss bar
- **HUD State**: A player's current display configuration including scoreboard content (game phase, round info, player counts), action bar message (phase-specific text), boss bar visibility and progress (for TNT holders only), and last update timestamp
- **Game Statistics**: Historical player data including games played, total rounds survived, wins, TNT tags given/received, and average survival time

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: 20-25 players can join and complete a full 6-round game without server lag or crashes
- **SC-002**: TNT transfers between players occur within 0.1 seconds of hit detection
- **SC-003**: Round countdowns are accurate to within 0.5 seconds of specified duration
- **SC-004**: 95% of TNT transfer attempts succeed when player is within 3-block range and not on cooldown
- **SC-005**: All visual effects (TNT block, particles, glowing) render correctly for all players within view distance
- **SC-006**: All audio cues (fuse, collision, warning, explosion) play at appropriate times for all players
- **SC-007**: Scoreboard and action bar update at least every second with current game state
- **SC-008**: Game results display within 2 seconds of game end showing accurate rankings and statistics
- **SC-009**: Players can complete join-to-game-end flow in under 10 minutes including lobby wait time
- **SC-010**: Zero players experience TNT-related damage from other players' explosions (isolation verified)
- **SC-011**: Arena boundaries prevent 100% of attempts to leave designated play area
- **SC-012**: Disconnected players are removed from active game within 5 seconds and TNT is reassigned if applicable
- **SC-013**: Arena list command displays all arenas with accurate real-time status and player counts within 0.5 seconds
- **SC-014**: Non-OP players are denied all admin commands 100% of the time with clear permission error messages
- **SC-015**: OP administrators can successfully create arenas with setpos1, setpos2, and creategame workflow in under 30 seconds
- **SC-016**: Scoreboard displays correct game phase information (waiting/countdown/in-game/spectating) 100% of the time
- **SC-017**: Action bar updates at least twice per second with accurate real-time information
- **SC-018**: Boss bar for TNT holders appears within 0.2 seconds of receiving TNT and disappears within 0.2 seconds of passing TNT
- **SC-019**: Boss bar progress accurately reflects remaining round time with smooth visual updates (at least 10 updates per second)
- **SC-020**: All title messages display for exactly 3 seconds with proper fade animations
- **SC-021**: Players can distinguish between all game phases (waiting/countdown/playing/eliminated/victory) through HUD elements alone without external communication
- **SC-022**: Spectator HUD displays accurate live game state information updating at least once per second
- **SC-023**: Game start effects (title, sound, particles, Regeneration) trigger simultaneously within 0.3 seconds for all players
- **SC-024**: Round start effects (lightning, particles, sound) are visible and audible to all players in the arena
- **SC-025**: Victory fireworks launch exactly every 0.5 seconds for 5 seconds (10 fireworks total) with varied colors
- **SC-026**: Elimination explosion effects (particles, sound, smoke ring) are visible to all nearby players within 30 blocks
- **SC-027**: All visual effects (particles, fireworks, lightning) render smoothly without causing client-side lag for players with standard settings
- **SC-028**: Audio cues for game start, round start, victory, and defeat are clearly distinguishable from each other
- **SC-029**: Winners are teleported to podium within 1 second of victory declaration (if podium exists in arena)

## Assumptions

1. **Minecraft Version**: Assumes Spigot/Paper server running Minecraft 1.16 or later with support for modern entity effects and world border mechanics
2. **Resource Pack**: No custom resource pack required; uses vanilla Minecraft blocks, sounds, and particle effects
3. **Arena Creation**: Administrators will manually build arenas using Minecraft creative mode before configuring them in the plugin
4. **Network Stability**: Assumes players have stable connections; excessive lag may affect tag accuracy but is outside system control
5. **Server Performance**: Assumes server hardware can handle 25 concurrent players with frequent entity updates and particle effects
6. **Language**: Default messages are in Japanese (ja_JP) as shown in CLAUDE.md, with support for localization files
7. **Rewards System**: Optional rewards (coins, points) are mentioned in config but not required for core gameplay functionality
8. **Database**: Defaults to YAML file storage for statistics; MySQL/SQLite support is optional enhancement
9. **World Management**: Assumes dedicated world(s) for TNT TAG arenas, separate from main survival/creative worlds
10. **PVP Settings**: Assumes server can disable PVP damage while allowing entity interaction (hitting) for tag mechanics
