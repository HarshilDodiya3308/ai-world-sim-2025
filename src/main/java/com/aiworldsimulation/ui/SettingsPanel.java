package com.aiworldsimulation.ui;

import javax.swing.*;
import java.awt.*;

/**
 * Settings panel for configuring simulation parameters.
 */
public class SettingsPanel extends JDialog {
    public SettingsPanel(Frame parent) {
        super(parent, "Settings", true);
        setSize(400, 300);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new GridLayout(5, 2, 10, 10));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        contentPanel.setBackground(new Color(20, 20, 30));

        // Settings components
        JLabel speedLabel = new JLabel("Simulation Speed:");
        speedLabel.setForeground(new Color(200, 200, 220));
        JSlider speedSlider = new JSlider(10, 5000, 500);
        speedSlider.setBackground(new Color(20, 20, 30));

        JLabel worldSizeLabel = new JLabel("World Size: 200x200");
        worldSizeLabel.setForeground(new Color(200, 200, 220));

        JLabel npcCountLabel = new JLabel("Initial NPCs: 8");
        npcCountLabel.setForeground(new Color(200, 200, 220));

        JLabel dangerLabel = new JLabel("Initial Danger: 0.3");
        dangerLabel.setForeground(new Color(200, 200, 220));

        JButton closeBtn = new JButton("Close");
        closeBtn.setBackground(new Color(40, 80, 120));
        closeBtn.setForeground(new Color(200, 200, 220));
        closeBtn.addActionListener(e -> dispose());

        contentPanel.add(speedLabel);
        contentPanel.add(speedSlider);
        contentPanel.add(worldSizeLabel);
        contentPanel.add(new JLabel());
        contentPanel.add(npcCountLabel);
        contentPanel.add(new JLabel());
        contentPanel.add(dangerLabel);
        contentPanel.add(new JLabel());
        contentPanel.add(new JLabel());
        contentPanel.add(closeBtn);

        add(contentPanel);
    }
}
