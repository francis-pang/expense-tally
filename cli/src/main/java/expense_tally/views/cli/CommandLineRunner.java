package expense_tally.views.cli;

import expense_tally.expense_manager.mapper.ExpenseReportMapper;
import expense_tally.expense_manager.persistence.DatabaseConnectable;
import expense_tally.expense_manager.persistence.DatabaseSessionFactoryBuilder;
import expense_tally.expense_manager.persistence.ExpenseReadable;
import expense_tally.expense_manager.persistence.ExpenseReportReader;
import expense_tally.expense_manager.persistence.mysql.MySqlConnection;
import expense_tally.expense_manager.persistence.sqlite.SqlLiteConnection;
import expense_tally.views.AppParameter;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.MalformedURLException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;


/**
 * This class acts as the Dependency Injection container. It create all the dependencies and inject into the rest of
 * the class
 */
public final class CommandLineRunner {
  private static final Logger LOGGER = LogManager.getLogger(CommandLineRunner.class);
  private static final String SQLITE_ENVIRONMENT_ID = "file_sqlite";

  public static void main(String[] args) {
    final int CSV_FILE_PARSING_ERR_CODE = 2;
    final int DATABASE_ERR_CODE = 3;
    try {
      Map<AppParameter, String> optionValues = CommandParser.parseCommandArgs(args);
      String databaseFileName = optionValues.get(AppParameter.DATABASE_PATH);
      DatabaseConnectable databaseConnectable = new SqlLiteConnection(databaseFileName);
      SqlSessionFactoryBuilder sqlSessionFactoryBuilder = new SqlSessionFactoryBuilder();
      DatabaseSessionFactoryBuilder databaseSessionFactoryBuilder =
              new DatabaseSessionFactoryBuilder(sqlSessionFactoryBuilder);
      ExpenseReadable expenseReadable = new ExpenseReportReader(databaseConnectable, databaseSessionFactoryBuilder,
              SQLITE_ENVIRONMENT_ID);
      ExpenseAccountant expenseAccountant = new ExpenseAccountant(expenseReadable);
      expenseAccountant.reconcileData(optionValues.get(AppParameter.CSV_PATH));
    } catch (IOException ioException) {
      LOGGER.atError().withThrowable(ioException).log("Error reading CSV file");
      System.exit(CSV_FILE_PARSING_ERR_CODE);
    } catch (SQLException sqlException) {
      LOGGER.atError().withThrowable(sqlException).log("Error reading from database");
      System.exit(DATABASE_ERR_CODE);
    }

    try {
      final String mysqlHost = "172.23.72.222";
      final String database = "expense_manager";
      final String user = "expensetally";
      final String password = "Password1";

      DatabaseConnectable mySqlDatabaseConnectable = new MySqlConnection(mysqlHost, database, user, password);
      Connection connection = mySqlDatabaseConnectable.connect();
      DatabaseSessionFactoryBuilder databaseSessionFactoryBuilder  =
          new DatabaseSessionFactoryBuilder(new SqlSessionFactoryBuilder());
      SqlSessionFactory sqlSessionFactory = databaseSessionFactoryBuilder.buildSessionFactory("mysql");
      try (SqlSession sqlSession = sqlSessionFactory.openSession(connection)) {
        ExpenseReportMapper expenseReportMapper = sqlSession.getMapper(ExpenseReportMapper.class);
        expenseReportMapper.deleteAllExpenseReports();
      }
    } catch (MalformedURLException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (SQLException throwables) {
      throwables.printStackTrace();
    }
  }
}
