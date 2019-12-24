package expense_tally.csv_parser.model;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.LocalDate;
import java.time.Month;
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
    public static LocalDate extractTransactionDate(LocalDate bankTransactionDate, String reference1) {
        final String RAW_DATE_FORMAT = "yyyyddMMM";
        int yearOfTransaction = bankTransactionDate.getYear();
        if (reference1.isBlank()) {
            LOGGER.warn("reference1 is empty");
            return bankTransactionDate;
        }
        String lastWord = extractLastWord(reference1);
        String month = lastWord.substring(2 , 5);
        String monthOfTransactionString = convertToTitleCase(month);
        if ("Dec".equals(monthOfTransactionString) && bankTransactionDate.getMonth() == Month.JANUARY) {
            yearOfTransaction--;
        }
        String transactionDateString = yearOfTransaction + // Year
                extractLastWord(reference1).substring(0, 2) +  //Day
                monthOfTransactionString; //Month
        DateTimeFormatter transactionDateFormatter = DateTimeFormatter.ofPattern(RAW_DATE_FORMAT);
        return LocalDate.parse(transactionDateString, transactionDateFormatter);
    }

    /**
     * Returns the last word of <i>string</i>
     * The last word is defined as the string after the last whitespace.
     *
     * @param string string containing the last word
     * @return the last word of <i>string</i>
     */
    private static String extractLastWord(String string) {
        return string.substring(string.lastIndexOf(' ') + 1);
    }

    /**
     * Return a converted title case version of <i>string</i>
     *
     * @param string string to be converted
     * @return converted title case of <i>string</i>
     */
    //TODO: Look for a pre-defined library of this functionality so that there is no need to maintain this
    // functionality.
    private static String convertToTitleCase(String string) {
        return string.substring(0, 1).toUpperCase() + string.substring(1).toLowerCase();
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
