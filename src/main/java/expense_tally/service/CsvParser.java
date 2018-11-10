package expense_tally.service;

import expense_tally.model.CsvTransaction;

import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class CsvParser {
    public CsvParser() {
    }

    public List<CsvTransaction> parseCsvFile(String filename) throws IOException {
        List<CsvTransaction> csvTransactionList = new ArrayList<>();

        // Ignore until start with Transaction Date
        BufferedReader csvBufferedReader = new BufferedReader(new FileReader(filename));
        String line;
        do {
            line = csvBufferedReader.readLine();

        } while (!line.startsWith("Transaction Date"));

        //09 Nov 2018
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy");

        // Skip empty lines, then use getter to retrieve information
        line = csvBufferedReader.readLine();
        int counter = 0;
        while (line != null) {
            if (line.length() == 0) {
                line = csvBufferedReader.readLine();
                continue;
            }
            String[] csvElements = line.split(",");
            CsvTransaction csvTransaction = new CsvTransaction();
            csvTransaction.setTransactionDate(LocalDate.parse(csvElements[0], formatter));
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
            csvTransactionList.add(csvTransaction);
            line = csvBufferedReader.readLine();
        }
        return csvTransactionList;
    }
}
