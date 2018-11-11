package expense_tally;

import expense_tally.database.ExpenseReport;
import expense_tally.database.ExpenseTransactionMapper;
import expense_tally.database.SqlLiteConnectionManager;
import expense_tally.model.CsvTransaction;
import expense_tally.model.ExpenseTransaction;
import expense_tally.service.CsvParser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.*;
import java.time.temporal.TemporalUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.lang.Long;

public class Application {
    private static final Logger LOGGER = Logger.getLogger(Application.class.getName());

    public static void main (String args[]) {
        // assumes the current class is called MyLogger


        // Read CSV file
        final String filename = "src/main/resource/csv/3db7c598cadc80893570d55a0243df1c.P000000013229282.csv";
        File file = new File(filename);
        System.out.println(file.getAbsolutePath());

        List<CsvTransaction> csvTransactions = new ArrayList<>();
        CsvParser transactionCsvParser = new CsvParser();
        try {
            csvTransactions = transactionCsvParser.parseCsvFile(filename);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Display
        /*
        for (CsvTransaction csvLine : csvTransactions) {
            System.out.println(csvLine.toString());
        }
        */

        // Connect to expense_tally.database
        List<ExpenseReport> expenseReports = new ArrayList<>();
        try {
            Connection databaseConnection = SqlLiteConnectionManager.connect();

            System.out.println("Network established");

            // Read all records into objects
            Statement retrieveAlLStatement = databaseConnection.createStatement();
            ResultSet retrieveAllResultSet = retrieveAlLStatement.executeQuery("SELECT * FROM expense_report");
            System.out.println(retrieveAllResultSet.getFetchSize());
            while(retrieveAllResultSet.next()) {
                ExpenseReport expenseReport = new ExpenseReport();
                expenseReport.setId(retrieveAllResultSet.getInt("_id"));
                expenseReport.setAccount(retrieveAllResultSet.getString("account"));
                expenseReport.setAmount(retrieveAllResultSet.getString("amount"));
                expenseReport.setCategory(retrieveAllResultSet.getString("category"));
                expenseReport.setSubcategory(retrieveAllResultSet.getString("subcategory"));
                expenseReport.setPaymentMethod(retrieveAllResultSet.getString("payment_method"));
                expenseReport.setDescription(retrieveAllResultSet.getString("description"));
                expenseReport.setExpensed(retrieveAllResultSet.getLong("expensed"));
                expenseReport.setModified(retrieveAllResultSet.getLong("modified"));
                expenseReport.setReferenceNumber(retrieveAllResultSet.getString("reference_number"));
                expenseReport.setStatus(retrieveAllResultSet.getString("status"));
                expenseReport.setProperty(retrieveAllResultSet.getString("property"));
                expenseReport.setProperty2(retrieveAllResultSet.getString("property2"));
                expenseReport.setProperty3(retrieveAllResultSet.getString("property3"));
                expenseReport.setProperty3(retrieveAllResultSet.getString("property4"));
                expenseReport.setProperty5(retrieveAllResultSet.getString("property5"));
                expenseReport.setTax(retrieveAllResultSet.getString("tax"));
                expenseReport.setExpenseTag(retrieveAllResultSet.getString("expense_tag"));
                expenseReports.add(expenseReport);
            }

            retrieveAllResultSet.close();
            databaseConnection.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Data mapping
        Map<Double, List<ExpenseTransaction>> expenseTransactionMap = ExpenseTransactionMapper.mapExpenseReports(expenseReports);

        // Reconcile data
        int numberOfNoMatchTransaction = 0;
        for (CsvTransaction csvTransaction : csvTransactions) {
            if (csvTransaction.getDebitAmount() == 0) {
                LOGGER.fine("This is not a debit transaction");
                continue;
            }
            List<ExpenseTransaction> expenseTransactionList = expenseTransactionMap.get(csvTransaction.getDebitAmount());
            if (expenseTransactionList == null) {
                LOGGER.info("Transaction in the CSV file does not exist in Expense Manager: " + csvTransaction.toString());
                numberOfNoMatchTransaction++;
                continue;
            }
            int noOfMatchingTransaction = 0;
            for(ExpenseTransaction matchingExpenseTransaction : expenseTransactionList) {
                LOGGER.fine("Comparing " + csvTransaction.getTransactionDate() + " vs " + LocalDate.ofInstant(matchingExpenseTransaction.getExpensedTime(), ZoneId.of("UTC").normalized()));
                Duration transactionTimeDifference = Duration.between(matchingExpenseTransaction.getExpensedTime(), csvTransaction.getTransactionDate().atStartOfDay().toInstant(ZoneOffset.UTC));
                if(transactionTimeDifference.toHours() > -24 &&
                   transactionTimeDifference.toHours() <= 48) {
                    noOfMatchingTransaction++;
                }
            }
            switch(noOfMatchingTransaction) {
                case 0:
                    LOGGER.info("After going through the list of matching amount, transaction in the CSV file does not exist in Expense Manager: " + csvTransaction.toString());
                    numberOfNoMatchTransaction++;
                    break;
                case 1:
                    LOGGER.finer("Found a matching transaction");
                    break;
                default:
                    LOGGER.info("Found more than 1 matching transaction for this");
                    LOGGER.info(csvTransaction.toString());
                    break;
            }
        }
        LOGGER.info("Found " + numberOfNoMatchTransaction + " non-matching transactions.");
    }
}
