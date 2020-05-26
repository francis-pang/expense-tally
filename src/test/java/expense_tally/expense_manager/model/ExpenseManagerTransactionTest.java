package expense_tally.expense_manager.model;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ExpenseManagerTransactionTest {
  @Test
  void createInstanceOf_nullCategory() {
    assertThatThrownBy(() -> ExpenseManagerTransaction.createInstanceOf(
          5.48,
          null,
          ExpenseSubCategory.CLOTHING,
          PaymentMethod.GRAY_PAY,
          "sd",
        Instant.now()
      ))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Category cannot be null");
  }

  @Test
  void createInstanceOf_nullSubCategory() {
    assertThatThrownBy(() -> ExpenseManagerTransaction.createInstanceOf(
        5.48,
        ExpenseCategory.ENTERTAINMENT,
        null,
        PaymentMethod.GRAY_PAY,
        "sd",
        Instant.now()
    ))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Subcategory cannot be null");
  }

  @Test
  void createInstanceOf_nullPaymentMethod() {
    assertThatThrownBy(() -> ExpenseManagerTransaction.createInstanceOf(
        5.48,
        ExpenseCategory.ENTERTAINMENT,
        ExpenseSubCategory.CLOTHING,
        null,
        "sd",
        Instant.now()
    ))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Payment method cannot be null");
  }

  @Test
  void createInstanceOf_nullDescription() {
    assertThatThrownBy(() -> ExpenseManagerTransaction.createInstanceOf(
        5.48,
        ExpenseCategory.ENTERTAINMENT,
        ExpenseSubCategory.CLOTHING,
        PaymentMethod.GIRO,
        null,
        Instant.now()
    ))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Description cannot be null or blank");
  }

  @Test
  void createInstanceOf_emptyDescription() {
    assertThatThrownBy(() -> ExpenseManagerTransaction.createInstanceOf(
        5.48,
        ExpenseCategory.ENTERTAINMENT,
        ExpenseSubCategory.CLOTHING,
        PaymentMethod.GIRO,
        "   ",
        Instant.now()
    ))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Description cannot be null or blank");
  }

  @Test
  void getAmount() {
    Instant testTime = Instant.now();
    double testAmount = 5.48;
    ExpenseManagerTransaction testExpenseManagerTransaction = ExpenseManagerTransaction.createInstanceOf(
        testAmount,
        ExpenseCategory.ENTERTAINMENT,
        ExpenseSubCategory.CLOTHING,
        PaymentMethod.GRAY_PAY,
        "sd",
        testTime
    );
    assertThat(testExpenseManagerTransaction.getAmount())
        .isNotNaN()
        .isNotNull()
        .isEqualByComparingTo(testAmount);
  }

  @Test
  void getPaymentMethod() {
    Instant testTime = Instant.now();
    double testAmount = 5.48;
    ExpenseManagerTransaction testExpenseManagerTransaction = ExpenseManagerTransaction.createInstanceOf(
        testAmount,
        ExpenseCategory.ENTERTAINMENT,
        ExpenseSubCategory.CLOTHING,
        PaymentMethod.GRAY_PAY,
        "sd",
        testTime
    );
    assertThat(testExpenseManagerTransaction.getPaymentMethod())
        .isNotNull()
        .isEqualByComparingTo(PaymentMethod.GRAY_PAY);
  }

  @Test
  void getExpendedTime() {
    Instant testTime = Instant.now();
    double testAmount = 5.48;
    ExpenseManagerTransaction testExpenseManagerTransaction = ExpenseManagerTransaction.createInstanceOf(
        testAmount,
        ExpenseCategory.ENTERTAINMENT,
        ExpenseSubCategory.CLOTHING,
        PaymentMethod.GRAY_PAY,
        "sd",
        testTime
    );
    assertThat(testExpenseManagerTransaction.getExpendedTime())
        .isNotNull()
        .isEqualTo(testTime);
  }

  @Test
  void getReferenceAmount_notSet() {
    Instant testTime = Instant.now();
    double testAmount = 5.48;
    ExpenseManagerTransaction testExpenseManagerTransaction = ExpenseManagerTransaction.createInstanceOf(
        testAmount,
        ExpenseCategory.ENTERTAINMENT,
        ExpenseSubCategory.CLOTHING,
        PaymentMethod.GRAY_PAY,
        "sd",
        testTime
    );
    assertThat(testExpenseManagerTransaction.getReferenceAmount())
        .isNull();
  }

  @Test
  void getReferenceAmount_afterSet() {
    Instant testTime = Instant.now();
    double testAmount = 5.48;
    ExpenseManagerTransaction testExpenseManagerTransaction = ExpenseManagerTransaction.createInstanceOf(
        testAmount,
        ExpenseCategory.ENTERTAINMENT,
        ExpenseSubCategory.CLOTHING,
        PaymentMethod.GRAY_PAY,
        "sd",
        testTime
    );
    testExpenseManagerTransaction.setReferenceAmount(4.77);
    assertThat(testExpenseManagerTransaction.getReferenceAmount())
        .isNotNaN()
        .isNotNull()
        .isEqualByComparingTo(4.77);
  }

  @Test
  void setReferenceAmount() {
    Instant testTime = Instant.now();
    double testAmount = 5.48;
    ExpenseManagerTransaction testExpenseManagerTransaction = ExpenseManagerTransaction.createInstanceOf(
        testAmount,
        ExpenseCategory.ENTERTAINMENT,
        ExpenseSubCategory.CLOTHING,
        PaymentMethod.GRAY_PAY,
        "sd",
        testTime
    );
    testExpenseManagerTransaction.setReferenceAmount(4.77);
    assertThat(testExpenseManagerTransaction.getReferenceAmount())
        .isNotNaN()
        .isNotNull()
        .isEqualByComparingTo(4.77);
  }

  @Test
  void testEquals_sameObject() {
    Instant testTime = Instant.now();
    double testAmount = 5.48;
    ExpenseManagerTransaction testExpenseManagerTransaction = ExpenseManagerTransaction.createInstanceOf(
        testAmount,
        ExpenseCategory.ENTERTAINMENT,
        ExpenseSubCategory.CLOTHING,
        PaymentMethod.GRAY_PAY,
        "sd",
        testTime
    );
    assertThat(testExpenseManagerTransaction.equals(testExpenseManagerTransaction))
        .isTrue();
  }

  @Test
  void testEquals_notSameInstanceType() {
    Instant testTime = Instant.now();
    double testAmount = 5.48;
    ExpenseManagerTransaction testExpenseManagerTransaction = ExpenseManagerTransaction.createInstanceOf(
        testAmount,
        ExpenseCategory.ENTERTAINMENT,
        ExpenseSubCategory.CLOTHING,
        PaymentMethod.GRAY_PAY,
        "sd",
        testTime
    );
    assertThat(testExpenseManagerTransaction.equals(testTime))
        .isFalse();
  }

  @Test
  void testEquals_notSameAmount() {
    Instant testTime = Instant.now();
    double testAmount = 5.48;
    ExpenseManagerTransaction testExpenseManagerTransaction = ExpenseManagerTransaction.createInstanceOf(
        testAmount,
        ExpenseCategory.ENTERTAINMENT,
        ExpenseSubCategory.CLOTHING,
        PaymentMethod.GRAY_PAY,
        "sd",
        testTime
    );
    ExpenseManagerTransaction expectedExpenseManagerTransaction = ExpenseManagerTransaction.createInstanceOf(
        5.47,
        ExpenseCategory.ENTERTAINMENT,
        ExpenseSubCategory.CLOTHING,
        PaymentMethod.GRAY_PAY,
        "sd",
        testTime
    );
    assertThat(testExpenseManagerTransaction.equals(expectedExpenseManagerTransaction))
        .isFalse();
  }

  @Test
  void testEquals_notSameCategory() {
    Instant testTime = Instant.now();
    double testAmount = 5.48;
    ExpenseManagerTransaction testExpenseManagerTransaction = ExpenseManagerTransaction.createInstanceOf(
        testAmount,
        ExpenseCategory.ENTERTAINMENT,
        ExpenseSubCategory.CLOTHING,
        PaymentMethod.GRAY_PAY,
        "sd",
        testTime
    );
    ExpenseManagerTransaction expectedExpenseManagerTransaction = ExpenseManagerTransaction.createInstanceOf(
        testAmount,
        ExpenseCategory.AESTHETIC,
            ExpenseSubCategory.CLOTHING,
        PaymentMethod.GRAY_PAY,
        "sd",
        testTime
    );
    assertThat(testExpenseManagerTransaction.equals(expectedExpenseManagerTransaction))
        .isFalse();
  }

  @Test
  void testEquals_notSameSubcategory() {
    Instant testTime = Instant.now();
    double testAmount = 5.48;
    ExpenseManagerTransaction testExpenseManagerTransaction = ExpenseManagerTransaction.createInstanceOf(
        testAmount,
        ExpenseCategory.ENTERTAINMENT,
        ExpenseSubCategory.CLOTHING,
        PaymentMethod.GRAY_PAY,
        "sd",
        testTime
    );
    ExpenseManagerTransaction expectedExpenseManagerTransaction = ExpenseManagerTransaction.createInstanceOf(
        testAmount,
        ExpenseCategory.ENTERTAINMENT,
        ExpenseSubCategory.ALCOHOL_AND_RESTAURANT,
        PaymentMethod.GRAY_PAY,
        "sd",
        testTime
    );
    assertThat(testExpenseManagerTransaction.equals(expectedExpenseManagerTransaction))
        .isFalse();
  }

  @Test
  void testEquals_notSamePaymentMethod() {
    Instant testTime = Instant.now();
    double testAmount = 5.48;
    ExpenseManagerTransaction testExpenseManagerTransaction = ExpenseManagerTransaction.createInstanceOf(
        testAmount,
        ExpenseCategory.ENTERTAINMENT,
        ExpenseSubCategory.CLOTHING,
        PaymentMethod.GRAY_PAY,
        "sd",
        testTime
    );
    ExpenseManagerTransaction expectedExpenseManagerTransaction = ExpenseManagerTransaction.createInstanceOf(
        testAmount,
        ExpenseCategory.ENTERTAINMENT,
        ExpenseSubCategory.CLOTHING,
        PaymentMethod.ELECTRONIC_TRANSFER,
        "sd",
        testTime
    );
    assertThat(testExpenseManagerTransaction.equals(expectedExpenseManagerTransaction))
        .isFalse();
  }

  @Test
  void testEquals_notSameDescription() {
    Instant testTime = Instant.now();
    double testAmount = 5.48;
    ExpenseManagerTransaction testExpenseManagerTransaction = ExpenseManagerTransaction.createInstanceOf(
        testAmount,
        ExpenseCategory.ENTERTAINMENT,
        ExpenseSubCategory.CLOTHING,
        PaymentMethod.GRAY_PAY,
        "sd",
        testTime
    );
    ExpenseManagerTransaction expectedExpenseManagerTransaction = ExpenseManagerTransaction.createInstanceOf(
        testAmount,
        ExpenseCategory.ENTERTAINMENT,
        ExpenseSubCategory.CLOTHING,
        PaymentMethod.GRAY_PAY,
        "asd",
        testTime
    );
    assertThat(testExpenseManagerTransaction.equals(expectedExpenseManagerTransaction))
        .isFalse();
  }

  @Test
  void testEquals_notSameExpendedTime() {
    Instant testTime = Instant.now();
    double testAmount = 5.48;
    ExpenseManagerTransaction testExpenseManagerTransaction = ExpenseManagerTransaction.createInstanceOf(
        testAmount,
        ExpenseCategory.ENTERTAINMENT,
        ExpenseSubCategory.CLOTHING,
        PaymentMethod.GRAY_PAY,
        "sd",
        testTime
    );
    ExpenseManagerTransaction expectedExpenseManagerTransaction = ExpenseManagerTransaction.createInstanceOf(
        testAmount,
        ExpenseCategory.ENTERTAINMENT,
        ExpenseSubCategory.CLOTHING,
        PaymentMethod.GRAY_PAY,
        "sd",
        testTime.plusSeconds(1)
    );
    assertThat(testExpenseManagerTransaction.equals(expectedExpenseManagerTransaction))
        .isFalse();
  }

  @Test
  void testEquals_noReferenceAmount() {
    Instant testTime = Instant.now();
    double testAmount = 5.48;
    ExpenseManagerTransaction testExpenseManagerTransaction = ExpenseManagerTransaction.createInstanceOf(
        testAmount,
        ExpenseCategory.ENTERTAINMENT,
        ExpenseSubCategory.CLOTHING,
        PaymentMethod.GRAY_PAY,
        "sd",
        testTime
    );
    ExpenseManagerTransaction expectedExpenseManagerTransaction = ExpenseManagerTransaction.createInstanceOf(
        testAmount,
        ExpenseCategory.ENTERTAINMENT,
        ExpenseSubCategory.CLOTHING,
        PaymentMethod.GRAY_PAY,
        "sd",
        testTime
    );
    expectedExpenseManagerTransaction.setReferenceAmount(0.07);
    assertThat(testExpenseManagerTransaction.equals(expectedExpenseManagerTransaction))
        .isFalse();
  }

  @Test
  void testEquals_notSameReferenceAmount() {
    Instant testTime = Instant.now();
    double testAmount = 5.48;
    ExpenseManagerTransaction testExpenseManagerTransaction = ExpenseManagerTransaction.createInstanceOf(
        testAmount,
        ExpenseCategory.ENTERTAINMENT,
        ExpenseSubCategory.CLOTHING,
        PaymentMethod.GRAY_PAY,
        "sd",
        testTime
    );
    testExpenseManagerTransaction.setReferenceAmount(0.01);
    ExpenseManagerTransaction expectedExpenseManagerTransaction = ExpenseManagerTransaction.createInstanceOf(
        testAmount,
        ExpenseCategory.ENTERTAINMENT,
        ExpenseSubCategory.CLOTHING,
        PaymentMethod.GRAY_PAY,
        "sd",
        testTime
    );
    expectedExpenseManagerTransaction.setReferenceAmount(0.07);
    assertThat(testExpenseManagerTransaction.equals(expectedExpenseManagerTransaction))
        .isFalse();
  }

  @Test
  void testHashCode() {
    Instant testTime = Instant.now();
    ExpenseManagerTransaction testExpenseManagerTransaction = ExpenseManagerTransaction.createInstanceOf(5.48,
        ExpenseCategory.ENTERTAINMENT, ExpenseSubCategory.CLOTHING, PaymentMethod.GRAY_PAY, "sd", testTime);
    assertThat(testExpenseManagerTransaction.hashCode())
        .isNotZero()
        .isEqualTo(ExpenseManagerTransaction.createInstanceOf(5.48, ExpenseCategory.ENTERTAINMENT,
            ExpenseSubCategory.CLOTHING, PaymentMethod.GRAY_PAY, "sd", testTime).hashCode());
  }

  @Test
  void testToString() {
    Instant testTime = LocalDateTime.of(2020, 5, 27, 1, 19).atZone(ZoneId.of("UTC")).toInstant();
    ExpenseManagerTransaction testExpenseManagerTransaction = ExpenseManagerTransaction.createInstanceOf(
        5.48,
        ExpenseCategory.ENTERTAINMENT,
        ExpenseSubCategory.CLOTHING,
        PaymentMethod.GRAY_PAY,
        "sd",
        testTime
    );
    assertThat(testExpenseManagerTransaction.toString())
        .isEqualTo("[amount=5.48, category=ENTERTAINMENT, subcategory=CLOTHING, paymentMethod=GRAY_PAY, description='sd', expendedTime=2020-05-27T01:19:00Z, referenceAmount=null]");
  }
}