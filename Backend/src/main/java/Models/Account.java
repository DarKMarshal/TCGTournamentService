package Models;

public class Account {
    private int id;
    private String username;
    private int playerId;
    private String dateOfBirth;
    private String passwordHash;
    private Role role;

    public Account(int id, String username, int playerId, String dateOfBirth, String passwordHash, Role role) {
        this.id = id;
        this.username = username;
        this.playerId = playerId;
        this.dateOfBirth = dateOfBirth;
        this.passwordHash = passwordHash;
        this.role = role;
    }

    public Account(String username, int playerId, String dateOfBirth, String passwordHash) {
        this.username = username;
        this.playerId = playerId;
        this.dateOfBirth = dateOfBirth;
        this.passwordHash = passwordHash;
        this.role = Role.PLAYER;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public int getPlayerId() { return playerId; }
    public void setPlayerId(int playerId) { this.playerId = playerId; }

    public String getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(String dateOfBirth) { this.dateOfBirth = dateOfBirth; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }
}
