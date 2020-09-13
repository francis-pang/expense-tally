package expense_tally.model.csv;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Even though it is common that there is no need to test the model class. I have decided to test this model class
 * because there has been business logic built into this model class
 */
class GenericCsvTransactionTest {
  @Test
  void builder_pastTransactionDate() throws MonetaryAmountException {
    LocalDate transactionDate = LocalDate.of(2019, 12, 27);
    TransactionType transactionType = TransactionType.PAY_NOW;
    double debitAmount = 5.00;

    assertThat(new GenericCsvTransaction.Builder(transactionDate, transactionType, debitAmount).build())
        .isNotNull()
        .extracting(
            "transactionDate",
            "transactionType",
            "debitAmount",
            "creditAmount",
            "transactionRef1",
            "transactionRef2",
            "transactionRef3"
        )
        .contains(
            transactionDate,
            transactionType,
            5.00,
            0.00,
            "",
            "",
            ""
        );
  }

  @Test
  void builder_nullTransactionDate() {
    LocalDate transactionDate = null;
    TransactionType transactionType = TransactionType.PAY_NOW;
    double debitAmount = 5.00;

    assertThatThrownBy(() -> new GenericCsvTransaction.Builder(transactionDate, transactionType, debitAmount).build())
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("transactionDate cannot be null.");
  }

  @Test
  void builder_futureTransactionDate() throws MonetaryAmountException {
    LocalDate transactionDate = LocalDate.now().plusDays(5);
    TransactionType transactionType = TransactionType.PAY_NOW;
    double debitAmount = 5.00;

    assertThat(new GenericCsvTransaction.Builder(transactionDate, transactionType, debitAmount).build())
        .isNotNull()
        .extracting(
            "transactionDate",
            "transactionType",
            "debitAmount",
            "creditAmount",
            "transactionRef1",
            "transactionRef2",
            "transactionRef3"
        )
        .contains(
            transactionDate,
            transactionType,
            5.00,
            0.00,
            "",
            "",
            ""
        );
  }

  @Test
  void builder_nullTransactionType() throws MonetaryAmountException {
    LocalDate transactionDate = LocalDate.of(2019, 12, 27);
    TransactionType transactionType = null;
    double debitAmount = 5.00;

    assertThat(new GenericCsvTransaction.Builder(transactionDate, transactionType, debitAmount).build())
        .isNotNull()
        .extracting(
            "transactionDate",
            "transactionType",
            "debitAmount",
            "creditAmount",
            "transactionRef1",
            "transactionRef2",
            "transactionRef3"
        )
        .contains(
            transactionDate,
            null,
            5.00,
            0.00,
            "",
            "",
            ""
        );
  }

  @Test
  void builder_negativeDebit() {
    LocalDate transactionDate = LocalDate.of(2019, 12, 27);
    TransactionType transactionType = TransactionType.PAY_NOW;
    double debitAmount = -5.00;

    assertThatThrownBy(() -> new GenericCsvTransaction.Builder(transactionDate, transactionType, debitAmount).build())
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Debit amount cannot be negative.");
  }

  @Test
  void builder_positiveCredit() throws MonetaryAmountException {
    LocalDate transactionDate = LocalDate.of(2019, 12, 27);
    TransactionType transactionType = TransactionType.PAY_NOW;
    double debitAmount = 0;
    double creditAmount = 4.50;

    assertThat(new GenericCsvTransaction.Builder(transactionDate, transactionType, debitAmount).creditAmount(creditAmount)
        .build())
        .isNotNull()
        .extracting(
            "transactionDate",
            "transactionType",
            "debitAmount",
            "creditAmount",
            "transactionRef1",
            "transactionRef2",
            "transactionRef3"
        )
        .contains(
            transactionDate,
            transactionType,
            0.0,
            4.50,
            "",
            "",
            ""
        );
  }

  @Test
  void builder_negativeCredit() {
    LocalDate transactionDate = LocalDate.of(2019, 12, 27);
    TransactionType transactionType = TransactionType.PAY_NOW;
    double debitAmount = 0;
    double creditAmount = -4.50;

    assertThatThrownBy(() -> new GenericCsvTransaction.Builder(transactionDate, transactionType, debitAmount)
        .creditAmount(creditAmount)
        .build())
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Credit amount cannot be negative.");
  }

  @Test
  void builder_ref1() throws MonetaryAmountException {
    LocalDate transactionDate = LocalDate.of(2019, 12, 27);
    TransactionType transactionType = TransactionType.PAY_NOW;
    double debitAmount = 5.00;
    String ref1 = "test ref1";

    assertThat(new GenericCsvTransaction.Builder(transactionDate, transactionType, debitAmount).transactionRef1(ref1).build())
        .isNotNull()
        .extracting(
            "transactionDate",
            "transactionType",
            "debitAmount",
            "creditAmount",
            "transactionRef1",
            "transactionRef2",
            "transactionRef3"
        )
        .contains(
            transactionDate,
            transactionType,
            5.00,
            0.00,
            "test ref1",
            "",
            ""
        );
  }

  @Test
  void builder_nullRef1() throws MonetaryAmountException {
    LocalDate transactionDate = LocalDate.of(2019, 12, 27);
    TransactionType transactionType = TransactionType.PAY_NOW;
    double debitAmount = 5.00;
    String ref1 = null;

    assertThat(new GenericCsvTransaction.Builder(transactionDate, transactionType, debitAmount).transactionRef1(ref1).build())
        .isNotNull()
        .extracting(
            "transactionDate",
            "transactionType",
            "debitAmount",
            "creditAmount",
            "transactionRef1",
            "transactionRef2",
            "transactionRef3"
        )
        .contains(
            transactionDate,
            transactionType,
            5.00,
            0.00,
            "",
            "",
            ""
        );
  }

  @Test
  void builder_emptyRef1() throws MonetaryAmountException {
    LocalDate transactionDate = LocalDate.of(2019, 12, 27);
    TransactionType transactionType = TransactionType.PAY_NOW;
    double debitAmount = 5.00;
    String ref1 = "";

    assertThat(new GenericCsvTransaction.Builder(transactionDate, transactionType, debitAmount).transactionRef1(ref1).build())
        .isNotNull()
        .extracting(
            "transactionDate",
            "transactionType",
            "debitAmount",
            "creditAmount",
            "transactionRef1",
            "transactionRef2",
            "transactionRef3"
        )
        .contains(
            transactionDate,
            transactionType,
            5.00,
            0.00,
            "",
            "",
            ""
        );
  }

  @Test
  void builder_ref2() throws MonetaryAmountException {
    LocalDate transactionDate = LocalDate.of(2019, 12, 27);
    TransactionType transactionType = TransactionType.PAY_NOW;
    double debitAmount = 5.00;
    String ref2 = "test ref2";

    assertThat(new GenericCsvTransaction.Builder(transactionDate, transactionType, debitAmount).transactionRef2(ref2).build())
        .isNotNull()
        .extracting(
            "transactionDate",
            "transactionType",
            "debitAmount",
            "creditAmount",
            "transactionRef1",
            "transactionRef2",
            "transactionRef3"
        )
        .contains(
            transactionDate,
            transactionType,
            5.00,
            0.00,
            "",
            "test ref2",
            ""
        );
  }

  @Test
  void builder_nullRef2() throws MonetaryAmountException {
    LocalDate transactionDate = LocalDate.of(2019, 12, 27);
    TransactionType transactionType = TransactionType.PAY_NOW;
    double debitAmount = 5.00;
    String ref2 = null;

    assertThat(new GenericCsvTransaction.Builder(transactionDate, transactionType, debitAmount).transactionRef2(ref2).build())
        .isNotNull()
        .extracting(
            "transactionDate",
            "transactionType",
            "debitAmount",
            "creditAmount",
            "transactionRef1",
            "transactionRef2",
            "transactionRef3"
        )
        .contains(
            transactionDate,
            transactionType,
            5.00,
            0.00,
            "",
            "",
            ""
        );
  }

  @Test
  void builder_ref2OnlyWhiteSpace() throws MonetaryAmountException {
    LocalDate transactionDate = LocalDate.of(2019, 12, 27);
    TransactionType transactionType = TransactionType.PAY_NOW;
    double debitAmount = 5.00;
    String ref2 = "      ";

    assertThat(new GenericCsvTransaction.Builder(transactionDate, transactionType, debitAmount).transactionRef2(ref2).build())
        .isNotNull()
        .extracting(
            "transactionDate",
            "transactionType",
            "debitAmount",
            "creditAmount",
            "transactionRef1",
            "transactionRef2",
            "transactionRef3"
        )
        .contains(
            transactionDate,
            transactionType,
            5.00,
            0.00,
            "",
            "",
            ""
        );
  }

  @Test
  void builder_ref3() throws MonetaryAmountException {
    LocalDate transactionDate = LocalDate.of(2019, 12, 27);
    TransactionType transactionType = TransactionType.PAY_NOW;
    double debitAmount = 5.00;
    String ref3 = "test ref3";

    assertThat(new GenericCsvTransaction.Builder(transactionDate, transactionType, debitAmount).transactionRef3(ref3).build())
        .isNotNull()
        .extracting(
            "transactionDate",
            "transactionType",
            "debitAmount",
            "creditAmount",
            "transactionRef1",
            "transactionRef2",
            "transactionRef3"
        )
        .contains(
            transactionDate,
            transactionType,
            5.00,
            0.00,
            "",
            "",
            "test ref3"
        );
  }

  @Test
  void builder_nullRef3() throws MonetaryAmountException {
    LocalDate transactionDate = LocalDate.of(2019, 12, 27);
    TransactionType transactionType = TransactionType.PAY_NOW;
    double debitAmount = 5.00;
    String ref3 = null;

    assertThat(new GenericCsvTransaction.Builder(transactionDate, transactionType, debitAmount).transactionRef3(ref3).build())
        .isNotNull()
        .extracting(
            "transactionDate",
            "transactionType",
            "debitAmount",
            "creditAmount",
            "transactionRef1",
            "transactionRef2",
            "transactionRef3"
        )
        .contains(
            transactionDate,
            transactionType,
            5.00,
            0.00,
            "",
            "",
            ""
        );
  }

  @Test
  void builder_ref3OnlyWhiteSpace() throws MonetaryAmountException {
    LocalDate transactionDate = LocalDate.of(2019, 12, 27);
    TransactionType transactionType = TransactionType.PAY_NOW;
    double debitAmount = 5.00;
    String ref3 = "      ";

    assertThat(new GenericCsvTransaction.Builder(transactionDate, transactionType, debitAmount).transactionRef3(ref3).build())
        .isNotNull()
        .extracting(
            "transactionDate",
            "transactionType",
            "debitAmount",
            "creditAmount",
            "transactionRef1",
            "transactionRef2",
            "transactionRef3"
        )
        .contains(
            transactionDate,
            transactionType,
            5.00,
            0.00,
            "",
            "",
            ""
        );
  }

  @Test
  void builder_positiveDebitAndCredit() throws MonetaryAmountException {
    LocalDate transactionDate = LocalDate.of(2019, 12, 27);
    TransactionType transactionType = TransactionType.PAY_NOW;
    double debitAmount = 5.00;
    double creditAmount = 4.50;

    assertThatThrownBy(() ->
        new GenericCsvTransaction.Builder(transactionDate, transactionType, debitAmount).creditAmount(creditAmount).build())
        .isInstanceOf(MonetaryAmountException.class)
        .hasMessage("Debit and credit cannot be co-exist at same time.");
  }

  @Test
  void equals_sameObjectReference() throws MonetaryAmountException {
    LocalDate transactionDate = LocalDate.of(2019, 12, 27);
    TransactionType transactionType = TransactionType.PAY_NOW;
    double debitAmount = 5.00;
    GenericCsvTransaction genericCsvTransaction1 = new GenericCsvTransaction.Builder(transactionDate, transactionType, debitAmount).build();

    assertThat(genericCsvTransaction1.equals(genericCsvTransaction1)).isTrue();
  }

  @Test
  void equals_notSameObject() throws MonetaryAmountException {
    LocalDate transactionDate = LocalDate.of(2019, 12, 27);
    TransactionType transactionType = TransactionType.PAY_NOW;
    double debitAmount = 5.00;
    GenericCsvTransaction genericCsvTransaction1 = new GenericCsvTransaction.Builder(transactionDate, transactionType, debitAmount).build();

    assertThat(genericCsvTransaction1.equals(transactionType)).isFalse();
  }

  @Test
  void equals_null() throws MonetaryAmountException {
    LocalDate transactionDate = LocalDate.of(2019, 12, 27);
    TransactionType transactionType = TransactionType.PAY_NOW;
    double debitAmount = 5.00;
    GenericCsvTransaction genericCsvTransaction1 = new GenericCsvTransaction.Builder(transactionDate, transactionType, debitAmount).build();

    assertThat(genericCsvTransaction1.equals(null)).isFalse();
  }

  @Test
  void equals_allFieldsAreEqual() throws MonetaryAmountException {
    LocalDate transactionDate = LocalDate.of(2019, 12, 27);
    TransactionType transactionType = TransactionType.PAY_NOW;
    double debitAmount = 5.00;
    GenericCsvTransaction genericCsvTransaction1 = new GenericCsvTransaction.Builder(transactionDate, transactionType, debitAmount).build();
    GenericCsvTransaction genericCsvTransaction2 = new GenericCsvTransaction.Builder(transactionDate, transactionType, debitAmount).build();
    assertThat(genericCsvTransaction1.equals(genericCsvTransaction2)).isTrue();
  }

  @Test
  void equals_differentDebitAmount() throws MonetaryAmountException {
    LocalDate transactionDate = LocalDate.of(2019, 12, 27);
    TransactionType transactionType = TransactionType.PAY_NOW;
    double debitAmount = 5.00;
    GenericCsvTransaction genericCsvTransaction1 = new GenericCsvTransaction.Builder(transactionDate, transactionType, debitAmount).build();
    GenericCsvTransaction genericCsvTransaction2 = new GenericCsvTransaction.Builder(transactionDate, transactionType, debitAmount + 0.01)
        .build();
    assertThat(genericCsvTransaction1.equals(genericCsvTransaction2)).isFalse();
  }

  @Test
  void equals_differentCreditAmount() throws MonetaryAmountException {
    LocalDate transactionDate = LocalDate.of(2019, 12, 27);
    TransactionType transactionType = TransactionType.PAY_NOW;
    double creditAmount = 5.00;
    GenericCsvTransaction genericCsvTransaction1 = new GenericCsvTransaction
        .Builder(transactionDate, transactionType, 0.00)
        .creditAmount(creditAmount)
        .build();

    GenericCsvTransaction genericCsvTransaction2 = new GenericCsvTransaction
        .Builder(transactionDate, transactionType, 0.00)
        .creditAmount(creditAmount + 0.01)
        .build();
    assertThat(genericCsvTransaction1.equals(genericCsvTransaction2)).isFalse();
  }

  @Test
  void equals_differentTransactionDate() throws MonetaryAmountException {
    LocalDate transactionDate = LocalDate.of(2019, 12, 27);
    TransactionType transactionType = TransactionType.PAY_NOW;
    double debitAmount = 5.00;
    GenericCsvTransaction genericCsvTransaction1 = new GenericCsvTransaction.Builder(transactionDate, transactionType, debitAmount).build();
    GenericCsvTransaction genericCsvTransaction2 = new GenericCsvTransaction.Builder(transactionDate.plusDays(1L), transactionType,
        debitAmount).build();
    assertThat(genericCsvTransaction1.equals(genericCsvTransaction2)).isFalse();
  }

  @Test
  void equals_differentTransactionRef1() throws MonetaryAmountException {
    LocalDate transactionDate = LocalDate.of(2019, 12, 27);
    TransactionType transactionType = TransactionType.PAY_NOW;
    double debitAmount = 5.00;
    GenericCsvTransaction genericCsvTransaction1 = new GenericCsvTransaction.Builder(transactionDate, transactionType, debitAmount).build();
    GenericCsvTransaction genericCsvTransaction2 = new GenericCsvTransaction.Builder(transactionDate, transactionType, debitAmount)
        .transactionRef1("Test").build();
    assertThat(genericCsvTransaction1.equals(genericCsvTransaction2)).isFalse();
  }

  @Test
  void equals_differentTransactionRef2() throws MonetaryAmountException {
    LocalDate transactionDate = LocalDate.of(2019, 12, 27);
    TransactionType transactionType = TransactionType.PAY_NOW;
    double debitAmount = 5.00;
    GenericCsvTransaction genericCsvTransaction1 = new GenericCsvTransaction.Builder(transactionDate, transactionType, debitAmount).build();
    GenericCsvTransaction genericCsvTransaction2 = new GenericCsvTransaction.Builder(transactionDate, transactionType, debitAmount)
        .transactionRef2("Test").build();
    assertThat(genericCsvTransaction1.equals(genericCsvTransaction2)).isFalse();
  }

  @Test
  void equals_differentTransactionRef3() throws MonetaryAmountException {
    LocalDate transactionDate = LocalDate.of(2019, 12, 27);
    TransactionType transactionType = TransactionType.PAY_NOW;
    double debitAmount = 5.00;
    GenericCsvTransaction genericCsvTransaction1 = new GenericCsvTransaction.Builder(transactionDate, transactionType, debitAmount).build();
    GenericCsvTransaction genericCsvTransaction2 = new GenericCsvTransaction.Builder(transactionDate, transactionType, debitAmount)
        .transactionRef3("Test").build();
    assertThat(genericCsvTransaction1.equals(genericCsvTransaction2)).isFalse();
  }

  @Test
  void equals_differentTransactionType() throws MonetaryAmountException {
    LocalDate transactionDate = LocalDate.of(2019, 12, 27);
    TransactionType transactionType = TransactionType.PAY_NOW;
    double debitAmount = 5.00;
    GenericCsvTransaction genericCsvTransaction1 = new GenericCsvTransaction.Builder(transactionDate, transactionType, debitAmount).build();
    GenericCsvTransaction genericCsvTransaction2 = new GenericCsvTransaction.Builder(transactionDate, TransactionType.MASTERCARD,
        debitAmount).build();
    assertThat(genericCsvTransaction1.equals(genericCsvTransaction2)).isFalse();
  }

  @Test
  void hashCode_same() throws MonetaryAmountException {
    LocalDate transactionDate = LocalDate.of(2019, 12, 27);
    TransactionType transactionType = TransactionType.PAY_NOW;
    double debitAmount = 5.00;
    GenericCsvTransaction genericCsvTransaction1 = new GenericCsvTransaction.Builder(transactionDate, transactionType, debitAmount).build();
    int expectedHashCode = Objects.hashCode(genericCsvTransaction1);
    assertThat(genericCsvTransaction1.hashCode())
        .isNotZero()
        .isEqualByComparingTo(expectedHashCode);
  }
}
