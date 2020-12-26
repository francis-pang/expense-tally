package expense_tally.views.cli;

import expense_tally.csv.parser.CsvParser;
import expense_tally.expense_manager.persistence.ExpenseReadable;
import expense_tally.expense_manager.transformation.ExpenseTransactionMapper;
import expense_tally.model.csv.AbstractCsvTransaction;
import expense_tally.model.persistence.database.ExpenseReport;
import expense_tally.model.persistence.transformation.ExpenseManagerTransaction;
import expense_tally.model.persistence.transformation.PaymentMethod;
import expense_tally.reconciliation.ExpenseReconciler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * This class do the job to spot any discrepancy between the source of truth and the manual recorded financial
 * records.
 * <p>The word Accountant is used as that is the job of the accountant in the real finance department.</p>
 * <p>This marks the start of the whole program.</p>
 */
public final class ExpenseAccountant {
  private static final Logger LOGGER = LogManager.getLogger(ExpenseAccountant.class);
  private final ExpenseReadable expenseReadable;
  private final ExpenseReconciler expenseReconciler;

  public ExpenseAccountant(ExpenseReadable expenseReadable,
                           ExpenseReconciler expenseReconciler) {
    this.expenseReadable = Objects.requireNonNull(expenseReadable);
    this.expenseReconciler = Objects.requireNonNull(expenseReconciler);
  }

  /**
   * Reconciles the data from CSV file against the transaction records in the Expense Manager application
   * @throws IOException if there is error to read the CSV file
   * @throws SQLException if there is error to access the database record in Expense Manager
   */
  public void reconcileData(String csvFilename) throws IOException, SQLException {
    List<AbstractCsvTransaction> bankTransactions;
    try {
      bankTransactions = CsvParser.parseCsvFile(csvFilename);
    } catch (RuntimeException runtimeException) {
      LOGGER.atError().withThrowable(runtimeException)
          .log("Unable to read the CSV file. CSV file location = {}", csvFilename);
      throw runtimeException;
    }
    final Map<Double, Map<PaymentMethod, List<ExpenseManagerTransaction>>> expensesByAmountAndPaymentMethod;
    try {
      List<ExpenseReport> expenseReports = expenseReadable.getExpenseTransactions();
      expensesByAmountAndPaymentMethod = ExpenseTransactionMapper.mapExpenseReportsToMap(expenseReports);
    } catch (SQLException ex) {
      LOGGER
          .atError()
          .withThrowable(ex)
          .log("Problem accessing the database.");
      throw ex;
    }
    expenseReconciler.reconcileBankData(bankTransactions, expensesByAmountAndPaymentMethod);
  }
}
