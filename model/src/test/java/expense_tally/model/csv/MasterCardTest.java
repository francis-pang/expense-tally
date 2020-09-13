package expense_tally.model.csv;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MasterCardTest extends AbstractCsvTransaction {

  @Test
  void from_success() throws MonetaryAmountException {
    // Create GenericCsvTransaction
    LocalDate transactionDate = LocalDate.of(2019, 12, 27);
    GenericCsvTransaction testGenericCsvTransaction =
        new GenericCsvTransaction.Builder(transactionDate, TransactionType.MASTERCARD, 4.55)
        .transactionRef1("TAPAS SI NG 20DEC")
        .transactionRef2("5132-4172-5981-4347")
        .build();
    assertThat(MasterCard.from(testGenericCsvTransaction))
        .isNotNull()
        .extracting("cardNumber",
            "transactionDate",
            "debitAmount",
            "creditAmount",
            "transactionRef1",
            "transactionRef2",
            "transactionRef3",
            "transactionType")
        .contains(
            "5132-4172-5981-4347",
            LocalDate.of(2019, 12, 20),
            4.55,
            0.00,
            "TAPAS SI NG 20DEC",
            "5132-4172-5981-4347",
            "",
            TransactionType.MASTERCARD
        );
  }

  @Test
  void from_nullBankTransactionDate() {
    // This is now an invalid test case. The only way to initialise MasterCard is from GenericCsvTransaction, and
    // GenericCsvTransaction can't have null transaction date. Transitively, there won't be a null transaction date as well.
  }

  /**
   * In this test, the reference 1 of the GenericCsvTransaction is empty string, so we will not expect the change in the
   * transaction date.
   */
  @Test
  void extractTransactionDate_emptyReference1() throws MonetaryAmountException {
    LocalDate transactionDate = LocalDate.of(2019, 12, 27);
    GenericCsvTransaction testGenericCsvTransaction = new GenericCsvTransaction.Builder(transactionDate, TransactionType.MASTERCARD, 4.55)
        .transactionRef2("5132-4172-5981-4347")
        .build();
    assertThat(MasterCard.from(testGenericCsvTransaction))
        .isNotNull()
        .extracting("cardNumber",
            "transactionDate",
            "debitAmount",
            "creditAmount",
            "transactionRef1",
            "transactionRef2",
            "transactionRef3",
            "transactionType")
        .contains(
            "5132-4172-5981-4347",
            LocalDate.of(2019, 12, 27),
            4.55,
            0.00,
            "",
            "5132-4172-5981-4347",
            "",
            TransactionType.MASTERCARD
        );
  }

  @Test
  void extractTransactionDate_noDateInReference1() throws MonetaryAmountException {
    // Create GenericCsvTransaction
    LocalDate transactionDate = LocalDate.of(2019, 12, 27);
    GenericCsvTransaction testGenericCsvTransaction = new GenericCsvTransaction.Builder(transactionDate, TransactionType.MASTERCARD, 4.55)
        .transactionRef1("TAPAS SI NG")
        .transactionRef2("5132-4172-5981-4347")
        .build();
    assertThat(MasterCard.from(testGenericCsvTransaction))
        .isNotNull()
        .extracting("cardNumber",
            "transactionDate",
            "debitAmount",
            "creditAmount",
            "transactionRef1",
            "transactionRef2",
            "transactionRef3",
            "transactionType")
        .contains(
            "5132-4172-5981-4347",
            LocalDate.of(2019, 12, 27),
            4.55,
            0.00,
            "TAPAS SI NG",
            "5132-4172-5981-4347",
            "",
            TransactionType.MASTERCARD
        );
  }

  @Test
  void extractTransactionDate_dateConversion() throws MonetaryAmountException {
    // Create GenericCsvTransaction
    LocalDate transactionDate = LocalDate.of(2019, 01, 27);
    GenericCsvTransaction testGenericCsvTransaction = new GenericCsvTransaction.Builder(transactionDate, TransactionType.MASTERCARD, 4.55)
        .transactionRef1("TAPAS SI NG 20DEC")
        .transactionRef2("5132-4172-5981-4347")
        .build();
    assertThat(MasterCard.from(testGenericCsvTransaction))
        .isNotNull()
        .extracting("cardNumber",
            "transactionDate",
            "debitAmount",
            "creditAmount",
            "transactionRef1",
            "transactionRef2",
            "transactionRef3",
            "transactionType")
        .contains(
            "5132-4172-5981-4347",
            LocalDate.of(2018, 12, 20),
            4.55,
            0.00,
            "TAPAS SI NG 20DEC",
            "5132-4172-5981-4347",
            "",
            TransactionType.MASTERCARD
        );
  }

  @Test
  void from_noCardNumber() throws MonetaryAmountException {
    // Create GenericCsvTransaction
    LocalDate transactionDate = LocalDate.of(2019, 12, 27);
    GenericCsvTransaction testGenericCsvTransaction = new GenericCsvTransaction.Builder(transactionDate, TransactionType.MASTERCARD, 4.55)
        .transactionRef1("TAPAS SI NG 20DEC")
        .build();
    assertThat(MasterCard.from(testGenericCsvTransaction))
        .isNotNull()
        .extracting("cardNumber",
            "transactionDate",
            "debitAmount",
            "creditAmount",
            "transactionRef1",
            "transactionRef2",
            "transactionRef3",
            "transactionType")
        .contains(
            null,
            LocalDate.of(2019, 12, 20),
            4.55,
            0.00,
            "TAPAS SI NG 20DEC",
            "",
            "",
            TransactionType.MASTERCARD
        );
  }

  @Test
  void from_invalidCardNumber() throws MonetaryAmountException {
    // Create GenericCsvTransaction
    LocalDate transactionDate = LocalDate.of(2019, 12, 27);
    GenericCsvTransaction testGenericCsvTransaction = new GenericCsvTransaction.Builder(transactionDate, TransactionType.MASTERCARD, 4.55)
        .transactionRef1("TAPAS SI NG 20DEC")
        .transactionRef2("5632-4172-5981-4347")
        .build();
    assertThatThrownBy(() -> MasterCard.from(testGenericCsvTransaction))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("MasterCard number is invalid.");
  }

  @Test
  void from_noTransactionType() throws MonetaryAmountException {
    // Create GenericCsvTransaction
    LocalDate transactionDate = LocalDate.of(2019, 12, 27);
    GenericCsvTransaction testGenericCsvTransaction = new GenericCsvTransaction.Builder(transactionDate, null, 4.55)
        .transactionRef1("TAPAS SI NG 20DEC")
        .transactionRef2("5632-4172-5981-4347")
        .build();
    assertThatThrownBy(() -> MasterCard.from(testGenericCsvTransaction))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("GenericCsvTransaction is not of MasterCard type.");
  }

  @Test
  void from_wrongTransactionType() throws MonetaryAmountException {
    LocalDate transactionDate = LocalDate.of(2019, 12, 27);
    GenericCsvTransaction testGenericCsvTransaction = new GenericCsvTransaction.Builder(transactionDate, TransactionType.PAY_NOW, 4.55)
        .transactionRef1("TAPAS SI NG 20DEC")
        .transactionRef2("5632-4172-5981-4347")
        .build();
    assertThatThrownBy(() -> MasterCard.from(testGenericCsvTransaction))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("GenericCsvTransaction is not of MasterCard type.");
  }

  @Test
  void from_noDateInRef1() throws MonetaryAmountException {
    LocalDate transactionDate = LocalDate.of(2019, 12, 27);
    GenericCsvTransaction testGenericCsvTransaction = new GenericCsvTransaction.Builder(transactionDate, TransactionType.MASTERCARD, 4.55)
        .transactionRef1("   a    ")
        .transactionRef2("5132-4172-5981-4347")
        .build();
    assertThat(MasterCard.from(testGenericCsvTransaction))
        .isNotNull()
        .extracting("cardNumber",
            "transactionDate",
            "debitAmount",
            "creditAmount",
            "transactionRef1",
            "transactionRef2",
            "transactionRef3",
            "transactionType")
        .contains(
            "5132-4172-5981-4347",
            LocalDate.of(2019, 12, 27),
            4.55,
            0.00,
            "   a    ",
            "5132-4172-5981-4347",
            "",
            TransactionType.MASTERCARD
        );
  }

  @Test
  void toString_test() throws MonetaryAmountException {
    LocalDate transactionDate = LocalDate.of(2019, 12, 27);
    GenericCsvTransaction testGenericCsvTransaction = new GenericCsvTransaction.Builder(transactionDate, TransactionType.MASTERCARD, 4.55)
        .transactionRef1("TAPAS SI NG 20DEC")
        .transactionRef2("5132-4172-5981-4347")
        .build();
    MasterCard masterCard = MasterCard.from(testGenericCsvTransaction);
    assertThat(masterCard.toString())
        .isNotBlank()
        .isEqualTo("MasterCard[cardNumber='5132-4172-5981-4347', transactionDate=2019-12-20, debitAmount=4.55, creditAmount=0.0, transactionRef1='TAPAS SI NG 20DEC', transactionRef2='null', transactionRef3='', transactionType=TransactionType[value='MST']]");
  }

  @Test
  void equals_sameObject() throws MonetaryAmountException {
    LocalDate transactionDate = LocalDate.of(2019, 12, 27);
    GenericCsvTransaction testGenericCsvTransaction = new GenericCsvTransaction.Builder(transactionDate, TransactionType.MASTERCARD, 4.55)
        .transactionRef1("TAPAS SI NG 20DEC")
        .transactionRef2("5132-4172-5981-4347")
        .build();
    MasterCard masterCard = MasterCard.from(testGenericCsvTransaction);
    MasterCard masterCardDifferenceReference = masterCard;
    assertThat(masterCard.equals(masterCardDifferenceReference)).isTrue();
  }

  @Test
  void equals_null() throws MonetaryAmountException {
    LocalDate transactionDate = LocalDate.of(2019, 12, 27);
    GenericCsvTransaction testGenericCsvTransaction = new GenericCsvTransaction.Builder(transactionDate, TransactionType.MASTERCARD, 4.55)
        .transactionRef1("TAPAS SI NG 20DEC")
        .transactionRef2("5132-4172-5981-4347")
        .build();
    MasterCard masterCard = MasterCard.from(testGenericCsvTransaction);
    MasterCard masterCardDifferenceReference = masterCard;
    assertThat(masterCard.equals(null)).isFalse();
  }

  @Test
  void equals_differentClass() throws MonetaryAmountException {
    LocalDate transactionDate = LocalDate.of(2019, 12, 27);
    GenericCsvTransaction testGenericCsvTransaction = new GenericCsvTransaction.Builder(transactionDate, TransactionType.MASTERCARD, 4.55)
        .transactionRef1("TAPAS SI NG 20DEC")
        .transactionRef2("5132-4172-5981-4347")
        .build();
    MasterCard masterCard = MasterCard.from(testGenericCsvTransaction);
    assertThat(masterCard.equals(testGenericCsvTransaction)).isFalse();
  }

  @Test
  void equals_differentBySuperClassComparison() throws MonetaryAmountException {
    LocalDate transactionDate = LocalDate.of(2019, 12, 27);
    GenericCsvTransaction testGenericCsvTransaction1 = new GenericCsvTransaction.Builder(transactionDate, TransactionType.MASTERCARD, 4.55)
        .transactionRef1("TAPAS SI NG 20DEC")
        .transactionRef2("5132-4172-5981-4347")
        .build();

    GenericCsvTransaction testGenericCsvTransaction2 = new GenericCsvTransaction.Builder(transactionDate, TransactionType.MASTERCARD, 4.55)
        .transactionRef1("TAPAS SI ")
        .transactionRef2("5132-4172-5981-4347")
        .build();
    MasterCard masterCard1 = MasterCard.from(testGenericCsvTransaction1);
    assertThat(masterCard1.equals(testGenericCsvTransaction2)).isFalse();
  }

  @Test
  void equals_differentCardNumber() throws MonetaryAmountException {
    LocalDate transactionDate = LocalDate.of(2019, 12, 27);
    GenericCsvTransaction testGenericCsvTransaction1 = new GenericCsvTransaction.Builder(transactionDate, TransactionType.MASTERCARD, 4.55)
        .transactionRef1("TAPAS SI NG 20DEC")
        .transactionRef2("5132-4172-5981-4347")
        .build();

    GenericCsvTransaction testGenericCsvTransaction2 = new GenericCsvTransaction.Builder(transactionDate, TransactionType.MASTERCARD, 4.55)
        .transactionRef1("TAPAS SI NG 20DEC")
        .transactionRef2("5132-4172-5981-4346")
        .build();
    MasterCard masterCard1 = MasterCard.from(testGenericCsvTransaction1);
    MasterCard masterCard2 = MasterCard.from(testGenericCsvTransaction2);
    assertThat(masterCard1.equals(masterCard2)).isFalse();
  }

  @Test
  void equals_symmetrySameClassTest() throws MonetaryAmountException {
    LocalDate transactionDate = LocalDate.of(2019, 12, 27);
    GenericCsvTransaction testGenericCsvTransaction1 = new GenericCsvTransaction.Builder(transactionDate, TransactionType.MASTERCARD, 4.55)
        .transactionRef1("TAPAS SI NG 20DEC")
        .transactionRef2("5132-4172-5981-4347")
        .build();

    GenericCsvTransaction testGenericCsvTransaction2 = new GenericCsvTransaction.Builder(transactionDate, TransactionType.MASTERCARD, 4.55)
        .transactionRef1("TAPAS SI NG 20DEC")
        .transactionRef2("5132-4172-5981-4347")
        .build();
    MasterCard masterCard1 = MasterCard.from(testGenericCsvTransaction1);
    MasterCard masterCard2 = MasterCard.from(testGenericCsvTransaction2);
    assertThat(masterCard1.equals(masterCard2)).isTrue();
  }

  /**
   * Transaction 1 and 2 are the same, while transaction 3 is different, in card number
   */
  @Test
  void equals_transitivityTest() throws MonetaryAmountException {
    LocalDate transactionDate = LocalDate.of(2019, 12, 27);
    GenericCsvTransaction testGenericCsvTransaction1 = new GenericCsvTransaction.Builder(transactionDate, TransactionType.MASTERCARD, 4.55)
        .transactionRef1("TAPAS SI NG 20DEC")
        .transactionRef2("5132-4172-5981-4346")
        .build();
    GenericCsvTransaction testGenericCsvTransaction2 = new GenericCsvTransaction.Builder(transactionDate, TransactionType.MASTERCARD, 4.55)
        .transactionRef1("TAPAS SI NG 20DEC")
        .transactionRef2("5132-4172-5981-4346")
        .build();
    GenericCsvTransaction testGenericCsvTransaction3 = new GenericCsvTransaction.Builder(transactionDate, TransactionType.MASTERCARD, 4.55)
        .transactionRef1("TAPAS SI NG 20DEC")
        .transactionRef2("5132-4172-5981-4346")
        .build();
    MasterCard masterCard1 = MasterCard.from(testGenericCsvTransaction1);
    MasterCard masterCard2 = MasterCard.from(testGenericCsvTransaction2);
    MasterCard masterCard3 = MasterCard.from(testGenericCsvTransaction3);
    SoftAssertions softAssertions = new SoftAssertions();
    softAssertions.assertThat(masterCard1.equals(masterCard2)).isTrue();
    // Transitivity test
    softAssertions.assertThat(masterCard2.equals(masterCard3))
        .isEqualTo(masterCard1.equals(masterCard3));
    softAssertions.assertAll();
  }

  @Test
  void equals_symmetryDifferentClassTest() throws MonetaryAmountException {
    LocalDate transactionDate = LocalDate.of(2019, 12, 27);
    GenericCsvTransaction testGenericCsvTransaction1 = new GenericCsvTransaction.Builder(transactionDate, TransactionType.MASTERCARD, 4.55)
        .transactionRef1("TAPAS SI NG 27DEC")
        .transactionRef2("5132-4172-5981-4346")
        .build();
    MasterCard masterCard1 = MasterCard.from(testGenericCsvTransaction1);
    SoftAssertions softAssertions = new SoftAssertions();
    softAssertions.assertThat(masterCard1.equals(testGenericCsvTransaction1)).isFalse();
    softAssertions.assertThat(testGenericCsvTransaction1.equals(masterCard1)).isFalse();
    softAssertions.assertAll();
  }

  @Test
  void equals_symmetryParentClassTest() throws MonetaryAmountException {
    LocalDate transactionDate = LocalDate.now();
    GenericCsvTransaction testGenericCsvTransaction1 = new GenericCsvTransaction.Builder(transactionDate, TransactionType.MASTERCARD, 4.55)
        .transactionRef1("TAPAS SI NG 20DEC")
        .transactionRef2("5132-4172-5981-4346")
        .build();
    MasterCard masterCard1 = MasterCard.from(testGenericCsvTransaction1);
    AbstractCsvTransaction abstractCsvTransaction1 = masterCard1;
    SoftAssertions softAssertions = new SoftAssertions();
    softAssertions.assertThat(masterCard1.equals(abstractCsvTransaction1)).isTrue();
    softAssertions.assertThat(abstractCsvTransaction1.equals(masterCard1)).isTrue();
    softAssertions.assertAll();
  }

  @Test
  void hashCode_same() throws MonetaryAmountException {
    LocalDate transactionDate = LocalDate.of(2019, 12, 27);
    GenericCsvTransaction testGenericCsvTransaction = new GenericCsvTransaction.Builder(transactionDate, TransactionType.MASTERCARD, 4.55)
        .transactionRef1("TAPAS SI NG 20DEC")
        .transactionRef2("5132-4172-5981-4347")
        .build();
    MasterCard masterCard = MasterCard.from(testGenericCsvTransaction);
    assertThat(masterCard.hashCode())
        .isNotZero()
        .isEqualByComparingTo(Objects.hashCode(masterCard));
  }
}