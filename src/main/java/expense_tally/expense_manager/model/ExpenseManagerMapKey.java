package expense_tally.expense_manager.model;

import java.util.Objects;
import java.util.StringJoiner;

/**
 * Custom class to store the content of key for a map between the this key and the list of
 * {@link ExpenseManagerTransaction}.
 */
//TODO: This class can be generalised so that the key can be reused
public class ExpenseManagerMapKey {
    private PaymentMethod paymentMethod;
    private Double amount;

    /**
     * Construct a ExpenseManagerMapKey object with the given payment method <i>paymentMethod</i>.
     *
     * @param paymentMethod payment method of the transaction
     */
    public ExpenseManagerMapKey(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    /**
     * Returns the payment method of this key
     *
     * @return the payment method of this key
     */
    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    /**
     * Returns the transaction amount of this key
     *
     * @return the transaction amount of this key
     */
    public Double getAmount() {
        return amount;
    }

    /**
     * Sets the transaction amount to be part of the key content for this map
     *
     * @param amount the transaction amount
     */
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
