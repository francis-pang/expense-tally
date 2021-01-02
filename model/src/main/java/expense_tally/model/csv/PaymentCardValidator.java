package expense_tally.model.csv;

import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class check on the validity of the payment card provided.
 */
public final class PaymentCardValidator {
  private static final List<Integer> MASTER_CARD_PREFIX_LIST = List.of(51, 52, 53, 54, 55);
  private static final int MASTER_CARD_LENGTH = 16;
  private static final Pattern REGEX = Pattern.compile("\\d+"); // All digits

  /**
   * Default constructor
   */
  private PaymentCardValidator() {
  }

  /**
   * Validates if the payment card number is valid
   *
   * @param cardNumber      payment card number
   * @param transactionType Transaaction type of payment
   * @return true if payment card number is valid, or else return false
   */
  public static boolean isPaymentCardValid(String cardNumber, TransactionType transactionType) {
    String trimmedCardNumber = cardNumber.replace("-", StringUtils.EMPTY);
    if (trimmedCardNumber.length() != MASTER_CARD_LENGTH) {
      return false;
    }
    if (transactionType.equals(TransactionType.MASTERCARD)) {
      boolean valid = false;
      for (Integer prefix : MASTER_CARD_PREFIX_LIST) {
        String prefixString = Integer.toString(prefix);
        if (trimmedCardNumber.startsWith(prefixString)) {
          valid = true;
          break;
        }
      }
      if (!valid) {
        return false;
      }
    }
    Matcher matcher = REGEX.matcher(trimmedCardNumber);
    return matcher.matches();
  }
}
