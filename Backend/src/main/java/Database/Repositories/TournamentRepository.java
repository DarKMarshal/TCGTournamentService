package Database.Repositories;

import Models.Tournament;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class TournamentRepository {
    private final Connection connection;

    public TournamentRepository(Connection connection) {
        this.connection = connection;
    }

    public void saveTournamentDivision(String eventId, Tournament tournament) throws SQLException {
        String sqlDiv = "INSERT OR REPLACE INTO tournaments (event_id, age_division, tournament_type) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sqlDiv)) {
            pstmt.setString(1, eventId);
            pstmt.setString(2, tournament.getAgeDivision().toString());
            pstmt.setString(3, tournament.getTournamentType());
            pstmt.executeUpdate();
        }
    }

    public Tournament getTournamentDivision(String eventId) {
        return null;
    }

    public void getAllTournamentDivisions(String eventId){

    }
}
