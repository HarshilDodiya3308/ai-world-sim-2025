package com.aiworldsimulation.database;

import com.aiworldsimulation.model.*;
import java.sql.*;
import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Data Access Object for Decisions.
 */
public class DecisionDao {
    private static final Logger logger = LoggerFactory.getLogger(DecisionDao.class);

    /**
     * Save a decision to the database.
     */
    public static void saveDecision(Decision decision) {
        String sql = "INSERT OR REPLACE INTO decisions (id, npc_id, action, explanation, confidence, was_successful) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, decision.getId());
            pstmt.setString(2, decision.getNpcId());
            pstmt.setString(3, decision.getChosenAction().toString());
            pstmt.setString(4, decision.getExplanation());
            pstmt.setDouble(5, decision.getConfidence());
            pstmt.setInt(6, decision.wasSuccessful() ? 1 : 0);
            pstmt.executeUpdate();
            logger.debug("Saved decision for NPC: {}", decision.getNpcId());
        } catch (SQLException e) {
            logger.error("Error saving decision", e);
        }
    }

    /**
     * Load all decisions for an NPC.
     */
    public static List<Decision> loadDecisionsForNPC(String npcId) {
        List<Decision> decisions = new ArrayList<>();
        String sql = "SELECT * FROM decisions WHERE npc_id = ? ORDER BY timestamp DESC LIMIT 50";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, npcId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                NPC.NPCAction action = NPC.NPCAction.valueOf(rs.getString("action"));
                Decision decision = new Decision(
                    rs.getString("id"),
                    rs.getString("npc_id"),
                    action,
                    new HashMap<>(),
                    rs.getString("explanation")
                );
                decision.setWasSuccessful(rs.getInt("was_successful") == 1);
                decisions.add(decision);
            }
        } catch (SQLException e) {
            logger.error("Error loading decisions for NPC: {}", npcId, e);
        }
        return decisions;
    }
}
