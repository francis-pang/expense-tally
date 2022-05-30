package expense_tally.model.csv;

import expense_tally.model.persistence.transformation.ExpenseCategory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class TransactionTypeTest {

  @Test
  void resolve_null() {
    assertThat(TransactionType.resolve(null))
        .isNull();
  }

  @Test
  void resolve_blankString() {
    assertThat(TransactionType.resolve(" "))
        .isNull();
  }

  @Test
  void resolve_found() {
    assertThat(TransactionType.resolve("BILL"))
        .isNotNull()
        .isEqualByComparingTo(TransactionType.BILL_PAYMENT);
  }

  @Test
  void resolve_notFound() {
    assertThat(TransactionType.resolve("GOOGLE"))
        .isNull();
  }

  @Test
  void value() {
    assertThat(TransactionType.BILL_PAYMENT.value())
        .isEqualTo("BILL");
  }

  @Test
  void testToString() {
    assertThat(TransactionType.MASTERCARD)
        .hasToString("TransactionType[value='MST']");
  }

  @ParameterizedTest
  @EnumSource(names = {
      "MASTERCARD",
      "NETS",
      "POINT_OF_SALE",
      "GIRO",
      "GIRO_COLLECTION",
      "FAST_PAYMENT",
      "FAST_COLLECTION",
      "FUNDS_TRANSFER_I",
      "FUNDS_TRANSFER_A",
      "BILL_PAYMENT",
      "PAY_NOW",
      "CASH_WITHDRAWAL",
      "INTEREST_EARNED",
      "STANDING_INSTRUCTION",
      "SALARY",
      "MAS_ELECTRONIC_PAYMENT_SYSTEM_RECEIPT"
  })
  void value_TestAllValues(TransactionType transactionType) {
    assertThat(transactionType)
        .isInstanceOf(TransactionType.class);
  }
}