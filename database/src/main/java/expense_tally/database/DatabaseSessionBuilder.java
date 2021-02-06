package expense_tally.database;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

/**
 * This class provides the method to construct the database session factory for a particular database.
 * <p>
 *     A database session factory is a container used to of a database session. The database session factory will
 * contain all the configurations needed to establish a database session with a specific database.
 * </p>
 *
 */
public final class DatabaseSessionBuilder {
  private static final Logger LOGGER = LogManager.getLogger(DatabaseSessionBuilder.class);
  // This is default to be at root of default folder
  private static final String CONFIGURATION_FILE_NAME = "mybatis-config.xml";
  private static DatabaseSessionBuilder databaseSessionBuilder;

  private final SqlSessionFactoryBuilder sqlSessionFactoryBuilder;

  private DatabaseSessionBuilder(SqlSessionFactoryBuilder sqlSessionFactoryBuilder) {
    this.sqlSessionFactoryBuilder = Objects.requireNonNull(sqlSessionFactoryBuilder);
  }

  public static DatabaseSessionBuilder of(SqlSessionFactoryBuilder sqlSessionFactoryBuilder) {
    if (databaseSessionBuilder == null) {
      LOGGER.atInfo().log("Creating new DatabaseSessionBuilder");
      databaseSessionBuilder = new DatabaseSessionBuilder(sqlSessionFactoryBuilder);
    }
    return databaseSessionBuilder;
  }

  /**
   * Returns a newly created database session factory based on the <i>environmentId</i>.
   * <p>A database session factory is needed to of a database session. This database session maintains the database
   * connection to the actual database server. The database server here refers to an actual remote database server, or a
   * database file</p>
   * @return the database session factory based on the <i>environmentId</i>.
   * @throws IOException when there is issue to read the myBatis configuration resource
   */
  public SqlSession buildSessionFactory(Environment environment) throws IOException {
    if (environment == null) {
      throw new IllegalArgumentException("Environment cannot be empty");
    }

    InputStream configurationInputStream = Resources.getResourceAsStream(CONFIGURATION_FILE_NAME);
    SqlSessionFactory sqlSessionFactory = sqlSessionFactoryBuilder.build(configurationInputStream);
    Configuration configuration = sqlSessionFactory.getConfiguration();
    configuration.setEnvironment(environment);
    return sqlSessionFactory.openSession();
  }
}
