package expense_tally.expense_manager;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Connection;
import java.sql.SQLException;

@ExtendWith(MockitoExtension.class)
class SqlLiteConnectionTest {
  private DatabaseConnectable databaseConnectable = new SqlLiteConnection("test.db");
  private DatabaseConnectable spyDatabaseConnectable = Mockito.spy(databaseConnectable);

  @Test
  void connect_connectionSuccess() throws SQLException {
    Connection mockConnection = Mockito.mock(Connection.class);
    Mockito.doReturn(mockConnection).when(spyDatabaseConnectable).connect();
    Assertions.assertThat(spyDatabaseConnectable.connect()).isEqualTo(mockConnection);
  }

  @Test
  void connect_connectionFail() throws SQLException {
    Mockito.doThrow(SQLException.class).when(spyDatabaseConnectable).connect();
    Assertions.assertThatThrownBy(() ->{
      spyDatabaseConnectable.connect();
    }).isInstanceOf(SQLException.class);
  }
}