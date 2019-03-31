package expense_tally;

import expense_tally.model.CsvTransaction.CsvTransaction;
import expense_tally.model.ExpenseManager.ExpenseManagerMapKey;
import expense_tally.model.ExpenseManager.ExpenseManagerTransaction;
import expense_tally.persistence.CsvParser;
import expense_tally.persistence.ExpenseTransactionDao;
import expense_tally.service.ExpenseReconciler;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class Application {
    private static final Logger LOGGER = Logger.getLogger(Application.class.getName());

    public static void main (String[] args) {
        // assumes the current class is called MyLogger

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

        ExpenseTransactionDao expenseTransactionDao = new ExpenseTransactionDao(databaseFile);
        Map<ExpenseManagerMapKey, List<ExpenseManagerTransaction>> expenseTransactionMap = null;
        try {
            expenseTransactionMap = expenseTransactionDao.getAllExpenseTransactions();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Reconcile data
        ExpenseReconciler.reconcileBankData(csvTransactions, expenseTransactionMap);
    }
}
