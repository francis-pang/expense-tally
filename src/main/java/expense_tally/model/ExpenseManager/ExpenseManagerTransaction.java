package expense_tally.model.ExpenseManager;

import java.time.Instant;
import java.util.Objects;
import java.util.StringJoiner;

public class ExpenseManagerTransaction {
    private Double amount;
    private String category;
    private String subcategory;
    private String paymentMethod;
    private String description;
    private Instant expensedTime;
    private Double referenceAmount;

    public ExpenseManagerTransaction() {
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getSubcategory() {
        return subcategory;
    }

    public void setSubcategory(String subcategory) {
        this.subcategory = subcategory;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Instant getExpensedTime() {
        return expensedTime;
    }

    public void setExpensedTime(Instant expensedTime) {
        this.expensedTime = expensedTime;
    }

    public Double getReferenceAmount() {
        return referenceAmount;
    }

    public void setReferenceAmount(Double referenceAmount) {
        this.referenceAmount = referenceAmount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ExpenseManagerTransaction)) return false;
        ExpenseManagerTransaction that = (ExpenseManagerTransaction) o;
        return Objects.equals(amount, that.amount) &&
                Objects.equals(category, that.category) &&
                Objects.equals(subcategory, that.subcategory) &&
                Objects.equals(paymentMethod, that.paymentMethod) &&
                Objects.equals(description, that.description) &&
                Objects.equals(expensedTime, that.expensedTime) &&
                Objects.equals(referenceAmount, that.referenceAmount);
    }

    @Override
    public int hashCode() {
        return Objects.hash(amount, category, subcategory, paymentMethod, description, expensedTime, referenceAmount);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", ExpenseManagerTransaction.class.getSimpleName() + "[", "]")
                .add("amount=" + amount)
                .add("category='" + category + "'")
                .add("subcategory='" + subcategory + "'")
                .add("paymentMethod='" + paymentMethod + "'")
                .add("description='" + description + "'")
                .add("expensedTime=" + expensedTime)
                .add("referenceAmount=" + referenceAmount)
                .toString();
    }
}
