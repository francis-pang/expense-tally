package expense_tally.reconciliation;

import expense_tally.csv_parser.model.CsvTransaction;
import expense_tally.expense_manager.model.ExpenseManagerMapKey;
import expense_tally.expense_manager.model.ExpenseManagerTransaction;
import expense_tally.expense_manager.model.PaymentMethod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;

/**
 * Reconciles the expenses between the Expense Manager application and the CSV file.
 */
public class ExpenseReconciler {
    private static final Logger LOGGER = LogManager.getLogger(ExpenseReconciler.class);
    private static final int MAXIMUM_TIME_DIFFERENCE_ALLOWED = 24;
    private static final String NULL_CSV_TRANSACTION_EXCEPTION_MSG = "Null reference is not an accepted csvTransactions value.";
    private static final String NULL_EXPENSE_TRANSACTION_MAP_EXCEPTION_MSG = "Null reference is not an accepted expenseTransactionMap value.";

    /**
     * Reconcile the data in the CSV file against the database record in the Expense Manager database
     * <p>This is a one way matching exercise. The reconciler iterates through each record in the CSV files to match
     * for at least a record in the database</p>
     *
     * @param csvTransactions       list of transactions in the CSV file
     * @param expenseTransactionMap a collection of the database record in the Expense Manager
     * @return the number of transaction that is not found in the CSV
     */
    public static int reconcileBankData(
        final List<CsvTransaction> csvTransactions,
        final Map<ExpenseManagerMapKey,
        List<ExpenseManagerTransaction>> expenseTransactionMap) {
        /**
         * Taking context from <a href="https://stackoverflow.com/a/15210142/1522867">stack overflow answer</a>, the
         * correct way <q cite="https://stackoverflow.com/a/15210142/1522867"> In this case it's perfectly ok to throw
         * an unchecked exception like an IllegalArgumentException, which should not be caught</q>
         */
        if (csvTransactions == null) {
            throw new IllegalArgumentException(NULL_CSV_TRANSACTION_EXCEPTION_MSG);
        }

        if (expenseTransactionMap == null) {
            throw new IllegalArgumentException(NULL_EXPENSE_TRANSACTION_MAP_EXCEPTION_MSG);
        }

        int numberOfNoMatchTransaction = 0;
        for (CsvTransaction csvTransaction : csvTransactions) {
            if (csvTransaction.getDebitAmount() == 0) {
                LOGGER.trace("This is not a debit transaction");
                continue;
            }
            if (csvTransaction.getType() == null) {
                LOGGER.warn("No valid type. Need to investigate this case. " + csvTransaction.toString());
                continue;
            }
            ExpenseManagerMapKey expenseManagerMapKey;
            expenseManagerMapKey = switch(csvTransaction.getType()) {
                case MASTERCARD -> new ExpenseManagerMapKey(PaymentMethod.DEBIT_CARD);
                case NETS, POINT_OF_SALE -> new ExpenseManagerMapKey(PaymentMethod.NETS);
                case PAY_NOW -> new ExpenseManagerMapKey(PaymentMethod.ELECTRONIC_TRANSFER);
                case FUNDS_TRANSFER_I, FUNDS_TRANSFER_A, FAST_PAYMENT, FAST_COLLECTION -> new ExpenseManagerMapKey(PaymentMethod.ELECTRONIC_TRANSFER);
                case BILL_PAYMENT -> new ExpenseManagerMapKey(PaymentMethod.I_BANKING);
                case GIRO, GIRO_COLLECTION -> new ExpenseManagerMapKey(PaymentMethod.GIRO);
                default -> null;
                };
            if (expenseManagerMapKey == null) {
                LOGGER.warn("Found an unknown transaction type: " + csvTransaction.getType());
            }

            expenseManagerMapKey.setAmount(csvTransaction.getDebitAmount());
            List<ExpenseManagerTransaction> expenseManagerTransactionList = expenseTransactionMap.get(expenseManagerMapKey);
            if (expenseManagerTransactionList == null) {
                LOGGER.info("Transaction in the CSV file does not exist in Expense Manager: " + csvTransaction.toString());
                numberOfNoMatchTransaction++;
                continue;
            }
            int noOfMatchingTransaction = 0;
            for (ExpenseManagerTransaction matchingExpenseManagerTransaction : expenseManagerTransactionList) {
                Duration transactionTimeDifference = Duration.between(matchingExpenseManagerTransaction.getExpensedTime(),
                        endOfDay(csvTransaction.getTransactionDate()));
                if (transactionTimeDifference.toHours() >= 0 &&
                        transactionTimeDifference.toHours() <= MAXIMUM_TIME_DIFFERENCE_ALLOWED) {
                    noOfMatchingTransaction++;
                }
            }
            switch (noOfMatchingTransaction) {
                case 0:
                    LOGGER.info("Transaction in the CSV file does not exist in Expense Manager: " + csvTransaction.toString());
                    numberOfNoMatchTransaction++;
                    break;
                case 1:
                    LOGGER.trace("Found a matching transaction");
                    break;
                default:
                    LOGGER.info("Found more than 1 matching transaction for this: " + csvTransaction.toString());
                    break;
            }
        }
        LOGGER.info("Found " + numberOfNoMatchTransaction + " non-matching transactions.");
        return numberOfNoMatchTransaction;
    }

    /**
     * Returns an instance of {@link java.time.ZonedDateTime} for a given <i>date</i>
     *
     * @param date representing date
     * @return an instance of {@link java.time.ZonedDateTime} for a given <i>date</i>
     */
    private static ZonedDateTime endOfDay(LocalDate date) {
        return LocalDateTime.of(date, LocalTime.MAX).atZone(ZoneId.of("Asia/Singapore"));
    }
}
