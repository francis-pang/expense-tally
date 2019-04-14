package expense_tally.expense_manager;

import expense_tally.expense_manager.model.ExpenseManagerMapKey;
import expense_tally.expense_manager.model.ExpenseManagerTransaction;
import expense_tally.expense_manager.model.PaymentMethod;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Instant;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExpenseTransactionDaoTest {
  @Mock
  SqlLiteConnectionManager mockSqlLiteConnectionManager = mock(SqlLiteConnectionManager.class);

  @InjectMocks
  static ExpenseTransactionDao testExpenseTransactionDao = new ExpenseTransactionDao("test-file.db");

  /**
   * A simple happy case where a single transaction database table is being read and parsed correctly
   */
  @Test
  void getAllExpenseTransactionsFromDatabase_happyCase() throws SQLException {
    Connection mockDatabaseConnection = mock(Connection.class);
    when(mockSqlLiteConnectionManager.connect()).thenReturn(mockDatabaseConnection);
    Statement mockStatement = mock(Statement.class);
    when(mockDatabaseConnection.createStatement()).thenReturn(mockStatement);

    // Mock what the result set will return back
    ResultSet mockResultSet = mock(ResultSet.class);
    when(mockResultSet.next()).thenReturn(true).thenReturn(false);
    when(mockResultSet.getInt("_id")).thenReturn(1);
    when(mockResultSet.getString("account")).thenReturn("2016");
    when(mockResultSet.getString("amount")).thenReturn("20");
    when(mockResultSet.getString("category")).thenReturn("Entertainment");
    when(mockResultSet.getString("subcategory")).thenReturn("Alcohol/ Restaurant");
    when(mockResultSet.getString("payment_method")).thenReturn("Cash");
    when(mockResultSet.getString("description")).thenReturn("Lunch. Mala Hui cui Guan. Shared with Sal, Lisa, Rick.");
    when(mockResultSet.getLong("expensed")).thenReturn(Long.valueOf("1459489440000"));
    when(mockResultSet.getLong("modified")).thenReturn(Long.valueOf("1459816738453"));
    when(mockResultSet.getString("reference_number")).thenReturn("");
    when(mockResultSet.getString("status")).thenReturn("Cleared");
    when(mockResultSet.getString("property")).thenReturn("");
    when(mockResultSet.getString("property2")).thenReturn("2016-04-01-13-44-00-573.jpg");
    when(mockResultSet.getString("property3")).thenReturn("");
    when(mockResultSet.getString("property4")).thenReturn("");
    when(mockResultSet.getString("property5")).thenReturn("");
    when(mockResultSet.getString("tax")).thenReturn("");
    when(mockResultSet.getString("expense_tag")).thenReturn("");
    when(mockStatement.executeQuery("SELECT * FROM expense_report")).thenReturn(mockResultSet);

    Map<ExpenseManagerMapKey, List<ExpenseManagerTransaction>> actualExpenseManagerMap = testExpenseTransactionDao.getAllExpenseTransactions();

    // Extract the list of transactions
    ExpenseManagerMapKey testExpenseManagerMapKey = new ExpenseManagerMapKey(PaymentMethod.CASH);
    testExpenseManagerMapKey.setAmount(20.0);
    List<ExpenseManagerTransaction> actualExpenseManagerTransactionList = actualExpenseManagerMap.get(testExpenseManagerMapKey);

    SoftAssertions softAssertions = new SoftAssertions();
    softAssertions.assertThat(actualExpenseManagerTransactionList).hasSize(1);
    softAssertions.assertThat(actualExpenseManagerTransactionList).element(0).extracting("amount").containsExactly(20.0);
    softAssertions.assertThat(actualExpenseManagerTransactionList).element(0).extracting("category").containsExactly("Entertainment");
    softAssertions.assertThat(actualExpenseManagerTransactionList).element(0).extracting("subcategory").containsExactly("Alcohol/ Restaurant");
    softAssertions.assertThat(actualExpenseManagerTransactionList).element(0).extracting("paymentMethod").containsExactly("Cash");
    softAssertions.assertThat(actualExpenseManagerTransactionList).element(0).extracting("description").containsExactly("Lunch. Mala Hui cui Guan. Shared with Sal, Lisa, Rick.");
    softAssertions.assertThat(actualExpenseManagerTransactionList).element(0).extracting("expensedTime").containsExactly(Instant.ofEpochSecond(1459489440));
    softAssertions.assertThat(actualExpenseManagerTransactionList).element(0).extracting("referenceAmount").containsExactly(0.0);
    softAssertions.assertAll();
  }

    @Test
    void getAllExpenseTransactionsFromDatabase_noRecord() throws SQLException {
        Connection mockDatabaseConnection = mock(Connection.class);
        when(mockSqlLiteConnectionManager.connect()).thenReturn(mockDatabaseConnection);
        Statement mockStatement = mock(Statement.class);
        when(mockDatabaseConnection.createStatement()).thenReturn(mockStatement);

        // Mock what the result set will return back
        ResultSet mockResultSet = mock(ResultSet.class);
        when(mockResultSet.next()).thenReturn(false);
        when(mockStatement.executeQuery("SELECT * FROM expense_report")).thenReturn(mockResultSet);

        Map<ExpenseManagerMapKey, List<ExpenseManagerTransaction>> actualExpenseManagerMap = testExpenseTransactionDao.getAllExpenseTransactions();
        assertThat(actualExpenseManagerMap).hasSize(0);
    }

    /**
     * Test for multiple records of different payment method and different amount
     * TODO: Check if this can be tested, because Mockito can't do mocking effectively
     */
}