package expense_tally.views.cli;

import expense_tally.csv_parser.CsvParsable;
import expense_tally.csv_parser.CsvTransaction;
import expense_tally.expense_manager.persistence.ExpenseReadable;
import expense_tally.expense_manager.persistence.ExpenseReport;
import expense_tally.expense_manager.transformation.ExpenseManagerTransaction;
import expense_tally.expense_manager.transformation.ExpenseTransactionMapper;
import expense_tally.expense_manager.transformation.PaymentMethod;
import expense_tally.reconciliation.ExpenseReconciler;
import expense_tally.views.AppParameter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
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

  private Map<AppParameter, String> extractOptions(String[] args) {
    final String EQUAL_SEPARATOR = "=";
    Map<AppParameter, String> optionValues = new HashMap<>();
    // Expect to received --database-filepath = XXXX --csv-filepath= XXXX
    // Allow 3 format of declaring parameter
    // 1. parameter=XXXX
    // 2. parameter = xxxxx
    // 3. parameter xxxxx
    int currentPosition = 0;
    while (currentPosition < args.length) {
      String string = args[currentPosition];
      AppParameterValuePair appParameterValuePair;
      // Handling case 1
      if (string.contains(EQUAL_SEPARATOR)) {
        appParameterValuePair = extractOptionKeyValuePair(string);
        if (appParameterValuePair == null) {
          throw new IllegalArgumentException("Unable to recognised option " + string);
        }
      } else {
        currentPosition++;
        String value = getStringFromArray(args, currentPosition, string);
        // Case 2
        if (value.equals(EQUAL_SEPARATOR)) { // Case 3
          currentPosition++;
          value = getStringFromArray(args, currentPosition, string);
        }
        if (value.isBlank()) {
          LOGGER.atError().log("Detect empty value for option {}", string);
          throw new IllegalArgumentException("Unable to process empty value for option " + string);
        }
        appParameterValuePair = AppParameterValuePair.create(string, value);
      }
      optionValues.put(appParameterValuePair.key, appParameterValuePair.value);
      currentPosition++;
    }
    return optionValues;
  }

  private String getStringFromArray(String[] args, int position, String key) {
    if (position == args.length) {
      LOGGER.atError().log("Unable to find value for key {}", key);
      throw new IllegalArgumentException("Unable to find value for a key.");
    }
    return args[position];
  }

  /**
   * Extracts the key value pair of the command line option from the string.
   * <p>If there is not valid key extracted, <b>null</b> will be returned.</p>
   * @param optionString string containing the option and its associated value
   * @return the extracted key-value pair of the command line option if exists, else returns null
   */
  private AppParameterValuePair extractOptionKeyValuePair(String optionString) {
    final char EQUAL_SIGN = '=';
    int indexOfEqualsSign = optionString.indexOf(EQUAL_SIGN);
    String key = optionString.substring(0, indexOfEqualsSign);
    AppParameter appParameter = AppParameter.resolve(key);
    if (appParameter == null) {
      LOGGER.atWarn().log("Unknown app parameter encountered: {}", key);
      return null;
    }
    String value = optionString.substring(indexOfEqualsSign + 1);
    return AppParameterValuePair.create(appParameter, value);
  }

  /**
   * Create a new instance of {@code ExpenseAccountant} based on the command line arguments
   * @param args command line arguments
   * @throws IllegalArgumentException if the arguments provided is malformed or missing compulsory field
   */
  public ExpenseAccountant(String[] args) throws IllegalArgumentException {
    Map<AppParameter, String> optionValues = extractOptions(args);
    if (!haveAllCompulsoryFieldsFilled(optionValues)) {
      LOGGER.atError().log("Missing at least one compulsory parameter. Parameters: {}", optionValues);
      throw new IllegalArgumentException("Need to provide both CSV and database path.");
    }
    csvFilename = optionValues.get(AppParameter.CSV_PATH);
    databaseFilename = optionValues.get(AppParameter.DATABASE_PATH);
  }

  public String getDatabaseFilename() {
    return databaseFilename;
  }

  private boolean haveAllCompulsoryFieldsFilled(Map<AppParameter, String> optionValues) {
    for (AppParameter appParameter : AppParameter.values()) {
      if (appParameter.isCompulsory() && !optionValues.containsKey(appParameter)) {
        return false;
      }
    }
    return true;
  }

  /**
   * Reconciles the data from CSV file against the transaction records in the Expense Manager application
   * @param csvParsable
   * @param expenseReadable
   * @param expenseTransactionMapper
   * @param expenseReconciler
   * @throws IOException if there is error to read the CSV file
   * @throws SQLException if there is error to access the database record in Expense Manager
   */
  public void reconcileData(CsvParsable csvParsable,
                            ExpenseReadable expenseReadable,
                            ExpenseTransactionMapper expenseTransactionMapper,
                            ExpenseReconciler expenseReconciler) throws IOException, SQLException {
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
          .log("Problem accessing the database. Database file location= {}", databaseFilename);
      throw ex;
    }
    expenseReconciler.reconcileBankData(bankTransactions, expensesByAmountAndPaymentMethod);
  }

  static class AppParameterValuePair {
    private AppParameter key;
    private String value;

    public AppParameterValuePair() {
    }

    /**
     * Create a new instance of {@code AppParameterValuePair} based on the {@code key} and {@code value}
     * @param key the application execution parameter
     * @param value value correspond to the application execution parameter
     * @return a new instance of {@code AppParameterValuePair} based on the {@code key} and {@code value}
     */
    static AppParameterValuePair create(AppParameter key, String value) {
      AppParameterValuePair appParameterValuePair = new AppParameterValuePair();
      appParameterValuePair.key = key;
      appParameterValuePair.value = value;
      return appParameterValuePair;
    }

    /**
     * Create a new instance of {@code AppParameterValuePair} based on the {@code appParameter} and {@code value}
     * @param appParameter a String representation of the {@code AppParameter}
     * @param value value correspond to the {@code AppParameter}
     * @return a new instance of {@code AppParameterValuePair} based on the {@code appParameter} and {@code value}
     * @throws IllegalArgumentException if the {@code appParameter} is an invalid {@code AppParameter}
     */
    static AppParameterValuePair create(String appParameter, String value) {
      AppParameter key = AppParameter.resolve(appParameter);
      if (key == null) {
        throw new IllegalArgumentException("appParameter is invalid.");
      }
      AppParameterValuePair appParameterValuePair = new AppParameterValuePair();
      appParameterValuePair.key = key;
      appParameterValuePair.value = value;
      return appParameterValuePair;
    }
  }
}
