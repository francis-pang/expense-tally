package expense_tally.model.csv;

import java.util.StringJoiner;
import java.util.stream.Stream;

/**
 * The type of transaction.
 * <p>Normally, the value of the transaction type is the abbreviation for the actual description</p>
 */
public enum TransactionType {
  MASTERCARD("MST", true),
  NETS("NETS", true),
  POINT_OF_SALE("POS", true),
  GIRO("IBG", true),
  GIRO_COLLECTION("GRO", true),
  FAST_PAYMENT("ICT", true),
  FAST_COLLECTION("IDT", true),
  FUNDS_TRANSFER_I("ITR", true),
  FUNDS_TRANSFER_A("ATR", true),
  BILL_PAYMENT("BILL", true),
  PAY_NOW("PayNow Transfer", true),
  CASH_WITHDRAWAL("AWL", false),
  INTEREST_EARNED("INT", false),
  STANDING_INSTRUCTION("SI", false),
  SALARY("SAL", false),
  MAS_ELECTRONIC_PAYMENT_SYSTEM_RECEIPT("MER", false);

  private final String value;
  private final boolean meantToBeProcessed;

  /**
   * A constructor taking the <i>value</i> of the transaction type
   *
   * @param value representation of the transaction type
   */
  TransactionType(String value, boolean meantToBeProcessed) {
    this.value = value;
    this.meantToBeProcessed = meantToBeProcessed;
  }

  /**
   * Returns the {@link TransactionType} given its string form
   *
   * @param transactionTypeStr trsnaction type in string form
   * @return the type of transaction given its string form, null if not found
   */
  public static TransactionType resolve(String transactionTypeStr) {
    if (transactionTypeStr == null || transactionTypeStr.isBlank()) {
      return null;
    }
    
    return Stream.of(values())
      .filter(transactionType -> transactionType.value.equals(transactionTypeStr))
      .findFirst()
      .orElse(null);
  }

  /**
   * Returns the value of the transaction type
   *
   * @return the value of the transaction type
   */
  public String value() {
    return this.value;
  }

  public boolean isMeantToBeProcessed() {
    return meantToBeProcessed;
  }

  @Override
  public String toString() {
    return new StringJoiner(", ", TransactionType.class.getSimpleName() + "[", "]")
        .add("value='" + value + "'")
        .toString();
  }
}
