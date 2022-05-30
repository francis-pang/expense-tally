package expense_tally.model.csv;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PaymentCardValidatorTest {

  @Test
  void isPaymentCardValid_successWithDash() {
    boolean result = PaymentCardValidator.isPaymentCardValid("5436-4163-3246-5823", TransactionType.MASTERCARD);
    assertThat(PaymentCardValidator.isPaymentCardValid("5436-4163-3246-5823", TransactionType.MASTERCARD))
        .isTrue();
  }

  @Test
  void isPaymentCardValid_successWithoutDash() {
    assertThat(PaymentCardValidator.isPaymentCardValid("5436416332465823", TransactionType.MASTERCARD))
        .isTrue();
  }

  @Test
  void isPaymentCardValid_incorrectLengthFail() {
    assertThat(PaymentCardValidator.isPaymentCardValid("5436-4163-3246-58", TransactionType.MASTERCARD))
        .isFalse();
  }

  @Test
  void isPaymentCardValid_masterCardWrongPrefix() {
    assertThat(PaymentCardValidator.isPaymentCardValid("4436-4163-3246-5823", TransactionType.MASTERCARD))
        .isFalse();
  }

  @Test
  void isPaymentCardValid_containNonDigit() {
    assertThat(PaymentCardValidator.isPaymentCardValid("5436-4a63-3246-5823", TransactionType.MASTERCARD))
        .isFalse();
  }

  @Test
  void isPaymentCardValid_transactionTypeNotRight() {
    assertThat(PaymentCardValidator.isPaymentCardValid("5436-4a63-3246-5823", TransactionType.PAY_NOW))
        .isFalse();
  }

  @Test
  void isPaymentCardValid_null() {
    assertThat(PaymentCardValidator.isPaymentCardValid(null, TransactionType.MASTERCARD))
        .isFalse();
  }

  @Test
  void isPaymentCardValid_emptySpace() {
    assertThat(PaymentCardValidator.isPaymentCardValid("    ", TransactionType.MASTERCARD))
        .isFalse();
  }
}