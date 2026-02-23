package Database.Repositories;

import Models.*;
import org.springframework.lang.NonNull;

import java.sql.*;
import java.util.*;

public class EventRepository implements Services.Contracts.IEventRepository {
    private final Connection connection;
    private final ResultsRepository resultsRepository;
    private final PlayerRepository playerRepository;
    private final TournamentRepository tournamentRepository;

    public EventRepository(Connection connection) {
        this.connection = connection;
        this.resultsRepository = new ResultsRepository(connection);
        this.playerRepository = new PlayerRepository(connection);
        this.tournamentRepository = new TournamentRepository(connection);

    }

    @Override
    public void saveEvent(@NonNull Event event) {
        try {
            connection.setAutoCommit(false);

            // 1. Save Event
            String sqlEvent = "INSERT OR REPLACE INTO events (id, name, uploader_id) VALUES (?, ?, ?)";
            try (PreparedStatement pstmt = connection.prepareStatement(sqlEvent)) {
                pstmt.setString(1, event.getId());
                pstmt.setString(2, event.getName());
                pstmt.setInt(3, event.getUploaderID());
                pstmt.executeUpdate();
            }

            // 2. Clear old divisions (Cascade will handle results)
            String sqlDeleteDiv = "DELETE FROM tournaments WHERE event_id = ?";
            try (PreparedStatement pstmt = connection.prepareStatement(sqlDeleteDiv)) {
                pstmt.setString(1, event.getId());
                pstmt.executeUpdate();
            }

            // 3. Save Divisions and Results
            for (Tournament tournament : event.getDivisions()){
                tournamentRepository.saveTournamentDivision(event.getId(), tournament);
                resultsRepository.saveResults(event.getId(), tournament.getAgeDivision().toString(), tournament.getResults());
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

    public Event getEventById(String id) {
        return null;
    }

    @Override
    public void deleteEvent(String id) {
        String sql = "DELETE FROM events WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
