package expense_tally.model;

import java.time.LocalDate;

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

        if (Double.compare(that.debitAmount, debitAmount) != 0) return false;
        if (Double.compare(that.creditAmount, creditAmount) != 0) return false;
        if (!transactionDate.equals(that.transactionDate)) return false;
        if (!reference.equals(that.reference)) return false;
        if (transactionRef1 != null ? !transactionRef1.equals(that.transactionRef1) : that.transactionRef1 != null)
            return false;
        if (transactionRef2 != null ? !transactionRef2.equals(that.transactionRef2) : that.transactionRef2 != null)
            return false;
        return transactionRef3 != null ? transactionRef3.equals(that.transactionRef3) : that.transactionRef3 == null;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = transactionDate.hashCode();
        result = 31 * result + reference.hashCode();
        temp = Double.doubleToLongBits(debitAmount);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(creditAmount);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + (transactionRef1 != null ? transactionRef1.hashCode() : 0);
        result = 31 * result + (transactionRef2 != null ? transactionRef2.hashCode() : 0);
        result = 31 * result + (transactionRef3 != null ? transactionRef3.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "CsvTransaction{" +
                "transactionDate=" + transactionDate +
                ", reference='" + reference + '\'' +
                ", debitAmount=" + debitAmount +
                ", creditAmount=" + creditAmount +
                ", transactionRef1='" + transactionRef1 + '\'' +
                ", transactionRef2='" + transactionRef2 + '\'' +
                ", transactionRef3='" + transactionRef3 + '\'' +
                '}';
    }
}
