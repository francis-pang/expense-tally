package expense_tally.expense_manager.persistence;

import expense_tally.expense_manager.persistence.database.DatabaseConnectable;
import expense_tally.expense_manager.persistence.database.sqlite.SqLiteConnection;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
class SqLiteConnectionTest {
  @Mock
  private DataSource mockDataSource;

  @InjectMocks
  private SqLiteConnection sqLiteConnection;

  @Test
  void create() {
    String testDatabaseConnection = "testSql";
    assertThat(SqLiteConnection.create(testDatabaseConnection))
        .isNotNull();
  }

  @Test
  void connect_connectionSuccess() throws SQLException {
    Connection mockConnection = Mockito.mock(Connection.class);
    Mockito.when(mockDataSource.getConnection()).thenReturn(mockConnection);
    assertThat(sqLiteConnection.connect())
        .isNotNull()
        .isEqualTo(mockConnection);
  }

  @Test
  void connect_error() throws SQLException {
    Mockito.when(mockDataSource.getConnection()).thenThrow(new SQLException("test SQL error"));
    assertThatThrownBy(() -> sqLiteConnection.connect())
        .isInstanceOf(SQLException.class)
        .hasMessage("test SQL error");
  }
}

