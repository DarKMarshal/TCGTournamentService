package com.darkmarshal.tournamentservice.Database.Repositories;

import com.darkmarshal.tournamentservice.Contracts.IPlayerRepository;
import com.darkmarshal.tournamentservice.Models.AgeDivision;
import com.darkmarshal.tournamentservice.Models.Player;
import com.darkmarshal.tournamentservice.Models.Result;
import com.darkmarshal.tournamentservice.DTO.Account.PersonalPage.PersonalPlayerDTO;
import com.darkmarshal.tournamentservice.DTO.Event.PlayerDTO;
import com.darkmarshal.tournamentservice.DTO.Leaderboard.LeaderboardDTO;
import org.springframework.lang.NonNull;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PlayerRepository implements IPlayerRepository {
    private final DataSource dataSource;

    public PlayerRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    private Connection getConn() throws SQLException {
        return dataSource.getConnection();
    }

    @Override
    public void savePlayer(@NonNull Player player) {
        String sql = "INSERT OR REPLACE INTO players (id, name, ageDivision, championship_points) VALUES (?, ?, ?, ?)";
        try (Connection conn = getConn();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, player.getId());
            pstmt.setString(2, player.getName());
            pstmt.setString(3, player.getAgeDivision() != null ? player.getAgeDivision().name() : null);
            pstmt.setInt(4, player.getChampionshipPoints());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Player getPlayerById(int id) {
        String sql = "SELECT * FROM players WHERE id = ?";
        try (Connection conn = getConn();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                Player player = new Player(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getInt("championship_points")
                );
                String ageDivisionStr = rs.getString("ageDivision");
                if (ageDivisionStr != null) {
                    player.setAgeDivision(AgeDivision.valueOf(ageDivisionStr));
                }
                return player;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Player getOrCreatePlayer(int id, String name) {
        Player existing = getPlayerById(id);
        if (existing == null) {
            // TODO: Add try catch for invalid ID entries
            Player newPlayer = new Player(id, name);
            savePlayer(newPlayer);
            return newPlayer;
        }
        return existing;
    }

    @Override
    public List<Player> getAllPlayers() {
        List<Player> players = new ArrayList<>();
        String sql = "SELECT * FROM players ORDER BY id";
        try (Connection conn = getConn();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Player player = new Player(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getInt("championship_points")
                );
                String ageDivisionStr = rs.getString("ageDivision");
                if (ageDivisionStr != null) {
                    player.setAgeDivision(AgeDivision.valueOf(ageDivisionStr));
                }
                players.add(player);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return players;
    }

    @Override
    public List<Player> getPlayersByAgeDivision(AgeDivision ageDivision) {
        List<Player> players = new ArrayList<>();
        String sql = "SELECT * FROM players WHERE ageDivision = ?";
        try (Connection conn = getConn();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, ageDivision.name());
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Player player = new Player(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getInt("championship_points")
                );
                player.setAgeDivision(ageDivision);
                players.add(player);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return players;
    }

    @Override
    public PersonalPlayerDTO findPersonalPlayer(int playerId) {
        String sql = "SELECT id, name, championship_points FROM players WHERE id = ?";
        try (Connection conn = getConn();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, playerId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new PersonalPlayerDTO(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getInt("championship_points")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<LeaderboardDTO> getLeaderboards() { 
        List<LeaderboardDTO> leaderboards = new ArrayList<>();
        for (AgeDivision ageDivision : AgeDivision.values()) {
            leaderboards.add(getLeaderboardByAgeDivision(ageDivision));
        }
        return leaderboards;
    }

    @Override
    public LeaderboardDTO getLeaderboardByAgeDivision(AgeDivision ageDivision) {
        List<PlayerDTO> playerDTOs = getPlayersByAgeDivision(ageDivision)
                .stream()
                .map(p -> new PlayerDTO(p.getName(), p.getChampionshipPoints()))
                .toList();
        return new LeaderboardDTO(ageDivision.name(), playerDTOs);
    }
    @Override
    public void updatePlayerChampionshipPoints(@NonNull List<Result> results) throws SQLException {
        String sql = "UPDATE players SET championship_points = championship_points + ? WHERE id = ?";
        try (Connection conn = getConn();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            for (Result result : results) {
                int pointsEarned = result.getChampionshipPointsEarned();
                if (pointsEarned > 0) {
                    pstmt.setInt(1, pointsEarned);
                    pstmt.setInt(2, result.getPlayer().getId());
                    pstmt.addBatch();
                }
            }
            pstmt.executeBatch();
        }
    }

    @Override
    public void updatePlayerAgeDivisions(@NonNull List<Result> results, @NonNull AgeDivision ageDivision) throws SQLException {
        String sql = "UPDATE players SET ageDivision = ? WHERE id = ?";
        try (Connection conn = getConn();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            for (Result result : results) {
                pstmt.setString(1, ageDivision.name());
                pstmt.setInt(2, result.getPlayer().getId());
                pstmt.addBatch();
            }
            pstmt.executeBatch();
        }
    }

    @Override
    public void deletePlayer(int id) {
        String sql = "DELETE FROM players WHERE id = ?";
        try (Connection conn = getConn();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
