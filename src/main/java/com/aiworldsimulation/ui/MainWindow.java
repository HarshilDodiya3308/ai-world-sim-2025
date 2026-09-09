package com.aiworldsimulation.ui;

import com.aiworldsimulation.simulation.*;
import com.aiworldsimulation.model.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

/**
 * Main application window.
 * Orchestrates all UI panels and coordinates the simulation.
 */
public class MainWindow extends JFrame {
    private SimulationManager simulationManager;
    private SimulationWorld world;
    private WorldSimulationPanel simulationPanel;
    private NPCListPanel npcListPanel;
    private DecisionInspectorPanel decisionPanel;
    private EventLogPanel eventLogPanel;
    private StatisticsPanel statisticsPanel;
    private ControlPanel controlPanel;
    private SettingsPanel settingsPanel;

    public MainWindow() {
        setTitle("AI World Simulation Engine");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLocationRelativeTo(null);

        // Initialize simulation
        initializeSimulation();

        // Setup UI
        setupUI();

        setVisible(true);
    }

    private void initializeSimulation() {
        // Create world
        world = new SimulationWorld(200, 200);
        world.initializeDemoWorld();
        createDemoNPCs();

        // Create simulation manager
        simulationManager = new SimulationManager(world);
        simulationManager.setSimulationSpeed(500);
    }

    private void createDemoNPCs() {
        // Create 8 NPCs with different roles and personalities
        NPCRole[] roles = {NPCRole.GUARD, NPCRole.TRADER, NPCRole.EXPLORER, NPCRole.MEDIC,
                          NPCRole.FARMER, NPCRole.SCOUT, NPCRole.GUARD, NPCRole.EXPLORER};
        Personality.PersonalityType[] personalities = {
            Personality.PersonalityType.AGGRESSIVE,
            Personality.PersonalityType.GREEDY,
            Personality.PersonalityType.CURIOUS,
            Personality.PersonalityType.HELPFUL,
            Personality.PersonalityType.BALANCED,
            Personality.PersonalityType.DEFENSIVE,
            Personality.PersonalityType.AGGRESSIVE,
            Personality.PersonalityType.CURIOUS
        };
        String[] names = {"Alex", "Bob", "Charlie", "Diana", "Eve", "Frank", "Grace", "Henry"};

        for (int i = 0; i < 8; i++) {
            NPC npc = new NPC(
                "NPC_" + i,
                names[i],
                roles[i],
                new Personality(personalities[i]),
                50 + (i * 15),
                50 + (i * 10)
            );
            world.addNPC(npc);
        }
    }

    private void setupUI() {
        // Set dark futuristic theme
        UIManager.put("Panel.background", new Color(20, 20, 30));
        UIManager.put("Label.foreground", new Color(200, 200, 220));
        UIManager.put("Button.background", new Color(40, 80, 120));
        UIManager.put("Button.foreground", new Color(200, 200, 220));

        // Create main container
        JPanel mainContainer = new JPanel(new BorderLayout(5, 5));
        mainContainer.setBackground(new Color(20, 20, 30));
        mainContainer.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        // Top: Control Panel
        controlPanel = new ControlPanel(simulationManager, world, this);
        mainContainer.add(controlPanel, BorderLayout.NORTH);

        // Center: Split pane with simulation and inspection panels
        JSplitPane centerSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        centerSplit.setBackground(new Color(20, 20, 30));

        // Left: World Simulation Panel
        simulationPanel = new WorldSimulationPanel(world, simulationManager);
        centerSplit.setLeftComponent(new JScrollPane(simulationPanel));

        // Right: Inspection panels
        JTabbedPane rightTabs = new JTabbedPane();
        rightTabs.setBackground(new Color(20, 20, 30));
        rightTabs.setForeground(new Color(200, 200, 220));

        npcListPanel = new NPCListPanel(world, simulationManager);
        rightTabs.addTab("NPCs", npcListPanel);

        decisionPanel = new DecisionInspectorPanel();
        rightTabs.addTab("Decision Inspector", decisionPanel);

        eventLogPanel = new EventLogPanel();
        rightTabs.addTab("Event Log", eventLogPanel);

        statisticsPanel = new StatisticsPanel(world, simulationManager);
        rightTabs.addTab("Statistics", statisticsPanel);

        centerSplit.setRightComponent(rightTabs);
        centerSplit.setDividerLocation(700);
        mainContainer.add(centerSplit, BorderLayout.CENTER);

        // Connect listener
        simulationManager.addListener(new SimulationManager.SimulationListener() {
            @Override
            public void onSimulationStep(SimulationWorld world, long stepCount) {
                SwingUtilities.invokeLater(() -> {
                    simulationPanel.repaint();
                    statisticsPanel.updateStatistics();
                    eventLogPanel.addEventLog("Step: " + stepCount);
                });
            }

            @Override
            public void onSimulationStateChanged(SimulationManager.SimulationState newState) {
                SwingUtilities.invokeLater(() -> controlPanel.updateControlStates(newState));
            }

            @Override
            public void onEventOccurred(WorldEvent event) {
                SwingUtilities.invokeLater(() -> eventLogPanel.addEventLog(event.toString()));
            }
        });

        setContentPane(mainContainer);
    }

    public DecisionInspectorPanel getDecisionInspectorPanel() {
        return decisionPanel;
    }

    public EventLogPanel getEventLogPanel() {
        return eventLogPanel;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainWindow());
    }
}
