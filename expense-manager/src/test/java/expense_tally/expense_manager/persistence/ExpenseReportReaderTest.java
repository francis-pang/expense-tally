package expense_tally.expense_manager.persistence;

import expense_tally.expense_manager.mapper.ExpenseReportMapper;
import expense_tally.model.persistence.database.ExpenseReport;
import org.apache.ibatis.binding.BindingException;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
class ExpenseReportReaderTest {
  @Mock
  private DatabaseConnectable mockDatabaseConnectable;

  @Mock
  private DatabaseSessionFactoryBuilder mockDatabaseSessionFactoryBuilder;

  private ExpenseReportReader expenseReportReader;

  // This is needed instead of using @InjectMocks because String cannot be mocked.
  @BeforeEach
  void setUp() {
    expenseReportReader = new ExpenseReportReader(mockDatabaseConnectable, mockDatabaseSessionFactoryBuilder,
            "testId");
  }

  /**
   * A simple happy case where a single transaction database table is being read and parsed correctly
   */
  @Test
  void getExpenseTransactions_retrieve1Record() throws SQLException, IOException {
    List<ExpenseReport> expectedExpenseReports = new ArrayList<>();
    /*
     * Following the practice of writing good test from
     * https://github.com/mockito/mockito/wiki/How-to-write-good-tests, this mock follows the principles of "Avoid
     * coding a tautology". We explicitly write the value so that we do not duplicate the logic between the tests and
     * the code.
     */
    ExpenseReport expectedExpenseReport1 = new ExpenseReport();
    expectedExpenseReport1.setId(1);
    expectedExpenseReport1.setAmount("20");
    expectedExpenseReport1.setAccount("2016");
    expectedExpenseReport1.setCategory("Entertainment");
    expectedExpenseReport1.setSubcategory("Alcohol/ Restaurant");
    expectedExpenseReport1.setPaymentMethod("Cash");
    expectedExpenseReport1.setDescription("Lunch. Mala Hui cui Guan. Shared with Sal, Lisa, Rick.");
    expectedExpenseReport1.setExpensedTime(Long.parseLong("1459489440000"));
    expectedExpenseReport1.setModificationTime(Long.parseLong("1459816738453"));
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

    Connection mockConnection = Mockito.mock(Connection.class);
    Mockito.when(mockDatabaseConnectable.connect()).thenReturn(mockConnection);
    SqlSessionFactory mockSqlSessionFactory = Mockito.mock(SqlSessionFactory.class);
    Mockito.when(mockDatabaseSessionFactoryBuilder.buildSessionFactory(Mockito.anyString()))
            .thenReturn(mockSqlSessionFactory);
    SqlSession mockSqlSession = Mockito.mock(SqlSession.class);
    Mockito.when(mockSqlSessionFactory.openSession(ExecutorType.REUSE, mockConnection)).thenReturn(mockSqlSession);
    ExpenseReportMapper mockExpenseReportMapper = Mockito.mock(ExpenseReportMapper.class);
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
  void getExpenseTransactions_noRecord() throws SQLException, IOException {
    Connection mockConnection = Mockito.mock(Connection.class);
    Mockito.when(mockDatabaseConnectable.connect()).thenReturn(mockConnection);
    SqlSessionFactory mockSqlSessionFactory = Mockito.mock(SqlSessionFactory.class);
    Mockito.when(mockDatabaseSessionFactoryBuilder.buildSessionFactory(Mockito.anyString()))
            .thenReturn(mockSqlSessionFactory);
    SqlSession mockSqlSession = Mockito.mock(SqlSession.class);
    Mockito.when(mockSqlSessionFactory.openSession(ExecutorType.REUSE, mockConnection)).thenReturn(mockSqlSession);
    ExpenseReportMapper mockExpenseReportMapper = Mockito.mock(ExpenseReportMapper.class);
    Mockito.when(mockSqlSession.getMapper(ExpenseReportMapper.class)).thenReturn(mockExpenseReportMapper);
    Mockito.when(mockExpenseReportMapper.getAllExpenseReports()).thenReturn(Collections.emptyList());
    assertThat(expenseReportReader.getExpenseTransactions())
            .isNotNull()
            .hasSize(0);
  }

  @Test
  void getExpenseTransactions_SqlError() throws SQLException {
    Connection mockConnection = Mockito.mock(Connection.class);
    Mockito.when(mockDatabaseConnectable.connect())
            .thenThrow(new SQLException("New SQL error"));
    assertThatThrownBy(() -> expenseReportReader.getExpenseTransactions())
        .isInstanceOf(SQLException.class)
        .hasMessage("New SQL error");
  }

  @Test
  void getExpenseTransaction_IOException() throws IOException {
    Mockito.when(mockDatabaseSessionFactoryBuilder.buildSessionFactory(Mockito.anyString()))
            .thenThrow(new IOException("test IOException"));
    assertThatThrownBy(() -> expenseReportReader.getExpenseTransactions())
            .isInstanceOf(IOException.class)
            .hasMessage("test IOException");
  }

  @Test
  void getExpenseTransaction_multipleRecords() throws SQLException, IOException {
    List<ExpenseReport> expectedExpenseReports = new ArrayList<>();

    ExpenseReport expectedExpenseReport1 = new ExpenseReport();
    expectedExpenseReport1.setId(1);
    expectedExpenseReport1.setAmount("20");
    expectedExpenseReport1.setAccount("2016");
    expectedExpenseReport1.setCategory("Entertainment");
    expectedExpenseReport1.setSubcategory("Alcohol/ Restaurant");
    expectedExpenseReport1.setPaymentMethod("Cash");
    expectedExpenseReport1.setDescription("Lunch. Mala Hui cui Guan. Shared with Sal, Lisa, Rick.");
    expectedExpenseReport1.setExpensedTime(Long.parseLong("1459489440000"));
    expectedExpenseReport1.setModificationTime(Long.parseLong("1459816738453"));
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

    ExpenseReport expectedExpenseReport2 = new ExpenseReport();
    expectedExpenseReport2.setId(2);
    expectedExpenseReport2.setAmount("20");
    expectedExpenseReport2.setAccount("2016");
    expectedExpenseReport2.setCategory("Entertainment");
    expectedExpenseReport2.setSubcategory("Alcohol/ Restaurant");
    expectedExpenseReport2.setPaymentMethod("Cash");
    expectedExpenseReport2.setDescription("Lunch. Mala Hui cui Guan. Shared with Sal, Lisa, Rick.");
    expectedExpenseReport2.setExpensedTime(Long.parseLong("1459489440000"));
    expectedExpenseReport2.setModificationTime(Long.parseLong("1459816738453"));
    expectedExpenseReport2.setReferenceNumber("");
    expectedExpenseReport2.setStatus("Cleared");
    expectedExpenseReport2.setProperty1("");
    expectedExpenseReport2.setProperty2("2016-04-01-13-44-00-574.jpg");
    expectedExpenseReport2.setProperty3("");
    expectedExpenseReport2.setProperty4("");
    expectedExpenseReport2.setProperty5("");
    expectedExpenseReport2.setTax("");
    expectedExpenseReport2.setExpenseTag("");
    expectedExpenseReports.add(expectedExpenseReport2);

    Connection mockConnection = Mockito.mock(Connection.class);
    Mockito.when(mockDatabaseConnectable.connect()).thenReturn(mockConnection);
    SqlSessionFactory mockSqlSessionFactory = Mockito.mock(SqlSessionFactory.class);
    Mockito.when(mockDatabaseSessionFactoryBuilder.buildSessionFactory(Mockito.anyString()))
            .thenReturn(mockSqlSessionFactory);
    SqlSession mockSqlSession = Mockito.mock(SqlSession.class);
    Mockito.when(mockSqlSessionFactory.openSession(ExecutorType.REUSE, mockConnection)).thenReturn(mockSqlSession);
    ExpenseReportMapper mockExpenseReportMapper = Mockito.mock(ExpenseReportMapper.class);
    Mockito.when(mockSqlSession.getMapper(ExpenseReportMapper.class)).thenReturn(mockExpenseReportMapper);
    Mockito.when(mockExpenseReportMapper.getAllExpenseReports()).thenReturn(expectedExpenseReports);

    List<ExpenseReport> actualExpenseReportList = expenseReportReader.getExpenseTransactions();

    SoftAssertions softAssertions = new SoftAssertions();
    softAssertions.assertThat(actualExpenseReportList).isNotNull();
    softAssertions.assertThat(actualExpenseReportList).hasSize(2);
    softAssertions.assertThat(actualExpenseReportList)
            .containsExactlyInAnyOrder(expectedExpenseReport1, expectedExpenseReport2);
    softAssertions.assertAll();
  }

  @Test
  void getExpenseTransaction_bindingException() throws SQLException, IOException {
    Connection mockConnection = Mockito.mock(Connection.class);
    Mockito.when(mockDatabaseConnectable.connect()).thenReturn(mockConnection);
    SqlSessionFactory mockSqlSessionFactory = Mockito.mock(SqlSessionFactory.class);
    Mockito.when(mockDatabaseSessionFactoryBuilder.buildSessionFactory(Mockito.anyString()))
            .thenReturn(mockSqlSessionFactory);
    SqlSession mockSqlSession = Mockito.mock(SqlSession.class);
    Mockito.when(mockSqlSessionFactory.openSession(ExecutorType.REUSE, mockConnection)).thenReturn(mockSqlSession);
    Mockito.when(mockSqlSession.getMapper(ExpenseReportMapper.class))
            .thenThrow(new BindingException("Unable to find ExpenseReportMapper class in mapping registry"));

    assertThatThrownBy(() -> expenseReportReader.getExpenseTransactions())
            .isInstanceOf(BindingException.class)
            .hasMessage("Unable to find ExpenseReportMapper class in mapping registry");
  }
}