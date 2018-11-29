package expense_tally.persistence;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SqlLiteConnectionManager {
    private String databaseFile;

    public SqlLiteConnectionManager(String databaseFile) {
        this.databaseFile = databaseFile;
    }

    public Connection connect() throws SQLException {
        Connection databaseConnection = DriverManager.getConnection(databaseFile);
        return databaseConnection;
    }
}
