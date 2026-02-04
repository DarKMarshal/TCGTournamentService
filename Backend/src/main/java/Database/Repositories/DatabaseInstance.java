package Database.Repositories;

import Database.DBInitializer;
import Database.DatabaseConfig;

import java.sql.*;

public class DatabaseInstance {
    private static final DatabaseInstance INSTANCE = new DatabaseInstance();
    private final String DBPATH;
    private Connection connection;
    private DatabaseInstance() {
        this.DBPATH = DatabaseConfig.DB_PATH;
    }
    public static DatabaseInstance getInstance() {return INSTANCE;}
    public Connection getConnection() {return connection;}

    public void connect() {
        try {
                connection = DriverManager.getConnection("jdbc:sqlite:" + DBPATH);

                if (!tablesExist()) {
                    DBInitializer.initializeDatabase(DBPATH);
                } else {
                    System.out.println("Connected");
                }

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public void disconnect() {
        try{
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Database connection closed");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private boolean tablesExist() throws SQLException {
        DatabaseMetaData meta = connection.getMetaData();
        ResultSet rs = meta.getTables(null, null, "players", null);
        return rs.next();
    }
}
