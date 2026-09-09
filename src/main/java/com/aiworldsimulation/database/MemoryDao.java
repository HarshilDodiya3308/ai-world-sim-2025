package com.aiworldsimulation.database;

import com.aiworldsimulation.model.*;
import java.sql.*;
import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Data Access Object for Memories.
 */
public class MemoryDao {
    private static final Logger logger = LoggerFactory.getLogger(MemoryDao.class);

    /**
     * Save a memory to the database.
     */
    public static void saveMemory(Memory memory) {
        String sql = "INSERT OR REPLACE INTO memories (id, npc_id, type, description, location_id, importance) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, memory.getId());
            pstmt.setString(2, memory.getNpcId());
            pstmt.setString(3, memory.getType().toString());
            pstmt.setString(4, memory.getDescription());
            pstmt.setInt(5, memory.getLocationId());
            pstmt.setDouble(6, memory.getImportance());
            pstmt.executeUpdate();
            logger.debug("Saved memory for NPC: {}", memory.getNpcId());
        } catch (SQLException e) {
            logger.error("Error saving memory", e);
        }
    }

    /**
     * Load all memories for an NPC.
     */
    public static List<Memory> loadMemoriesForNPC(String npcId) {
        List<Memory> memories = new ArrayList<>();
        String sql = "SELECT * FROM memories WHERE npc_id = ? ORDER BY timestamp DESC";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, npcId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Memory memory = new Memory(
                    rs.getString("id"),
                    rs.getString("npc_id"),
                    Memory.MemoryType.valueOf(rs.getString("type")),
                    rs.getString("description"),
                    rs.getInt("location_id"),
                    rs.getDouble("importance")
                );
                memories.add(memory);
            }
        } catch (SQLException e) {
            logger.error("Error loading memories for NPC: {}", npcId, e);
        }
        return memories;
    }
}
