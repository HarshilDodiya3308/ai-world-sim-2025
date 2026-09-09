package com.aiworldsimulation.ai;

import com.aiworldsimulation.model.*;
import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The core decision-making engine for NPCs.
 * Evaluates the current world state, available actions, NPC personality and memory,
 * calculates decision scores, and selects the best action.
 *
 * This is the "brain" of the AI system and demonstrates the explainable AI concept.
 */
public class DecisionEngine {
    private static final Logger logger = LoggerFactory.getLogger(DecisionEngine.class);

    private static final double HEALTH_CRITICAL = 20.0;
    private static final double HEALTH_LOW = 50.0;
    private static final double ENERGY_LOW = 30.0;
    private static final double HUNGER_HIGH = 70.0;

    /**
     * Main decision-making method.
     * Evaluates all possible actions and returns the best one with explanation.
     */
    public Decision makeDecision(NPC npc, List<NPC> nearbyNpcs, List<Location> visibleLocations, 
                                WorldState worldState) {
        Map<NPC.NPCAction, Double> actionScores = new HashMap<>();
        StringBuilder explanation = new StringBuilder();

        // Evaluate each possible action
        for (NPC.NPCAction action : NPC.NPCAction.values()) {
            double score = evaluateAction(action, npc, nearbyNpcs, visibleLocations, worldState);
            actionScores.put(action, score);
        }

        // Select action with highest score
        NPC.NPCAction bestAction = actionScores.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(NPC.NPCAction.WAIT);

        // Build explanation
        explanation.append(generateExplanation(npc, bestAction, actionScores));

        // Create and return decision
        String decisionId = UUID.randomUUID().toString();
        Decision decision = new Decision(decisionId, npc.getId(), bestAction, actionScores, 
                                        explanation.toString());

        logger.debug("NPC {} made decision: {} with score {}", npc.getName(), bestAction, 
                    actionScores.get(bestAction));

        return decision;
    }

    /**
     * Evaluate a specific action based on NPC state, environment, and personality.
     * Returns a score from 0 to 100.
     */
    private double evaluateAction(NPC.NPCAction action, NPC npc, List<NPC> nearbyNpcs,
                                 List<Location> visibleLocations, WorldState worldState) {
        double baseScore = 50.0;  // Neutral starting point
        double score = baseScore;

        // Factor in NPC vital signs
        score += evaluateHealthFactor(npc, action);
        score += evaluateEnergyFactor(npc, action);
        score += evaluateHungerFactor(npc, action);

        // Factor in environment
        score += evaluateEnvironmentFactor(npc, action, visibleLocations, worldState);

        // Factor in nearby NPCs
        score += evaluateInteractionFactor(npc, action, nearbyNpcs);

        // Factor in personality
        score *= npc.getPersonality().getInfluenceForAction(action.toString());

        // Factor in memory
        score += evaluateMemoryFactor(npc, action);

        return Math.max(0, Math.min(100, score));
    }

    private double evaluateHealthFactor(NPC npc, NPC.NPCAction action) {
        double health = npc.getHealth();
        
        if (health < HEALTH_CRITICAL) {
            // Critical health: prioritize healing and retreat
            if (action == NPC.NPCAction.HEAL || action == NPC.NPCAction.REST) return 30.0;
            if (action == NPC.NPCAction.RETREAT) return 25.0;
            if (action == NPC.NPCAction.ATTACK) return -40.0;
        } else if (health < HEALTH_LOW) {
            // Low health: encourage healing
            if (action == NPC.NPCAction.HEAL) return 20.0;
            if (action == NPC.NPCAction.REST) return 15.0;
            if (action == NPC.NPCAction.ATTACK) return -20.0;
        }
        return 0.0;
    }

    private double evaluateEnergyFactor(NPC npc, NPC.NPCAction action) {
        double energy = npc.getEnergy();
        
        if (energy < ENERGY_LOW) {
            // Low energy: prioritize rest
            if (action == NPC.NPCAction.REST) return 25.0;
            if (action == NPC.NPCAction.WAIT) return 10.0;
            if (action == NPC.NPCAction.ATTACK || action == NPC.NPCAction.EXPLORE) return -15.0;
        }
        return 0.0;
    }

    private double evaluateHungerFactor(NPC npc, NPC.NPCAction action) {
        double hunger = npc.getHunger();
        
        if (hunger > HUNGER_HIGH) {
            // High hunger: prioritize eating
            if (action == NPC.NPCAction.EAT) return 30.0;
            if (action == NPC.NPCAction.TRADE) return 10.0;  // To get food
            if (action == NPC.NPCAction.COLLECT_RESOURCE) return 10.0;  // Might find food
        }
        return 0.0;
    }

    private double evaluateEnvironmentFactor(NPC npc, NPC.NPCAction action, 
                                            List<Location> visibleLocations, 
                                            WorldState worldState) {
        double score = 0.0;
        
        // Find current location
        Location currentLoc = visibleLocations.stream()
                .filter(loc -> loc.getX() == npc.getX() && loc.getY() == npc.getY())
                .findFirst()
                .orElse(null);

        if (currentLoc != null) {
            // High danger: encourage retreat
            if (currentLoc.getDangerLevel() > 0.7) {
                if (action == NPC.NPCAction.RETREAT) score += 20.0;
                if (action == NPC.NPCAction.ATTACK) score -= 15.0;
            }

            // High resources: encourage collection
            if (currentLoc.getResourceLevel() > 0.6) {
                if (action == NPC.NPCAction.COLLECT_RESOURCE) score += 15.0;
                if (action == NPC.NPCAction.EXPLORE) score += 5.0;
            }
        }

        // Exploration bonus for undiscovered areas
        double undiscoveredPenalty = visibleLocations.stream()
                .filter(loc -> !loc.isDiscovered())
                .count();
        if (action == NPC.NPCAction.EXPLORE && undiscoveredPenalty > 0) {
            score += 10.0;
        }

        return score;
    }

    private double evaluateInteractionFactor(NPC npc, NPC.NPCAction action, List<NPC> nearbyNpcs) {
        double score = 0.0;

        if (action == NPC.NPCAction.TRADE && nearbyNpcs.size() > 0) {
            score += 10.0;
        }

        if (action == NPC.NPCAction.HELP) {
            // Check if any nearby NPCs need help
            boolean needyNpcNearby = nearbyNpcs.stream()
                    .anyMatch(n -> n.getHealth() < 50);
            if (needyNpcNearby) {
                score += 20.0;
            } else {
                score -= 10.0;
            }
        }

        return score;
    }

    private double evaluateMemoryFactor(NPC npc, NPC.NPCAction action) {
        double score = 0.0;
        List<Memory> memories = npc.getMemories();

        // Check for dangerous location memories
        if (action == NPC.NPCAction.RETREAT) {
            long dangerMemories = memories.stream()
                    .filter(m -> m.getType() == Memory.MemoryType.DANGER && m.isRelevant())
                    .count();
            score += dangerMemories * 5.0;
        }

        // Check for successful action memories
        if (action == NPC.NPCAction.EXPLORE) {
            long discoveryMemories = memories.stream()
                    .filter(m -> m.getType() == Memory.MemoryType.DISCOVERY && m.isRelevant())
                    .count();
            score += discoveryMemories * 3.0;
        }

        return score;
    }

    /**
     * Generate a human-readable explanation for why an action was chosen.
     */
    private String generateExplanation(NPC npc, NPC.NPCAction bestAction, 
                                      Map<NPC.NPCAction, Double> scores) {
        StringBuilder sb = new StringBuilder();
        sb.append("Decision: ").append(bestAction).append("\n");
        sb.append("Confidence: ").append(String.format("%.1f%%", calculateConfidence(bestAction, scores) * 100)).append("\n\n");
        
        sb.append("Reason:\n");

        // Add context-specific reasoning
        if (npc.getHealth() < HEALTH_CRITICAL) {
            sb.append("- Health is CRITICAL (").append(String.format("%.1f", npc.getHealth())).append(")\n");
        }
        if (npc.getEnergy() < ENERGY_LOW) {
            sb.append("- Energy is LOW (")
             .append(String.format("%.1f", npc.getEnergy())).append(")\n");
        }
        if (npc.getHunger() > HUNGER_HIGH) {
            sb.append("- Hunger is HIGH (")
             .append(String.format("%.1f", npc.getHunger())).append(")\n");
        }

        sb.append("- Personality: ").append(npc.getPersonality().getType()).append("\n");
        sb.append("- Role: ").append(npc.getRole().getName()).append("\n\n");

        sb.append("Action Scores:\n");
        scores.entrySet().stream()
                .sorted((a, b) -> b.getValue().compareTo(a.getValue()))
                .limit(5)
                .forEach(e -> sb.append(String.format("  %s: %.1f\n", e.getKey(), e.getValue())));

        return sb.toString();
    }

    private double calculateConfidence(NPC.NPCAction bestAction, Map<NPC.NPCAction, Double> scores) {
        double bestScore = scores.getOrDefault(bestAction, 0.0);
        double maxScore = scores.values().stream().mapToDouble(Double::doubleValue).max().orElse(1.0);
        return maxScore > 0 ? bestScore / maxScore : 0.5;
    }
}
