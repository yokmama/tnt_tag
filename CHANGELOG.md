# Changelog

All notable changes to TNT TAG will be documented in this file.

## [1.0.0] - 2025-01-07

### Added
- Initial release of TNT TAG plugin
- 6-round survival tag gameplay for 20-25 players
- Auto-start system when minimum players reached
- TNT holder mechanics with speed boosts and transfer system
- Round-specific configurations (holder counts, durations, effects)
- Arena management with pos1/pos2 setup and creation commands
- Comprehensive HUD system:
  - Scoreboard: Phase-specific game state display
  - Action Bar: Real-time updates (2x/sec) for all player states
  - Boss Bar: TNT holder warnings with countdown progress
  - Title Messages: Event notifications with 3-second animations
- Visual & audio effects:
  - Game Start: Countdown sounds, title display, green particles, Regeneration
  - Round Start: Lightning effects, dramatic sounds, particle pillars
  - Victory: 10 fireworks, golden particles, glowing effect
  - Defeat: Smoke clouds, somber sounds
  - Elimination: Explosion particles, smoke rings, blindness effect
- Player commands: `/tnttag join/leave/stats/list` (no OP required)
- Admin commands: `/tnttag setpos1/setpos2/creategame/delete/start/stop/reload` (OP required)
- Statistics tracking system (games played, wins, rounds survived, tags)
- Multilingual support (Japanese and English)
- Player guide documentation (HTML format)
- Arena boundary protection system
- Complete game cleanup and restart capability

### Technical Details
- Paper 1.21.5 API compatibility
- Java 21 support
- Async-safe HUD managers running on main thread
- Legacy color code support via LegacyComponentSerializer
- Proper game instance cleanup to prevent memory leaks
- Debug logging for troubleshooting

### Fixed
- Arena center coordinate calculation (Y-axis)
- Legacy color code warnings in HUD managers
- Async thread errors in scoreboard operations
- Arena boundary check (2D X/Z only, allowing vertical movement)
- HUD cleanup on game end (scoreboard and boss bar persistence)
- Game instance cleanup (prevents "already in game" errors)
- Player game map cleanup (prevents "game full" errors after restart)

### Credits
- **Game Design:** Saito Yuuki (斉藤ゆうき)
- **Implementation:** TNT TAG Development Team
