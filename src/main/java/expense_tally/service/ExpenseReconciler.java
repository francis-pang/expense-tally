package expense_tally.service;

import expense_tally.model.CsvTransaction;
import expense_tally.model.ExpenseTransaction;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * A service class to perform the object to reconcile the incoming reconciler
 */
public class ExpenseReconciler {
    private static final Logger LOGGER = Logger.getLogger(ExpenseReconciler.class.getName());

    public static void reconcileBankData (List<CsvTransaction> csvTransactions, Map<Double, List<ExpenseTransaction>> expenseTransactionMap) {
        final int MAXIMUM_TIME_DIFFERENCE_ALLOWED = 48;

        int numberOfNoMatchTransaction = 0;
        for (CsvTransaction csvTransaction : csvTransactions) {
            if (csvTransaction.getDebitAmount() == 0) {
                LOGGER.fine("This is not a debit transaction");
                continue;
            }
            List<ExpenseTransaction> expenseTransactionList = expenseTransactionMap.get(csvTransaction.getDebitAmount());
            if (expenseTransactionList == null) {
                LOGGER.info("Transaction in the CSV file does not exist in Expense Manager: " + csvTransaction.toString());
                numberOfNoMatchTransaction++;
                continue;
            }
            int noOfMatchingTransaction = 0;
            for(ExpenseTransaction matchingExpenseTransaction : expenseTransactionList) {
                Duration transactionTimeDifference = Duration.between(matchingExpenseTransaction.getExpensedTime(),
                        endOfDay(csvTransaction.getTransactionDate()));
                if(transactionTimeDifference.toHours() <= MAXIMUM_TIME_DIFFERENCE_ALLOWED) {
                    noOfMatchingTransaction++;
                }
            }
            switch(noOfMatchingTransaction) {
                case 0:
                    LOGGER.info("After going through the list of matching amount, transaction in the CSV file does not exist in Expense Manager: " + csvTransaction.toString());
                    numberOfNoMatchTransaction++;
                    break;
                case 1:
                    LOGGER.finer("Found a matching transaction");
                    break;
                default:
                    LOGGER.info("Found more than 1 matching transaction for this");
                    LOGGER.info(csvTransaction.toString());
                    break;
            }
        }
        LOGGER.info("Found " + numberOfNoMatchTransaction + " non-matching transactions.");
    }

    private static LocalDateTime endOfDay(LocalDate date) {
        return LocalDateTime.of(date, LocalTime.MAX);
    }
}
