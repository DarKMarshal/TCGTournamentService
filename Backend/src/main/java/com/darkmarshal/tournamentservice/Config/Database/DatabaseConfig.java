package com.darkmarshal.tournamentservice.Config.Database;

import java.nio.file.Paths;

public class DatabaseConfig {
    private static final String DB_PATH = Paths.get(System.getProperty("user.dir"), "database.sqlite").toString();

    public static String getDbPath() {
        return DB_PATH;
    }
}
