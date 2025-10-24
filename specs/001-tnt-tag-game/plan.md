# Implementation Plan: TNT TAG - Minecraft Survival Tag Minigame

**Branch**: `001-tnt-tag-game` | **Date**: 2025-10-24 | **Spec**: [spec.md](./spec.md)
**Input**: Feature specification from `/specs/001-tnt-tag-game/spec.md`

**Note**: This template is filled in by the `/speckit.plan` command. See `.specify/templates/commands/plan.md` for the execution workflow.

## Summary

TNT TAG is a Minecraft multiplayer minigame plugin implementing a 6-round survival tag system for 20-25 players. Players must avoid holding TNT when countdown timers expire, with eliminated players transitioning to spectator mode. The system includes comprehensive HUD (scoreboard, action bar, boss bar, titles), visual/audio effects for all game phases, arena management with boundary enforcement, and statistical tracking.

**Technical Approach**: Spigot/Paper plugin (Java) with event-driven architecture, scheduled task system for timers/HUD updates, configuration-based arena management, and file-based persistent storage.

## Technical Context

**Language/Version**: Java 17 (for Minecraft 1.20.x compatibility)
**Primary Dependencies**:
- Spigot API 1.20.x / Paper API 1.20.x (server framework)
- Bukkit Event System (event handling)
- BukkitScheduler (async tasks, timers)

**Storage**: YAML files (config.yml, arenas.yml, messages_ja_JP.yml, player stats)
**Testing**: JUnit 5 + Mockito (unit tests), MockBukkit (Bukkit API mocking)
**Target Platform**: Spigot/Paper server 1.20.x on Linux/Windows dedicated servers
**Project Type**: Single Minecraft server plugin (JAR artifact)
**Performance Goals**:
- Support 25 concurrent players with <50ms tick time
- HUD updates: Scoreboard (1/sec), Action Bar (2/sec), Boss Bar (10/sec)
- TNT transfer latency <100ms
- Particle effects within 20-block radius

**Constraints**:
- Must not modify Minecraft core mechanics
- No custom client mods required
- Vanilla resource pack compatible
- OP permission system for admin commands
- Arena boundary enforcement without custom world generation

**Scale/Scope**:
- Up to 10 concurrent game instances per server
- Multiple arenas (5-10 typical)
- 78 functional requirements
- 6 distinct game phases with unique HUD states
- 29 measurable success criteria

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

**Status**: ✅ PASS (No constitution violations - template constitution in use)

Since the project constitution is currently a template with placeholder principles, no specific gates are enforced. The standard Minecraft plugin architecture follows common best practices:

- ✅ **Single-purpose plugin**: Dedicated to TNT TAG minigame functionality
- ✅ **Event-driven architecture**: Uses Bukkit event system (standard pattern)
- ✅ **Configuration-based**: YAML for arenas, messages, settings
- ✅ **No external service dependencies**: Self-contained plugin
- ✅ **Standard testing approach**: JUnit + Mockito for Java plugins

**Re-evaluation after Phase 1**: Will verify data model and contracts align with any future constitution updates.

## Project Structure

### Documentation (this feature)

```text
specs/001-tnt-tag-game/
├── spec.md              # Feature specification (completed)
├── plan.md              # This file (/speckit.plan command output)
├── research.md          # Phase 0 output - Technology decisions
├── data-model.md        # Phase 1 output - Entity models
├── quickstart.md        # Phase 1 output - Developer setup guide
├── contracts/           # Phase 1 output - Event contracts
│   ├── events.md        # Custom event specifications
│   └── commands.md      # Command interface specifications
├── checklists/
│   └── requirements.md  # Quality validation checklist (completed)
└── tasks.md             # Phase 2 output (/speckit.tasks command - NOT created yet)
```

### Source Code (repository root)

```text
src/main/java/com/example/tnttag/
├── TNTTagPlugin.java           # Main plugin class (JavaPlugin)
├── commands/                   # Command executors
│   ├── TNTTagCommand.java      # Main command dispatcher
│   ├── player/                 # Player commands (join, leave, stats, list)
│   └── admin/                  # Admin commands (setpos1/2, creategame, delete, etc.)
├── game/                       # Core game logic
│   ├── GameManager.java        # Manages all game instances
│   ├── GameInstance.java       # Single game session
│   ├── Round.java              # Round logic and state
│   ├── RoundConfig.java        # Round-specific configurations
│   └── GameState.java          # Game state enum
├── player/                     # Player management
│   ├── PlayerManager.java      # Track player states
│   ├── PlayerGameData.java     # Per-player game data
│   ├── TNTHolderState.java     # TNT possession state
│   └── HUDState.java           # HUD display state
├── arena/                      # Arena management
│   ├── ArenaManager.java       # Arena CRUD operations
│   ├── Arena.java              # Arena entity
│   ├── ArenaSetupSession.java  # Temp pos1/pos2 storage
│   └── BoundaryEnforcer.java   # Boundary violation handler
├── hud/                        # HUD rendering
│   ├── HUDRenderer.java        # Main HUD coordinator
│   ├── ScoreboardManager.java  # Sidebar scoreboard
│   ├── ActionBarManager.java   # Action bar updates
│   ├── BossBarManager.java     # Boss bar for TNT holders
│   └── TitleManager.java       # Title/subtitle messages
├── effects/                    # Visual & audio effects
│   ├── EffectManager.java      # Effect coordinator
│   ├── ParticleEffects.java    # Particle spawning
│   ├── SoundEffects.java       # Sound playback
│   └── FireworkEffects.java    # Victory fireworks
├── events/                     # Custom events
│   ├── TNTTagStartEvent.java
│   ├── TNTTagRoundStartEvent.java
│   ├── TNTTagEvent.java        # TNT transfer event
│   ├── TNTExplosionEvent.java
│   └── TNTTagEndEvent.java
├── listeners/                  # Bukkit event listeners
│   ├── PlayerListener.java     # Join/quit/disconnect
│   ├── GameListener.java       # Game-specific events
│   ├── TNTTransferListener.java # Tag mechanics
│   └── DamageListener.java     # PVP damage prevention
├── stats/                      # Statistics tracking
│   ├── StatsManager.java       # Stats CRUD
│   └── GameStatistics.java     # Player stats entity
├── config/                     # Configuration
│   ├── ConfigManager.java      # Config loader
│   └── MessageManager.java     # i18n message loader
└── util/                       # Utilities
    ├── LocationUtil.java       # Position calculations
    ├── RandomUtil.java         # Random selection
    └── TimeUtil.java           # Time formatting

src/main/resources/
├── plugin.yml                  # Plugin metadata
├── config.yml                  # Main configuration
├── arenas.yml                  # Arena definitions
└── messages_ja_JP.yml          # Japanese messages

tests/java/com/example/tnttag/
├── unit/                       # Unit tests
│   ├── game/
│   ├── arena/
│   ├── player/
│   └── util/
└── integration/                # Integration tests
    ├── GameFlowTest.java       # Full game flow
    ├── ArenaSetupTest.java     # Arena creation
    └── CommandTest.java        # Command execution
```

**Structure Decision**: Single project structure (Option 1) selected because:
- Minecraft plugins are self-contained JAR artifacts
- All logic runs server-side (no frontend/backend split)
- Standard Maven/Gradle plugin layout
- Bukkit event system provides natural separation of concerns

## Complexity Tracking

> **No violations identified** - Standard Minecraft plugin architecture with established patterns.

| Violation | Why Needed | Simpler Alternative Rejected Because |
|-----------|------------|-------------------------------------|
| N/A | N/A | N/A |

