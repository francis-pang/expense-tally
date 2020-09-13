package expense_tally.reconciliation;

import expense_tally.model.csv.GenericCsvTransaction;
import expense_tally.model.csv.TransactionType;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DiscrepantTransactionTest {

  @Test
  void from_null() {
    // We explicitly do not guard against NPE here because null object shouldn't be passed in the first place, and we
    // do not know how to handle that as well
    assertThatThrownBy(() -> DiscrepantTransaction.from(null))
        .isInstanceOf(NullPointerException.class);
  }

  @Test
  void from_creditAmount() {
    LocalDate testDate = LocalDate.now();
    GenericCsvTransaction genericCsvTransaction = new CsvTransactionTestBuilder()
        .transactionDate(testDate.getYear(), testDate.getMonthValue(), testDate.getDayOfMonth())
        .transactionRef1("Test ref 1")
        .transactionRef2("Test ref 3")
        .transactionRef3("Testt ref 3")
        .build();
    DiscrepantTransaction discrepantTransaction = DiscrepantTransaction.from(genericCsvTransaction);
    assertThat(discrepantTransaction).isNotNull();
    SoftAssertions softAssertions = new SoftAssertions();
    softAssertions.assertThat(discrepantTransaction.getTime()).isEqualTo(testDate);
    softAssertions.assertThat(discrepantTransaction.getAmount()).isEqualByComparingTo(0.8);
    softAssertions.assertThat(discrepantTransaction.getDescription()).isEqualTo("Test ref 1 Test ref 3 Testt ref 3");
    softAssertions.assertThat(discrepantTransaction.getType()).isEqualByComparingTo(TransactionType.MASTERCARD);
    softAssertions.assertAll();
  }

  @Test
  void from_debitAmount() {
    LocalDate testDate = LocalDate.now();
    GenericCsvTransaction genericCsvTransaction = new CsvTransactionTestBuilder()
        .transactionDate(testDate.getYear(), testDate.getMonthValue(), testDate.getDayOfMonth())
        .debitAmount(6.66)
        .transactionRef1("Test ref 1")
        .transactionRef2("Test ref 3")
        .transactionRef3("Testt ref 3")
        .build();
    DiscrepantTransaction discrepantTransaction = DiscrepantTransaction.from(genericCsvTransaction);
    assertThat(discrepantTransaction).isNotNull();
    SoftAssertions softAssertions = new SoftAssertions();
    softAssertions.assertThat(discrepantTransaction.getTime()).isEqualTo(testDate);
    softAssertions.assertThat(discrepantTransaction.getAmount()).isEqualByComparingTo(6.66);
    softAssertions.assertThat(discrepantTransaction.getDescription()).isEqualTo("Test ref 1 Test ref 3 Testt ref 3");
    softAssertions.assertThat(discrepantTransaction.getType()).isEqualByComparingTo(TransactionType.MASTERCARD);
    softAssertions.assertAll();
  }

  @Test
  void from_zeroAmount() {
    LocalDate testDate = LocalDate.now();
    GenericCsvTransaction genericCsvTransaction = new CsvTransactionTestBuilder()
        .transactionDate(testDate.getYear(), testDate.getMonthValue(), testDate.getDayOfMonth())
        .debitAmount(0.00)
        .transactionRef1("Test ref 1")
        .transactionRef2("Test ref 3")
        .transactionRef3("Testt ref 3")
        .build();
    DiscrepantTransaction discrepantTransaction = DiscrepantTransaction.from(genericCsvTransaction);
    assertThat(discrepantTransaction).isNotNull();
    SoftAssertions softAssertions = new SoftAssertions();
    softAssertions.assertThat(discrepantTransaction.getTime()).isEqualTo(testDate);
    softAssertions.assertThat(discrepantTransaction.getAmount()).isEqualByComparingTo(0.00);
    softAssertions.assertThat(discrepantTransaction.getDescription()).isEqualTo("Test ref 1 Test ref 3 Testt ref 3");
    softAssertions.assertThat(discrepantTransaction.getType()).isEqualByComparingTo(TransactionType.MASTERCARD);
    softAssertions.assertAll();
  }

  @Test
  void from_spaceOutsideRef() {
    LocalDate testDate = LocalDate.now();
    GenericCsvTransaction genericCsvTransaction = new CsvTransactionTestBuilder()
        .transactionDate(testDate.getYear(), testDate.getMonthValue(), testDate.getDayOfMonth())
        .transactionRef1("   Test ref 1    ")
        .transactionRef2("Test ref 3")
        .transactionRef3("Testt ref 3")
        .build();
    DiscrepantTransaction discrepantTransaction = DiscrepantTransaction.from(genericCsvTransaction);
    assertThat(discrepantTransaction).isNotNull();
    SoftAssertions softAssertions = new SoftAssertions();
    softAssertions.assertThat(discrepantTransaction.getTime()).isEqualTo(testDate);
    softAssertions.assertThat(discrepantTransaction.getAmount()).isEqualByComparingTo(0.8);
    softAssertions.assertThat(discrepantTransaction.getDescription()).isEqualTo("Test ref 1 Test ref 3 Testt ref 3");
    softAssertions.assertThat(discrepantTransaction.getType()).isEqualByComparingTo(TransactionType.MASTERCARD);
    softAssertions.assertAll();
  }

  @Test
  void from_emptyRef3() {
    LocalDate testDate = LocalDate.now();
    GenericCsvTransaction genericCsvTransaction = new CsvTransactionTestBuilder()
        .transactionDate(testDate.getYear(), testDate.getMonthValue(), testDate.getDayOfMonth())
        .transactionRef1("   Test ref 1    ")
        .transactionRef2("Test ref 3")
        .transactionRef3("")
        .build();
    DiscrepantTransaction discrepantTransaction = DiscrepantTransaction.from(genericCsvTransaction);
    assertThat(discrepantTransaction).isNotNull();
    SoftAssertions softAssertions = new SoftAssertions();
    softAssertions.assertThat(discrepantTransaction.getTime()).isEqualTo(testDate);
    softAssertions.assertThat(discrepantTransaction.getAmount()).isEqualByComparingTo(0.8);
    softAssertions.assertThat(discrepantTransaction.getDescription()).isEqualTo("Test ref 1 Test ref 3");
    softAssertions.assertThat(discrepantTransaction.getType()).isEqualByComparingTo(TransactionType.MASTERCARD);
    softAssertions.assertAll();
  }

  @Test
  void getTime() {
    LocalDate testDate = LocalDate.parse("2020-05-28");
    GenericCsvTransaction genericCsvTransaction = new CsvTransactionTestBuilder()
        .transactionDate(testDate.getYear(), testDate.getMonthValue(), testDate.getDayOfMonth())
        .build();
    DiscrepantTransaction discrepantTransaction = DiscrepantTransaction.from(genericCsvTransaction);
    assertThat(discrepantTransaction).isNotNull();
    assertThat(discrepantTransaction.getTime())
        .isNotNull()
        .isEqualTo("2020-05-28");
  }

  @Test
  void getAmount() {
    GenericCsvTransaction genericCsvTransaction = new CsvTransactionTestBuilder().build();
    DiscrepantTransaction discrepantTransaction = DiscrepantTransaction.from(genericCsvTransaction);
    assertThat(discrepantTransaction).isNotNull();
    assertThat(discrepantTransaction.getAmount()).isEqualByComparingTo(0.8);
  }

  @Test
  void getDescription() {
    LocalDate testDate = LocalDate.now();
    GenericCsvTransaction genericCsvTransaction = new CsvTransactionTestBuilder()
        .transactionRef1("Test ref 1")
        .transactionRef2("Test ref 3")
        .transactionRef3("Testt ref 3")
        .build();
    DiscrepantTransaction discrepantTransaction = DiscrepantTransaction.from(genericCsvTransaction);
    assertThat(discrepantTransaction).isNotNull();
    assertThat(discrepantTransaction.getDescription())
        .isNotBlank()
        .isEqualTo("Test ref 1 Test ref 3 Testt ref 3");
  }

  @Test
  void getType() {
    LocalDate testDate = LocalDate.now();
    GenericCsvTransaction genericCsvTransaction = new CsvTransactionTestBuilder()
        .transactionDate(testDate.getYear(), testDate.getMonthValue(), testDate.getDayOfMonth())
        .transactionRef1("Test ref 1")
        .transactionRef2("Test ref 3")
        .transactionRef3("Testt ref 3")
        .transactionType(TransactionType.GIRO)
        .build();
    DiscrepantTransaction discrepantTransaction = DiscrepantTransaction.from(genericCsvTransaction);
    assertThat(discrepantTransaction).isNotNull();
    assertThat(discrepantTransaction.getType())
        .isNotNull()
        .isEqualByComparingTo(TransactionType.GIRO);
  }
}