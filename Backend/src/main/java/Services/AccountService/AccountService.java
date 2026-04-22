package Services.AccountService;

import Models.Account;
import Models.Role;
import Services.Contracts.IAccountRepository;
import Services.DTO.Account.AccountResponse;
import Services.DTO.Account.LoginRequest;
import Services.DTO.Account.SignupRequest;
import Services.Security.JwtUtil;
import org.mindrot.jbcrypt.BCrypt;

import java.util.List;

public class AccountService {

    public static AccountResponse signup(IAccountRepository accountRepository, SignupRequest request) {
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
        response.setToken(JwtUtil.generateToken(account.getId(), account.getUsername(), account.getRole().name()));
        return response;
    }

    public static AccountResponse login(IAccountRepository accountRepository, LoginRequest request) {
        Account account = accountRepository.getAccountByUsername(request.getUsername());
        if (account == null) {
            throw new IllegalArgumentException("Invalid username or password");
        }
        if (!BCrypt.checkpw(request.getPassword(), account.getPasswordHash())) {
            throw new IllegalArgumentException("Invalid username or password");
        }
        AccountResponse response = toResponse(account);
        response.setToken(JwtUtil.generateToken(account.getId(), account.getUsername(), account.getRole().name()));
        return response;
    }

    public static AccountResponse updateRole(IAccountRepository accountRepository, int accountId, Role role) {
        Account account = accountRepository.getAccountById(accountId);
        if (account == null) {
            throw new IllegalArgumentException("Account not found");
        }
        accountRepository.updateAccountRole(accountId, role);
        account.setRole(role);
        return toResponse(account);
    }

    public static List<AccountResponse> getAllAccounts(IAccountRepository accountRepository) {
        return accountRepository.getAllAccounts().stream()
                .map(AccountService::toResponse)
                .toList();
    }

    public static AccountResponse getAccountById(IAccountRepository accountRepository, int id) {
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
