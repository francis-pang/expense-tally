package expense_tally.expense_manager;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Manages the database connection to SQLite embedded database engine.
 * <p>All the database configuration and related information is stored inside this class</p>
 * <p>As quoted in the title <a href="https://en.wikipedia.org/wiki/SQLite">Wikipedia page</a>
 * , SQLite library is linked and form part of the application. Hence there isn't a database server. The entire
 * database (definitions, tables, indices, and the data itself) is stored inside a single cross-platform file.</p>
 */
public class SqlLiteConnection implements DatabaseConnectable {
  private final static String SQLITE_JDBC_PREFIX = "jdbc:sqlite:";
  private String databaseFile;

  /**
   * Construct a SqlLiteConnection with the file path to the database file
   *
   * @param databaseFile file path of the database file
   */
  public SqlLiteConnection(String databaseFile) {
    this.databaseFile = SQLITE_JDBC_PREFIX + databaseFile;
  }

  /**
   * Returns the connection to the database
   *
   * @return the connection to the database
   * @throws SQLException when there is an error accessing the database
   */
  @Override
  public Connection connect() throws SQLException {
    return DriverManager.getConnection(databaseFile);
  }
}
