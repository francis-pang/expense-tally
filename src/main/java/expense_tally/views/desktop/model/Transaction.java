package expense_tally.views.desktop.model;

import expense_tally.reconciliation.model.DiscrepantTransaction;
import javafx.scene.control.CheckBox;

import java.time.LocalDate;

public class Transaction {
  private CheckBox checkBox;
  private LocalDate time;
  private double amount;
  private String description;
  private String type;

  public static Transaction from(DiscrepantTransaction discrepantTransaction) {
    Transaction transaction = new Transaction();
    transaction.time = discrepantTransaction.getTime();
    transaction.amount = discrepantTransaction.getAmount();
    transaction.description = discrepantTransaction.getDescription();
    transaction.type = discrepantTransaction.getType().value();
    transaction.checkBox = new CheckBox();
    return transaction;
  }
}
