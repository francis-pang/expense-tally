package expense_tally.csv_parser;

import expense_tally.csv_parser.model.CsvTransaction;
import expense_tally.csv_parser.model.MasterCard;
import expense_tally.csv_parser.model.TransactionType;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Parses a CSV file of bank transaction.
 *
 * <p>Each record in the CSV file is a CsvTransaction. The format of the CSV file is pre-defined in a fixed sequence as below:</p>
 *
 * <ol>
 * <li>Transaction Date</li>
 * <li>TransactionType</li>
 * <li>Debit Amount</li>
 * <li>Credit Amount</li>
 * <li>Transaction Ref1</li>
 * <li>Transaction Ref2</li>
 * <li>Transaction Ref3</li>
 * </ol>
 *
 * <p>Note that Transaction Ref1/2/3 are optional field. Empty field will be set to empty String.</p>
 *
 * @see CsvTransaction
 */
public class CsvParser implements CsvParsable {
    private static final Logger LOGGER = LogManager.getLogger(CsvParser.class);
    private final String CSV_HEADER_LINE = "Transaction Date";

    /**
     * Parse a CSV file in the pre-defined format from the file with the directory <i>filePath</i>.
     *
     * @param filePath file path of the CSV file, regardless relative or absolute path
     * @return a list of CsvTransaction read from the CSV file
     * @throws IOException when there is Input/Output error
     */
    // TODO: Refactor to read from a buffer stream so that there isn't a need to unit test the part of reading from a file
    @Override
    public List<CsvTransaction> parseCsvFile(String filePath) throws IOException {
        List<CsvTransaction> csvTransactionList = new ArrayList<>();


        try (BufferedReader csvBufferedReader = new BufferedReader(new FileReader(filePath))) {
            String line;
            // Ignore until start with Transaction Date
            do {
                line = csvBufferedReader.readLine();
            } while (line != null && !line.startsWith(CSV_HEADER_LINE));

            // Skip empty lines, then use getter to retrieve information
            line = csvBufferedReader.readLine();
            while (line != null) {
                if (line.length() == 0) {
                    line = csvBufferedReader.readLine();
                    continue;
                }
                CsvTransaction csvTransaction = parseSingleTransaction(line);
                if (csvTransaction != null) {
                    csvTransactionList.add(parseSingleTransaction(line));
                }

                line = csvBufferedReader.readLine();
            }
        } catch (IOException ex) {
            LOGGER.error("Cannot read from CSV input file {}", filePath);
            throw LOGGER.throwing(Level.ERROR, ex);
        }
        return csvTransactionList;
    }

    /**
     * Parse and process a line in the CSV file.
     *
     * @param csvLine a single line of csv with proper line ending, delimited by the comma character
     * @return a CsvTransaction based on the line of csv. The sequence (position of each of the elements) of the csv
     * file is fixed. If it is a AWL/ INT/ SI/ ITR/ BILL type of transaction, null will be returned.
     */
    private CsvTransaction parseSingleTransaction(String csvLine) {
        final String CSV_TRANSACTION_DATE_FORMAT = "dd MMM yyyy"; //09 Nov 2018
        final String CSV_DELIMITER = ",";
        // All these are zero based position
        final int TRANSACTION_DATE_POSITION = 0;
        final int REFERENCE_POSITION = 1;
        final int DEBIT_AMOUNT_POSITION = 2;
        final int CREDIT_AMOUNT_POSITION = 3;
        final int TRANSACTION_REF_1_POSITION = 4;
        final int TRANSACTION_REF_2_POSITION = 5;
        final int TRANSACTION_REF_3_POSITION = 6;

        DateTimeFormatter csvTransactionDateFormatter = DateTimeFormatter.ofPattern(CSV_TRANSACTION_DATE_FORMAT);
        String[] csvElements = csvLine.split(CSV_DELIMITER);
        CsvTransaction csvTransaction = new CsvTransaction();
        csvTransaction.setReference(csvElements[REFERENCE_POSITION]);
        csvTransaction.setTransactionDate(LocalDate.parse(csvElements[TRANSACTION_DATE_POSITION], csvTransactionDateFormatter));
        csvTransaction.setDebitAmount((csvElements[DEBIT_AMOUNT_POSITION].isBlank())
                ? 0.00
                : Double.parseDouble(csvElements[DEBIT_AMOUNT_POSITION]));
        csvTransaction.setCreditAmount((csvElements[CREDIT_AMOUNT_POSITION].isBlank())
                ? 0.00
                : Double.parseDouble(csvElements[CREDIT_AMOUNT_POSITION]));
        if (csvElements.length >= 5) {
            csvTransaction.setTransactionRef1(csvElements[TRANSACTION_REF_1_POSITION]);
        } else {
            csvTransaction.setTransactionRef1("");
        }
        if (csvElements.length >= 6) {
            csvTransaction.setTransactionRef2(csvElements[TRANSACTION_REF_2_POSITION]);
        } else {
            csvTransaction.setTransactionRef2("");
        }
        if (csvElements.length >= 7) {
            csvTransaction.setTransactionRef3(csvElements[TRANSACTION_REF_3_POSITION]);
        } else {
            csvTransaction.setTransactionRef3("");
        }
        csvTransaction.setType(TransactionType.resolve(csvTransaction.getReference()));
        if (csvTransaction.getType() == null) {
            LOGGER.info("Found a new transaction type: {}; {}", csvTransaction.getReference(), csvLine);
            return csvTransaction;
        }
        switch (csvTransaction.getType()) {
            case MASTERCARD:
                csvTransaction = new MasterCard(csvTransaction);
                LOGGER.debug("Detect a PaymentCard transaction: {}", csvTransaction);
                if (!csvTransaction.getTransactionRef2().isBlank()) {
                    ((MasterCard) csvTransaction).setCardNumber(csvElements[TRANSACTION_REF_2_POSITION]);
                } else {
                    LOGGER.trace("This MasterCard transaction doesn't record the card number. {}",
                        csvTransaction);
                }
                //FIXME: Error handling when there is no transaction date to be parsed
                csvTransaction.setTransactionDate(
                        MasterCard.extractTransactionDate(
                                csvTransaction.getTransactionDate(),
                                csvTransaction.getTransactionRef1()));
                break;
            case FAST_PAYMENT:
                if (TransactionType.PAY_NOW.value().equals(csvTransaction.getTransactionRef1())) {
                    csvTransaction.setType(TransactionType.PAY_NOW);
                }
                break;
            case CASH_WITHDRAWAL:
            case INTEREST_EARNED:
            case STANDING_INSTRUCTION:
            case SALARY:
            case MAS_ELECTRONIC_PAYMENT_SYSTEM_RECEIPT:
                /*
                 * For this type of transaction, do not store them because they do not contribute to the reconciliation
                 * process
                 */
                return null; //TODO: Find a way to better elegantly handle this
            default:
                // Do nothing
        }
        return csvTransaction;
    }
}
