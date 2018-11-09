package expense_tally.service;

import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class CsvParser {
    public CsvParser() {
    }

    public List<expense_tally.model.TransactionCsv> parseCsvFile(String filename) throws IOException {
        List<expense_tally.model.TransactionCsv> transactionCsvList = new ArrayList<>();

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
            expense_tally.model.TransactionCsv transactionCsv = new expense_tally.model.TransactionCsv();
            transactionCsv.setTransactionDate(LocalDate.parse(csvElements[0], formatter));
            transactionCsv.setReference(csvElements[1]);
            transactionCsv.setDebitAmount((csvElements[2].isBlank()) ? 0.00 : Double.parseDouble(csvElements[2]));
            transactionCsv.setCreditAmount((csvElements[3].isBlank()) ? 0.00 : Double.parseDouble(csvElements[3]));
            if (csvElements.length >= 5) {
                transactionCsv.setTransactionRef1(csvElements[4]);
            } else {
                transactionCsv.setTransactionRef1("");
            }
            if (csvElements.length >= 6) {
                transactionCsv.setTransactionRef2(csvElements[5]);
            } else {
                transactionCsv.setTransactionRef2("");
            }
            if (csvElements.length >= 7) {
                transactionCsv.setTransactionRef3(csvElements[6]);
            } else {
                transactionCsv.setTransactionRef3("");
            }
            transactionCsvList.add(transactionCsv);
            line = csvBufferedReader.readLine();
        }
        return transactionCsvList;
    }
}
