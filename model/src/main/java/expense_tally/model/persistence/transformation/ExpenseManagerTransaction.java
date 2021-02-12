package expense_tally.model.persistence.transformation;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.time.Instant;
import java.util.StringJoiner;

/**
 * This class stores all the details of a transaction inside the Expense Manager app.
 */
public final class ExpenseManagerTransaction {
  private int id;
  private Double amount;
  private ExpenseCategory category;
  private ExpenseSubCategory subcategory;
  private PaymentMethod paymentMethod;
  private String description;
  private Instant expensedTime;
  private Double referenceAmount;

  /**
   * Private default constructor so that no one can create an object based on this class
   */
  private ExpenseManagerTransaction() {
  }

  /**
   * Create a {@link ExpenseManagerTransaction} with the minimally required parameters.
   * @param id identifier of the transaction
   * @param amount transaction amount
   * @param category transaction category
   * @param subCategory transaction sub-category
   * @param paymentMethod transaction payment method
   * @param description transaction description
   * @param expensedTime time when the transaction occurs
   * @see ExpenseCategory
   * @see ExpenseSubCategory
   * @return a new instance of {@link ExpenseManagerTransaction}
   * @throws IllegalArgumentException when any of the enum class is null, or an blank description is provided
   */
  public static ExpenseManagerTransaction create(int id, double amount, ExpenseCategory category,
                                                 ExpenseSubCategory subCategory,
                                                 PaymentMethod paymentMethod, String description,
                                                 Instant expensedTime) {
    ExpenseManagerTransaction expenseManagerTransaction = new ExpenseManagerTransaction();
    if (id <= 0) {
      throw new IllegalArgumentException("ID cannot 0 or negative");
    }
    expenseManagerTransaction.id = id;
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
    Instant currentInstant = Instant.now();
    if (expensedTime == null || expensedTime.isAfter(currentInstant)) {
      throw new IllegalArgumentException("Expensed time cannot be null or in the future");
    }
    expenseManagerTransaction.expensedTime = expensedTime;
    return expenseManagerTransaction;
  }

  public int getId() {
    return id;
  }

  public Double getAmount() {
    return amount;
  }

  public ExpenseCategory getCategory() {
    return category;
  }

  public ExpenseSubCategory getSubcategory() {
    return subcategory;
  }

  public String getDescription() {
    return description;
  }

  public PaymentMethod getPaymentMethod() {
    return paymentMethod;
  }

  public Instant getExpensedTime() {
    return expensedTime;
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

    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    ExpenseManagerTransaction that = (ExpenseManagerTransaction) o;

    return new EqualsBuilder()
        .append(id, that.id)
        .append(amount, that.amount)
        .append(category, that.category)
        .append(subcategory, that.subcategory)
        .append(paymentMethod, that.paymentMethod)
        .append(description, that.description)
        .append(expensedTime, that.expensedTime)
        .append(referenceAmount, that.referenceAmount)
        .isEquals();
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder(17, 37)
        .append(id)
        .append(amount)
        .append(category)
        .append(subcategory)
        .append(paymentMethod)
        .append(description)
        .append(expensedTime)
        .append(referenceAmount)
        .toHashCode();
  }

  @Override
  public String toString() {
    return new StringJoiner(", ", ExpenseManagerTransaction.class.getSimpleName() + "[", "]")
        .add("id=" + id)
        .add("amount=" + amount)
        .add("category=" + category)
        .add("subcategory=" + subcategory)
        .add("paymentMethod=" + paymentMethod)
        .add("description='" + description + "'")
        .add("expendedTime=" + expensedTime)
        .add("referenceAmount=" + referenceAmount)
        .toString();
  }
}
