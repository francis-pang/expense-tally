package expense_tally.reconciliation;

import expense_tally.csv_parser.AbstractCsvTransaction;
import expense_tally.csv_parser.TransactionType;
import expense_tally.expense_manager.transformation.ExpenseManagerTransaction;
import expense_tally.expense_manager.transformation.PaymentMethod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Reconciles the expenses between the Expense Manager application and the CSV file.
 */
public final class ExpenseReconciler {
  private static final Logger LOGGER = LogManager.getLogger(ExpenseReconciler.class);
  private static final int MAXIMUM_TIME_DIFFERENCE_ALLOWED = 24;
  private static final String NULL_CSV_TRANSACTION_EXCEPTION_MSG = "Null reference is not an accepted csvTransactions value.";
  private static final String NULL_EXPENSE_TRANSACTION_MAP_EXCEPTION_MSG = "Null reference is not an accepted expenseTransactionMap value.";

  public ExpenseReconciler() { //Default implementation
  }

  /**
   * Reconcile the data in the CSV file against the database record in the Expense Manager database
   * <p>This is a one way matching exercise. The reconciler iterates through each record in the CSV files to match
   * for at least a record in the database</p>
   *
   * @param abstractCsvTransactions                  list of transactions in the CSV file
   * @param expensesByAmountAndPaymentMethod a collection of the database record in the Expense Manager
   * @return the number of transaction that is not found in the CSV
   */
  public List<DiscrepantTransaction> reconcileBankData(
      final List<? extends AbstractCsvTransaction> abstractCsvTransactions,
      final Map<Double, Map<PaymentMethod, List<ExpenseManagerTransaction>>> expensesByAmountAndPaymentMethod) {
    /*
     * Taking context from stack overflow answer(https://stackoverflow.com/a/15210142/1522867), the correct way
     * (https://stackoverflow.com/a/15210142/1522867) in this case it's perfectly ok to throw
     * an unchecked exception like an IllegalArgumentException, which should not be caught.
     */
    if (abstractCsvTransactions == null) {
      throw new IllegalArgumentException(NULL_CSV_TRANSACTION_EXCEPTION_MSG);
    }

    if (expensesByAmountAndPaymentMethod == null) {
      throw new IllegalArgumentException(NULL_EXPENSE_TRANSACTION_MAP_EXCEPTION_MSG);
    }

    List<DiscrepantTransaction> discrepantTransactions = new ArrayList<>();
    for (AbstractCsvTransaction abstractCsvTransaction : abstractCsvTransactions) {
      if (!csvRecordHasMatchingTransaction(abstractCsvTransaction, expensesByAmountAndPaymentMethod)) {
        DiscrepantTransaction discrepantTransaction = DiscrepantTransaction.from(abstractCsvTransaction);
        discrepantTransactions.add(discrepantTransaction);
      }
    }
    final int discrepantTransactionSize = discrepantTransactions.size();
    LOGGER.atInfo().log("Found {} non-matching transactions.", discrepantTransactionSize);
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
        LOGGER.atWarn().log("Unable to resolve transaction type {} to a payment method.", transactionType);
        return null;

    }
  }

  private static int calculateNumberOfMatchingTransactions(final LocalDate csvTransactionDate,
                                                           List<ExpenseManagerTransaction> expenseManagerTransactions) {
    AtomicInteger noOfMatchingTransaction = new AtomicInteger();
    expenseManagerTransactions
        .parallelStream()
        .forEach(expenseManagerTransaction -> {
          Duration transactionTimeDifference = Duration.between(expenseManagerTransaction.getExpendedTime(),
              endOfDay(csvTransactionDate));
          if (transactionTimeDifference.toHours() >= 0 &&
              transactionTimeDifference.toHours() <= MAXIMUM_TIME_DIFFERENCE_ALLOWED) {
            noOfMatchingTransaction.getAndIncrement();
          }
        });
    return noOfMatchingTransaction.get();
  }

  private static boolean csvRecordHasMatchingTransaction(final AbstractCsvTransaction abstractCsvTransaction,
                                                         final Map<Double, Map<PaymentMethod,
                                                             List<ExpenseManagerTransaction>>> expensesByAmountAndPaymentMethod) {
    if (abstractCsvTransaction.getDebitAmount() == 0) {
      LOGGER.atTrace().log("This is not a debit transaction");
      return true;
    }
    double debitAmount = abstractCsvTransaction.getDebitAmount();
    PaymentMethod expensePaymentMethod = mapPaymentMethodFrom(abstractCsvTransaction.getTransactionType());
    if (expensePaymentMethod == null) {
      LOGGER.atWarn().log("Found an unknown transaction type: {}", abstractCsvTransaction);
      return true;
    }
    List<ExpenseManagerTransaction> possibleMatchingExpenses = expensesByAmountAndPaymentMethod
        .getOrDefault(debitAmount, Collections.emptyMap())
        .getOrDefault(expensePaymentMethod, Collections.emptyList());
    LocalDate csvTransactionDate = abstractCsvTransaction.getTransactionDate();
    int matchingTransactionCount = calculateNumberOfMatchingTransactions(csvTransactionDate, possibleMatchingExpenses);
    switch (matchingTransactionCount) {
      case 0:
        LOGGER.atInfo().log("Transaction in the CSV file does not exist in Expense Manager: {}", abstractCsvTransaction);
        return false;
      case 1:
        LOGGER.atTrace().log("Found a matching transaction");
        return true;
      default:
        LOGGER.atInfo().log("Found more than 1 matching transaction for this: {}", abstractCsvTransaction);
        return true;
    }
  }
}
