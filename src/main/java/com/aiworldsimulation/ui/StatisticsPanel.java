package com.aiworldsimulation.ui;

import com.aiworldsimulation.simulation.*;
import com.aiworldsimulation.model.*;
import javax.swing.*;
import java.awt.*;
import java.util.*;

/**
 * Panel displaying statistics about the simulation.
 */
public class StatisticsPanel extends JPanel {
    private JLabel statsLabel;
    private SimulationWorld world;
    private SimulationManager simulationManager;

    public StatisticsPanel(SimulationWorld world, SimulationManager simulationManager) {
        this.world = world;
        this.simulationManager = simulationManager;
        setLayout(new BorderLayout(5, 5));
        setBackground(new Color(20, 20, 30));
        setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(100, 150, 200)),
                "Statistics", javax.swing.border.TitledBorder.LEFT, javax.swing.border.TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 12), new Color(100, 150, 200)));

        statsLabel = new JLabel();
        statsLabel.setForeground(new Color(200, 200, 220));
        statsLabel.setFont(new Font("Courier New", Font.PLAIN, 11));
        statsLabel.setVerticalAlignment(SwingConstants.TOP);

        add(new JScrollPane(statsLabel), BorderLayout.CENTER);

        // Update statistics every 1 second
        Timer updateTimer = new Timer(1000, e -> updateStatistics());
        updateTimer.start();
    }

    public void updateStatistics() {
        SwingUtilities.invokeLater(() -> {
            StringBuilder sb = new StringBuilder();
            sb.append("<html>");
            sb.append("<b>World Statistics</b><br/>");
            sb.append("<br/>");
            sb.append("<b>Population:</b><br/>");
            sb.append("Total NPCs: ").append(world.getNPCs().size()).append("<br/>");
            long aliveNPCs = world.getNPCs().stream().filter(NPC::isAlive).count();
            sb.append("Alive: ").append(aliveNPCs).append("<br/>");
            sb.append("<br/>");

            sb.append("<b>Simulation:</b><br/>");
            sb.append("Step Count: ").append(simulationManager.getStepCount()).append("<br/>");
            sb.append("State: ").append(simulationManager.getState()).append("<br/>");
            sb.append("Speed: ").append(simulationManager.getSimulationSpeed()).append(" ms/step<br/>");
            sb.append("<br/>");

            sb.append("<b>Average Stats:</b><br/>");
            sb.append(String.format("Health: %.1f%%<br/>", world.getAverageHealth()));
            sb.append(String.format("Energy: %.1f%%<br/>", world.getAverageEnergy()));
            sb.append("<br/>");

            sb.append("<b>Resources:</b><br/>");
            sb.append(String.format("Total Money: $%.1f<br/>", world.getTotalMoney()));
            sb.append("Locations: ").append(world.getLocations().size()).append("<br/>");
            sb.append("<br/>");

            sb.append("<b>NPC States:</b><br/>");
            Map<NPC.NPCState, Long> stateCount = new HashMap<>();
            for (NPC npc : world.getNPCs()) {
                stateCount.merge(npc.getCurrentState(), 1L, Long::sum);
            }
            for (NPC.NPCState state : NPC.NPCState.values()) {
                sb.append(state).append(": ").append(stateCount.getOrDefault(state, 0L)).append("<br/>");
            }
            sb.append("<br/>");

            sb.append("<b>Most Common Actions:</b><br/>");
            Map<NPC.NPCAction, Long> actionCount = new HashMap<>();
            for (NPC npc : world.getNPCs()) {
                if (!npc.getDecisionHistory().isEmpty()) {
                    Decision lastDecision = npc.getDecisionHistory().get(npc.getDecisionHistory().size() - 1);
                    actionCount.merge(lastDecision.getChosenAction(), 1L, Long::sum);
                }
            }
            actionCount.entrySet().stream()
                    .sorted((a, b) -> b.getValue().compareTo(a.getValue()))
                    .limit(5)
                    .forEach(e -> sb.append(e.getKey()).append(": ").append(e.getValue()).append("<br/>"));

            sb.append("</html>");
            statsLabel.setText(sb.toString());
        });
    }
}
