package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SqlLiteConnectionManager {
    public static Connection connect() throws SQLException {
        final String databaseFile = "jdbc:sqlite:D:/code/expense-tally/src/main/resource/database/2018-11-09.db";
        Connection databaseConnection = DriverManager.getConnection(databaseFile);
        return databaseConnection;
    }
}
