package expense_tally.service;

import expense_tally.model.CsvTransaction;
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
        csvFile.delete();
    }

    private CsvTransaction assembleCsvTransaction(LocalDate transactionDate, String reference, Double debitAmount,
        Double creditAmount, String transactionRef1, String transactionRef2, String transactionRef3) {
        CsvTransaction csvTransaction = new CsvTransaction();
        csvTransaction.setTransactionDate(transactionDate);
        csvTransaction.setReference(reference);
        csvTransaction.setDebitAmount(debitAmount);
        csvTransaction.setCreditAmount(creditAmount);
        csvTransaction.setTransactionRef1(transactionRef1);
        csvTransaction.setTransactionRef2(transactionRef2);
        csvTransaction.setTransactionRef3(transactionRef3);
        return csvTransaction;
    }

    /**
     * Input a csv file with header and 1 row of data. All the rows in the data are fully filled
     * Expects that all the data in the file is filled
     */
    @Test
    void parseOneRowDataFullyFilled() throws IOException {
        csvFileWriter.write("Transaction Date,Reference,Debit Amount,Credit Amount,Transaction Ref1,Transaction Ref2,Transaction Ref3\n");
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
            "OTHR eAngBao for Vivien.");
        List actualCsvTransaction = csvParser.parseCsvFile(csvFile.getAbsolutePath());
        assertEquals(expectedCsvTransaction, actualCsvTransaction.get(0));
    }

    @Test
    void parseHeaderWithNoRow() throws IOException {
        csvFileWriter.write("Transaction Date,Reference,Debit Amount,Credit Amount,Transaction Ref1,Transaction Ref2,Transaction Ref3\n");
        csvFileWriter.close();
        List actualCsvTransaction = csvParser.parseCsvFile(csvFile.getAbsolutePath());
        assertEquals(0, actualCsvTransaction.size());
    }

    @Test
    void parseOneRowDataNoCredit() throws IOException {
        csvFileWriter.write("Transaction Date,Reference,Debit Amount,Credit Amount,Transaction Ref1,Transaction Ref2,Transaction Ref3\n");
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
                "OTHR eAngBao for Vivien.");
        List actualCsvTransaction = csvParser.parseCsvFile(csvFile.getAbsolutePath());
        assertEquals(expectedCsvTransaction, actualCsvTransaction.get(0));
    }

    @Test
    void parseOneRowDataNoDebit() throws IOException {
        csvFileWriter.write("Transaction Date,Reference,Debit Amount,Credit Amount,Transaction Ref1,Transaction Ref2,Transaction Ref3\n");
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
                "OTHR eAngBao for Vivien.");
        List actualCsvTransaction = csvParser.parseCsvFile(csvFile.getAbsolutePath());
        assertEquals(expectedCsvTransaction, actualCsvTransaction.get(0));
    }

    @Test
    void parseOneRowDataNoTransactionRef() throws IOException {
        csvFileWriter.write("Transaction Date,Reference,Debit Amount,Credit Amount,Transaction Ref1,Transaction Ref2,Transaction Ref3\n");
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
                "");
        List actualCsvTransaction = csvParser.parseCsvFile(csvFile.getAbsolutePath());
        assertEquals(expectedCsvTransaction, actualCsvTransaction.get(0));
    }

    @Test
    void parseOneRowDataNoHeader() throws IOException {
        csvFileWriter.write("09 Nov 2018,ICT, 148.88, ,,,\n");
        csvFileWriter.close();

        List actualCsvTransaction = csvParser.parseCsvFile(csvFile.getAbsolutePath());
        assertEquals(0, actualCsvTransaction.size());
    }

    @Test
    void parseOneRowDataWithEmptyLines() throws IOException {
        csvFileWriter.write("Transaction Date,Reference,Debit Amount,Credit Amount,Transaction Ref1,Transaction Ref2,Transaction Ref3\n");
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
                "OTHR eAngBao for Vivien.");
        List actualCsvTransaction = csvParser.parseCsvFile(csvFile.getAbsolutePath());
        assertEquals(expectedCsvTransaction, actualCsvTransaction.get(0));
    }
}