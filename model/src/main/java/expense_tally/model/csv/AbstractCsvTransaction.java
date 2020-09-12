package expense_tally.model.csv;

import java.time.LocalDate;
import java.util.Objects;
import java.util.StringJoiner;

public abstract class AbstractCsvTransaction {
  //Attributes
  protected LocalDate transactionDate;
  protected double debitAmount;
  protected double creditAmount;
  protected String transactionRef1;
  protected String transactionRef2;
  protected String transactionRef3;
  protected TransactionType transactionType;

  protected AbstractCsvTransaction() {
  }

  /**
   * Returns transaction date of this CSV transaction
   *
   * @return transaction date when the transaction is reconciled/ marked as completed by the bank
   */
  public LocalDate getTransactionDate() {
    return transactionDate;
  }

  /**
   * Returns the amount of money deducted from the bank account
   *
   * @return the amount of money deducted from the bank account
   */
  public double getDebitAmount() {
    return debitAmount;
  }

  /**
   * Returns the amount of money credited into the bank account
   *
   * @return the amount of money credited into the bank account
   */
  public double getCreditAmount() {
    return creditAmount;
  }

  /**
   * Returns the transaction reference line 1 of this transaction.
   * <p>Transaction reference line 1 contains the transaction date for
   * {@link MasterCard} transactions.</p>
   *
   * @return the transaction reference line 1 of this transaction.
   */
  public String getTransactionRef1() {
    return transactionRef1;
  }

  /**
   * Returns the transaction reference line 2 of this transaction
   * <p>Transaction reference line 2 refers to the card number for
   * {@link MasterCard} transactions. It represents the handwritten description
   * in PayNow transaction.</p>
   *
   * @return the transaction reference line 2 of this transaction
   */
  public String getTransactionRef2() {
    return transactionRef2;
  }

  /**
   * Returns the transaction reference line 3 of this transaction
   *
   * @return the transaction reference line 3 of this transaction
   */
  public String getTransactionRef3() {
    return transactionRef3;
  }

  /**
   * Returns the type of transaction
   *
   * @return the type of transaction
   */
  public TransactionType getTransactionType() {
    return transactionType;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof AbstractCsvTransaction)) {
      return false;
    }
    AbstractCsvTransaction that = (AbstractCsvTransaction) o;
    return Double.compare(that.debitAmount, debitAmount) == 0 &&
        Double.compare(that.creditAmount, creditAmount) == 0 &&
        Objects.equals(transactionDate, that.transactionDate) &&
        Objects.equals(transactionRef1, that.transactionRef1) &&
        Objects.equals(transactionRef2, that.transactionRef2) &&
        Objects.equals(transactionRef3, that.transactionRef3) &&
        transactionType == that.transactionType;
  }

  @Override
  public int hashCode() {
    return Objects.hash(transactionDate, debitAmount, creditAmount, transactionRef1, transactionRef2,
        transactionRef3, transactionType);
  }

  @Override
  public String toString() {
    return new StringJoiner(", ", AbstractCsvTransaction.class.getSimpleName() + "[", "]")
        .add("transactionDate=" + transactionDate)
        .add("debitAmount=" + debitAmount)
        .add("creditAmount=" + creditAmount)
        .add("transactionRef1='" + transactionRef1 + "'")
        .add("transactionRef2='" + transactionRef2 + "'")
        .add("transactionRef3='" + transactionRef3 + "'")
        .add("transactionType=" + transactionType)
        .toString();
  }
}
