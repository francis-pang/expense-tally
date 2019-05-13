package expense_tally;

import expense_tally.csv_parser.CsvParser;
import expense_tally.csv_parser.model.CsvTransaction;
import expense_tally.expense_manager.ExpenseTransactionDao;
import expense_tally.expense_manager.model.ExpenseManagerMapKey;
import expense_tally.expense_manager.model.ExpenseManagerTransaction;
import expense_tally.reconciliation.ExpenseReconciler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Driver {
  private static final Logger LOGGER = LogManager.getLogger(Driver.class);
  private static String csvFilename;
  private static String databaseFilename;

  public Driver() {
  }

  public void readArgs(String[] args) throws IllegalArgumentException{
    if (args.length < 2) {
      LOGGER.error("Insufficient parameters provided. Exiting now. Args=" + args.toString());
      throw new IllegalArgumentException("Insufficient parameters provided.");
    }
    this.csvFilename = args[0];
    this.databaseFilename = args[1];
  }

  public void reconcileData() throws IOException, SQLException {
    List<CsvTransaction> csvTransactions = new ArrayList<>();
    CsvParser transactionCsvParser = CsvParser.getCsvParser();
    csvTransactions = transactionCsvParser.parseCsvFile(csvFilename);

    ExpenseTransactionDao expenseTransactionDao = ExpenseTransactionDao.getExpenseTransactionDao();
    Map<ExpenseManagerMapKey, List<ExpenseManagerTransaction>> expenseTransactionMap = null;
    try {
      expenseTransactionMap = expenseTransactionDao.getAllExpenseTransactionsFrom(databaseFilename);
    } catch (SQLException e) {
      LOGGER.error(e.getStackTrace());
    }

    reconcileData(csvTransactions, expenseTransactionMap);
  }

  public void reconcileData(List<CsvTransaction> csvTransactions, Map<ExpenseManagerMapKey, List<ExpenseManagerTransaction>> expenseTransactionMap) {
    ExpenseReconciler.reconcileBankData(csvTransactions, expenseTransactionMap);
  }
}
