package expense_tally.expense_manager;

import expense_tally.expense_manager.model.ExpenseManagerMapKey;
import expense_tally.expense_manager.model.ExpenseManagerTransaction;
import expense_tally.expense_manager.model.ExpenseReport;

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

    /**
     * Construct a ExpenseTransactionDao with the file path of the database file
     * <p>The file path <i>databaseFile</i> can be relative to the classpath or a absolute path.</p>
     * @param databaseFile file path of the database file
     */
    public ExpenseTransactionDao(String databaseFile) {
        this.sqlLiteConnectionManager = new SqlLiteConnectionManager(databaseFile);
    }

    /**
     * Returns all the mappings between the content of the customised key and the list of
     * {@link ExpenseManagerTransaction}.
     * @return all the mapping between the content of the customised key and the list of
     * {@link ExpenseManagerTransaction}.
     * @throws SQLException when there is an error accessing the database
     * @see ExpenseManagerMapKey
     */
    public Map<ExpenseManagerMapKey, List<ExpenseManagerTransaction>> getAllExpenseTransactions() throws SQLException {
        Map<Double, List<ExpenseManagerTransaction>> expenseTransactionMap = new HashMap<>();
        Connection databaseConnection = sqlLiteConnectionManager.connect();
        List<ExpenseReport> expenseReports = importDataFromDatabase(databaseConnection);
        return ExpenseTransactionMapper.mapExpenseReportsToMap(expenseReports);
    }

    /**
     * Returns all the expenses stored in the database of expense manager application
     * @param databaseConnection the connection to the expense manager database
     * @return a list of expenses representing the transaction amount, catagroy, subcatagory, payment meothd,
     * description, time of expesnse, and other related field
     * @throws SQLException when there is an error accessing the database
     */
    private List<ExpenseReport> importDataFromDatabase(Connection databaseConnection) throws SQLException {
        // Connect to expense_tally.persistence
        List<ExpenseReport> expenseReports = new ArrayList<>();
        // Read all records into objects
        Statement retrieveAlLStatement = databaseConnection.createStatement();
        ResultSet retrieveAllResultSet = retrieveAlLStatement.executeQuery("SELECT * FROM expense_report");
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
