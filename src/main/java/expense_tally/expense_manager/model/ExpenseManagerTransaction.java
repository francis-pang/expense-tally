package expense_tally.expense_manager.model;

import java.time.Instant;
import java.util.Objects;

public class ExpenseManagerTransaction {
  private Double amount;
  private ExpenseCategory category;
  private ExpenseSubCategory subcategory;
  private PaymentMethod paymentMethod;
  private String description;
  private Instant expendedTime;
  private Double referenceAmount;

  private ExpenseManagerTransaction() {
  }

  public static ExpenseManagerTransaction createInstanceOf(double amount, ExpenseCategory category,
                                                           ExpenseSubCategory subCategory,
                                                           PaymentMethod paymentMethod, String description,
                                                           Instant expendedTime) {
    ExpenseManagerTransaction expenseManagerTransaction = new ExpenseManagerTransaction();
    expenseManagerTransaction.amount = amount;
    expenseManagerTransaction.category = category;
    expenseManagerTransaction.subcategory = subCategory;
    expenseManagerTransaction.paymentMethod = paymentMethod;
    expenseManagerTransaction.description = description;
    expenseManagerTransaction.expendedTime = expendedTime;
    return expenseManagerTransaction;
  }

  public Double getAmount() {
    return amount;
  }

  public PaymentMethod getPaymentMethod() {
    return paymentMethod;
  }

  public Instant getExpendedTime() {
    return expendedTime;
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
        expendedTime.equals(that.expendedTime) &&
        referenceAmount.equals(that.referenceAmount);
  }

  @Override
  public int hashCode() {
    return Objects.hash(amount, category, subcategory, paymentMethod, description, expendedTime, referenceAmount);
  }
}
