package expense_tally.csv.parser;

import expense_tally.model.csv.AbstractCsvTransaction;
import expense_tally.model.csv.GenericCsvTransaction;
import expense_tally.model.csv.MasterCard;
import expense_tally.model.csv.MonetaryAmountException;
import expense_tally.model.csv.TransactionType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import static expense_tally.csv.parser.CsvPosition.CREDIT_AMOUNT;
import static expense_tally.csv.parser.CsvPosition.DEBIT_AMOUNT;
import static expense_tally.csv.parser.CsvPosition.REFERENCE;
import static expense_tally.csv.parser.CsvPosition.TRANSACTION_DATE;
import static expense_tally.csv.parser.CsvPosition.TRANSACTION_REF_1;
import static expense_tally.csv.parser.CsvPosition.TRANSACTION_REF_2;
import static expense_tally.csv.parser.CsvPosition.TRANSACTION_REF_3;
import static expense_tally.model.csv.TransactionType.PAY_NOW;
import static expense_tally.model.csv.TransactionType.resolve;

/**
 * Parses a CSV file of bank transaction.
 *
 * <p>Each record in the CSV file is a GenericCsvTransaction. The format of the CSV file is pre-defined in a fixed sequence as below:</p>
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
 * @see GenericCsvTransaction
 */
public class CsvParser {
  private static final Logger LOGGER = LogManager.getLogger(CsvParser.class);
  private static final String CSV_HEADER_LINE = "Transaction Date";
  private static final String CSV_TRANSACTION_DATE_FORMAT = "dd MMM yyyy"; //09 Nov 2018
  private static final DateTimeFormatter CSV_TRANSACTION_DATE_FORMATTER = DateTimeFormatter.ofPattern(
      CSV_TRANSACTION_DATE_FORMAT);
  private static final String CSV_DELIMITER = ",";
  private static final double DEFAULT_AMOUNT = 0.00;

  private CsvParser() {
  }

  // TODO: Refactor to read from a buffer stream so that there isn't a need to unit test the part of reading from a file
  /**
   * Read the content of a CSV file in the pre-defined format from the file with the directory <i>filePath</i>.
   * <p>Each line inside the comma separated value (csv) file is a single transaction record</p>
   * @param filePath location of the csv file. file path can be relative or absolutely path.
   * @return list of transaction extracted from the csv file
   * @throws IOException when there is issue reading the filepath
   */
  public static List<AbstractCsvTransaction> parseCsvFile(String filePath) throws IOException {
    try (BufferedReader csvBufferedReader = new BufferedReader(new FileReader(filePath))) {
      return parseCsvTransactionFromBufferedReader(csvBufferedReader);
    } catch (IOException ex) {
      LOGGER.atError().withThrowable(ex).log("Cannot read from CSV input file {}", filePath);
      throw ex;
    }
  }

  private static List<AbstractCsvTransaction> parseCsvTransactionFromBufferedReader(BufferedReader csvBufferedReader)
      throws IOException {
    List<AbstractCsvTransaction> abstractCsvTransactions = new ArrayList<>();
    skipUntilHeaderLine(csvBufferedReader);
    csvBufferedReader
      .lines()
      .filter(line -> !line.isBlank())
      .forEach(line -> parseSingleTransaction(line).ifPresent(abstractCsvTransactions::add));
    return abstractCsvTransactions;
  }

  private static Optional<AbstractCsvTransaction> parseSingleTransaction(String line) {
    Optional<GenericCsvTransaction> optionalGenericCsvTransaction = Optional.empty();
    try {
      optionalGenericCsvTransaction = transformToGenericCsvTransaction(line);
    } catch (MonetaryAmountException e) {
      LOGGER.atError().withThrowable(e).log("Unable to parse transaction. line={}", line);
    }
    return optionalGenericCsvTransaction.map(CsvParser::modifyBaseOnTransactionType);
  }

  private static void skipUntilHeaderLine(BufferedReader bufferedReader) throws IOException {
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
   * @return a GenericCsvTransaction based on the line of csv. The sequence (position of each of the elements) of the csv
   * file is fixed. If it is of a transaction not meant for processing, null will be returned.
   * @throws MonetaryAmountException if both the debit and credit amount isn't fill up as non-zero value
   */
  private static Optional<GenericCsvTransaction> transformToGenericCsvTransaction(String csvLine) 
    throws MonetaryAmountException {
    String[] csvElements = csvLine.split(CSV_DELIMITER);
    String reference = csvElements[REFERENCE.position];
    TransactionType transactionType = resolve(reference);
    boolean isNullTransactionType = transactionType == null;
    if (isNullTransactionType || !transactionType.isMeantToBeProcessed()) {
      if (isNullTransactionType) {
        LOGGER.atInfo().log("Found a new transaction type: {}; csvLine: {}", reference, csvLine);   
      }
      return Optional.empty();
    }
    LocalDate transactionDate = LocalDate.parse(csvElements[TRANSACTION_DATE.position],
        CSV_TRANSACTION_DATE_FORMATTER);
    double debitAmount = parseMonetaryAmount(csvElements[DEBIT_AMOUNT.position]);
    GenericCsvTransaction.Builder builder = new GenericCsvTransaction.Builder(transactionDate, transactionType,
        debitAmount);
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
    GenericCsvTransaction genericCsvTransaction = builder.build();
    return Optional.of(genericCsvTransaction);
  }

  /**
   * Returns the parsed monetary amount in double
   * @param amount monetary expressed in {@code String} form
   * @return the parsed monetary amount in double
   */
  private static double parseMonetaryAmount(String amount) {
    return (amount.isBlank()) ? DEFAULT_AMOUNT : Double.parseDouble(amount);
  }

  private static AbstractCsvTransaction modifyBaseOnTransactionType(GenericCsvTransaction genericCsvTransaction) {
    TransactionType transactionType = genericCsvTransaction.getTransactionType();
    switch (transactionType) {
      case MASTERCARD:
        try {
          return MasterCard.from(genericCsvTransaction);
        } catch (RuntimeException runtimeException) {
          LOGGER.atWarn()
              .withThrowable(runtimeException)
              .log("Unable to convert csv transaction to MasterCard transaction: {}", genericCsvTransaction);
          return genericCsvTransaction;
        }
      case FAST_PAYMENT:
        String ref1 = genericCsvTransaction.getTransactionRef1();
        if (PAY_NOW.value().equals(ref1)) {
          genericCsvTransaction.setTransactionType(PAY_NOW);
        }
        return genericCsvTransaction;
      default:
        return genericCsvTransaction;
    }
  }
}
