package expense_tally.expense_manager;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * This interface defines a intreface which allows the user of the interface to be able to connect to the database.
 */
public interface DatabaseConnectable {
    Connection connect() throws SQLException;
}
