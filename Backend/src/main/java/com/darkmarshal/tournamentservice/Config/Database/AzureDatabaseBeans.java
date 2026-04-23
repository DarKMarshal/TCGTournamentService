package com.darkmarshal.tournamentservice.Config.Database;

import com.darkmarshal.tournamentservice.Contracts.*;
import com.darkmarshal.tournamentservice.Database.Repositories.*;
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
    public IEventRepository eventRepository(IResultsRepository resultsRepository,
                                            IPlayerRepository playerRepository,
                                            ITournamentRepository tournamentRepository) {
        return new EventRepository(dataSource, resultsRepository, playerRepository, tournamentRepository);
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