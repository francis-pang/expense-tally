package expense_tally.expense_manager.persistence.database.sqlite;

import expense_tally.expense_manager.persistence.database.DatabaseConnectable;
import org.sqlite.SQLiteDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Manages the database connection to SQLite embedded database engine.
 * <p>All the database configuration and related information is stored inside this class</p>
 * <p>As quoted in the title <a href="https://en.wikipedia.org/wiki/SQLite">Wikipedia page</a>
 * , SQLite library is linked and form part of the application. Hence there isn't a database server. The entire
 * database (definitions, tables, indices, and the data itself) is stored inside a single cross-platform file.</p>
 */
public final class SqLiteConnection implements DatabaseConnectable {
  private static final String SQLITE_JDBC_PREFIX = "jdbc:sqlite:";
  private final DataSource dataSource;

  /**
   * Default constructor
   */
  private SqLiteConnection(DataSource dataSource) {
    this.dataSource = dataSource;
  }

  /**
   * Construct a SqlLiteConnection with the file path to the database file
   *
   * @param databaseFile file path of the database file
   */
  public static SqLiteConnection create(String databaseFile) {
    SQLiteDataSource sqLiteDataSource = new SQLiteDataSource();
    String connectionUrl = constructConnectionUrl(databaseFile);
    sqLiteDataSource.setUrl(connectionUrl);
    return new SqLiteConnection(sqLiteDataSource);
  }

  private static String constructConnectionUrl(String databaseFile) {
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append(SQLITE_JDBC_PREFIX);
    stringBuilder.append(databaseFile);
    return stringBuilder.toString();
  }

  @Override
  public Connection connect() throws SQLException {
    return dataSource.getConnection();
  }
}
