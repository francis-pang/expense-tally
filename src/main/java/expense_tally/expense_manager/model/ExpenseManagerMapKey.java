package expense_tally.expense_manager.model;

import java.util.Objects;
import java.util.StringJoiner;

/**
 * Custom class to store the content of key for a map between the this key and the list of
 * {@link ExpenseManagerTransaction}.
 */
//TODO: This class can be generalised so that the key can be reused
public class ExpenseManagerMapKey implements Comparable {
    private PaymentMethod paymentMethod;
    private Double amount;

    /**
     * Construct a ExpenseManagerMapKey object with the given payment method <i>paymentMethod</i>.
     *
     * @param paymentMethod payment method of the transaction
     */
    public ExpenseManagerMapKey(PaymentMethod paymentMethod, Double amount) {
        this.paymentMethod = paymentMethod;
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
    public int compareTo(Object o) {
        if (this == o) return 0;
        if (!(o instanceof ExpenseManagerMapKey)) {
            return -1;
        }
        ExpenseManagerMapKey that = (ExpenseManagerMapKey) o;
        if (paymentMethod.equals(that.paymentMethod)) {
            return Double.compare(amount, that.amount);
        } else {
            return paymentMethod.compareTo(that.paymentMethod);
        }
    }
}
