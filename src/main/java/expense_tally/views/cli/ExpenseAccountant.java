package expense_tally.views.cli;

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
  private String csvFilename;
  private String databaseFilename;

  /**
   *
   * @param args
   * @throws IllegalArgumentException
   */
  public ExpenseAccountant(String[] args) {
    final String DATABASE_PARAMETER = "database-filepath";
    final String CSV_PARAMETER = "csv-filepath";
    final String PARAMETER_PREFIX = "--";
    final char EQUAL_SIGN = '=';
    final String EQUAL_SEPARATOR = "=";
    final char DOUBLE_QUOTATION = '"';

    if (args.length < 2) {
      LOGGER.atError().log("Console receives {} argument", args.length);
      throw new IllegalArgumentException("Need to provide both CSV and database path.");
    }

    /*
     * Expect to received --database-filepath = XXXX --csv-filepath= XXXX
     * Allow 3 format of declaring parameter
     * 1. parameter=XXXX //TODO
     * 2. parameter = xxxxx
     * 3. parameter =xxxxx
     */
    //TODO: For now, we ignore any parameters after 2nd parameters, next time we can handle them.
    if (!isEven(args.length)) {
      LOGGER.atError().log("Argument is not in odd number. Args= {}", () -> Arrays.toString(args));
      throw new IllegalArgumentException("Odd number of parameters provided.");
    }
    this.csvFilename = args[0];
    this.databaseFilename = args[1];
    // Parse first string
    // Strip the equal sign at the place if any

    int argumentIndex = 0;
    while (argumentIndex < args.length) {
      String parameter = args[argumentIndex].trim().replace(EQUAL_SIGN, Character.MIN_VALUE);
      argumentIndex++;
      // Next string can be an equal, or an actual parameter with equal in front
      String value = args[argumentIndex].trim();
      boolean canHaveEqualInFront = true;
      if (EQUAL_SEPARATOR.equals(value)) {
        argumentIndex++;
        value = args[argumentIndex].replace(DOUBLE_QUOTATION, Character.MIN_VALUE); //Ignore the current parameter
        canHaveEqualInFront = false;
      }
      if (value.charAt(0) == EQUAL_SIGN && !canHaveEqualInFront) {
        throw new IllegalArgumentException("Unknown value found: " + value);
      } else {
        switch (parameter) {
          case PARAMETER_PREFIX + DATABASE_PARAMETER:
            databaseFilename = value;
            break;
          case PARAMETER_PREFIX + CSV_PARAMETER:
            csvFilename = value;
            break;
          default:
            throw new IllegalArgumentException("Unknown parameter found: " + parameter);
        }
      }
      argumentIndex++;
    }
  }

  private boolean isEven(int count) {
    return count % 2 == 0;
  }

  public void reconcileData() throws IOException, SQLException {
    List<CsvTransaction> bankTransactions = getCsvTransactionsFrom(csvFilename);
    try {
      Map<ExpenseManagerMapKey, List<ExpenseManagerTransaction>> manuallyRecordedTransactionMap =
          getExpenseManagerTransactionsByKeyFrom(databaseFilename);
      reconcileData(bankTransactions, manuallyRecordedTransactionMap);
    } catch (SQLException ex) {
      LOGGER
          .atError()
          .withThrowable(ex)
          .log("Problem accessing the database. Database file location= {}", databaseFilename);
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

  private Map<ExpenseManagerMapKey, List<ExpenseManagerTransaction>> getExpenseManagerTransactionsByKeyFrom(
      String databaseFilename) throws SQLException {
    /*
     * Instead of using the caller as an Inversion of Control container, it will be better to create and initialise the
     *  service whenever you need them. Unless this is a long running process, each Service life cycle is short. They
     *  are one time use, and can be garbage collected after the information is extracted.
     */
    DatabaseConnectable databaseConnectable = new SqlLiteConnection(databaseFilename);
    ExpenseReadable expenseReadable = new ExpenseReportReader(databaseConnectable);
    List<ExpenseReport> expenseReports = expenseReadable.getExpenseTransactions();
    return ExpenseTransactionMapper.mapExpenseReportsToMap(expenseReports);
  }
}
