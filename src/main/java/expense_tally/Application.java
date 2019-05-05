package expense_tally;

import expense_tally.csv_parser.model.CsvTransaction;
import expense_tally.expense_manager.model.ExpenseManagerMapKey;
import expense_tally.expense_manager.model.ExpenseManagerTransaction;
import expense_tally.csv_parser.CsvParser;
import expense_tally.expense_manager.ExpenseTransactionDao;
import expense_tally.reconciliation.ExpenseReconciler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Application {
    private static final Logger LOGGER = LogManager.getLogger(Application.class);

    public static void main (String[] args) {
        // Configurable
        final String filename = "src/main/resource/csv/d03521949520f3bd1fc4b9d5f32e40df" +
                ".P000000013229282_01Dec-02Feb" +
                ".csv";
        final String databaseFile = "jdbc:sqlite:D:\\code\\expense-tally\\src\\main\\resource" +
                "\\database\\personal_finance" +
                ".db";

        List<CsvTransaction> csvTransactions = new ArrayList<>();
        CsvParser transactionCsvParser = new CsvParser();
        try {
            csvTransactions = transactionCsvParser.parseCsvFile(filename);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Map<ExpenseManagerMapKey, List<ExpenseManagerTransaction>> expenseTransactionMap = null;
        try {
            ExpenseTransactionDao expenseTransactionDao = new ExpenseTransactionDao(databaseFile);
            expenseTransactionMap = expenseTransactionDao.getAllExpenseTransactions();
        } catch (SQLException e) {
            LOGGER.error(e.getMessage());
        }

        // Reconcile data
        ExpenseReconciler.reconcileBankData(csvTransactions, expenseTransactionMap);
    }
}
