package expense_tally.model.CsvTransaction;

import java.util.StringJoiner;

public enum TransactionType {
    MASTERCARD ("MST"),
    NETS("NETS"),
    GIRO("IBG"),
    FUNDS_TRANSFER("ITR"),
    BILL_PAYMENT("BILL"),
    PayNow("PayNow Transfer");

    private final String value;
    TransactionType(String value) {
        this.value = value;
    }
    public String value() {
        return this.value;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", TransactionType.class.getSimpleName() + "[", "]")
                .add("value='" + value + "'")
                .toString();
    }
}
