package expense_tally.expense_manager.persistence;

import expense_tally.model.persistence.transformation.ExpenseManagerTransaction;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

/**
 * This interface provides a way for reading data from a source.
 * <p>
 *   The source can be from a database engine, external files, e.g a XML, comma-separated values, and etc. The data
 * read will reconstruct and return in {@link expense_tally.model.persistence.transformation.ExpenseManagerTransaction}
 * format.
 * </p>
 * <p>
 *   <b>Restriction</b>: For now the interface is limited to read from a database file.
 * </p>
 *
 * @see expense_tally.model.persistence.transformation.ExpenseManagerTransaction
 */
@Mapper
public interface ExpenseReadable {

}
