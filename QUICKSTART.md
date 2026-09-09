# Quick Start Guide

## Installation (5 minutes)

### 1. Prerequisites
```bash
# Check Java version (need 17+)
java -version

# Check Maven version (need 3.6+)
mvn -version
```

### 2. Clone & Build
```bash
# Clone repository
git clone https://github.com/HarshilDodiya3308/ai-world-sim-2025.git
cd ai-world-sim-2025

# Build project
mvn clean package
```

### 3. Run Application
```bash
# Option 1: Using Maven
mvn exec:java -Dexec.mainClass="com.aiworldsimulation.Main"

# Option 2: Using JAR
java -jar target/ai-world-simulation-1.0.0.jar
```

## First Steps (5 minutes)

### Launch Window
- Application opens in maximized mode
- Shows 200x200 world grid with NPCs and locations
- Control panel at top with START button

### Start Simulation
1. Click **START** button (top-left)
2. Watch NPCs move and interact automatically
3. Observe colors change (health status, location danger)

### Inspect an NPC
1. Click on any **circle** in the world (NPC)
2. Selected NPC gets green border
3. Check right panels for details:
   - **NPCs tab**: Lists all NPCs with vitals
   - **Decision Inspector tab**: Shows why selected NPC did its action
   - **Event Log tab**: Real-time events
   - **Statistics tab**: World-wide stats

### Control Simulation
- **PAUSE**: Freeze simulation
- **RESUME**: Continue
- **STOP**: Reset to beginning
- **STEP**: Advance one step manually
- **Speed dropdown**: Change simulation speed

## Understanding the Visualization

### NPCs (Circles)
- **Color**: Green = High Health, Red = Low Health, Yellow = Medium
- **Size**: All same (12 pixels)
- **Label**: First letter of name

### Locations (Squares)
- **Color Intensity**: Darker = More Danger
- **Size**: 30 pixels
- **Label**: First 3 letters of name

### Grid
- **Light grid lines**: Every 20 units
- **Dark background**: Safe areas

## Reading the Panels

### NPCs Panel (Tab 1)
```
Alex (Guard) - HP: 85 | NRG: 72
Bob (Trader) - HP: 92 | NRG: 65
...
```

Click on any NPC to see detailed stats:
- Role and Personality
- Health/Energy/Hunger/Money
- Current position and state
- Recent decisions

### Decision Inspector (Tab 2)

Shows:
```
=== CURRENT STATE ===
Health: 85.0%
Energy: 72.0%
Hunger: 25.0%
Money: $55.0

=== RECENT DECISIONS ===
[14:32:45] MOVE (Confidence: 85.3%)
[14:32:44] EXPLORE (Confidence: 92.1%)

=== DECISION REASONING ===
Decision: EXPLORE
Confidence: 92.1%

Reason:
- Personality: CURIOUS
- Role: EXPLORER

Action Scores:
  EXPLORE: 82.5
  MOVE: 45.2
  REST: 20.0
  ...
```

### Event Log (Tab 3)
```
[14:32:45] Step: 125
[14:32:44] Step: 124
[14:32:43] Step: 123
...
```

### Statistics (Tab 4)
```
Population:
  Total NPCs: 8
  Alive: 8

Simulation:
  Step Count: 125
  State: RUNNING
  Speed: 500 ms/step

Average Stats:
  Health: 78.5%
  Energy: 65.3%

NPC States:
  IDLE: 2
  MOVING: 3
  ACTING: 2
  RESTING: 1
```

## Experiment Ideas

### 1. Observe Personality Differences
- Aggressive NPC → Attacks frequently
- Curious NPC → Explores more
- Greedy NPC → Collects resources
- Helpful NPC → Helps others
- Defensive NPC → Stays safe

### 2. Change Simulation Speed
Speed dropdown options:
- Slow (2s): Observe individual decisions
- Normal (500ms): Balanced view
- Fast (100ms): See emergent behavior
- Very Fast (10ms): Full-speed chaos

### 3. Pause and Analyze
1. Click PAUSE
2. Click STEP to advance 1 step
3. Inspect an NPC's decision
4. Understand what factors influenced it

### 4. Monitor Statistics
- Watch how health/energy change over time
- See which actions are most common
- Track NPC state distribution

## Troubleshooting

### NPCs not visible
- **Solution**: Check World Simulation Panel (left side)
- They appear as small colored circles
- Try zooming out if needed

### Simulation won't start
- **Solution**: Click START button again
- Check console for errors
- Try restarting application

### Database error
- **Solution**: Delete `ai_world_simulation.db`
- Restart application
- Database will be recreated

### Performance lag
- **Solution**: Reduce simulation speed
- Close other applications
- Increase Java heap: `java -Xmx1024m -jar ...`

## Next Steps

1. **Read Full README.md** for complete feature list
2. **Check ARCHITECTURE.md** to understand the AI system
3. **Run tests**: `mvn test`
4. **Modify code**: Try changing NPC personalities or actions
5. **Contribute**: Submit improvements to the project

## Tips & Tricks

- Right-click → Settings for configuration options
- Use STEP button while PAUSED to debug decisions
- Watch the Event Log while simulation runs
- Customize NPC count in `MainWindow.createDemoNPCs()`
- Check decision reasoning to understand NPC choices
- Locations with low resources auto-replenish over time
- NPCs that die stay as ghosts (0 health) until reset

---

Enjoy exploring the simulation! 🚀
