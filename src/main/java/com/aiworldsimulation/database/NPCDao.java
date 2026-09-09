package com.aiworldsimulation.database;

import com.aiworldsimulation.model.*;
import java.sql.*;
import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Data Access Object for NPCs.
 * Handles all database operations for NPC persistence.
 */
public class NPCDao {
    private static final Logger logger = LoggerFactory.getLogger(NPCDao.class);

    /**
     * Save an NPC to the database.
     */
    public static void saveNPC(NPC npc) {
        String sql = "INSERT OR REPLACE INTO npcs (id, name, role, personality, x, y, health, energy, hunger, money, current_state, current_action, current_goal) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, npc.getId());
            pstmt.setString(2, npc.getName());
            pstmt.setString(3, npc.getRole().toString());
            pstmt.setString(4, npc.getPersonality().getType().toString());
            pstmt.setInt(5, npc.getX());
            pstmt.setInt(6, npc.getY());
            pstmt.setDouble(7, npc.getHealth());
            pstmt.setDouble(8, npc.getEnergy());
            pstmt.setDouble(9, npc.getHunger());
            pstmt.setDouble(10, npc.getMoney());
            pstmt.setString(11, npc.getCurrentState().toString());
            pstmt.setString(12, npc.getCurrentAction().toString());
            pstmt.setString(13, npc.getCurrentGoal());
            pstmt.executeUpdate();
            logger.debug("Saved NPC: {}", npc.getName());
        } catch (SQLException e) {
            logger.error("Error saving NPC: {}", npc.getName(), e);
        }
    }

    /**
     * Load an NPC from the database by ID.
     */
    public static NPC loadNPC(String id) {
        String sql = "SELECT * FROM npcs WHERE id = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String name = rs.getString("name");
                NPCRole role = NPCRole.valueOf(rs.getString("role"));
                Personality.PersonalityType personalityType = 
                    Personality.PersonalityType.valueOf(rs.getString("personality"));
                Personality personality = new Personality(personalityType);
                int x = rs.getInt("x");
                int y = rs.getInt("y");

                NPC npc = new NPC(id, name, role, personality, x, y);
                npc.setPosition(rs.getInt("x"), rs.getInt("y"));
                // Additional fields would be set here
                return npc;
            }
        } catch (SQLException e) {
            logger.error("Error loading NPC: {}", id, e);
        }
        return null;
    }

    /**
     * Load all NPCs from the database.
     */
    public static List<NPC> loadAllNPCs() {
        List<NPC> npcs = new ArrayList<>();
        String sql = "SELECT id FROM npcs";

        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                NPC npc = loadNPC(rs.getString("id"));
                if (npc != null) {
                    npcs.add(npc);
                }
            }
            logger.info("Loaded {} NPCs from database", npcs.size());
        } catch (SQLException e) {
            logger.error("Error loading NPCs", e);
        }
        return npcs;
    }

    /**
     * Delete an NPC from the database.
     */
    public static void deleteNPC(String id) {
        String sql = "DELETE FROM npcs WHERE id = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, id);
            pstmt.executeUpdate();
            logger.debug("Deleted NPC: {}", id);
        } catch (SQLException e) {
            logger.error("Error deleting NPC: {}", id, e);
        }
    }
}
