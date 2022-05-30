package expense_tally.model.persistence.transformation;

import java.util.stream.Stream;

/**
 *
 */
public enum ExpenseCategory {
  ENTERTAINMENT("Entertainment"),
  FOOD("Food"),
  AESTHETIC("Aesthetic"),
  TRANSPORT("Transport"),
  LOANS("Loans"),
  HOUSEHOLD("Household"),
  HEALTH_CARE("Health Care"),
  PERSONAL("Personal"),
  VACATION("Vacation"),
  UNCATEGORIZED("Uncategorized");

  private final String value;

  /**
   * Default constructor
   *
   * @param value the value of the value
   */
  ExpenseCategory(String value) {
    this.value = value;
  }

  /**
   * Returns the {@link ExpenseCategory} given its string form
   *
   * @param expenseCategoryStr expense category in string form
   * @return the category of the expense record given its string form, null if not found
   */
  public static ExpenseCategory resolve(String expenseCategoryStr) {
    if (expenseCategoryStr == null || expenseCategoryStr.isBlank()) {
      return null;
    }

    return Stream.of(values())
      .filter(expenseCategory -> expenseCategory.value.equals(expenseCategoryStr))
      .findFirst()
      .orElse(null);
  }

  public String value() {
    return value;
  }
}
