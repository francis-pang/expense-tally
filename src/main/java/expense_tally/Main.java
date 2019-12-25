package expense_tally;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URL;

/**
 * This class acts as the Dependency Injection container. It create all the dependencies and inject into the rest of
 * the class
 */
public class Main extends Application {
    private static final Logger LOGGER = LogManager.getLogger(Main.class);
    private static final String TITLE = "Expense Tally";
    private static final String FXML_RELATIVE_PATH = "/ui/ui.fxml";

//    public static void main(String[] args) {
//        final int INSUFFICIENT_PARAMETERS_ERR_CODE = 1;
//        final int CSV_FILE_PARSING_ERR_CODE = 2;
//        final int DATABASE_ERR_CODE = 3;
//
//        //TODO: For now, we ignore any parameters after 2nd parameters, next time we can handle them.
//        try {
//            ExpenseAccountant expenseAccountant = new ExpenseAccountant(args);
//            expenseAccountant.reconcileData();
//        } catch (IOException ioException) {
//            LOGGER.error("Error reading CSV file", ioException);
//            System.exit(CSV_FILE_PARSING_ERR_CODE);
//            //TODO: Print a error message, then exit the program
//        } catch (SQLException sqlException) {
//            LOGGER.error("Error reading from database", sqlException);
//            //TODO: Print a error message, then exit the program
//            System.exit(DATABASE_ERR_CODE);
//        }
//    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader();
        URL xmlUrl = getClass().getResource(FXML_RELATIVE_PATH);
        loader.setLocation(xmlUrl);
        BorderPane borderPane = loader.load();
        Scene root = new Scene(borderPane);
        primaryStage.setTitle(TITLE);
        primaryStage.setScene(root);
        primaryStage.show();
    }
}
