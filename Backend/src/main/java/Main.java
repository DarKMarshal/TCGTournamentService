import Database.Repositories.DatabaseInstance;
import Services.ImportService.TournamentResultEntryService;

public class Main {
    public static void main(String[] args) {
        DatabaseInstance db = DatabaseInstance.createInstance();
        String FilePath = "C:\\Users\\outfi\\IdeaProjects\\TCGTournamentService\\Backend\\src\\main\\resources\\Data\\Trailside Cup_26-01-002815-r5middle FINAL.tdf";
        db.connect();

        TournamentResultEntryService.retrieveEventInformation(db.getConnection(), FilePath);

        db.disconnect();
    }
}