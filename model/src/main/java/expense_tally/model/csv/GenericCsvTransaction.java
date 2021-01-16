package expense_tally.model.csv;

import expense_tally.Exception.StringResolver;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.LocalDate;
import java.util.StringJoiner;

/**
 * This class describe the raw format of a CSV transaction. This is what each line of CSV record in the DBS exported
 * transaction history looks like.
 */
public final class GenericCsvTransaction extends AbstractCsvTransaction {
  private static final Logger LOGGER = LogManager.getLogger(GenericCsvTransaction.class);

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
  private GenericCsvTransaction(LocalDate transactionDate, double debitAmount, double creditAmount,
                                String transactionRef1, String transactionRef2, String transactionRef3,
                                TransactionType transactionType) {
    this.transactionDate = transactionDate;
    this.debitAmount = debitAmount;
    this.creditAmount = creditAmount;
    this.transactionRef1 = transactionRef1;
    this.transactionRef2 = transactionRef2;
    this.transactionRef3 = transactionRef3;
    this.transactionType = transactionType;
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
  public String toString() {
    return new StringJoiner(", ", GenericCsvTransaction.class.getSimpleName() + "[", "]")
        .add("transactionDate=" + transactionDate)
        .add("debitAmount=" + debitAmount)
        .add("creditAmount=" + creditAmount)
        .add("transactionRef1='" + transactionRef1 + "'")
        .add("transactionRef2='" + transactionRef2 + "'")
        .add("transactionRef3='" + transactionRef3 + "'")
        .add("transactionType=" + transactionType)
        .toString();
  }

  public static class Builder {
    private static final String NULL_TRANSACTION_DATE_ERR_MSG = "transactionDate cannot be null.";
    private static final String NEGATIVE_DEBIT_ERR_MSG = "Debit amount cannot be negative.";
    private static final String NEGATIVE_CREDIT_ERR_MSG = "Credit amount cannot be negative.";
    private static final String POSITIVE_CREDIT_DEBIT_ERR_MSG = "Debit and credit cannot be co-exist at same time.";

    private LocalDate transactionDate;
    private final TransactionType transactionType;
    private double debitAmount;
    private double creditAmount = 0.00;
    private String transactionRef1 = StringUtils.EMPTY;
    private String transactionRef2 = StringUtils.EMPTY;
    private String transactionRef3 = StringUtils.EMPTY;

    /**
     * Construct the base unit of the {@code GenericCsvTransaction} builder
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
        LOGGER.atTrace().log("transactionRef1:\"{}\"", StringResolver.resolveNullableString(transactionRef1));
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
        LOGGER.atTrace().log("transactionRef2: \"{}\"", StringResolver.resolveNullableString(transactionRef2));
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
        LOGGER.atTrace().log("transactionRef3 :\"{}\"", StringResolver.resolveNullableString(transactionRef3));
        return this;
      }
      this.transactionRef3 = transactionRef3;
      return this;
    }

    /**
     * Returns an instance of {@code GenericCsvTransaction} created from the fields set on this builder.
     *
     * @return an instance of {@code GenericCsvTransaction} created from the fields set on this builder.
     * @throws MonetaryAmountException if both the debit and credit amount isn't fill up as non-zero value
     */
    public GenericCsvTransaction build() throws MonetaryAmountException {
      if (debitAmount > 0 && creditAmount > 0) {
        throw new MonetaryAmountException(POSITIVE_CREDIT_DEBIT_ERR_MSG);
      }
      return new GenericCsvTransaction(transactionDate, debitAmount, creditAmount, transactionRef1, transactionRef2,
          transactionRef3, transactionType);
    }
  }
}