package expense_tally.persistence;

import expense_tally.model.ExpenseManager.ExpenseManagerMapKey;
import expense_tally.model.ExpenseManager.ExpenseManagerTransaction;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Data access object for expense transaction
 */
public class ExpenseTransactionDao {
    private SqlLiteConnectionManager sqlLiteConnectionManager;

    public ExpenseTransactionDao(String databaseFile) {
        this.sqlLiteConnectionManager = new SqlLiteConnectionManager(databaseFile);
    }

    public Map<ExpenseManagerMapKey, List<ExpenseManagerTransaction>> getAllExpenseTransactions() throws SQLException {
        Map<Double, List<ExpenseManagerTransaction>> expenseTransactionMap = new HashMap();

        Connection databaseConnection = sqlLiteConnectionManager.connect();
        List<ExpenseReport> expenseReports = importDataFromDatabase(databaseConnection);
        return ExpenseTransactionMapper.mapExpenseReportsToMap(expenseReports);
    }

    private List<ExpenseReport> importDataFromDatabase(Connection databaseConnection) throws SQLException {
        // Connect to expense_tally.persistence
        List<ExpenseReport> expenseReports = new ArrayList<>();
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
        return expenseReports;
    }
}
