package expense_tally.model.persistence.transformation;

import expense_tally.Exception.StringResolver;
import org.apache.commons.lang3.StringUtils;

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
    if (StringUtils.isBlank(value)) {
      String errorMessage = String.format("Blank expense category is disallowed: %s",
          StringResolver.resolveNullableString(value));
      throw new IllegalArgumentException(errorMessage);
    }
    this.value = value;
  }

  /**
   * Returns the {@link ExpenseCategory} given its string form
   *
   * @param expenseCategoryStr expense category in string form
   * @return the category of the expense record given its string form, null if not found
   */
  public static ExpenseCategory resolve(String expenseCategoryStr) {
    for (ExpenseCategory expenseCategory : values()) {
      if (expenseCategory.value.equals(expenseCategoryStr)) {
        return expenseCategory;
      }
    }
    return null;
  }

  public String value() {
    return value;
  }
}
