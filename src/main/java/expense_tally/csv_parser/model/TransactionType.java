package expense_tally.csv_parser.model;

import java.util.StringJoiner;

/**
 * The type of transaction.
 * <p>Normally, the value of the transaction type is the abbreviation for the actual description</p>
 */
public enum TransactionType {
    MASTERCARD ("MST"),
    NETS("NETS"),
    GIRO("IBG"),
    FUNDS_TRANSFER("ITR"),
    BILL_PAYMENT("BILL"),
    PAY_NOW("PayNow Transfer");

    private final String value;

    /**
     * A constructor taking the <i>value</i> of the transaction type
     * @param value representation of the transaction type
     */
    TransactionType(String value) {
        this.value = value;
    }

    /**
     * Returns the value of the transaction type
     * @return the value of the transaction type
     */
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
