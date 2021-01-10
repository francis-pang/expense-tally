package expense_tally.expense_manager.persistence.database.mysql;

import com.mysql.cj.conf.ConnectionUrl;
import com.mysql.cj.jdbc.MysqlDataSource;
import expense_tally.expense_manager.persistence.database.DatabaseConnectable;
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

  public MySqlConnection(DataSource dataSource) {
    this.dataSource = dataSource;
  }

  public static MySqlConnection create(String connectionUrl, String database, String username, String password)
      throws SQLException {
    MysqlDataSource mysqlDataSource = new MysqlDataSource();
    String connectionString = constructConnectionString(connectionUrl, database);
    mysqlDataSource.setUrl(connectionString);
    mysqlDataSource.setDatabaseName(database);
    mysqlDataSource.setUser(username);
    mysqlDataSource.setPassword(password);
    mysqlDataSource.setLogSlowQueries(true);
    return new MySqlConnection(mysqlDataSource);
  }

  @Override
  public Connection connect() throws SQLException {
    return dataSource.getConnection();
  }

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
