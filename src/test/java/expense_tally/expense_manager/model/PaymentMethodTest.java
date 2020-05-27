package expense_tally.expense_manager.model;

import expense_tally.expense_manager.transformation.PaymentMethod;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PaymentMethodTest {

  @Test
  void resolve_found() {
    assertThat(PaymentMethod.resolve("Cash"))
        .isNotNull()
        .isEqualTo(PaymentMethod.CASH);
  }

  @Test
  void resolve_notFound() {
    assertThat(PaymentMethod.resolve("Invalid"))
        .isNull();
  }

  @Test
  void resolve_null() {
    assertThat(PaymentMethod.resolve(null))
        .isNull();
  }

  @Test
  void resolve_emptyString() {
    assertThat(PaymentMethod.resolve(""))
        .isNull();
  }
}