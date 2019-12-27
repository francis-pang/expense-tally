package expense_tally.views.desktop;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URL;

public class DesktopClient extends Application {
  private static final Logger LOGGER = LogManager.getLogger(DesktopClient.class);
  private static final String TITLE = "Expense Tally";
  private static final String FXML_RELATIVE_PATH = "/ui/ui.fxml";

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
