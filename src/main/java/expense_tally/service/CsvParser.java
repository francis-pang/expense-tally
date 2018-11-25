package expense_tally.service;

import expense_tally.model.CsvTransaction;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class CsvParser {
    public CsvParser() {
    }

    public List<CsvTransaction> parseCsvFile(String filename) throws IOException {
        final String CSV_HEADER_LINE = "Transaction Date";
        List<CsvTransaction> csvTransactionList = new ArrayList<>();

        // Ignore until start with Transaction Date
        BufferedReader csvBufferedReader = new BufferedReader(new FileReader(filename));
        String line;
        do {
            line = csvBufferedReader.readLine();

        } while (!line.startsWith(CSV_HEADER_LINE));

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

    private CsvTransaction parseSingleTransaction(String csvLine){
        final String CSV_TRANSACTION_DATE_FORMAT = "dd MMM yyyy";

        //09 Nov 2018
        DateTimeFormatter csvTransactionDateFormatter = DateTimeFormatter.ofPattern(CSV_TRANSACTION_DATE_FORMAT);

        String[] csvElements = csvLine.split(",");
        CsvTransaction csvTransaction = new CsvTransaction();
        csvTransaction.setTransactionDate(LocalDate.parse(csvElements[0], csvTransactionDateFormatter));
        csvTransaction.setReference(csvElements[1]);
        csvTransaction.setDebitAmount((csvElements[2].isBlank()) ? 0.00 : Double.parseDouble(csvElements[2]));
        csvTransaction.setCreditAmount((csvElements[3].isBlank()) ? 0.00 : Double.parseDouble(csvElements[3]));
        if (csvElements.length >= 5) {
            csvTransaction.setTransactionRef1(csvElements[4]);
        } else {
            csvTransaction.setTransactionRef1("");
        }
        if (csvElements.length >= 6) {
            csvTransaction.setTransactionRef2(csvElements[5]);
        } else {
            csvTransaction.setTransactionRef2("");
        }
        if (csvElements.length >= 7) {
            csvTransaction.setTransactionRef3(csvElements[6]);
        } else {
            csvTransaction.setTransactionRef3("");
        }
        return csvTransaction;
    }
}
