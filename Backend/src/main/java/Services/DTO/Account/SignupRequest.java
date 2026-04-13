package Services.DTO.Account;

public class SignupRequest {
    private String username;
    private int playerId;
    private String dateOfBirth;
    private String password;

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public int getPlayerId() { return playerId; }
    public void setPlayerId(int playerId) { this.playerId = playerId; }

    public String getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(String dateOfBirth) { this.dateOfBirth = dateOfBirth; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
