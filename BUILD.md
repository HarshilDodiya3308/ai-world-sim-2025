# Build and Deployment Guide

## Building the Project

### Prerequisites
- Java 17 JDK
- Maven 3.6.0+

### Build Commands

**Clean build**:
```bash
mvn clean package
```

**Compile only**:
```bash
mvn compile
```

**Run tests**:
```bash
mvn test
```

**Generate JAR**:
```bash
mvn package -DskipTests
```

**Build with all plugins**:
```bash
mvn clean install -U
```

## Running the Application

### Method 1: Maven Exec Plugin
```bash
mvn exec:java -Dexec.mainClass="com.aiworldsimulation.Main"
```

### Method 2: Execute JAR
```bash
java -jar target/ai-world-simulation-1.0.0.jar
```

### Method 3: IDE Run
Most IDEs (IntelliJ, Eclipse) allow running directly:
1. Open `Main.java`
2. Right-click → Run

## Troubleshooting

### Build Issues

**Error: No compiler is provided**
- Solution: Ensure `JAVA_HOME` environment variable is set correctly
  ```bash
  export JAVA_HOME=/path/to/jdk17
  ```

**Error: SQLite JDBC not found**
- Solution: Run `mvn clean install` to download dependencies

**Error: Tests failing**
- Solution: Skip tests during build:
  ```bash
  mvn package -DskipTests
  ```

### Runtime Issues

**Error: No main class specified**
- Ensure you're using the shaded JAR or specify main class:
  ```bash
  java -cp target/ai-world-simulation-1.0.0.jar com.aiworldsimulation.Main
  ```

**Error: Database locked**
- Close all instances of the application
- Delete `ai_world_simulation.db` and restart

**Error: Out of Memory**
- Increase heap size:
  ```bash
  java -Xmx1024m -jar target/ai-world-simulation-1.0.0.jar
  ```

## Configuration

### Modify Simulation Parameters

Edit `pom.xml` for build settings:
```xml
<properties>
    <maven.compiler.source>17</maven.compiler.source>
    <maven.compiler.target>17</maven.compiler.target>
</properties>
```

### Runtime Configuration

In `MainWindow.java`:
```java
// Adjust world size
world = new SimulationWorld(200, 200);

// Adjust simulation speed (milliseconds)
simulationManager.setSimulationSpeed(500);

// Modify demo NPC count in createDemoNPCs()
```

## Project Structure

```
ai-world-sim-2025/
├── pom.xml                  # Maven configuration
├── README.md               # Main documentation
├── ARCHITECTURE.md         # Architecture details
├── BUILD.md               # This file
├── src/
│   ├── main/
│   │   ├── java/com/aiworldsimulation/
│   │   │   ├── Main.java
│   │   │   ├── model/     # Data models
│   │   │   ├── ai/        # Decision engine
│   │   │   ├── simulation/ # Simulation logic
│   │   │   ├── ui/        # Swing UI
│   │   │   ├── database/  # Persistence
│   │   │   ├── util/      # Utilities
│   │   │   └── exception/ # Custom exceptions
│   │   └── resources/
│   │       └── logging.properties
│   └── test/
│       └── java/com/aiworldsimulation/test/
│           ├── DecisionEngineTest.java
│           ├── ActionExecutorTest.java
│           └── NPCTest.java
├── target/
│   ├── classes/
│   ├── test-classes/
│   └── ai-world-simulation-1.0.0.jar
└── .gitignore
```

## Continuous Integration

For GitHub Actions CI/CD, create `.github/workflows/build.yml`:

```yaml
name: Build and Test

on: [push, pull_request]

jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v2
      - name: Set up JDK 17
        uses: actions/setup-java@v2
        with:
          java-version: '17'
      - name: Build with Maven
        run: mvn clean package
      - name: Run tests
        run: mvn test
```

## Distribution

### Create Standalone JAR
The pom.xml uses maven-shade-plugin to create a fat JAR:
```bash
mvn clean package
```

Result: `target/ai-world-simulation-1.0.0.jar` (ready to distribute)

### Create ZIP Distribution
```bash
mvn clean package
zip -r ai-world-sim-dist.zip target/ai-world-simulation-1.0.0.jar README.md ARCHITECTURE.md BUILD.md
```

## Performance Optimization

### Heap Size
```bash
java -Xms512m -Xmx1024m -jar target/ai-world-simulation-1.0.0.jar
```

### Reduce UI Refresh Rate
In `NPCListPanel.java` and `StatisticsPanel.java`, increase Timer delay.

### Disable Database Logging
In `DatabaseManager.java`, comment out logger calls.

## Troubleshooting Build Failures

### Clean Maven Cache
```bash
rm -rf ~/.m2/repository
mvn clean install
```

### Update Dependencies
```bash
mvn versions:display-dependency-updates
mvn dependency:tree
```

### Verbose Output
```bash
mvn clean package -X
```

---

For additional help, check the main README.md or ARCHITECTURE.md.
