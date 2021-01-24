package expense_tally.expense_manager.persistence.database.mysql;

import com.mysql.cj.conf.ConnectionUrl;
import com.mysql.cj.jdbc.MysqlDataSource;
import expense_tally.exception.StringResolver;
import expense_tally.expense_manager.persistence.database.DatabaseConnectable;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * This class provide the default implementation to connection to a MySQL database.
 */
public class MySqlConnection implements DatabaseConnectable {
  private static final Logger LOGGER = LogManager.getLogger(MySqlConnection.class);
  private final DataSource dataSource;
  private final String connectionUrl;

  private MySqlConnection(DataSource dataSource, String connectionUrl) {
    this.dataSource = dataSource;
    this.connectionUrl = connectionUrl;
  }

  /**
   * Create a new instance of <i>MySqlConnection</i> based on the input parameters.
   * <p>
   *   Returns a newly created instance of <i>MySqlConnection</i> if creation succeeds.
   * </p>
   * @param connectionUrl URL of the database connection. Does not need to include database scheme.
   * @param database name of the database to be connected
   * @param username username to login to the database system
   * @param password password to login to the database system.
   *                 <p>
   *                 username needs to be provided as well if password is needed.
   *                 </p>
   * @return a newly created instance of <i>MySqlConnection</i> if creation succeeds.
   * @throws SQLException if there is issue setting up the requirement for MySqlConnection
   */
  public static MySqlConnection create(String connectionUrl, String database, String username, String password)
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
    return new MySqlConnection(mysqlDataSource, connectionString);
  }

  @Override
  public Connection connect() throws SQLException {
    return dataSource.getConnection();
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

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;

    if (o == null || getClass() != o.getClass()) return false;

    MySqlConnection that = (MySqlConnection) o;

    return new EqualsBuilder()
        .append(dataSource, that.dataSource)
        .append(connectionUrl, that.connectionUrl)
        .isEquals();
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder(17, 37)
        .append(dataSource)
        .append(connectionUrl)
        .toHashCode();
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this)
        .append("dataSource", dataSource)
        .append("connectionUrl", connectionUrl)
        .toString();
  }
}
