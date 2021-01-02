package expense_tally.views.cli;

import expense_tally.views.AppParameter;
import org.apache.commons.lang3.StringUtils;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CommandParserTest {
  @Test
  void parseCommandArgs_null() {
    assertThatThrownBy(() -> CommandParser.parseCommandArgs(null))
        .isInstanceOf(NullPointerException.class);
  }

  @Test
  void parseCommandArgs_emptyArray() {
    String[] testArgs = new String[]{};
    assertThatThrownBy(() -> CommandParser.parseCommandArgs(testArgs))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Need to provide both CSV and database path.");
  }

  @Test
  void parseCommandArgs_1element() {
    String[] testArgs = new String[]{"csv-filepath"};
    assertThatThrownBy(() -> CommandParser.parseCommandArgs(testArgs))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Unable to find value for a key.");
  }

  @Test
  void parseCommandArgs_1ValidParameter() {
    String[] testArgs = new String[]{"csv-filepath=./some.csv"};
    assertThatThrownBy(() -> CommandParser.parseCommandArgs(testArgs))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Need to provide both CSV and database path.");
  }

  /**
   * We want to test a valid case where user pass in arguments of both options and value in a same word (without
   * spacing)
   */
  @Test
  void parseCommandArgs_allValidParametersWithEquals() {
    String[] testArgs = new String[]{"csv-filepath=./some.csv", "database-filepath=./database.db"};
    Map<AppParameter, String> appParameterMap = CommandParser.parseCommandArgs(testArgs);
    SoftAssertions softAssertions = new SoftAssertions();
    assertThat(appParameterMap).isNotNull();
    softAssertions.assertThat(appParameterMap).extractingByKey(AppParameter.CSV_PATH).isEqualTo("./some.csv");
    softAssertions.assertThat(appParameterMap).extractingByKey(AppParameter.DATABASE_PATH).isEqualTo("./database.db");
    softAssertions.assertAll();
  }

  @Test
  void parseCommandArgs_optionWithoutValue() {
    String[] testArgs = new String[]{"csv-filepath", StringUtils.EMPTY};
    assertThatThrownBy(() -> CommandParser.parseCommandArgs(testArgs))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Unable to process empty value for option csv-filepath");
  }

  /**
   * We want to test a valid case where user pass in arguments of both options and value in separate words
   */
  @Test
  void parseCommandArgs_allValidParametersWithoutEquals() {
    String[] testArgs = new String[]{"csv-filepath", "./some.csv", "database-filepath", "./database.db"};
    Map<AppParameter, String> appParameterMap = CommandParser.parseCommandArgs(testArgs);
    SoftAssertions softAssertions = new SoftAssertions();
    assertThat(appParameterMap).isNotNull();
    softAssertions.assertThat(appParameterMap).extractingByKey(AppParameter.CSV_PATH).isEqualTo("./some.csv");
    softAssertions.assertThat(appParameterMap).extractingByKey(AppParameter.DATABASE_PATH).isEqualTo("./database.db");
    softAssertions.assertAll();
  }

  /**
   * We want to test a valid case where user pass in arguments of both options and value in separate words, separated
   * by equals in between
   */
  @Test
  void parseCommandArgs_allValidParametersWithoutEqualsAndSpace() {
    String[] testArgs = new String[]{"csv-filepath", "./some.csv", "database-filepath", "./database.db"};
    Map<AppParameter, String> appParameterMap = CommandParser.parseCommandArgs(testArgs);
    SoftAssertions softAssertions = new SoftAssertions();
    assertThat(appParameterMap).isNotNull();
    softAssertions.assertThat(appParameterMap).extractingByKey(AppParameter.CSV_PATH).isEqualTo("./some.csv");
    softAssertions.assertThat(appParameterMap).extractingByKey(AppParameter.DATABASE_PATH).isEqualTo("./database.db");
    softAssertions.assertAll();
  }

  /**
   * We want to test a valid case where user pass in arguments of different format of options with value
   */
  @Test
  void parseCommandArgs_allValidParametersMixedFormat() {
    String[] testArgs = new String[]{"csv-filepath", "./some.csv", "database-filepath=./database.db"};
    Map<AppParameter, String> appParameterMap = CommandParser.parseCommandArgs(testArgs);
    assertThat(appParameterMap).isNotNull();
    SoftAssertions softAssertions = new SoftAssertions();
    softAssertions.assertThat(appParameterMap).extractingByKey(AppParameter.CSV_PATH).isEqualTo("./some.csv");
    softAssertions.assertThat(appParameterMap).extractingByKey(AppParameter.DATABASE_PATH).isEqualTo("./database.db");
    softAssertions.assertAll();
  }

  /**
   * This is a case where an extra valid formatted parameter is passed. We do not accept unknown parameter
   */
  @Test
  void parseCommandArgs_allValidParametersUnknownExtraParameter() {
    String[] testArgs = new String[]{"csv-filepath=./some.csv", "database-filepath=./database.db", "extra=something"};
    assertThatThrownBy(() -> CommandParser.parseCommandArgs(testArgs))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Unable to recognised option extra=something");
  }

  @Test
  void parseCommandArgs_allValidParametersOneMisspelt() {
    String[] testArgs = new String[]{"cv-filepath", "./some.csv", "database-filepath=./database.db"};
    assertThatThrownBy(() -> CommandParser.parseCommandArgs(testArgs))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("appParameter is invalid.");
  }

  /**
   * Test that the program is able to detect missing value
   */
  @Test
  void parseCommandArgs_missingValueAfterEquals() {
    String[] testArgs = new String[]{"csv-filepath", "=", "./some.csv", "database-filepath", "="};
    assertThatThrownBy(() -> CommandParser.parseCommandArgs(testArgs))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Unable to find value for a key.");
  }

  /**
   * Test that the program is able to detect missing value, which disguised as an empty string
   */
  @Test
  void parseCommandArgs_emptyValueString() {
    String[] testArgs = new String[]{"csv-filepath", "=", "./some.csv", "--database-filepath", "=",
            StringUtils.EMPTY};
    assertThatThrownBy(() -> CommandParser.parseCommandArgs(testArgs))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Unable to process empty value for option database-filepath");
  }

  /**
   * Test that the program is able to detect missing value, which disguised as an string of blank space
   */
  @Test
  void parseCommandArgs_missingValueAsBlankSpace() {
    String[] testArgs = new String[]{"csv-filepath", "=", "./some.csv", "database-filepath", "=", "    "};
    assertThatThrownBy(() -> CommandParser.parseCommandArgs(testArgs))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Unable to process empty value for option database-filepath");
  }
}
