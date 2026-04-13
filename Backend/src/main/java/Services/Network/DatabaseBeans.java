package Services.Network;

import Database.Repositories.*;
import Services.Contracts.IAccountRepository;
import Services.Contracts.IEventRepository;
import Services.Contracts.IPlayerRepository;
import Services.Contracts.IResultsRepository;
import Services.Contracts.ITournamentRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.sql.Connection;

/**
 * Bridges the manually-managed DatabaseInstance into Spring's DI context
 * so that controllers can inject repositories.
 */
@Configuration
public class DatabaseBeans {

    private final DatabaseInstance databaseInstance;

    public DatabaseBeans() {
        this.databaseInstance = DatabaseInstance.createInstance();
        this.databaseInstance.connect();
    }

    @Bean
    public Connection databaseConnection() {
        return databaseInstance.getConnection();
    }

    @Bean
    public IEventRepository eventRepository() {
        return new EventRepository(databaseInstance.getConnection());
    }

    @Bean
    public ITournamentRepository tournamentRepository() {
        return new TournamentRepository(databaseInstance.getConnection());
    }

    @Bean
    public IResultsRepository resultsRepository() {
        return new ResultsRepository(databaseInstance.getConnection());
    }

    @Bean
    public IPlayerRepository playerRepository() {
        return new PlayerRepository(databaseInstance.getConnection());
    }

    @Bean
    public IAccountRepository accountRepository() {
        return new AccountRepository(databaseInstance.getConnection());
    }
}
