package expense_tally.model;

import java.time.Instant;

public class ExpenseTransaction {
    private Double amount;
    private String category;
    private String subcategory;
    private String paymentMethod;
    private String description;
    private Instant expensedTime;
    private Double referenceAmount;

    public ExpenseTransaction() {
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
        if (!(o instanceof ExpenseTransaction)) return false;

        ExpenseTransaction that = (ExpenseTransaction) o;

        if (!amount.equals(that.amount)) return false;
        if (!category.equals(that.category)) return false;
        if (!subcategory.equals(that.subcategory)) return false;
        if (!paymentMethod.equals(that.paymentMethod)) return false;
        if (description != null ? !description.equals(that.description) : that.description != null) return false;
        if (!expensedTime.equals(that.expensedTime)) return false;
        return referenceAmount != null ? referenceAmount.equals(that.referenceAmount) : that.referenceAmount == null;
    }

    @Override
    public int hashCode() {
        int result = amount.hashCode();
        result = 31 * result + category.hashCode();
        result = 31 * result + subcategory.hashCode();
        result = 31 * result + paymentMethod.hashCode();
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + expensedTime.hashCode();
        result = 31 * result + (referenceAmount != null ? referenceAmount.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ExpenseTransaction{" +
                "amount=" + amount +
                ", category='" + category + '\'' +
                ", subcategory='" + subcategory + '\'' +
                ", paymentMethod='" + paymentMethod + '\'' +
                ", description='" + description + '\'' +
                ", expensedTime=" + expensedTime +
                ", referenceAmount='" + referenceAmount + '\'' +
                '}';
    }
}
