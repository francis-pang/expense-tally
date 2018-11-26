package expense_tally.service;

import expense_tally.model.CsvTransaction;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Single Responsibility Principle: Every class should only do one thing
 * CsvParser does 1 thing: Parse a CSV file, return the parsed content as a list of CSV Transactions
 */
public class CsvParser {
    public CsvParser() {
    }

    // TODO: Refactor to read from a buffer stream so that there isn't a need to unit test the part of reading from a file
    public List<CsvTransaction> parseCsvFile(String filename) throws IOException {
        final String CSV_HEADER_LINE = "Transaction Date";
        List<CsvTransaction> csvTransactionList = new ArrayList<>();

        // Ignore until start with Transaction Date
        BufferedReader csvBufferedReader = new BufferedReader(new FileReader(filename));
        String line;
        do {
            line = csvBufferedReader.readLine();
        } while (line != null && !line.startsWith(CSV_HEADER_LINE));

        // Skip empty lines, then use getter to retrieve information
        line = csvBufferedReader.readLine();
        int counter = 0;
        while (line != null) {
            if (line.length() == 0) {
                line = csvBufferedReader.readLine();
                continue;
            }
            csvTransactionList.add(parseSingleTransaction(line));
            line = csvBufferedReader.readLine();
        }
        return csvTransactionList;
    }

    /**
     * Parse and process a line in the CSV file.
     * @param csvLine a single line of csv with proper line ending, delimited by the comma character
     * @return a CsvTransaction based on the line of csv. The sequence (position of each of the elements) of the csv
     * file is fixed
     */
    private CsvTransaction parseSingleTransaction(String csvLine){
        final String CSV_TRANSACTION_DATE_FORMAT = "dd MMM yyyy"; //09 Nov 2018
        final String CSV_DELIMITER = ",";
        final int TRANSACTION_DATE_POSITION = 1;
        final int REFERENCE_POSITION = 2;
        final int DEBIT_AMOUNT_POSITION = 3;
        final int CREDIT_AMOUNT_POSITION = 4;
        final int TRANSACTION_REF_1_POSITION = 5;
        final int TRANSACTION_REF_2_POSITION = 6;
        final int TRANSACTION_REF_3_POSITION = 7;

        DateTimeFormatter csvTransactionDateFormatter = DateTimeFormatter.ofPattern(CSV_TRANSACTION_DATE_FORMAT);
        String[] csvElements = csvLine.split(CSV_DELIMITER);
        CsvTransaction csvTransaction = new CsvTransaction();
        csvTransaction.setTransactionDate(LocalDate.parse(csvElements[TRANSACTION_DATE_POSITION - 1], csvTransactionDateFormatter));
        csvTransaction.setReference(csvElements[REFERENCE_POSITION - 1]);
        csvTransaction.setDebitAmount((csvElements[DEBIT_AMOUNT_POSITION - 1].isBlank())
            ? 0.00
            : Double.parseDouble(csvElements[DEBIT_AMOUNT_POSITION - 1]));
        csvTransaction.setCreditAmount((csvElements[CREDIT_AMOUNT_POSITION - 1].isBlank())
            ? 0.00
            : Double.parseDouble(csvElements[CREDIT_AMOUNT_POSITION - 1]));
        if (csvElements.length >= 5) {
            csvTransaction.setTransactionRef1(csvElements[TRANSACTION_REF_1_POSITION - 1]);
        } else {
            csvTransaction.setTransactionRef1("");
        }
        if (csvElements.length >= 6) {
            csvTransaction.setTransactionRef2(csvElements[TRANSACTION_REF_2_POSITION - 1]);
        } else {
            csvTransaction.setTransactionRef2("");
        }
        if (csvElements.length >= 7) {
            csvTransaction.setTransactionRef3(csvElements[TRANSACTION_REF_3_POSITION - 1]);
        } else {
            csvTransaction.setTransactionRef3("");
        }
        return csvTransaction;
    }
}
