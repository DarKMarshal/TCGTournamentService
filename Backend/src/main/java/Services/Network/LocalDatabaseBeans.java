package Services.Network;

import Database.Repositories.*;
import Services.Contracts.IAccountRepository;
import Services.Contracts.IEventRepository;
import Services.Contracts.IPlayerRepository;
import Services.Contracts.IResultsRepository;
import Services.Contracts.ITournamentRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * Bridges the manually managed DatabaseInstance into Spring's DI context
 * so that controllers can inject repositories.
 */
@Configuration
@Profile("local")
public class LocalDatabaseBeans {

    private final DatabaseInstance databaseInstance;

    public LocalDatabaseBeans() {
        this.databaseInstance = DatabaseInstance.createInstance();
        this.databaseInstance.connect();
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
