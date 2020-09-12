package expense_tally.csv;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.LocalDate;
import java.time.Month;
import java.time.MonthDay;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.StringJoiner;

/**
 * Models a specialised type of transaction that is based on MasterCard©. MasterCard© is a type of payment card which
 * is processed between the merchants and the card issuing banks. The payment can be of debit, credit or prepaid in
 * nature.
 *
 * @see GenericCsvTransaction
 */
public final class MasterCard extends AbstractCsvTransaction {
  private static final Logger LOGGER = LogManager.getLogger(MasterCard.class);
  private static final char SPACE_CHARACTER = ' ';
  private static final DateTimeFormatter REFERENCE_1_DATE_FORMAT = DateTimeFormatter.ofPattern("ddMMM");
  private static final String INVALID_REFERENCE_DATE_EXCEPTION_ERR_MSG = "Referenced date is not well formatted.";
  private static final String INVALID_CARD_NUMBER_ERR_MSG = "MasterCard number is invalid.";
  private static final String NOT_MASTER_CARD_ERR_MSG = "GenericCsvTransaction is not of MasterCard type.";

  private String cardNumber;
  /**
   * An empty constructor on the mastercard. The default cardNumber is a null String.
   */
  private MasterCard() {
  }

  /**
   * Convert a generic csv transaction into a MasterCard transaction
   * @param genericCsvTransaction generic csv transaction
   * @return a converted generic csv transaction
   * @throws IllegalArgumentException if genericCsvTransaction is not of Master card transaction type
   */
  public static MasterCard from(GenericCsvTransaction genericCsvTransaction) {
    TransactionType transactionType = genericCsvTransaction.getTransactionType();
    if (transactionType == null || !transactionType.equals(TransactionType.MASTERCARD)) {
      LOGGER.atWarn().log("This is not a MasterCard transaction: {}", genericCsvTransaction);
      throw new IllegalArgumentException(NOT_MASTER_CARD_ERR_MSG);
    }
    MasterCard masterCard = new MasterCard();
    masterCard.debitAmount = genericCsvTransaction.debitAmount;
    masterCard.creditAmount = genericCsvTransaction.creditAmount;
    String ref1 = genericCsvTransaction.getTransactionRef1();
    masterCard.transactionRef1 = ref1;
    LocalDate transactionDate = genericCsvTransaction.getTransactionDate();
    masterCard.transactionDate = computeTransactionDate(transactionDate, ref1);
    String ref2 = genericCsvTransaction.getTransactionRef2();
    if (!ref2.isBlank()) {
      masterCard.setCardNumber(ref2.trim());
    } else {
      LOGGER.atDebug().log("This MasterCard transaction doesn't record the card number. {}", genericCsvTransaction);
    }
    masterCard.transactionRef3 = genericCsvTransaction.transactionRef3;
    masterCard.transactionType = TransactionType.MASTERCARD;
    return masterCard;
  }

  /**
   * Returns the transaction date
   * <p>A MasterCard© transaction date is not the transaction date given by the CSV file. The CSV file records the
   * value date of the transaction in <i>bankTransactionDate</i>, while the actual transaction date is stored in
   * reference line 1 <i>reference1</i> of the transaction.</p>
   *
   * @param bankTransactionDate value date of the transaction
   * @param reference1          reference line 1
   * @return the transaction date
   */
  private static LocalDate extractTransactionDate(final LocalDate bankTransactionDate, final String reference1)
      throws InvalidReferenceDateException {
    int yearOfTransaction = bankTransactionDate.getYear();
    if (reference1.isBlank()) { //Reference1 will never be null by design
      LOGGER.atWarn().log("reference1 is empty");
      return bankTransactionDate;
    }
    // Inside the CSV file, it is known that the date of transaction (without the year) is recorded at end of the
    // field reference1. The date of transaction is stored in ddMMM format. An example is 20DDEC, which mean 20th
    // December.
    String lastWord = extractLastWord(reference1);
    MonthDay transactionMonthDay = toMonthDayFromPartialDate(lastWord);
    Month transactionMonth = transactionMonthDay.getMonth();
    Month bankTransactionMonth = bankTransactionDate.getMonth();
    if (Month.DECEMBER.equals(transactionMonth) &&
        Month.JANUARY.equals(bankTransactionMonth)) {
      yearOfTransaction--;
    }
    return LocalDate.of(yearOfTransaction, transactionMonthDay.getMonthValue(), transactionMonthDay.getDayOfMonth());
  }

  private static MonthDay toMonthDayFromPartialDate(final String date) throws InvalidReferenceDateException {
    /**
     * There is a null check in the program flow. This is dead code, because there is only 1 caller at the time of
     * writing, and the caller isn't going pass an null date. However, rather than removing the check for 100% code
     * coverage, I choose to leave it here for defensive coding purpose. Refers to discussion on
     * <a href="https://softwareengineering.stackexchange.com/a/373234">stack exchange</a> for more more details.
     */
    if (date == null || date.length() != 5) {
      throw new InvalidReferenceDateException(INVALID_REFERENCE_DATE_EXCEPTION_ERR_MSG);
    }
    String titleCaseDate = convertDateToTitleCase(date);
    return MonthDay.parse(titleCaseDate, REFERENCE_1_DATE_FORMAT);
  }

  /**
   * Returns a formatted date which the month is titled case
   *
   * @param date date in DDMMM formate
   * @return a formatted date which the month is titled case
   */
  private static String convertDateToTitleCase(String date) {
    String titleCaseDate = date.toLowerCase();
    char firstLetterOfMonth = titleCaseDate.charAt(2);
    char firstLetterOfMonthUpperCase = Character.toUpperCase(firstLetterOfMonth);
    // This method works because we know that the first 3 letters abbreviation of each month is distinct among
    // themselves, so we can replace by all occurances of that letter.
    return titleCaseDate.replace(firstLetterOfMonth, firstLetterOfMonthUpperCase);
  }

  /**
   * Returns the last word of <i>string</i>
   * <p>The last word is defined as the string after the last whitespace.</p>
   *
   * @param string string containing the last word
   * @return the last word of <i>string</i>
   */
  private static String extractLastWord(final String string) {
    String trimmedString = string.trim();
    int positionOfLastSpace = trimmedString.lastIndexOf(SPACE_CHARACTER);
    if (positionOfLastSpace == -1) {
      return "";
    }
    return trimmedString.substring(positionOfLastSpace + 1);
  }

  /**
   * Sets the card number of this MasterCard© card
   *
   * @param cardNumber card number of this MasterCard© card
   */
  private void setCardNumber(String cardNumber) {
    if (!PaymentCardValidator.isPaymentCardValid(cardNumber, TransactionType.MASTERCARD)) {
      throw new IllegalArgumentException(INVALID_CARD_NUMBER_ERR_MSG);
    }
    this.cardNumber = cardNumber;
  }

  private static LocalDate computeTransactionDate(LocalDate transactionDate, String transactionRef1) {
    try {
      transactionDate = extractTransactionDate(transactionDate, transactionRef1);
    } catch (InvalidReferenceDateException | RuntimeException e) {
      LOGGER.atWarn()
          .withThrowable(e)
          .log("Cannot retrieve transaction date {0} from MasterCard transaction reference 1 {1}. Setting to bank " +
              "transaction date.", transactionDate, transactionRef1);
    }
    return transactionDate;
  }

  @Override
  public String toString() {
    return new StringJoiner(", ", MasterCard.class.getSimpleName() + "[", "]")
        .add("cardNumber='" + cardNumber + "'")
        .add("transactionDate=" + transactionDate)
        .add("debitAmount=" + debitAmount)
        .add("creditAmount=" + creditAmount)
        .add("transactionRef1='" + transactionRef1 + "'")
        .add("transactionRef2='" + transactionRef2 + "'")
        .add("transactionRef3='" + transactionRef3 + "'")
        .add("transactionType=" + transactionType)
        .toString();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o != null && getClass() == o.getClass()) {
      MasterCard that = (MasterCard) o;
      return cardNumber.equals(that.cardNumber) && super.equals(that);
    } else if (o instanceof AbstractCsvTransaction) {
      return super.equals(o);
    }
    return false;
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), cardNumber);
  }
}
