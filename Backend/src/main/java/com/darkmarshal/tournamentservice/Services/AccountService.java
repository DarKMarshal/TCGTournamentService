package com.darkmarshal.tournamentservice.Services;

import com.darkmarshal.tournamentservice.Models.Account;
import com.darkmarshal.tournamentservice.Models.Role;
import com.darkmarshal.tournamentservice.Contracts.IAccountRepository;
import com.darkmarshal.tournamentservice.DTO.Account.AccountResponse;
import com.darkmarshal.tournamentservice.DTO.Account.LoginRequest;
import com.darkmarshal.tournamentservice.DTO.Account.SignupRequest;
import com.darkmarshal.tournamentservice.Security.JwtUtil;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AccountService {

    private final IAccountRepository accountRepository;
    private final JwtUtil jwtUtil;

    public AccountService(IAccountRepository accountRepository, JwtUtil jwtUtil) {
        this.accountRepository = accountRepository;
        this.jwtUtil = jwtUtil;
    }

    public AccountResponse signup(SignupRequest request) {
        if (request.getUsername() == null || request.getUsername().isBlank()) {
            throw new IllegalArgumentException("Username is required");
        }
        if (request.getPassword() == null || request.getPassword().isBlank()) {
            throw new IllegalArgumentException("Password is required");
        }
        if (request.getDateOfBirth() == null || request.getDateOfBirth().isBlank()) {
            throw new IllegalArgumentException("Date of birth is required");
        }
        if (request.getPlayerId() > 9999999) {
            throw new IllegalArgumentException("Player ID must be at most 7 digits");
        }
        if (accountRepository.usernameExists(request.getUsername())) {
            throw new IllegalArgumentException("Username already exists");
        }

        String passwordHash = BCrypt.hashpw(request.getPassword(), BCrypt.gensalt());
        Account account = new Account(
                request.getUsername(),
                request.getPlayerId(),
                request.getDateOfBirth(),
                passwordHash
        );
        accountRepository.saveAccount(account);
        AccountResponse response = toResponse(account);
        response.setToken(jwtUtil.generateToken(account.getId(), account.getUsername(), account.getRole().name()));
        return response;
    }

    public AccountResponse login(LoginRequest request) {
        Account account = accountRepository.getAccountByUsername(request.getUsername());
        if (account == null) {
            throw new IllegalArgumentException("Invalid username or password");
        }
        if (!BCrypt.checkpw(request.getPassword(), account.getPasswordHash())) {
            throw new IllegalArgumentException("Invalid username or password");
        }
        AccountResponse response = toResponse(account);
        response.setToken(jwtUtil.generateToken(account.getId(), account.getUsername(), account.getRole().name()));
        return response;
    }

    public AccountResponse updateRole(int accountId, Role role) {
        Account account = accountRepository.getAccountById(accountId);
        if (account == null) {
            throw new IllegalArgumentException("Account not found");
        }
        accountRepository.updateAccountRole(accountId, role);
        account.setRole(role);
        return toResponse(account);
    }

    public List<AccountResponse> getAllAccounts() {
        return accountRepository.getAllAccounts().stream()
                .map(AccountService::toResponse)
                .toList();
    }

    public AccountResponse getAccountById(int id) {
        Account account = accountRepository.getAccountById(id);
        if (account == null) {
            throw new IllegalArgumentException("Account not found");
        }
        return toResponse(account);
    }

    private static AccountResponse toResponse(Account account) {
        return new AccountResponse(
                account.getId(),
                account.getUsername(),
                account.getPlayerId(),
                account.getDateOfBirth(),
                account.getRole().name()
        );
    }
}
