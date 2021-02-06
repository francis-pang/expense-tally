package expsense_tally.database.sqlite;

import expense_tally.database.sqlite.SqLiteConnection;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
class SqLiteConnectionTest {
  @Test
  void createDataSource_pass() throws SQLException {
    String testDatabaseConnection = "testSql";
    assertThat(SqLiteConnection.createDataSource(testDatabaseConnection, 3500))
        .isNotNull();
  }

  @Test
  void createDataSource_null() {
    assertThatThrownBy(() -> SqLiteConnection.createDataSource(null, 3500))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessage("database file path cannot be blank.");
  }

  @Test
  void createDataSource_empty() {
    assertThatThrownBy(() -> SqLiteConnection.createDataSource(StringUtils.EMPTY, 3500))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("database file path cannot be blank.");
  }

  @Test
  void createDataSource_spaceOnly() {
    assertThatThrownBy(() -> SqLiteConnection.createDataSource(StringUtils.SPACE, 3500))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("database file path cannot be blank.");
  }

  @Test
  void createDataSource_negativeLoginTimeout() {
    String testDatabaseConnection = "testSql";
    assertThatThrownBy(() -> SqLiteConnection.createDataSource(testDatabaseConnection, -7))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Login time out value cannot be negative.");
  }

  @Test
  void createDataSource_zeroLoginTimeout() throws SQLException {
    String testDatabaseConnection = "testSql";
    assertThat(SqLiteConnection.createDataSource(testDatabaseConnection, NumberUtils.INTEGER_ZERO))
        .isNotNull();
  }

  @Test
  void createDataSource_minusOneLoginTimeout() {
    String testDatabaseConnection = "testSql";
    assertThatThrownBy(() -> SqLiteConnection.createDataSource(testDatabaseConnection, NumberUtils.INTEGER_MINUS_ONE))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Login time out value cannot be negative.");
  }
}

