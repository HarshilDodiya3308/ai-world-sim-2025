package com.aiworldsimulation.ui;

import com.aiworldsimulation.simulation.*;
import com.aiworldsimulation.model.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * Control panel with simulation controls and settings.
 */
public class ControlPanel extends JPanel {
    private SimulationManager simulationManager;
    private SimulationWorld world;
    private MainWindow mainWindow;
    private JButton startBtn, pauseBtn, stopBtn, stepBtn, resetBtn, settingsBtn;
    private JComboBox<String> speedCombo;
    private JLabel statusLabel;

    public ControlPanel(SimulationManager simulationManager, SimulationWorld world, MainWindow mainWindow) {
        this.simulationManager = simulationManager;
        this.world = world;
        this.mainWindow = mainWindow;

        setLayout(new FlowLayout(FlowLayout.LEFT, 10, 5));
        setBackground(new Color(20, 20, 30));
        setBorder(BorderFactory.createLineBorder(new Color(100, 150, 200), 2));

        // Status label
        statusLabel = new JLabel("Status: STOPPED");
        statusLabel.setForeground(new Color(100, 200, 255));
        statusLabel.setFont(new Font("Arial", Font.BOLD, 12));
        add(statusLabel);
        add(Box.createHorizontalStrut(20));

        // Control buttons
        startBtn = createButton("START", e -> simulationManager.start());
        pauseBtn = createButton("PAUSE", e -> simulationManager.pause());
        pauseBtn.setEnabled(false);
        stopBtn = createButton("STOP", e -> simulationManager.stop());
        stopBtn.setEnabled(false);
        stepBtn = createButton("STEP", e -> simulationManager.step());
        resetBtn = createButton("RESET", e -> simulationManager.reset());

        add(startBtn);
        add(pauseBtn);
        add(stopBtn);
        add(stepBtn);
        add(resetBtn);
        add(Box.createHorizontalStrut(20));

        // Speed control
        add(new JLabel("Speed:"));
        speedCombo = new JComboBox<>(new String[]{"Slow (2s)", "Normal (500ms)", "Fast (100ms)", "Very Fast (10ms)"});
        speedCombo.setBackground(new Color(40, 80, 120));
        speedCombo.setForeground(new Color(200, 200, 220));
        speedCombo.setSelectedIndex(1);
        speedCombo.addActionListener(e -> updateSpeed());
        add(speedCombo);

        add(Box.createHorizontalStrut(20));
        settingsBtn = createButton("SETTINGS", e -> showSettings());
        add(settingsBtn);
    }

    private JButton createButton(String text, javax.swing.AbstractAction action) {
        JButton btn = new JButton(text);
        btn.setBackground(new Color(40, 80, 120));
        btn.setForeground(new Color(200, 200, 220));
        btn.setFont(new Font("Arial", Font.BOLD, 11));
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createLineBorder(new Color(100, 150, 200), 1));
        btn.addActionListener(action);
        return btn;
    }

    private void updateSpeed() {
        int[] speeds = {2000, 500, 100, 10};
        int index = speedCombo.getSelectedIndex();
        simulationManager.setSimulationSpeed(speeds[index]);
    }

    private void showSettings() {
        JOptionPane.showMessageDialog(this,
                "Settings:\n" +
                "- Simulation Speed: Adjust using the Speed dropdown\n" +
                "- World Size: 200x200\n" +
                "- Current NPCs: " + world.getNPCs().size() + "\n\n" +
                "Use STEP button to advance one step at a time.\n" +
                "Use PAUSE/RESUME to control simulation.",
                "Settings", JOptionPane.INFORMATION_MESSAGE);
    }

    public void updateControlStates(SimulationManager.SimulationState state) {
        switch (state) {
            case RUNNING:
                statusLabel.setText("Status: RUNNING");
                statusLabel.setForeground(new Color(100, 255, 100));
                startBtn.setEnabled(false);
                pauseBtn.setEnabled(true);
                stopBtn.setEnabled(true);
                stepBtn.setEnabled(false);
                break;
            case PAUSED:
                statusLabel.setText("Status: PAUSED");
                statusLabel.setForeground(new Color(255, 200, 100));
                startBtn.setText("RESUME");
                startBtn.setEnabled(true);
                pauseBtn.setEnabled(false);
                stopBtn.setEnabled(true);
                stepBtn.setEnabled(true);
                break;
            case STOPPED:
                statusLabel.setText("Status: STOPPED");
                statusLabel.setForeground(new Color(255, 100, 100));
                startBtn.setText("START");
                startBtn.setEnabled(true);
                pauseBtn.setEnabled(false);
                stopBtn.setEnabled(false);
                stepBtn.setEnabled(true);
                break;
        }
    }
}
