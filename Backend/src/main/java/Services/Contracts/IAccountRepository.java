package Services.Contracts;

import Models.Account;
import Models.Role;

import java.util.List;

public interface IAccountRepository {
    void saveAccount(Account account);
    Account getAccountById(int id);
    Account getAccountByUsername(String username);
    List<Account> getAllAccounts();
    void updateAccountRole(int id, Role role);
    void deleteAccount(int id);
    boolean usernameExists(String username);
}
