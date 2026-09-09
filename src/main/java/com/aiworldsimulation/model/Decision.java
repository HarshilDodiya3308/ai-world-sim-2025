package com.aiworldsimulation.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Represents a decision made by an NPC.
 * Stores the decision, its reasoning, and the scores for all evaluated actions.
 */
public class Decision implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String id;
    private String npcId;
    private NPC.NPCAction chosenAction;
    private Map<NPC.NPCAction, Double> actionScores;
    private String explanation;
    private LocalDateTime timestamp;
    private boolean wasSuccessful;
    private double confidence;     // 0 to 1.0

    public Decision(String id, String npcId, NPC.NPCAction chosenAction,
                   Map<NPC.NPCAction, Double> actionScores, String explanation) {
        this.id = id;
        this.npcId = npcId;
        this.chosenAction = chosenAction;
        this.actionScores = new HashMap<>(actionScores);
        this.explanation = explanation;
        this.timestamp = LocalDateTime.now();
        this.wasSuccessful = false;
        this.confidence = calculateConfidence();
    }

    private double calculateConfidence() {
        if (actionScores.isEmpty()) return 0.5;
        double chosenScore = actionScores.getOrDefault(chosenAction, 0.0);
        double maxScore = actionScores.values().stream().mapToDouble(Double::doubleValue).max().orElse(1.0);
        return maxScore > 0 ? chosenScore / maxScore : 0.5;
    }

    // Getters
    public String getId() { return id; }
    public String getNpcId() { return npcId; }
    public NPC.NPCAction getChosenAction() { return chosenAction; }
    public Map<NPC.NPCAction, Double> getActionScores() { return new HashMap<>(actionScores); }
    public String getExplanation() { return explanation; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public boolean wasSuccessful() { return wasSuccessful; }
    public double getConfidence() { return confidence; }

    // Setters
    public void setWasSuccessful(boolean successful) { this.wasSuccessful = successful; }

    @Override
    public String toString() {
        return String.format("%s chose %s (confidence: %.1f%%) at %s",
                           npcId, chosenAction.toString(), confidence * 100, timestamp.toLocalTime());
    }
}
