package Services.Network;

import Services.DTO.Account.PersonalPage.PersonalDataDTO;
import Services.DTO.Account.PersonalPage.PersonalEventDTO;
import Services.DTO.Account.PersonalPage.PersonalPlayerDTO;
import Services.DTO.Account.PersonalPage.PersonalResultDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/personal")
public class PersonalController {

    private final Connection connection;

    @Autowired
    public PersonalController(Connection connection) {
        this.connection = connection;
    }

    @GetMapping("/{playerId}")
    public ResponseEntity<?> getPersonalData(@PathVariable int playerId) {
        try {
            // 1. Look up the player
            PersonalPlayerDTO player = findPlayer(playerId);

            // 2. Get results for this player (empty list if player not found)
            List<PersonalResultDTO> results = player != null ? findResultsByPlayerId(playerId) : List.of();

            // 3. Get events uploaded by this player
            List<PersonalEventDTO> uploadedEvents = findEventsByUploaderId(playerId);

            return ResponseEntity.ok(new PersonalDataDTO(player, results, uploadedEvents));
        } catch (SQLException e) {
            return ResponseEntity.internalServerError().body(Map.of("error", "Database error"));
        }
    }

    private PersonalPlayerDTO findPlayer(int playerId) throws SQLException {
        String sql = "SELECT id, name, championship_points FROM players WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, playerId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new PersonalPlayerDTO(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getInt("championship_points")
                );
            }
        }
        return null;
    }

    private List<PersonalResultDTO> findResultsByPlayerId(int playerId) throws SQLException {
        List<PersonalResultDTO> results = new ArrayList<>();
        String sql = "SELECT r.event_id, e.name AS event_name, r.age_division, r.placement, r.points " +
                "FROM results r " +
                "JOIN events e ON r.event_id = e.id " +
                "WHERE r.player_id = ? " +
                "ORDER BY e.name, r.age_division";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
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
        }
        return results;
    }

    private List<PersonalEventDTO> findEventsByUploaderId(int uploaderId) throws SQLException {
        List<PersonalEventDTO> events = new ArrayList<>();
        String sql = "SELECT id, name FROM events WHERE uploader_id = ? ORDER BY name";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, uploaderId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                events.add(new PersonalEventDTO(
                        rs.getString("id"),
                        rs.getString("name")
                ));
            }
        }
        return events;
    }
}
