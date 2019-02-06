package expense_tally.model.CsvTransaction;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.StringJoiner;

public class MasterCard extends CsvTransaction {
    //TODO: Change to custom format
    private String cardNumber;

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public MasterCard() {
        super();
    }

    public static LocalDate extractTransactionDate(LocalDate bankTransactionDate, String reference1) {
        final String RAW_DATE_FORMAT = "yyyyddMMM";
        int yearOfTransaction = bankTransactionDate.getYear();
        String transactionDate = yearOfTransaction + // Year
                extractLastWord(reference1).substring(0, 2) +  //Day
                convertToTitleCase(extractLastWord(reference1).substring(2, 5)); //Month
        DateTimeFormatter transactionDateFormatter = DateTimeFormatter.ofPattern(RAW_DATE_FORMAT);
        return LocalDate.parse(transactionDate, transactionDateFormatter);
    }

    private static String extractLastWord(String string) {
        return string.substring(string.lastIndexOf(' ') + 1);
    }

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
