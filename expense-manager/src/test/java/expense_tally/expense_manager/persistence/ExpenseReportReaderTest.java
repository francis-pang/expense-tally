package expense_tally.expense_manager.persistence;

import expense_tally.expense_manager.mapper.ExpenseReportMapper;
import expense_tally.model.persistence.database.ExpenseReport;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
class ExpenseReportReaderTest {
  @Spy
  private DatabaseConnectable spyDatabaseConnectable;
  @Mock
  private Connection mockConnection;
  @Mock
  private ExpenseReportMapper mockExpenseReportMapper;
  @Mock
  private SqliteSessionFactoryBuilder mockSqliteSessionBuilder;
  @Mock
  private SqlSessionFactory mockSessionFactory;
  @Mock
  private SqlSession mockSqlSession;
  @InjectMocks
  private ExpenseReportReader expenseReportReader;

  /**
   * A simple happy case where a single transaction database table is being read and parsed correctly
   */
  @Test
  void getExpenseTransactions_retrieve1Record() throws SQLException {
    List<ExpenseReport> expectedExpenseReports = new ArrayList<>();
    /*
     * Following the practice of writing good test from https://github
     * .com/mockito/mockito/wiki/How-to-write-good-tests, this mock follows the principles of "Avoid coding a
     * tautology". We explicitly write the value so that we do not duplicate the logic between the tests and the code.
     */
    ExpenseReport expectedExpenseReport1 = new ExpenseReport();
    expectedExpenseReport1.setId(1);
    expectedExpenseReport1.setAmount("20");
    expectedExpenseReport1.setAccount("2016");
    expectedExpenseReport1.setCategory("Entertainment");
    expectedExpenseReport1.setSubcategory("Alcohol/ Restaurant");
    expectedExpenseReport1.setPaymentMethod("Cash");
    expectedExpenseReport1.setDescription("Lunch. Mala Hui cui Guan. Shared with Sal, Lisa, Rick.");
    expectedExpenseReport1.setExpensedTime(Long.valueOf("1459489440000"));
    expectedExpenseReport1.setModificationTime(Long.valueOf("1459816738453"));
    expectedExpenseReport1.setReferenceNumber("");
    expectedExpenseReport1.setStatus("Cleared");
    expectedExpenseReport1.setProperty1("");
    expectedExpenseReport1.setProperty2("2016-04-01-13-44-00-573.jpg");
    expectedExpenseReport1.setProperty3("");
    expectedExpenseReport1.setProperty4("");
    expectedExpenseReport1.setProperty5("");
    expectedExpenseReport1.setTax("");
    expectedExpenseReport1.setExpenseTag("");
    expectedExpenseReports.add(expectedExpenseReport1);

    Mockito.when(spyDatabaseConnectable.connect()).thenReturn(mockConnection);
    Mockito.when(mockSqliteSessionBuilder.createSqliteSessionFactory()).thenReturn(mockSessionFactory);
    Mockito.when(mockSessionFactory.openSession(ExecutorType.REUSE, mockConnection)).thenReturn(mockSqlSession);
    Mockito.when(mockSqlSession.getMapper(ExpenseReportMapper.class)).thenReturn(mockExpenseReportMapper);
    Mockito.when(mockExpenseReportMapper.getAllExpenseReports()).thenReturn(expectedExpenseReports);

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
    List<ExpenseReport> expenseReports = new ArrayList<>();

    Mockito.when(spyDatabaseConnectable.connect()).thenReturn(mockConnection);
    Mockito.when(spyDatabaseConnectable.connect()).thenReturn(mockConnection);
    Mockito.when(mockSqliteSessionBuilder.createSqliteSessionFactory()).thenReturn(mockSessionFactory);
    Mockito.when(mockSessionFactory.openSession(ExecutorType.REUSE, mockConnection)).thenReturn(mockSqlSession);
    Mockito.when(mockSqlSession.getMapper(ExpenseReportMapper.class)).thenReturn(mockExpenseReportMapper);
    Mockito.when(mockExpenseReportMapper.getAllExpenseReports()).thenReturn(expenseReports);

    assertThat(expenseReportReader.getExpenseTransactions()).hasSize(0);
  }

  @Test
  void getExpenseTransactions_SqlError() throws SQLException {
    Mockito.when(spyDatabaseConnectable.connect()).thenThrow(new SQLException("test SQLException"));
    assertThatThrownBy(() -> expenseReportReader.getExpenseTransactions())
        .isInstanceOf(SQLException.class)
        .hasMessage("test SQLException");
  }

  /**
   * Test for multiple records of different payment method and different amount
   * TODO: Check if this can be tested, because Mockito can't do mocking effectively
   */
}