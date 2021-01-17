package expense_tally.expense_manager.persistence.database;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * This interface defines a interface which allows the user of the interface to be able to connect to the database.
 */
public interface DatabaseConnectable {
  /**
   * Returns the connection to the database
   *
   * @return the connection to the database
   * @throws SQLException when there is an error accessing the database
   */
  Connection connect() throws SQLException;
}
