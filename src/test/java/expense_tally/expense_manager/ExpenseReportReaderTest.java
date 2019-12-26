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

        /*
         * Following the practice of writing good test from https://github
         * .com/mockito/mockito/wiki/How-to-write-good-tests, this mock follows the principles of "Avoid coding a
         * tautology". We explicitly write the value so that we do not duplicate the logic between the tests and the code.
         */
        when(mockResultSet.next()).thenReturn(true).thenReturn(false);
        when(mockResultSet.getInt("_id")).thenReturn(1);
        when(mockResultSet.getString("account")).thenReturn("2016");
        when(mockResultSet.getString("amount")).thenReturn("20");
        when(mockResultSet.getString("category")).thenReturn("Entertainment");
        when(mockResultSet.getString("subcategory")).thenReturn("Alcohol/ Restaurant");
        when(mockResultSet.getString("payment_method")).thenReturn("Cash");
        when(mockResultSet.getString("description")).thenReturn("Lunch. Mala Hui cui Guan. Shared with Sal," +
                " Lisa, Rick.");
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

        List<ExpenseReport> actualExpenseReportList = testExpenseReadable.getExpenseTransactions();

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
        DatabaseConnectable mockDatabaseConnectable = Mockito.spy(DatabaseConnectable.class);
        ExpenseReadable testExpenseReadable = new ExpenseReportReader(mockDatabaseConnectable);

        when(mockDatabaseConnectable.connect()).thenReturn(mockConnection);
        when(mockConnection.createStatement()).thenReturn(mockStatement);

        when(mockStatement.executeQuery("SELECT * FROM expense_report")).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(false);

        assertThat(testExpenseReadable.getExpenseTransactions()).hasSize(0);
    }

    @Test
    void getExpenseTransactions_SqlError() throws SQLException {
        DatabaseConnectable mockDatabaseConnectable = Mockito.spy(DatabaseConnectable.class);
        ExpenseReadable testExpenseReadable = new ExpenseReportReader(mockDatabaseConnectable);
        when(mockDatabaseConnectable.connect()).thenThrow(new SQLException("Test SQL error"));
        assertThatThrownBy(() -> testExpenseReadable.getExpenseTransactions())
            .isInstanceOf(SQLException.class);
    }

    /**
     * Test for multiple records of different payment method and different amount
     * TODO: Check if this can be tested, because Mockito can't do mocking effectively
     */
}