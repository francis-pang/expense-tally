package expense_tally.reconciliation.model;

import expense_tally.csv_parser.model.CsvTransaction;
import expense_tally.csv_parser.model.TransactionType;
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
   * Construct an instance from a {@link expense_tally.csv_parser.model.CsvTransaction}
   * @param csvTransaction the CSV transaction with discrepancy
   * @return an instance constructed from {@link expense_tally.csv_parser.model.CsvTransaction}
   */
  public static DiscrepantTransaction from(CsvTransaction csvTransaction) {
    DiscrepantTransaction transaction = new DiscrepantTransaction();
    transaction.setAmount(csvTransaction);
    transaction.setDescription(csvTransaction);
    transaction.time = csvTransaction.getTransactionDate();
    transaction.type = csvTransaction.getType();
    return transaction;
  }

  private void setDescription(CsvTransaction csvTransaction) {
    StringJoiner stringJoiner = new StringJoiner(" ");
    String ref1 = csvTransaction.getTransactionRef1();
    stringJoiner.add(parseReference(ref1));

    String ref2 = csvTransaction.getTransactionRef2();
    stringJoiner.add(parseReference(ref2));

    String ref3 = csvTransaction.getTransactionRef3();
    stringJoiner.add(parseReference(ref3));

    description = stringJoiner.toString().trim();
  }

  private String parseReference(String ref) {
    return (ref == null || ref.isBlank()) ? "" : ref;
  }

  private void setAmount(CsvTransaction csvTransaction) {
    if (csvTransaction.getDebitAmount() > 0) {
      amount = csvTransaction.getDebitAmount();
    } else {
      LOGGER.info("Found a discrepant transaction with credit amount");
      amount = csvTransaction.getCreditAmount();
    }
  }
}
