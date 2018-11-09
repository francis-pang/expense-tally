package expense_tally;

import expense_tally.model.TransactionCsv;
import expense_tally.service.CsvParser;

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class Application {
    public static void main (String args[]) {
        // Read CSV file
        final String filename = "src/main/resource/csv/3db7c598cadc80893570d55a0243df1c.P000000013229282.csv";
        File file = new File(filename);
        System.out.println(file.getAbsolutePath());

        List<TransactionCsv> transactionCsvList = new ArrayList<>();
        CsvParser transactionCsvParser = new CsvParser();
        try {
            transactionCsvList = transactionCsvParser.parseCsvFile(filename);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Display
        for (TransactionCsv csvLine : transactionCsvList) {
            System.out.println(csvLine.toString());
        }
    }
}
