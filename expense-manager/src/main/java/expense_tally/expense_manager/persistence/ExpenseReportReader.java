package expense_tally.expense_manager.persistence;

import expense_tally.expense_manager.mapper.ExpenseReportMapper;
import expense_tally.model.persistence.database.ExpenseReport;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

/**
 * {@code ExpenseReportReader} provide the user ways to retrieve the
 * {@link ExpenseReport} from a database file.
 * <p>The current implementation only support reading from SQLite database system.</p>
 *
 * @see ExpenseReport
 */
public final class ExpenseReportReader implements ExpenseReadable {
  private static final Logger LOGGER = LogManager.getLogger(ExpenseReportReader.class);

  private DatabaseConnectable databaseConnectable;
  private SqliteSessionFactoryBuilder sqliteSessionFactoryBuilder;

  /**
   * The default constructor file path of the database file
   *
   * @param databaseConnectable the database source of the expense report retrieval
   */
  public ExpenseReportReader(DatabaseConnectable databaseConnectable,
                             SqliteSessionFactoryBuilder sqliteSessionFactoryBuilder) {
    this.databaseConnectable = Objects.requireNonNull(databaseConnectable);
    this.sqliteSessionFactoryBuilder = Objects.requireNonNull(sqliteSessionFactoryBuilder);
  }

  @Override
  public List<ExpenseReport> getExpenseTransactions() throws SQLException {
    return importDataFromDatabase();
  }

  /**
   * Returns all the expenses stored in the database of expense manager application
   *
   * @return a list of expenses representing the transaction amount, category, subcategory, payment method,
   * description, time of expense, and other related field
   * @throws SQLException when there is an error accessing the database
   */
  private List<ExpenseReport> importDataFromDatabase() throws SQLException {
    // Connect to expense_tally.model.persistence
    try (Connection databaseConnection = databaseConnectable.connect()) {
      return importDataFromConnection(databaseConnection);
    } catch (SQLException ex) {
      LOGGER.atError().withThrowable(ex).log("Cannot read from database");
      throw ex;
    }
  }

  private List<ExpenseReport> importDataFromConnection(Connection databaseConnection) throws SQLException {
    SqlSessionFactory sqlSessionFactory = sqliteSessionFactoryBuilder.createSqliteSessionFactory();
    try (SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.REUSE, databaseConnection)) {
      ExpenseReportMapper expenseReportMapper = sqlSession.getMapper(ExpenseReportMapper.class);
      List<ExpenseReport> expenseReports = expenseReportMapper.getAllExpenseReports();
      return expenseReports;
    }
  }
}