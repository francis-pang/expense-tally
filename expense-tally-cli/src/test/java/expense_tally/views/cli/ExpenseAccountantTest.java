package expense_tally.views.cli;

import expense_tally.csv_parser.CsvParser;
import expense_tally.expense_manager.persistence.ExpenseReportReader;
import expense_tally.expense_manager.transformation.ExpenseTransactionMapper;
import expense_tally.reconciliation.ExpenseReconciler;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.nio.BufferOverflowException;
import java.sql.SQLException;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExpenseAccountantTest {
  // For testing of positive test cases on this class's' only public method, reconcileData() has no return type.
  // Hence it is hard to verify on the result/ outcome. in this case, I will follow the answer seen in
  // https://stackoverflow.com/a/1607713/1522867 to "to use mocking framework that will create the mocks on the fly,
  // specify expectations on them, and verify those expectations."

  @Mock
  private CsvParser mockCsvParser;
  @Mock
  private ExpenseReportReader mockExpenseReportReader;
  @Mock
  private ExpenseTransactionMapper mockExpenseTransactionMapper;
  @Mock
  private ExpenseReconciler mockExpenseReconciler;
  @InjectMocks
  private ExpenseAccountant expenseAccountant;


  @Test
  void reconcileData_noError() throws SQLException, IOException {
    Mockito.when(mockCsvParser.parseCsvFile("./some.csv")).thenReturn(Collections.emptyList());
    Mockito.when(mockExpenseReportReader.getExpenseTransactions()).thenReturn(Collections.emptyList());
    Mockito.when(mockExpenseTransactionMapper.mapExpenseReportsToMap(Collections.emptyList()))
        .thenReturn(Collections.emptyMap());
    Mockito.when(mockExpenseReconciler.reconcileBankData(Collections.emptyList(), Collections.emptyMap()))
        .thenReturn(Collections.emptyList());
    expenseAccountant.reconcileData("./some.csv");
    Mockito.verify(mockExpenseReportReader, Mockito.times(1)).getExpenseTransactions();
    Mockito.verify(mockExpenseTransactionMapper, Mockito.times(1)).mapExpenseReportsToMap(Collections.emptyList());
    Mockito.verify(mockExpenseReconciler, Mockito.times(1)).reconcileBankData(Collections.emptyList(), Collections.emptyMap());
  }

  @Test
  void constructor_nullCsvParser() throws SQLException, IOException {
    Assertions.assertThatThrownBy(() -> new ExpenseAccountant(null, mockExpenseReportReader, mockExpenseTransactionMapper,
        mockExpenseReconciler))
        .isInstanceOf(NullPointerException.class)
        .hasNoCause();
  }

  @Test
  void constructor_nullExpenseReadable() throws SQLException, IOException {
    Assertions.assertThatThrownBy(() -> new ExpenseAccountant(mockCsvParser, null, mockExpenseTransactionMapper,
        mockExpenseReconciler))
        .isInstanceOf(NullPointerException.class)
        .hasNoCause();
  }

  @Test
  void constructor_nullExpenseReconciler() throws SQLException, IOException {
    Assertions.assertThatThrownBy(() -> new ExpenseAccountant(mockCsvParser, mockExpenseReportReader, null,
        mockExpenseReconciler))
        .isInstanceOf(NullPointerException.class)
        .hasNoCause();
  }

  @Test
  void constructor_nullExpenseTransactionMapper() throws SQLException, IOException {
    String[] testArgs = new String[]{"csv-filepath=./some.csv", "database-filepath=./database.db"};
    Assertions.assertThatThrownBy(() -> new ExpenseAccountant(mockCsvParser, mockExpenseReportReader,
        mockExpenseTransactionMapper,null))
        .isInstanceOf(NullPointerException.class)
        .hasNoCause();
  }

  @Test
  void reconcileData_csvParsableError() throws SQLException, IOException {
    Mockito.when(mockCsvParser.parseCsvFile("./some.csv")).thenThrow(new BufferOverflowException());
    Assertions.assertThatThrownBy(() -> expenseAccountant.reconcileData("./some.csv"))
        .isInstanceOf(BufferOverflowException.class)
        .hasNoCause();
    verifyNoInteractions(mockExpenseReconciler);
  }

  @Test
  void reconcileData_expenseReadableError() throws SQLException, IOException {
    Mockito.when(mockCsvParser.parseCsvFile("./some.csv")).thenReturn(Collections.emptyList());
    Mockito.when(mockExpenseReportReader.getExpenseTransactions()).thenThrow(new SQLException("test sql exception"));
    String[] testArgs = new String[]{"csv-filepath=./some.csv", "database-filepath=./database.db"};
    Assertions.assertThatThrownBy(() -> expenseAccountant.reconcileData("./some.csv"))
        .isInstanceOf(SQLException.class)
        .hasMessage("test sql exception");
    Mockito.verify(mockExpenseTransactionMapper, Mockito.never()).mapExpenseReportsToMap(ArgumentMatchers.anyList());
    Mockito.verify(mockExpenseReconciler, Mockito.never()).reconcileBankData(ArgumentMatchers.anyList(), ArgumentMatchers.anyMap());
  }

  @Test
  void reconcileData_expenseReconcilerError() throws SQLException, IOException {
    Mockito.when(mockCsvParser.parseCsvFile("./some.csv")).thenReturn(Collections.emptyList());
    Mockito.when(mockExpenseReportReader.getExpenseTransactions()).thenReturn(Collections.emptyList());
    Mockito.when(mockExpenseTransactionMapper.mapExpenseReportsToMap(Collections.emptyList()))
        .thenReturn(Collections.emptyMap());
    Mockito.when(mockExpenseReconciler.reconcileBankData(Collections.emptyList(), Collections.emptyMap()))
        .thenThrow(new IllegalStateException("test illegal state exception"));
    Assertions.assertThatThrownBy(() -> expenseAccountant.reconcileData("./some.csv"))
        .isInstanceOf(IllegalStateException.class)
        .hasMessage("test illegal state exception");
  }

  @Test
  void reconcileData_expenseTransactionMapperError() throws SQLException, IOException {
    Mockito.when(mockCsvParser.parseCsvFile("./some.csv")).thenReturn(Collections.emptyList());
    Mockito.when(mockExpenseReportReader.getExpenseTransactions()).thenReturn(Collections.emptyList());
    Mockito.when(mockExpenseTransactionMapper.mapExpenseReportsToMap(Collections.emptyList()))
        .thenThrow(new RuntimeException("test runtime exception"));
    Assertions.assertThatThrownBy(() -> expenseAccountant.reconcileData("./some.csv"))
        .isInstanceOf(RuntimeException.class)
        .hasMessage("test runtime exception");
    verifyNoInteractions(mockExpenseReconciler);
  }
}