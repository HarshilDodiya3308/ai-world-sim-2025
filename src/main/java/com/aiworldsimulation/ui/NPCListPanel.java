package com.aiworldsimulation.ui;

import com.aiworldsimulation.simulation.*;
import com.aiworldsimulation.model.*;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import java.awt.*;
import java.util.*;

/**
 * Panel displaying the list of all NPCs with their vital statistics.
 */
public class NPCListPanel extends JPanel {
    private JList<String> npcList;
    private DefaultListModel<String> listModel;
    private JLabel statsLabel;
    private SimulationWorld world;
    private SimulationManager simulationManager;

    public NPCListPanel(SimulationWorld world, SimulationManager simulationManager) {
        this.world = world;
        this.simulationManager = simulationManager;
        setLayout(new BorderLayout(5, 5));
        setBackground(new Color(20, 20, 30));
        setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(100, 150, 200)),
                "NPCs", javax.swing.border.TitledBorder.LEFT, javax.swing.border.TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 12), new Color(100, 150, 200)));

        // NPC List
        listModel = new DefaultListModel<>();
        npcList = new JList<>(listModel);
        npcList.setBackground(new Color(30, 30, 40));
        npcList.setForeground(new Color(200, 200, 220));
        npcList.addListSelectionListener(this::onNPCSelected);

        add(new JScrollPane(npcList), BorderLayout.CENTER);

        // Stats label
        statsLabel = new JLabel();
        statsLabel.setForeground(new Color(150, 200, 255));
        statsLabel.setFont(new Font("Courier New", Font.PLAIN, 11));
        add(new JScrollPane(statsLabel), BorderLayout.SOUTH);

        // Update NPC list every 500ms
        Timer updateTimer = new Timer(500, e -> updateNPCList());
        updateTimer.start();
    }

    private void updateNPCList() {
        SwingUtilities.invokeLater(() -> {
            listModel.clear();
            for (NPC npc : world.getNPCs()) {
                String status = String.format("%s (%s) - HP: %.0f | NRG: %.0f",
                        npc.getName(), npc.getRole().getName(), npc.getHealth(), npc.getEnergy());
                listModel.addElement(status);
            }
        });
    }

    private void onNPCSelected(ListSelectionEvent e) {
        int index = npcList.getSelectedIndex();
        if (index >= 0 && index < world.getNPCs().size()) {
            NPC selected = world.getNPCs().get(index);
            displayNPCStats(selected);
        }
    }

    private void displayNPCStats(NPC npc) {
        StringBuilder sb = new StringBuilder();
        sb.append("<html>");
        sb.append("<b>").append(npc.getName()).append(" (ID: ").append(npc.getId()).append(")</b><br/>");
        sb.append("Role: ").append(npc.getRole().getName()).append("<br/>");
        sb.append("Personality: ").append(npc.getPersonality().getType()).append("<br/>");
        sb.append("<br/>");
        sb.append("<b>Vitals:</b><br/>");
        sb.append(String.format("Health: %.1f%%<br/>", npc.getHealth()));
        sb.append(String.format("Energy: %.1f%%<br/>", npc.getEnergy()));
        sb.append(String.format("Hunger: %.1f%%<br/>", npc.getHunger()));
        sb.append("<br/>");
        sb.append("<b>Resources:</b><br/>");
        sb.append(String.format("Money: $%.1f<br/>", npc.getMoney()));
        sb.append("Inventory: ").append(npc.getInventory().size()).append(" items<br/>");
        sb.append("<br/>");
        sb.append("<b>State:</b><br/>");
        sb.append("Position: (").append(npc.getX()).append(", ").append(npc.getY()).append(")<br/>");
        sb.append("State: ").append(npc.getCurrentState()).append("<br/>");
        sb.append("Action: ").append(npc.getCurrentAction()).append("<br/>");
        sb.append("Goal: ").append(npc.getCurrentGoal()).append("<br/>");
        sb.append("</html>");
        statsLabel.setText(sb.toString());
    }
}
