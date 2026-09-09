package com.aiworldsimulation.ui;

import com.aiworldsimulation.model.*;
import javax.swing.*;
import java.awt.*;

/**
 * Panel displaying the decision analysis for a selected NPC.
 */
public class DecisionInspectorPanel extends JPanel {
    private JTextArea decisionText;
    private JLabel selectedNPCLabel;

    public DecisionInspectorPanel() {
        setLayout(new BorderLayout(5, 5));
        setBackground(new Color(20, 20, 30));
        setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(100, 150, 200)),
                "Decision Inspector", javax.swing.border.TitledBorder.LEFT, javax.swing.border.TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 12), new Color(100, 150, 200)));

        // Header
        selectedNPCLabel = new JLabel("Select an NPC to inspect");
        selectedNPCLabel.setForeground(new Color(100, 200, 255));
        selectedNPCLabel.setFont(new Font("Arial", Font.BOLD, 14));
        add(selectedNPCLabel, BorderLayout.NORTH);

        // Decision text
        decisionText = new JTextArea();
        decisionText.setEditable(false);
        decisionText.setBackground(new Color(30, 30, 40));
        decisionText.setForeground(new Color(200, 200, 220));
        decisionText.setFont(new Font("Courier New", Font.PLAIN, 11));
        decisionText.setLineWrap(true);
        decisionText.setWrapStyleWord(true);
        decisionText.setText("Click on an NPC in the world to view its decision reasoning.\n\n" +
                "The decision engine evaluates:\n" +
                "- NPC health, energy, and hunger levels\n" +
                "- Environmental factors (danger, resources)\n" +
                "- Personality influence on action preference\n" +
                "- Relevant memories from past experiences\n" +
                "- Nearby NPCs and possible interactions\n\n" +
                "Each action receives a score (0-100), and the\n" +
                "NPC chooses the action with the highest score.");
        add(new JScrollPane(decisionText), BorderLayout.CENTER);
    }

    public void displayDecision(NPC npc) {
        if (npc == null) {
            selectedNPCLabel.setText("No NPC selected");
            decisionText.setText("Select an NPC to view its decisions");
            return;
        }

        selectedNPCLabel.setText(npc.getName() + " (" + npc.getRole().getName() + ")");

        StringBuilder sb = new StringBuilder();
        sb.append("=== CURRENT STATE ===\n\n");
        sb.append(String.format("Health: %.1f%%\n", npc.getHealth()));
        sb.append(String.format("Energy: %.1f%%\n", npc.getEnergy()));
        sb.append(String.format("Hunger: %.1f%%\n", npc.getHunger()));
        sb.append(String.format("Money: $%.1f\n\n", npc.getMoney()));

        sb.append("=== RECENT DECISIONS ===\n\n");
        java.util.List<Decision> recentDecisions = npc.getDecisionHistory();
        if (recentDecisions.isEmpty()) {
            sb.append("No decisions yet.\n");
        } else {
            for (int i = Math.max(0, recentDecisions.size() - 5); i < recentDecisions.size(); i++) {
                Decision decision = recentDecisions.get(i);
                sb.append(String.format("[%s] %s (Confidence: %.0f%%)\n",
                        decision.getTimestamp().toLocalTime(),
                        decision.getChosenAction(),
                        decision.getConfidence() * 100));
            }
        }

        sb.append("\n=== DECISION REASONING ===\n\n");
        if (!recentDecisions.isEmpty()) {
            Decision lastDecision = recentDecisions.get(recentDecisions.size() - 1);
            sb.append(lastDecision.getExplanation());
        } else {
            sb.append("No decisions have been made yet.");
        }

        sb.append("\n\n=== MEMORIES ===\n\n");
        java.util.List<Memory> memories = npc.getMemories();
        if (memories.isEmpty()) {
            sb.append("No memories.");
        } else {
            for (int i = Math.max(0, memories.size() - 5); i < memories.size(); i++) {
                Memory memory = memories.get(i);
                sb.append(String.format("[%s] %s: %s (Importance: %.0f%%)\n",
                        memory.getTimestamp().toLocalTime(),
                        memory.getType().getLabel(),
                        memory.getDescription(),
                        memory.getImportance() * 100));
            }
        }

        decisionText.setText(sb.toString());
        decisionText.setCaretPosition(0);
    }
}
