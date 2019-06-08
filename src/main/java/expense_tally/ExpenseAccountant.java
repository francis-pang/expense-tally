package expense_tally;

import expense_tally.csv_parser.CsvParser;
import expense_tally.csv_parser.model.CsvTransaction;
import expense_tally.expense_manager.DatabaseConnectable;
import expense_tally.expense_manager.ExpenseReadable;
import expense_tally.expense_manager.ExpenseReportReader;
import expense_tally.expense_manager.ExpenseTransactionMapper;
import expense_tally.expense_manager.SqlLiteConnection;
import expense_tally.expense_manager.model.ExpenseManagerMapKey;
import expense_tally.expense_manager.model.ExpenseManagerTransaction;
import expense_tally.expense_manager.model.ExpenseReport;
import expense_tally.reconciliation.ExpenseReconciler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * This class do the job to spot any discrepancy between the source of truth and the manual recorded financial
 * records.
 * <p>The word Accountant is used as that is the job of the accountant in the real finance department.</p>
 * <p>This marks the start of the whole program.</p>
 */
public class ExpenseAccountant {
  private static final Logger LOGGER = LogManager.getLogger(ExpenseAccountant.class);
  private static String csvFilename;
  private static String databaseFilename;

  public ExpenseAccountant(String[] args) throws IllegalArgumentException{
    if (args.length < 2) {
      LOGGER.error("Insufficient parameters provided. Exiting now. Args=" + Arrays.toString(args));
      throw new IllegalArgumentException("Insufficient parameters provided.");
    }
    this.csvFilename = args[0];
    this.databaseFilename = args[1];
  }

  public void reconcileData() throws IOException, SQLException {
    List<CsvTransaction> bankTransactions = getCsvTransactionsFrom(csvFilename);

    try {
      Map<ExpenseManagerMapKey, List<ExpenseManagerTransaction>> manuallyRecordedTransactionMap =
          getExpenseManagerTransactionsByKeyFrom(databaseFilename);
      reconcileData(bankTransactions, manuallyRecordedTransactionMap);
    } catch (SQLException ex) {
      LOGGER.error("Problem accessing the database. Database file location=" + databaseFilename,ex);
      throw ex;
    }
  }

  public void reconcileData(List<CsvTransaction> csvTransactions, Map<ExpenseManagerMapKey, List<ExpenseManagerTransaction>> expenseTransactionMap) {
    ExpenseReconciler.reconcileBankData(csvTransactions, expenseTransactionMap);
  }

  private List<CsvTransaction> getCsvTransactionsFrom(String filename) throws IOException {
    CsvParser transactionCsvParser = new CsvParser();
    return transactionCsvParser.parseCsvFile(csvFilename);
  }

  private Map<ExpenseManagerMapKey, List<ExpenseManagerTransaction>>
    getExpenseManagerTransactionsByKeyFrom(String databaseFilename) throws SQLException {
    /*
     * Instead of using the Main as an Inversion of Control container, it will be better to create and initialise the
     *  service whenever you need them. Unless this is a long running process, each Service life cycle is short. They
     *  are one time use, and can be garbage collected after the information is extracted.
     */
    DatabaseConnectable databaseConnectable = new SqlLiteConnection(databaseFilename);
    ExpenseReadable expenseReadable = new ExpenseReportReader(databaseConnectable);
    List<ExpenseReport> expenseReports = expenseReadable.getExpenseTransactions();
    return ExpenseTransactionMapper.mapExpenseReportsToMap(expenseReports);
  }
}
