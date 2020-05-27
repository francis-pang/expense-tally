package expense_tally.expense_manager.transformation;

import java.time.Instant;
import java.util.Objects;
import java.util.StringJoiner;

/**
 *
 */
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

  /**
   * Create a {@link ExpenseManagerTransaction} with the minimally required parameters.
   * @param amount transaction amount
   * @param category transaction category
   * @param subCategory transaction sub-category
   * @param paymentMethod transaction payment method
   * @param description transaction description
   * @param expendedTime time when the transaction occurs
   * @see ExpenseCategory
   * @see ExpenseSubCategory
   * @return a new instance of {@link ExpenseManagerTransaction}
   * @hrows IllegalArgumentException when any of the enum class is null, or an blank description is provided
   */
  public static ExpenseManagerTransaction createInstanceOf(double amount, ExpenseCategory category,
                                                           ExpenseSubCategory subCategory,
                                                           PaymentMethod paymentMethod, String description,
                                                           Instant expendedTime) {
    ExpenseManagerTransaction expenseManagerTransaction = new ExpenseManagerTransaction();
    expenseManagerTransaction.amount = amount;
    if (category == null) {
      throw new IllegalArgumentException("Category cannot be null");
    }
    expenseManagerTransaction.category = category;
    if (subCategory == null) {
      throw new IllegalArgumentException("Subcategory cannot be null");
    }
    expenseManagerTransaction.subcategory = subCategory;
    if (paymentMethod == null) {
      throw new IllegalArgumentException("Payment method cannot be null");
    }
    expenseManagerTransaction.paymentMethod = paymentMethod;
    if (description == null || description.isBlank()) {
      throw new IllegalArgumentException("Description cannot be null or blank");
    }
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
    if (this == o) {
      return true;
    }
    if (o == null) {
      return false;
    }
    if (getClass() != o.getClass()) {
      return false;
    }
    ExpenseManagerTransaction that = (ExpenseManagerTransaction) o;
    return amount.equals(that.amount) &&
        category == that.category &&
        subcategory == that.subcategory &&
        paymentMethod == that.paymentMethod &&
        description.equals(that.description) &&
        expendedTime.equals(that.expendedTime) &&
        Objects.equals(referenceAmount, that.referenceAmount);
  }

  @Override
  public int hashCode() {
    return Objects.hash(amount, category, subcategory, paymentMethod, description, expendedTime, referenceAmount);
  }

  @Override
  public String toString() {
    return new StringJoiner(", ", ExpenseManagerTransaction.class.getSimpleName() + "[", "]")
        .add("amount=" + amount)
        .add("category=" + category)
        .add("subcategory=" + subcategory)
        .add("paymentMethod=" + paymentMethod)
        .add("description='" + description + "'")
        .add("expendedTime=" + expendedTime)
        .add("referenceAmount=" + referenceAmount)
        .toString();
  }
}
