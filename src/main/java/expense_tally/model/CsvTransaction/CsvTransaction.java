package expense_tally.model.CsvTransaction;

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
     * @param transactionDate
     * @param reference
     * @param debitAmount
     * @param creditAmount
     * @param transactionRef1
     * @param transactionRef2
     * @param transactionRef3
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
     * return transaction date of this CSV transaction
     * @return transaction date
     */
    public LocalDate getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(LocalDate transactionDate) {
        this.transactionDate = transactionDate;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public double getDebitAmount() {
        return debitAmount;
    }

    public void setDebitAmount(double debitAmount) {
        this.debitAmount = debitAmount;
    }

    public double getCreditAmount() {
        return creditAmount;
    }

    public void setCreditAmount(double creditAmount) {
        this.creditAmount = creditAmount;
    }

    public String getTransactionRef1() {
        return transactionRef1;
    }

    public void setTransactionRef1(String transactionRef1) {
        this.transactionRef1 = transactionRef1;
    }

    public String getTransactionRef2() {
        return transactionRef2;
    }

    public void setTransactionRef2(String transactionRef2) {
        this.transactionRef2 = transactionRef2;
    }

    public String getTransactionRef3() {
        return transactionRef3;
    }

    public void setTransactionRef3(String transactionRef3) {
        this.transactionRef3 = transactionRef3;
    }

    public TransactionType getType() {
        return type;
    }

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
