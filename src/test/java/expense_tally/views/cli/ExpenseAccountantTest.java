package expense_tally.views.cli;

import expense_tally.csv_parser.CsvParser;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
class ExpenseAccountantTest {
  // For testing of positive test cases on this class's' only public method, reconcileData() has no return type.
  // Hence it is hard to verify on the result/ outcome. in this case, I will follow the answer seen in
  // https://stackoverflow.com/a/1607713/1522867 to "to use mocking framework that will create the mocks on the fly,
  // specify expections on them, and verify those expectations."

  @Mock
  private CsvParser mockCsvParser;

  @Test
  void constructor_null() {
    assertThatThrownBy(() -> new ExpenseAccountant(null, mockCsvParser))
        .isInstanceOf(NullPointerException.class);
  }

  @Test
  void constructor_emptyArray() {
    String[] testArgs = new String[]{};
    assertThatThrownBy(() -> new ExpenseAccountant(testArgs, mockCsvParser))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Need to provide both CSV and database path.");
  }

  @Test
  void constructor_1element() {
    String[] testArgs = new String[]{"csv-filepath"};
    assertThatThrownBy(() -> new ExpenseAccountant(testArgs, mockCsvParser))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Unable to find value for a key.");
  }

  @Test
  void constructor_1ValidParameter() {
    String[] testArgs = new String[]{"csv-filepath=./some.csv"};
    assertThatThrownBy(() -> new ExpenseAccountant(testArgs, mockCsvParser))
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
    ExpenseAccountant expenseAccountant = new ExpenseAccountant(testArgs, mockCsvParser);
    SoftAssertions softAssertions = new SoftAssertions();
    assertThat(expenseAccountant).isNotNull();
    softAssertions.assertThat(expenseAccountant).extracting("csvFilename").isEqualTo("./some.csv");
    softAssertions.assertThat(expenseAccountant).extracting("databaseFilename").isEqualTo("./database.db");
    softAssertions.assertAll();
  }

  @Test
  void constructor_optionWithoutValue() {
    String[] testArgs = new String[]{"csv-filepath",""};
    assertThatThrownBy(() -> new ExpenseAccountant(testArgs, mockCsvParser))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Unable to process empty value for option csv-filepath");
  }

  /**
   * We want to test a valid case where user pass in arguments of both options and value in separate words
   */
  @Test
  void constructor_allValidParametersWithoutEquals() {
    String[] testArgs = new String[]{"csv-filepath", "./some.csv", "database-filepath", "./database.db"};
    ExpenseAccountant expenseAccountant = new ExpenseAccountant(testArgs, mockCsvParser);
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
    ExpenseAccountant expenseAccountant = new ExpenseAccountant(testArgs, mockCsvParser);
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
    ExpenseAccountant expenseAccountant = new ExpenseAccountant(testArgs, mockCsvParser);
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
    assertThatThrownBy(() -> new ExpenseAccountant(testArgs, mockCsvParser))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Unable to recognised option extra=something");
  }

  @Test
  void constructor_allValidParametersOneMisspelt() {
    String[] testArgs = new String[]{"cv-filepath", "./some.csv", "database-filepath=./database.db"};
    assertThatThrownBy(() -> new ExpenseAccountant(testArgs, mockCsvParser))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("appParameter is invalid.");
  }

  /**
   * Test that the program is able to detect missing value
   */
  @Test
  void constructor_missingValueAfterEquals() {
    String[] testArgs = new String[]{"csv-filepath", "=", "./some.csv", "database-filepath", "="};
    assertThatThrownBy(() -> new ExpenseAccountant(testArgs, mockCsvParser))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Unable to find value for a key.");
  }

  /**
   * Test that the program is able to detect missing value, which disguised as an empty string
   */
  @Test
  void constructor_emptyValueString() {
    String[] testArgs = new String[]{"csv-filepath", "=", "./some.csv", "database-filepath", "=", ""};
    assertThatThrownBy(() -> new ExpenseAccountant(testArgs, mockCsvParser))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Unable to process empty value for option database-filepath");
  }

  /**
   * Test that the program is able to detect missing value, which disguised as an string of blank space
   */
  @Test
  void constructor_missingValueAsBlankSpace() {
    String[] testArgs = new String[]{"csv-filepath", "=", "./some.csv", "database-filepath", "=", "    "};
    assertThatThrownBy(() -> new ExpenseAccountant(testArgs, mockCsvParser))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Unable to process empty value for option database-filepath");
  }

}