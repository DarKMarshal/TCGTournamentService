package Services.Network;

import Database.Repositories.*;
import Services.Contracts.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import javax.sql.DataSource;

@Configuration
@Profile("azure")
public class AzureDatabaseBeans {

    private final DataSource dataSource;

    public AzureDatabaseBeans(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Bean
    public IEventRepository eventRepository() {
        return new EventRepository(dataSource);
    }

    @Bean
    public ITournamentRepository tournamentRepository() {
        return new TournamentRepository(dataSource);
    }

    @Bean
    public IResultsRepository resultsRepository() {
        return new ResultsRepository(dataSource);
    }

    @Bean
    public IPlayerRepository playerRepository() {
        return new PlayerRepository(dataSource);
    }

    @Bean
    public IAccountRepository accountRepository() {
        return new AccountRepository(dataSource);
    }
}