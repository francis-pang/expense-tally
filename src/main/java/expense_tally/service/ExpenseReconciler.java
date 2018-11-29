package expense_tally.service;

import expense_tally.model.CsvTransaction;
import expense_tally.model.ExpenseTransaction;
import expense_tally.util.TemporalUtil;

import java.time.Duration;
import java.time.ZoneOffset;
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
                //LOGGER.fine("Comparing " + csvTransaction.getTransactionDate() + " vs " + LocalDate.ofInstant(matchingExpenseTransaction.getExpensedTime(), ZoneId.of("UTC").normalized()));
                Duration transactionTimeDifference = Duration.between(matchingExpenseTransaction.getExpensedTime(),
                        TemporalUtil.atEndOfDay(csvTransaction.getTransactionDate()).toInstant(ZoneOffset.UTC));
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
}
