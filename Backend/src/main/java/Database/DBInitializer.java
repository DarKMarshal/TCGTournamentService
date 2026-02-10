package Database;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.stream.Collectors;

public class DBInitializer {
    public static void initializeDatabase(String dbPath) throws Exception {
        String schema = readSchemaFile();

        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:" + dbPath);
            Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(schema);
        }
    }

    private static String readSchemaFile() throws Exception {
        try (InputStream is = DBInitializer.class.getResourceAsStream("/Schema/schema.sql");
             BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            return reader.lines().collect(Collectors.joining("\n"));
        }
    }
}
