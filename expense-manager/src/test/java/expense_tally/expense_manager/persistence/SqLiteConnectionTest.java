package expense_tally.expense_manager.persistence;

import expense_tally.expense_manager.persistence.database.sqlite.SqLiteConnection;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.sql.DataSource;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class SqLiteConnectionTest {
  @Mock
  private DataSource mockDataSource;

  @InjectMocks
  private SqLiteConnection sqLiteConnection;

  @Test
  void create() {
    String testDatabaseConnection = "testSql";
    assertThat(SqLiteConnection.createDataSource(testDatabaseConnection, null, null, null))
        .isNotNull();
  }
}

