package Services.Network;

import Models.Role;
import Services.AccountService.AccountService;
import Services.Contracts.IAccountRepository;
import Services.DTO.Account.AccountResponse;
import Services.DTO.Account.LoginRequest;
import Services.DTO.Account.RoleUpdateRequest;
import Services.DTO.Account.SignupRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/accounts")
@CrossOrigin(origins = "*")
public class AccountController {

    private final IAccountRepository accountRepository;

    public AccountController(IAccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody SignupRequest request) {
        try {
            AccountResponse response = AccountService.signup(accountRepository, request);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            AccountResponse response = AccountService.login(accountRepository, request);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<List<AccountResponse>> getAllAccounts() {
        return ResponseEntity.ok(AccountService.getAllAccounts(accountRepository));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getAccount(@PathVariable int id) {
        try {
            return ResponseEntity.ok(AccountService.getAccountById(accountRepository, id));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}/role")
    public ResponseEntity<?> updateRole(@PathVariable int id, @RequestBody RoleUpdateRequest request) {
        try {
            Role role = Role.valueOf(request.getRole());
            AccountResponse response = AccountService.updateRole(accountRepository, id, role);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
