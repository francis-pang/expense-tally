package expense_tally.model.ExpenseManager;

import java.util.Objects;
import java.util.StringJoiner;

public class ExpenseManagerMapKey {
    private PaymentMethod paymentMethod;
    private Double amount;

    public ExpenseManagerMapKey(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ExpenseManagerMapKey)) return false;
        ExpenseManagerMapKey that = (ExpenseManagerMapKey) o;
        return paymentMethod == that.paymentMethod &&
                Objects.equals(amount, that.amount);
    }

    @Override
    public int hashCode() {
        return Objects.hash(paymentMethod, amount);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", ExpenseManagerMapKey.class.getSimpleName() + "[", "]")
                .add("paymentMethod=" + paymentMethod)
                .add("amount=" + amount)
                .toString();
    }
}
