package expense_tally.views.cli;

import org.apache.commons.lang3.StringUtils;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

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