package expense_tally.expense_manager.persistence;

import org.apache.ibatis.datasource.pooled.PooledDataSourceFactory;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.TransactionFactory;
import org.apache.ibatis.type.TypeAliasRegistry;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.sql.DataSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
class SqliteSessionFactoryBuilderTest {
  @Mock
  private PooledDataSourceFactory mockPooledDataSourceFactory;

  @Mock
  private TransactionFactory mockTransactionFactory;

  @Mock
  private Configuration mockConfiguration;

  @Mock
  private SqlSessionFactoryBuilder mockSqlSessionFactoryBuilder;

  @InjectMocks
  private SqliteSessionFactoryBuilder sqliteSessionFactoryBuilder;

  @Test
  void createSqliteSessionFactory_simpleSuccess() {
    DataSource mockDataSource = Mockito.mock(DataSource.class);
    Mockito.when(mockPooledDataSourceFactory.getDataSource()).thenReturn(mockDataSource);
    TypeAliasRegistry mockTypeAliasRegistry = Mockito.mock(TypeAliasRegistry.class);
    Mockito.when(mockConfiguration.getTypeAliasRegistry()).thenReturn(mockTypeAliasRegistry);
    SqlSessionFactory mockSqlSessionFactory = Mockito.mock(SqlSessionFactory.class);
    Mockito.when(mockSqlSessionFactoryBuilder.build(Mockito.any(Configuration.class))).
        thenReturn(mockSqlSessionFactory);
    assertThat(sqliteSessionFactoryBuilder.createSqliteSessionFactory())
        .isNotNull()
        .isEqualTo(mockSqlSessionFactory);
  }

  @Test
  void createSqliteSessionFactory_noDataSource() {
    assertThatThrownBy(() -> sqliteSessionFactoryBuilder.createSqliteSessionFactory())
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Parameter 'dataSource' must not be null");
  }

}