import Database.Repositories.DatabaseInstance;

public class main {
    public static void main(String[] args) {
        DatabaseInstance db = DatabaseInstance.createInstance();
        db.connect();
        db.disconnect();
    }
}
