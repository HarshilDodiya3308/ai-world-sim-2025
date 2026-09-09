package com.aiworldsimulation.ui;

import javax.swing.*;
import java.awt.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * Panel displaying real-time event logs from the simulation.
 */
public class EventLogPanel extends JPanel {
    private JTextArea eventText;
    private int eventCount = 0;
    private static final int MAX_EVENTS = 1000;

    public EventLogPanel() {
        setLayout(new BorderLayout(5, 5));
        setBackground(new Color(20, 20, 30));
        setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(100, 150, 200)),
                "Event Log", javax.swing.border.TitledBorder.LEFT, javax.swing.border.TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 12), new Color(100, 150, 200)));

        eventText = new JTextArea();
        eventText.setEditable(false);
        eventText.setBackground(new Color(30, 30, 40));
        eventText.setForeground(new Color(100, 200, 100));
        eventText.setFont(new Font("Courier New", Font.PLAIN, 10));
        eventText.setLineWrap(true);
        eventText.setWrapStyleWord(true);
        eventText.setText("[Simulation started]\n");

        add(new JScrollPane(eventText), BorderLayout.CENTER);
    }

    public void addEventLog(String message) {
        SwingUtilities.invokeLater(() -> {
            String timestamp = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
            String logEntry = String.format("[%s] %s\n", timestamp, message);
            eventText.append(logEntry);
            eventCount++;

            // Keep only recent events
            if (eventCount > MAX_EVENTS) {
                eventText.setText("");
                eventCount = 0;
                eventText.append(logEntry);
            }

            // Auto-scroll to bottom
            eventText.setCaretPosition(eventText.getDocument().getLength());
        });
    }

    public void clearLog() {
        eventText.setText("");
        eventCount = 0;
    }
}
