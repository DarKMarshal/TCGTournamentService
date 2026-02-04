package Database;

import java.nio.file.Paths;

public class DatabaseConfig {
    public static final String DB_PATH = Paths.get(System.getProperty("user.dir"), "database.sqlite").toString();
}