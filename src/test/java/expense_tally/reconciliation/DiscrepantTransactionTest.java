package expense_tally.reconciliation;

import expense_tally.csv_parser.CsvTransaction;
import expense_tally.csv_parser.TransactionType;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class DiscrepantTransactionTest {

  @Test
  void from_null() {
    // We explicitly do not guard against NPE here because null object shouldn't be passed in the first place, and we
    // do not know how to handle that as well
    Assertions.assertThatThrownBy(() -> DiscrepantTransaction.from(null))
        .isInstanceOf(NullPointerException.class);
  }

  @Test
  void from_creditAmount() {
    LocalDate testDate = LocalDate.now();
    CsvTransaction csvTransaction = CsvTransaction.of(
        testDate,
        TransactionType.GIRO,
        0.0,
        7.89,
        "Test ref 1",
        "Test ref 3",
        "Testt ref 3"
    );
    DiscrepantTransaction discrepantTransaction = DiscrepantTransaction.from(csvTransaction);
    assertThat(discrepantTransaction).isNotNull();
    SoftAssertions softAssertions = new SoftAssertions();
    softAssertions.assertThat(discrepantTransaction.getTime()).isEqualTo(testDate);
    softAssertions.assertThat(discrepantTransaction.getAmount()).isEqualByComparingTo(7.89);
    softAssertions.assertThat(discrepantTransaction.getDescription()).isEqualTo("Test ref 1 Test ref 3 Testt ref 3");
    softAssertions.assertThat(discrepantTransaction.getType()).isEqualByComparingTo(TransactionType.GIRO);
    softAssertions.assertAll();
  }

  @Test
  void from_debitAmount() {
    LocalDate testDate = LocalDate.now();
    CsvTransaction csvTransaction = CsvTransaction.of(
        testDate,
        TransactionType.GIRO,
        6.66,
        0.00,
        "Test ref 1",
        "Test ref 3",
        "Testt ref 3"
    );
    DiscrepantTransaction discrepantTransaction = DiscrepantTransaction.from(csvTransaction);
    assertThat(discrepantTransaction).isNotNull();
    SoftAssertions softAssertions = new SoftAssertions();
    softAssertions.assertThat(discrepantTransaction.getTime()).isEqualTo(testDate);
    softAssertions.assertThat(discrepantTransaction.getAmount()).isEqualByComparingTo(6.66);
    softAssertions.assertThat(discrepantTransaction.getDescription()).isEqualTo("Test ref 1 Test ref 3 Testt ref 3");
    softAssertions.assertThat(discrepantTransaction.getType()).isEqualByComparingTo(TransactionType.GIRO);
    softAssertions.assertAll();
  }

  @Test
  void from_zeroAmount() {
    LocalDate testDate = LocalDate.now();
    CsvTransaction csvTransaction = CsvTransaction.of(
        testDate,
        TransactionType.GIRO,
        0.00,
        0.00,
        "Test ref 1",
        "Test ref 3",
        "Testt ref 3"
    );
    DiscrepantTransaction discrepantTransaction = DiscrepantTransaction.from(csvTransaction);
    assertThat(discrepantTransaction).isNotNull();
    SoftAssertions softAssertions = new SoftAssertions();
    softAssertions.assertThat(discrepantTransaction.getTime()).isEqualTo(testDate);
    softAssertions.assertThat(discrepantTransaction.getAmount()).isEqualByComparingTo(0.00);
    softAssertions.assertThat(discrepantTransaction.getDescription()).isEqualTo("Test ref 1 Test ref 3 Testt ref 3");
    softAssertions.assertThat(discrepantTransaction.getType()).isEqualByComparingTo(TransactionType.GIRO);
    softAssertions.assertAll();
  }

  @Test
  void from_spaceOutsideRef() {
    LocalDate testDate = LocalDate.now();
    CsvTransaction csvTransaction = CsvTransaction.of(
        testDate,
        TransactionType.GIRO,
        6.66,
        0.00,
        "   Test ref 1    ",
        "Test ref 3",
        "Testt ref 3"
    );
    DiscrepantTransaction discrepantTransaction = DiscrepantTransaction.from(csvTransaction);
    assertThat(discrepantTransaction).isNotNull();
    SoftAssertions softAssertions = new SoftAssertions();
    softAssertions.assertThat(discrepantTransaction.getTime()).isEqualTo(testDate);
    softAssertions.assertThat(discrepantTransaction.getAmount()).isEqualByComparingTo(6.66);
    softAssertions.assertThat(discrepantTransaction.getDescription()).isEqualTo("Test ref 1 Test ref 3 Testt ref 3");
    softAssertions.assertThat(discrepantTransaction.getType()).isEqualByComparingTo(TransactionType.GIRO);
    softAssertions.assertAll();
  }

  @Test
  void from_emptyRef3() {
    LocalDate testDate = LocalDate.now();
    CsvTransaction csvTransaction = CsvTransaction.of(
        testDate,
        TransactionType.GIRO,
        6.66,
        0.00,
        "   Test ref 1    ",
        "Test ref 3",
        ""
    );
    DiscrepantTransaction discrepantTransaction = DiscrepantTransaction.from(csvTransaction);
    assertThat(discrepantTransaction).isNotNull();
    SoftAssertions softAssertions = new SoftAssertions();
    softAssertions.assertThat(discrepantTransaction.getTime()).isEqualTo(testDate);
    softAssertions.assertThat(discrepantTransaction.getAmount()).isEqualByComparingTo(6.66);
    softAssertions.assertThat(discrepantTransaction.getDescription()).isEqualTo("Test ref 1 Test ref 3");
    softAssertions.assertThat(discrepantTransaction.getType()).isEqualByComparingTo(TransactionType.GIRO);
    softAssertions.assertAll();
  }

  @Test
  void getTime() {
    LocalDate testDate = LocalDate.parse("2020-05-28");
    CsvTransaction csvTransaction = CsvTransaction.of(
        testDate,
        TransactionType.GIRO,
        50.0,
        0.00,
        "Test ref 1",
        "Test ref 3",
        "Testt ref 3"
    );
    DiscrepantTransaction discrepantTransaction = DiscrepantTransaction.from(csvTransaction);
    assertThat(discrepantTransaction).isNotNull();
    assertThat(discrepantTransaction.getTime())
        .isNotNull()
        .isEqualTo("2020-05-28");
  }

  @Test
  void getAmount() {
    LocalDate testDate = LocalDate.now();
    CsvTransaction csvTransaction = CsvTransaction.of(
        testDate,
        TransactionType.GIRO,
        50.0,
        0.00,
        "Test ref 1",
        "Test ref 3",
        "Testt ref 3"
    );
    DiscrepantTransaction discrepantTransaction = DiscrepantTransaction.from(csvTransaction);
    assertThat(discrepantTransaction).isNotNull();
    assertThat(discrepantTransaction.getAmount()).isEqualByComparingTo(50.0);
  }

  @Test
  void getDescription() {
    LocalDate testDate = LocalDate.now();
    CsvTransaction csvTransaction = CsvTransaction.of(
        testDate,
        TransactionType.GIRO,
        50.0,
        0.00,
        "Test ref 1",
        "Test ref 3",
        "Testt ref 3"
    );
    DiscrepantTransaction discrepantTransaction = DiscrepantTransaction.from(csvTransaction);
    assertThat(discrepantTransaction).isNotNull();
    assertThat(discrepantTransaction.getDescription())
        .isNotBlank()
        .isEqualTo("Test ref 1 Test ref 3 Testt ref 3");
  }

  @Test
  void getType() {
    LocalDate testDate = LocalDate.now();
    CsvTransaction csvTransaction = CsvTransaction.of(
        testDate,
        TransactionType.GIRO,
        50.0,
        0.00,
        "Test ref 1",
        "Test ref 3",
        "Testt ref 3"
    );
    DiscrepantTransaction discrepantTransaction = DiscrepantTransaction.from(csvTransaction);
    assertThat(discrepantTransaction).isNotNull();
    assertThat(discrepantTransaction.getDescription())
        .isNotBlank()
        .isEqualTo("Test ref 1 Test ref 3 Testt ref 3");
  }
}