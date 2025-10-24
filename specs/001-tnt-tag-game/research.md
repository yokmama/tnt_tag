# Phase 0: Research & Technology Decisions

**Feature**: TNT TAG - Minecraft Survival Tag Minigame
**Date**: 2025-10-24
**Status**: Completed

## Overview

This document captures technology decisions, best practices research, and rationale for the TNT TAG plugin implementation. All technical choices are based on Minecraft plugin development standards and performance requirements specified in the feature spec.

## Technology Stack Decisions

### Decision 1: Spigot vs Paper API

**Decision**: Use **Paper API 1.20.x** as primary, with Spigot API 1.20.x compatibility fallback

**Rationale**:
- Paper is a performance-optimized fork of Spigot with additional APIs
- Better scheduler and async chunk loading APIs
- More efficient particle and entity tracking
- Backwards compatible with Spigot - plugin works on both
- Performance requirement: 25 concurrent players with <50ms tick time (Paper's optimizations help)

**Alternatives Considered**:
- **Spigot-only**: Rejected because Paper's async APIs are valuable for HUD updates (2-10 times/sec) and particle effects
- **Fabric/Forge**: Rejected because they require client-side mods (violates requirement for vanilla compatibility)
- **Vanilla server**: Rejected because lacks plugin API entirely

**References**:
- Paper Performance Improvements: https://docs.papermc.io/paper/dev/getting-started
- Spigot vs Paper comparison: https://www.spigotmc.org/wiki/spigot-vs-paper/

---

### Decision 2: Java Version

**Decision**: **Java 17** (LTS)

**Rationale**:
- Minecraft 1.20.x requires Java 17 minimum
- LTS (Long Term Support) version with stability guarantees
- Modern language features (records, pattern matching, text blocks) improve code quality
- All major server hosts support Java 17+

**Alternatives Considered**:
- **Java 8**: Rejected - no longer supported by Minecraft 1.20+
- **Java 21** (latest LTS): Rejected - not yet widely adopted by hosting providers (premature for production)

**References**:
- Minecraft Java requirements: https://help.minecraft.net/hc/en-us/articles/4409159214989

---

### Decision 3: Scheduler Strategy for HUD Updates

**Decision**: **BukkitScheduler** with separate async repeating tasks for each HUD component

**Rationale**:
- Different update frequencies required (SC-017, SC-019, SC-029):
  - Scoreboard: 1/sec (20 ticks)
  - Action Bar: 2/sec (10 ticks)
  - Boss Bar: 10/sec (2 ticks)
- Async tasks prevent main thread blocking (performance requirement: <50ms tick time)
- Paper's `Folia` scheduler not yet production-ready (experimental)

**Implementation Pattern**:
```java
// Scoreboard: Every 20 ticks (1 second)
Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, () -> {
    scoreboardManager.updateAll();
}, 0L, 20L);

// Action Bar: Every 10 ticks (0.5 seconds = 2/sec)
Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, () -> {
    actionBarManager.updateAll();
}, 0L, 10L);

// Boss Bar: Every 2 ticks (0.1 seconds = 10/sec)
Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, () -> {
    bossBarManager.updateProgress();
}, 0L, 2L);
```

**Alternatives Considered**:
- **Single unified timer**: Rejected - forces all HUD to update at highest frequency (wasteful)
- **Paper async scheduler**: Rejected - requires Paper-specific code, breaks Spigot compatibility
- **Manual thread management**: Rejected - BukkitScheduler handles lifecycle and thread safety

**References**:
- Bukkit Scheduler docs: https://hub.spigotmc.org/javadocs/spigot/org/bukkit/scheduler/BukkitScheduler.html
- Async task best practices: https://www.spigotmc.org/wiki/scheduler-programming/

---

### Decision 4: TNT Transfer Detection

**Decision**: **EntityDamageByEntityEvent** with damage cancellation

**Rationale**:
- Detects player-to-player hits (left-click attack)
- Provides attacker and victim entities
- Can cancel damage while still detecting hit (PVP disabled per FR-010)
- Standard Bukkit event, works on all server versions

**Implementation Pattern**:
```java
@EventHandler
public void onEntityDamage(EntityDamageByEntityEvent event) {
    if (event.getDamager() instanceof Player attacker &&
        event.getEntity() instanceof Player victim) {

        event.setCancelled(true); // No damage (FR-010)

        if (isTNTHolder(attacker) && !isTNTHolder(victim) &&
            !isOnCooldown(attacker)) {
            transferTNT(attacker, victim);
            applyCooldown(attacker, 500); // 0.5s per FR-005
        }
    }
}
```

**Alternatives Considered**:
- **PlayerInteractEntityEvent**: Rejected - only detects right-click (not attack)
- **Custom packet listeners**: Rejected - version-dependent, fragile
- **Proximity detection**: Rejected - doesn't distinguish intent to tag

**References**:
- EntityDamageByEntityEvent: https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/event/entity/EntityDamageByEntityEvent.html

---

### Decision 5: Arena Boundary Enforcement

**Decision**: **WorldBorder API** per arena

**Rationale**:
- Native Minecraft feature (vanilla compatible)
- Handles boundary violations automatically
- Visual indicator for players (red screen effect near border)
- Per-world support allows multiple arenas

**Implementation Pattern**:
```java
public void setupArenaBoundary(Arena arena) {
    World world = arena.getWorld();
    WorldBorder border = world.getWorldBorder();

    Location center = arena.getCenterSpawn();
    double radius = arena.getRadius();

    border.setCenter(center);
    border.setSize(radius * 2); // Diameter
    border.setWarningDistance(5); // Red screen 5 blocks from edge
}
```

**Alternatives Considered**:
- **Manual PlayerMoveEvent check**: Rejected - high performance cost at 25 players
- **Invisible barrier blocks**: Rejected - requires world modification, breaks pre-built arenas
- **ProtectionLib/WorldGuard dependency**: Rejected - adds external dependency (violates single-plugin principle)

**References**:
- WorldBorder API: https://hub.spigotmc.org/javadocs/spigot/org/bukkit/WorldBorder.html

---

### Decision 6: Particle Effect Optimization

**Decision**: **Distance-based particle reduction** with configurable radius

**Rationale**:
- SC-027 requirement: no client-side lag
- Edge case: multiple simultaneous explosions (spec mentions reduction)
- Render particles only for players within configurable radius (default: 20 blocks)
- Reduce particle count based on distance (farther = fewer particles)

**Implementation Pattern**:
```java
public void spawnParticleOptimized(Location location, Particle particle, int baseCount) {
    double maxRadius = 20.0; // Configurable

    for (Player player : location.getWorld().getPlayers()) {
        double distance = player.getLocation().distance(location);

        if (distance > maxRadius) continue; // Skip distant players

        // Reduce particle count by distance (linear falloff)
        int count = (int) (baseCount * (1.0 - (distance / maxRadius)));
        player.spawnParticle(particle, location, Math.max(count, 1));
    }
}
```

**Alternatives Considered**:
- **Global particle spawn**: Rejected - causes lag per SC-027
- **Fixed low particle count**: Rejected - reduces visual quality for nearby players
- **Server-side limit only**: Rejected - doesn't optimize per-client rendering

**References**:
- Particle API: https://hub.spigotmc.org/javadocs/spigot/org/bukkit/Particle.html
- Client performance considerations: https://www.spigotmc.org/wiki/particle-effects/

---

### Decision 7: Statistics Storage

**Decision**: **YAML flat files** (per-player) with in-memory cache

**Rationale**:
- Simple deployment (no database setup)
- Assumption #8 from spec: "Defaults to YAML file storage"
- Low write frequency (only at game end)
- Easy manual editing for administrators
- In-memory cache prevents disk I/O during gameplay

**File Structure**:
```yaml
# plugins/TNTTag/stats/<uuid>.yml
player:
  uuid: "a1b2c3d4-..."
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

**Alternatives Considered**:
- **SQLite**: Rejected for MVP - adds complexity, spec marks as "optional enhancement"
- **MySQL**: Rejected - requires external service (overkill for stats)
- **Single stats.yml**: Rejected - concurrent write issues with 25 players

**References**:
- Bukkit Configuration API: https://hub.spigotmc.org/javadocs/spigot/org/bukkit/configuration/file/FileConfiguration.html

---

### Decision 8: Firework Launch Timing

**Decision**: **BukkitRunnable** with counter-based loop

**Rationale**:
- SC-025 requirement: exactly 0.5-second intervals for 5 seconds (10 fireworks)
- Need precise timing control
- Must continue even if player disconnects (edge case from spec)
- Runnable allows countdown tracking and cancellation

**Implementation Pattern**:
```java
public void launchVictoryFireworks(Player player) {
    new BukkitRunnable() {
        int count = 0;
        Location loc = player.getLocation().clone();

        @Override
        public void run() {
            if (count >= 10) {
                this.cancel();
                return;
            }

            // Launch firework at stored location
            spawnRandomFirework(loc);
            count++;
        }
    }.runTaskTimer(plugin, 0L, 10L); // 10 ticks = 0.5 seconds
}
```

**Alternatives Considered**:
- **Scheduled task per firework**: Rejected - creates 10 tasks instead of 1
- **Async timer**: Rejected - firework spawn must be on main thread

**References**:
- BukkitRunnable: https://hub.spigotmc.org/javadocs/spigot/org/bukkit/scheduler/BukkitRunnable.html
- Firework API: https://hub.spigotmc.org/javadocs/spigot/org/bukkit/entity/Firework.html

---

## Testing Strategy

### Decision 9: MockBukkit for Unit Tests

**Decision**: **MockBukkit** framework for mocking Bukkit API

**Rationale**:
- Allows testing plugin logic without running full Minecraft server
- Mocks Player, World, Location, Event system
- Fast test execution (no server startup)
- Standard tool for Bukkit plugin testing

**Setup**:
```java
@BeforeEach
void setUp() {
    server = MockBukkit.mock();
    plugin = MockBukkit.load(TNTTagPlugin.class);
}

@AfterEach
void tearDown() {
    MockBukkit.unmock();
}
```

**Alternatives Considered**:
- **Manual mocking**: Rejected - too much boilerplate
- **Spigot test server**: Rejected - slow, complex setup
- **No unit tests**: Rejected - violates testing requirements

**References**:
- MockBukkit GitHub: https://github.com/MockBukkit/MockBukkit

---

### Decision 10: Integration Test Strategy

**Decision**: **Testcontainers with actual Spigot server** for critical paths

**Rationale**:
- Some behaviors require real server (e.g., scheduler timing, event ordering)
- Testcontainers provides disposable Docker-based test servers
- Run only for critical paths: full game flow, arena creation, command execution

**Critical Test Scenarios**:
1. Full 6-round game with mock players (validates FR-001 to FR-061)
2. Arena setup workflow: setpos1 → setpos2 → creategame (validates FR-066 to FR-071)
3. Command permission enforcement (validates FR-076 to FR-078)

**Alternatives Considered**:
- **No integration tests**: Rejected - complex event interactions need validation
- **Manual testing only**: Rejected - not repeatable/automatable
- **Full test server**: Rejected - too heavy for CI/CD

**References**:
- Testcontainers: https://www.testcontainers.org/

---

## Best Practices Applied

### Event-Driven Architecture
- All game state changes trigger custom events (TNTTagStartEvent, TNTTagRoundStartEvent, etc.)
- Loose coupling between components (listeners subscribe to events)
- Extensibility: other plugins can listen to TNT TAG events

### Configuration Management
- Externalized configuration (config.yml, arenas.yml, messages_ja_JP.yml)
- Hot-reload support via `/tnttag reload` (FR-075)
- Sensible defaults with override capability

### Performance Optimization
- Async HUD updates (off main thread)
- Distance-based particle culling
- In-memory caching of stats and arena data
- Lazy initialization of game instances

### Error Handling
- Graceful degradation (e.g., podium teleport fails → stay in place)
- Clear error messages (e.g., permission denied, invalid commands)
- Logging at appropriate levels (INFO for game events, WARN for issues, ERROR for critical failures)

### Code Organization
- Package-by-feature (game, arena, hud, effects, stats)
- Single Responsibility Principle (managers focus on one domain)
- Dependency injection where possible (pass dependencies to constructors)

---

## Open Questions & Future Research

### For Future Enhancements (Not in Scope)
1. **Database migration**: If stats volume grows, research Hibernate/jOOQ for SQL support
2. **Multi-server support**: Investigate BungeeCord/Velocity plugin messaging for cross-server games
3. **Custom resource packs**: Research server-side resource pack hosting for enhanced visuals
4. **Anti-cheat integration**: Research hooks for popular anti-cheat plugins (AAC, NoCheatPlus)

### Deferred Decisions
- Exact particle types for effects (will finalize during implementation based on visual testing)
- Sound volume/pitch values (will tune during implementation)
- Configuration default values (will set based on playtesting feedback)

---

## Summary

All technology choices are **finalized** and ready for Phase 1 design:

✅ Platform: Paper API 1.20.x (Spigot compatible)
✅ Language: Java 17
✅ Storage: YAML files with in-memory cache
✅ Testing: JUnit 5 + Mockito + MockBukkit + Testcontainers
✅ HUD: Multi-frequency async schedulers
✅ Boundaries: WorldBorder API
✅ Particles: Distance-based optimization
✅ TNT Transfer: EntityDamageByEntityEvent
✅ Fireworks: BukkitRunnable timed loop
✅ Stats: Per-player YAML files

**Next Phase**: Generate data model, event contracts, and quickstart guide.
