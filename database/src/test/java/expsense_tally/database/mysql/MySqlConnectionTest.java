package expsense_tally.database.mysql;

import expense_tally.database.mysql.MySqlConnection;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
class MySqlConnectionTest {
  @Test
  void createDataSource_okay() throws SQLException {
    assertThat(MySqlConnection.createDataSource(
        "172.22.18.96",
        "expense_manager",
        "expensetally",
        "Password1",
        3500
    )).isNotNull();
  }

  @Test
  void createDataSource_connectionUrlIsNull() {
    assertThatThrownBy(() -> MySqlConnection.createDataSource( null, "expense_manager", "expensetally", "Password1",
        3500))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Connection URL should not be null or blank.");
  }

  @Test
  void createDataSource_connectionUrlIsEmpty() {
    assertThatThrownBy(() -> MySqlConnection.createDataSource(StringUtils.EMPTY, "expense_manager", "expensetally",
        "Password1", 3500))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Connection URL should not be null or blank.");
  }

  @Test
  void createDataSource_connectionUrlIsBlank() {
    assertThatThrownBy(() -> MySqlConnection.createDataSource(StringUtils.SPACE, "expense_manager", "expensetally",
        "Password1", 3500))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Connection URL should not be null or blank.");
  }

  @Test
  void createDataSource_databaseIsNull() {
    assertThatThrownBy(() -> MySqlConnection.createDataSource( "172.22.18.96", null, "expensetally", "Password1", 3500))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Database name should not be null or blank.");
  }

  @Test
  void createDataSource_databaseIsEmpty() {
    assertThatThrownBy(() -> MySqlConnection.createDataSource( "172.22.18.96", StringUtils.EMPTY, "expensetally",
        "Password1", 3500))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Database name should not be null or blank.");
  }

  @Test
  void createDataSource_databaseIsBlank() {
    assertThatThrownBy(() -> MySqlConnection.createDataSource( "172.22.18.96", StringUtils.SPACE, "expensetally",
        "Password1", 3500))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Database name should not be null or blank.");
  }

  @Test
  void createDataSource_usernameIsNull() {
    assertThatThrownBy(() -> MySqlConnection.createDataSource( "172.22.18.96", "expense_manager", StringUtils.EMPTY,
        "Password1", 3500))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Password needs to be accompanied by username.");
  }

  @Test
  void createDataSource_usernameIsEmpty() {
    assertThatThrownBy(() -> MySqlConnection.createDataSource( "172.22.18.96", "expense_manager", null, "Password1",
        3500))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Password needs to be accompanied by username.");
  }

  @Test
  void createDataSource_usernameIsBlank() {
    assertThatThrownBy(() -> MySqlConnection.createDataSource( "172.22.18.96", "expense_manager", StringUtils.SPACE,
        "Password1", 3500))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Password needs to be accompanied by username.");
  }

  @Test
  void createDataSource_passwordIsNull() throws SQLException {
    assertThat(MySqlConnection.createDataSource(
        "172.22.18.96",
        "expense_manager",
        "expense_manager",
        null,
        3500
    )).isNotNull();
  }

  @Test
  void createDataSource_passwordIsEmpty() throws SQLException {
    assertThat(MySqlConnection.createDataSource(
        "172.22.18.96",
        "expense_manager",
        "expense_manager",
        StringUtils.EMPTY,
        3500
    )).isNotNull();
  }

  @Test
  void createDataSource_passwordIsBlank() throws SQLException {
    assertThat(MySqlConnection.createDataSource(
        "172.22.18.96",
        "expense_manager",
        "expense_manager",
        StringUtils.SPACE,
        4500
    )).isNotNull();
  }

  @Test
  void createDataSource_usernameAndPasswordNull() throws SQLException {
    assertThat(MySqlConnection.createDataSource(
        "172.22.18.96",
        "expense_manager",
        null,
        null,
        3500
    )).isNotNull();
  }

  @Test
  void createDataSource_negativeLoginTimeout() {
    assertThatThrownBy(() -> MySqlConnection.createDataSource("172.22.18.96","expense_manager",null,null, -7))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Login time out value cannot be negative.");
  }

  @Test
  void createDataSource_zeroLoginTimeout() throws SQLException {
    assertThat(MySqlConnection.createDataSource("172.22.18.96",
        "expense_manager",
        null,
        null,
        NumberUtils.INTEGER_ZERO
    )).isNotNull();
  }

  @Test
  void createDataSource_minusOneLoginTimeout() {
    assertThatThrownBy(() -> MySqlConnection.createDataSource("172.22.18.96", "expense_manager", null, null, NumberUtils.INTEGER_MINUS_ONE))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Login time out value cannot be negative.");
  }

  @Test
  void createDataSource_noUserPwOkay() throws SQLException {
    assertThat(MySqlConnection.createDataSource(
        "172.22.18.96",
        "expense_manager",
        3500
    )).isNotNull();
  }

  @Test
  void createDataSource_noUserPwConnectionUrlIsNull() {
    assertThatThrownBy(() -> MySqlConnection.createDataSource( null, "expense_manager", 3500))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Connection URL should not be null or blank.");
  }

  @Test
  void createDataSource_noUserPwConnectionUrlIsEmpty() {
    assertThatThrownBy(() -> MySqlConnection.createDataSource(StringUtils.EMPTY, "expense_manager", 3500))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Connection URL should not be null or blank.");
  }

  @Test
  void createDataSource_noUserPwConnectionUrlIsBlank() {
    assertThatThrownBy(() -> MySqlConnection.createDataSource(StringUtils.SPACE, "expense_manager", 3500))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Connection URL should not be null or blank.");
  }

  @Test
  void createDataSource_noUserPwDatabaseIsNull() {
    assertThatThrownBy(() -> MySqlConnection.createDataSource( "172.22.18.96", null, 3500))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Database name should not be null or blank.");
  }

  @Test
  void createDataSource_noUserPwDatabaseIsEmpty() {
    assertThatThrownBy(() -> MySqlConnection.createDataSource( "172.22.18.96", StringUtils.EMPTY, 3500))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Database name should not be null or blank.");
  }

  @Test
  void createDataSource_noUserPwDatabaseIsBlank() {
    assertThatThrownBy(() -> MySqlConnection.createDataSource( "172.22.18.96", StringUtils.SPACE, 3500))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Database name should not be null or blank.");
  }

  @Test
  void createDataSource_noUserPwNegativeLoginTimeout() {
    assertThatThrownBy(() -> MySqlConnection.createDataSource("172.22.18.96","expense_manager", -7))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Login time out value cannot be negative.");
  }

  @Test
  void createDataSource_noUserPwZeroLoginTimeout() throws SQLException {
    assertThat(MySqlConnection.createDataSource("172.22.18.96", "expense_manager", NumberUtils.INTEGER_ZERO))
        .isNotNull();
  }

  @Test
  void createDataSource_NoUserPwMinusOneLoginTimeout() {
    assertThatThrownBy(() -> MySqlConnection.createDataSource("172.22.18.96", "expense_manager",
        NumberUtils.INTEGER_MINUS_ONE))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Login time out value cannot be negative.");
  }
}