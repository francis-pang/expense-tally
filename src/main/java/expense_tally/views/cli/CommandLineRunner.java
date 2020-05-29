package expense_tally.views.cli;

import expense_tally.csv_parser.CsvParser;
import expense_tally.expense_manager.persistence.DatabaseConnectable;
import expense_tally.expense_manager.persistence.ExpenseReadable;
import expense_tally.expense_manager.persistence.ExpenseReportReader;
import expense_tally.expense_manager.persistence.SqlLiteConnection;
import expense_tally.reconciliation.ExpenseReconciler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.sql.SQLException;


/**
 * This class acts as the Dependency Injection container. It create all the dependencies and inject into the rest of
 * the class
 */
public class CommandLineRunner {
  private static final Logger LOGGER = LogManager.getLogger(CommandLineRunner.class);

  public static void main(String[] args) {
    final int CSV_FILE_PARSING_ERR_CODE = 2;
    final int DATABASE_ERR_CODE = 3;

    CsvParser csvParser = new CsvParser();
    ExpenseReconciler expenseReconciler = new ExpenseReconciler();
    try {
      ExpenseAccountant expenseAccountant = new ExpenseAccountant(args, csvParser);
      String databaseFileName = expenseAccountant.getDatabaseFilename();
      DatabaseConnectable databaseConnectable = new SqlLiteConnection(databaseFileName);
      ExpenseReadable expenseReadable = new ExpenseReportReader(databaseConnectable);

      expenseAccountant.reconcileData(expenseReadable, expenseReconciler);
    } catch (IOException ioException) {
      LOGGER.atError().withThrowable(ioException).log("Error reading CSV file");
      System.exit(CSV_FILE_PARSING_ERR_CODE);
    } catch (SQLException sqlException) {
      LOGGER.atError().withThrowable(sqlException).log("Error reading from database");
      System.exit(DATABASE_ERR_CODE);
    }
  }
}
