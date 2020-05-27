package expense_tally.expense_manager.model;

import expense_tally.expense_manager.transformation.ExpenseSubCategory;
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
  void resolve_null() {
    assertThat(ExpenseSubCategory.resolve(null))
        .isNull();
  }

  @Test
  void resolve_emptyString() {
    assertThat(ExpenseSubCategory.resolve(""))
        .isNull();
  }

  @Test
  void value() {
    assertThat(ExpenseSubCategory.ALCOHOL_AND_RESTAURANT.value())
        .isEqualTo("Alcohol/ Restaurant");
  }
}