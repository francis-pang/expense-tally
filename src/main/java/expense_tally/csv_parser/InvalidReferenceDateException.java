package expense_tally.csv_parser;

public class InvalidReferenceDateException extends Exception {
  private static final long serialVersionUID = 10763608L;

  public InvalidReferenceDateException(String message) {
    super(message);
  }
}
