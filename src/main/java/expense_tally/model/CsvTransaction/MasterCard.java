package expense_tally.model.CsvTransaction;

import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.StringJoiner;

/**
 * Models a specialised type of transaction that is based on MasterCard©. MasterCard© is a type of payment card which
 * is processed between the merchants and the card issuing banks. The payment can be of debit, credit or prepaid in
 * nature.
 *
 * @see expense_tally.model.CsvTransaction.CsvTransaction
 */
public class MasterCard extends CsvTransaction {
    //TODO: Change to custom format
    private String cardNumber;

    /**
     * Returns the card number of this MasterCard© card
     * @return the card number of this MasterCard© card
     */
    public String getCardNumber() {
        return cardNumber;
    }

    /**
     * Sets the card number of this MasterCard© card
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

    /**
     * Returns the transaction date
     * <p>A MasterCard© transaction date is not the transaction date given by the CSV file. The CSV file records the
     * value date of the transaction in <i>bankTransactionDate</i>, while the actual transaction date is stored in
     * reference line 1 <i>reference1</i> of the transaction.</p>
     * @param bankTransactionDate value date of the transaction
     * @param reference1 reference line 1
     * @return the transaction date
     */
    public static LocalDate extractTransactionDate(LocalDate bankTransactionDate, String reference1) {
        final String RAW_DATE_FORMAT = "yyyyddMMM";
        int yearOfTransaction = bankTransactionDate.getYear();
        String monthOfTransactionString = convertToTitleCase(extractLastWord(reference1).substring(2, 5));
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
     * @param string string containing the last word
     * @return the last word of <i>string</i>
     */
    private static String extractLastWord(String string) {
        return string.substring(string.lastIndexOf(' ') + 1);
    }

    /**
     * Return a converted title case version of <i>string</i>
     * @param string string to be converted
     * @return converted title case of <i>string</i>
     */
    //TODO: Look for a pre-defined library of this functionality so that there is no need to maintain this
    // functionality.
    private static String convertToTitleCase(String string) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(string.substring(0, 1).toUpperCase());
        stringBuilder.append(string.substring(1).toLowerCase());
        return stringBuilder.toString();
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", MasterCard.class.getSimpleName() + "[", "]")
                .add(super.toString())
                .toString();
    }
}
