# 💥 TNT TAG - Explosive Survival Tag Game

TNT TAG is an explosive survival-style tag minigame plugin for Minecraft servers, designed for 20-25 players. Get ready for intense 6-round matches where randomly selected players carry TNT and must pass it to others before time runs out!

## 🎮 Game Overview

- **Players:** 20-25 players per game
- **Duration:** Approximately 5 minutes (6 rounds total)
- **Objective:** Survive all 6 rounds without exploding. Last player standing wins!
- **Game Mode:** Survival tag with explosive consequences

## ✨ Key Features

### Dynamic Round System
- **6 Fixed Rounds** with escalating difficulty
- Round-specific TNT holder counts (from 1 player to 50% of survivors)
- Variable round durations (25-50 seconds)
- Special glowing effect in final rounds (Rounds 5-6)

### Balanced Gameplay Mechanics
- **Speed Boost:** TNT holders get Speed II, normal players get Speed I
- **No PVP Damage:** Players can only transfer TNT, no actual damage
- **No Splash Damage:** Only TNT holders explode when time runs out
- **Arena Boundaries:** Automatic teleport-back system prevents players from escaping

### Rich Visual & Audio Feedback
- **Scoreboard:** Real-time game state, round info, and survivor count
- **Action Bar:** Updates 2x per second with status and warnings
- **Boss Bar:** TNT holders see countdown progress bar
- **Title Messages:** Round start, TNT transfer, explosion, and victory announcements
- **Particle Effects:** Explosions, victory fireworks, game start effects
- **Sound Effects:** Countdown beeps, explosions, and atmospheric audio

### Auto-Start System
- Games automatically start when 20 players join
- 10-second countdown with visual and audio cues
- Automatic game creation and cleanup

## 📋 Round Breakdown

| Round | TNT Holders | Duration | Special Effect |
|-------|-------------|----------|----------------|
| 1 | 1 player | 40s | None |
| 2 | 25% of players | 30s | None |
| 3 | 25% of players | 30s | None |
| 4 | 50% of players | 25s | None |
| 5 | 33% of players | 40s | All Glowing ✨ |
| 6 | 33% of players | 50s | All Glowing ✨ |

## 💻 Commands

### Player Commands
- `/tnttag join [arena]` - Join a game
- `/tnttag leave` - Leave current game
- `/tnttag stats [player]` - View statistics
- `/tnttag list` - List available arenas

### Admin Commands
- `/tnttag setpos1` - Set arena corner 1
- `/tnttag setpos2` - Set arena corner 2
- `/tnttag creategame <arena>` - Create new arena
- `/tnttag delete <arena>` - Delete arena
- `/tnttag start <arena>` - Force start game
- `/tnttag stop <arena>` - Force stop game
- `/tnttag reload` - Reload configuration

## 🔧 Configuration

Fully customizable via `config.yml`:
- Minimum/maximum player counts
- Countdown duration
- Round-specific settings (TNT holders, duration, effects)
- Debug mode
- Messages in Japanese and English

## 📊 Statistics System

Tracks player performance:
- Games played
- Wins
- Rounds survived
- TNT tags given/received
- Total survival time

## 🎯 Perfect For

- **Mini-game Servers:** Add variety to your server's game rotation
- **Events:** Host tournaments with built-in statistics
- **Community Servers:** Great for 20+ player gatherings
- **Competition:** Built-in leaderboards via statistics

## 🌐 Multilingual Support

- **Japanese (日本語)** - Full support
- **English** - Full support
- Includes player guide documentation in both languages

## 📦 Requirements

- **Minecraft Version:** 1.21.5+
- **Server Software:** Paper (recommended)
- **Java Version:** 21+
- **Players:** 20-25 recommended (minimum 4 for testing)

## 🚀 Installation

1. Download the latest JAR file
2. Place in your server's `plugins` folder
3. Restart your server
4. Configure arenas using `/tnttag setpos1` and `/tnttag setpos2`
5. Create arena with `/tnttag creategame <arena>`
6. Players can join with `/tnttag join`

## 📖 Documentation

Comprehensive player guide included at `docs/index.html`:
- Game rules and mechanics
- Strategy tips
- Command reference
- HUD explanation
- Available in Japanese and English

## 🎨 Design Philosophy

TNT TAG was designed with these principles:
- **Fast-paced action** - Quick rounds keep energy high
- **Strategic depth** - Speed differential creates cat-and-mouse gameplay
- **Spectator-friendly** - Clear visual feedback for observers
- **Fair randomization** - Equal opportunity for all players
- **Scalable difficulty** - Progressive challenge across rounds

## 🐛 Bug Reports & Support

Found a bug? Have a suggestion?
- GitHub Issues: [Report here](https://github.com/yokmama/tnt_tag/issues)

## 👨‍💻 Credits

**Game Design:** Saito Yuuki (斉藤ゆうき)
**Implementation:** TNT TAG Development Team

## 📜 License

Licensed under MIT License. See LICENSE file for details.

---

**Ready to explode?** Download now and bring explosive fun to your Minecraft server! 💥
