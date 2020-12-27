package expense_tally.views.cli;

import expense_tally.expense_manager.persistence.DatabaseConnectable;
import expense_tally.expense_manager.persistence.ExpenseReadable;
import expense_tally.expense_manager.persistence.ExpenseReportReader;
import expense_tally.expense_manager.persistence.SqlLiteConnection;
import expense_tally.expense_manager.persistence.SqliteSessionFactoryBuilder;
import expense_tally.views.AppParameter;
import org.apache.ibatis.datasource.pooled.PooledDataSourceFactory;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.TransactionFactory;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
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

  public static void main(String[] args) {
    final int CSV_FILE_PARSING_ERR_CODE = 2;
    final int DATABASE_ERR_CODE = 3;

    PooledDataSourceFactory pooledDataSourceFactory = new PooledDataSourceFactory();
    TransactionFactory transactionFactory = new JdbcTransactionFactory();
    SqlSessionFactoryBuilder sqlSessionFactoryBuilder = new SqlSessionFactoryBuilder();
    Configuration configuration = new Configuration();

    try {
      Map<AppParameter, String> optionValues = CommandParser.parseCommandArgs(args);
      String databaseFileName = optionValues.get(AppParameter.DATABASE_PATH);
      DatabaseConnectable databaseConnectable = new SqlLiteConnection(databaseFileName);
      SqliteSessionFactoryBuilder sqliteSessionFactoryBuilder = new SqliteSessionFactoryBuilder(pooledDataSourceFactory,
          transactionFactory, sqlSessionFactoryBuilder, configuration);
      ExpenseReadable expenseReadable = new ExpenseReportReader(databaseConnectable, sqliteSessionFactoryBuilder);
      ExpenseAccountant expenseAccountant = new ExpenseAccountant(expenseReadable);
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
