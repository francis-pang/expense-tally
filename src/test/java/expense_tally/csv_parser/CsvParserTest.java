package expense_tally.csv_parser;

import expense_tally.csv_parser.exception.MonetaryAmountException;
import expense_tally.csv_parser.model.CsvTransaction;
import expense_tally.csv_parser.model.MasterCard;
import expense_tally.csv_parser.model.TransactionType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CsvParserTest {
  //TODO: This 2 constant tag should be in its own enum class, but there is an error wit the error message"Attribute
  // value must be constant" prohibit me from doing so. Putting a task here to look at it later as a clean up.
  private static final String NEGATIVE_TAG = "Negative";
  private static final String POSITIVE_TAG = "Positive";

  private static CsvParser csvParser;
  private File csvFile;
  private static Path currentRelativePath;
  private FileWriter csvFileWriter;

  @BeforeAll
  static void setUpOnce() {
    csvParser = new CsvParser();
    currentRelativePath = Paths.get("");
  }

  @BeforeEach
  void setUp() throws IOException {
    // Create the test file
    csvFile = new File(currentRelativePath.toString() + "test.csv");
    csvFileWriter = new FileWriter(csvFile);
  }

  @AfterEach
  void tearDown() {
    //FIXME: Why is this csvFile not deleted?
    if (!csvFile.delete()) {
      System.out.println("The testing CSV file is not deleted");
    }
  }

  /**
   * Input a csv file with header and 1 row of data. All the rows in the data are fully filled
   * Expects that all the data in the file is filled
   */
  @Test
  @Tag(value = POSITIVE_TAG)
  void parseCsvFile_parseOneRowDataFullyFilled() throws IOException, MonetaryAmountException {
    csvFileWriter.write("Transaction Date,TransactionType,Debit Amount,Credit Amount,Transaction Ref1,Transaction Ref2,Transaction Ref3\n");
    csvFileWriter.write("09 Nov 2018,ICT, 148.88, 0,PayNow Transfer,To: YUEN HUI SHAN  VIVIEN,OTHR eAngBao for Vivien.,\n");
    csvFileWriter.close();

    CsvTransaction expectedCsvTransaction = new CsvTransaction.Builder(
        LocalDate.of(2018, 11, 9),
        TransactionType.PAY_NOW,
        148.88)
        .creditAmount(0)
        .transactionRef1("PayNow Transfer")
        .transactionRef2("To: YUEN HUI SHAN  VIVIEN")
        .transactionRef3("OTHR eAngBao for Vivien.")
        .build();

    assertThat(csvParser.parseCsvFile(csvFile.getAbsolutePath()))
        .isNotNull()
        .hasSize(1)
        .usingRecursiveFieldByFieldElementComparator()
        .containsExactlyInAnyOrder(expectedCsvTransaction);
  }

  @Test
  @Tag(POSITIVE_TAG)
  void parseCsvFile_parseHeaderWithNoRow() throws IOException {
    csvFileWriter.write("Transaction Date,TransactionType,Debit Amount,Credit Amount,Transaction Ref1,Transaction Ref2,Transaction Ref3\n");
    csvFileWriter.close();
    assertThat(csvParser.parseCsvFile(csvFile.getAbsolutePath()))
        .isNotNull()
        .hasSize(0);
  }

  @Test
  @Tag(POSITIVE_TAG)
  void parseCsvFile_parseOneRowDataNoDebit() throws IOException, MonetaryAmountException {
    csvFileWriter.write("Transaction Date,TransactionType,Debit Amount,Credit Amount,Transaction Ref1,Transaction Ref2,Transaction Ref3\n");
    csvFileWriter.write("09 Nov 2018,ICT, , 148.88,PayNow Transfer,To: YUEN HUI SHAN  VIVIEN,OTHR eAngBao for Vivien.,\n");
    csvFileWriter.close();

    CsvTransaction expectedCsvTransaction = new CsvTransaction.Builder(
        LocalDate.of(2018, 11, 9),
        TransactionType.PAY_NOW,
        0.00)
        .creditAmount(148.88)
        .transactionRef1("PayNow Transfer")
        .transactionRef2("To: YUEN HUI SHAN  VIVIEN")
        .transactionRef3("OTHR eAngBao for Vivien.")
        .build();

    assertThat(csvParser.parseCsvFile(csvFile.getAbsolutePath()))
        .isNotNull()
        .hasSize(1)
        .usingRecursiveFieldByFieldElementComparator()
        .containsExactlyInAnyOrder(expectedCsvTransaction);
  }

  /*
   * This test case also prove that a transaction record without credit amount will work alright
   */
  @Test
  @Tag(NEGATIVE_TAG)
  void parseCsvFile_oneRowDataNoTransactionRef() throws IOException, MonetaryAmountException {
    csvFileWriter.write("Transaction Date,TransactionType,Debit Amount,Credit Amount,Transaction Ref1,Transaction Ref2,Transaction Ref3\n");
    csvFileWriter.write("09 Nov 2018,ICT, 148.88,,,,\n");
    csvFileWriter.close();

    CsvTransaction expectedCsvTransaction = new CsvTransaction.Builder(
        LocalDate.of(2018, 11, 9),
        TransactionType.FAST_PAYMENT,
        148.88)
        .creditAmount(0.00)
        .transactionRef1("")
        .transactionRef2("")
        .transactionRef3("")
        .build();

    assertThat(csvParser.parseCsvFile(csvFile.getAbsolutePath()))
        .isNotNull()
        .hasSize(1)
        .usingRecursiveFieldByFieldElementComparator()
        .containsExactlyInAnyOrder(expectedCsvTransaction);
  }

  @Test
  @Tag(NEGATIVE_TAG)
  void parseCsvFile_oneRowDataNoHeader() throws IOException {
    csvFileWriter.write("09 Nov 2018,ICT, 148.88, ,,,\n");
    csvFileWriter.close();
    assertThat(csvParser.parseCsvFile(csvFile.getAbsolutePath()))
        .isNotNull()
        .hasSize(0);
  }

  /*
   * The method will read a CSV file with one line of transaction only. That transaction is a AWL transaction. As
   * per the code, a number will be return, and then we will get a 0 size list back.
   */
  @Test
  @Tag(POSITIVE_TAG)
  void parseCsvFile_oneAwlTransaction() throws IOException {
    csvFileWriter.write("Transaction Date,TransactionType,Debit Amount,Credit Amount,Transaction Ref1,Transaction Ref2,Transaction Ref3\n");
    csvFileWriter.write("25 Oct 2018,AWL, 20.00, ,00141067,DTL ROCHOR,,,\n");
    csvFileWriter.close();
    assertThat(csvParser.parseCsvFile(csvFile.getAbsolutePath()))
        .isNotNull()
        .hasSize(0);
  }

  /*
   * Read a record that is of unknown transaction type, and there is empty line in the file so this need to be read and
   * skipped.
   */
  @Test
  @Tag(POSITIVE_TAG)
  void parseCsvFile_unknownTransaction() throws IOException {
    csvFileWriter.write("Transaction Date,TransactionType,Debit Amount,Credit Amount,Transaction Ref1,Transaction Ref2,Transaction Ref3\n");
    csvFileWriter.write("\n");
    csvFileWriter.write("08 Oct 2018,QCDM, 20000.00, ,,,,\n");
    csvFileWriter.close();
    assertThat(csvParser.parseCsvFile(csvFile.getAbsolutePath()))
        .isNotNull()
        .hasSize(0);
  }

  @Test
  @Tag(POSITIVE_TAG)
  void parseCsvFile_masterCardTransaction() throws IOException, MonetaryAmountException {
    csvFileWriter.write("Transaction Date,TransactionType,Debit Amount,Credit Amount,Transaction Ref1,Transaction Ref2,Transaction Ref3\n");
    csvFileWriter.write("\n");
    csvFileWriter.write("19 Oct 2018,MST, 9.42, ,BUS/MRT 2431992        SI NG 10OCT,5548-2741-0014-1067,,\n");
    csvFileWriter.close();

    CsvTransaction expectedCsvTransaction = new CsvTransaction.Builder(
        LocalDate.of(2018, 10, 10),
        TransactionType.MASTERCARD,
        9.42)
        .creditAmount(0.00)
        .transactionRef1("BUS/MRT 2431992        SI NG 10OCT")
        .transactionRef2("5548-2741-0014-1067")
        .transactionRef3("")
        .build();
    MasterCard expectedMaster = MasterCard.from(expectedCsvTransaction);

    assertThat(csvParser.parseCsvFile(csvFile.getAbsolutePath()))
        .isNotNull()
        .hasSize(1)
        .usingRecursiveFieldByFieldElementComparator()
        .containsExactlyInAnyOrder(expectedMaster);
  }

  @Test
  @Tag(POSITIVE_TAG)
  void parseCsvFile_billTransaction() throws IOException, MonetaryAmountException {
    csvFileWriter.write("Transaction Date,TransactionType,Debit Amount,Credit Amount,Transaction Ref1,Transaction Ref2,Transaction Ref3\n");
    csvFileWriter.write("\n");
    csvFileWriter.write("27 Oct 2018,BILL, 182.94, ,UOB -4265884034470665     : I-BANK,,,\n");
    csvFileWriter.close();

    CsvTransaction expectedCsvTransaction = new CsvTransaction.Builder(
        LocalDate.of(2018, 10, 27),
        TransactionType.BILL_PAYMENT,
        182.94)
        .creditAmount(0.00)
        .transactionRef1("UOB -4265884034470665     : I-BANK")
        .transactionRef2("")
        .transactionRef3("")
        .build();

    assertThat(csvParser.parseCsvFile(csvFile.getAbsolutePath()))
        .isNotNull()
        .hasSize(1)
        .usingRecursiveFieldByFieldElementComparator()
        .containsExactlyInAnyOrder(expectedCsvTransaction);
  }

  @Test
  @Tag(NEGATIVE_TAG)
  void parseCsvFile_ioException() {
    assertThatThrownBy(() -> csvParser.parseCsvFile("invalidfile.csv"))
        .isInstanceOf(IOException.class);
  }

  /**
   * This test a MasterCard transaction which has empty Transaction Reference 2. This is a edge case, because
   * normally the MasterCard card number is inside that field
   */
  @Test
  @Tag(POSITIVE_TAG)
  void parseCsvFile_masterCardEmptyTransactionRef2() throws IOException, MonetaryAmountException {
    csvFileWriter.write("Transaction Date,TransactionType,Debit Amount,Credit Amount,Transaction Ref1,Transaction Ref2,Transaction Ref3\n");
    csvFileWriter.write("\n");
    csvFileWriter.write("04 Oct 2018,MST, 10.16,,test ref 1 SI NG 10OCT,,test ref 3\n");
    csvFileWriter.close();

    CsvTransaction expectedCsvTransaction = new CsvTransaction.Builder(
        LocalDate.of(2018, 10, 4),
        TransactionType.MASTERCARD,
        10.16)
        .creditAmount(0.00)
        .transactionRef1("test ref 1 SI NG 10OCT")
        .transactionRef2("")
        .transactionRef3("test ref 3")
        .build();

    MasterCard expectedMaster = MasterCard.from(expectedCsvTransaction);

    assertThat(csvParser.parseCsvFile(csvFile.getAbsolutePath()))
        .isNotNull()
        .hasSize(1)
        .usingRecursiveFieldByFieldElementComparator()
        .containsExactlyInAnyOrder(expectedMaster);
  }
}