package expense_tally.expense_manager.persistence;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
class SqlLiteConnectionTest {
  private DatabaseConnectable spyDatabaseConnectable;

  @Test
  void constructor() {
    String testDatabaseConnection = "testSql";
    assertThat(new SqlLiteConnection(testDatabaseConnection))
        .isNotNull();
  }

  @Test
  void connect_connectionSuccess() throws SQLException {
    spyDatabaseConnectable = new SqlLiteConnection("test error string");
    Connection mockedConnection = Mockito.mock(Connection.class);
    try (MockedStatic<DriverManager> mockDriverManager = Mockito.mockStatic(DriverManager.class)) {
      mockDriverManager.when(() -> DriverManager.getConnection(Mockito.anyString()))
          .thenReturn(mockedConnection);
      assertThat(spyDatabaseConnectable.connect())
          .isNotNull()
          .isEqualTo(mockedConnection);
    }
  }

  @Test
  void connect_error() {
    spyDatabaseConnectable = new SqlLiteConnection("test error string");
    SQLException testSqlException = new SQLException("test error");
    try (MockedStatic<DriverManager> mockDriverManager = Mockito.mockStatic(DriverManager.class)) {
      mockDriverManager.when(() -> DriverManager.getConnection(Mockito.anyString()))
          .thenThrow(testSqlException);
      assertThatThrownBy(() -> spyDatabaseConnectable.connect())
          .isInstanceOf(SQLException.class)
          .hasMessage("test error");
    }
  }
}
