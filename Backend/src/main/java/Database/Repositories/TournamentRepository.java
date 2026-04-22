package Database.Repositories;

import Models.Tournament;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TournamentRepository implements Services.Contracts.ITournamentRepository {
    private final Connection connection;
    private final DataSource dataSource;

    public TournamentRepository(Connection connection) {
        this.connection = connection;
        this.dataSource = null;
    }

    public TournamentRepository(DataSource dataSource) {
        this.dataSource = dataSource;
        this.connection = null;
    }

    private Connection getConn() throws SQLException {
        if (dataSource != null) {
            return dataSource.getConnection();
        } else {
            return ConnectionUtil.nonClosing(connection);
        }
    }

    @Override
    public void saveTournamentDivision(String eventId, Tournament tournament) throws SQLException {
        String sqlDiv = "INSERT OR REPLACE INTO tournaments (event_id, age_division, tournament_type) VALUES (?, ?, ?)";
        try (Connection conn = getConn();
             PreparedStatement pstmt = conn.prepareStatement(sqlDiv)) {
            pstmt.setString(1, eventId);
            pstmt.setString(2, tournament.getAgeDivision().toString());
            pstmt.setString(3, tournament.getTournamentType());
            pstmt.executeUpdate();
        }
    }

    public void saveTournamentDivision(Connection conn, String eventId, Tournament tournament) throws SQLException {
        String sqlDiv = "INSERT OR REPLACE INTO tournaments (event_id, age_division, tournament_type) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sqlDiv)) {
            pstmt.setString(1, eventId);
            pstmt.setString(2, tournament.getAgeDivision().toString());
            pstmt.setString(3, tournament.getTournamentType());
            pstmt.executeUpdate();
        }
    }

    @Override
    public Tournament getTournamentDivision(String eventId) {
        return null;
    }


    /**
     * Returns all divisions for a given event as [ageDivision, tournamentType] pairs.
     */
    @Override
    public List<String[]> findAllDivisions(String eventId) {
        List<String[]> divisions = new ArrayList<>();
        String sql = "SELECT age_division, tournament_type FROM tournaments WHERE event_id = ? ORDER BY age_division";
        try (Connection conn = getConn();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, eventId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                divisions.add(new String[]{
                        rs.getString("age_division"),
                        rs.getString("tournament_type")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return divisions;
    }
}