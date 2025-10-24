# Developer Quickstart: TNT TAG Plugin

**Feature**: TNT TAG - Minecraft Survival Tag Minigame
**Date**: 2025-10-24
**Audience**: Java developers new to this project

## Overview

This guide helps you get the TNT TAG plugin development environment set up and running tests within 15 minutes. It covers installation, building, testing, and running a local test server.

---

## Prerequisites

### Required

- **Java 17 JDK** (LTS version)
  - Download: https://adoptium.net/temurin/releases/?version=17
  - Verify: `java -version` (should show 17.x.x)

- **Maven 3.8+** or **Gradle 8.0+** (choose one)
  - Maven: https://maven.apache.org/download.cgi
  - Gradle: https://gradle.org/install/
  - Verify: `mvn -version` or `gradle -version`

- **Git**
  - Download: https://git-scm.com/downloads
  - Verify: `git --version`

### Recommended

- **IntelliJ IDEA** (Community or Ultimate) or **VS Code** with Java extensions
- **Docker** (for integration tests with Testcontainers)
- **Spigot BuildTools** (for building latest Spigot API)

---

## Quick Setup (5 minutes)

### 1. Clone Repository

```bash
git clone <repository-url>
cd tnt_tag
git checkout 001-tnt-tag-game
```

### 2. Build Spigot API (First Time Only)

The plugin depends on Spigot API, which isn't in Maven Central. Build it locally:

```bash
# Create build directory
mkdir -p buildtools
cd buildtools

# Download BuildTools
curl -o BuildTools.jar https://hub.spigotmc.org/jenkins/job/BuildTools/lastSuccessfulBuild/artifact/target/BuildTools.jar

# Build Spigot 1.20.4 (takes 5-10 minutes)
java -jar BuildTools.jar --rev 1.20.4

# Return to project root
cd ..
```

**Note**: BuildTools installs Spigot to your local Maven repository (`~/.m2/repository`).

### 3. Build Plugin

**Using Maven**:
```bash
mvn clean package
```

**Using Gradle**:
```bash
gradle clean build
```

**Expected Output**:
```
BUILD SUCCESS
Total time: 15.2 s
```

**Artifact Location**:
- Maven: `target/TNTTag-1.0-SNAPSHOT.jar`
- Gradle: `build/libs/TNTTag-1.0-SNAPSHOT.jar`

---

## Project Structure

```
tnt_tag/
├── src/
│   ├── main/
│   │   ├── java/com/example/tnttag/  # Plugin source code
│   │   └── resources/                 # Config files, plugin.yml
│   └── test/
│       └── java/com/example/tnttag/  # Unit & integration tests
├── specs/
│   └── 001-tnt-tag-game/              # Design documentation
├── pom.xml                             # Maven build file
├── build.gradle                        # Gradle build file (alternative)
└── README.md                           # Project overview
```

---

## Running Tests

### Unit Tests (Fast - No Server Required)

Uses MockBukkit to mock Bukkit API.

**Maven**:
```bash
mvn test
```

**Gradle**:
```bash
gradle test
```

**Expected Output**:
```
Tests run: 42, Failures: 0, Errors: 0, Skipped: 0
```

### Integration Tests (Slow - Requires Docker)

Uses Testcontainers to spin up a real Spigot server.

**Maven**:
```bash
mvn verify -P integration-tests
```

**Gradle**:
```bash
gradle integrationTest
```

**First Run**: Downloads Spigot Docker image (~500MB), takes 2-3 minutes.

**Expected Output**:
```
[INFO] Running GameFlowTest
[INFO] Container started: spigot-test
[INFO] Full 6-round game flow: PASSED
[INFO] Tests run: 3, Failures: 0, Errors: 0, Skipped: 0
```

---

## Running Local Test Server

### Option 1: Manual Setup (Traditional)

1. **Create Server Directory**:
```bash
mkdir -p test-server
cd test-server
```

2. **Copy Spigot JAR**:
```bash
# Copy from BuildTools output
cp ../buildtools/spigot-1.20.4.jar spigot.jar
```

3. **Accept EULA**:
```bash
# First run generates eula.txt
java -jar spigot.jar

# Edit eula.txt
echo "eula=true" > eula.txt
```

4. **Install Plugin**:
```bash
# Copy built plugin to plugins/ folder
mkdir -p plugins
cp ../target/TNTTag-1.0-SNAPSHOT.jar plugins/
```

5. **Start Server**:
```bash
java -Xms1G -Xmx2G -jar spigot.jar nogui
```

6. **Connect**: Open Minecraft 1.20.4 and connect to `localhost:25565`

### Option 2: Docker Compose (Recommended)

1. **Create docker-compose.yml**:
```yaml
version: '3.8'
services:
  minecraft:
    image: itzg/minecraft-server:java17
    ports:
      - "25565:25565"
    environment:
      EULA: "TRUE"
      TYPE: "SPIGOT"
      VERSION: "1.20.4"
      MEMORY: "2G"
    volumes:
      - ./test-server:/data
      - ./target/TNTTag-1.0-SNAPSHOT.jar:/plugins/TNTTag.jar
```

2. **Start Server**:
```bash
docker-compose up
```

3. **Connect**: Minecraft client to `localhost:25565`

4. **Stop Server**: `Ctrl+C` or `docker-compose down`

---

## First-Time Setup Checklist

After server starts, run these commands in-game:

### 1. Give Yourself OP

In server console:
```
op YourUsername
```

### 2. Create Test Arena

In-game (as OP):
```
/tnttag setpos1
[Move to opposite corner]
/tnttag setpos2
/tnttag creategame test_arena
```

### 3. Verify Arena

```
/tnttag list
```

Expected output:
```
════ TNT TAG Arenas ════
test_arena - [Available] (0/25 players)
═══════════════════════════
```

### 4. Test Join

```
/tnttag join test_arena
```

Expected: You're teleported to arena, scoreboard appears with "Waiting for players..."

---

## Development Workflow

### 1. Make Code Changes

Edit files in `src/main/java/com/example/tnttag/`

### 2. Run Tests

```bash
# Unit tests (fast feedback)
mvn test -Dtest=GameManagerTest

# Specific test method
mvn test -Dtest=GameManagerTest#testRoundProgression
```

### 3. Build Plugin

```bash
mvn clean package -DskipTests  # Skip tests for faster builds
```

### 4. Hot-Reload (Without Server Restart)

Install **PlugMan** on test server:
```bash
# Download PlugMan
cd test-server/plugins
wget https://github.com/r-clancy/PlugMan/releases/download/2.3.4/PlugMan-2.3.4.jar
```

In-game:
```
/plugman unload TNTTag
/plugman load TNTTag
```

Or use:
```
/tnttag reload  # Reloads config only, not code
```

### 5. Debug with IntelliJ

1. **Add Remote Debug Configuration**:
   - Run → Edit Configurations → Add New → Remote JVM Debug
   - Host: localhost
   - Port: 5005

2. **Start Server with Debug**:
```bash
java -Xms1G -Xmx2G -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005 -jar spigot.jar nogui
```

3. **Attach Debugger**: Run → Debug 'Remote JVM Debug'

4. **Set Breakpoints**: Click left gutter in code editor

---

## Common Tasks

### Add New Dependency

**Maven** (pom.xml):
```xml
<dependency>
    <groupId>com.example</groupId>
    <artifactId>library</artifactId>
    <version>1.0.0</version>
</dependency>
```

**Gradle** (build.gradle):
```groovy
dependencies {
    implementation 'com.example:library:1.0.0'
}
```

Then rebuild: `mvn clean package`

### Create New Event Listener

1. Create class in `src/main/java/com/example/tnttag/listeners/`:
```java
package com.example.tnttag.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class MyListener implements Listener {
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        // Your logic here
    }
}
```

2. Register in `TNTTagPlugin.java`:
```java
@Override
public void onEnable() {
    getServer().getPluginManager().registerEvents(new MyListener(), this);
}
```

### Add New Command

1. Add to `plugin.yml`:
```yaml
commands:
  mycommand:
    description: My custom command
    usage: /mycommand
```

2. Create executor in `src/main/java/com/example/tnttag/commands/`:
```java
package com.example.tnttag.commands;

import org.bukkit.command.*;

public class MyCommandExecutor implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command,
                             String label, String[] args) {
        sender.sendMessage("Command executed!");
        return true;
    }
}
```

3. Register in `TNTTagPlugin.java`:
```java
@Override
public void onEnable() {
    getCommand("mycommand").setExecutor(new MyCommandExecutor());
}
```

---

## Troubleshooting

### Build Fails: "Cannot resolve symbol 'org.bukkit'"

**Solution**: Build Spigot API first (see "Build Spigot API" above)

### Tests Fail: "Could not initialize plugin: TNTTagPlugin"

**Solution**: Ensure MockBukkit is in test dependencies:
```xml
<dependency>
    <groupId>com.github.seeseemelk</groupId>
    <artifactId>MockBukkit-v1.20</artifactId>
    <version>3.9.0</version>
    <scope>test</scope>
</dependency>
```

### Server Crashes: "Unsupported major.minor version"

**Solution**: Verify Java 17 is being used:
```bash
java -version  # Should show 17.x.x
which java     # Check path
```

### Plugin Not Loading: "Could not load 'TNTTag.jar'"

**Solution**: Check server logs for error details:
```bash
tail -f test-server/logs/latest.log
```

Common causes:
- Missing dependency in plugin.yml
- Java version mismatch
- Corrupt JAR file (rebuild: `mvn clean package`)

### Permission Denied: "このコマンドを実行する権限がありません"

**Solution**: Give yourself OP in server console:
```
op YourUsername
```

---

## Useful Resources

### Official Documentation

- **Spigot API**: https://hub.spigotmc.org/javadocs/spigot/
- **Bukkit API**: https://hub.spigotmc.org/javadocs/bukkit/
- **Paper API**: https://docs.papermc.io/paper/dev/api

### Community Resources

- **Spigot Forums**: https://www.spigotmc.org/forums/
- **PaperMC Discord**: https://discord.gg/papermc
- **r/admincraft**: https://reddit.com/r/admincraft

### Project-Specific Docs

- **Feature Spec**: `specs/001-tnt-tag-game/spec.md`
- **Data Model**: `specs/001-tnt-tag-game/data-model.md`
- **Event Contracts**: `specs/001-tnt-tag-game/contracts/events.md`
- **Command Contracts**: `specs/001-tnt-tag-game/contracts/commands.md`
- **Implementation Plan**: `specs/001-tnt-tag-game/plan.md`
- **Research**: `specs/001-tnt-tag-game/research.md`

---

## Development Environment Checklist

### ✅ Setup Complete When:

- [ ] Java 17 installed (`java -version`)
- [ ] Maven or Gradle installed (`mvn -version`)
- [ ] Spigot API built (`ls ~/.m2/repository/org/spigotmc/spigot-api`)
- [ ] Project builds without errors (`mvn clean package`)
- [ ] Unit tests pass (`mvn test`)
- [ ] Test server runs (`java -jar test-server/spigot.jar`)
- [ ] Plugin loads on server (check `plugins` folder in-game)
- [ ] First arena created (`/tnttag list` shows arena)
- [ ] Can join game (`/tnttag join test_arena`)

### ✅ Ready to Develop When:

- [ ] IDE configured (IntelliJ/VS Code with Java plugin)
- [ ] Remote debugger attached (port 5005)
- [ ] PlugMan installed (for hot-reload)
- [ ] Read spec.md and data-model.md
- [ ] Familiar with event contracts
- [ ] Familiar with command contracts

---

## Next Steps

1. **Read Core Documentation**:
   - Start with `specs/001-tnt-tag-game/spec.md` (feature requirements)
   - Review `specs/001-tnt-tag-game/data-model.md` (entities and relationships)

2. **Explore Codebase**:
   - `src/main/java/com/example/tnttag/TNTTagPlugin.java` (main entry point)
   - `src/main/java/com/example/tnttag/game/GameManager.java` (core game logic)
   - `src/main/java/com/example/tnttag/commands/` (command handlers)

3. **Run Integration Tests**:
   ```bash
   mvn verify -P integration-tests
   ```

4. **Check Tasks**:
   - Once tasks.md is generated by `/speckit.tasks`, review implementation order

5. **Join Development Discussion**:
   - Check project Discord/Slack for team coordination
   - Review open issues/PRs in repository

---

## Getting Help

### Quick Questions

- Check `specs/001-tnt-tag-game/` documentation first
- Search Spigot API javadocs
- Review existing code for patterns

### Issues

- **Build/Setup Problems**: Check Troubleshooting section above
- **Design Questions**: Reference spec.md and data-model.md
- **API Usage**: Consult Spigot/Bukkit javadocs

### Contact

- **Project Lead**: See repository README
- **Team Chat**: [Discord/Slack link if available]
- **Issue Tracker**: [GitHub/GitLab issues if available]

---

**Estimated Time to Productive**:
- First-time setup: 15-20 minutes
- Build + test cycle: 1-2 minutes
- Hot-reload cycle: 10 seconds

**You're ready to start developing! 🚀**
