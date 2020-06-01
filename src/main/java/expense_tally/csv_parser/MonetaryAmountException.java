package expense_tally.csv_parser;

public final class MonetaryAmountException extends Exception {
  private static final long serialVersionUID = 1L;
  public MonetaryAmountException(String message) {
    super(message);
  }
}
