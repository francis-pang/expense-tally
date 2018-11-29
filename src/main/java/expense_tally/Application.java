package expense_tally;

import expense_tally.database.ExpenseTransactionDao;
import expense_tally.model.CsvTransaction;
import expense_tally.model.ExpenseTransaction;
import expense_tally.service.CsvParser;
import expense_tally.service.ExpenseReconciler;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class Application {
    private static final Logger LOGGER = Logger.getLogger(Application.class.getName());

    public static void main (String args[]) {
        // assumes the current class is called MyLogger

        // Configurable
        final String filename = "src/main/resource/csv/3db7c598cadc80893570d55a0243df1c.P000000013229282.csv";
        final String databaseFile = "jdbc:sqlite:D:/code/expense-tally/src/main/resource/database/2018-11-09.db";

        List<CsvTransaction> csvTransactions = new ArrayList<>();
        CsvParser transactionCsvParser = new CsvParser();
        try {
            csvTransactions = transactionCsvParser.parseCsvFile(filename);
        } catch (IOException e) {
            e.printStackTrace();
        }

        ExpenseTransactionDao expenseTransactionDao = new ExpenseTransactionDao(databaseFile);
        Map<Double, List<ExpenseTransaction>> expenseTransactionMap = null;
        try {
            expenseTransactionMap = expenseTransactionDao.getAllExpenseTransactions();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Reconcile data
        ExpenseReconciler.reconcileBankData(csvTransactions, expenseTransactionMap);
    }
}
