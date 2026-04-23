package com.darkmarshal.tournamentservice.Config.Database;

import com.darkmarshal.tournamentservice.Database.Repositories.*;
import com.darkmarshal.tournamentservice.Contracts.IAccountRepository;
import com.darkmarshal.tournamentservice.Contracts.IEventRepository;
import com.darkmarshal.tournamentservice.Contracts.IPlayerRepository;
import com.darkmarshal.tournamentservice.Contracts.IResultsRepository;
import com.darkmarshal.tournamentservice.Contracts.ITournamentRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import javax.sql.DataSource;

/**
 * Bridges the manually managed DatabaseInstance into Spring's DI context
 * so that controllers can inject repositories.
 */
@Configuration
@Profile("local")
public class LocalDatabaseBeans {

    private final DataSource dataSource;

    public LocalDatabaseBeans() {
        DatabaseInstance databaseInstance = DatabaseInstance.createInstance();
        databaseInstance.connect();
        this.dataSource = databaseInstance.getDataSource();
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
    public IEventRepository eventRepository(IResultsRepository resultsRepository,
                                            IPlayerRepository playerRepository,
                                            ITournamentRepository tournamentRepository) {
        return new EventRepository(dataSource, resultsRepository, playerRepository, tournamentRepository);
    }

    @Bean
    public IAccountRepository accountRepository() {
        return new AccountRepository(dataSource);
    }
}
