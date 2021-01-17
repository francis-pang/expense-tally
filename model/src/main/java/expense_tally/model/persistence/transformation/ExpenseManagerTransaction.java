package expense_tally.model.persistence.transformation;

import org.apache.commons.lang3.builder.ToStringBuilder;

import java.time.Instant;
import java.util.Objects;

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
  private Instant expendedTime;
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
   * @param expendedTime time when the transaction occurs
   * @see ExpenseCategory
   * @see ExpenseSubCategory
   * @return a new instance of {@link ExpenseManagerTransaction}
   * @throws IllegalArgumentException when any of the enum class is null, or an blank description is provided
   */
  public static ExpenseManagerTransaction create(int id, double amount, ExpenseCategory category,
                                                 ExpenseSubCategory subCategory,
                                                 PaymentMethod paymentMethod, String description,
                                                 Instant expendedTime) {
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
    if (expendedTime == null || expendedTime.isAfter(currentInstant)) {
      throw new IllegalArgumentException("Expensed time cannot be null or in the future");
    }
    expenseManagerTransaction.expendedTime = expendedTime;
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
    if (o == null || getClass() != o.getClass()) return false;
    ExpenseManagerTransaction that = (ExpenseManagerTransaction) o;
    return id == that.id &&
        amount.equals(that.amount) &&
        category == that.category &&
        subcategory == that.subcategory &&
        paymentMethod == that.paymentMethod &&
        description.equals(that.description) &&
        expendedTime.equals(that.expendedTime) &&
        Objects.equals(referenceAmount, that.referenceAmount);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, amount, category, subcategory, paymentMethod, description, expendedTime, referenceAmount);
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this)
        .append("id", id)
        .append("amount", amount)
        .append("category", category)
        .append("subcategory", subcategory)
        .append("paymentMethod", paymentMethod)
        .append("description", description)
        .append("expendedTime", expendedTime)
        .append("referenceAmount", referenceAmount)
        .toString();
  }
}
