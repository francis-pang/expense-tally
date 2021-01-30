package expense_tally.expense_manager.persistence.database.mysql;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.sql.DataSource;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
class MySqlConnectionTest {
  @Mock
  private DataSource mockDataSource;

  @InjectMocks
  private MySqlConnection mySqlConnection;

  @Test
  void create_okay() throws SQLException {
    assertThat(MySqlConnection.createDataSource(
        "172.22.18.96",
        "expense_manager",
        "expensetally",
        "Password1"
    )).isNotNull();
  }

  @Test
  void create_connectionUrlIsNull() {
    assertThatThrownBy(() -> MySqlConnection.createDataSource( null, "expense_manager", "expensetally", "Password1"))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Connection URL should not be null or blank.");
  }

  @Test
  void create_connectionUrlIsEmpty() {
    assertThatThrownBy(() -> MySqlConnection.createDataSource(StringUtils.EMPTY, "expense_manager", "expensetally", "Password1"))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Connection URL should not be null or blank.");
  }

  @Test
  void create_connectionUrlIsBlank() {
    assertThatThrownBy(() -> MySqlConnection.createDataSource(StringUtils.SPACE, "expense_manager", "expensetally", "Password1"))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Connection URL should not be null or blank.");
  }

  @Test
  void create_databaseIsNull() {
    assertThatThrownBy(() -> MySqlConnection.createDataSource( "172.22.18.96", null, "expensetally", "Password1"))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Database name should not be null or blank.");
  }

  @Test
  void create_databaseIsEmpty() {
    assertThatThrownBy(() -> MySqlConnection.createDataSource( "172.22.18.96", StringUtils.EMPTY, "expensetally", "Password1"))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Database name should not be null or blank.");
  }

  @Test
  void create_databaseIsBlank() {
    assertThatThrownBy(() -> MySqlConnection.createDataSource( "172.22.18.96", StringUtils.SPACE, "expensetally", "Password1"))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Database name should not be null or blank.");
  }

  @Test
  void create_usernameIsNull() {
    assertThatThrownBy(() -> MySqlConnection.createDataSource( "172.22.18.96", "expense_manager", StringUtils.EMPTY,
        "Password1"))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Password needs to be accompanied by username.");
  }

  @Test
  void create_usernameIsEmpty() {
    assertThatThrownBy(() -> MySqlConnection.createDataSource( "172.22.18.96", "expense_manager", null, "Password1"))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Password needs to be accompanied by username.");
  }

  @Test
  void create_usernameIsBlank() {
    assertThatThrownBy(() -> MySqlConnection.createDataSource( "172.22.18.96", "expense_manager", StringUtils.SPACE,
        "Password1"))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Password needs to be accompanied by username.");
  }

  @Test
  void create_passwordIsNull() throws SQLException {
    assertThat(MySqlConnection.createDataSource(
        "172.22.18.96",
        "expense_manager",
        "expense_manager",
        null
    )).isNotNull();
  }

  @Test
  void create_passwordIsEmpty() throws SQLException {
    assertThat(MySqlConnection.createDataSource(
        "172.22.18.96",
        "expense_manager",
        "expense_manager",
        StringUtils.EMPTY
    )).isNotNull();
  }

  @Test
  void create_passwordIsBlank() throws SQLException {
    assertThat(MySqlConnection.createDataSource(
        "172.22.18.96",
        "expense_manager",
        "expense_manager",
        StringUtils.SPACE
    )).isNotNull();
  }

  @Test
  void create_usernameAndPasswordNull() throws SQLException {
    assertThat(MySqlConnection.createDataSource(
        "172.22.18.96",
        "expense_manager",
        null,
        null
    )).isNotNull();
  }
}