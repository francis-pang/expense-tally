package expense_tally.views.cli;

import expense_tally.expense_manager.persistence.ExpenseReportReadable;
import expense_tally.expense_manager.persistence.ExpenseUpdatable;
import expense_tally.expense_manager.persistence.database.DatabaseEnvironmentId;
import expense_tally.expense_manager.persistence.database.DatabaseSessionBuilder;
import expense_tally.expense_manager.persistence.database.ExpenseManagerTransactionDatabaseProxy;
import expense_tally.expense_manager.persistence.database.ExpenseReportDatabaseReader;
import expense_tally.expense_manager.persistence.database.mysql.MySqlConnection;
import expense_tally.expense_manager.persistence.database.sqlite.SqLiteConnection;
import expense_tally.views.AppParameter;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.TransactionFactory;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;


/**
 * This class acts as the Dependency Injection container. It of all the dependencies and inject into the rest of
 * the class
 */
public final class CommandLineRunner {
  private static final Logger LOGGER = LogManager.getLogger(CommandLineRunner.class);
  private static final String REMOTE_DATABASE_NAME = "expense_manager";
  private static final String REMOTE_DATABASE_USERNAME = "expensetally";
  private static final String REMOTE_DATABASE_PASSWORD = "Password1";

  public static void main(String[] args) {
    final int CSV_FILE_PARSING_ERR_CODE = 2;
    final int DATABASE_ERR_CODE = 3;
    try {
      Map<AppParameter, String> optionValues = CommandParser.parseCommandArgs(args);
      String localDatabaseFilePath = optionValues.get(AppParameter.DATABASE_PATH);
      String remoteDatabaseHostName = optionValues.get(AppParameter.DATABASE_HOST);
      ExpenseReportReadable expenseReportReadable = constructExpenseReportReadable(localDatabaseFilePath);
      ExpenseUpdatable expenseUpdatable = constructExpenseUpdatable(remoteDatabaseHostName);
      ExpenseAccountant expenseAccountant = new ExpenseAccountant(expenseReportReadable, expenseUpdatable);
      expenseAccountant.reconcileData(optionValues.get(AppParameter.CSV_PATH));
    } catch (IOException ioException) {
      LOGGER.atError().withThrowable(ioException).log("Error reading CSV file");
      Runtime runtime = Runtime.getRuntime();
      runtime.exit(CSV_FILE_PARSING_ERR_CODE);
    } catch (SQLException sqlException) {
      LOGGER.atError().withThrowable(sqlException).log("Error reading from database");
      Runtime runtime = Runtime.getRuntime();
      runtime.exit(DATABASE_ERR_CODE);
    }
  }

  private static ExpenseUpdatable constructExpenseUpdatable(String mysqlHost) throws SQLException, IOException {
    SqlSession sqlSession = createSqlSession(DatabaseEnvironmentId.MYSQL, mysqlHost, REMOTE_DATABASE_NAME,
        REMOTE_DATABASE_USERNAME, REMOTE_DATABASE_PASSWORD);
    return new ExpenseManagerTransactionDatabaseProxy(sqlSession);
  }

  private static ExpenseReportReadable constructExpenseReportReadable(String sqLiteFilePath) throws IOException, SQLException {
    SqlSession sqlSession = createSqlSession(DatabaseEnvironmentId.SQLITE, sqLiteFilePath, null, null, null);
    return new ExpenseReportDatabaseReader(sqlSession);
  }

  private static SqlSession createSqlSession(DatabaseEnvironmentId databaseEnvironmentId,
                                             String databaseConnectionUrl,
                                             String databaseName,
                                             String username,
                                             String password) throws SQLException, IOException {
    DataSource dataSource;
    switch(databaseEnvironmentId) {
      case SQLITE:
        dataSource = SqLiteConnection.createDataSource(databaseConnectionUrl);
        break;
      case MYSQL:
        dataSource = MySqlConnection.createDataSource(databaseConnectionUrl, databaseName, username, password);
        break;
      default:
        throw new IllegalStateException("Unexpected value: " + databaseEnvironmentId);
    }
    Environment environment = constructEnvironment(databaseEnvironmentId.name(), dataSource,
        new JdbcTransactionFactory());

    // Create SqlSessionFactory
    DatabaseSessionBuilder databaseSessionBuilder = DatabaseSessionBuilder.of(new SqlSessionFactoryBuilder());
    return databaseSessionBuilder.buildSessionFactory(environment);
  }

  public static Environment constructEnvironment(String environmentId,
                                          DataSource dataSource,
                                          TransactionFactory transactionFactory) {
    return new Environment.Builder(environmentId)
        .dataSource(dataSource)
        .transactionFactory(transactionFactory)
        .build();
  }
}
