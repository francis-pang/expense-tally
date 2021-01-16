package expense_tally.expense_manager.persistence.database;

import expense_tally.Exception.StringResolver;
import org.apache.ibatis.io.Resources;
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
 *     A database session factory is a container used to create a database session. The database session factory will
 * contain all the configurations needed to establish a database session with a specific database.
 * </p>
 *
 */
public final class DatabaseSessionFactoryBuilder {
  private static final Logger LOGGER = LogManager.getLogger(DatabaseSessionFactoryBuilder.class);
  // This is default to be at root of default folder
  private static final String CONFIGURATION_FILE_NAME = "mybatis-config.xml";

  private final SqlSessionFactoryBuilder sqlSessionFactoryBuilder;

  public DatabaseSessionFactoryBuilder(SqlSessionFactoryBuilder sqlSessionFactoryBuilder) {
    this.sqlSessionFactoryBuilder = Objects.requireNonNull(sqlSessionFactoryBuilder);
  }

  /**
   * Returns a newly created database session factory based on the <i>environmentId</i>.
   * <p>A database session factory is needed to create a database session. This database session maintains the database
   * connection to the actual database server. The database server here refers to an actual remote database server, or a
   * database file</p>
   * @param environmentId the environment ID specified inside the list of <i>environments</i> element tags inside the
   *                     MyBatis configuration file
   * @return the database session factory based on the <i>environmentId</i>.
   * @throws IOException when there is issue to read the myBatis configuration resource
   */
  public SqlSessionFactory buildSessionFactory(String environmentId) throws IOException {
    if (environmentId == null || environmentId.isBlank()) {
      LOGGER.atError()
          .log("Environment ID is invalid: \"{}\"", StringResolver.resolveNullableString(environmentId));
      throw new IllegalArgumentException("Environment ID cannot be empty");
    }
    InputStream configurationInputStream = Resources.getResourceAsStream(CONFIGURATION_FILE_NAME);
    return sqlSessionFactoryBuilder.build(configurationInputStream, environmentId);
  }
}
