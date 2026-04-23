package com.darkmarshal.tournamentservice.Controllers;

import com.darkmarshal.tournamentservice.Models.Role;
import com.darkmarshal.tournamentservice.Services.AccountService;
import com.darkmarshal.tournamentservice.DTO.Account.AccountResponse;
import com.darkmarshal.tournamentservice.DTO.Account.LoginRequest;
import com.darkmarshal.tournamentservice.DTO.Account.RoleUpdateRequest;
import com.darkmarshal.tournamentservice.DTO.Account.SignupRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody SignupRequest request) {
        try {
            AccountResponse response = accountService.signup(request);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            AccountResponse response = accountService.login(request);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<List<AccountResponse>> getAllAccounts() {
        return ResponseEntity.ok(accountService.getAllAccounts());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getAccount(@PathVariable int id) {
        try {
            return ResponseEntity.ok(accountService.getAccountById(id));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}/role")
    public ResponseEntity<?> updateRole(@PathVariable int id, @RequestBody RoleUpdateRequest request) {
        try {
            Role role = Role.valueOf(request.getRole());
            AccountResponse response = accountService.updateRole(id, role);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
