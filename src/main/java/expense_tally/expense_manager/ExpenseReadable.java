package expense_tally.expense_manager;

import expense_tally.expense_manager.model.ExpenseManagerMapKey;
import expense_tally.expense_manager.model.ExpenseManagerTransaction;
import expense_tally.expense_manager.model.ExpenseReport;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * This interface provides a way for reading data from a source.
 * <p>The source can be from a database engine, external files, e.g a XML, comma-separated values, and etc. The data
 * read will reconstruct and return in {@link expense_tally.expense_manager.model.ExpenseManagerTransaction} format.</p>
 * <p><b>Restriction</b>: For now the interface is limited to read from a database file.</p>
 *
 * @see expense_tally.expense_manager.model.ExpenseManagerTransaction
 */
public interface ExpenseReadable {
    /**
     * Read the expense transaction from a data source and return the expense transactions retrieved based on a
     * pre-defined group key.
     * {@link expense_tally.expense_manager.model.ExpenseManagerMapKey}
     *
     * @return a map of {@code ExpenseTransaction} from the data source
     */
    public Map<ExpenseManagerMapKey, List<ExpenseManagerTransaction>> getExpenseTransactionMap() throws SQLException;

    /**
     * Read the expense transaction from the data source and get return the expense transaction as a {@code List}
     *
     * @return a list of {@code ExpenseTransaction} from the data source
     */
    public List<ExpenseReport> getExpenseTransactions() throws SQLException;
}
