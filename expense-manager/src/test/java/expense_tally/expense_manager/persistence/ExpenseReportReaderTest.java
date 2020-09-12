package expense_tally.expense_manager.persistence;

import expense_tally.expense_manager.persistence.DatabaseConnectable;
import expense_tally.persistence.database.ExpenseReport;
import expense_tally.expense_manager.persistence.ExpenseReportReader;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExpenseReportReaderTest {
  @Mock
  ResultSet mockResultSet;
  @Spy
  private DatabaseConnectable spyDatabaseConnectable;
  @Mock
  private Connection mockConnection;
  @Mock
  private Statement mockStatement;
  @InjectMocks
  private ExpenseReportReader expenseReportReader;

  /**
   * A simple happy case where a single transaction database table is being read and parsed correctly
   */
  @Test
  void getExpenseTransactions_retrieve1Record() throws SQLException {
    Mockito.when(spyDatabaseConnectable.connect()).thenReturn(mockConnection);
    Mockito.when(mockConnection.createStatement()).thenReturn(mockStatement);
    Mockito.when(mockStatement.executeQuery("SELECT * FROM expense_report")).thenReturn(mockResultSet);

    /*
     * Following the practice of writing good test from https://github
     * .com/mockito/mockito/wiki/How-to-write-good-tests, this mock follows the principles of "Avoid coding a
     * tautology". We explicitly write the value so that we do not duplicate the logic between the tests and the code.
     */
    Mockito.when(mockResultSet.next()).thenReturn(true).thenReturn(false);
    Mockito.when(mockResultSet.getInt("_id")).thenReturn(1);
    Mockito.when(mockResultSet.getString("account")).thenReturn("2016");
    Mockito.when(mockResultSet.getString("amount")).thenReturn("20");
    Mockito.when(mockResultSet.getString("category")).thenReturn("Entertainment");
    Mockito.when(mockResultSet.getString("subcategory")).thenReturn("Alcohol/ Restaurant");
    Mockito.when(mockResultSet.getString("payment_method")).thenReturn("Cash");
    Mockito.when(mockResultSet.getString("description")).thenReturn("Lunch. Mala Hui cui Guan. Shared with Sal," +
        " Lisa, Rick.");
    Mockito.when(mockResultSet.getLong("expensed")).thenReturn(Long.valueOf("1459489440000"));
    Mockito.when(mockResultSet.getLong("modified")).thenReturn(Long.valueOf("1459816738453"));
    Mockito.when(mockResultSet.getString("reference_number")).thenReturn("");
    Mockito.when(mockResultSet.getString("status")).thenReturn("Cleared");
    Mockito.when(mockResultSet.getString("property")).thenReturn("");
    Mockito.when(mockResultSet.getString("property2")).thenReturn("2016-04-01-13-44-00-573.jpg");
    Mockito.when(mockResultSet.getString("property3")).thenReturn("");
    Mockito.when(mockResultSet.getString("property4")).thenReturn("");
    Mockito.when(mockResultSet.getString("property5")).thenReturn("");
    Mockito.when(mockResultSet.getString("tax")).thenReturn("");
    Mockito.when(mockResultSet.getString("expense_tag")).thenReturn("");

    List<ExpenseReport> actualExpenseReportList = expenseReportReader.getExpenseTransactions();

    SoftAssertions softAssertions = new SoftAssertions();
    softAssertions.assertThat(actualExpenseReportList).hasSize(1);
    softAssertions.assertThat(actualExpenseReportList).element(0).extracting("amount").isEqualTo("20");
    softAssertions.assertThat(actualExpenseReportList).element(0).extracting("category").isEqualTo("Entertainment");
    softAssertions.assertThat(actualExpenseReportList).element(0).extracting("subcategory").isEqualTo("Alcohol/ Restaurant");
    softAssertions.assertThat(actualExpenseReportList).element(0).extracting("paymentMethod").isEqualTo("Cash");
    softAssertions.assertThat(actualExpenseReportList).element(0).extracting("description").isEqualTo("Lunch. Mala Hui cui Guan. Shared with Sal, Lisa, Rick.");
    softAssertions.assertThat(actualExpenseReportList).element(0).extracting("expensedTime").isEqualTo(Long.valueOf("1459489440000"));
    softAssertions.assertThat(actualExpenseReportList).element(0).extracting("referenceNumber").isEqualTo("");
    softAssertions.assertAll();
  }

  @Test
  void getExpenseTransactions_noRecord() throws SQLException {
    Mockito.when(spyDatabaseConnectable.connect()).thenReturn(mockConnection);
    Mockito.when(mockConnection.createStatement()).thenReturn(mockStatement);

    Mockito.when(mockStatement.executeQuery("SELECT * FROM expense_report")).thenReturn(mockResultSet);
    Mockito.when(mockResultSet.next()).thenReturn(false);

    assertThat(expenseReportReader.getExpenseTransactions()).hasSize(0);
  }

  @Test
  void getExpenseTransactions_SqlError() throws SQLException {
    Mockito.when(spyDatabaseConnectable.connect()).thenThrow(new SQLException("test SQLException"));
    Assertions.assertThatThrownBy(() -> expenseReportReader.getExpenseTransactions())
        .isInstanceOf(SQLException.class)
        .hasMessage("test SQLException");
  }

  /**
   * Test for multiple records of different payment method and different amount
   * TODO: Check if this can be tested, because Mockito can't do mocking effectively
   */
}