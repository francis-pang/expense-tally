package expense_tally.reconciliation;

import expense_tally.csv_parser.model.CsvTransaction;
import expense_tally.csv_parser.model.TransactionType;
import expense_tally.expense_manager.model.ExpenseManagerMapKey;
import expense_tally.expense_manager.model.ExpenseManagerTransaction;
import expense_tally.expense_manager.model.PaymentMethod;
import expense_tally.reconciliation.model.DiscrepantTransaction;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
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

  private ExpenseReconciler() {
    throw new IllegalStateException("Shouldn't be able to initialise " + this.getClass().getName());
  }

  /**
   * Reconcile the data in the CSV file against the database record in the Expense Manager database
   * <p>This is a one way matching exercise. The reconciler iterates through each record in the CSV files to match
   * for at least a record in the database</p>
   *
   * @param csvTransactions       list of transactions in the CSV file
   * @param expenseTransactionMap a collection of the database record in the Expense Manager
   * @return the number of transaction that is not found in the CSV
   */
  public static List<DiscrepantTransaction> reconcileBankData(
      final List<CsvTransaction> csvTransactions,
      final Map<ExpenseManagerMapKey, List<ExpenseManagerTransaction>> expenseTransactionMap) {
    /*
     * Taking context from stack overflow answer(https://stackoverflow.com/a/15210142/1522867), the correct way
     * (https://stackoverflow.com/a/15210142/1522867) in this case it's perfectly ok to throw
     * an unchecked exception like an IllegalArgumentException, which should not be caught.
     */
    if (csvTransactions == null) {
      throw new IllegalArgumentException(NULL_CSV_TRANSACTION_EXCEPTION_MSG);
    }

    if (expenseTransactionMap == null) {
      throw new IllegalArgumentException(NULL_EXPENSE_TRANSACTION_MAP_EXCEPTION_MSG);
    }

    List<DiscrepantTransaction> discrepantTransactions = new ArrayList<>();
    for (CsvTransaction csvTransaction : csvTransactions) {
      if (!csvRecordHasMatchingTransaction(csvTransaction, expenseTransactionMap)) {
        DiscrepantTransaction discrepantTransaction = DiscrepantTransaction.from(csvTransaction);
        discrepantTransactions.add(discrepantTransaction);
      }
    }
    final int discrepantTransactionSize = discrepantTransactions.size();
    LOGGER.info("Found {} non-matching transactions.", discrepantTransactionSize);
    return discrepantTransactions;
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

  /**
   * Returns the equivalence mapping transaction type in the Expense Manager when given the <i>transactionType</i>
   *
   * @param transactionType Transaction type retrieve from CSV file
   * @return the equivalence mapping transaction type in the Expense Manager when given the <i>transactionType</i>
   */
  private static PaymentMethod mapPaymentMethodFrom(TransactionType transactionType) {
    if (transactionType == null) {
      return null;
    }
    switch (transactionType) {
      case MASTERCARD:
        return PaymentMethod.DEBIT_CARD;
      case NETS:
      case POINT_OF_SALE:
        return PaymentMethod.NETS;
      case PAY_NOW:
      case FUNDS_TRANSFER_I:
      case FUNDS_TRANSFER_A:
      case FAST_PAYMENT:
      case FAST_COLLECTION:
        return PaymentMethod.ELECTRONIC_TRANSFER;
      case BILL_PAYMENT:
        return PaymentMethod.I_BANKING;
      case GIRO:
      case GIRO_COLLECTION:
        return PaymentMethod.GIRO;
      default:
        LOGGER.warn("Unable to resolve transaction type {} to a payment method.", transactionType);
        return null;

    }
  }

  private static int calculateNumberOfMatchingTransactions(final LocalDate csvTransactionDate,
                                                           final List<ExpenseManagerTransaction> expenseManagerTransactionList) {
    int noOfMatchingTransaction = 0;
    for (ExpenseManagerTransaction matchingExpenseManagerTransaction : expenseManagerTransactionList) {
      Duration transactionTimeDifference = Duration.between(matchingExpenseManagerTransaction.getExpensedTime(),
          endOfDay(csvTransactionDate));
      if (transactionTimeDifference.toHours() >= 0 && transactionTimeDifference.toHours() <= MAXIMUM_TIME_DIFFERENCE_ALLOWED) {
        noOfMatchingTransaction++;
      }
    }
    return noOfMatchingTransaction;
  }

  private static boolean csvRecordHasMatchingTransaction(final CsvTransaction csvTransaction,
                                                         final Map<ExpenseManagerMapKey, List<ExpenseManagerTransaction>> expenseTransactionMap) {
    if (csvTransaction.getDebitAmount() == 0) {
      LOGGER.trace("This is not a debit transaction");
      return true;
    }
    PaymentMethod expensePaymentMethod = mapPaymentMethodFrom(csvTransaction.getTransactionType());
    if (expensePaymentMethod == null) {
      LOGGER.warn("Found an unknown transaction type: {}", csvTransaction);
      return true;
    }
    ExpenseManagerMapKey expenseManagerMapKey = new ExpenseManagerMapKey(expensePaymentMethod, csvTransaction.getDebitAmount());
    List<ExpenseManagerTransaction> expenseManagerTransactionList = expenseTransactionMap.get(expenseManagerMapKey);
    if (expenseManagerTransactionList == null) {
      LOGGER.info("Transaction in the CSV file does not exist in Expense Manager: {}", csvTransaction);
      return false;
    }
    switch (calculateNumberOfMatchingTransactions(csvTransaction.getTransactionDate(),
        expenseManagerTransactionList)) {
      case 0:
        LOGGER.info("Transaction in the CSV file does not exist in Expense Manager: {}", csvTransaction);
        return false;
      case 1:
        LOGGER.trace("Found a matching transaction");
        return true;
      default:
        LOGGER.info("Found more than 1 matching transaction for this: {}", csvTransaction);
        return true;
    }
  }
}
