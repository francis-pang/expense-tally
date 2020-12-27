package expense_tally.model.persistence.database;

import expense_tally.model.persistence.transformation.ExpenseCategory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.assertj.core.api.Assertions.assertThat;

class ExpenseCategoryTest {
  @Test
  void resolve_foundEntertainment() {
    assertThat(ExpenseCategory.resolve(ExpenseCategory.ENTERTAINMENT.value()))
        .isNotNull()
        .isEqualTo(ExpenseCategory.ENTERTAINMENT);
  }

  @Test
  void resolve_notFound() {
    assertThat(ExpenseCategory.resolve("invalid"))
        .isNull();
  }

  @Test
  void value_Food() {
    assertThat(ExpenseCategory.FOOD.value())
        .isNotBlank()
        .isEqualTo("Food");
  }

  @ParameterizedTest
  @EnumSource(names = {
      "ENTERTAINMENT",
      "FOOD",
      "AESTHETIC",
      "TRANSPORT",
      "LOANS",
      "HOUSEHOLD",
      "HEALTH_CARE",
      "PERSONAL",
      "VACATION",
      "UNCATEGORIZED"
  })
  void value_TestAllValues(ExpenseCategory expenseCategory) {
    assertThat(expenseCategory)
        .isInstanceOf(ExpenseCategory.class);
  }
}