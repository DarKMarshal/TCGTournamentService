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

    public void saveResults(int divisionId, @NonNull List<Result> results) throws SQLException {
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

}
