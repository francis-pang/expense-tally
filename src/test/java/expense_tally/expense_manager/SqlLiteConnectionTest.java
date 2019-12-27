package expense_tally.expense_manager;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Connection;
import java.sql.SQLException;

@ExtendWith(MockitoExtension.class)
class SqlLiteConnectionTest {
  @Spy
  private DatabaseConnectable spyDatabaseConnectable;

  @Test
  void constructor() {
    String testDatabaseConnection = "testSql";
    spyDatabaseConnectable = new SqlLiteConnection(testDatabaseConnection);
  }

  @Test
  void connect_connectionSuccess() throws SQLException {
    Connection mockConnection = Mockito.mock(Connection.class);
    Mockito.when(spyDatabaseConnectable.connect()).thenReturn(mockConnection);
    Assertions.assertThat(spyDatabaseConnectable.connect())
        .isEqualTo(mockConnection);
  }

  @Test
  void connect_connectionFail() throws SQLException {
    Mockito.when(spyDatabaseConnectable.connect()).thenThrow(SQLException.class);
    Assertions.assertThatThrownBy(() -> spyDatabaseConnectable.connect())
        .isInstanceOf(SQLException.class);
  }
}
