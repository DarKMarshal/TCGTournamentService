package com.darkmarshal.tournamentservice.DTO.Account;

public class AccountResponse {
    private int id;
    private String username;
    private int playerId;
    private String dateOfBirth;
    private String role;
    private String token;

    public AccountResponse(int id, String username, int playerId, String dateOfBirth, String role) {
        this.id = id;
        this.username = username;
        this.playerId = playerId;
        this.dateOfBirth = dateOfBirth;
        this.role = role;
    }

    public int getId() { return id; }
    public String getUsername() { return username; }
    public int getPlayerId() { return playerId; }
    public String getDateOfBirth() { return dateOfBirth; }
    public String getRole() { return role; }
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
}
