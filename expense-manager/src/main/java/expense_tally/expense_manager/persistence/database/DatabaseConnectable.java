package expense_tally.expense_manager.persistence.database;

import expense_tally.expense_manager.persistence.ExpenseReadable;
import expense_tally.expense_manager.persistence.ExpenseUpdatable;
import expense_tally.model.persistence.transformation.ExpenseManagerTransaction;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * This interface defines a interface which allows the user of the interface to be able to connect to the database.
 */
public interface DatabaseConnectable {
  /**
   * Returns the connection to the database
   *
   * @return the connection to the database
   * @throws SQLException when there is an error accessing the database
   */
  Connection connect() throws SQLException;

  /**
   * This class provide concert implementation to communicate with a specific <i>MySQL</i> database
   */
  class ExpenseTransactionMapperDatabaseProxy implements ExpenseReadable, ExpenseUpdatable {
    @Override
    public List<ExpenseManagerTransaction> getAllExpenseManagerTransaction() {
      return null;
    }

    @Override
    public boolean add(ExpenseManagerTransaction expenseManagerTransaction) {
      return false;
    }

    @Override
    public boolean clear() {
      return false;
    }
  }
}
