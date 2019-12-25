package expense_tally.ui;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

public class MainController {
  @FXML
  protected void handleCsvFilePathTextFieldAction(MouseEvent event) {
    handleFilePathTextFieldAction(event);
  }

  @FXML
  protected void handleDatabaseFilePathTextFieldAction(MouseEvent event) {
    handleFilePathTextFieldAction(event);
  }

  private void handleFilePathTextFieldAction(MouseEvent event) {
    TextField textField = (TextField) event.getSource();
    String filePath = getFilePathFromUser();
    textField.setText(filePath);
  }

  private String getFilePathFromUser() {
    FileChooser fileChooser = new FileChooser();
    File selectedFile = fileChooser.showOpenDialog(new Stage());
    return selectedFile.getAbsolutePath();
  }
}
