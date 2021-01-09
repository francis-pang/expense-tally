package expense_tally.expense_manager.persistence.mysql;

import com.mysql.cj.conf.ConnectionUrl;
import expense_tally.expense_manager.persistence.DatabaseConnectable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * This class provide the default implementation to connection to a MySQL database.
 */
public class MySqlConnection implements DatabaseConnectable {
  private static final Logger LOGGER = LogManager.getLogger(MySqlConnection.class);

  private final String connectionUrl;
  private final String database;
  private final String username;
  private final String password;

  public MySqlConnection(String connectionUrl, String database, String username, String password) {
    this.connectionUrl = connectionUrl;
    this.database = database;
    this.username = username;
    this.password = password;
  }

  @Override
  public Connection connect() throws SQLException {
    String databaseConnectionUrl = constructConnectionString();
    return DriverManager.getConnection(databaseConnectionUrl, username, password);
  }

  private String constructConnectionString() {
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append(ConnectionUrl.Type.SINGLE_CONNECTION.getScheme());
    stringBuilder.append("//");
    stringBuilder.append(connectionUrl);
    stringBuilder.append("/");
    stringBuilder.append(database);
    LOGGER.atTrace().log("MySQL connection string:{}", stringBuilder.toString());
    return stringBuilder.toString();
  }
}
