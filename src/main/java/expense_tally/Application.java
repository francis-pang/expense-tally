package expense_tally;

import database.ExpenseReport;
import database.SqlLiteConnectionManager;
import expense_tally.model.CsvTransaction;
import expense_tally.service.CsvParser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class Application {
    public static void main (String args[]) {
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

        // Connect to database
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
                expenseReport.setExpensed(retrieveAllResultSet.getInt("expensed"));
                expenseReport.setModified(retrieveAllResultSet.getInt("modified"));
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

        // Display
        for (ExpenseReport expenseReport : expenseReports) {
            System.out.println(expenseReport.toString());
        }
    }
}
