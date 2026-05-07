package com.darkmarshal.tournamentservice.DTO.Account;

public class RoleUpdateRequest {
    private int accountId;
    private String role;

    public int getAccountId() { return accountId; }
    public void setAccountId(int accountId) { this.accountId = accountId; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}
