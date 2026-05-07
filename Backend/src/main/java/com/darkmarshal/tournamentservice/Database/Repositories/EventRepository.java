package com.darkmarshal.tournamentservice.Database.Repositories;

import com.darkmarshal.tournamentservice.Contracts.IEventRepository;
import com.darkmarshal.tournamentservice.Contracts.IPlayerRepository;
import com.darkmarshal.tournamentservice.Contracts.IResultsRepository;
import com.darkmarshal.tournamentservice.Contracts.ITournamentRepository;
import com.darkmarshal.tournamentservice.Models.*;
import com.darkmarshal.tournamentservice.DTO.Account.PersonalPage.PersonalEventDTO;
import org.springframework.lang.NonNull;

import javax.sql.DataSource;
import java.sql.*;
import java.util.*;

public class EventRepository implements IEventRepository {
    private final DataSource dataSource;
    private final IResultsRepository resultsRepository;
    private final IPlayerRepository playerRepository;
    private final ITournamentRepository tournamentRepository;

    public EventRepository(DataSource dataSource,
                           IResultsRepository resultsRepository,
                           IPlayerRepository playerRepository,
                           ITournamentRepository tournamentRepository) {
        this.dataSource = dataSource;
        this.resultsRepository = resultsRepository;
        this.playerRepository = playerRepository;
        this.tournamentRepository = tournamentRepository;
    }

    private Connection getConn() throws SQLException {
        return dataSource.getConnection();
    }

    @Override
    public void saveEvent(@NonNull Event event) {
        try (Connection conn = getConn()) {
            conn.setAutoCommit(false);

            try {

                // 1. Save Event
                String sqlEvent = "INSERT OR REPLACE INTO events (id, name, uploader_id) VALUES (?, ?, ?)";
                try (PreparedStatement pstmt = conn.prepareStatement(sqlEvent)) {
                    pstmt.setString(1, event.getId());
                    pstmt.setString(2, event.getName());
                    pstmt.setInt(3, event.getUploaderID());
                    pstmt.executeUpdate();
                }

                // 2. Clear old divisions (Cascade will handle results)
                String sqlDeleteDiv = "DELETE FROM tournaments WHERE event_id = ?";
                try (PreparedStatement pstmt = conn.prepareStatement(sqlDeleteDiv)) {
                    pstmt.setString(1, event.getId());
                    pstmt.executeUpdate();
                }

                // 3. Save Divisions and Results
                for (Tournament tournament : event.getDivisions()) {
                    tournamentRepository.saveTournamentDivision(event.getId(), tournament);
                    resultsRepository.saveResults(event.getId(), tournament.getAgeDivision().toString(), tournament.getResults());
                    playerRepository.updatePlayerChampionshipPoints(tournament.getResults());
                    playerRepository.updatePlayerAgeDivisions(tournament.getResults(), tournament.getAgeDivision());
                }

                conn.commit();
            } catch (SQLException e) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
                e.printStackTrace();
            } finally {
                try {
                    conn.setAutoCommit(true);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();}
        }

    public Event getEventById(String id) {
        Event event = null;
        String sql = "SELECT * FROM events WHERE id = ?";
        try (Connection conn = getConn();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                event = new Event(
                        rs.getString("id"),
                        rs.getString("name"),
                        rs.getInt("uploader_id"),
                        tournamentRepository.findAllDivisions(id).stream()
                                .map(div -> {
                                    String stringAgeDivision = div[0];
                                    String tournamentType = div[1];
                                    List<Result> results = resultsRepository.getResultsByEventAndDivision(id, stringAgeDivision);
                                    AgeDivision ageDivision = AgeDivision.valueOf(stringAgeDivision);
                                    return new Tournament(ageDivision, tournamentType, results);
                                })
                                .toList()
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return event;
    }

    @Override
    public void deleteEvent(String id) {
        String sql = "DELETE FROM events WHERE id = ?";
        try (Connection conn = getConn();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<PersonalEventDTO> findEventsByUploaderId(int uploaderId) {
        List<PersonalEventDTO> events = new ArrayList<>();
        String sql = "SELECT id, name FROM events WHERE uploader_id = ?";
        try (Connection conn = getConn();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, uploaderId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                events.add(new PersonalEventDTO(rs.getString("id"), rs.getString("name")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return events;
    }

    /**
     * Returns all events with empty division lists (lightweight, for event list display).
     */
    public List<Event> getAllEvents() {
        List<Event> events = new ArrayList<>();
        String sql = "SELECT id, name, uploader_id FROM events ORDER BY name";
        try (Connection conn = getConn();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                events.add(new Event(
                        rs.getString("id"),
                        rs.getString("name"),
                        rs.getInt("uploader_id"),
                        new ArrayList<>()
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return events;
    }
}
