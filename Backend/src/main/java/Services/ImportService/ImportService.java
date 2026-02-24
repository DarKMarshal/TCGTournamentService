package Services.ImportService;

import Database.Repositories.EventRepository;
import Database.Repositories.PlayerRepository;
import Models.Event;
import Services.Contracts.IImportService;

import java.sql.Connection;

public class ImportService extends IImportService {

    // TODO: This may have to be changed to allow for multiple import types
    public static void retrieveEventInformation(Connection connection, String filepath) {

        EventRepository eventRepository = new EventRepository(connection);
        Event parsedEvent = null;

        try {
            parsedEvent = TDFParseService.parseEventFile(filepath, new PlayerRepository(connection), new EventRepository(connection));
        } catch (Exception e) {
            System.out.println("Error parsing event file: " + e.getMessage());
        }

        if (parsedEvent != null) {
            //TODO: Display event information to the user and ask for confirmation before proceeding with import

            eventRepository.saveEvent(parsedEvent);
        }
    }
}
