package expense_tally.model.persistence.transformation;

/**
 * Different type of payment method for {@link ExpenseManagerTransaction}
 */
public enum PaymentMethod {
  CASH("Cash"),
  CREDIT_CARD("Credit Card"),
  DBS_DEBIT_CARD("Debit 1067"),
  YOUTRIP_DEBIT_CARD("Debit 6709"),
  UOD_ONE_CREDIT_CARD("Credit 0665"),
  ELECTRONIC_TRANSFER("Electronic Transfer"),
  GIRO("Giro"),
  GRAY_PAY("Grab Pay"),
  NETS("NETS"),
  I_BANKING("iBanking");

  private final String value;

  /**
   * Construct a payment method with the <i>value</i>
   *
   * @param value string representing the payment method
   */
  PaymentMethod(String value) {
    this.value = value;
  }

  /**
   * Returns the payment method represented by this <i>value</i>
   *
   * @param paymentMethodStr content of the payment method
   * @return the payment method represented by this <i>value</i>
   */
  public static PaymentMethod resolve(String paymentMethodStr) {
    if (paymentMethodStr == null || paymentMethodStr.isBlank()) {
      return null;
    }
    for (PaymentMethod paymentMethod : values()) {
      if (paymentMethod.value.equals(paymentMethodStr)) {
        return paymentMethod;
      }
    }
    return null;
  }

  public String value() {
    return value;
  }
}
