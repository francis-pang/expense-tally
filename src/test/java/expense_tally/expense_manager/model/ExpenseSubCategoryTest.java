package expense_tally.expense_manager.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ExpenseSubCategoryTest {

  @Test
  void resolve_sports() {
    assertThat(ExpenseSubCategory.resolve(ExpenseSubCategory.SPORTS.value()))
        .isNotNull()
        .isEqualTo(ExpenseSubCategory.SPORTS);
  }

  @Test
  void resolve_notFound() {
    assertThat(ExpenseSubCategory.resolve("Not Exists"))
        .isNull();
  }

  @Test
  void value() {
    assertThat(ExpenseSubCategory.ALCOHOL_AND_RESTAURANT.value())
        .isEqualTo("Alcohol/ Restaurant");
  }
}