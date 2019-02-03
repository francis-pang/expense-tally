package expense_tally.model;

import java.time.LocalDate;
import java.util.Objects;
import java.util.StringJoiner;

public class CsvTransaction {
    //Attributes
    private LocalDate transactionDate;
    private String reference;
    private double debitAmount;
    private double creditAmount;
    private String transactionRef1;
    private String transactionRef2;
    private String transactionRef3;

    // Constructor
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

    public CsvTransaction() {
    }

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
                Objects.equals(transactionRef3, that.transactionRef3);
    }

    @Override
    public int hashCode() {
        return Objects.hash(transactionDate, reference, debitAmount, creditAmount, transactionRef1, transactionRef2, transactionRef3);
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
                .toString();
    }
}
