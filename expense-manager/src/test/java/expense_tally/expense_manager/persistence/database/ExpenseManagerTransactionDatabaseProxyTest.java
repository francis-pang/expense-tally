package expense_tally.expense_manager.persistence.database;

import expense_tally.expense_manager.persistence.database.mapper.ExpenseManagerTransactionMapper;
import expense_tally.model.persistence.transformation.ExpenseCategory;
import expense_tally.model.persistence.transformation.ExpenseManagerTransaction;
import expense_tally.model.persistence.transformation.ExpenseSubCategory;
import expense_tally.model.persistence.transformation.PaymentMethod;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.binding.BindingException;
import org.apache.ibatis.exceptions.PersistenceException;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
class ExpenseManagerTransactionDatabaseProxyTest {
  @Mock
  private DatabaseConnectable mockDatabaseConnectable;

  @Mock
  private DatabaseSessionFactoryBuilder mockDatabaseSessionFactoryBuilder;

  private static final String TEST_ENVIRONMENT_ID = "testId";

  private ExpenseManagerTransactionDatabaseProxy expenseManagerTransactionDatabaseProxy;

  @BeforeEach
  void setUp() {
    expenseManagerTransactionDatabaseProxy = new ExpenseManagerTransactionDatabaseProxy(
        mockDatabaseConnectable,
        mockDatabaseSessionFactoryBuilder,
        TEST_ENVIRONMENT_ID
    );
  }

  @Test
  void constructor_simpleOkay() {
    assertThat(new ExpenseManagerTransactionDatabaseProxy(
        mockDatabaseConnectable,
        mockDatabaseSessionFactoryBuilder,
        TEST_ENVIRONMENT_ID
    ))
        .isNotNull();
    Mockito.verifyNoInteractions(mockDatabaseConnectable);
    Mockito.verifyNoInteractions(mockDatabaseSessionFactoryBuilder);
  }

  @Test
  void constructor_databaseConnectableIsNull() {
    assertThatThrownBy(() -> new ExpenseManagerTransactionDatabaseProxy(
        null,
        mockDatabaseSessionFactoryBuilder,
        TEST_ENVIRONMENT_ID
    ))
        .isInstanceOf(NullPointerException.class);
    Mockito.verifyNoInteractions(mockDatabaseConnectable);
    Mockito.verifyNoInteractions(mockDatabaseSessionFactoryBuilder);
  }

  @Test
  void constructor_databaseSessionFactoryBuilderIsNull() {
    assertThatThrownBy(() -> new ExpenseManagerTransactionDatabaseProxy(
        mockDatabaseConnectable,
        null,
        TEST_ENVIRONMENT_ID
    ))
        .isInstanceOf(NullPointerException.class);
    Mockito.verifyNoInteractions(mockDatabaseConnectable);
    Mockito.verifyNoInteractions(mockDatabaseSessionFactoryBuilder);
  }

  @Test
  void constructor_environmentIdIsNull() {
    assertThatThrownBy(() -> new ExpenseManagerTransactionDatabaseProxy(
        mockDatabaseConnectable,
        mockDatabaseSessionFactoryBuilder,
        null
    ))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Environment ID cannot be null or empty");
    Mockito.verifyNoInteractions(mockDatabaseConnectable);
    Mockito.verifyNoInteractions(mockDatabaseSessionFactoryBuilder);
  }

  @Test
  void constructor_environmentIdIsBlankString() {
    assertThatThrownBy(() -> new ExpenseManagerTransactionDatabaseProxy(
        mockDatabaseConnectable,
        mockDatabaseSessionFactoryBuilder,
        StringUtils.EMPTY
    ))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Environment ID cannot be null or empty");
    Mockito.verifyNoInteractions(mockDatabaseConnectable);
    Mockito.verifyNoInteractions(mockDatabaseSessionFactoryBuilder);
  }

  @Test
  void constructor_environmentIdIsEmptyString() {
    assertThatThrownBy(() -> new ExpenseManagerTransactionDatabaseProxy(
        mockDatabaseConnectable,
        mockDatabaseSessionFactoryBuilder,
        "       "
    ))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Environment ID cannot be null or empty");
    Mockito.verifyNoInteractions(mockDatabaseConnectable);
    Mockito.verifyNoInteractions(mockDatabaseSessionFactoryBuilder);
  }

  @Test
  void getAllExpenseManagerTransaction_returnOneResult() throws IOException, SQLException {
    ExpenseManagerTransaction expectedExpenseManagerTransaction = ExpenseManagerTransaction.create(
        4867,
        Double.parseDouble("30.0"),
        ExpenseCategory.ENTERTAINMENT,
        ExpenseSubCategory.ALCOHOL_AND_RESTAURANT,
        PaymentMethod.CASH,
        "Dinner. Sbcd Korean tofu house. Millenia walk.",
        Instant.ofEpochMilli(Long.parseLong("1514813160000"))
    );
    Connection mockConnection = Mockito.mock(Connection.class);
    Mockito.when(mockDatabaseConnectable.connect()).thenReturn(mockConnection);
    SqlSessionFactory mockSqlSessionFactory = Mockito.mock(SqlSessionFactory.class);
    Mockito.when(mockDatabaseSessionFactoryBuilder.buildSessionFactory(TEST_ENVIRONMENT_ID))
        .thenReturn(mockSqlSessionFactory);
    SqlSession mockSqlSession = Mockito.mock(SqlSession.class);
    Mockito.when(mockSqlSessionFactory.openSession(ExecutorType.SIMPLE, mockConnection)).thenReturn(mockSqlSession);
    ExpenseManagerTransactionMapper mockExpenseManagerTransactionMapper =
        Mockito.mock(ExpenseManagerTransactionMapper.class);
    Mockito.when(mockSqlSession.getMapper(ExpenseManagerTransactionMapper.class))
        .thenReturn(mockExpenseManagerTransactionMapper);
    ExpenseManagerTransactionMapper.ExpnsMngrTrnsctnMpprIntermediate testExpnsMngrTrnsctnMpprIntermediate =
        new ExpenseManagerTransactionMapper.ExpnsMngrTrnsctnMpprIntermediate(
            4867,
            Double.parseDouble("30.0"),
            "Entertainment",
            "Alcohol/ Restaurant",
            "Cash",
            "Dinner. Sbcd Korean tofu house. Millenia walk.",
            Instant.ofEpochMilli(Long.parseLong("1514813160000")),
            Double.parseDouble("0")
        );
    List<ExpenseManagerTransactionMapper.ExpnsMngrTrnsctnMpprIntermediate> mockExpnsMngrTrnsctnMpprIntermediates = new
        ArrayList<>();
    mockExpnsMngrTrnsctnMpprIntermediates.add(testExpnsMngrTrnsctnMpprIntermediate);
    Mockito.when(mockExpenseManagerTransactionMapper.getAllExpenseManagerTransactions())
        .thenReturn(mockExpnsMngrTrnsctnMpprIntermediates);
    assertThat(expenseManagerTransactionDatabaseProxy.getAllExpenseManagerTransaction())
        .isNotNull()
        .isNotEmpty()
        .containsExactlyInAnyOrder(expectedExpenseManagerTransaction);
  }

  @Test
  void getAllExpenseManagerTransaction_returnZeroResult() throws IOException, SQLException {
    Connection mockConnection = Mockito.mock(Connection.class);
    Mockito.when(mockDatabaseConnectable.connect()).thenReturn(mockConnection);
    SqlSessionFactory mockSqlSessionFactory = Mockito.mock(SqlSessionFactory.class);
    Mockito.when(mockDatabaseSessionFactoryBuilder.buildSessionFactory(TEST_ENVIRONMENT_ID))
        .thenReturn(mockSqlSessionFactory);
    SqlSession mockSqlSession = Mockito.mock(SqlSession.class);
    Mockito.when(mockSqlSessionFactory.openSession(ExecutorType.SIMPLE, mockConnection)).thenReturn(mockSqlSession);
    ExpenseManagerTransactionMapper mockExpenseManagerTransactionMapper =
        Mockito.mock(ExpenseManagerTransactionMapper.class);
    Mockito.when(mockSqlSession.getMapper(ExpenseManagerTransactionMapper.class))
        .thenReturn(mockExpenseManagerTransactionMapper);
    Mockito.when(mockExpenseManagerTransactionMapper.getAllExpenseManagerTransactions())
        .thenReturn(Collections.emptyList());
    assertThat(expenseManagerTransactionDatabaseProxy.getAllExpenseManagerTransaction())
        .isNotNull()
        .isEmpty();
  }

  @Test
  void getAllExpenseManagerTransaction_returnMultipleResults() throws SQLException, IOException {
    ExpenseManagerTransaction expectedExpenseManagerTransaction1 = ExpenseManagerTransaction.create(
        4867,
        Double.parseDouble("30.0"),
        ExpenseCategory.ENTERTAINMENT,
        ExpenseSubCategory.ALCOHOL_AND_RESTAURANT,
        PaymentMethod.CASH,
        "Dinner. Sbcd Korean tofu house. Millenia walk.",
        Instant.ofEpochMilli(Long.parseLong("1514813160000"))
    );
    ExpenseManagerTransaction expectedExpenseManagerTransaction2 = ExpenseManagerTransaction.create(
        4868,
        Double.parseDouble("7"),
        ExpenseCategory.ENTERTAINMENT,
        ExpenseSubCategory.KARAOKE_PARTY,
        PaymentMethod.CASH,
        "Teo Heng. Suntec City.",
        Instant.ofEpochMilli(Long.parseLong("1514813220000"))
    );
    expectedExpenseManagerTransaction2.setReferenceAmount(Double.parseDouble("4.0"));
    ExpenseManagerTransaction expectedExpenseManagerTransaction3 = ExpenseManagerTransaction.create(
        4871,
        Double.parseDouble("6.1"),
        ExpenseCategory.FOOD,
        ExpenseSubCategory.FOOD_COURT_AND_FAST_FOOD,
        PaymentMethod.DBS_DEBIT_CARD,
        "Lunch. Single Bowl Salad. Happy Tummy.",
        Instant.ofEpochMilli(Long.parseLong("1515388500000"))
    );
    Connection mockConnection = Mockito.mock(Connection.class);
    Mockito.when(mockDatabaseConnectable.connect()).thenReturn(mockConnection);
    SqlSessionFactory mockSqlSessionFactory = Mockito.mock(SqlSessionFactory.class);
    Mockito.when(mockDatabaseSessionFactoryBuilder.buildSessionFactory(TEST_ENVIRONMENT_ID))
        .thenReturn(mockSqlSessionFactory);
    SqlSession mockSqlSession = Mockito.mock(SqlSession.class);
    Mockito.when(mockSqlSessionFactory.openSession(ExecutorType.SIMPLE, mockConnection)).thenReturn(mockSqlSession);
    ExpenseManagerTransactionMapper mockExpenseManagerTransactionMapper =
        Mockito.mock(ExpenseManagerTransactionMapper.class);
    Mockito.when(mockSqlSession.getMapper(ExpenseManagerTransactionMapper.class))
        .thenReturn(mockExpenseManagerTransactionMapper);
    ExpenseManagerTransactionMapper.ExpnsMngrTrnsctnMpprIntermediate testExpnsMngrTrnsctnMpprIntermediate1 =
        new ExpenseManagerTransactionMapper.ExpnsMngrTrnsctnMpprIntermediate(
            4867,
            Double.parseDouble("30.0"),
            "Entertainment",
            "Alcohol/ Restaurant",
            "Cash",
            "Dinner. Sbcd Korean tofu house. Millenia walk.",
            Instant.ofEpochMilli(Long.parseLong("1514813160000")),
            Double.parseDouble("0")
        );
    ExpenseManagerTransactionMapper.ExpnsMngrTrnsctnMpprIntermediate testExpnsMngrTrnsctnMpprIntermediate2 =
        new ExpenseManagerTransactionMapper.ExpnsMngrTrnsctnMpprIntermediate(
            4868,
            Double.parseDouble("7"),
            "Entertainment",
            "Karaoke/ Party",
            "Cash",
            "Teo Heng. Suntec City.",
            Instant.ofEpochMilli(Long.parseLong("1514813220000")),
            Double.parseDouble("4.0")
        );
    ExpenseManagerTransactionMapper.ExpnsMngrTrnsctnMpprIntermediate testExpnsMngrTrnsctnMpprIntermediate3 =
        new ExpenseManagerTransactionMapper.ExpnsMngrTrnsctnMpprIntermediate(
            4871,
            Double.parseDouble("6.1"),
            "Food",
            "Food court/ Fast food",
            "Debit 5548-2741-0014-1067",
            "Lunch. Single Bowl Salad. Happy Tummy.",
            Instant.ofEpochMilli(Long.parseLong("1515388500000")),
            Double.parseDouble("0")
        );
    List<ExpenseManagerTransactionMapper.ExpnsMngrTrnsctnMpprIntermediate> mockExpnsMngrTrnsctnMpprIntermediates =
        List.of(
            testExpnsMngrTrnsctnMpprIntermediate1,
            testExpnsMngrTrnsctnMpprIntermediate2,
            testExpnsMngrTrnsctnMpprIntermediate3
        );
    Mockito.when(mockExpenseManagerTransactionMapper.getAllExpenseManagerTransactions())
        .thenReturn(mockExpnsMngrTrnsctnMpprIntermediates);
    assertThat(expenseManagerTransactionDatabaseProxy.getAllExpenseManagerTransaction())
        .isNotNull()
        .isNotEmpty()
        .containsExactlyInAnyOrder(
            expectedExpenseManagerTransaction1,
            expectedExpenseManagerTransaction2,
            expectedExpenseManagerTransaction3
        );
  }

  @Test
  void getAllExpenseManagerTransaction_databaseConnectableConnectException() throws SQLException {
    Mockito.when(mockDatabaseConnectable.connect()).thenThrow(new SQLException("databaseConnectableConnectException"));
    assertThatThrownBy(() -> expenseManagerTransactionDatabaseProxy.getAllExpenseManagerTransaction())
        .isInstanceOf(SQLException.class)
        .hasMessage("databaseConnectableConnectException");
  }


  @Test
  void getAllExpenseManagerTransaction_databaseSessionFactoryBuilderBuildSessionFactoryException() throws SQLException,
      IOException {
    Connection mockConnection = Mockito.mock(Connection.class);
    Mockito.when(mockDatabaseConnectable.connect()).thenReturn(mockConnection);
    Mockito.when(mockDatabaseSessionFactoryBuilder.buildSessionFactory(TEST_ENVIRONMENT_ID))
        .thenThrow(new IOException("databaseSessionFactoryBuilderBuildSessionFactoryException"));
    assertThatThrownBy(() -> expenseManagerTransactionDatabaseProxy.getAllExpenseManagerTransaction())
        .isInstanceOf(IOException.class)
        .hasMessage("databaseSessionFactoryBuilderBuildSessionFactoryException");
  }

  @Test
  void getAllExpenseManagerTransaction_sqlSessionFactoryOpenSessionException() throws SQLException, IOException {
    Connection mockConnection = Mockito.mock(Connection.class);
    Mockito.when(mockDatabaseConnectable.connect()).thenReturn(mockConnection);
    SqlSessionFactory mockSqlSessionFactory = Mockito.mock(SqlSessionFactory.class);
    Mockito.when(mockDatabaseSessionFactoryBuilder.buildSessionFactory(TEST_ENVIRONMENT_ID))
        .thenReturn(mockSqlSessionFactory);
    Mockito.when(mockSqlSessionFactory.openSession(ExecutorType.SIMPLE, mockConnection))
        .thenThrow(new PersistenceException("sqlSessionFactoryOpenSessionException"));
    assertThatThrownBy(() -> expenseManagerTransactionDatabaseProxy.getAllExpenseManagerTransaction())
        .isInstanceOf(PersistenceException.class)
        .hasMessage("sqlSessionFactoryOpenSessionException");
  }

  @Test
  void getAllExpenseManagerTransaction_sqlSessionGetMapperException() throws SQLException, IOException {
    Connection mockConnection = Mockito.mock(Connection.class);
    Mockito.when(mockDatabaseConnectable.connect()).thenReturn(mockConnection);
    SqlSessionFactory mockSqlSessionFactory = Mockito.mock(SqlSessionFactory.class);
    Mockito.when(mockDatabaseSessionFactoryBuilder.buildSessionFactory(TEST_ENVIRONMENT_ID))
        .thenReturn(mockSqlSessionFactory);
    SqlSession mockSqlSession = Mockito.mock(SqlSession.class);
    Mockito.when(mockSqlSessionFactory.openSession(ExecutorType.SIMPLE, mockConnection)).thenReturn(mockSqlSession);
    Mockito.when(mockSqlSession.getMapper(ExpenseManagerTransactionMapper.class))
        .thenThrow(new BindingException("Type ExpenseManagerTransactionMapper is not known to the MapperRegistry."));
    assertThatThrownBy(() -> expenseManagerTransactionDatabaseProxy.getAllExpenseManagerTransaction())
        .isInstanceOf(BindingException.class)
        .hasMessage("Type ExpenseManagerTransactionMapper is not known to the MapperRegistry.");
  }

  @Test
  void getAllExpenseManagerTransaction_expenseManagerTransactionMapperGetAllExpenseManagerTransactionsException()
      throws SQLException, IOException {
    Connection mockConnection = Mockito.mock(Connection.class);
    Mockito.when(mockDatabaseConnectable.connect()).thenReturn(mockConnection);
    SqlSessionFactory mockSqlSessionFactory = Mockito.mock(SqlSessionFactory.class);
    Mockito.when(mockDatabaseSessionFactoryBuilder.buildSessionFactory(TEST_ENVIRONMENT_ID))
        .thenReturn(mockSqlSessionFactory);
    SqlSession mockSqlSession = Mockito.mock(SqlSession.class);
    Mockito.when(mockSqlSessionFactory.openSession(ExecutorType.SIMPLE, mockConnection)).thenReturn(mockSqlSession);
    ExpenseManagerTransactionMapper mockExpenseManagerTransactionMapper =
        Mockito.mock(ExpenseManagerTransactionMapper.class);
    Mockito.when(mockSqlSession.getMapper(ExpenseManagerTransactionMapper.class))
        .thenReturn(mockExpenseManagerTransactionMapper);
    Mockito.when(mockExpenseManagerTransactionMapper.getAllExpenseManagerTransactions())
        .thenThrow(
            new PersistenceException("expenseManagerTransactionMapperGetAllExpenseManagerTransactionsException"));
    assertThatThrownBy(() -> expenseManagerTransactionDatabaseProxy.getAllExpenseManagerTransaction())
        .isInstanceOf(PersistenceException.class)
        .hasMessage("expenseManagerTransactionMapperGetAllExpenseManagerTransactionsException");
  }

  @Test
  void getAllExpenseManagerTransaction_iDIsZero() throws SQLException, IOException {
    Connection mockConnection = Mockito.mock(Connection.class);
    Mockito.when(mockDatabaseConnectable.connect()).thenReturn(mockConnection);
    SqlSessionFactory mockSqlSessionFactory = Mockito.mock(SqlSessionFactory.class);
    Mockito.when(mockDatabaseSessionFactoryBuilder.buildSessionFactory(TEST_ENVIRONMENT_ID))
        .thenReturn(mockSqlSessionFactory);
    SqlSession mockSqlSession = Mockito.mock(SqlSession.class);
    Mockito.when(mockSqlSessionFactory.openSession(ExecutorType.SIMPLE, mockConnection)).thenReturn(mockSqlSession);
    ExpenseManagerTransactionMapper mockExpenseManagerTransactionMapper =
        Mockito.mock(ExpenseManagerTransactionMapper.class);
    Mockito.when(mockSqlSession.getMapper(ExpenseManagerTransactionMapper.class))
        .thenReturn(mockExpenseManagerTransactionMapper);
    ExpenseManagerTransactionMapper.ExpnsMngrTrnsctnMpprIntermediate testExpnsMngrTrnsctnMpprIntermediate =
        new ExpenseManagerTransactionMapper.ExpnsMngrTrnsctnMpprIntermediate(
            0,
            Double.parseDouble("30.0"),
            "Entertainment",
            "Alcohol/ Restaurant",
            "Cash",
            "Dinner. Sbcd Korean tofu house. Millenia walk.",
            Instant.ofEpochMilli(Long.parseLong("1514813160000")),
            Double.parseDouble("0")
        );
    List<ExpenseManagerTransactionMapper.ExpnsMngrTrnsctnMpprIntermediate> mockExpnsMngrTrnsctnMpprIntermediates = new
        ArrayList<>();
    mockExpnsMngrTrnsctnMpprIntermediates.add(testExpnsMngrTrnsctnMpprIntermediate);
    Mockito.when(mockExpenseManagerTransactionMapper.getAllExpenseManagerTransactions())
        .thenReturn(mockExpnsMngrTrnsctnMpprIntermediates);
    assertThat(expenseManagerTransactionDatabaseProxy.getAllExpenseManagerTransaction())
        .isNotNull()
        .isEmpty();
  }

  @Test
  void getAllExpenseManagerTransaction_categoryIsEmpty() throws SQLException, IOException {
    Connection mockConnection = Mockito.mock(Connection.class);
    Mockito.when(mockDatabaseConnectable.connect()).thenReturn(mockConnection);
    SqlSessionFactory mockSqlSessionFactory = Mockito.mock(SqlSessionFactory.class);
    Mockito.when(mockDatabaseSessionFactoryBuilder.buildSessionFactory(TEST_ENVIRONMENT_ID))
        .thenReturn(mockSqlSessionFactory);
    SqlSession mockSqlSession = Mockito.mock(SqlSession.class);
    Mockito.when(mockSqlSessionFactory.openSession(ExecutorType.SIMPLE, mockConnection)).thenReturn(mockSqlSession);
    ExpenseManagerTransactionMapper mockExpenseManagerTransactionMapper =
        Mockito.mock(ExpenseManagerTransactionMapper.class);
    Mockito.when(mockSqlSession.getMapper(ExpenseManagerTransactionMapper.class))
        .thenReturn(mockExpenseManagerTransactionMapper);
    ExpenseManagerTransactionMapper.ExpnsMngrTrnsctnMpprIntermediate testExpnsMngrTrnsctnMpprIntermediate =
        new ExpenseManagerTransactionMapper.ExpnsMngrTrnsctnMpprIntermediate(
            4867,
            Double.parseDouble("30.0"),
            StringUtils.EMPTY,
            "Alcohol/ Restaurant",
            "Cash",
            "Dinner. Sbcd Korean tofu house. Millenia walk.",
            Instant.ofEpochMilli(Long.parseLong("1514813160000")),
            Double.parseDouble("0")
        );
    List<ExpenseManagerTransactionMapper.ExpnsMngrTrnsctnMpprIntermediate> mockExpnsMngrTrnsctnMpprIntermediates = new
        ArrayList<>();
    mockExpnsMngrTrnsctnMpprIntermediates.add(testExpnsMngrTrnsctnMpprIntermediate);
    Mockito.when(mockExpenseManagerTransactionMapper.getAllExpenseManagerTransactions())
        .thenReturn(mockExpnsMngrTrnsctnMpprIntermediates);
    assertThat(expenseManagerTransactionDatabaseProxy.getAllExpenseManagerTransaction())
        .isNotNull()
        .isEmpty();
  }

  @Test
  void getAllExpenseManagerTransaction_categoryIsNull() throws SQLException, IOException {
    Connection mockConnection = Mockito.mock(Connection.class);
    Mockito.when(mockDatabaseConnectable.connect()).thenReturn(mockConnection);
    SqlSessionFactory mockSqlSessionFactory = Mockito.mock(SqlSessionFactory.class);
    Mockito.when(mockDatabaseSessionFactoryBuilder.buildSessionFactory(TEST_ENVIRONMENT_ID))
        .thenReturn(mockSqlSessionFactory);
    SqlSession mockSqlSession = Mockito.mock(SqlSession.class);
    Mockito.when(mockSqlSessionFactory.openSession(ExecutorType.SIMPLE, mockConnection)).thenReturn(mockSqlSession);
    ExpenseManagerTransactionMapper mockExpenseManagerTransactionMapper =
        Mockito.mock(ExpenseManagerTransactionMapper.class);
    Mockito.when(mockSqlSession.getMapper(ExpenseManagerTransactionMapper.class))
        .thenReturn(mockExpenseManagerTransactionMapper);
    ExpenseManagerTransactionMapper.ExpnsMngrTrnsctnMpprIntermediate testExpnsMngrTrnsctnMpprIntermediate =
        new ExpenseManagerTransactionMapper.ExpnsMngrTrnsctnMpprIntermediate(
            4867,
            Double.parseDouble("30.0"),
            null,
            "Alcohol/ Restaurant",
            "Cash",
            "Dinner. Sbcd Korean tofu house. Millenia walk.",
            Instant.ofEpochMilli(Long.parseLong("1514813160000")),
            Double.parseDouble("0")
        );
    List<ExpenseManagerTransactionMapper.ExpnsMngrTrnsctnMpprIntermediate> mockExpnsMngrTrnsctnMpprIntermediates = new
        ArrayList<>();
    mockExpnsMngrTrnsctnMpprIntermediates.add(testExpnsMngrTrnsctnMpprIntermediate);
    Mockito.when(mockExpenseManagerTransactionMapper.getAllExpenseManagerTransactions())
        .thenReturn(mockExpnsMngrTrnsctnMpprIntermediates);
    assertThat(expenseManagerTransactionDatabaseProxy.getAllExpenseManagerTransaction())
        .isNotNull()
        .isEmpty();
  }

  @Test
  void getAllExpenseManagerTransaction_categoryIsBlank() throws SQLException, IOException {
    Connection mockConnection = Mockito.mock(Connection.class);
    Mockito.when(mockDatabaseConnectable.connect()).thenReturn(mockConnection);
    SqlSessionFactory mockSqlSessionFactory = Mockito.mock(SqlSessionFactory.class);
    Mockito.when(mockDatabaseSessionFactoryBuilder.buildSessionFactory(TEST_ENVIRONMENT_ID))
        .thenReturn(mockSqlSessionFactory);
    SqlSession mockSqlSession = Mockito.mock(SqlSession.class);
    Mockito.when(mockSqlSessionFactory.openSession(ExecutorType.SIMPLE, mockConnection)).thenReturn(mockSqlSession);
    ExpenseManagerTransactionMapper mockExpenseManagerTransactionMapper =
        Mockito.mock(ExpenseManagerTransactionMapper.class);
    Mockito.when(mockSqlSession.getMapper(ExpenseManagerTransactionMapper.class))
        .thenReturn(mockExpenseManagerTransactionMapper);
    ExpenseManagerTransactionMapper.ExpnsMngrTrnsctnMpprIntermediate testExpnsMngrTrnsctnMpprIntermediate =
        new ExpenseManagerTransactionMapper.ExpnsMngrTrnsctnMpprIntermediate(
            4867,
            Double.parseDouble("30.0"),
            "    ",
            "Alcohol/ Restaurant",
            "Cash",
            "Dinner. Sbcd Korean tofu house. Millenia walk.",
            Instant.ofEpochMilli(Long.parseLong("1514813160000")),
            Double.parseDouble("0")
        );
    List<ExpenseManagerTransactionMapper.ExpnsMngrTrnsctnMpprIntermediate> mockExpnsMngrTrnsctnMpprIntermediates = new
        ArrayList<>();
    mockExpnsMngrTrnsctnMpprIntermediates.add(testExpnsMngrTrnsctnMpprIntermediate);
    Mockito.when(mockExpenseManagerTransactionMapper.getAllExpenseManagerTransactions())
        .thenReturn(mockExpnsMngrTrnsctnMpprIntermediates);
    assertThat(expenseManagerTransactionDatabaseProxy.getAllExpenseManagerTransaction())
        .isNotNull()
        .isEmpty();
  }

  @Test
  void getAllExpenseManagerTransaction_subcategoryIsEmpty() throws SQLException, IOException {
    Connection mockConnection = Mockito.mock(Connection.class);
    Mockito.when(mockDatabaseConnectable.connect()).thenReturn(mockConnection);
    SqlSessionFactory mockSqlSessionFactory = Mockito.mock(SqlSessionFactory.class);
    Mockito.when(mockDatabaseSessionFactoryBuilder.buildSessionFactory(TEST_ENVIRONMENT_ID))
        .thenReturn(mockSqlSessionFactory);
    SqlSession mockSqlSession = Mockito.mock(SqlSession.class);
    Mockito.when(mockSqlSessionFactory.openSession(ExecutorType.SIMPLE, mockConnection)).thenReturn(mockSqlSession);
    ExpenseManagerTransactionMapper mockExpenseManagerTransactionMapper =
        Mockito.mock(ExpenseManagerTransactionMapper.class);
    Mockito.when(mockSqlSession.getMapper(ExpenseManagerTransactionMapper.class))
        .thenReturn(mockExpenseManagerTransactionMapper);
    ExpenseManagerTransactionMapper.ExpnsMngrTrnsctnMpprIntermediate testExpnsMngrTrnsctnMpprIntermediate =
        new ExpenseManagerTransactionMapper.ExpnsMngrTrnsctnMpprIntermediate(
            4867,
            Double.parseDouble("30.0"),
            "Entertainment",
            StringUtils.EMPTY,
            "Cash",
            "Dinner. Sbcd Korean tofu house. Millenia walk.",
            Instant.ofEpochMilli(Long.parseLong("1514813160000")),
            Double.parseDouble("0")
        );
    List<ExpenseManagerTransactionMapper.ExpnsMngrTrnsctnMpprIntermediate> mockExpnsMngrTrnsctnMpprIntermediates = new
        ArrayList<>();
    mockExpnsMngrTrnsctnMpprIntermediates.add(testExpnsMngrTrnsctnMpprIntermediate);
    Mockito.when(mockExpenseManagerTransactionMapper.getAllExpenseManagerTransactions())
        .thenReturn(mockExpnsMngrTrnsctnMpprIntermediates);
    assertThat(expenseManagerTransactionDatabaseProxy.getAllExpenseManagerTransaction())
        .isNotNull()
        .isEmpty();
  }

  @Test
  void getAllExpenseManagerTransaction_subcategoryIsNull() throws SQLException, IOException {
    Connection mockConnection = Mockito.mock(Connection.class);
    Mockito.when(mockDatabaseConnectable.connect()).thenReturn(mockConnection);
    SqlSessionFactory mockSqlSessionFactory = Mockito.mock(SqlSessionFactory.class);
    Mockito.when(mockDatabaseSessionFactoryBuilder.buildSessionFactory(TEST_ENVIRONMENT_ID))
        .thenReturn(mockSqlSessionFactory);
    SqlSession mockSqlSession = Mockito.mock(SqlSession.class);
    Mockito.when(mockSqlSessionFactory.openSession(ExecutorType.SIMPLE, mockConnection)).thenReturn(mockSqlSession);
    ExpenseManagerTransactionMapper mockExpenseManagerTransactionMapper =
        Mockito.mock(ExpenseManagerTransactionMapper.class);
    Mockito.when(mockSqlSession.getMapper(ExpenseManagerTransactionMapper.class))
        .thenReturn(mockExpenseManagerTransactionMapper);
    ExpenseManagerTransactionMapper.ExpnsMngrTrnsctnMpprIntermediate testExpnsMngrTrnsctnMpprIntermediate =
        new ExpenseManagerTransactionMapper.ExpnsMngrTrnsctnMpprIntermediate(
            4867,
            Double.parseDouble("30.0"),
            "Entertainment",
            null,
            "Cash",
            "Dinner. Sbcd Korean tofu house. Millenia walk.",
            Instant.ofEpochMilli(Long.parseLong("1514813160000")),
            Double.parseDouble("0")
        );
    List<ExpenseManagerTransactionMapper.ExpnsMngrTrnsctnMpprIntermediate> mockExpnsMngrTrnsctnMpprIntermediates = new
        ArrayList<>();
    mockExpnsMngrTrnsctnMpprIntermediates.add(testExpnsMngrTrnsctnMpprIntermediate);
    Mockito.when(mockExpenseManagerTransactionMapper.getAllExpenseManagerTransactions())
        .thenReturn(mockExpnsMngrTrnsctnMpprIntermediates);
    assertThat(expenseManagerTransactionDatabaseProxy.getAllExpenseManagerTransaction())
        .isNotNull()
        .isEmpty();
  }

  @Test
  void getAllExpenseManagerTransaction_subcategoryIsBlank() throws SQLException, IOException {
    Connection mockConnection = Mockito.mock(Connection.class);
    Mockito.when(mockDatabaseConnectable.connect()).thenReturn(mockConnection);
    SqlSessionFactory mockSqlSessionFactory = Mockito.mock(SqlSessionFactory.class);
    Mockito.when(mockDatabaseSessionFactoryBuilder.buildSessionFactory(TEST_ENVIRONMENT_ID))
        .thenReturn(mockSqlSessionFactory);
    SqlSession mockSqlSession = Mockito.mock(SqlSession.class);
    Mockito.when(mockSqlSessionFactory.openSession(ExecutorType.SIMPLE, mockConnection)).thenReturn(mockSqlSession);
    ExpenseManagerTransactionMapper mockExpenseManagerTransactionMapper =
        Mockito.mock(ExpenseManagerTransactionMapper.class);
    Mockito.when(mockSqlSession.getMapper(ExpenseManagerTransactionMapper.class))
        .thenReturn(mockExpenseManagerTransactionMapper);
    ExpenseManagerTransactionMapper.ExpnsMngrTrnsctnMpprIntermediate testExpnsMngrTrnsctnMpprIntermediate =
        new ExpenseManagerTransactionMapper.ExpnsMngrTrnsctnMpprIntermediate(
            4867,
            Double.parseDouble("30.0"),
            "Entertainment",
            "    ",
            "Cash",
            "Dinner. Sbcd Korean tofu house. Millenia walk.",
            Instant.ofEpochMilli(Long.parseLong("1514813160000")),
            Double.parseDouble("0")
        );
    List<ExpenseManagerTransactionMapper.ExpnsMngrTrnsctnMpprIntermediate> mockExpnsMngrTrnsctnMpprIntermediates = new
        ArrayList<>();
    mockExpnsMngrTrnsctnMpprIntermediates.add(testExpnsMngrTrnsctnMpprIntermediate);
    Mockito.when(mockExpenseManagerTransactionMapper.getAllExpenseManagerTransactions())
        .thenReturn(mockExpnsMngrTrnsctnMpprIntermediates);
    assertThat(expenseManagerTransactionDatabaseProxy.getAllExpenseManagerTransaction())
        .isNotNull()
        .isEmpty();
  }

  @Test
  void getAllExpenseManagerTransaction_paymentMethodIsEmpty() throws SQLException, IOException {
    Connection mockConnection = Mockito.mock(Connection.class);
    Mockito.when(mockDatabaseConnectable.connect()).thenReturn(mockConnection);
    SqlSessionFactory mockSqlSessionFactory = Mockito.mock(SqlSessionFactory.class);
    Mockito.when(mockDatabaseSessionFactoryBuilder.buildSessionFactory(TEST_ENVIRONMENT_ID))
        .thenReturn(mockSqlSessionFactory);
    SqlSession mockSqlSession = Mockito.mock(SqlSession.class);
    Mockito.when(mockSqlSessionFactory.openSession(ExecutorType.SIMPLE, mockConnection)).thenReturn(mockSqlSession);
    ExpenseManagerTransactionMapper mockExpenseManagerTransactionMapper =
        Mockito.mock(ExpenseManagerTransactionMapper.class);
    Mockito.when(mockSqlSession.getMapper(ExpenseManagerTransactionMapper.class))
        .thenReturn(mockExpenseManagerTransactionMapper);
    ExpenseManagerTransactionMapper.ExpnsMngrTrnsctnMpprIntermediate testExpnsMngrTrnsctnMpprIntermediate =
        new ExpenseManagerTransactionMapper.ExpnsMngrTrnsctnMpprIntermediate(
            4867,
            Double.parseDouble("30.0"),
            "Entertainment",
            "Alcohol/ Restaurant",
            StringUtils.EMPTY,
            "Dinner. Sbcd Korean tofu house. Millenia walk.",
            Instant.ofEpochMilli(Long.parseLong("1514813160000")),
            Double.parseDouble("0")
        );
    List<ExpenseManagerTransactionMapper.ExpnsMngrTrnsctnMpprIntermediate> mockExpnsMngrTrnsctnMpprIntermediates = new
        ArrayList<>();
    mockExpnsMngrTrnsctnMpprIntermediates.add(testExpnsMngrTrnsctnMpprIntermediate);
    Mockito.when(mockExpenseManagerTransactionMapper.getAllExpenseManagerTransactions())
        .thenReturn(mockExpnsMngrTrnsctnMpprIntermediates);
    assertThat(expenseManagerTransactionDatabaseProxy.getAllExpenseManagerTransaction())
        .isNotNull()
        .isEmpty();
  }

  @Test
  void getAllExpenseManagerTransaction_paymentMethodIsNull() throws SQLException, IOException {
    Connection mockConnection = Mockito.mock(Connection.class);
    Mockito.when(mockDatabaseConnectable.connect()).thenReturn(mockConnection);
    SqlSessionFactory mockSqlSessionFactory = Mockito.mock(SqlSessionFactory.class);
    Mockito.when(mockDatabaseSessionFactoryBuilder.buildSessionFactory(TEST_ENVIRONMENT_ID))
        .thenReturn(mockSqlSessionFactory);
    SqlSession mockSqlSession = Mockito.mock(SqlSession.class);
    Mockito.when(mockSqlSessionFactory.openSession(ExecutorType.SIMPLE, mockConnection)).thenReturn(mockSqlSession);
    ExpenseManagerTransactionMapper mockExpenseManagerTransactionMapper =
        Mockito.mock(ExpenseManagerTransactionMapper.class);
    Mockito.when(mockSqlSession.getMapper(ExpenseManagerTransactionMapper.class))
        .thenReturn(mockExpenseManagerTransactionMapper);
    ExpenseManagerTransactionMapper.ExpnsMngrTrnsctnMpprIntermediate testExpnsMngrTrnsctnMpprIntermediate =
        new ExpenseManagerTransactionMapper.ExpnsMngrTrnsctnMpprIntermediate(
            4867,
            Double.parseDouble("30.0"),
            "Entertainment",
            "Alcohol/ Restaurant",
            null,
            "Dinner. Sbcd Korean tofu house. Millenia walk.",
            Instant.ofEpochMilli(Long.parseLong("1514813160000")),
            Double.parseDouble("0")
        );
    List<ExpenseManagerTransactionMapper.ExpnsMngrTrnsctnMpprIntermediate> mockExpnsMngrTrnsctnMpprIntermediates = new
        ArrayList<>();
    mockExpnsMngrTrnsctnMpprIntermediates.add(testExpnsMngrTrnsctnMpprIntermediate);
    Mockito.when(mockExpenseManagerTransactionMapper.getAllExpenseManagerTransactions())
        .thenReturn(mockExpnsMngrTrnsctnMpprIntermediates);
    assertThat(expenseManagerTransactionDatabaseProxy.getAllExpenseManagerTransaction())
        .isNotNull()
        .isEmpty();
  }

  @Test
  void getAllExpenseManagerTransaction_paymentMethodIsBlank() throws IOException, SQLException {
    Connection mockConnection = Mockito.mock(Connection.class);
    Mockito.when(mockDatabaseConnectable.connect()).thenReturn(mockConnection);
    SqlSessionFactory mockSqlSessionFactory = Mockito.mock(SqlSessionFactory.class);
    Mockito.when(mockDatabaseSessionFactoryBuilder.buildSessionFactory(TEST_ENVIRONMENT_ID))
        .thenReturn(mockSqlSessionFactory);
    SqlSession mockSqlSession = Mockito.mock(SqlSession.class);
    Mockito.when(mockSqlSessionFactory.openSession(ExecutorType.SIMPLE, mockConnection)).thenReturn(mockSqlSession);
    ExpenseManagerTransactionMapper mockExpenseManagerTransactionMapper =
        Mockito.mock(ExpenseManagerTransactionMapper.class);
    Mockito.when(mockSqlSession.getMapper(ExpenseManagerTransactionMapper.class))
        .thenReturn(mockExpenseManagerTransactionMapper);
    ExpenseManagerTransactionMapper.ExpnsMngrTrnsctnMpprIntermediate testExpnsMngrTrnsctnMpprIntermediate =
        new ExpenseManagerTransactionMapper.ExpnsMngrTrnsctnMpprIntermediate(
            4867,
            Double.parseDouble("30.0"),
            "Entertainment",
            "Alcohol/ Restaurant",
            " ",
            "Dinner. Sbcd Korean tofu house. Millenia walk.",
            Instant.ofEpochMilli(Long.parseLong("1514813160000")),
            Double.parseDouble("0")
        );
    List<ExpenseManagerTransactionMapper.ExpnsMngrTrnsctnMpprIntermediate> mockExpnsMngrTrnsctnMpprIntermediates = new
        ArrayList<>();
    mockExpnsMngrTrnsctnMpprIntermediates.add(testExpnsMngrTrnsctnMpprIntermediate);
    Mockito.when(mockExpenseManagerTransactionMapper.getAllExpenseManagerTransactions())
        .thenReturn(mockExpnsMngrTrnsctnMpprIntermediates);
    assertThat(expenseManagerTransactionDatabaseProxy.getAllExpenseManagerTransaction())
        .isNotNull()
        .isEmpty();
  }

  @Test
  void getAllExpenseManagerTransaction_descriptionIsEmpty() throws SQLException, IOException {
    Connection mockConnection = Mockito.mock(Connection.class);
    Mockito.when(mockDatabaseConnectable.connect()).thenReturn(mockConnection);
    SqlSessionFactory mockSqlSessionFactory = Mockito.mock(SqlSessionFactory.class);
    Mockito.when(mockDatabaseSessionFactoryBuilder.buildSessionFactory(TEST_ENVIRONMENT_ID))
        .thenReturn(mockSqlSessionFactory);
    SqlSession mockSqlSession = Mockito.mock(SqlSession.class);
    Mockito.when(mockSqlSessionFactory.openSession(ExecutorType.SIMPLE, mockConnection)).thenReturn(mockSqlSession);
    ExpenseManagerTransactionMapper mockExpenseManagerTransactionMapper =
        Mockito.mock(ExpenseManagerTransactionMapper.class);
    Mockito.when(mockSqlSession.getMapper(ExpenseManagerTransactionMapper.class))
        .thenReturn(mockExpenseManagerTransactionMapper);
    ExpenseManagerTransactionMapper.ExpnsMngrTrnsctnMpprIntermediate testExpnsMngrTrnsctnMpprIntermediate =
        new ExpenseManagerTransactionMapper.ExpnsMngrTrnsctnMpprIntermediate(
            4867,
            Double.parseDouble("30.0"),
            "Entertainment",
            "Alcohol/ Restaurant",
            "Cash",
            StringUtils.EMPTY,
            Instant.ofEpochMilli(Long.parseLong("1514813160000")),
            Double.parseDouble("0")
        );
    List<ExpenseManagerTransactionMapper.ExpnsMngrTrnsctnMpprIntermediate> mockExpnsMngrTrnsctnMpprIntermediates = new
        ArrayList<>();
    mockExpnsMngrTrnsctnMpprIntermediates.add(testExpnsMngrTrnsctnMpprIntermediate);
    Mockito.when(mockExpenseManagerTransactionMapper.getAllExpenseManagerTransactions())
        .thenReturn(mockExpnsMngrTrnsctnMpprIntermediates);
    assertThat(expenseManagerTransactionDatabaseProxy.getAllExpenseManagerTransaction())
        .isNotNull()
        .isEmpty();
  }

  @Test
  void getAllExpenseManagerTransaction_descriptionIsNull() throws SQLException, IOException {
    Connection mockConnection = Mockito.mock(Connection.class);
    Mockito.when(mockDatabaseConnectable.connect()).thenReturn(mockConnection);
    SqlSessionFactory mockSqlSessionFactory = Mockito.mock(SqlSessionFactory.class);
    Mockito.when(mockDatabaseSessionFactoryBuilder.buildSessionFactory(TEST_ENVIRONMENT_ID))
        .thenReturn(mockSqlSessionFactory);
    SqlSession mockSqlSession = Mockito.mock(SqlSession.class);
    Mockito.when(mockSqlSessionFactory.openSession(ExecutorType.SIMPLE, mockConnection)).thenReturn(mockSqlSession);
    ExpenseManagerTransactionMapper mockExpenseManagerTransactionMapper =
        Mockito.mock(ExpenseManagerTransactionMapper.class);
    Mockito.when(mockSqlSession.getMapper(ExpenseManagerTransactionMapper.class))
        .thenReturn(mockExpenseManagerTransactionMapper);
    ExpenseManagerTransactionMapper.ExpnsMngrTrnsctnMpprIntermediate testExpnsMngrTrnsctnMpprIntermediate =
        new ExpenseManagerTransactionMapper.ExpnsMngrTrnsctnMpprIntermediate(
            4867,
            Double.parseDouble("30.0"),
            "Entertainment",
            "Alcohol/ Restaurant",
            "Cash",
            null,
            Instant.ofEpochMilli(Long.parseLong("1514813160000")),
            Double.parseDouble("0")
        );
    List<ExpenseManagerTransactionMapper.ExpnsMngrTrnsctnMpprIntermediate> mockExpnsMngrTrnsctnMpprIntermediates = new
        ArrayList<>();
    mockExpnsMngrTrnsctnMpprIntermediates.add(testExpnsMngrTrnsctnMpprIntermediate);
    Mockito.when(mockExpenseManagerTransactionMapper.getAllExpenseManagerTransactions())
        .thenReturn(mockExpnsMngrTrnsctnMpprIntermediates);
    assertThat(expenseManagerTransactionDatabaseProxy.getAllExpenseManagerTransaction())
        .isNotNull()
        .isEmpty();
  }

  @Test
  void getAllExpenseManagerTransaction_descriptionIsBlank() throws IOException, SQLException {
    Connection mockConnection = Mockito.mock(Connection.class);
    Mockito.when(mockDatabaseConnectable.connect()).thenReturn(mockConnection);
    SqlSessionFactory mockSqlSessionFactory = Mockito.mock(SqlSessionFactory.class);
    Mockito.when(mockDatabaseSessionFactoryBuilder.buildSessionFactory(TEST_ENVIRONMENT_ID))
        .thenReturn(mockSqlSessionFactory);
    SqlSession mockSqlSession = Mockito.mock(SqlSession.class);
    Mockito.when(mockSqlSessionFactory.openSession(ExecutorType.SIMPLE, mockConnection)).thenReturn(mockSqlSession);
    ExpenseManagerTransactionMapper mockExpenseManagerTransactionMapper =
        Mockito.mock(ExpenseManagerTransactionMapper.class);
    Mockito.when(mockSqlSession.getMapper(ExpenseManagerTransactionMapper.class))
        .thenReturn(mockExpenseManagerTransactionMapper);
    ExpenseManagerTransactionMapper.ExpnsMngrTrnsctnMpprIntermediate testExpnsMngrTrnsctnMpprIntermediate =
        new ExpenseManagerTransactionMapper.ExpnsMngrTrnsctnMpprIntermediate(
            4867,
            Double.parseDouble("30.0"),
            "Entertainment",
            "Alcohol/ Restaurant",
            "Cash",
            "   ",
            Instant.ofEpochMilli(Long.parseLong("1514813160000")),
            Double.parseDouble("0")
        );
    List<ExpenseManagerTransactionMapper.ExpnsMngrTrnsctnMpprIntermediate> mockExpnsMngrTrnsctnMpprIntermediates = new
        ArrayList<>();
    mockExpnsMngrTrnsctnMpprIntermediates.add(testExpnsMngrTrnsctnMpprIntermediate);
    Mockito.when(mockExpenseManagerTransactionMapper.getAllExpenseManagerTransactions())
        .thenReturn(mockExpnsMngrTrnsctnMpprIntermediates);
    assertThat(expenseManagerTransactionDatabaseProxy.getAllExpenseManagerTransaction())
        .isNotNull()
        .isEmpty();
  }

  @Test
  void getAllExpenseManagerTransaction_expensedTimeIsNull() throws IOException, SQLException {
    Connection mockConnection = Mockito.mock(Connection.class);
    Mockito.when(mockDatabaseConnectable.connect()).thenReturn(mockConnection);
    SqlSessionFactory mockSqlSessionFactory = Mockito.mock(SqlSessionFactory.class);
    Mockito.when(mockDatabaseSessionFactoryBuilder.buildSessionFactory(TEST_ENVIRONMENT_ID))
        .thenReturn(mockSqlSessionFactory);
    SqlSession mockSqlSession = Mockito.mock(SqlSession.class);
    Mockito.when(mockSqlSessionFactory.openSession(ExecutorType.SIMPLE, mockConnection)).thenReturn(mockSqlSession);
    ExpenseManagerTransactionMapper mockExpenseManagerTransactionMapper =
        Mockito.mock(ExpenseManagerTransactionMapper.class);
    Mockito.when(mockSqlSession.getMapper(ExpenseManagerTransactionMapper.class))
        .thenReturn(mockExpenseManagerTransactionMapper);
    ExpenseManagerTransactionMapper.ExpnsMngrTrnsctnMpprIntermediate testExpnsMngrTrnsctnMpprIntermediate =
        new ExpenseManagerTransactionMapper.ExpnsMngrTrnsctnMpprIntermediate(
            4867,
            Double.parseDouble("30.0"),
            "Entertainment",
            "Alcohol/ Restaurant",
            "Cash",
            "Dinner. Sbcd Korean tofu house. Millenia walk.",
            null,
            Double.parseDouble("0")
        );
    List<ExpenseManagerTransactionMapper.ExpnsMngrTrnsctnMpprIntermediate> mockExpnsMngrTrnsctnMpprIntermediates = new
        ArrayList<>();
    mockExpnsMngrTrnsctnMpprIntermediates.add(testExpnsMngrTrnsctnMpprIntermediate);
    Mockito.when(mockExpenseManagerTransactionMapper.getAllExpenseManagerTransactions())
        .thenReturn(mockExpnsMngrTrnsctnMpprIntermediates);
    assertThat(expenseManagerTransactionDatabaseProxy.getAllExpenseManagerTransaction())
        .isNotNull()
        .isEmpty();
  }

  @Test
  void add_succeed() throws SQLException, IOException {
    Connection mockConnection = Mockito.mock(Connection.class);
    Mockito.when(mockDatabaseConnectable.connect()).thenReturn(mockConnection);
    SqlSessionFactory mockSqlSessionFactory = Mockito.mock(SqlSessionFactory.class);
    Mockito.when(mockDatabaseSessionFactoryBuilder.buildSessionFactory(TEST_ENVIRONMENT_ID))
        .thenReturn(mockSqlSessionFactory);
    SqlSession mockSqlSession = Mockito.mock(SqlSession.class);
    Mockito.when(mockSqlSessionFactory.openSession(ExecutorType.SIMPLE, mockConnection)).thenReturn(mockSqlSession);
    ExpenseManagerTransactionMapper mockExpenseManagerTransactionMapper =
        Mockito.mock(ExpenseManagerTransactionMapper.class);
    Mockito.when(mockSqlSession.getMapper(ExpenseManagerTransactionMapper.class))
        .thenReturn(mockExpenseManagerTransactionMapper);
    Mockito.when(mockExpenseManagerTransactionMapper.addExpenseManagerTransaction(
        4867,
        Double.parseDouble("30.0"),
        "Entertainment",
        "Alcohol/ Restaurant",
        "Cash",
        "Dinner. Sbcd Korean tofu house. Millenia walk.",
        Instant.ofEpochMilli(Long.parseLong("1514813160000")),
        Double.parseDouble("0")
    )).thenReturn(1);
    ExpenseManagerTransaction testExpenseManagerTransaction = ExpenseManagerTransaction.create(
        4867,
        Double.parseDouble("30.0"),
        ExpenseCategory.ENTERTAINMENT,
        ExpenseSubCategory.ALCOHOL_AND_RESTAURANT,
        PaymentMethod.CASH,
        "Dinner. Sbcd Korean tofu house. Millenia walk.",
        Instant.ofEpochMilli(Long.parseLong("1514813160000"))
    );
    assertThat(expenseManagerTransactionDatabaseProxy.add(testExpenseManagerTransaction))
        .isTrue();
  }

  @Test
  void add_fail() throws IOException, SQLException {
    Connection mockConnection = Mockito.mock(Connection.class);
    Mockito.when(mockDatabaseConnectable.connect()).thenReturn(mockConnection);
    SqlSessionFactory mockSqlSessionFactory = Mockito.mock(SqlSessionFactory.class);
    Mockito.when(mockDatabaseSessionFactoryBuilder.buildSessionFactory(TEST_ENVIRONMENT_ID))
        .thenReturn(mockSqlSessionFactory);
    SqlSession mockSqlSession = Mockito.mock(SqlSession.class);
    Mockito.when(mockSqlSessionFactory.openSession(ExecutorType.SIMPLE, mockConnection)).thenReturn(mockSqlSession);
    ExpenseManagerTransactionMapper mockExpenseManagerTransactionMapper =
        Mockito.mock(ExpenseManagerTransactionMapper.class);
    Mockito.when(mockSqlSession.getMapper(ExpenseManagerTransactionMapper.class))
        .thenReturn(mockExpenseManagerTransactionMapper);
    Mockito.when(mockExpenseManagerTransactionMapper.addExpenseManagerTransaction(
        4867,
        Double.parseDouble("30.0"),
        "Entertainment",
        "Alcohol/ Restaurant",
        "Cash",
        "Dinner. Sbcd Korean tofu house. Millenia walk.",
        Instant.ofEpochMilli(Long.parseLong("1514813160000")),
        Double.parseDouble("0")
    )).thenReturn(0);
    ExpenseManagerTransaction testExpenseManagerTransaction = ExpenseManagerTransaction.create(
        4867,
        Double.parseDouble("30.0"),
        ExpenseCategory.ENTERTAINMENT,
        ExpenseSubCategory.ALCOHOL_AND_RESTAURANT,
        PaymentMethod.CASH,
        "Dinner. Sbcd Korean tofu house. Millenia walk.",
        Instant.ofEpochMilli(Long.parseLong("1514813160000"))
    );
    assertThat(expenseManagerTransactionDatabaseProxy.add(testExpenseManagerTransaction))
        .isFalse();
  }

  @Test
  void add_idIsZero() {
    ExpenseManagerTransaction mockExpenseManagerTransaction = Mockito.mock(ExpenseManagerTransaction.class);
    Mockito.when(mockExpenseManagerTransaction.getId()).thenReturn(Long.parseLong("0"));
    assertThatThrownBy(() -> expenseManagerTransactionDatabaseProxy.add(mockExpenseManagerTransaction))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("ID cannot be non positive.");
  }

  @Test
  void add_categoryIsEmpty() {
    ExpenseManagerTransaction mockExpenseManagerTransaction = Mockito.mock(ExpenseManagerTransaction.class);
    Mockito.when(mockExpenseManagerTransaction.getId()).thenReturn(Long.parseLong("4867"));
    ExpenseCategory mockExpenseCategory = Mockito.mock(ExpenseCategory.class);
    Mockito.when(mockExpenseManagerTransaction.getCategory()).thenReturn(mockExpenseCategory);
    Mockito.when(mockExpenseCategory.value()).thenReturn(StringUtils.EMPTY);
    assertThatThrownBy(() -> expenseManagerTransactionDatabaseProxy.add(mockExpenseManagerTransaction))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Category cannot be null or empty.");
  }

  @Test
  void add_categoryIsNull() {
    ExpenseManagerTransaction mockExpenseManagerTransaction = Mockito.mock(ExpenseManagerTransaction.class);
    Mockito.when(mockExpenseManagerTransaction.getId()).thenReturn(Long.parseLong("4867"));
    assertThatThrownBy(() -> expenseManagerTransactionDatabaseProxy.add(mockExpenseManagerTransaction))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Category cannot be null.");
  }

  @Test
  void add_categoryIsBlank() {
    ExpenseManagerTransaction mockExpenseManagerTransaction = Mockito.mock(ExpenseManagerTransaction.class);
    Mockito.when(mockExpenseManagerTransaction.getId()).thenReturn(Long.parseLong("4867"));
    ExpenseCategory mockExpenseCategory = Mockito.mock(ExpenseCategory.class);
    Mockito.when(mockExpenseManagerTransaction.getCategory()).thenReturn(mockExpenseCategory);
    Mockito.when(mockExpenseCategory.value()).thenReturn("   ");
    assertThatThrownBy(() -> expenseManagerTransactionDatabaseProxy.add(mockExpenseManagerTransaction))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Category cannot be null or empty.");
  }

  @Test
  void add_subcategoryIsEmpty() {
    ExpenseManagerTransaction mockExpenseManagerTransaction = Mockito.mock(ExpenseManagerTransaction.class);
    Mockito.when(mockExpenseManagerTransaction.getId()).thenReturn(Long.parseLong("4867"));
    Mockito.when(mockExpenseManagerTransaction.getCategory()).thenReturn(ExpenseCategory.ENTERTAINMENT);
    ExpenseSubCategory mockExpenseSubCategory = Mockito.mock(ExpenseSubCategory.class);
    Mockito.when(mockExpenseManagerTransaction.getSubcategory()).thenReturn(mockExpenseSubCategory);
    Mockito.when(mockExpenseSubCategory.value()).thenReturn(StringUtils.EMPTY);
    assertThatThrownBy(() -> expenseManagerTransactionDatabaseProxy.add(mockExpenseManagerTransaction))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Subcategory cannot be null or empty.");
  }

  @Test
  void add_subcategoryIsNull() {
    ExpenseManagerTransaction mockExpenseManagerTransaction = Mockito.mock(ExpenseManagerTransaction.class);
    Mockito.when(mockExpenseManagerTransaction.getId()).thenReturn(Long.parseLong("4867"));
    Mockito.when(mockExpenseManagerTransaction.getCategory()).thenReturn(ExpenseCategory.ENTERTAINMENT);
    assertThatThrownBy(() -> expenseManagerTransactionDatabaseProxy.add(mockExpenseManagerTransaction))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Subcategory cannot be null.");
  }

  @Test
  void add_subcategoryIsBlank() {
    ExpenseManagerTransaction mockExpenseManagerTransaction = Mockito.mock(ExpenseManagerTransaction.class);
    Mockito.when(mockExpenseManagerTransaction.getId()).thenReturn(Long.parseLong("4867"));
    Mockito.when(mockExpenseManagerTransaction.getCategory()).thenReturn(ExpenseCategory.ENTERTAINMENT);
    ExpenseSubCategory mockExpenseSubCategory = Mockito.mock(ExpenseSubCategory.class);
    Mockito.when(mockExpenseManagerTransaction.getSubcategory()).thenReturn(mockExpenseSubCategory);
    Mockito.when(mockExpenseSubCategory.value()).thenReturn(StringUtils.SPACE);
    assertThatThrownBy(() -> expenseManagerTransactionDatabaseProxy.add(mockExpenseManagerTransaction))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Subcategory cannot be null or empty.");
  }

  @Test
  void add_paymentMethodIsEmpty() {
    ExpenseManagerTransaction mockExpenseManagerTransaction = Mockito.mock(ExpenseManagerTransaction.class);
    Mockito.when(mockExpenseManagerTransaction.getId()).thenReturn(Long.parseLong("4867"));
    Mockito.when(mockExpenseManagerTransaction.getCategory()).thenReturn(ExpenseCategory.ENTERTAINMENT);
    Mockito.when(mockExpenseManagerTransaction.getSubcategory()).thenReturn(ExpenseSubCategory.ALCOHOL_AND_RESTAURANT);
    PaymentMethod mockPaymentMethod = Mockito.mock(PaymentMethod.class);
    Mockito.when(mockExpenseManagerTransaction.getPaymentMethod()).thenReturn(mockPaymentMethod);
    Mockito.when(mockPaymentMethod.value()).thenReturn(StringUtils.EMPTY);
    assertThatThrownBy(() -> expenseManagerTransactionDatabaseProxy.add(mockExpenseManagerTransaction))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Payment Method cannot be null or empty.");
  }

  @Test
  void add_paymentMethodIsNull() {
    ExpenseManagerTransaction mockExpenseManagerTransaction = Mockito.mock(ExpenseManagerTransaction.class);
    Mockito.when(mockExpenseManagerTransaction.getId()).thenReturn(Long.parseLong("4867"));
    Mockito.when(mockExpenseManagerTransaction.getCategory()).thenReturn(ExpenseCategory.ENTERTAINMENT);
    Mockito.when(mockExpenseManagerTransaction.getSubcategory()).thenReturn(ExpenseSubCategory.ALCOHOL_AND_RESTAURANT);
    assertThatThrownBy(() -> expenseManagerTransactionDatabaseProxy.add(mockExpenseManagerTransaction))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Payment Method cannot be null.");
  }

  @Test
  void add_paymentMethodIsBlank() {
    ExpenseManagerTransaction mockExpenseManagerTransaction = Mockito.mock(ExpenseManagerTransaction.class);
    Mockito.when(mockExpenseManagerTransaction.getId()).thenReturn(Long.parseLong("4867"));
    Mockito.when(mockExpenseManagerTransaction.getCategory()).thenReturn(ExpenseCategory.ENTERTAINMENT);
    Mockito.when(mockExpenseManagerTransaction.getSubcategory()).thenReturn(ExpenseSubCategory.ALCOHOL_AND_RESTAURANT);
    PaymentMethod mockPaymentMethod = Mockito.mock(PaymentMethod.class);
    Mockito.when(mockExpenseManagerTransaction.getPaymentMethod()).thenReturn(mockPaymentMethod);
    Mockito.when(mockPaymentMethod.value()).thenReturn(StringUtils.SPACE);
    assertThatThrownBy(() -> expenseManagerTransactionDatabaseProxy.add(mockExpenseManagerTransaction))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Payment Method cannot be null or empty.");
  }

  @Test
  void add_descriptionIsEmpty() {
    ExpenseManagerTransaction mockExpenseManagerTransaction = Mockito.mock(ExpenseManagerTransaction.class);
    Mockito.when(mockExpenseManagerTransaction.getId()).thenReturn(Long.parseLong("4867"));
    Mockito.when(mockExpenseManagerTransaction.getCategory()).thenReturn(ExpenseCategory.ENTERTAINMENT);
    Mockito.when(mockExpenseManagerTransaction.getSubcategory()).thenReturn(ExpenseSubCategory.ALCOHOL_AND_RESTAURANT);
    Mockito.when(mockExpenseManagerTransaction.getPaymentMethod()).thenReturn(PaymentMethod.CASH);
    Mockito.when(mockExpenseManagerTransaction.getDescription()).thenReturn(StringUtils.EMPTY);
    assertThatThrownBy(() -> expenseManagerTransactionDatabaseProxy.add(mockExpenseManagerTransaction))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Description cannot be null or empty.");
  }

  @Test
  void add_descriptionIsNull() {
    ExpenseManagerTransaction mockExpenseManagerTransaction = Mockito.mock(ExpenseManagerTransaction.class);
    Mockito.when(mockExpenseManagerTransaction.getId()).thenReturn(Long.parseLong("4867"));
    Mockito.when(mockExpenseManagerTransaction.getCategory()).thenReturn(ExpenseCategory.ENTERTAINMENT);
    Mockito.when(mockExpenseManagerTransaction.getSubcategory()).thenReturn(ExpenseSubCategory.ALCOHOL_AND_RESTAURANT);
    Mockito.when(mockExpenseManagerTransaction.getPaymentMethod()).thenReturn(PaymentMethod.CASH);
    Mockito.when(mockExpenseManagerTransaction.getDescription()).thenReturn(null);
    assertThatThrownBy(() -> expenseManagerTransactionDatabaseProxy.add(mockExpenseManagerTransaction))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Description cannot be null or empty.");
  }

  @Test
  void add_descriptionIsBlank() {
    ExpenseManagerTransaction mockExpenseManagerTransaction = Mockito.mock(ExpenseManagerTransaction.class);
    Mockito.when(mockExpenseManagerTransaction.getId()).thenReturn(Long.parseLong("4867"));
    Mockito.when(mockExpenseManagerTransaction.getCategory()).thenReturn(ExpenseCategory.ENTERTAINMENT);
    Mockito.when(mockExpenseManagerTransaction.getSubcategory()).thenReturn(ExpenseSubCategory.ALCOHOL_AND_RESTAURANT);
    Mockito.when(mockExpenseManagerTransaction.getPaymentMethod()).thenReturn(PaymentMethod.CASH);
    Mockito.when(mockExpenseManagerTransaction.getDescription()).thenReturn(StringUtils.SPACE);
    assertThatThrownBy(() -> expenseManagerTransactionDatabaseProxy.add(mockExpenseManagerTransaction))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Description cannot be null or empty.");
  }

  @Test
  void add_expensedTimeIsNull() {
    ExpenseManagerTransaction mockExpenseManagerTransaction = Mockito.mock(ExpenseManagerTransaction.class);
    Mockito.when(mockExpenseManagerTransaction.getId()).thenReturn(Long.parseLong("4867"));
    Mockito.when(mockExpenseManagerTransaction.getCategory()).thenReturn(ExpenseCategory.ENTERTAINMENT);
    Mockito.when(mockExpenseManagerTransaction.getSubcategory()).thenReturn(ExpenseSubCategory.ALCOHOL_AND_RESTAURANT);
    Mockito.when(mockExpenseManagerTransaction.getPaymentMethod()).thenReturn(PaymentMethod.CASH);
    Mockito.when(mockExpenseManagerTransaction.getDescription()).thenReturn("Dinner.");
    Mockito.when(mockExpenseManagerTransaction.getExpendedTime()).thenReturn(null);
    assertThatThrownBy(() -> expenseManagerTransactionDatabaseProxy.add(mockExpenseManagerTransaction))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Expensed time cannot be null or in future.");
  }

  @Test
  void add_expensedTimeInTheFuture() {
    ExpenseManagerTransaction mockExpenseManagerTransaction = Mockito.mock(ExpenseManagerTransaction.class);
    Mockito.when(mockExpenseManagerTransaction.getId()).thenReturn(Long.parseLong("4867"));
    Mockito.when(mockExpenseManagerTransaction.getCategory()).thenReturn(ExpenseCategory.ENTERTAINMENT);
    Mockito.when(mockExpenseManagerTransaction.getSubcategory()).thenReturn(ExpenseSubCategory.ALCOHOL_AND_RESTAURANT);
    Mockito.when(mockExpenseManagerTransaction.getPaymentMethod()).thenReturn(PaymentMethod.CASH);
    Mockito.when(mockExpenseManagerTransaction.getDescription()).thenReturn("Dinner.");
    Instant futureInstant = Instant.MAX;
    Mockito.when(mockExpenseManagerTransaction.getExpendedTime()).thenReturn(futureInstant);
    assertThatThrownBy(() -> expenseManagerTransactionDatabaseProxy.add(mockExpenseManagerTransaction))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Expensed time cannot be null or in future.");
  }

  @Test
  void add_referenceAmountNull() throws IOException, SQLException {
    ExpenseManagerTransaction mockExpenseManagerTransaction = Mockito.mock(ExpenseManagerTransaction.class);
    Mockito.when(mockExpenseManagerTransaction.getId()).thenReturn(Long.parseLong("4867"));
    Mockito.when(mockExpenseManagerTransaction.getAmount()).thenReturn(Double.parseDouble("30.0"));
    Mockito.when(mockExpenseManagerTransaction.getCategory()).thenReturn(ExpenseCategory.ENTERTAINMENT);
    Mockito.when(mockExpenseManagerTransaction.getSubcategory()).thenReturn(ExpenseSubCategory.ALCOHOL_AND_RESTAURANT);
    Mockito.when(mockExpenseManagerTransaction.getPaymentMethod()).thenReturn(PaymentMethod.CASH);
    Mockito.when(mockExpenseManagerTransaction.getDescription())
        .thenReturn("Dinner. Sbcd Korean tofu house. Millenia walk.");
    Mockito.when(mockExpenseManagerTransaction.getExpendedTime())
        .thenReturn(Instant.ofEpochMilli(Long.parseLong("1514813160000")));
    Connection mockConnection = Mockito.mock(Connection.class);
    Mockito.when(mockDatabaseConnectable.connect()).thenReturn(mockConnection);
    SqlSessionFactory mockSqlSessionFactory = Mockito.mock(SqlSessionFactory.class);
    Mockito.when(mockDatabaseSessionFactoryBuilder.buildSessionFactory(TEST_ENVIRONMENT_ID))
        .thenReturn(mockSqlSessionFactory);
    SqlSession mockSqlSession = Mockito.mock(SqlSession.class);
    Mockito.when(mockSqlSessionFactory.openSession(ExecutorType.SIMPLE, mockConnection)).thenReturn(mockSqlSession);
    ExpenseManagerTransactionMapper mockExpenseManagerTransactionMapper =
        Mockito.mock(ExpenseManagerTransactionMapper.class);
    Mockito.when(mockSqlSession.getMapper(ExpenseManagerTransactionMapper.class))
        .thenReturn(mockExpenseManagerTransactionMapper);
    Mockito.when(mockExpenseManagerTransactionMapper.addExpenseManagerTransaction(
        4867,
        Double.parseDouble("30.0"),
        "Entertainment",
        "Alcohol/ Restaurant",
        "Cash",
        "Dinner. Sbcd Korean tofu house. Millenia walk.",
        Instant.ofEpochMilli(Long.parseLong("1514813160000")),
        Double.parseDouble("0")
    )).thenReturn(1);
    assertThat(expenseManagerTransactionDatabaseProxy.add(mockExpenseManagerTransaction))
        .isTrue();
  }

  @Test
  void add_referenceAmountIsZero() throws IOException, SQLException {
    ExpenseManagerTransaction mockExpenseManagerTransaction = Mockito.mock(ExpenseManagerTransaction.class);
    Mockito.when(mockExpenseManagerTransaction.getId()).thenReturn(Long.parseLong("4867"));
    Mockito.when(mockExpenseManagerTransaction.getAmount()).thenReturn(Double.parseDouble("30.0"));
    Mockito.when(mockExpenseManagerTransaction.getCategory()).thenReturn(ExpenseCategory.ENTERTAINMENT);
    Mockito.when(mockExpenseManagerTransaction.getSubcategory()).thenReturn(ExpenseSubCategory.ALCOHOL_AND_RESTAURANT);
    Mockito.when(mockExpenseManagerTransaction.getPaymentMethod()).thenReturn(PaymentMethod.CASH);
    Mockito.when(mockExpenseManagerTransaction.getDescription())
        .thenReturn("Dinner. Sbcd Korean tofu house. Millenia walk.");
    Mockito.when(mockExpenseManagerTransaction.getExpendedTime())
        .thenReturn(Instant.ofEpochMilli(Long.parseLong("1514813160000")));
    Mockito.when(mockExpenseManagerTransaction.getReferenceAmount())
        .thenReturn(Double.parseDouble("0"));
    Connection mockConnection = Mockito.mock(Connection.class);
    Mockito.when(mockDatabaseConnectable.connect()).thenReturn(mockConnection);
    SqlSessionFactory mockSqlSessionFactory = Mockito.mock(SqlSessionFactory.class);
    Mockito.when(mockDatabaseSessionFactoryBuilder.buildSessionFactory(TEST_ENVIRONMENT_ID))
        .thenReturn(mockSqlSessionFactory);
    SqlSession mockSqlSession = Mockito.mock(SqlSession.class);
    Mockito.when(mockSqlSessionFactory.openSession(ExecutorType.SIMPLE, mockConnection)).thenReturn(mockSqlSession);
    ExpenseManagerTransactionMapper mockExpenseManagerTransactionMapper =
        Mockito.mock(ExpenseManagerTransactionMapper.class);
    Mockito.when(mockSqlSession.getMapper(ExpenseManagerTransactionMapper.class))
        .thenReturn(mockExpenseManagerTransactionMapper);
    Mockito.when(mockExpenseManagerTransactionMapper.addExpenseManagerTransaction(
        4867,
        Double.parseDouble("30.0"),
        "Entertainment",
        "Alcohol/ Restaurant",
        "Cash",
        "Dinner. Sbcd Korean tofu house. Millenia walk.",
        Instant.ofEpochMilli(Long.parseLong("1514813160000")),
        Double.parseDouble("0")
    )).thenReturn(1);
    assertThat(expenseManagerTransactionDatabaseProxy.add(mockExpenseManagerTransaction))
        .isTrue();
  }

  @Test
  void add_referenceAmountIsNegative() {
    ExpenseManagerTransaction mockExpenseManagerTransaction = Mockito.mock(ExpenseManagerTransaction.class);
    Mockito.when(mockExpenseManagerTransaction.getId()).thenReturn(Long.parseLong("4867"));
    Mockito.when(mockExpenseManagerTransaction.getCategory()).thenReturn(ExpenseCategory.ENTERTAINMENT);
    Mockito.when(mockExpenseManagerTransaction.getSubcategory()).thenReturn(ExpenseSubCategory.ALCOHOL_AND_RESTAURANT);
    Mockito.when(mockExpenseManagerTransaction.getPaymentMethod()).thenReturn(PaymentMethod.CASH);
    Mockito.when(mockExpenseManagerTransaction.getDescription()).thenReturn("Dinner.");
    Mockito.when(mockExpenseManagerTransaction.getExpendedTime())
        .thenReturn(Instant.ofEpochMilli(Long.parseLong("1514813160000")));
    Mockito.when(mockExpenseManagerTransaction.getReferenceAmount())
        .thenReturn(Double.parseDouble("-7.0"));
    assertThatThrownBy(() -> expenseManagerTransactionDatabaseProxy.add(mockExpenseManagerTransaction))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Reference amount cannot be negative.");
  }

  @Test
  void add_databaseConnectableConnectException() throws SQLException {
    Mockito.when(mockDatabaseConnectable.connect()).thenThrow(new SQLException("databaseConnectableConnectException"));
    ExpenseManagerTransaction testExpenseManagerTransaction = ExpenseManagerTransaction.create(
        4867,
        Double.parseDouble("30.0"),
        ExpenseCategory.ENTERTAINMENT,
        ExpenseSubCategory.ALCOHOL_AND_RESTAURANT,
        PaymentMethod.CASH,
        "Dinner. Sbcd Korean tofu house. Millenia walk.",
        Instant.ofEpochMilli(Long.parseLong("1514813160000"))
    );
    assertThatThrownBy(() -> expenseManagerTransactionDatabaseProxy.add(testExpenseManagerTransaction))
        .isInstanceOf(SQLException.class)
        .hasMessage("databaseConnectableConnectException");
    Mockito.verifyNoInteractions(mockDatabaseSessionFactoryBuilder);
  }

  @Test
  void add_databaseSessionFactoryBuilderBuildSessionFactoryException() throws SQLException, IOException {
    Connection mockConnection = Mockito.mock(Connection.class);
    Mockito.when(mockDatabaseConnectable.connect()).thenReturn(mockConnection);
    Mockito.when(mockDatabaseSessionFactoryBuilder.buildSessionFactory(TEST_ENVIRONMENT_ID))
        .thenThrow(new IOException("databaseSessionFactoryBuilderBuildSessionFactoryException"));
    ExpenseManagerTransaction testExpenseManagerTransaction = ExpenseManagerTransaction.create(
        4867,
        Double.parseDouble("30.0"),
        ExpenseCategory.ENTERTAINMENT,
        ExpenseSubCategory.ALCOHOL_AND_RESTAURANT,
        PaymentMethod.CASH,
        "Dinner. Sbcd Korean tofu house. Millenia walk.",
        Instant.ofEpochMilli(Long.parseLong("1514813160000"))
    );
    assertThatThrownBy(() -> expenseManagerTransactionDatabaseProxy.add(testExpenseManagerTransaction))
        .isInstanceOf(IOException.class)
        .hasMessage("databaseSessionFactoryBuilderBuildSessionFactoryException");
  }

  @Test
  void add_sqlSessionFactoryOpenSessionException() throws SQLException, IOException {
    Connection mockConnection = Mockito.mock(Connection.class);
    Mockito.when(mockDatabaseConnectable.connect()).thenReturn(mockConnection);
    SqlSessionFactory mockSqlSessionFactory = Mockito.mock(SqlSessionFactory.class);
    Mockito.when(mockDatabaseSessionFactoryBuilder.buildSessionFactory(TEST_ENVIRONMENT_ID))
        .thenReturn(mockSqlSessionFactory);
    SqlSession mockSqlSession = Mockito.mock(SqlSession.class);
    Mockito.when(mockSqlSessionFactory.openSession(ExecutorType.SIMPLE, mockConnection))
        .thenThrow(new PersistenceException("sqlSessionFactoryOpenSessionException"));
    ExpenseManagerTransaction testExpenseManagerTransaction = ExpenseManagerTransaction.create(
        4867,
        Double.parseDouble("30.0"),
        ExpenseCategory.ENTERTAINMENT,
        ExpenseSubCategory.ALCOHOL_AND_RESTAURANT,
        PaymentMethod.CASH,
        "Dinner. Sbcd Korean tofu house. Millenia walk.",
        Instant.ofEpochMilli(Long.parseLong("1514813160000"))
    );
    assertThatThrownBy(() -> expenseManagerTransactionDatabaseProxy.add(testExpenseManagerTransaction))
        .isInstanceOf(PersistenceException.class)
        .hasMessage("sqlSessionFactoryOpenSessionException");
  }

  @Test
  void add_sqlSessionGetMapperException() throws SQLException, IOException {
    Connection mockConnection = Mockito.mock(Connection.class);
    Mockito.when(mockDatabaseConnectable.connect()).thenReturn(mockConnection);
    SqlSessionFactory mockSqlSessionFactory = Mockito.mock(SqlSessionFactory.class);
    Mockito.when(mockDatabaseSessionFactoryBuilder.buildSessionFactory(TEST_ENVIRONMENT_ID))
        .thenReturn(mockSqlSessionFactory);
    SqlSession mockSqlSession = Mockito.mock(SqlSession.class);
    Mockito.when(mockSqlSessionFactory.openSession(ExecutorType.SIMPLE, mockConnection)).thenReturn(mockSqlSession);
    Mockito.when(mockSqlSession.getMapper(ExpenseManagerTransactionMapper.class))
        .thenThrow(new BindingException("Type ExpenseManagerTransactionMapper is not known to the MapperRegistry."));
    ExpenseManagerTransaction testExpenseManagerTransaction = ExpenseManagerTransaction.create(
        4867,
        Double.parseDouble("30.0"),
        ExpenseCategory.ENTERTAINMENT,
        ExpenseSubCategory.ALCOHOL_AND_RESTAURANT,
        PaymentMethod.CASH,
        "Dinner. Sbcd Korean tofu house. Millenia walk.",
        Instant.ofEpochMilli(Long.parseLong("1514813160000"))
    );
    assertThatThrownBy(() -> expenseManagerTransactionDatabaseProxy.add(testExpenseManagerTransaction))
        .isInstanceOf(BindingException.class)
        .hasMessage("Type ExpenseManagerTransactionMapper is not known to the MapperRegistry.");
  }

  @Test
  void add_expenseManagerTransactionMapperAddExpenseManagerTransactionException() throws SQLException, IOException {
    Connection mockConnection = Mockito.mock(Connection.class);
    Mockito.when(mockDatabaseConnectable.connect()).thenReturn(mockConnection);
    SqlSessionFactory mockSqlSessionFactory = Mockito.mock(SqlSessionFactory.class);
    Mockito.when(mockDatabaseSessionFactoryBuilder.buildSessionFactory(TEST_ENVIRONMENT_ID))
        .thenReturn(mockSqlSessionFactory);
    SqlSession mockSqlSession = Mockito.mock(SqlSession.class);
    Mockito.when(mockSqlSessionFactory.openSession(ExecutorType.SIMPLE, mockConnection)).thenReturn(mockSqlSession);
    ExpenseManagerTransactionMapper mockExpenseManagerTransactionMapper =
        Mockito.mock(ExpenseManagerTransactionMapper.class);
    Mockito.when(mockSqlSession.getMapper(ExpenseManagerTransactionMapper.class))
        .thenReturn(mockExpenseManagerTransactionMapper);
    Mockito.when(mockExpenseManagerTransactionMapper.addExpenseManagerTransaction(
        4867,
        Double.parseDouble("30.0"),
        "Entertainment",
        "Alcohol/ Restaurant",
        "Cash",
        "Dinner. Sbcd Korean tofu house. Millenia walk.",
        Instant.ofEpochMilli(Long.parseLong("1514813160000")),
        Double.parseDouble("0")
    )).thenThrow(new PersistenceException("expenseManagerTransactionMapperAddExpenseManagerTransactionException"));
    ExpenseManagerTransaction testExpenseManagerTransaction = ExpenseManagerTransaction.create(
        4867,
        Double.parseDouble("30.0"),
        ExpenseCategory.ENTERTAINMENT,
        ExpenseSubCategory.ALCOHOL_AND_RESTAURANT,
        PaymentMethod.CASH,
        "Dinner. Sbcd Korean tofu house. Millenia walk.",
        Instant.ofEpochMilli(Long.parseLong("1514813160000"))
    );
    assertThatThrownBy(() -> expenseManagerTransactionDatabaseProxy.add(testExpenseManagerTransaction))
        .isInstanceOf(PersistenceException.class)
        .hasMessage("expenseManagerTransactionMapperAddExpenseManagerTransactionException");
  }

  @Test
  void clear_succeed() throws SQLException, IOException {
    Connection mockConnection = Mockito.mock(Connection.class);
    Mockito.when(mockDatabaseConnectable.connect()).thenReturn(mockConnection);
    SqlSessionFactory mockSqlSessionFactory = Mockito.mock(SqlSessionFactory.class);
    Mockito.when(mockDatabaseSessionFactoryBuilder.buildSessionFactory(TEST_ENVIRONMENT_ID))
        .thenReturn(mockSqlSessionFactory);
    SqlSession mockSqlSession = Mockito.mock(SqlSession.class);
    Mockito.when(mockSqlSessionFactory.openSession(ExecutorType.SIMPLE, mockConnection)).thenReturn(mockSqlSession);
    ExpenseManagerTransactionMapper mockExpenseManagerTransactionMapper =
        Mockito.mock(ExpenseManagerTransactionMapper.class);
    Mockito.when(mockSqlSession.getMapper(ExpenseManagerTransactionMapper.class))
        .thenReturn(mockExpenseManagerTransactionMapper);
    Mockito.when(mockExpenseManagerTransactionMapper.deleteAllExpenseManagerTransactions())
        .thenReturn(true);
    assertThat(expenseManagerTransactionDatabaseProxy.clear())
        .isTrue();
  }

  @Test
  void clear_databaseConnectableConnectException() throws SQLException {
    Mockito.when(mockDatabaseConnectable.connect()).thenThrow(new SQLException("databaseConnectableConnectException"));
    assertThatThrownBy(() -> expenseManagerTransactionDatabaseProxy.clear())
        .isInstanceOf(SQLException.class)
        .hasMessage("databaseConnectableConnectException");
    Mockito.verifyNoInteractions(mockDatabaseSessionFactoryBuilder);
  }

  @Test
  void clear_databaseSessionFactoryBuilderBuildSessionFactoryException() throws SQLException, IOException {
    Connection mockConnection = Mockito.mock(Connection.class);
    Mockito.when(mockDatabaseConnectable.connect()).thenReturn(mockConnection);
    Mockito.when(mockDatabaseSessionFactoryBuilder.buildSessionFactory(TEST_ENVIRONMENT_ID))
        .thenThrow(new IOException("databaseSessionFactoryBuilderBuildSessionFactoryException"));
    assertThatThrownBy(() -> expenseManagerTransactionDatabaseProxy.clear())
        .isInstanceOf(IOException.class)
        .hasMessage("databaseSessionFactoryBuilderBuildSessionFactoryException");
  }

  @Test
  void clear_sqlSessionFactoryOpenSessionException() throws SQLException, IOException {
    Connection mockConnection = Mockito.mock(Connection.class);
    Mockito.when(mockDatabaseConnectable.connect()).thenReturn(mockConnection);
    SqlSessionFactory mockSqlSessionFactory = Mockito.mock(SqlSessionFactory.class);
    Mockito.when(mockDatabaseSessionFactoryBuilder.buildSessionFactory(TEST_ENVIRONMENT_ID))
        .thenReturn(mockSqlSessionFactory);
    Mockito.when(mockSqlSessionFactory.openSession(ExecutorType.SIMPLE, mockConnection))
        .thenThrow(new PersistenceException("sqlSessionFactoryOpenSessionException"));
    assertThatThrownBy(() -> expenseManagerTransactionDatabaseProxy.clear())
        .isInstanceOf(PersistenceException.class)
        .hasMessage("sqlSessionFactoryOpenSessionException");
  }

  @Test
  void clear_sqlSessionGetMapperException() throws SQLException, IOException {
    Connection mockConnection = Mockito.mock(Connection.class);
    Mockito.when(mockDatabaseConnectable.connect()).thenReturn(mockConnection);
    SqlSessionFactory mockSqlSessionFactory = Mockito.mock(SqlSessionFactory.class);
    Mockito.when(mockDatabaseSessionFactoryBuilder.buildSessionFactory(TEST_ENVIRONMENT_ID))
        .thenReturn(mockSqlSessionFactory);
    SqlSession mockSqlSession = Mockito.mock(SqlSession.class);
    Mockito.when(mockSqlSessionFactory.openSession(ExecutorType.SIMPLE, mockConnection)).thenReturn(mockSqlSession);
    Mockito.when(mockSqlSession.getMapper(ExpenseManagerTransactionMapper.class))
        .thenThrow(new BindingException("Type ExpenseManagerTransactionMapper is not known to the MapperRegistry."));
    assertThatThrownBy(() -> expenseManagerTransactionDatabaseProxy.clear())
        .isInstanceOf(BindingException.class)
        .hasMessage("Type ExpenseManagerTransactionMapper is not known to the MapperRegistry.");
  }

  @Test
  void clear_expenseManagerTransactionMapperDeleteAllExpenseManagerTransactionsException() throws SQLException, IOException {
    Connection mockConnection = Mockito.mock(Connection.class);
    Mockito.when(mockDatabaseConnectable.connect()).thenReturn(mockConnection);
    SqlSessionFactory mockSqlSessionFactory = Mockito.mock(SqlSessionFactory.class);
    Mockito.when(mockDatabaseSessionFactoryBuilder.buildSessionFactory(TEST_ENVIRONMENT_ID))
        .thenReturn(mockSqlSessionFactory);
    SqlSession mockSqlSession = Mockito.mock(SqlSession.class);
    Mockito.when(mockSqlSessionFactory.openSession(ExecutorType.SIMPLE, mockConnection)).thenReturn(mockSqlSession);
    ExpenseManagerTransactionMapper mockExpenseManagerTransactionMapper =
        Mockito.mock(ExpenseManagerTransactionMapper.class);
    Mockito.when(mockSqlSession.getMapper(ExpenseManagerTransactionMapper.class))
        .thenReturn(mockExpenseManagerTransactionMapper);
    Mockito.when(mockExpenseManagerTransactionMapper.deleteAllExpenseManagerTransactions())
        .thenThrow(new PersistenceException("expenseManagerTransactionMapperDeleteAllExpenseManagerTransactionsException"));
    assertThatThrownBy(() -> expenseManagerTransactionDatabaseProxy.clear())
        .isInstanceOf(PersistenceException.class)
        .hasMessage("expenseManagerTransactionMapperDeleteAllExpenseManagerTransactionsException");
  }
}