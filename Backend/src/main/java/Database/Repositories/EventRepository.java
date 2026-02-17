package Database.Repositories;

import Models.*;
import org.springframework.lang.NonNull;

import java.sql.*;
import java.util.*;

public class EventRepository {
    private final Connection connection;
    private final ResultsRepository resultsRepository;
    private final PlayerRepository playerRepository;

    public EventRepository(Connection connection) {
        this.connection = connection;
        this.resultsRepository = new ResultsRepository(connection);
        this.playerRepository = new PlayerRepository(connection);

    }

    public void saveEvent(@NonNull Event event) {
        try {
            connection.setAutoCommit(false);

            // 1. Save Event
            String sqlEvent = "INSERT OR REPLACE INTO tournaments (id, name) VALUES (?, ?)";
            try (PreparedStatement pstmt = connection.prepareStatement(sqlEvent)) {
                pstmt.setInt(1, event.getId());
                pstmt.setString(2, event.getName());
                pstmt.executeUpdate();
            }

            // 2. Clear old divisions (Cascade will handle results)
            String sqlDeleteDiv = "DELETE FROM divisions WHERE tournament_id = ?";
            try (PreparedStatement pstmt = connection.prepareStatement(sqlDeleteDiv)) {
                pstmt.setInt(1, event.getId());
                pstmt.executeUpdate();
            }

            // 3. Save Divisions and Results
            for (Tournament tournament : event.getDivisions()){
                saveTournamentDivision(event.getId(), tournament);
                playerRepository.updatePlayerChampionshipPoints(tournament.getResults());
            }

            connection.commit();
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    private void saveTournamentDivision(int eventId, Tournament tournament) throws SQLException {
        String sqlDiv = "INSERT INTO divisions (tournament_id, age_division, tournament_type) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sqlDiv)) {
            pstmt.setInt(1, eventId);
            pstmt.setString(2, tournament.getAgeDivision().toString());
            pstmt.setString(3, tournament.getTournamentType());
            pstmt.executeUpdate();
        }
    }

        public void deleteEvent(int id) {
        String sql = "DELETE FROM tournaments WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
