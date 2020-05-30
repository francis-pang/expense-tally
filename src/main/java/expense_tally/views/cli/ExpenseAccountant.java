package expense_tally.views.cli;

import expense_tally.csv_parser.CsvParsable;
import expense_tally.csv_parser.CsvTransaction;
import expense_tally.expense_manager.persistence.ExpenseReadable;
import expense_tally.expense_manager.persistence.ExpenseReport;
import expense_tally.expense_manager.transformation.ExpenseManagerTransaction;
import expense_tally.expense_manager.transformation.ExpenseTransactionMapper;
import expense_tally.expense_manager.transformation.PaymentMethod;
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
public class ExpenseAccountant {
  private static final Logger LOGGER = LogManager.getLogger(ExpenseAccountant.class);
  private final CsvParsable csvParsable;
  private final ExpenseReadable expenseReadable;
  private final ExpenseTransactionMapper expenseTransactionMapper;
  private final ExpenseReconciler expenseReconciler;

  public ExpenseAccountant(CsvParsable csvParsable,
                           ExpenseReadable expenseReadable,
                           ExpenseTransactionMapper expenseTransactionMapper,
                           ExpenseReconciler expenseReconciler) {
    this.csvParsable = Objects.requireNonNull(csvParsable);
    this.expenseReadable = Objects.requireNonNull(expenseReadable);
    this.expenseTransactionMapper = Objects.requireNonNull(expenseTransactionMapper);
    this.expenseReconciler = Objects.requireNonNull(expenseReconciler);
  }

  /**
   * Reconciles the data from CSV file against the transaction records in the Expense Manager application
   * @param csvFilename
   * @throws IOException if there is error to read the CSV file
   * @throws SQLException if there is error to access the database record in Expense Manager
   */
  public void reconcileData(String csvFilename) throws IOException, SQLException {
    if (csvParsable == null) {
      String errorMessage = "CSV Parsable is null";
      LOGGER.atError().log(errorMessage);
      throw new IllegalArgumentException(errorMessage);
    }
    if (expenseReadable == null) {
      String errorMessage = "Expense Readable is null";
      LOGGER.atError().log(errorMessage);
      throw new IllegalArgumentException(errorMessage);
    }
    if (expenseTransactionMapper == null) {
      String errorMessage = "Expense Transaction Mapper is null";
      LOGGER.atError().log(errorMessage);
      throw new IllegalArgumentException(errorMessage);

    }
    if (expenseReconciler == null) {
      String errorMessage = "Expense Reconciler is null";
      LOGGER.atError().log(errorMessage);
      throw new IllegalArgumentException(errorMessage);
    }
    List<CsvTransaction> bankTransactions;
    try {
      bankTransactions = csvParsable.parseCsvFile(csvFilename);
    } catch (RuntimeException runtimeException) {
      LOGGER.atError().withThrowable(runtimeException)
          .log("Unable to read the CSV file. CSV file location = {}", csvFilename);
      throw runtimeException;
    }
    final Map<Double, Map<PaymentMethod, List<ExpenseManagerTransaction>>> expensesByAmountAndPaymentMethod;
    try {
      List<ExpenseReport> expenseReports = expenseReadable.getExpenseTransactions();
      expensesByAmountAndPaymentMethod = expenseTransactionMapper.mapExpenseReportsToMap(expenseReports);
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
