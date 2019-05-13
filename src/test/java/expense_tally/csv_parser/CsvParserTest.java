package expense_tally.csv_parser;

import expense_tally.csv_parser.model.CsvTransaction;
import expense_tally.csv_parser.model.MasterCard;
import expense_tally.csv_parser.model.TransactionType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CsvParserTest {
    private static CsvParser csvParser;
    private File csvFile;
    private static Path currentRelativePath;
    private FileWriter csvFileWriter;

    @BeforeAll
    static void setUpOnce() {
        csvParser = CsvParser.getCsvParser();
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

    private CsvTransaction assembleCsvTransaction(LocalDate transactionDate, String reference, Double debitAmount,
            Double creditAmount, String transactionRef1, String transactionRef2, String transactionRef3,
            TransactionType transactionType) {
        CsvTransaction csvTransaction = new CsvTransaction();
        csvTransaction.setTransactionDate(transactionDate);
        csvTransaction.setReference(reference);
        csvTransaction.setDebitAmount(debitAmount);
        csvTransaction.setCreditAmount(creditAmount);
        csvTransaction.setTransactionRef1(transactionRef1);
        csvTransaction.setTransactionRef2(transactionRef2);
        csvTransaction.setTransactionRef3(transactionRef3);
        csvTransaction.setType(transactionType);
        return csvTransaction;
    }

    /**
     * Input a csv file with header and 1 row of data. All the rows in the data are fully filled
     * Expects that all the data in the file is filled
     */
    @Test
    void parseCsvFile_parseOneRowDataFullyFilled() throws IOException {
        csvFileWriter.write("Transaction Date,TransactionType,Debit Amount,Credit Amount,Transaction Ref1,Transaction Ref2,Transaction Ref3\n");
        csvFileWriter.write("09 Nov 2018,ICT, 148.88, 99.99,PayNow Transfer,To: YUEN HUI SHAN  VIVIEN,OTHR eAngBao for Vivien.,\n");
        csvFileWriter.close();

        // Build expected CsvTransaction
        CsvTransaction expectedCsvTransaction = assembleCsvTransaction(
            LocalDate.of(2018, 11, 9),
            "ICT",
            148.88,
            99.99,
            "PayNow Transfer",
            "To: YUEN HUI SHAN  VIVIEN",
            "OTHR eAngBao for Vivien.",
                TransactionType.PAY_NOW);
        List<CsvTransaction> actualCsvTransaction = csvParser.parseCsvFile(csvFile.getAbsolutePath());
        assertEquals(expectedCsvTransaction, actualCsvTransaction.get(0));
    }

    @Test
    void parseCsvFile_parseHeaderWithNoRow() throws IOException {
        csvFileWriter.write("Transaction Date,TransactionType,Debit Amount,Credit Amount,Transaction Ref1,Transaction Ref2,Transaction Ref3\n");
        csvFileWriter.close();
        List<CsvTransaction> actualCsvTransaction = csvParser.parseCsvFile(csvFile.getAbsolutePath());
        assertEquals(0, actualCsvTransaction.size());
    }

    @Test
    void parseCsvFile_parseOneRowDataNoCredit() throws IOException {
        csvFileWriter.write("Transaction Date,TransactionType,Debit Amount,Credit Amount,Transaction Ref1,Transaction Ref2,Transaction Ref3\n");
        csvFileWriter.write("09 Nov 2018,ICT, 148.88, ,PayNow Transfer,To: YUEN HUI SHAN  VIVIEN,OTHR eAngBao for Vivien.,\n");
        csvFileWriter.close();

        // Build expected CsvTransaction
        CsvTransaction expectedCsvTransaction = assembleCsvTransaction(
                LocalDate.of(2018, 11, 9),
                "ICT",
                148.88,
                0.00,
                "PayNow Transfer",
                "To: YUEN HUI SHAN  VIVIEN",
                "OTHR eAngBao for Vivien.",
                TransactionType.PAY_NOW);
        List<CsvTransaction> actualCsvTransaction = csvParser.parseCsvFile(csvFile.getAbsolutePath());
        assertEquals(expectedCsvTransaction, actualCsvTransaction.get(0));
    }

    @Test
    void parseCsvFile_parseOneRowDataNoDebit() throws IOException {
        csvFileWriter.write("Transaction Date,TransactionType,Debit Amount,Credit Amount,Transaction Ref1,Transaction Ref2,Transaction Ref3\n");
        csvFileWriter.write("09 Nov 2018,ICT, , 148.88,PayNow Transfer,To: YUEN HUI SHAN  VIVIEN,OTHR eAngBao for Vivien.,\n");
        csvFileWriter.close();

        // Build expected CsvTransaction
        CsvTransaction expectedCsvTransaction = assembleCsvTransaction(
                LocalDate.of(2018, 11, 9),
                "ICT",
                0.00,
                148.88,
                "PayNow Transfer",
                "To: YUEN HUI SHAN  VIVIEN",
                "OTHR eAngBao for Vivien.",
                TransactionType.PAY_NOW);
        List<CsvTransaction> actualCsvTransaction = csvParser.parseCsvFile(csvFile.getAbsolutePath());
        assertEquals(expectedCsvTransaction, actualCsvTransaction.get(0));
    }

    @Test
    void parseCsvFile_parseOneRowDataNoTransactionRef() throws IOException {
        csvFileWriter.write("Transaction Date,TransactionType,Debit Amount,Credit Amount,Transaction Ref1,Transaction Ref2,Transaction Ref3\n");
        csvFileWriter.write("09 Nov 2018,ICT, 148.88, ,,,\n");
        csvFileWriter.close();

        // Build expected CsvTransaction
        CsvTransaction expectedCsvTransaction = assembleCsvTransaction(
                LocalDate.of(2018, 11, 9),
                "ICT",
                148.88,
                0.00,
                "",
                "",
                "",
                TransactionType.FAST_PAYMENT);
        List<CsvTransaction> actualCsvTransaction = csvParser.parseCsvFile(csvFile.getAbsolutePath());
        assertEquals(expectedCsvTransaction, actualCsvTransaction.get(0));
    }

    @Test
    void parseCsvFile_parseOneRowDataNoHeader() throws IOException {
        csvFileWriter.write("09 Nov 2018,ICT, 148.88, ,,,\n");
        csvFileWriter.close();

        List<CsvTransaction> actualCsvTransaction = csvParser.parseCsvFile(csvFile.getAbsolutePath());
        assertEquals(0, actualCsvTransaction.size());
    }

    @Test
    void parseCsvFile_parseOneRowDataWithEmptyLines() throws IOException {
        csvFileWriter.write("Transaction Date,TransactionType,Debit Amount,Credit Amount,Transaction Ref1,Transaction Ref2,Transaction Ref3\n");
        csvFileWriter.write("\n");
        csvFileWriter.write("09 Nov 2018,ICT, 148.88, ,PayNow Transfer,To: YUEN HUI SHAN  VIVIEN,OTHR eAngBao for Vivien.,\n");
        csvFileWriter.close();

        // Build expected CsvTransaction
        CsvTransaction expectedCsvTransaction = assembleCsvTransaction(
                LocalDate.of(2018, 11, 9),
                "ICT",
                148.88,
                0.00,
                "PayNow Transfer",
                "To: YUEN HUI SHAN  VIVIEN",
                "OTHR eAngBao for Vivien.",
                TransactionType.PAY_NOW);
        List<CsvTransaction> actualCsvTransaction = csvParser.parseCsvFile(csvFile.getAbsolutePath());
        assertEquals(expectedCsvTransaction, actualCsvTransaction.get(0));
    }

    /**
     * The method will read a CSV file with one line of transaction only. That transaction is a AWL transaction. As
     * per the code, a number will be return, and then we will get a 0 size list back.
     */
    @Test
    void parseCsvFile_ReadAAwlTransaction() throws IOException {
        csvFileWriter.write("Transaction Date,TransactionType,Debit Amount,Credit Amount,Transaction Ref1,Transaction Ref2,Transaction Ref3\n");
        csvFileWriter.write("\n");
        csvFileWriter.write("25 Oct 2018,AWL, 20.00, ,00141067,DTL ROCHOR,,,\n");
        csvFileWriter.close();

        List<CsvTransaction> actualCsvTransaction = csvParser.parseCsvFile(csvFile.getAbsolutePath());
        assertEquals(0, actualCsvTransaction.size());
    }

    @Test
    void parseCsvFile_UnknownTransaction() throws IOException {
        csvFileWriter.write("Transaction Date,TransactionType,Debit Amount,Credit Amount,Transaction Ref1,Transaction Ref2,Transaction Ref3\n");
        csvFileWriter.write("\n");
        csvFileWriter.write("08 Oct 2018,QCDM, 20000.00, ,,,,\n");
        csvFileWriter.close();

        // Build expected CsvTransaction
        CsvTransaction expectedCsvTransaction = assembleCsvTransaction(
                LocalDate.of(2018, 10, 8),
                "QCDM",
                20000.00,
                0.00,
                "",
                "",
                "",
                null);
        List<CsvTransaction> actualCsvTransaction = csvParser.parseCsvFile(csvFile.getAbsolutePath());
        assertEquals(1, actualCsvTransaction.size());
        assertEquals(expectedCsvTransaction, actualCsvTransaction.get(0));
    }

    @Test
    void parseCsvFile_MasterCardTransaction() throws IOException {
        csvFileWriter.write("Transaction Date,TransactionType,Debit Amount,Credit Amount,Transaction Ref1,Transaction Ref2,Transaction Ref3\n");
        csvFileWriter.write("\n");
        csvFileWriter.write("19 Oct 2018,MST, 9.42, ,BUS/MRT 2431992        SI NG 10OCT,5548-2741-0014-1067,,\n");
        csvFileWriter.close();

        // Build expected CsvTransaction
        MasterCard expectedMasterCardTransaction = new MasterCard();
        expectedMasterCardTransaction.setTransactionDate(LocalDate.of(2018, 10, 10));
        expectedMasterCardTransaction.setCardNumber("5548-2741-0014-1067");
        expectedMasterCardTransaction.setType(TransactionType.MASTERCARD);
        expectedMasterCardTransaction.setDebitAmount(9.42);
        expectedMasterCardTransaction.setTransactionRef1("BUS/MRT 2431992        SI NG 10OCT");
        expectedMasterCardTransaction.setTransactionRef2("5548-2741-0014-1067");
        expectedMasterCardTransaction.setTransactionRef3("");
        expectedMasterCardTransaction.setReference(TransactionType.MASTERCARD.value());

        List<CsvTransaction> actualCsvTransaction = csvParser.parseCsvFile(csvFile.getAbsolutePath());
        assertEquals(1, actualCsvTransaction.size());
        assertEquals(expectedMasterCardTransaction, actualCsvTransaction.get(0));
    }

    @Test
    void parseCsvFile_BillTransaction() throws IOException {
        csvFileWriter.write("Transaction Date,TransactionType,Debit Amount,Credit Amount,Transaction Ref1,Transaction Ref2,Transaction Ref3\n");
        csvFileWriter.write("\n");
        csvFileWriter.write("27 Oct 2018,BILL, 182.94, ,UOB -4265884034470665     : I-BANK,,,\n");
        csvFileWriter.close();

        // Build expected CsvTransaction
        CsvTransaction expectedCsvTransaction = assembleCsvTransaction(
                LocalDate.of(2018, 10, 27),
                TransactionType.BILL_PAYMENT.value(),
                182.94,
                0.00,
                "UOB -4265884034470665     : I-BANK",
                "",
                "",
                TransactionType.BILL_PAYMENT);
        List<CsvTransaction> actualCsvTransaction = csvParser.parseCsvFile(csvFile.getAbsolutePath());
        assertEquals(1, actualCsvTransaction.size());
        assertEquals(expectedCsvTransaction, actualCsvTransaction.get(0));
    }

    @Test
    void parseCsvFile_NonPayNowIctTransaction() throws IOException {
        csvFileWriter.write("Transaction Date,TransactionType,Debit Amount,Credit Amount,Transaction Ref1,Transaction Ref2,Transaction Ref3\n");
        csvFileWriter.write("\n");
        csvFileWriter.write("04 Oct 2018,ICT, 10.16, ,CITI:86305727402:I-BANK,Shipping 25814010124440322,FCPM 21148372132,\n");
        csvFileWriter.close();

        // Build expected CsvTransaction
        CsvTransaction expectedCsvTransaction = assembleCsvTransaction(
            LocalDate.of(2018, 10, 04),
            TransactionType.FAST_PAYMENT.value(),
            10.16,
            0.00,
            "CITI:86305727402:I-BANK",
            "Shipping 25814010124440322",
            "FCPM 21148372132",
            TransactionType.FAST_PAYMENT);
        List<CsvTransaction> actualCsvTransaction = csvParser.parseCsvFile(csvFile.getAbsolutePath());
        assertEquals(1, actualCsvTransaction.size());
        assertEquals(expectedCsvTransaction, actualCsvTransaction.get(0));
    }

    @Test
    void parseCsvFile_NoRefIctTransaction() throws IOException {
        csvFileWriter.write("Transaction Date,TransactionType,Debit Amount,Credit Amount,Transaction Ref1,Transaction Ref2,Transaction Ref3\n");
        csvFileWriter.write("\n");
        csvFileWriter.write("04 Oct 2018,ICT, 10.16, ,,,\n");
        csvFileWriter.close();

        // Build expected CsvTransaction
        CsvTransaction expectedCsvTransaction = assembleCsvTransaction(
            LocalDate.of(2018, 10, 04),
            TransactionType.FAST_PAYMENT.value(),
            10.16,
            0.00,
            "",
            "",
            "",
            TransactionType.FAST_PAYMENT);
        List<CsvTransaction> actualCsvTransaction = csvParser.parseCsvFile(csvFile.getAbsolutePath());
        assertEquals(1, actualCsvTransaction.size());
        assertEquals(expectedCsvTransaction, actualCsvTransaction.get(0));
    }
}