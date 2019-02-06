package expense_tally.model.ExpenseManager;

public enum PaymentMethod {
    CASH("Cash"),
    CREDIT_CARD("Credit Card"),
    ELECTRONIC_TRANSFER("Electronic Transfer"),
    NETS("NETS"),
    GIRO("Giro"),
    DEBIT_CARD("Debit"),
    EZ_LINK("Ez-link"),
    GRAY_PAY("Grab Pay");
    private String value;

    PaymentMethod(String value) {
        this.value = value;
    }

    public String value() {
        return this.value;
    }

    public static PaymentMethod resolve(String value) {
        if (value == null || "".equals(value.trim())) {
            return null;
        }
        for (PaymentMethod paymentMethod : values()) {
            if (paymentMethod.value.equals(value)) {
                return paymentMethod;
            }
        }
        return null;
    }
}
