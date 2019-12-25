package expense_tally.ui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

public class MainController {
  @FXML
  private TextField csvFilePathTextField;

  @FXML
  private TextField databaseFilePathTextField;

  @FXML
  protected void handleCsvFilePathButtonAction(ActionEvent event) {
    fillFilePathInTextField(csvFilePathTextField);
  }

  @FXML
  protected void handleDatabaseFilePathButton(ActionEvent event) {
    fillFilePathInTextField(databaseFilePathTextField);
  }

  private void fillFilePathInTextField(TextField textField) {
    File selectedFile = getFileFromUser();
    String filePath = selectedFile.getAbsolutePath();
    textField.setText(filePath);
  }

  private File getFileFromUser() {
    FileChooser fileChooser = new FileChooser();
    return fileChooser.showOpenDialog(new Stage());
  }
}
