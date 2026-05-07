package com.darkmarshal.tournamentservice.Database.Repositories;

import com.darkmarshal.tournamentservice.Database.DBInitializer;
import com.darkmarshal.tournamentservice.Config.Database.DatabaseConfig;
import org.sqlite.SQLiteDataSource;

import javax.sql.DataSource;
import java.sql.*;

public class DatabaseInstance {
    private static final DatabaseInstance INSTANCE = new DatabaseInstance();
    private final String dbPath;
    private DataSource dataSource;

    private DatabaseInstance() {
        this.dbPath = DatabaseConfig.getDbPath();
    }

    public static DatabaseInstance createInstance() { return INSTANCE; }

    /**
     * Returns a DataSource for obtaining connections.
     * Callers should obtain connections from this DataSource rather than
     * holding a reference to a single raw Connection.
     */
    public DataSource getDataSource() { return dataSource; }

    public void connect() {
        try {
            SQLiteDataSource ds = new SQLiteDataSource();
            ds.setUrl("jdbc:sqlite:" + dbPath);
            this.dataSource = ds;

            try (Connection connection = ds.getConnection()) {
                if (!tablesExist(connection)) {
                    DBInitializer.initializeDatabase(dbPath);
                } else {
                    System.out.println("Connected");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private boolean tablesExist(Connection connection) throws SQLException {
        DatabaseMetaData meta = connection.getMetaData();
        ResultSet rs = meta.getTables(null, null, "players", null);
        return rs.next();
    }
}
