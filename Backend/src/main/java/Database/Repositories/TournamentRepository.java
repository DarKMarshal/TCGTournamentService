package Database.Repositories;

import Models.*;
import org.springframework.lang.NonNull;

import java.sql.*;
import java.util.*;

public class TournamentRepository {
    private final Connection connection;

    public TournamentRepository(Connection connection) {
        this.connection = connection;
    }

    public void saveTournament(@NonNull Tournament tournament) {
        try {
            connection.setAutoCommit(false);

            // 1. Save Tournament
            String sqlTournament = "INSERT OR REPLACE INTO tournaments (id, name) VALUES (?, ?)";
            try (PreparedStatement pstmt = connection.prepareStatement(sqlTournament)) {
                pstmt.setInt(1, tournament.getId());
                pstmt.setString(2, tournament.getName());
                pstmt.executeUpdate();
            }

            // 2. Clear old divisions (Cascade will handle results)
            String sqlDeleteDiv = "DELETE FROM divisions WHERE tournament_id = ?";
            try (PreparedStatement pstmt = connection.prepareStatement(sqlDeleteDiv)) {
                pstmt.setInt(1, tournament.getId());
                pstmt.executeUpdate();
            }

            // 3. Save Divisions and Results
            for (Map.Entry<AgeDivision, Tournament.DivisionData> entry : tournament.getDivisions().entrySet()) {
                saveDivision(tournament.getId(), entry.getKey(), entry.getValue());
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
    private void saveDivision(int tournamentId, @NonNull AgeDivision ageDiv, @NonNull Tournament.DivisionData data) throws SQLException {
        String sqlDiv = "INSERT INTO divisions (tournament_id, age_division, tournament_type) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sqlDiv, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, tournamentId);
            pstmt.setString(2, ageDiv.name());
            pstmt.setString(3, data.getTournamentType());
            pstmt.executeUpdate();

            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    int divisionId = rs.getInt(1);
                    saveResults(divisionId, data.getResults());
                }
            }
        }
    }

    private void saveResults(int divisionId, @NonNull List<Result> results) throws SQLException {
        String sqlResult = "INSERT INTO results (division_id, player_id, placement, points, match_points, opponent_win_percentage, opponent_opponent_win_percentage) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sqlResult)) {
            for (Result res : results) {
                pstmt.setInt(1, divisionId);
                pstmt.setInt(2, res.getPlayer().getId());
                pstmt.setInt(3, res.getPlacement());
                pstmt.setInt(4, res.getPoints());
                pstmt.setInt(5, res.getMatchPoints());
                pstmt.setDouble(6, res.getOpponentWinPercentage());
                pstmt.setDouble(7, res.getOpponentOpponentWinPercentage());
                pstmt.addBatch();
            }
            pstmt.executeBatch();
        }
    }

    public void deleteTournament(int id) {
        String sql = "DELETE FROM tournaments WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
