package expense_tally.csv_parser.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PaymentCardValidatorTest {

  @Test
  void isPaymentCardValid_successWithDash() {
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
}