package database;

public class ExpenseReport {
    private int id;
    private String account;
    private String amount;
    private String category;
    private String subcategory;
    private String paymentMethod;
    private String description;
    private int expensed;
    private int modified;
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

    public void setPaymentMethod(String payment_method) {
        this.paymentMethod = payment_method;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getExpensed() {
        return expensed;
    }

    public void setExpensed(int expensed) {
        this.expensed = expensed;
    }

    public int getModified() {
        return modified;
    }

    public void setModified(int modified) {
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

        if (id != that.id) return false;
        if (expensed != that.expensed) return false;
        if (modified != that.modified) return false;
        if (account != null ? !account.equals(that.account) : that.account != null) return false;
        if (!amount.equals(that.amount)) return false;
        if (category != null ? !category.equals(that.category) : that.category != null) return false;
        if (subcategory != null ? !subcategory.equals(that.subcategory) : that.subcategory != null) return false;
        if (!paymentMethod.equals(that.paymentMethod)) return false;
        if (!description.equals(that.description)) return false;
        if (referenceNumber != null ? !referenceNumber.equals(that.referenceNumber) : that.referenceNumber != null)
            return false;
        if (!status.equals(that.status)) return false;
        if (property != null ? !property.equals(that.property) : that.property != null) return false;
        if (property2 != null ? !property2.equals(that.property2) : that.property2 != null) return false;
        if (property3 != null ? !property3.equals(that.property3) : that.property3 != null) return false;
        if (property4 != null ? !property4.equals(that.property4) : that.property4 != null) return false;
        if (property5 != null ? !property5.equals(that.property5) : that.property5 != null) return false;
        if (tax != null ? !tax.equals(that.tax) : that.tax != null) return false;
        return expenseTag != null ? expenseTag.equals(that.expenseTag) : that.expenseTag == null;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (account != null ? account.hashCode() : 0);
        result = 31 * result + amount.hashCode();
        result = 31 * result + (category != null ? category.hashCode() : 0);
        result = 31 * result + (subcategory != null ? subcategory.hashCode() : 0);
        result = 31 * result + paymentMethod.hashCode();
        result = 31 * result + description.hashCode();
        result = 31 * result + expensed;
        result = 31 * result + modified;
        result = 31 * result + (referenceNumber != null ? referenceNumber.hashCode() : 0);
        result = 31 * result + status.hashCode();
        result = 31 * result + (property != null ? property.hashCode() : 0);
        result = 31 * result + (property2 != null ? property2.hashCode() : 0);
        result = 31 * result + (property3 != null ? property3.hashCode() : 0);
        result = 31 * result + (property4 != null ? property4.hashCode() : 0);
        result = 31 * result + (property5 != null ? property5.hashCode() : 0);
        result = 31 * result + (tax != null ? tax.hashCode() : 0);
        result = 31 * result + (expenseTag != null ? expenseTag.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ExpenseReport{" +
                "id=" + id +
                ", account='" + account + '\'' +
                ", amount='" + amount + '\'' +
                ", category='" + category + '\'' +
                ", subcategory='" + subcategory + '\'' +
                ", paymentMethod='" + paymentMethod + '\'' +
                ", description='" + description + '\'' +
                ", expensed=" + expensed +
                ", modified=" + modified +
                ", referenceNumber='" + referenceNumber + '\'' +
                ", status='" + status + '\'' +
                ", property='" + property + '\'' +
                ", property2='" + property2 + '\'' +
                ", property3='" + property3 + '\'' +
                ", property4='" + property4 + '\'' +
                ", property5='" + property5 + '\'' +
                ", tax='" + tax + '\'' +
                ", expenseTag='" + expenseTag + '\'' +
                '}';
    }
}
