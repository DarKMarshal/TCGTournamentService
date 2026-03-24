package Database.Repositories;

import Models.AgeDivision;
import Models.Player;
import Models.Result;
import org.springframework.lang.NonNull;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PlayerRepository implements Services.Contracts.IPlayerRepository {
    private final Connection connection;

    public PlayerRepository(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void savePlayer(@NonNull Player player) {
        String sql = "INSERT OR REPLACE INTO players (id, name, ageDivision, championship_points) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
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
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
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
        try (Statement stmt = connection.createStatement();
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
    public void updatePlayerChampionshipPoints(@NonNull List<Result> results) throws SQLException {
        String sql = "UPDATE players SET championship_points = championship_points + ? WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
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
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
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
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
