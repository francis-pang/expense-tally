package expense_tally.views.desktop.controllers;

import expense_tally.csv_parser.CsvParsable;
import expense_tally.csv_parser.CsvParser;
import expense_tally.csv_parser.model.CsvTransaction;
import expense_tally.expense_manager.DatabaseConnectable;
import expense_tally.expense_manager.ExpenseReadable;
import expense_tally.expense_manager.ExpenseReportReader;
import expense_tally.expense_manager.ExpenseTransactionMapper;
import expense_tally.expense_manager.SqlLiteConnection;
import expense_tally.expense_manager.model.ExpenseManagerMapKey;
import expense_tally.expense_manager.model.ExpenseManagerTransaction;
import expense_tally.expense_manager.model.ExpenseReport;
import expense_tally.reconciliation.ExpenseReconciler;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class MainController {
  @FXML
  private TextField databaseFilePathTextField;

  @FXML
  private TextField csvFilePathTextField;

  @FXML
  private TableView transactionTableView;

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
    //TODO: Check on file validity
    return selectedFile.getAbsolutePath();
  }

  protected void handleGenerateTransactionTableButtonAction(MouseEvent mouseEvent) {
    // Validate text field content
    String databaseFilePath = databaseFilePathTextField.getText();
    String csvFilePath = csvFilePathTextField.getText();

    // Parse CSV file
    CsvParsable csvParsable = new CsvParser();
    List<CsvTransaction> csvTransactions = null;
    try {
      csvTransactions = csvParsable.parseCsvFile(csvFilePath);
    } catch (IOException e) {
      //FIXME
      e.printStackTrace();
    }

    // Parse database
    DatabaseConnectable databaseConnectable = new SqlLiteConnection(databaseFilePath);
    ExpenseReadable expenseReadable = new ExpenseReportReader(databaseConnectable);
    List<ExpenseReport> expenseReports = null;
    try {
      expenseReports = expenseReadable.getExpenseTransactions();
    } catch (SQLException e) {
      //FIXME
      e.printStackTrace();
    }
    Map<ExpenseManagerMapKey, List<ExpenseManagerTransaction>> expenseManagerMap =
        ExpenseTransactionMapper.mapExpenseReportsToMap(expenseReports);

    // Reconcile both data source
    ExpenseReconciler.reconcileBankData(csvTransactions, expenseManagerMap);

    // Convert to table view
  }
}
