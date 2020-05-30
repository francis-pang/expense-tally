package expense_tally.expense_manager.persistence;

import java.util.Objects;

/**
 * This class models the database schema of a transaction stored in the Expense Manager application. All the
 * attributes inside this class model after the equivalence data type of the database schema declaration.
 */
public class ExpenseReport {
  private int id;
  private String account;
  private String amount;
  private String category;
  private String subcategory;
  private String paymentMethod;
  private String description;
  private long expensedTime;
  private long modificationTime;
  /**
   * The reference amount that is input when the expensed amount is not the same
   */
  private String referenceNumber;
  private String status;
  private String property1;
  private String property2;
  private String property3;
  private String property4;
  private String property5;
  private String tax;
  private String expenseTag;

  public ExpenseReport() { //Default implementation
  }

  public void setId(int id) {
    this.id = id;
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

  public long getExpensedTime() {
    return expensedTime;
  }

  public void setExpensedTime(long expensedTime) {
    this.expensedTime = expensedTime;
  }

  public void setModificationTime(long modificationTime) {
    this.modificationTime = modificationTime;
  }

  public String getReferenceNumber() {
    return referenceNumber;
  }

  public void setReferenceNumber(String referenceNumber) {
    this.referenceNumber = referenceNumber;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public void setProperty1(String property1) {
    this.property1 = property1;
  }

  public void setProperty2(String property2) {
    this.property2 = property2;
  }

  public void setProperty3(String property3) {
    this.property3 = property3;
  }

  public void setProperty4(String property4) {
    this.property4 = property4;
  }

  public void setProperty5(String property5) {
    this.property5 = property5;
  }

  public void setTax(String tax) {
    this.tax = tax;
  }

  public void setExpenseTag(String expenseTag) {
    this.expenseTag = expenseTag;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ExpenseReport that = (ExpenseReport) o;
    return id == that.id &&
        expensedTime == that.expensedTime &&
        modificationTime == that.modificationTime &&
        Objects.equals(account, that.account) &&
        Objects.equals(amount, that.amount) &&
        Objects.equals(category, that.category) &&
        Objects.equals(subcategory, that.subcategory) &&
        Objects.equals(paymentMethod, that.paymentMethod) &&
        Objects.equals(description, that.description) &&
        Objects.equals(referenceNumber, that.referenceNumber) &&
        Objects.equals(status, that.status) &&
        Objects.equals(property1, that.property1) &&
        Objects.equals(property2, that.property2) &&
        Objects.equals(property3, that.property3) &&
        Objects.equals(property4, that.property4) &&
        Objects.equals(property5, that.property5) &&
        Objects.equals(tax, that.tax) &&
        Objects.equals(expenseTag, that.expenseTag);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, account, amount, category, subcategory, paymentMethod, description, expensedTime, modificationTime, referenceNumber, status, property1, property2, property3, property4, property5, tax, expenseTag);
  }
}
