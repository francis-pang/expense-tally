package expense_tally.service;

import expense_tally.model.CsvTransaction.CsvTransaction;
import expense_tally.model.ExpenseManager.ExpenseManagerMapKey;
import expense_tally.model.ExpenseManager.ExpenseManagerTransaction;
import expense_tally.model.ExpenseManager.PaymentMethod;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * A service class to perform the object to reconcile the incoming reconciler
 */
public class ExpenseReconciler {
    private static final Logger LOGGER = Logger.getLogger(ExpenseReconciler.class.getName());

    public static void reconcileBankData (List<CsvTransaction> csvTransactions, Map<ExpenseManagerMapKey, List<ExpenseManagerTransaction>> expenseTransactionMap) {
        final int MAXIMUM_TIME_DIFFERENCE_ALLOWED = 24;

        int numberOfNoMatchTransaction = 0;
        for (CsvTransaction csvTransaction : csvTransactions) {
            if (csvTransaction.getDebitAmount() == 0) {
                LOGGER.fine("This is not a debit transaction");
                continue;
            }
            if (csvTransaction.getType() == null) {
                LOGGER.warning("No valid type. Need to investigate this case. " + csvTransaction.toString());
                continue;
            }
            ExpenseManagerMapKey expenseManagerMapKey;
            switch(csvTransaction.getType()) {
                case MASTERCARD:
                    expenseManagerMapKey = new ExpenseManagerMapKey(PaymentMethod.DEBIT_CARD);
                    break;
                case NETS:
                    expenseManagerMapKey = new ExpenseManagerMapKey(PaymentMethod.NETS);
                    break;
                case PayNow:
                    expenseManagerMapKey = new ExpenseManagerMapKey(PaymentMethod.ELECTRONIC_TRANSFER);
                    break;
                case FUNDS_TRANSFER:
                    expenseManagerMapKey = new ExpenseManagerMapKey(PaymentMethod.ELECTRONIC_TRANSFER);
                    break;
                case BILL_PAYMENT:
                    expenseManagerMapKey = new ExpenseManagerMapKey(PaymentMethod.DEBIT_CARD);
                    break;
                default:
                    LOGGER.warning("Found an unknown transaction type: " + csvTransaction.getType());
                    continue;
            }
            expenseManagerMapKey.setAmount(csvTransaction.getDebitAmount());
            List<ExpenseManagerTransaction> expenseManagerTransactionList = expenseTransactionMap.get(expenseManagerMapKey);
            if (expenseManagerTransactionList == null) {
                LOGGER.info("Transaction in the CSV file does not exist in Expense Manager: " + csvTransaction.toString());
                numberOfNoMatchTransaction++;
                continue;
            }
            int noOfMatchingTransaction = 0;
            for(ExpenseManagerTransaction matchingExpenseManagerTransaction : expenseManagerTransactionList) {
                Duration transactionTimeDifference = Duration.between(matchingExpenseManagerTransaction.getExpensedTime(),
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
                    LOGGER.info("Found more than 1 matching transaction for this: " + csvTransaction.toString());
                    break;
            }
        }
        LOGGER.info("Found " + numberOfNoMatchTransaction + " non-matching transactions.");
    }

    private static ZonedDateTime endOfDay(LocalDate date) {
        return LocalDateTime.of(date, LocalTime.MAX).atZone(ZoneId.of("Asia/Singapore"));
    }
}
