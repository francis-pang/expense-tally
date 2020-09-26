package expense_tally.expense_manager.persistence;

import expense_tally.expense_manager.mapper.ExpenseReportMapper;
import expense_tally.model.persistence.database.ExpenseReport;
import org.apache.ibatis.datasource.pooled.PooledDataSourceFactory;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.TransactionFactory;
import org.apache.ibatis.type.TypeAliasRegistry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.sql.DataSource;
import java.util.Objects;

public final class SqliteSessionFactoryBuilder {
  private static final Logger LOGGER = LogManager.getLogger(SqliteSessionFactoryBuilder.class);
  private static final String ENVIRONMENT_ID = "file_sqlite";
  private static final boolean AGGRESSIVE_LAZY_LOADING = true;

  private final PooledDataSourceFactory pooledDataSourceFactory;
  private final TransactionFactory transactionFactory;
  private final SqlSessionFactoryBuilder sqlSessionFactoryBuilder;
  private final Configuration configuration;

  public SqliteSessionFactoryBuilder(PooledDataSourceFactory pooledDataSourceFactory,
                                     TransactionFactory transactionFactory,
                                     SqlSessionFactoryBuilder sqlSessionFactoryBuilder,
                                     Configuration configuration) {
    this.pooledDataSourceFactory = Objects.requireNonNull(pooledDataSourceFactory);
    this.transactionFactory = Objects.requireNonNull(transactionFactory);
    this.sqlSessionFactoryBuilder = Objects.requireNonNull(sqlSessionFactoryBuilder);
    this.configuration = Objects.requireNonNull(configuration);
  }

  /**
   *
   * @return
   */
  public SqlSessionFactory createSqliteSessionFactory() {
    // This can be further optimised to just provide a configuration
    DataSource dataSource = pooledDataSourceFactory.getDataSource();
    Environment environment = createEnvironment(dataSource);
    configure(environment);
    LOGGER.atDebug().log("SQL session configuration: {}", configuration);
    return sqlSessionFactoryBuilder.build(configuration);
  }

  private Environment createEnvironment(DataSource dataSource) {
    return new Environment.Builder(ENVIRONMENT_ID)
        .transactionFactory(transactionFactory)
        .dataSource(dataSource)
        .build();
  }

  private void configure(Environment environment) {
    configuration.setEnvironment(environment);
    // This can be generalised to take in a list of class
    configuration.addMapper(ExpenseReportMapper.class);
    configuration.setAggressiveLazyLoading(AGGRESSIVE_LAZY_LOADING);
    TypeAliasRegistry typeAliasRegistry = configuration.getTypeAliasRegistry();
    typeAliasRegistry.registerAlias(ExpenseReport.class);
  }
}
