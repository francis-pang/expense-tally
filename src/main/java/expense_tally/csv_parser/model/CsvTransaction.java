package expense_tally.csv_parser.model;

import java.time.LocalDate;
import java.util.Objects;
import java.util.StringJoiner;

/**
 * This class describe the raw format of a CSV transaction. This is exactly what each line of CSV record in the DBS
 * exported transaction history looks like.
 * Right now, there is an additional field named <i>TransactionType</i> parked inside this class for convenience sake.
 */
public class CsvTransaction {
    //Attributes
    private LocalDate transactionDate;
    private String reference;
    private double debitAmount;
    private double creditAmount;
    private String transactionRef1;
    private String transactionRef2;
    private String transactionRef3;
    /*
     * TODO: This attribute should be moved out. In a broader sense, the application should let the parser p
     */
    private TransactionType type;

    /*
     * Having studied the builder design pattern, I find that the builder pattern is an overkill for constructing
     * this simple object. Instead, I have just read an chapter off Effective Java (3rd edition) to understand that
     * we can use static factory methods pattern.
     * TODO: Convert to static factory methods
     */
    /**
     * Create a new CSV transaction with all the given parameters.
     * @param transactionDate the transaction date
     * @param reference the abbreviation of the type of transaction
     * @param debitAmount amount of money deducted from the bank account
     * @param creditAmount amount of money credited into the bank account
     * @param transactionRef1 transaction reference line 1, used to store additional label or information of the
     *                        transaction
     * @param transactionRef2 transaction reference line 2, used to store additional label or information of the
     *                        transaction
     * @param transactionRef3 transaction reference line 3, used to store additional label or information of the
     *                        transaction
     */
    public CsvTransaction(LocalDate transactionDate,
                          String reference,
                          double debitAmount,
                          double creditAmount,
                          String transactionRef1,
                          String transactionRef2,
                          String transactionRef3) {
        this.transactionDate = transactionDate;
        this.reference = reference;
        this.debitAmount = debitAmount;
        this.creditAmount = creditAmount;
        this.transactionRef1 = transactionRef1;
        this.transactionRef2 = transactionRef2;
        this.transactionRef3 = transactionRef3;
    }

    /**
     * Creates a new, empty CSV transaction with default double value and null non-primitive object attributes.
     */
    public CsvTransaction() {
    }

    /**
     * Returns transaction date of this CSV transaction
     * @return transaction date when the transaction is reconciled/ marked as completed by the bank
     */
    public LocalDate getTransactionDate() {
        return transactionDate;
    }

    /**
     * Set the transaction date
     * <p>The transaction date refers to the date when the bank regards the transaction as completed/ recorded. This
     * date will always be later than the actual transaction date when the purchase is made. There is no indication
     * of time stamp as well.</p>
     * @param transactionDate transaction date
     */
    public void setTransactionDate(LocalDate transactionDate) {
        this.transactionDate = transactionDate;
    }

    /**
     * Returns the <i>reference</i> of this transaction.
     * <p>Reference represents the shorthand for the type of transaction. The list of transaction type seen is
     * declared in {@link TransactionType}.</p>
     * @return reference of this transaction
     */
    public String getReference() {
        return reference;
    }

    /**
     * Sets the refernce of this transaction
     * @param reference of this transaction
     */
    public void setReference(String reference) {
        this.reference = reference;
    }

    /**
     * Returns the amount of money deducted from the bank account
     * @return the amount of money deducted from the bank account
     */
    public double getDebitAmount() {
        return debitAmount;
    }

    /**
     * Set the amount of money deducted from the bank account
     * @param debitAmount amount of money deducted from the bank account
     */
    public void setDebitAmount(double debitAmount) {
        this.debitAmount = debitAmount;
    }

    /**
     * Returns the amount of money credited into the bank account
     * @return the amount of money credited into the bank account
     */
    public double getCreditAmount() {
        return creditAmount;
    }

    /**
     * Sets the amount of money credited into the bank account
     * @param creditAmount amount of money credited into the bank account
     */
    public void setCreditAmount(double creditAmount) {
        this.creditAmount = creditAmount;
    }

    /**
     * Returns the transaction reference line 1 of this transaction.
     * <p>Transaction reference line 1 contains the transaction date for
     * {@link MasterCard} transactions.</p>
     * @return the transaction reference line 1 of this transaction.
     */
    public String getTransactionRef1() {
        return transactionRef1;
    }

    /**
     * Set the transaction reference line 1 of this transaction.
     * <p>Transaction reference line 1 contains the transaction date for
     * {@link MasterCard} transactions.</p>
     * @param transactionRef1 transaction reference line 1
     */
    public void setTransactionRef1(String transactionRef1) {
        this.transactionRef1 = transactionRef1;
    }

    /**
     * Returns the transaction reference line 2 of this transaction
     * <p>Transaction reference line 2 refers to the card number for
     * {@link MasterCard} transactions. It represents the handwritten description
     * in PayNow transaction.</p>
     * @return the transaction reference line 2 of this transaction
     */
    public String getTransactionRef2() {
        return transactionRef2;
    }

    /**
     * Sets the transaction reference line 2 of this transaction
     * <p>{@link MasterCard} transactions. It represents the handwritten description
     * in PayNow transaction.</p>
     * @param transactionRef2 transaction reference line 2 of this transaction
     */
    public void setTransactionRef2(String transactionRef2) {
        this.transactionRef2 = transactionRef2;
    }

    /**
     * Returns the transaction reference line 3 of this transaction
     * @return the transaction reference line 3 of this transaction
     */
    public String getTransactionRef3() {
        return transactionRef3;
    }

    /**
     * Set the transaction reference line 3 of this transaction
     * @param transactionRef3 transaction reference line 3
     */
    public void setTransactionRef3(String transactionRef3) {
        this.transactionRef3 = transactionRef3;
    }

    /**
     * Returns the type of transaction
     * @return the type of transaction
     */
    public TransactionType getType() {
        return type;
    }

    /**
     * Set the type of transaction specified by <i>type</i>
     * @param type type of transaction
     * @see TransactionType
     */
    public void setType(TransactionType type) {
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CsvTransaction)) return false;
        CsvTransaction that = (CsvTransaction) o;
        return Double.compare(that.debitAmount, debitAmount) == 0 &&
                Double.compare(that.creditAmount, creditAmount) == 0 &&
                Objects.equals(transactionDate, that.transactionDate) &&
                Objects.equals(reference, that.reference) &&
                Objects.equals(transactionRef1, that.transactionRef1) &&
                Objects.equals(transactionRef2, that.transactionRef2) &&
                Objects.equals(transactionRef3, that.transactionRef3) &&
                type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(transactionDate, reference, debitAmount, creditAmount, transactionRef1, transactionRef2, transactionRef3, type);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", CsvTransaction.class.getSimpleName() + "[", "]")
                .add("transactionDate=" + transactionDate)
                .add("reference='" + reference + "'")
                .add("debitAmount=" + debitAmount)
                .add("creditAmount=" + creditAmount)
                .add("transactionRef1='" + transactionRef1 + "'")
                .add("transactionRef2='" + transactionRef2 + "'")
                .add("transactionRef3='" + transactionRef3 + "'")
                .add("type=" + type)
                .toString();
    }
}
