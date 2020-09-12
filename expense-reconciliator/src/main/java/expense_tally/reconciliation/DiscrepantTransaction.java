package expense_tally.reconciliation;

import expense_tally.csv.AbstractCsvTransaction;
import expense_tally.csv.GenericCsvTransaction;
import expense_tally.csv.TransactionType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.LocalDate;
import java.util.StringJoiner;

/**
 * Display a transaction which has discrepancy among the data sources.
 */
public final class DiscrepantTransaction {
  private static final Logger LOGGER = LogManager.getLogger(DiscrepantTransaction.class);

  private LocalDate time;
  private double amount;
  private String description;
  private TransactionType type;

  /**
   * Construct an instance from a {@link GenericCsvTransaction}
   *
   * @param abstractCsvTransaction the CSV transaction with discrepancy
   * @return an instance constructed from {@link GenericCsvTransaction}
   */
  public static DiscrepantTransaction from(AbstractCsvTransaction abstractCsvTransaction) {
    DiscrepantTransaction transaction = new DiscrepantTransaction();
    transaction.amount = extractAmount(abstractCsvTransaction);
    transaction.description = extractDescription(abstractCsvTransaction);
    transaction.time = abstractCsvTransaction.getTransactionDate();
    transaction.type = abstractCsvTransaction.getTransactionType();
    return transaction;
  }

  public LocalDate getTime() {
    return time;
  }

  public double getAmount() {
    return amount;
  }

  public String getDescription() {
    return description;
  }

  public TransactionType getType() {
    return type;
  }

  /**
   * Parse the
   * @param abstractCsvTransaction
   * @return
   */
  private static double extractAmount(AbstractCsvTransaction abstractCsvTransaction) {
    if (abstractCsvTransaction.getDebitAmount() > 0) {
      return abstractCsvTransaction.getDebitAmount();
    } else if (abstractCsvTransaction.getCreditAmount() > 0) {
      LOGGER.atInfo().log("Found a discrepant transaction with credit amount. Credit: {}",
          abstractCsvTransaction::getCreditAmount);
      return abstractCsvTransaction.getCreditAmount();
    } else {
      LOGGER.atInfo().log("Found a discrepant transaction with no credit or debit amount.");
      return 0.0;
    }
  }

  private static String extractDescription(AbstractCsvTransaction abstractCsvTransaction) {
    StringJoiner stringJoiner = new StringJoiner(" ");
    String reference1 = abstractCsvTransaction.getTransactionRef1();
    addReferenceToStringJoiner(stringJoiner, reference1);
    String reference2 = abstractCsvTransaction.getTransactionRef2();
    addReferenceToStringJoiner(stringJoiner, reference2);
    String reference3 = abstractCsvTransaction.getTransactionRef3();
    addReferenceToStringJoiner(stringJoiner, reference3);
    return stringJoiner.toString().trim();
  }


  private static void addReferenceToStringJoiner(StringJoiner stringJoiner, String reference) {
    String parsedReference = parseReference(reference);
    stringJoiner.add(parsedReference);
  }

  private static String parseReference(String ref) {
    return (ref == null || ref.isBlank()) ? "" : ref.trim();
  }
}
