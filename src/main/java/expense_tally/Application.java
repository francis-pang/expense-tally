package expense_tally;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.sql.SQLException;

public class Application {
  private static final Logger LOGGER = LogManager.getLogger(Application.class);

  public static void main(String[] args) {
    final int INSUFFICIENT_PARAMETERS_ERR_CODE = 1;
    final int CSV_FILE_PARSING_ERR_CODE = 2;
    final int DATABASE_ERR_CODE = 3;

    //TODO: For now, we ignore any parameters after 2nd parameters, next time we can handle them.
    Driver driver = new Driver();
    try {
      driver.readArgs(args);
    } catch (IllegalArgumentException ex) {
      LOGGER.error("Error processing argument", ex);
      System.exit(INSUFFICIENT_PARAMETERS_ERR_CODE);
    }

    try {
      driver.reconcileData();
    } catch (IOException ioException) {
      LOGGER.error("Error reading CSV file",ioException);
      System.exit(CSV_FILE_PARSING_ERR_CODE);
      //TODO: Print a error message, then exit the program
    } catch (SQLException sqlException) {
      LOGGER.error("Error reading from database", sqlException);
      //TODO: Print a error message, then exit the program
      System.exit(DATABASE_ERR_CODE);
    }
  }
}
