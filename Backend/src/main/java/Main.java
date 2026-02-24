import Database.Repositories.DatabaseInstance;
import Services.ImportService.ImportService;

public class Main {
    public static void main(String[] args) {
        DatabaseInstance db = DatabaseInstance.createInstance();
        String FilePath = "C:\\Users\\outfi\\IdeaProjects\\TCGTournamentService\\Backend\\src\\main\\resources\\Data\\Trailside Challenge_25-11-016498 FINAL.tdf";
        db.connect();

        ImportService.retrieveEventInformation(db.getConnection(), FilePath);

        db.disconnect();
    }
}