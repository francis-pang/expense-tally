package expense_tally.expense_manager.model;

import java.time.Instant;
import java.util.Objects;
import java.util.StringJoiner;

public class ExpenseManagerTransaction {
    private Double amount;
    private ExpenseCategory category;
    private ExpenseSubCategory subcategory;
    private PaymentMethod paymentMethod;
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

    public ExpenseCategory getCategory() {
        return category;
    }

    public void setCategory(ExpenseCategory category) {
        this.category = category;
    }

    public ExpenseSubCategory getSubcategory() {
        return subcategory;
    }

    public void setSubcategory(ExpenseSubCategory subcategory) {
        this.subcategory = subcategory;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
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
        return amount.equals(that.amount) &&
                category == that.category &&
                subcategory == that.subcategory &&
                paymentMethod == that.paymentMethod &&
                description.equals(that.description) &&
                expensedTime.equals(that.expensedTime) &&
                referenceAmount.equals(that.referenceAmount);
    }

    @Override
    public int hashCode() {
        return Objects.hash(amount, category, subcategory, paymentMethod, description, expensedTime, referenceAmount);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", ExpenseManagerTransaction.class.getSimpleName() + "[", "]")
                .add("amount=" + amount)
                .add("category=" + category)
                .add("subcategory=" + subcategory)
                .add("paymentMethod=" + paymentMethod)
                .add("description='" + description + "'")
                .add("expensedTime=" + expensedTime)
                .add("referenceAmount=" + referenceAmount)
                .toString();
    }
}
