package expense_tally.csv_parser.model;

import expense_tally.csv_parser.exception.InvalidReferenceDateException;
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
 * @see CsvTransaction
 */
public class MasterCard extends CsvTransaction {
    private static final Logger LOGGER = LogManager.getLogger(MasterCard.class);
    private static final String RAW_DATE_FORMAT = "yyyyddMMM";
    private static final char SPACE_CHARACTER = ' ';
    private static final DateTimeFormatter REFERENCE_1_DATE_FORMAT = DateTimeFormatter.ofPattern("ddMMM");
    private static final String INVALID_REFERENCE_DATE_EXCEPTION_ERR_MSG = "Referenced date is not well formatted.";
    //TODO: Change to custom format
    private String cardNumber;

    /**
     * Sets the card number of this MasterCard© card
     *
     * @param cardNumber card number of this MasterCard© card
     */
    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    /**
     * An empty constructor on the mastercard. The default cardNumber is a null String.
     */
    public MasterCard() {
        super();
    }

    public MasterCard(CsvTransaction csvTransaction) {
        this.setReference(csvTransaction.getReference());
        this.setTransactionDate(csvTransaction.getTransactionDate());
        this.setDebitAmount(csvTransaction.getDebitAmount());
        this.setCreditAmount(csvTransaction.getCreditAmount());
        this.setTransactionRef1(csvTransaction.getTransactionRef1());
        this.setTransactionRef2(csvTransaction.getTransactionRef2());
        this.setTransactionRef3(csvTransaction.getTransactionRef3());
        this.setType(TransactionType.MASTERCARD);
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
    public static LocalDate extractTransactionDate(final LocalDate bankTransactionDate, final String reference1)
        throws InvalidReferenceDateException {
        int yearOfTransaction = bankTransactionDate.getYear();
        if (reference1.isBlank()) {
            LOGGER.warn("reference1 is empty");
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
        // Since we know that the date is stored in ddMMM format, we will extract it.
        if (date == null || date.length() != 5) {
            throw new InvalidReferenceDateException(INVALID_REFERENCE_DATE_EXCEPTION_ERR_MSG);
        }
        String titleCaseDate = convertDateToTitleCase(date);
        return MonthDay.parse(titleCaseDate, REFERENCE_1_DATE_FORMAT);
    }

    /**
     * Returns a formatted date which the month is titled case
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

    @Override
    public String toString() {
        return new StringJoiner(", ", MasterCard.class.getSimpleName() + "[", "]")
                .add(super.toString())
                .add("cardNumber=" + cardNumber)
                .toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MasterCard)) return false;
        if (!super.equals(o)) return false;
        MasterCard that = (MasterCard) o;
        return cardNumber.equals(that.cardNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), cardNumber);
    }
}
