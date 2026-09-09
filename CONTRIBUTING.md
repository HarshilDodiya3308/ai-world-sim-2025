# Contributing to AI World Simulation Engine

## Code of Conduct

Be respectful and constructive. This project welcomes contributions from everyone.

## How to Contribute

### Reporting Bugs

1. Check if the bug already exists in Issues
2. Create a new Issue with:
   - Clear title describing the bug
   - Steps to reproduce
   - Expected vs actual behavior
   - Environment (OS, Java version, etc.)
   - Screenshots if applicable

### Suggesting Enhancements

1. Use Issues to suggest features
2. Clearly describe the feature
3. Explain use cases and benefits
4. Provide examples if possible

### Pull Requests

1. **Fork** the repository
2. **Create a feature branch**: `git checkout -b feature/my-feature`
3. **Commit changes**: `git commit -am 'Add my feature'`
4. **Push to branch**: `git push origin feature/my-feature`
5. **Submit Pull Request** with:
   - Clear description of changes
   - Reference to related Issues
   - Screenshots for UI changes
   - Test results

## Development Setup

```bash
# Clone your fork
git clone https://github.com/YOUR_USERNAME/ai-world-sim-2025.git
cd ai-world-sim-2025

# Add upstream remote
git remote add upstream https://github.com/HarshilDodiya3308/ai-world-sim-2025.git

# Create feature branch
git checkout -b feature/my-feature

# Build and test
mvn clean test
mvn exec:java -Dexec.mainClass="com.aiworldsimulation.Main"
```

## Code Style

- Use meaningful variable names
- Add Javadoc comments for public methods
- Follow existing formatting conventions
- Keep methods focused and small
- Write unit tests for new functionality

## Testing

```bash
# Run all tests
mvn test

# Run specific test
mvn test -Dtest=DecisionEngineTest

# Run with verbose output
mvn test -X
```

## Commit Messages

- Start with capital letter
- Use imperative mood ("Add feature" not "Added feature")
- Reference issues: "Fix #123"
- Keep to one logical change per commit

Example:
```
Add new PATROL action for Guard NPCs

Implements patrol behavior that allows Guard role NPCs to systematically
move between waypoints. Includes decision engine scoring for Guard personality.

Fixes #45
```

## Areas for Contribution

- **Features**: New NPC actions, personality types, world events
- **Performance**: Optimization of decision engine, rendering
- **Testing**: Additional unit tests, integration tests
- **Documentation**: Improving guides, API docs, tutorials
- **UI/UX**: Improving visualization, adding new panels
- **Database**: Adding persistence features, optimization

## Questions?

Feel free to open an Issue or ask in discussions.

Thank you for contributing! 🎉
