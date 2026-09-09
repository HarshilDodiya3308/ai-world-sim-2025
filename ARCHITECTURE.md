# AI World Simulation Engine - Architecture Documentation

## System Overview

The AI World Simulation Engine is a comprehensive Java application that simulates autonomous NPCs (Non-Player Characters) in a 2D world. Each NPC has its own decision-making process influenced by personality, memory, environment, and vital statistics.

## Core Components

### 1. Decision Engine (AI Brain)

**Location**: `com.aiworldsimulation.ai.DecisionEngine`

The heart of the AI system. Evaluates every possible action using a sophisticated multi-factor algorithm:

```
Final Score = (Base Score + Health Factor + Energy Factor + Hunger Factor + 
               Environment Factor + Interaction Factor + Memory Factor) 
              × Personality Modifier
```

**Key Methods**:
- `makeDecision()`: Main decision-making entry point
- `evaluateAction()`: Scores a single action (0-100)
- `generateExplanation()`: Creates human-readable reasoning

**Decision Factors**:
1. **Health Factor**: Adjusts based on NPC health status
   - Critical (< 20): +30 for HEAL/REST, -40 for ATTACK
   - Low (< 50): +20 for HEAL, -20 for ATTACK

2. **Energy Factor**: Discourages exhausting actions when low
   - Low energy (< 30): +25 for REST, -15 for ATTACK/EXPLORE

3. **Hunger Factor**: Encourages eating when hungry
   - High hunger (> 70): +30 for EAT, +10 for TRADE/COLLECT

4. **Environment Factor**: Responds to world conditions
   - High danger (> 0.7): +20 for RETREAT, -15 for ATTACK
   - High resources (> 0.6): +15 for COLLECT, +5 for EXPLORE

5. **Interaction Factor**: Considers nearby NPCs
   - Trading: +10 if other NPCs nearby
   - Helping: +20 if injured NPCs nearby, -10 if none

6. **Memory Factor**: Learns from past experiences
   - RETREAT: +5 per relevant danger memory
   - EXPLORE: +3 per discovery memory

7. **Personality Modifier**: Multiplier between 0.5-1.5
   - Aggressive: ATTACK ×1.4, RETREAT ×0.4
   - Curious: EXPLORE ×1.5, RETREAT ×0.7
   - Greedy: TRADE ×1.4, COLLECT ×1.4
   - Helpful: HELP ×1.5, HEAL ×1.3

### 2. Personality System

**Location**: `com.aiworldsimulation.ai.Personality`

Defines 6 core personality types, each with 6 traits (0.0-1.0 scale):

| Type | Aggression | Defensiveness | Curiosity | Greed | Helpfulness | Caution |
|------|-----------|---------------|-----------|-------|-------------|----------|
| Aggressive | 0.8 | 0.2 | 0.5 | 0.4 | 0.2 | 0.1 |
| Defensive | 0.2 | 0.8 | 0.3 | 0.3 | 0.5 | 0.8 |
| Curious | 0.3 | 0.3 | 0.9 | 0.4 | 0.4 | 0.4 |
| Greedy | 0.4 | 0.4 | 0.5 | 0.9 | 0.1 | 0.3 |
| Helpful | 0.1 | 0.4 | 0.5 | 0.1 | 0.9 | 0.5 |
| Balanced | 0.5 | 0.5 | 0.5 | 0.5 | 0.5 | 0.5 |

**Method**: `getInfluenceForAction(actionName)` returns a multiplier for action preference.

### 3. Simulation World

**Location**: `com.aiworldsimulation.simulation.SimulationWorld`

Manages all entities and provides context:
- Contains up to 200×200 grid
- Manages NPC and Location collections
- Provides spatial queries (nearby NPCs, visible locations)
- Updates world state each tick

**Key Responsibilities**:
- NPC management (add, remove, query)
- Location queries
- World state updates
- Integration with DecisionEngine and ActionExecutor

### 4. Action Executor

**Location**: `com.aiworldsimulation.simulation.ActionExecutor`

Implements actual action behaviors:
- **MOVE**: Random movement within world bounds
- **EXPLORE**: Discover new locations, create memories
- **ATTACK**: Find nearby NPCs and deal damage
- **RETREAT**: Move away from danger
- **REST**: Restore energy
- **EAT**: Consume food or find resources
- **TRADE**: Exchange goods with nearby NPCs
- **COLLECT_RESOURCE**: Gather from current location
- **HELP**: Heal nearby injured NPCs
- **HEAL**: Self-healing
- **PATROL**: Systematic movement
- **WAIT**: Idle action with minimal effects

Each action:
- Modifies NPC state (position, health, energy, etc.)
- Consumes resources
- May modify world state (locations, other NPCs)
- Returns success/failure status

### 5. Memory System

**Location**: `com.aiworldsimulation.model.Memory`

NPCs retain last 100 memories with:
- Type: DANGER, SUCCESS, FAILURE, DISCOVERY, INTERACTION, RESOURCE, INJURY
- Description: Human-readable event text
- Location: Where it occurred
- Importance: 0.0-1.0 weighting
- Relevance: Boolean flag for filtering
- Timestamp: When it occurred

Memories influence future decisions through the Memory Factor in DecisionEngine.

### 6. Simulation Manager

**Location**: `com.aiworldsimulation.simulation.SimulationManager`

Controls simulation lifecycle:
- **States**: STOPPED, RUNNING, PAUSED
- **Execution**: Runs on background thread with configurable speed
- **Listeners**: Notifies UI of state changes and steps
- **Commands**: start(), pause(), resume(), stop(), step(), reset()

**Threading Model**:
- Uses `ScheduledExecutorService` for background execution
- Step rate configurable (10ms to 2000ms)
- Thread-safe via `CopyOnWriteArrayList` for listeners

## Data Flow

### Simulation Step Sequence

```
1. SimulationManager.step() called
   ↓
2. For each NPC in world:
   a. Get nearby NPCs and visible locations
   b. Create WorldState
   c. DecisionEngine.makeDecision()
      - Evaluate all 12 actions
      - Calculate scores considering:
        * Current NPC vitals
        * Environment conditions
        * Personality traits
        * Memory history
      - Select highest-scoring action
      - Generate explanation text
   d. Decision stored in NPC history
   e. ActionExecutor.executeAction()
      - Implement chosen action
      - Update NPC state
      - Modify world/other NPCs
   f. Set decision success status
   ↓
3. SimulationWorld.updateWorld()
   a. Apply natural effects:
      - Resource depletion
      - Danger fluctuation
      - Health regeneration
      - Hunger increase
      - Energy decrease
   ↓
4. Notify all listeners of step completion
```

## UI Architecture

### Panel Hierarchy

```
MainWindow (JFrame)
├── ControlPanel (BorderLayout.NORTH)
│   ├── Status Label
│   ├── Start/Pause/Stop/Step/Reset Buttons
│   ├── Speed Combo Box
│   └── Settings Button
├── JSplitPane (BorderLayout.CENTER)
│   ├── WorldSimulationPanel (LEFT)
│   │   └── 2D World Visualization
│   └── JTabbedPane (RIGHT)
│       ├── NPCListPanel (NPCs Tab)
│       ├── DecisionInspectorPanel (Decision Inspector Tab)
│       ├── EventLogPanel (Event Log Tab)
│       └── StatisticsPanel (Statistics Tab)
```

### Real-time Updates

UI components update via:
1. `SimulationManager.addListener()` registration
2. `onSimulationStep()` callback for each simulation tick
3. `SwingUtilities.invokeLater()` for thread-safe GUI updates
4. `Timer` for periodic refresh (500ms for NPC list, 1000ms for stats)

## Database Layer

**Technology**: SQLite with JDBC

**Tables**:
- **npcs**: Core NPC data (ID, name, role, personality, position, vitals)
- **locations**: Locations (ID, name, terrain, position, resources, danger)
- **memories**: Memory events (ID, NPC ID, type, description, importance)
- **decisions**: Decision records (ID, NPC ID, action, explanation, confidence)
- **events**: World events (ID, type, description, affected entities)
- **inventory**: NPC inventory items
- **relationships**: NPC-to-NPC relationship scores
- **saved_games**: Simulation save records

**DAO Pattern**: Each major entity has a DAO class for CRUD operations:
- `NPCDao`
- `LocationDao`
- `MemoryDao`
- `DecisionDao`

## Key Algorithms

### Decision Scoring Algorithm

```java
double score = 50.0;  // Base score

// Add/subtract based on vital signs
score += evaluateHealthFactor(npc, action);
score += evaluateEnergyFactor(npc, action);
score += evaluateHungerFactor(npc, action);

// Add/subtract based on environment
score += evaluateEnvironmentFactor(npc, action, locations, world);

// Add/subtract based on nearby NPCs
score += evaluateInteractionFactor(npc, action, nearbyNpcs);

// Add/subtract based on memory
score += evaluateMemoryFactor(npc, action);

// Apply personality multiplier
score *= personality.getInfluenceForAction(action.toString());

// Clamp to 0-100
return Math.max(0, Math.min(100, score));
```

### Spatial Queries

```java
// Get NPCs within distance
List<NPC> nearby = world.getNearbyNPCs(x, y, distance);

// Implemented as:
npcs.stream()
    .filter(npc -> npc.getDistanceTo(x, y) <= distance)
    .collect(Collectors.toList());
```

## Extensibility

### Adding New Actions

1. Add to `NPC.NPCAction` enum
2. Implement in `ActionExecutor.executeAction()`
3. Add scoring logic in `DecisionEngine.evaluateAction()`
4. Add to `Personality.getInfluenceForAction()`

### Adding New Personality Types

1. Add to `Personality.PersonalityType` enum
2. Initialize traits in `Personality.initializeTraits()`
3. Test in decision engine

### Adding World Events

1. Add to `WorldEvent.WorldEventType`
2. Generate events in `SimulationWorld.updateWorld()`
3. Modify decision engine to respond to events

## Performance Considerations

- **Memory**: Each NPC stores last 100 memories and 50 decisions (~2KB per NPC)
- **Computation**: Decision engine runs O(actions) per NPC per tick
- **Rendering**: Custom panel repaints on every step (configurable)
- **Threading**: Simulation runs on separate thread; UI updates queued
- **Database**: Asynchronous persistence can be added for production

## Testing Strategy

**Unit Tests** (`src/test/java/`):
- `DecisionEngineTest`: Verify decision-making logic
- `ActionExecutorTest`: Verify action implementations
- `NPCTest`: Verify NPC state management

**Integration Testing**:
- Run full simulation and verify emergent behaviors
- Check memory accumulation
- Verify database persistence

**UI Testing**:
- Manual testing of all panels
- Verify real-time updates
- Test extreme speeds

---

For more details, refer to inline code documentation.
