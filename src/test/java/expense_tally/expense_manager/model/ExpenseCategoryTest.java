package expense_tally.expense_manager.model;

import org.junit.jupiter.api.Test;

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
}