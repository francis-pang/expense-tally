package expense_tally.csv_parser.model;

import java.util.StringJoiner;

/**
 * The type of transaction.
 * <p>Normally, the value of the transaction type is the abbreviation for the actual description</p>
 */
public enum TransactionType {
    MASTERCARD("MST"),
    NETS("NETS"),
    POINT_OF_SALE("POS"),
    GIRO("IBG"),
    GIRO_COLLECTION("GRO"),
    FAST_PAYMENT("ICT"),
    FAST_COLLECTION("IDT"),
    FUNDS_TRANSFER_I("ITR"),
    FUNDS_TRANSFER_A("ATR"),
    BILL_PAYMENT("BILL"),
    PAY_NOW("PayNow Transfer"),
    CASH_WITHDRAWAL("AWL"),
    INTEREST_EARNED("INT"),
    STANDING_INSTRUCTION("SI"),
    SALARY("SAL"),
    MAS_ELECTRONIC_PAYMENT_SYSTEM_RECEIPT("MER");

    private final String value;

    /**
     * A constructor taking the <i>value</i> of the transaction type
     *
     * @param value representation of the transaction type
     */
    TransactionType(String value) {
        this.value = value;
    }

    /**
     * Returns the value of the transaction type
     *
     * @return the value of the transaction type
     */
    public String value() {
        return this.value;
    }

    /**
     * Returns the {@link TransactionType} given its string form
     *
     * @param transactionTypeStr trsnaction type in string form
     * @return the type of transaction given its string form, null if not found
     */
    public static TransactionType resolve(String transactionTypeStr) {
        for (TransactionType transactionType : values()) {
            if (transactionType.value.equals(transactionTypeStr)) {
                return transactionType;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", TransactionType.class.getSimpleName() + "[", "]")
                .add("value='" + value + "'")
                .toString();
    }
}
