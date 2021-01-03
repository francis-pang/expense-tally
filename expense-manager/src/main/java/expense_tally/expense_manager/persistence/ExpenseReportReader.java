package expense_tally.expense_manager.persistence;

import expense_tally.expense_manager.mapper.ExpenseReportMapper;
import expense_tally.model.persistence.database.ExpenseReport;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * {@code ExpenseReportReader} provide the user ways to retrieve the {@link ExpenseReport} from a database file.
 * <p>
 *     The current implementation only support reading from SQLite database system.
 * </p>
 *  @see ExpenseReport
 *
 * <p><b>Implementation Note:</b></p>
 * <p>
 *     This class use {@link DatabaseSessionFactoryBuilder} as a dependency, instead of {@link SqlSessionFactory}.
 *     This is because I want to abstract away the knowledge of myBatis dependency from the caller.
 * </p>
 */
public final class ExpenseReportReader implements ExpenseReadable {
  private static final Logger LOGGER = LogManager.getLogger(ExpenseReportReader.class);

  private final DatabaseConnectable databaseConnectable;
  private final DatabaseSessionFactoryBuilder databaseSessionFactoryBuilder;
  private final String environmentId;

  public ExpenseReportReader(DatabaseConnectable databaseConnectable,
                             DatabaseSessionFactoryBuilder databaseSessionFactoryBuilder,
                             String environmentId) {
    this.databaseConnectable = databaseConnectable;
    this.databaseSessionFactoryBuilder = databaseSessionFactoryBuilder;
    this.environmentId = environmentId;
  }

  @Override
  public List<ExpenseReport> getExpenseTransactions() throws SQLException, IOException {
    return importDataFromDatabase();
  }

  /**
   * Returns all the expenses stored in the database of expense manager application
   *
   * @return a list of expenses representing the transaction amount, category, subcategory, payment method,
   * description, time of expense, and other related field
   * @throws SQLException when there is an error accessing the database
   */
  private List<ExpenseReport> importDataFromDatabase() throws SQLException, IOException {
    // Connect to expense_tally.model.persistence
    try (Connection databaseConnection = databaseConnectable.connect()) {
      return importDataFromConnection(databaseConnection);
    } catch (SQLException sqlException) {
      LOGGER.atError().withThrowable(sqlException).log("Cannot create a database connection");
      throw sqlException;
    } catch (IOException ioException) {
      LOGGER.atError().withThrowable(ioException).log("Cannot read from database source");
      throw ioException;
    }
  }

  private List<ExpenseReport> importDataFromConnection(Connection databaseConnection) throws SQLException, IOException {
    SqlSessionFactory sqlSessionFactory = databaseSessionFactoryBuilder.buildSessionFactory(environmentId);
    try (SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.REUSE, databaseConnection)) {
      ExpenseReportMapper expenseReportMapper = sqlSession.getMapper(ExpenseReportMapper.class);
      return expenseReportMapper.getAllExpenseReports();
    } catch (RuntimeException runtimeException) {
      LOGGER.atError().withThrowable(runtimeException).log("Unable to retrieve expense report from database. " +
              "environment ID: \"{}\", scheme: \"{}\"", environmentId, databaseConnection.getSchema());
      throw runtimeException;
    }
  }
}