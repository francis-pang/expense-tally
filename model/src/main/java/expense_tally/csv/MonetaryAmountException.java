package expense_tally.csv;

public final class MonetaryAmountException extends Exception {
  private static final long serialVersionUID = 1L;
  public MonetaryAmountException(String message) {
    super(message);
  }
}
