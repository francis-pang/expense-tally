package expense_tally.views.cli;

import expense_tally.csv.parser.CsvParser;
import expense_tally.expense_manager.mapper.ExpenseManagerTransactionMapper;
import expense_tally.expense_manager.persistence.ExpenseReportReadable;
import expense_tally.expense_manager.persistence.ExpenseUpdatable;
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
  private final ExpenseReportReadable expenseReportReadable;
  private final ExpenseUpdatable expenseUpdatable;

  public ExpenseAccountant(ExpenseReportReadable expenseReportReadable) {
    this.expenseReportReadable = Objects.requireNonNull(expenseReportReadable);
    this.expenseUpdatable = null;
  }

  public ExpenseAccountant(ExpenseReportReadable expenseReportReadable, ExpenseUpdatable expenseUpdatable) {
    this.expenseReportReadable = expenseReportReadable;
    this.expenseUpdatable = expenseUpdatable;
  }

  /**
   * Reconciles the data from CSV file against the transaction records in the Expense Manager application
   * @param csvFilename filename of the comma-separated value file
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
      boolean deleteResult = expenseUpdatable.clear();
      LOGGER.atDebug().log("Table is cleared:{}", deleteResult);
      List<ExpenseReport> expenseReports = expenseReportReadable.getExpenseTransactions();
      List<ExpenseManagerTransaction> expenseManagerTransactions =
          ExpenseTransactionMapper.mapExpenseReports(expenseReports);
      if (expenseUpdatable != null) {
        for (ExpenseManagerTransaction expenseManagerTransaction : expenseManagerTransactions) {
          expenseUpdatable.add(expenseManagerTransaction);
        }
      }
      expensesByAmountAndPaymentMethod =
          ExpenseTransactionMapper.convertToTableOfAmountAndPaymentMethod(expenseManagerTransactions);
    } catch (SQLException ex) {
      LOGGER
          .atError()
          .withThrowable(ex)
          .log("Problem accessing the database.");
      throw ex;
    }
    ExpenseReconciler.reconcileBankData(bankTransactions, expensesByAmountAndPaymentMethod);
  }
}
