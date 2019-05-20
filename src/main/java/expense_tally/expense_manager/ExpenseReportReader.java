package expense_tally.expense_manager;

import expense_tally.expense_manager.model.ExpenseManagerMapKey;
import expense_tally.expense_manager.model.ExpenseManagerTransaction;
import expense_tally.expense_manager.model.ExpenseReport;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * {@code ExpenseReportReader} provide the user ways to retrieve the
 * {@link expense_tally.expense_manager.model.ExpenseReport} from a database file.
 * <p>The current implementation only support reading from SQLite database system.</p>
 *
 * @see expense_tally.expense_manager.model.ExpenseReport
 */
public class ExpenseReportReader implements ExpenseReadable {
  private DatabaseConnectable databaseConnectable;

  /**
   * The default constructor file path of the database file
   *
   * @param databaseConnectable the database source of the expense report retrieval
   */
  public ExpenseReportReader(DatabaseConnectable databaseConnectable) throws SQLException {
    this.databaseConnectable = databaseConnectable;
  }

  /**
   * Returns all the mappings between the content of the customised key and the list of
   * {@link ExpenseManagerTransaction}.
   *
   * @return all the mapping between the content of the customised key and the list of
   * {@link ExpenseManagerTransaction}.
   * <p>The file path <i>databaseFile</i> can be relative to the classpath or a absolute path.</p>
   * @throws SQLException when there is an error accessing the database
   * @see ExpenseManagerMapKey
   */
  @Override
  public Map<ExpenseManagerMapKey, List<ExpenseManagerTransaction>> getExpenseTransactionMap() throws SQLException {
    return ExpenseTransactionMapper.mapExpenseReportsToMap(getExpenseTransactions());
  }

  /**
   * Read the expense transaction from the data source and get return the expense transaction as a {@code List}
   *
   * @return a list of {@code ExpenseTransaction} from the data source
   */
  @Override
  public List<ExpenseReport> getExpenseTransactions() throws SQLException {
    return importDataFromDatabase();
  }

  /**
   * Returns all the expenses stored in the database of expense manager application
   *
   * @return a list of expenses representing the transaction amount, catagroy, subcatagory, payment meothd,
   * description, time of expesnse, and other related field
   * @throws SQLException when there is an error accessing the database
   */
  private List<ExpenseReport> importDataFromDatabase() throws SQLException {
    // Connect to expense_tally.persistence
    List<ExpenseReport> expenseReports = new ArrayList<>();
    // Read all records into objects
    Connection databaseConnection = databaseConnectable.connect();
    Statement retrieveAllStatement = databaseConnection.createStatement();
    ResultSet retrieveAllResultSet = retrieveAllStatement.executeQuery("SELECT * FROM expense_report");
    while (retrieveAllResultSet.next()) {
      ExpenseReport expenseReport = new ExpenseReport();
      expenseReport.setId(retrieveAllResultSet.getInt(Column.ID));
      expenseReport.setAccount(retrieveAllResultSet.getString(Column.ACCOUNT));
      expenseReport.setAmount(retrieveAllResultSet.getString(Column.AMOUNT));
      expenseReport.setCategory(retrieveAllResultSet.getString(Column.CATEGORY));
      expenseReport.setSubcategory(retrieveAllResultSet.getString(Column.SUBCATEGORY));
      expenseReport.setPaymentMethod(retrieveAllResultSet.getString(Column.PAYMENT_METHOD));
      expenseReport.setDescription(retrieveAllResultSet.getString(Column.DESCRIPTION));
      expenseReport.setExpensedTime(retrieveAllResultSet.getLong(Column.EXPENSED_TIME));
      expenseReport.setModificationTime(retrieveAllResultSet.getLong(Column.MODIFICATION_TIME));
      expenseReport.setReferenceNumber(retrieveAllResultSet.getString(Column.REFERENCE_NUMBER));
      expenseReport.setStatus(retrieveAllResultSet.getString(Column.STATUS));
      expenseReport.setProperty1(retrieveAllResultSet.getString(Column.PROPERTY_1));
      expenseReport.setProperty2(retrieveAllResultSet.getString(Column.PROPERTY_2));
      expenseReport.setProperty3(retrieveAllResultSet.getString(Column.PROPERTY_3));
      expenseReport.setProperty4(retrieveAllResultSet.getString(Column.PROPERTY_4));
      expenseReport.setProperty5(retrieveAllResultSet.getString(Column.PROPERTY_5));
      expenseReport.setTax(retrieveAllResultSet.getString(Column.TAX));
      expenseReport.setExpenseTag(retrieveAllResultSet.getString(Column.EXPENSE_TAG));
      expenseReports.add(expenseReport);
    }
    retrieveAllResultSet.close();
    databaseConnection.close();
    return expenseReports;
  }

  public static class Column {
    public static final String ID = "_id";
    public static final String ACCOUNT = "account";
    public static final String AMOUNT = "amount";
    public static final String CATEGORY = "category";
    public static final String SUBCATEGORY = "subcategory";
    public static final String PAYMENT_METHOD = "payment_method";
    public static final String DESCRIPTION = "description";
    public static final String EXPENSED_TIME = "expensed";
    public static final String MODIFICATION_TIME = "modified";
    public static final String REFERENCE_NUMBER = "reference_number";
    public static final String STATUS = "status";
    public static final String PROPERTY_1 = "property";
    public static final String PROPERTY_2 = "property2";
    public static final String PROPERTY_3 = "property3";
    public static final String PROPERTY_4 = "property4";
    public static final String PROPERTY_5 = "property5";
    public static final String TAX = "tax";
    public static final String EXPENSE_TAG = "expense_tag";

  }
}
