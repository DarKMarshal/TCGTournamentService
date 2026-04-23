package com.darkmarshal.tournamentservice;

import com.darkmarshal.tournamentservice.Database.Repositories.*;
import com.darkmarshal.tournamentservice.Contracts.*;
import com.darkmarshal.tournamentservice.Services.ImportService;
import com.darkmarshal.tournamentservice.Services.TDFParseService;

import javax.sql.DataSource;

public class TestMain {
    public static void main(String[] args) {
        DatabaseInstance db = DatabaseInstance.createInstance();
        String FilePath = "C:\\Users\\outfi\\IdeaProjects\\TCGTournamentService\\Backend\\src\\main\\resources\\Data\\Trailside Challenge_25-11-016498 FINAL.tdf";
        db.connect();

        DataSource dataSource = db.getDataSource();

        IPlayerRepository playerRepository = new PlayerRepository(dataSource);
        ITournamentRepository tournamentRepository = new TournamentRepository(dataSource);
        IResultsRepository resultsRepository = new ResultsRepository(dataSource);
        IEventRepository eventRepository = new EventRepository(dataSource, resultsRepository, playerRepository, tournamentRepository);

        IParseService parseService = new TDFParseService(playerRepository);

        ImportService importService = new ImportService(
                eventRepository,
                parseService,
                null,  // CachedDataService — not needed for standalone testing
                null   // WebSocketBroadcastService — not needed for standalone testing
        );
        importService.retrieveEventInformation(FilePath);
    }
}
