package Database.Repositories;

import Models.Result;
import org.springframework.lang.NonNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class ResultsRepository {
    private final Connection connection;

    public ResultsRepository(Connection connection) {
        this.connection = connection;
    }

    public void saveResults(String eventId, String ageDivision, @NonNull List<Result> results) throws SQLException {
        String sqlResult = "INSERT OR REPLACE INTO results (event_id, age_division, player_id, placement, points, match_points, opponent_win_percentage, opponent_opponent_win_percentage) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sqlResult)) {
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



}
