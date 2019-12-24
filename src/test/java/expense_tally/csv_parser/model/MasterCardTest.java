package expense_tally.csv_parser.model;

import expense_tally.csv_parser.exception.InvalidReferenceDateException;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MasterCardTest {

  @Test
  void setCardNumber() {
  }

  @Test
  void extractTransactionDate_success() throws InvalidReferenceDateException {
    LocalDate testBankTransactionDate = LocalDate.now();
    String testReference1 = "TAPAS 26               SI NG 20DEC";

    int expectedYear = LocalDate.now().getYear();
    LocalDate expectedDate = LocalDate.of(expectedYear, 12, 20);
    assertEquals(expectedDate, MasterCard.extractTransactionDate(testBankTransactionDate, testReference1));
  }

  @Test
  void extractTransactionDate_nullBankTransactionDate() {
    String testReference1 = "TAPAS 26               SI NG 20DEC";
    assertThrows(NullPointerException.class,
        () -> MasterCard.extractTransactionDate(null, testReference1));
  }

  @Test
  void extractTransactionDate_nullReference1() {
    LocalDate testBankTransactionDate = LocalDate.now();
    assertThrows(NullPointerException.class,
        () -> MasterCard.extractTransactionDate(testBankTransactionDate, null));
  }

  @Test
  void extractTransactionDate_reference1EmptyString() throws InvalidReferenceDateException {
    LocalDate testBankTransactionDate = LocalDate.now();
    String testReference1 = "";
    assertEquals(testBankTransactionDate, MasterCard.extractTransactionDate(testBankTransactionDate, testReference1));
  }

  @Test
  void extractTransactionDate_noDateInReference1() throws InvalidReferenceDateException {
    LocalDate testBankTransactionDate = LocalDate.now();
    String testReference1 = "TAPAS 26";
    Exception exception = assertThrows(InvalidReferenceDateException.class,
        () -> MasterCard.extractTransactionDate(testBankTransactionDate, testReference1));
    assertEquals("Referenced date is not well formatted.", exception.getMessage());
  }

  @Test
  void extractTransactionDate_dateConversion() throws InvalidReferenceDateException {
    LocalDate testBankTransactionDate = LocalDate.of(2019, 01, 05);
    String testReference1 = "TAPAS 26               SI NG 20DEC";
    LocalDate expectedDate = LocalDate.of(2018, 12, 20);
    assertEquals(expectedDate, MasterCard.extractTransactionDate(testBankTransactionDate, testReference1));
  }
}