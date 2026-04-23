package com.darkmarshal.tournamentservice.Database.Repositories;

import com.darkmarshal.tournamentservice.Models.Account;
import com.darkmarshal.tournamentservice.Models.Role;
import com.darkmarshal.tournamentservice.Contracts.IAccountRepository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AccountRepository implements IAccountRepository {
    private final DataSource dataSource;

    public AccountRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    private Connection getConn() throws SQLException {
        return dataSource.getConnection();
    }

    @Override
    public void saveAccount(Account account) {
        String sql = "INSERT INTO accounts (username, player_id, date_of_birth, password_hash, role) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = getConn();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, account.getUsername());
            pstmt.setInt(2, account.getPlayerId());
            pstmt.setString(3, account.getDateOfBirth());
            pstmt.setString(4, account.getPasswordHash());
            pstmt.setString(5, account.getRole().name());
            pstmt.executeUpdate();

            ResultSet keys = pstmt.getGeneratedKeys();
            if (keys.next()) {
                account.setId(keys.getInt(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Account getAccountById(int id) {
        String sql = "SELECT * FROM accounts WHERE id = ?";
        try (Connection conn = getConn();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return mapRow(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Account getAccountByUsername(String username) {
        String sql = "SELECT * FROM accounts WHERE username = ?";
        try (Connection conn = getConn();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return mapRow(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Account> getAllAccounts() {
        List<Account> accounts = new ArrayList<>();
        String sql = "SELECT * FROM accounts ORDER BY id";
        try (Connection conn = getConn();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                accounts.add(mapRow(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return accounts;
    }

    @Override
    public void updateAccountRole(int id, Role role) {
        String sql = "UPDATE accounts SET role = ? WHERE id = ?";
        try (Connection conn = getConn();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, role.name());
            pstmt.setInt(2, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteAccount(int id) {
        String sql = "DELETE FROM accounts WHERE id = ?";
        try (Connection conn = getConn();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean usernameExists(String username) {
        String sql = "SELECT COUNT(*) FROM accounts WHERE username = ?";
        try (Connection conn = getConn();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private Account mapRow(ResultSet rs) throws SQLException {
        return new Account(
                rs.getInt("id"),
                rs.getString("username"),
                rs.getInt("player_id"),
                rs.getString("date_of_birth"),
                rs.getString("password_hash"),
                Role.valueOf(rs.getString("role"))
        );
    }
}
