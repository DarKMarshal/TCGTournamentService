import Database.Repositories.DatabaseInstance;

public class main {
    public static void main(String[] args) {
        DatabaseInstance db = DatabaseInstance.getInstance();
        db.connect();
        db.disconnect();
    }
}
