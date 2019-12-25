package expense_tally.views.desktop.model;

import expense_tally.reconciliation.model.DiscrepantTransaction;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;

public class Transaction {
  private SimpleBooleanProperty meantToBeAddedToDatabase;
  private SimpleStringProperty time;
  private SimpleDoubleProperty amount;
  private SimpleStringProperty description;
  private SimpleStringProperty type;

  public boolean isMeantToBeAddedToDatabase() {
    return meantToBeAddedToDatabase.get();
  }

  public SimpleBooleanProperty meantToBeAddedToDatabaseProperty() {
    return meantToBeAddedToDatabase;
  }

  public String getTime() {
    return time.get();
  }

  public SimpleStringProperty timeProperty() {
    return time;
  }

  public double getAmount() {
    return amount.get();
  }

  public SimpleDoubleProperty amountProperty() {
    return amount;
  }

  public String getDescription() {
    return description.get();
  }

  public SimpleStringProperty descriptionProperty() {
    return description;
  }

  public String getType() {
    return type.get();
  }

  public SimpleStringProperty typeProperty() {
    return type;
  }

  public static Transaction from(DiscrepantTransaction discrepantTransaction) {
    Transaction transaction = new Transaction();
    transaction.time = new SimpleStringProperty(discrepantTransaction.getTime().toString());
    transaction.amount = new SimpleDoubleProperty(discrepantTransaction.getAmount());
    transaction.description = new SimpleStringProperty(discrepantTransaction.getDescription());
    transaction.type = new SimpleStringProperty(discrepantTransaction.getType().toString());
    transaction.meantToBeAddedToDatabase = new SimpleBooleanProperty(false);
    return transaction;
  }


}
