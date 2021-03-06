package expense_tally.reconciliation;

import expense_tally.model.csv.GenericCsvTransaction;
import expense_tally.model.csv.MonetaryAmountException;
import expense_tally.model.csv.TransactionType;

import java.time.LocalDate;

public class CsvTransactionTestBuilder {
  private LocalDate transactionDate = LocalDate.of(2009, 4, 24);

  private double debitAmount = 0.8;
  private double creditAmount = 0;
  private String transactionRef1 = "KOUFU PTE LTD SI NG 24APR,5548-2741-0014-1067";
  private String transactionRef2 = "";
  private String transactionRef3 = "";
  private TransactionType type = TransactionType.MASTERCARD;

  public CsvTransactionTestBuilder() {
  }

  public CsvTransactionTestBuilder transactionDate(final int year, int month, int day) {
    this.transactionDate = LocalDate.of(year, month, day);
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

  public GenericCsvTransaction build() {
    try {
      GenericCsvTransaction genericCsvTransaction = new GenericCsvTransaction
          .Builder(transactionDate, type, debitAmount)
          .creditAmount(creditAmount)
          .transactionRef1(transactionRef1)
          .transactionRef2(transactionRef2)
          .transactionRef3(transactionRef3)
          .build();
      return genericCsvTransaction;
    } catch (MonetaryAmountException e) {
      throw new IllegalStateException(e);
    }
  }
}