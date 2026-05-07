package com.darkmarshal.tournamentservice.Database.Repositories;

import com.darkmarshal.tournamentservice.Contracts.IResultsRepository;
import com.darkmarshal.tournamentservice.Models.Player;
import com.darkmarshal.tournamentservice.Models.Result;
import com.darkmarshal.tournamentservice.DTO.Account.PersonalPage.PersonalResultDTO;
import org.springframework.lang.NonNull;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ResultsRepository implements IResultsRepository {
    private final DataSource dataSource;

    public ResultsRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    private Connection getConn() throws SQLException {
        return dataSource.getConnection();
    }

    @Override
    public void saveResults(String eventId, String ageDivision, @NonNull List<Result> results) throws SQLException {
        String sqlResult = "INSERT OR REPLACE INTO results (event_id, age_division, player_id, placement, points, match_points, opponent_win_percentage, opponent_opponent_win_percentage) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = getConn();
             PreparedStatement pstmt = conn.prepareStatement(sqlResult)) {
            for (Result res : results) {
                pstmt.setString(1, eventId);
                pstmt.setString(2, ageDivision);
                pstmt.setInt(3, res.getPlayer().getId());
                pstmt.setInt(4, res.getPlacement());
                pstmt.setInt(5, res.getChampionshipPointsEarned());
                pstmt.setInt(6, res.getMatchPoints());
                pstmt.setDouble(7, res.getOpponentWinPercentage());
                pstmt.setDouble(8, res.getOpponentOpponentWinPercentage());
                pstmt.addBatch();
            }
            pstmt.executeBatch();
        }
    }

    /**
     * Returns all results for a given event and age division, with Player objects hydrated.
     */
    @Override
    public List<Result> getResultsByEventAndDivision(String eventId, String ageDivision) {
        List<Result> results = new ArrayList<>();
        String sql = "SELECT r.placement, r.points, r.match_points, " +
                "r.opponent_win_percentage, r.opponent_opponent_win_percentage, " +
                "p.id AS player_id, p.name AS player_name, p.championship_points " +
                "FROM results r " +
                "JOIN players p ON r.player_id = p.id " +
                "WHERE r.event_id = ? AND r.age_division = ? " +
                "ORDER BY r.placement ASC";
        try (Connection conn = getConn();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, eventId);
            pstmt.setString(2, ageDivision);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Player player = new Player(
                        rs.getInt("player_id"),
                        rs.getString("player_name"),
                        rs.getInt("championship_points")
                );
                Result result = new Result(
                        player,
                        rs.getInt("placement"),
                        rs.getInt("match_points"),
                        rs.getDouble("opponent_win_percentage"),
                        rs.getDouble("opponent_opponent_win_percentage")
                );
                result.setChampionshipPointsEarned(rs.getInt("points"));
                results.add(result);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return results;
    }

    @Override
    public List<PersonalResultDTO> findResultsByPlayerId(int playerId) {
        List<PersonalResultDTO> results = new ArrayList<>();
        String sql = "SELECT r.event_id, e.name AS event_name, r.age_division, r.placement, r.points " +
                "FROM results r " +
                "JOIN events e ON r.event_id = e.id " +
                "WHERE r.player_id = ? " +
                "ORDER BY e.name, r.age_division";
        try (Connection conn = getConn();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, playerId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                results.add(new PersonalResultDTO(
                        rs.getString("event_id"),
                        rs.getString("event_name"),
                        rs.getString("age_division"),
                        rs.getInt("placement"),
                        rs.getInt("points")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return results;
    }
}