package expense_tally.expense_manager.persistence.sqlite;

import expense_tally.expense_manager.persistence.DatabaseConnectable;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Objects;

/**
 * Manages the database connection to SQLite embedded database engine.
 * <p>All the database configuration and related information is stored inside this class</p>
 * <p>As quoted in the title <a href="https://en.wikipedia.org/wiki/SQLite">Wikipedia page</a>
 * , SQLite library is linked and form part of the application. Hence there isn't a database server. The entire
 * database (definitions, tables, indices, and the data itself) is stored inside a single cross-platform file.</p>
 */
public final class SqlLiteConnection implements DatabaseConnectable {
  private static final String SQLITE_JDBC_PREFIX = "jdbc:sqlite:";
  private final String databaseFile;

  /**
   * Construct a SqlLiteConnection with the file path to the database file
   *
   * @param databaseFile file path of the database file
   */
  public SqlLiteConnection(String databaseFile) {
    this.databaseFile = SQLITE_JDBC_PREFIX + Objects.requireNonNull(databaseFile);
  }

  @Override
  public Connection connect() throws SQLException {
    // This line of code cannot be tested because DriverManager cannot be further mocked as Mockito does not support
    // static method mocking
    return DriverManager.getConnection(databaseFile);
  }
}
