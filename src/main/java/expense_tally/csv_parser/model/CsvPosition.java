package expense_tally.csv_parser.model;

public enum CsvPosition {
  // All these are zero based position
  TRANSACTION_DATE(0),
  REFERENCE(1),
  DEBIT_AMOUNT(2),
  CREDIT_AMOUNT(3),
  TRANSACTION_REF_1(4),
  TRANSACTION_REF_2(5),
  TRANSACTION_REF_3(6)
  ;

  public final int position;

  CsvPosition(int position) {
    this.position = position;
  }
}
