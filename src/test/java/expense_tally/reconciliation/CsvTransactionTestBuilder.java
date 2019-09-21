package expense_tally.reconciliation;

import expense_tally.csv_parser.model.CsvTransaction;
import expense_tally.csv_parser.model.TransactionType;

import java.time.LocalDate;

public class CsvTransactionTestBuilder {
  private LocalDate transactionDate;
  private String reference = "";

  private double debitAmount = 0;
  private double creditAmount = 0;
  private String transactionRef1 = "";
  private String transactionRef2 = "";
  private String transactionRef3 = "";
  private TransactionType type = null;

  public CsvTransactionTestBuilder(final String transactionDate) {
    String[] transactionDataStringArray = transactionDate.split("-");
    this.transactionDate = LocalDate.of(
        Integer.parseInt(transactionDataStringArray[2]),
        Integer.parseInt(transactionDataStringArray[1]),
        Integer.parseInt(transactionDataStringArray[0]));
  }

  public CsvTransactionTestBuilder reference(String reference) {
    this.reference = reference;
    return this;
  }

  public CsvTransactionTestBuilder debitAmount(double debitAmount) {
    this.debitAmount = debitAmount;
    return this;
  }

  public CsvTransactionTestBuilder creditAmount(double creditAmount) {
    this.creditAmount = creditAmount;
    return this;
  }

  public CsvTransactionTestBuilder transactionRef1(String transactionRef1) {
    this.transactionRef1 = transactionRef1;
    return this;
  }

  public CsvTransactionTestBuilder transactionRef2(String transactionRef2) {
    this.transactionRef2 = transactionRef2;
    return this;
  }

  public CsvTransactionTestBuilder transactionRef3(String transactionRef3) {
    this.transactionRef3 = transactionRef3;
    return this;
  }

  public CsvTransactionTestBuilder transactionType(TransactionType transactionType) {
    type = transactionType;
    return this;
  }

  public CsvTransaction build() {
    return csvTransaction(this);
  }

  private CsvTransaction csvTransaction(CsvTransactionTestBuilder builder) {
    CsvTransaction csvTransaction =  new CsvTransaction(
       builder.transactionDate,
        builder.reference,
        builder.debitAmount,
        builder.creditAmount,
        builder.transactionRef1,
        builder.transactionRef2,
        builder.transactionRef3
    );
    csvTransaction.setType(builder.type);
    return csvTransaction;
  }
}