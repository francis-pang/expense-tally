package expense_tally.views.cli;

import expense_tally.expense_manager.persistence.database.DatabaseEnvironmentId;
import expense_tally.expense_manager.persistence.database.DatabaseSessionBuilder;
import expense_tally.expense_manager.persistence.database.mysql.MySqlConnection;
import expense_tally.expense_manager.persistence.database.sqlite.SqLiteConnection;
import expense_tally.views.AppParameter;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.TransactionFactory;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

class CommandLineRunnerTest {
  @Test
  void main_commandParserParseCommandArgsError() {
    String[] testArgs = ("--csv-filepath test.csv " +
        "--database-filepath personal_finance.db " +
        "--destination-database-hostname 192.168.120.183").split(StringUtils.SPACE);
    try (MockedStatic<CommandParser> mockCommandParser = Mockito.mockStatic(CommandParser.class)) {
      mockCommandParser.when(() -> CommandParser.parseCommandArgs(testArgs))
          .thenThrow(new IllegalArgumentException("test error"));
      Assertions.assertThatThrownBy(() -> CommandLineRunner.main(testArgs))
          .isInstanceOf(IllegalArgumentException.class)
          .hasMessage("test error");
    }
  }
}