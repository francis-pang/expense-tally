package expense_tally.views.desktop.controllers;

import expense_tally.csv_parser.CsvParsable;
import expense_tally.csv_parser.CsvParser;
import expense_tally.csv_parser.CsvTransaction;
import expense_tally.expense_manager.persistence.DatabaseConnectable;
import expense_tally.expense_manager.persistence.ExpenseReadable;
import expense_tally.expense_manager.persistence.ExpenseReport;
import expense_tally.expense_manager.persistence.ExpenseReportReader;
import expense_tally.expense_manager.persistence.SqlLiteConnection;
import expense_tally.expense_manager.transformation.ExpenseManagerMapKey;
import expense_tally.expense_manager.transformation.ExpenseManagerTransaction;
import expense_tally.expense_manager.transformation.ExpenseTransactionMapper;
import expense_tally.reconciliation.DiscrepantTransaction;
import expense_tally.reconciliation.ExpenseReconciler;
import expense_tally.views.desktop.model.Transaction;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public class MainController implements Initializable {
  @FXML
  TableColumn<Transaction, Boolean> tableAddRecordCheckBox;
  @FXML
  private TextField databaseFilePathTextField;
  @FXML
  private TextField csvFilePathTextField;
  @FXML
  private TableView<Transaction> transactionTableView;
  @FXML
  private HBox csvFilePathHBox;
  @FXML
  private HBox databaseFilePathHBox;
  @FXML
  private VBox userFormVBox;
  @FXML
  private BorderPane baseBorderPane;

  private static final Logger LOGGER = LogManager.getLogger(MainController.class);

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    tableAddRecordCheckBox.setCellValueFactory(cellData -> cellData.getValue().meantToBeAddedToDatabaseProperty());
    tableAddRecordCheckBox.setCellFactory(parameter -> new CheckBoxTableCell<>());
  }

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
    File file = getFileFromUser();
    String filePath = file.getAbsolutePath();
    textField.setText(filePath);
  }

  private File getFileFromUser() {
    FileChooser fileChooser = new FileChooser();
    // There is no need to check if the file path is a valid file, this is provided off the shell by showOpenDialog()
    return fileChooser.showOpenDialog(new Stage());
  }

  public void handleGenerateTransactionTableButtonAction() {
    LOGGER.atTrace().log("User clicks on generate transaction button");
    // Validate text field content
    String databaseFilePath = databaseFilePathTextField.getText();
    String csvFilePath = csvFilePathTextField.getText();

    // Parse CSV file
    CsvParsable csvParsable = new CsvParser();
    List<CsvTransaction> csvTransactions = null;
    try {
      csvTransactions = csvParsable.parseCsvFile(csvFilePath);
    } catch (IOException e) {
      String errorMessage = String.format("Unable to read from the csv file: %s", csvFilePath);
      LOGGER.atWarn().withThrowable(e).log(errorMessage);
      addErrorTextBelowInput(csvFilePathHBox, errorMessage);
      return;
    }

    // Parse database
    DatabaseConnectable databaseConnectable = new SqlLiteConnection(databaseFilePath);
    ExpenseReadable expenseReadable = new ExpenseReportReader(databaseConnectable);
    List<ExpenseReport> expenseReports;
    try {
      expenseReports = expenseReadable.getExpenseTransactions();
    } catch (SQLException e) {
      String errorMessage = String.format("Unable to read from the database: %s",databaseFilePath);
      LOGGER.atWarn().withThrowable(e).log(errorMessage);
      addErrorTextBelowInput(databaseFilePathHBox, errorMessage);
      return;
    }

    Map<ExpenseManagerMapKey, List<ExpenseManagerTransaction>> expenseManagerMap =
        ExpenseTransactionMapper.mapExpenseReportsToMap(expenseReports);

    // Reconcile both data source
    List<DiscrepantTransaction> discrepantTransactions =
        ExpenseReconciler.reconcileBankData(csvTransactions, expenseManagerMap);

    List<Transaction> tableViewTransactions = new ArrayList<>();
    discrepantTransactions.forEach(discrepantTransaction -> {
      Transaction transaction = Transaction.from(discrepantTransaction);
      tableViewTransactions.add(transaction);
    });

    ObservableList<Transaction> transactionObservableList =
        FXCollections.observableArrayList(tableViewTransactions);

    // Convert to table view
    transactionTableView.setItems(transactionObservableList);
  }

  private void addErrorTextBelowInput(HBox inputHBox, String errorMessage) {
    Text errorText = new Text(errorMessage);
    errorText.setSelectionFill(Color.RED); //FIXME: Color is not red
    // FIXME: There is an issue with adding it straight after the input box.
    userFormVBox.getChildren().add(errorText);
    // FIXME: Auto-resizing does not work
    baseBorderPane.resize(baseBorderPane.getMaxWidth(), baseBorderPane.getMaxHeight());
  }
}
