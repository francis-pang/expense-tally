package expense_tally.expense_manager.persistence.database.mysql;

import com.mysql.cj.conf.ConnectionUrl;
import com.mysql.cj.jdbc.MysqlDataSource;
import expense_tally.exception.StringResolver;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.sql.DataSource;
import java.sql.SQLException;

/**
 * This class provides implementation to common method to retrieve object to connect to a MySQL database server.
 * @see DataSource
 */
public class MySqlConnection {
  private static final Logger LOGGER = LogManager.getLogger(MySqlConnection.class);

  /**
   * Private constructor
   * Utility classes, which are collections of static members, are not meant to be instantiated
   */
  private MySqlConnection() {
  }

  /**
   * Returns a data source based on the provided parameters.
   * @param connectionUrl hostname of the database connection. Port is not needed, default to be 3006.
   * @param database name of the database to be connected
   * @param loginTimeout maximum time in milliseconds that this data source can wait while attempting to connect to a
   *                     database.
   * @return a data source based on the provided parameters.
   * @throws SQLException if database access error occurs
   * @throws IllegalArgumentException if <i>connectionUrl</i> or <i>database</i> are blank, or when loginTimeout is
   * negative.
   */
  public static DataSource createDataSource(String connectionUrl, String database, int loginTimeout)
      throws SQLException {
    return createDataSource(connectionUrl, database, StringUtils.EMPTY, StringUtils.EMPTY, loginTimeout);
  }

  /**
   * Returns a data source based on the provided parameters.
   * @param connectionUrl hostname of the database connection. Port is not needed, default to be 3006.
   * @param database name of the database to be connected
   * @param username username to login to database server. This needs to be provided together with password.
   * @param password password to login to database server. This needs to be provided together with username.
   * @param loginTimeout maximum time in milliseconds that this data source can wait while attempting to connect to a
   *                     database.
   * @return a data source based on the provided parameters.
   * @throws SQLException if database access error occurs
   * @throws IllegalArgumentException if <i>connectionUrl</i>  or <i>database</i> are blank, or only one of the fields
   * <i>username</i> and <i>password</i> is filled, or when loginTimeout is negative.
   */
  public static DataSource createDataSource(String connectionUrl, String database, String username, String password,
                                            int loginTimeout)
      throws SQLException {
    if (StringUtils.isBlank(connectionUrl)) {
      LOGGER.atError().log("connectionUrl is blank:{}", StringResolver.resolveNullableString(connectionUrl));
      throw new IllegalArgumentException("Connection URL should not be null or blank.");
    }
    if (StringUtils.isBlank(database)) {
      LOGGER.atError().log("database is blank:{}", StringResolver.resolveNullableString(database));
      throw new IllegalArgumentException("Database name should not be null or blank.");
    }
    MysqlDataSource mysqlDataSource = new MysqlDataSource();
    String connectionString = constructConnectionString(connectionUrl, database);
    mysqlDataSource.setUrl(connectionString);
    mysqlDataSource.setDatabaseName(database);
    if (loginTimeout < 0) {
      LOGGER.atError().log("loginTimeout is negative: {}", loginTimeout);
      throw new IllegalArgumentException("Login time out value cannot be negative.");
    }
    int loginTimeSeconds = (int) Math.ceil(loginTimeout / 1000.0);
    mysqlDataSource.setLoginTimeout(loginTimeSeconds);
    mysqlDataSource.setConnectTimeout(loginTimeSeconds);
    boolean isUserNameBlank = StringUtils.isBlank(username);
    boolean isPasswordBlank = StringUtils.isBlank(password);
    if (!isUserNameBlank) {
      mysqlDataSource.setUser(username);
      if (!isPasswordBlank) {
        mysqlDataSource.setPassword(password);
      }
    } else if (!isPasswordBlank) { // username is blank
      LOGGER.atError().log("Password is provided without username.");
      throw new IllegalArgumentException("Password needs to be accompanied by username.");
    }
    //FIXME: Need to find a way to test
    mysqlDataSource.setLogSlowQueries(true);
    LOGGER.atInfo().log("Creating MySqlConnection: connectionString:{}, database:{}, username:{}", connectionString,
        database, StringResolver.resolveNullableString(username));
    return mysqlDataSource;
  }

  /**
   * Create the database connection string that MySQLDriver requires.
   * @param connectionUrl URL of the database connection. Does not need to include database scheme.
   * @param database name of the database to be connected
   * @return the database connection string that MySQLDriver requires.
   */
  private static String constructConnectionString(String connectionUrl, String database) {
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append(ConnectionUrl.Type.SINGLE_CONNECTION.getScheme());
    stringBuilder.append("//");
    stringBuilder.append(connectionUrl);
    // Due to a bug in the MySQL driver, the database need to be included as part of the connection URL
    stringBuilder.append("/");
    stringBuilder.append(database);
    LOGGER.atDebug().log("MySQL connection string:{}", stringBuilder.toString());
    return stringBuilder.toString();
  }
}
