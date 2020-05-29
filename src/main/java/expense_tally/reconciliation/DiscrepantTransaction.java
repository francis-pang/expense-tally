package expense_tally.reconciliation;

import expense_tally.csv_parser.CsvTransaction;
import expense_tally.csv_parser.TransactionType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.LocalDate;
import java.util.StringJoiner;

/**
 * Display a transaction which has discrepancy among the data sources.
 */
public class DiscrepantTransaction {
  private static final Logger LOGGER = LogManager.getLogger(DiscrepantTransaction.class);

  private LocalDate time;
  private double amount;
  private String description;
  private TransactionType type;

  /**
   * Construct an instance from a {@link CsvTransaction}
   *
   * @param csvTransaction the CSV transaction with discrepancy
   * @return an instance constructed from {@link CsvTransaction}
   */
  public static DiscrepantTransaction from(CsvTransaction csvTransaction) {
    DiscrepantTransaction transaction = new DiscrepantTransaction();
    transaction.amount = extractAmount(csvTransaction);
    transaction.description = extractDescription(csvTransaction);
    transaction.time = csvTransaction.getTransactionDate();
    transaction.type = csvTransaction.getTransactionType();
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
   * @param csvTransaction
   * @return
   */
  private static double extractAmount(CsvTransaction csvTransaction) {
    if (csvTransaction.getDebitAmount() > 0) {
      return csvTransaction.getDebitAmount();
    } else if (csvTransaction.getCreditAmount() > 0) {
      LOGGER.atInfo().log("Found a discrepant transaction with credit amount. Credit: {}",
          csvTransaction::getCreditAmount);
      return csvTransaction.getCreditAmount();
    } else {
      LOGGER.atInfo().log("Found a discrepant transaction with no credit or debit amount.");
      return 0.0;
    }
  }

  private static String extractDescription(CsvTransaction csvTransaction) {
    StringJoiner stringJoiner = new StringJoiner(" ");
    String reference1 = csvTransaction.getTransactionRef1();
    addReferenceToStringJoiner(stringJoiner, reference1);
    String reference2 = csvTransaction.getTransactionRef2();
    addReferenceToStringJoiner(stringJoiner, reference2);
    String reference3 = csvTransaction.getTransactionRef3();
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
