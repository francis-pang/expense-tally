package expense_tally.expense_manager.persistence;

import expense_tally.expense_manager.persistence.database.sqlite.SqLiteConnection;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
class SqLiteConnectionTest {
  @Test
  void createDataSource_pass() {
    String testDatabaseConnection = "testSql";
    assertThat(SqLiteConnection.createDataSource(testDatabaseConnection))
        .isNotNull();
  }

  @Test
  void createDataSource_null() {
    assertThatThrownBy(() -> SqLiteConnection.createDataSource(null))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessage("database file path cannot be blank.");
  }

  @Test
  void createDataSource_empty() {
    assertThatThrownBy(() -> SqLiteConnection.createDataSource(StringUtils.EMPTY))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("database file path cannot be blank.");
  }

  @Test
  void createDataSource_spaceOnly() {
    assertThatThrownBy(() -> SqLiteConnection.createDataSource(StringUtils.SPACE))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("database file path cannot be blank.");
  }
}

