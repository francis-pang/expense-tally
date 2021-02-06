package expsense_tally.database;

import expense_tally.database.DatabaseSessionBuilder;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
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
class DatabaseSessionBuilderTest {
  @Mock
  private SqlSessionFactoryBuilder mockSqlSessionFactoryBuilder;

  @InjectMocks
  private DatabaseSessionBuilder databaseSessionBuilder;

  @Test
  void of_once() {
    assertThat(DatabaseSessionBuilder.of(mockSqlSessionFactoryBuilder))
        .isNotNull();
  }

  @Test
  void of_twice() {
    DatabaseSessionBuilder testDatabaseSessionBuilder = DatabaseSessionBuilder.of(mockSqlSessionFactoryBuilder);
    assertThat(testDatabaseSessionBuilder)
        .isNotNull();
    assertThat(DatabaseSessionBuilder.of(mockSqlSessionFactoryBuilder))
        .isNotNull()
        .isEqualTo(testDatabaseSessionBuilder);
  }

  @Test
  void buildSessionFactory_fine() throws IOException {
    InputStream mockConfigurationStream = Mockito.mock(InputStream.class);
    SqlSessionFactory mockSqlSessionFactory = Mockito.mock(SqlSessionFactory.class);

    Mockito.when(mockSqlSessionFactoryBuilder.build(mockConfigurationStream))
            .thenReturn(mockSqlSessionFactory);
    Environment mockEnvironment = Mockito.mock(Environment.class);
    SqlSession mockSqlSession = Mockito.mock(SqlSession.class);
    Configuration mockConfiguration = Mockito.mock(Configuration.class);
    Mockito.when(mockSqlSessionFactory.getConfiguration()).thenReturn(mockConfiguration);
    Mockito.when(mockSqlSessionFactory.openSession()).thenReturn(mockSqlSession);
    try (MockedStatic<Resources> mockResources = Mockito.mockStatic(Resources.class)) {
      mockResources.when(() -> Resources.getResourceAsStream("mybatis-config.xml"))
              .thenReturn(mockConfigurationStream);
      assertThat(databaseSessionBuilder.buildSessionFactory(mockEnvironment))
              .isNotNull()
              .isEqualTo(mockSqlSession);
    }
  }

  @Test
  void buildSessionFactory_nullEnvironmentId() {
    assertThatThrownBy(() -> databaseSessionBuilder.buildSessionFactory(null))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Environment cannot be empty");
  }

  @Test
  void buildSessionFactory_iOException() {
    try (MockedStatic<Resources> mockResources = Mockito.mockStatic(Resources.class)) {
      mockResources.when(() -> Resources.getResourceAsStream("mybatis-config.xml"))
              .thenThrow(new IOException("This is a test error"));
      assertThatThrownBy(() -> databaseSessionBuilder.buildSessionFactory(Mockito.mock(Environment.class)))
              .isInstanceOf(IOException.class)
              .hasMessage("This is a test error");
    }
  }
}