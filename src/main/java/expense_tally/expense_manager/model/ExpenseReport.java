package expense_tally.expense_manager.model;

import java.util.Objects;
import java.util.StringJoiner;

public class ExpenseReport {
    private int id;
    private String account;
    private String amount;
    private String category;
    private String subcategory;
    private String paymentMethod;
    private String description;
    private long expensed;
    private long modified;
    private String referenceNumber;
    private String status;
    private String property;
    private String property2;
    private String property3;
    private String property4;
    private String property5;
    private String tax;
    private String expenseTag;

    public ExpenseReport() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
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

    public long getExpensed() {
        return expensed;
    }

    public void setExpensed(long expensed) {
        this.expensed = expensed;
    }

    public long getModified() {
        return modified;
    }

    public void setModified(long modified) {
        this.modified = modified;
    }

    public String getReferenceNumber() {
        return referenceNumber;
    }

    public void setReferenceNumber(String referenceNumber) {
        this.referenceNumber = referenceNumber;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    public String getProperty2() {
        return property2;
    }

    public void setProperty2(String property2) {
        this.property2 = property2;
    }

    public String getProperty3() {
        return property3;
    }

    public void setProperty3(String property3) {
        this.property3 = property3;
    }

    public String getProperty4() {
        return property4;
    }

    public void setProperty4(String property4) {
        this.property4 = property4;
    }

    public String getProperty5() {
        return property5;
    }

    public void setProperty5(String property5) {
        this.property5 = property5;
    }

    public String getTax() {
        return tax;
    }

    public void setTax(String tax) {
        this.tax = tax;
    }

    public String getExpenseTag() {
        return expenseTag;
    }

    public void setExpenseTag(String expenseTag) {
        this.expenseTag = expenseTag;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ExpenseReport)) return false;
        ExpenseReport that = (ExpenseReport) o;
        return id == that.id &&
                expensed == that.expensed &&
                modified == that.modified &&
                Objects.equals(account, that.account) &&
                Objects.equals(amount, that.amount) &&
                Objects.equals(category, that.category) &&
                Objects.equals(subcategory, that.subcategory) &&
                Objects.equals(paymentMethod, that.paymentMethod) &&
                Objects.equals(description, that.description) &&
                Objects.equals(referenceNumber, that.referenceNumber) &&
                Objects.equals(status, that.status) &&
                Objects.equals(property, that.property) &&
                Objects.equals(property2, that.property2) &&
                Objects.equals(property3, that.property3) &&
                Objects.equals(property4, that.property4) &&
                Objects.equals(property5, that.property5) &&
                Objects.equals(tax, that.tax) &&
                Objects.equals(expenseTag, that.expenseTag);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, account, amount, category, subcategory, paymentMethod, description, expensed, modified, referenceNumber, status, property, property2, property3, property4, property5, tax, expenseTag);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", ExpenseReport.class.getSimpleName() + "[", "]")
                .add("id=" + id)
                .add("account='" + account + "'")
                .add("amount='" + amount + "'")
                .add("category='" + category + "'")
                .add("subcategory='" + subcategory + "'")
                .add("paymentMethod='" + paymentMethod + "'")
                .add("description='" + description + "'")
                .add("expensed=" + expensed)
                .add("modified=" + modified)
                .add("referenceNumber='" + referenceNumber + "'")
                .add("status='" + status + "'")
                .add("property='" + property + "'")
                .add("property2='" + property2 + "'")
                .add("property3='" + property3 + "'")
                .add("property4='" + property4 + "'")
                .add("property5='" + property5 + "'")
                .add("tax='" + tax + "'")
                .add("expenseTag='" + expenseTag + "'")
                .toString();
    }
}
