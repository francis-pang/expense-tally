package expense_tally.expense_manager.persistence;

import expense_tally.model.persistence.database.ExpenseReport;
import expense_tally.model.persistence.transformation.ExpenseManagerTransaction;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

/**
 * This interface provides a way for reading data from a source.
 * <p>The source can be from a database engine, external files, e.g a XML, comma-separated values, and etc. The data
 * read will reconstruct and return in {@link ExpenseManagerTransaction} format.</p>
 * <p><b>Restriction</b>: For now the interface is limited to read from a database file.</p>
 *
 * @see ExpenseManagerTransaction
 */
public interface ExpenseReadable {
  /**
   * Read the expense transaction from the data source and get return the expense transaction as a {@code List}
   *
   * @return a list of {@code ExpenseTransaction} from the data source
   * @throws SQLException when there is problem reading from the database
   */
  List<ExpenseReport> getExpenseTransactions() throws SQLException, IOException;
}
