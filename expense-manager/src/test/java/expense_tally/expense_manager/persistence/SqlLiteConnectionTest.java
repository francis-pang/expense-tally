package expense_tally.expense_manager.persistence;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Connection;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
class SqlLiteConnectionTest {
  @Spy
  private DatabaseConnectable spyDatabaseConnectable;

  @Test
  void constructor() {
    String testDatabaseConnection = "testSql";
    assertThat(new SqlLiteConnection(testDatabaseConnection))
        .isNotNull();
  }

  @Test
  void connect_connectionSuccess() throws SQLException {
    Connection mockConnection = Mockito.mock(Connection.class);
    Mockito.when(spyDatabaseConnectable.connect()).thenReturn(mockConnection);
    assertThat(spyDatabaseConnectable.connect())
        .isEqualTo(mockConnection);
  }

  @Test
  void connect_connectionFail() throws SQLException {
    Mockito.when(spyDatabaseConnectable.connect()).thenThrow(SQLException.class);
    assertThatThrownBy(() -> spyDatabaseConnectable.connect())
        .isInstanceOf(SQLException.class);
  }
}
