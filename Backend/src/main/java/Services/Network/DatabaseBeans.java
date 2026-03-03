package Services.Network;

import Database.Repositories.DatabaseInstance;
import Database.Repositories.EventRepository;
import Database.Repositories.ResultsRepository;
import Database.Repositories.TournamentRepository;
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
    public EventRepository eventRepository() {
        return new EventRepository(databaseInstance.getConnection());
    }

    @Bean
    public TournamentRepository tournamentRepository() {
        return new TournamentRepository(databaseInstance.getConnection());
    }

    @Bean
    public ResultsRepository resultsRepository() {
        return new ResultsRepository(databaseInstance.getConnection());
    }
}
