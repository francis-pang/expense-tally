package expense_tally.expense_manager.transformation;

/**
 * Different type of payment method for {@link ExpenseManagerTransaction}
 */
public enum PaymentMethod {
  CASH("Cash"),
  CREDIT_CARD("Credit Card"),
  DEBIT_CARD("Debit 5548-2741-0014-1067"),
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
    if (paymentMethodStr == null || "".equals(paymentMethodStr.trim())) {
      return null;
    }
    for (PaymentMethod paymentMethod : values()) {
      if (paymentMethod.value.equals(paymentMethodStr)) {
        return paymentMethod;
      }
    }
    return null;
  }
}
