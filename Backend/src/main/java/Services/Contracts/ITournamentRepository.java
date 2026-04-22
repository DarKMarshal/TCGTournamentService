package Services.Contracts;

import Models.Tournament;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface ITournamentRepository {
    void saveTournamentDivision(String eventId, Tournament tournament) throws SQLException;

    void saveTournamentDivision(Connection conn, String eventId, Tournament tournament) throws SQLException;

    Tournament getTournamentDivision(String eventId);

    List<String[]> findAllDivisions(String eventId);
}
