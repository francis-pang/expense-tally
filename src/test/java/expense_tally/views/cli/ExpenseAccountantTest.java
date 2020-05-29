package expense_tally.views.cli;

import expense_tally.csv_parser.CsvParser;
import expense_tally.expense_manager.persistence.ExpenseReportReader;
import expense_tally.expense_manager.transformation.ExpenseTransactionMapper;
import expense_tally.reconciliation.ExpenseReconciler;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.nio.BufferOverflowException;
import java.sql.SQLException;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Mockito.mock;
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

  @Test
  void constructor_null() {
    assertThatThrownBy(() -> new ExpenseAccountant(null))
        .isInstanceOf(NullPointerException.class);
  }

  @Test
  void constructor_emptyArray() {
    String[] testArgs = new String[]{};
    assertThatThrownBy(() -> new ExpenseAccountant(testArgs))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Need to provide both CSV and database path.");
  }

  @Test
  void constructor_1element() {
    String[] testArgs = new String[]{"csv-filepath"};
    assertThatThrownBy(() -> new ExpenseAccountant(testArgs))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Unable to find value for a key.");
  }

  @Test
  void constructor_1ValidParameter() {
    String[] testArgs = new String[]{"csv-filepath=./some.csv"};
    assertThatThrownBy(() -> new ExpenseAccountant(testArgs))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Need to provide both CSV and database path.");
  }

  /**
   * We want to test a valid case where user pass in arguments of both options and value in a same word (without
   * spacing)
   */
  @Test
  void constructor_allValidParametersWithEquals() {
    String[] testArgs = new String[]{"csv-filepath=./some.csv", "database-filepath=./database.db"};
    ExpenseAccountant expenseAccountant = new ExpenseAccountant(testArgs);
    SoftAssertions softAssertions = new SoftAssertions();
    assertThat(expenseAccountant).isNotNull();
    softAssertions.assertThat(expenseAccountant).extracting("csvFilename").isEqualTo("./some.csv");
    softAssertions.assertThat(expenseAccountant).extracting("databaseFilename").isEqualTo("./database.db");
    softAssertions.assertAll();
  }

  @Test
  void constructor_optionWithoutValue() {
    String[] testArgs = new String[]{"csv-filepath", ""};
    assertThatThrownBy(() -> new ExpenseAccountant(testArgs))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Unable to process empty value for option csv-filepath");
  }

  /**
   * We want to test a valid case where user pass in arguments of both options and value in separate words
   */
  @Test
  void constructor_allValidParametersWithoutEquals() {
    String[] testArgs = new String[]{"csv-filepath", "./some.csv", "database-filepath", "./database.db"};
    ExpenseAccountant expenseAccountant = new ExpenseAccountant(testArgs);
    SoftAssertions softAssertions = new SoftAssertions();
    assertThat(expenseAccountant).isNotNull();
    softAssertions.assertThat(expenseAccountant).extracting("csvFilename").isEqualTo("./some.csv");
    softAssertions.assertThat(expenseAccountant).extracting("databaseFilename").isEqualTo("./database.db");
    softAssertions.assertAll();
  }

  /**
   * We want to test a valid case where user pass in arguments of both options and value in separate words, separated
   * by equals in between
   */
  @Test
  void constructor_allValidParametersWithoutEqualsAndSpace() {
    String[] testArgs = new String[]{"csv-filepath", "./some.csv", "database-filepath", "./database.db"};
    ExpenseAccountant expenseAccountant = new ExpenseAccountant(testArgs);
    SoftAssertions softAssertions = new SoftAssertions();
    assertThat(expenseAccountant).isNotNull();
    softAssertions.assertThat(expenseAccountant).extracting("csvFilename").isEqualTo("./some.csv");
    softAssertions.assertThat(expenseAccountant).extracting("databaseFilename").isEqualTo("./database.db");
    softAssertions.assertAll();
  }

  /**
   * We want to test a valid case where user pass in arguments of different format of options with value
   */
  @Test
  void constructor_allValidParametersMixedFormat() {
    String[] testArgs = new String[]{"csv-filepath", "./some.csv", "database-filepath=./database.db"};
    ExpenseAccountant expenseAccountant = new ExpenseAccountant(testArgs);
    SoftAssertions softAssertions = new SoftAssertions();
    assertThat(expenseAccountant).isNotNull();
    softAssertions.assertThat(expenseAccountant).extracting("csvFilename").isEqualTo("./some.csv");
    softAssertions.assertThat(expenseAccountant).extracting("databaseFilename").isEqualTo("./database.db");
    softAssertions.assertAll();
  }

  /**
   * This is a case where an extra valid formatted parameter is passed. We do not accept unknown parameter
   */
  @Test
  void constructor_allValidParametersUnknownExtraParameter() {
    String[] testArgs = new String[]{"csv-filepath=./some.csv", "database-filepath=./database.db", "extra=something"};
    assertThatThrownBy(() -> new ExpenseAccountant(testArgs))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Unable to recognised option extra=something");
  }

  @Test
  void constructor_allValidParametersOneMisspelt() {
    String[] testArgs = new String[]{"cv-filepath", "./some.csv", "database-filepath=./database.db"};
    assertThatThrownBy(() -> new ExpenseAccountant(testArgs))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("appParameter is invalid.");
  }

  /**
   * Test that the program is able to detect missing value
   */
  @Test
  void constructor_missingValueAfterEquals() {
    String[] testArgs = new String[]{"csv-filepath", "=", "./some.csv", "database-filepath", "="};
    assertThatThrownBy(() -> new ExpenseAccountant(testArgs))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Unable to find value for a key.");
  }

  /**
   * Test that the program is able to detect missing value, which disguised as an empty string
   */
  @Test
  void constructor_emptyValueString() {
    String[] testArgs = new String[]{"csv-filepath", "=", "./some.csv", "database-filepath", "=", ""};
    assertThatThrownBy(() -> new ExpenseAccountant(testArgs))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Unable to process empty value for option database-filepath");
  }

  /**
   * Test that the program is able to detect missing value, which disguised as an string of blank space
   */
  @Test
  void constructor_missingValueAsBlankSpace() {
    String[] testArgs = new String[]{"csv-filepath", "=", "./some.csv", "database-filepath", "=", "    "};
    assertThatThrownBy(() -> new ExpenseAccountant(testArgs))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Unable to process empty value for option database-filepath");
  }

//  public void reconcileData(ExpenseReadable expenseReadable, ExpenseReconciler expenseReconciler,
//                            ExpenseTransactionMapper expenseTransactionMapper) throws IOException, SQLException {

  @Test
  void reconcileData_noError() throws SQLException, IOException {
    CsvParser mockCsvParser = mock(CsvParser.class);
    when(mockCsvParser.parseCsvFile("./some.csv")).thenReturn(Collections.emptyList());

    ExpenseReportReader mockExpenseReportReader = mock(ExpenseReportReader.class);
    when(mockExpenseReportReader.getExpenseTransactions()).thenReturn(Collections.emptyList());

    ExpenseTransactionMapper mockExpenseTransactionMapper = mock(ExpenseTransactionMapper.class);
    when(mockExpenseTransactionMapper.mapExpenseReportsToMap(Collections.emptyList())).thenReturn(Collections.emptyMap());

    ExpenseReconciler mockExpenseReconciler = mock(ExpenseReconciler.class);
    when(mockExpenseReconciler.reconcileBankData(Collections.emptyList(), Collections.emptyMap()))
        .thenReturn(Collections.emptyList());

    String[] testArgs = new String[]{"csv-filepath=./some.csv", "database-filepath=./database.db"};
    ExpenseAccountant expenseAccountant = new ExpenseAccountant(testArgs);
    expenseAccountant.reconcileData(mockCsvParser, mockExpenseReportReader, mockExpenseTransactionMapper,
        mockExpenseReconciler);
    verify(mockExpenseReportReader, times(1)).getExpenseTransactions();
    verify(mockExpenseTransactionMapper, times(1)).mapExpenseReportsToMap(Collections.emptyList());
    verify(mockExpenseReconciler, times(1)).reconcileBankData(Collections.emptyList(), Collections.emptyMap());
  }

  @Test
  void reconcileData_nullCsvParser() throws SQLException, IOException {
    ExpenseReportReader mockExpenseReportReader = mock(ExpenseReportReader.class);
    ExpenseTransactionMapper mockExpenseTransactionMapper = mock(ExpenseTransactionMapper.class);
    ExpenseReconciler mockExpenseReconciler = mock(ExpenseReconciler.class);

    String[] testArgs = new String[]{"csv-filepath=./some.csv", "database-filepath=./database.db"};
    ExpenseAccountant expenseAccountant = new ExpenseAccountant(testArgs);
    assertThatThrownBy(() -> expenseAccountant.reconcileData(null, mockExpenseReportReader,
        mockExpenseTransactionMapper, mockExpenseReconciler))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("CSV Parsable is null");
  }

  @Test
  void reconcileData_nullExpenseReadable() throws SQLException, IOException {
    CsvParser mockCsvParser = mock(CsvParser.class);
    ExpenseReportReader mockExpenseReportReader = mock(ExpenseReportReader.class);
    ExpenseTransactionMapper mockExpenseTransactionMapper = mock(ExpenseTransactionMapper.class);
    ExpenseReconciler mockExpenseReconciler = mock(ExpenseReconciler.class);

    String[] testArgs = new String[]{"csv-filepath=./some.csv", "database-filepath=./database.db"};
    ExpenseAccountant expenseAccountant = new ExpenseAccountant(testArgs);
    assertThatThrownBy(() -> expenseAccountant.reconcileData(mockCsvParser, null,
        mockExpenseTransactionMapper, mockExpenseReconciler))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Expense Readable is null");
  }

  @Test
  void reconcileData_nullExpenseReconciler() throws SQLException, IOException {
    CsvParser mockCsvParser = mock(CsvParser.class);
    ExpenseReportReader mockExpenseReportReader = mock(ExpenseReportReader.class);
    ExpenseTransactionMapper mockExpenseTransactionMapper = mock(ExpenseTransactionMapper.class);

    String[] testArgs = new String[]{"csv-filepath=./some.csv", "database-filepath=./database.db"};
    ExpenseAccountant expenseAccountant = new ExpenseAccountant(testArgs);
    assertThatThrownBy(() -> expenseAccountant.reconcileData(mockCsvParser, mockExpenseReportReader,
        mockExpenseTransactionMapper, null))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Expense Reconciler is null");
  }

  @Test
  void reconcileData_nullExpenseTransactionMapper() throws SQLException, IOException {
    CsvParser mockCsvParser = mock(CsvParser.class);
    ExpenseReportReader mockExpenseReportReader = mock(ExpenseReportReader.class);
    ExpenseReconciler mockExpenseReconciler = mock(ExpenseReconciler.class);

    String[] testArgs = new String[]{"csv-filepath=./some.csv", "database-filepath=./database.db"};
    ExpenseAccountant expenseAccountant = new ExpenseAccountant(testArgs);
    assertThatThrownBy(() -> expenseAccountant.reconcileData(mockCsvParser, mockExpenseReportReader, null,
        mockExpenseReconciler))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Expense Transaction Mapper is null");
    verifyNoInteractions(mockExpenseReconciler);
  }

  @Test
  void reconcileData_csvParsableError() throws SQLException, IOException {
    CsvParser mockCsvParser = mock(CsvParser.class);
    when(mockCsvParser.parseCsvFile("./some.csv")).thenThrow(new BufferOverflowException());
    ExpenseReportReader mockExpenseReportReader = mock(ExpenseReportReader.class);
    ExpenseTransactionMapper mockExpenseTransactionMapper = mock(ExpenseTransactionMapper.class);
    ExpenseReconciler mockExpenseReconciler = mock(ExpenseReconciler.class);

    String[] testArgs = new String[]{"csv-filepath=./some.csv", "database-filepath=./database.db"};
    ExpenseAccountant expenseAccountant = new ExpenseAccountant(testArgs);
    assertThatThrownBy(() -> expenseAccountant.reconcileData(mockCsvParser, mockExpenseReportReader, mockExpenseTransactionMapper,
        mockExpenseReconciler))
        .isInstanceOf(BufferOverflowException.class)
        .hasNoCause();
    verifyNoInteractions(mockExpenseReconciler);
  }

  @Test
  void reconcileData_expenseReadableError() throws SQLException, IOException {
    CsvParser mockCsvParser = mock(CsvParser.class);
    when(mockCsvParser.parseCsvFile("./some.csv")).thenReturn(Collections.emptyList());
    ExpenseReportReader mockExpenseReportReader = mock(ExpenseReportReader.class);
    when(mockExpenseReportReader.getExpenseTransactions()).thenThrow(new SQLException("test sql exception"));
    ExpenseTransactionMapper mockExpenseTransactionMapper = mock(ExpenseTransactionMapper.class);
    ExpenseReconciler mockExpenseReconciler = mock(ExpenseReconciler.class);

    String[] testArgs = new String[]{"csv-filepath=./some.csv", "database-filepath=./database.db"};
    ExpenseAccountant expenseAccountant = new ExpenseAccountant(testArgs);
    assertThatThrownBy(() -> expenseAccountant.reconcileData(mockCsvParser, mockExpenseReportReader, mockExpenseTransactionMapper,
        mockExpenseReconciler))
        .isInstanceOf(SQLException.class)
        .hasMessage("test sql exception");
    verify(mockExpenseTransactionMapper, never()).mapExpenseReportsToMap(anyList());
    verify(mockExpenseReconciler, never()).reconcileBankData(anyList(), anyMap());
  }

  @Test
  void reconcileData_expenseReconcilerError() throws SQLException, IOException {
    CsvParser mockCsvParser = mock(CsvParser.class);
    when(mockCsvParser.parseCsvFile("./some.csv")).thenReturn(Collections.emptyList());

    ExpenseReportReader mockExpenseReportReader = mock(ExpenseReportReader.class);
    when(mockExpenseReportReader.getExpenseTransactions()).thenReturn(Collections.emptyList());

    ExpenseTransactionMapper mockExpenseTransactionMapper = mock(ExpenseTransactionMapper.class);
    when(mockExpenseTransactionMapper.mapExpenseReportsToMap(Collections.emptyList())).thenReturn(Collections.emptyMap());

    ExpenseReconciler mockExpenseReconciler = mock(ExpenseReconciler.class);
    when(mockExpenseReconciler.reconcileBankData(Collections.emptyList(), Collections.emptyMap()))
        .thenThrow(new IllegalStateException("test illegal state exception"));

    String[] testArgs = new String[]{"csv-filepath=./some.csv", "database-filepath=./database.db"};
    ExpenseAccountant expenseAccountant = new ExpenseAccountant(testArgs);
    assertThatThrownBy(() -> expenseAccountant.reconcileData(mockCsvParser, mockExpenseReportReader, mockExpenseTransactionMapper,
        mockExpenseReconciler))
        .isInstanceOf(IllegalStateException.class)
        .hasMessage("test illegal state exception");
  }

  @Test
  void reconcileData_expenseTransactionMapperError() throws SQLException, IOException {
    CsvParser mockCsvParser = mock(CsvParser.class);
    when(mockCsvParser.parseCsvFile("./some.csv")).thenReturn(Collections.emptyList());

    ExpenseReportReader mockExpenseReportReader = mock(ExpenseReportReader.class);
    when(mockExpenseReportReader.getExpenseTransactions()).thenReturn(Collections.emptyList());

    ExpenseTransactionMapper mockExpenseTransactionMapper = mock(ExpenseTransactionMapper.class);
    when(mockExpenseTransactionMapper.mapExpenseReportsToMap(Collections.emptyList()))
        .thenThrow(new RuntimeException("test runtime exception"));

    ExpenseReconciler mockExpenseReconciler = mock(ExpenseReconciler.class);

    String[] testArgs = new String[]{"csv-filepath=./some.csv", "database-filepath=./database.db"};
    ExpenseAccountant expenseAccountant = new ExpenseAccountant(testArgs);
    assertThatThrownBy(() -> expenseAccountant.reconcileData(mockCsvParser, mockExpenseReportReader, mockExpenseTransactionMapper,
        mockExpenseReconciler))
        .isInstanceOf(RuntimeException.class)
        .hasMessage("test runtime exception");
    verifyNoInteractions(mockExpenseReconciler);
  }

  /**
   * Test case:
   * 1. Everything runs smoothly (X)
   * 3. expenseReconciler is null (X)
   * 4. expenseTransactionMapper is null (X)
   * 5. expenseReadable has error (X)
   * 6. expenseReconciler has error (X)
   * 7. expenseTransactionMapper has error (X)
   **/


}