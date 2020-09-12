package expense_tally.views.desktop;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.net.URL;

//ADD THIS IN THE VM option before running
//--module-path "C:\Program Files\Java\javafx-sdk-13.0.1\lib" --add-modules=javafx.controls,javafx.fxml
public final class DesktopClient extends Application {
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
