package expense_tally.expense_manager.persistence;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.io.InputStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
class DatabaseSessionFactoryBuilderTest {
  @Mock
  private SqlSessionFactoryBuilder mockSqlSessionFactoryBuilder;

  @InjectMocks
  private DatabaseSessionFactoryBuilder databaseSessionFactoryBuilder;

  @Test
  void buildSessionFactory_fine() throws IOException {
    InputStream mockConfigurationStream = Mockito.mock(InputStream.class);
    SqlSessionFactory mockSqlSessionFactory = Mockito.mock(SqlSessionFactory.class);
    Mockito.when(mockSqlSessionFactoryBuilder.build(mockConfigurationStream, "test environment"))
            .thenReturn(mockSqlSessionFactory);
    try (MockedStatic<Resources> mockResources = Mockito.mockStatic(Resources.class)) {
      mockResources.when(() -> Resources.getResourceAsStream("mybatis-config.xml"))
              .thenReturn(mockConfigurationStream);
      assertThat(databaseSessionFactoryBuilder.buildSessionFactory("test environment"))
              .isNotNull()
              .isEqualTo(mockSqlSessionFactory);
    }
  }

  @Test
  void buildSessionFactory_nullEnvironmentId() {
    assertThatThrownBy(() -> databaseSessionFactoryBuilder.buildSessionFactory(null))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Environment ID cannot be empty");
  }

  @Test
  void buildSessionFactory_emptyEnvironmentId() {
    assertThatThrownBy(() -> databaseSessionFactoryBuilder.buildSessionFactory(null))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Environment ID cannot be empty");
  }

  @Test
  void buildSessionFactory_iOException() {
    try (MockedStatic<Resources> mockResources = Mockito.mockStatic(Resources.class)) {
      mockResources.when(() -> Resources.getResourceAsStream("mybatis-config.xml"))
              .thenThrow(new IOException("This is a test error"));
      assertThatThrownBy(() -> databaseSessionFactoryBuilder.buildSessionFactory("This will fail"))
              .isInstanceOf(IOException.class)
              .hasMessage("This is a test error");
    }
  }
}