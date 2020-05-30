package expense_tally.csv_parser;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.LocalDate;
import java.util.Objects;
import java.util.StringJoiner;

/**
 * This class describe the raw format of a CSV transaction. This is what each line of CSV record in the DBS exported
 * transaction history looks like.
 */
public class CsvTransaction {
  private static final Logger LOGGER = LogManager.getLogger(CsvTransaction.class);
  //Attributes
  protected LocalDate transactionDate;
  protected double debitAmount;
  protected double creditAmount;
  protected String transactionRef1;
  protected String transactionRef2;
  protected String transactionRef3;
  protected TransactionType transactionType;

  /**
   * Creates a new, empty CSV transaction with default double value and null non-primitive object attributes.
   */
  protected CsvTransaction() {
  }

  /**
   * Create a new CSV transaction with all the given parameters.
   *
   * @param transactionDate the transaction date
   * @param transactionType the abbreviation of the type of transaction
   * @param debitAmount     amount of money deducted from the bank account
   * @param creditAmount    amount of money credited into the bank account
   * @param transactionRef1 transaction reference line 1, used to store additional label or information of the
   *                        transaction
   * @param transactionRef2 transaction reference line 2, used to store additional label or information of the
   *                        transaction
   * @param transactionRef3 transaction reference line 3, used to store additional label or information of the
   *                        transaction
   */
  public static CsvTransaction of(LocalDate transactionDate,
                                  TransactionType transactionType,
                                  double debitAmount,
                                  double creditAmount,
                                  String transactionRef1,
                                  String transactionRef2,
                                  String transactionRef3) {

    /*
     * Having studied the builder design pattern, I find that the builder pattern is an overkill for constructing
     * this simple object. Instead, I have just read an chapter off Effective Java (3rd edition) to understand that
     * we can use static factory methods pattern.
     */
    CsvTransaction csvTransaction = new CsvTransaction();
    csvTransaction.transactionDate = transactionDate;
    csvTransaction.transactionType = transactionType;
    csvTransaction.debitAmount = debitAmount;
    csvTransaction.creditAmount = creditAmount;
    csvTransaction.transactionRef1 = transactionRef1;
    csvTransaction.transactionRef2 = transactionRef2;
    csvTransaction.transactionRef3 = transactionRef3;
    return csvTransaction;
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

  /**
   * Set the type of transaction specified by <i>type</i>
   *
   * @param transactionType type of transaction
   * @see TransactionType
   */
  public void setTransactionType(TransactionType transactionType) {
    this.transactionType = transactionType;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    CsvTransaction that = (CsvTransaction) o;
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
    return new StringJoiner(", ", CsvTransaction.class.getSimpleName() + "[", "]")
        .add("transactionDate=" + transactionDate)
        .add("debitAmount=" + debitAmount)
        .add("creditAmount=" + creditAmount)
        .add("transactionRef1='" + transactionRef1 + "'")
        .add("transactionRef2='" + transactionRef2 + "'")
        .add("transactionRef3='" + transactionRef3 + "'")
        .add("type=" + transactionType)
        .toString();
  }

  public static class Builder {
    private static final String NULL_TRANSACTION_DATE_ERR_MSG = "transactionDate cannot be null.";
    private static final String NEGATIVE_DEBIT_ERR_MSG = "Debit amount cannot be negative.";
    private static final String NEGATIVE_CREDIT_ERR_MSG = "Credit amount cannot be negative.";
    private static final String POSITIVE_CREDIT_DEBIT_ERR_MSG = "Debit and credit cannot be co-exist at same time.";
    private static final String NULL_EMPTY_STRING_POST_FIX_MSG = " is null or blank, not making any changes.";

    private LocalDate transactionDate;
    private TransactionType transactionType;
    private double debitAmount;
    private double creditAmount = 0.00;
    private String transactionRef1 = "";
    private String transactionRef2 = "";
    private String transactionRef3 = "";

    /**
     * Construct the base unit of the {@code CsvTransaction} builder
     *
     * @param transactionDate the transaction date
     * @param transactionType the abbreviation of the type of transaction
     * @param debitAmount     amount of money deducted from the bank account
     */
    public Builder(LocalDate transactionDate, TransactionType transactionType, double debitAmount) {
      setTransactionDate(transactionDate);
      this.transactionType = transactionType;
      setDebitAmount(debitAmount);
    }

    private void setTransactionDate(LocalDate transactionDate) {
      if (transactionDate == null) {
        throw new IllegalArgumentException(NULL_TRANSACTION_DATE_ERR_MSG);
      }
      this.transactionDate = transactionDate;
    }

    private void setDebitAmount(double debitAmount) {
      if (debitAmount < 0) {
        throw new IllegalArgumentException(NEGATIVE_DEBIT_ERR_MSG);
      }
      this.debitAmount = debitAmount;
    }

    /**
     * Sets the transactionRef1.
     * <p>This method allow override of the previous value set.</p>
     *
     * @param creditAmount amount of money credited into the bank account
     * @return This builder.
     */
    public Builder creditAmount(double creditAmount) {
      if (creditAmount < 0) {
        throw new IllegalArgumentException(NEGATIVE_CREDIT_ERR_MSG);
      }
      this.creditAmount = creditAmount;
      return this;
    }

    /**
     * Sets the transactionRef1.
     * <p>This method allow override of the previous value set.</p>
     *
     * @param transactionRef1 transaction reference line 1, used to store additional label or information of the
     *                        transaction
     * @return This builder.
     */
    public Builder transactionRef1(String transactionRef1) {
      // This setter method can be generalised for all transaction reference string using Reflection. However, using
      // reflection is error prone and computationally expensive. Hence I have decided to deny the DRY principal and
      // duplicate the setter code.
      if (transactionRef1 == null || transactionRef1.isBlank()) {
        LOGGER.atTrace().log("transactionRef1 {}", NULL_EMPTY_STRING_POST_FIX_MSG);
        return this;
      }
      this.transactionRef1 = transactionRef1;
      return this;
    }

    /**
     * Sets the transactionRef2.
     * <p>This method allow override of the previous value set.</p>
     *
     * @param transactionRef2 transaction reference line 2, used to store additional label or information of the
     *                        transaction
     * @return This builder.
     */
    public Builder transactionRef2(String transactionRef2) {
      if (transactionRef2 == null || transactionRef2.isBlank()) {
        LOGGER.atTrace().log("transactionRef2 {}", NULL_EMPTY_STRING_POST_FIX_MSG);
        return this;
      }
      this.transactionRef2 = transactionRef2;
      return this;
    }

    /**
     * Sets the transactionRef3.
     * <p>This method allow override of the previous value set.</p>
     *
     * @param transactionRef3 transaction reference line 3, used to store additional label or information of the
     *                        transaction
     * @return This builder.
     */
    public Builder transactionRef3(String transactionRef3) {
      if (transactionRef3 == null || transactionRef3.isBlank()) {
        LOGGER.atTrace().log("transactionRef3 {}", NULL_EMPTY_STRING_POST_FIX_MSG);
        return this;
      }
      this.transactionRef3 = transactionRef3;
      return this;
    }

    /**
     * Returns an instance of {@code CsvTransaction} created from the fields set on this builder.
     *
     * @return an instance of {@code CsvTransaction} created from the fields set on this builder.
     * @throws MonetaryAmountException if both the debit and credit amount isn't fill up as non-zero value
     */
    public CsvTransaction build() throws MonetaryAmountException {
      if (debitAmount > 0 && creditAmount > 0) {
        throw new MonetaryAmountException(POSITIVE_CREDIT_DEBIT_ERR_MSG);
      }
      return CsvTransaction.of(
          transactionDate,
          transactionType,
          debitAmount,
          creditAmount,
          transactionRef1,
          transactionRef2,
          transactionRef3
      );
    }
  }
}