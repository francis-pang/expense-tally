package expense_tally.csv_parser;

import expense_tally.csv_parser.exception.MonetaryAmountException;
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

import static expense_tally.csv_parser.model.CsvPosition.*;

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
  private static final String CSV_HEADER_LINE = "Transaction Date";
  private static final String CSV_TRANSACTION_DATE_FORMAT = "dd MMM yyyy"; //09 Nov 2018
  private static final DateTimeFormatter CSV_TRANSACTION_DATE_FORMATTER = DateTimeFormatter.ofPattern(
      CSV_TRANSACTION_DATE_FORMAT);
  private static final String CSV_DELIMITER = ",";
  private static final double DEFAULT_AMOUNT = 0.00;

  /**
   * Parse a CSV file in the pre-defined format from the file with the directory <i>filePath</i>.
   *
   * @param filePath file path of the CSV file, regardless relative or absolute path
   * @return a list of CsvTransaction read from the CSV file
   * @throws IOException when there is Input/Output error
   */
  @Override
  public List<CsvTransaction> parseCsvFile(String filePath) throws IOException {
    try (BufferedReader csvBufferedReader = new BufferedReader(new FileReader(filePath))) {
      return parseCsvTransactionFromBufferedReader(csvBufferedReader);
    } catch (IOException ex) {
      LOGGER.error("Cannot read from CSV input file {}", filePath);
      throw LOGGER.throwing(Level.ERROR, ex);
    }
  }

  private List<CsvTransaction> parseCsvTransactionFromBufferedReader(BufferedReader csvBufferedReader)
      throws IOException {
    List<CsvTransaction> csvTransactions = new ArrayList<>();
    skipUntilHeaderLine(csvBufferedReader);
    String line = csvBufferedReader.readLine();
    while (line != null) { // Read until end of file
      if (!line.isBlank()) {
        parseSingleTransaction(line, csvTransactions);
      }
      line = csvBufferedReader.readLine();
    }
    return csvTransactions;
  }

  private void parseSingleTransaction(String line, List<CsvTransaction> csvTransactions) {
    CsvTransaction csvTransaction = null;
    try {
      csvTransaction = parseSingleTransaction(line);
    } catch (MonetaryAmountException e) {
      LOGGER.error("Unable to parse transaction. line={}", line);
      LOGGER.throwing(e);
    }
    if (csvTransaction == null) {
      return;
    }
    csvTransaction = modifyBaseOnTransactionType(csvTransaction);
    csvTransactions.add(csvTransaction);
  }

  private void skipUntilHeaderLine(BufferedReader bufferedReader) throws IOException {
    // Ignore until start with Transaction Date
    String line;
    do {
      line = bufferedReader.readLine();
    } while (line != null && !line.startsWith(CSV_HEADER_LINE));
  }

  /**
   * Parse and process a line in the CSV file.
   *
   * @param csvLine a single line of csv with proper line ending, delimited by the comma character
   * @return a CsvTransaction based on the line of csv. The sequence (position of each of the elements) of the csv
   * file is fixed. If it is of a transacton not meant for processing, null will be returned.
   */
  private CsvTransaction parseSingleTransaction(String csvLine) throws MonetaryAmountException {
    String[] csvElements = csvLine.split(CSV_DELIMITER);
    String reference = csvElements[REFERENCE.position];
    TransactionType transactionType = TransactionType.resolve(reference);
    if (transactionType == null) {
      LOGGER.info("Found a new transaction type: {}; {}", reference, csvLine);
      return null;
    } else if (!transactionType.isMeantToBeProcessed()) {
      return null;
    }
    LocalDate transactionDate = LocalDate.parse(csvElements[TRANSACTION_DATE.position],
        CSV_TRANSACTION_DATE_FORMATTER);
    double debitAmount = parseMonetaryAmount(csvElements[DEBIT_AMOUNT.position]);
    CsvTransaction.Builder builder = new CsvTransaction.Builder(transactionDate, transactionType, debitAmount);
    if (csvElements.length >= 5) {
      double creditAmount = parseMonetaryAmount(csvElements[CREDIT_AMOUNT.position]);
      builder.creditAmount(creditAmount);
    }
    if (csvElements.length >= 5) {
      builder.transactionRef1(csvElements[TRANSACTION_REF_1.position]);
    }
    if (csvElements.length >= 6) {
      builder.transactionRef2(csvElements[TRANSACTION_REF_2.position]);
    }
    if (csvElements.length >= 7) {
      builder.transactionRef3(csvElements[TRANSACTION_REF_3.position]);
    }
    return builder.build();
  }

  private double parseMonetaryAmount(String amount) {
    return (amount.isBlank()) ? DEFAULT_AMOUNT : Double.parseDouble(amount);
  }

  private CsvTransaction modifyBaseOnTransactionType(CsvTransaction csvTransaction) {
    TransactionType transactionType = csvTransaction.getTransactionType();
    switch (transactionType) {
      case MASTERCARD:
        try {
          return MasterCard.from(csvTransaction);
        } catch (RuntimeException e) {
          LOGGER.warn("Unable to convert csv transaction to MasterCard transaction - csvTransaction={}",
              csvTransaction);
          LOGGER.throwing(Level.WARN, e);
          return csvTransaction;
        }
      case FAST_PAYMENT:
        String ref1 = csvTransaction.getTransactionRef1();
        if (TransactionType.PAY_NOW.value().equals(ref1)) {
          csvTransaction.setTransactionType(TransactionType.PAY_NOW);
        }
        return csvTransaction;
      default:
        return csvTransaction;
    }
  }
}
