package expense_tally.expense_manager.persistence.database;

import expense_tally.expense_manager.persistence.ExpenseReportReadable;
import expense_tally.expense_manager.persistence.database.mapper.ExpenseReportMapper;
import expense_tally.model.persistence.database.ExpenseReport;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Objects;

/**
 * {@code ExpenseReportReader} provide the user ways to retrieve the {@link ExpenseReport} from a database file.
 * <p>
 *     The current implementation only support reading from SQLite database system.
 * </p>
 *  @see ExpenseReport
 *
 * <p><b>Implementation Note:</b></p>
 * <p>
 *     This class use {@link expense_tally.database.DatabaseSessionBuilder} as a dependency, instead of {@link SqlSessionFactory}.
 *     This is because I want to abstract away the knowledge of myBatis dependency from the caller.
 * </p>
 */
public final class ExpenseReportDatabaseReader implements ExpenseReportReadable {
  private static final Logger LOGGER = LogManager.getLogger(ExpenseReportDatabaseReader.class);

  private SqlSession sqlSession;

  public ExpenseReportDatabaseReader(SqlSession sqlSession) {
    this.sqlSession = Objects.requireNonNull(sqlSession);
  }

  @Override
  public List<ExpenseReport> getExpenseTransactions() {
    try{
      ExpenseReportMapper expenseReportMapper = sqlSession.getMapper(ExpenseReportMapper.class);
      return expenseReportMapper.getAllExpenseReports();
    } catch (RuntimeException exception) {
      LOGGER.atError()
          .withThrowable(exception)
          .log("Unable to retrieve expense report from database. configuration:{}, connection:{}",
              sqlSession.getConfiguration(),
              sqlSession.getConnection());
      throw exception;
    }
  }
}