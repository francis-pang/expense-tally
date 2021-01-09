package expense_tally.expense_manager.persistence;

import expense_tally.model.persistence.transformation.ExpenseManagerTransaction;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

/**
 * This interface provides a way for updating the ExpenseManagerTransaction from a data source.
 * <p>
 *   The source can be from a database engine, external files, e.g a XML, comma-separated values, and etc. The data
 * read will reconstruct and return in {@link expense_tally.model.persistence.transformation.ExpenseManagerTransaction}
 * format.
 *
 * </p>
 * <p>
 *   <b>Restriction</b>: For now the interface is limited to create/update/delete entries to a database source
 * </p>
 *
 * @see expense_tally.model.persistence.transformation.ExpenseManagerTransaction
 */
public interface ExpenseUpdatable {
  /**
   * Retrieves all {@link ExpenseManagerTransaction} entries from the data source
   * @return all {@link ExpenseManagerTransaction} entries from the data source
   */
  List<ExpenseManagerTransaction> getAllExpenseManagerTransaction() throws IOException, SQLException;

  /**
   * Adds a new {@link ExpenseManagerTransaction} to the data source. Returns if this data source changes as a result
   * .
   * @param expenseManagerTransaction
   * @return true if this data source changes. Otherwise, false.
   */
  boolean add(ExpenseManagerTransaction expenseManagerTransaction) throws IOException, SQLException;

  /**
   * Removes all {@link ExpenseManagerTransaction} from this data source. The data source will be empty after this
   * method returns. Return true if the deletion succeeds.
   * @return true if the deletion succeeds. Otherwise, false.
   */
  boolean clear();
}
