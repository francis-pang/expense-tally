package expense_tally.views.cli;

import expense_tally.expense_manager.persistence.ExpenseReportReadable;
import expense_tally.expense_manager.persistence.ExpenseUpdatable;
import expense_tally.expense_manager.persistence.database.DatabaseConnectable;
import expense_tally.expense_manager.persistence.database.DatabaseSessionFactoryBuilder;
import expense_tally.expense_manager.persistence.database.ExpenseManagerTransactionDatabaseProxy;
import expense_tally.expense_manager.persistence.database.ExpenseReportDatabaseReader;
import expense_tally.expense_manager.persistence.database.mysql.MySqlConnection;
import expense_tally.expense_manager.persistence.database.sqlite.SqLiteConnection;
import expense_tally.views.AppParameter;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;


/**
 * This class acts as the Dependency Injection container. It create all the dependencies and inject into the rest of
 * the class
 */
public final class CommandLineRunner {
  private static final Logger LOGGER = LogManager.getLogger(CommandLineRunner.class);
  private static final String SQLITE_ENVIRONMENT_ID = "file_sqlite";
  private static final String MYSQL_ENVIRONMENT_ID = "mysql";

  public static void main(String[] args) {
    final int CSV_FILE_PARSING_ERR_CODE = 2;
    final int DATABASE_ERR_CODE = 3;
    try {
      Map<AppParameter, String> optionValues = CommandParser.parseCommandArgs(args);
      String databaseFileName = optionValues.get(AppParameter.DATABASE_PATH);
      DatabaseConnectable databaseConnectable = SqLiteConnection.create(databaseFileName);
      SqlSessionFactoryBuilder sqliteSessionFactoryBuilder = new SqlSessionFactoryBuilder();
      DatabaseSessionFactoryBuilder sqlLiteDatabaseSessionFactoryBuilder =
              new DatabaseSessionFactoryBuilder(sqliteSessionFactoryBuilder);
      ExpenseReportReadable expenseReportReadable = new ExpenseReportDatabaseReader(databaseConnectable,
          sqlLiteDatabaseSessionFactoryBuilder, SQLITE_ENVIRONMENT_ID);

      final String mysqlHost = "172.22.18.96";
      final String database = "expense_manager";
      final String user = "expensetally";
      final String password = "Password1";
      DatabaseConnectable mySqlConnectable = MySqlConnection.create(mysqlHost, database, user, password);
      SqlSessionFactoryBuilder mySqlSessionFactoryBuilder = new SqlSessionFactoryBuilder();
      DatabaseSessionFactoryBuilder mySqlDatabaseSessionFactoryBuilder =
          new DatabaseSessionFactoryBuilder(mySqlSessionFactoryBuilder);
      ExpenseUpdatable expenseUpdatable = new ExpenseManagerTransactionDatabaseProxy(mySqlConnectable,
          mySqlDatabaseSessionFactoryBuilder, MYSQL_ENVIRONMENT_ID);

      ExpenseAccountant expenseAccountant = new ExpenseAccountant(expenseReportReadable, expenseUpdatable);
      expenseAccountant.reconcileData(optionValues.get(AppParameter.CSV_PATH));
    } catch (IOException ioException) {
      LOGGER.atError().withThrowable(ioException).log("Error reading CSV file");
      System.exit(CSV_FILE_PARSING_ERR_CODE);
    } catch (SQLException sqlException) {
      LOGGER.atError().withThrowable(sqlException).log("Error reading from database");
      System.exit(DATABASE_ERR_CODE);
    }
  }
}
