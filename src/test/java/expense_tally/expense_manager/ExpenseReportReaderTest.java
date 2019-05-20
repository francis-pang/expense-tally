package expense_tally.expense_manager;

import expense_tally.expense_manager.model.ExpenseReport;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
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
  private DatabaseConnectable mockDatabaseConnectable;

  @Mock
  private Connection mockConnection;

  @Mock
  private Statement mockStatement;

  @Mock
  ResultSet mockResultSet;

  /**
   * A simple happy case where a single transaction database table is being read and parsed correctly
   */
  @Test
  void getExpenseTransactions_retrieve1Record() throws SQLException {
    ExpenseReadable testExpenseReadable = new ExpenseReportReader(mockDatabaseConnectable);
    when(mockDatabaseConnectable.connect()).thenReturn(mockConnection);
    when(mockConnection.createStatement()).thenReturn(mockStatement);
    when(mockStatement.executeQuery("SELECT * FROM expense_report")).thenReturn(mockResultSet);

    when(mockResultSet.next()).thenReturn(true).thenReturn(false);
    when(mockResultSet.getInt(ExpenseReportReader.Column.ID)).thenReturn(1);
    when(mockResultSet.getString(ExpenseReportReader.Column.ACCOUNT)).thenReturn("2016");
    when(mockResultSet.getString(ExpenseReportReader.Column.AMOUNT)).thenReturn("20");
    when(mockResultSet.getString(ExpenseReportReader.Column.CATEGORY)).thenReturn("Entertainment");
    when(mockResultSet.getString(ExpenseReportReader.Column.SUBCATEGORY)).thenReturn("Alcohol/ Restaurant");
    when(mockResultSet.getString(ExpenseReportReader.Column.PAYMENT_METHOD)).thenReturn("Cash");
    when(mockResultSet.getString(ExpenseReportReader.Column.DESCRIPTION)).thenReturn("Lunch. Mala Hui cui Guan. " +
        "Shared with Sal, Lisa, Rick.");
    when(mockResultSet.getLong(ExpenseReportReader.Column.EXPENSED_TIME)).thenReturn(Long.valueOf("1459489440000"));
    when(mockResultSet.getLong(ExpenseReportReader.Column.MODIFICATION_TIME)).thenReturn(Long.valueOf("1459816738453"));
    when(mockResultSet.getString(ExpenseReportReader.Column.REFERENCE_NUMBER)).thenReturn("");
    when(mockResultSet.getString(ExpenseReportReader.Column.STATUS)).thenReturn("Cleared");
    when(mockResultSet.getString(ExpenseReportReader.Column.PROPERTY_1)).thenReturn("");
    when(mockResultSet.getString(ExpenseReportReader.Column.PROPERTY_2)).thenReturn("2016-04-01-13-44-00-573.jpg");
    when(mockResultSet.getString(ExpenseReportReader.Column.PROPERTY_3)).thenReturn("");
    when(mockResultSet.getString(ExpenseReportReader.Column.PROPERTY_4)).thenReturn("");
    when(mockResultSet.getString(ExpenseReportReader.Column.PROPERTY_5)).thenReturn("");
    when(mockResultSet.getString(ExpenseReportReader.Column.TAX)).thenReturn("");
    when(mockResultSet.getString(ExpenseReportReader.Column.EXPENSE_TAG)).thenReturn("");

    List<ExpenseReport> actualExpenseReportList = testExpenseReadable.getExpenseTransactions();

    SoftAssertions softAssertions = new SoftAssertions();
    softAssertions.assertThat(actualExpenseReportList).hasSize(1);
    softAssertions.assertThat(actualExpenseReportList).element(0).extracting("amount").containsExactly("20");
    softAssertions.assertThat(actualExpenseReportList).element(0).extracting("category").containsExactly("Entertainment");
    softAssertions.assertThat(actualExpenseReportList).element(0).extracting("subcategory").containsExactly("Alcohol/ Restaurant");
    softAssertions.assertThat(actualExpenseReportList).element(0).extracting("paymentMethod").containsExactly("Cash");
    softAssertions.assertThat(actualExpenseReportList).element(0).extracting("description").containsExactly("Lunch. Mala Hui cui Guan. Shared with Sal, Lisa, Rick.");
    softAssertions.assertThat(actualExpenseReportList).element(0).extracting("expensedTime").containsExactly(Long.valueOf("1459489440000"));
    softAssertions.assertThat(actualExpenseReportList).element(0).extracting("referenceNumber").containsExactly("");
    softAssertions.assertAll();
  }

  @Test
  void getExpenseTransactions_noRecord() throws SQLException {
    DatabaseConnectable mockDatabaseConnectable = Mockito.spy(DatabaseConnectable.class);
    ExpenseReadable testExpenseReadable = new ExpenseReportReader(mockDatabaseConnectable);

    when(mockDatabaseConnectable.connect()).thenReturn(mockConnection);
    when(mockConnection.createStatement()).thenReturn(mockStatement);

    when(mockStatement.executeQuery("SELECT * FROM expense_report")).thenReturn(mockResultSet);
    when(mockResultSet.next()).thenReturn(false);

    assertThat(testExpenseReadable.getExpenseTransactions()).hasSize(0);
  }

  @Test
  void getExpenseTransactions_SqlError() {
    DatabaseConnectable mockDatabaseConnectable = Mockito.spy(DatabaseConnectable.class);

    assertThatThrownBy(() -> {
      ExpenseReadable testExpenseReadable = new ExpenseReportReader(mockDatabaseConnectable);
      when(mockDatabaseConnectable.connect()).thenThrow(new SQLException("Test SQL error"));
      testExpenseReadable.getExpenseTransactions();
    }).isInstanceOf(SQLException.class);
  }

  /**
   * Test for multiple records of different payment method and different amount
   * TODO: Check if this can be tested, because Mockito can't do mocking effectively
   */
}