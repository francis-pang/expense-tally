package expense_tally.exception;

import expense_tally.AppStringConstant;

/**
 * This class provide an application standardised way of handling String object.
 * <p>
 *   <b>Implementation Details</b><br/>
 *   Even though that there is a StringUtils class in Apache common lang3 library, this library provides a
 *   application standardised method for this project. Importing the Apache Common Lang3 library is one possible
 *   solution, but this is a huge overhead of library dependency which can be avoided by duplicating the few logics.
 * </p>
 */
public final class StringResolver {
  /**
   * Private default constructor so that no one can create an object based on this class
   */
  private StringResolver() {
  }

  /**
   * Return "NULL" if <i>string</i> is null. Otherwise, returns <i>string</i>
   * @param string string to be checked
   * @return "NULL" if <i>string</i> is null. Otherwise, returns <i>string</i>
   */
  public static String resolveNullableString(String string) {
    if (string == null) {
      return AppStringConstant.NULL.value();
    }
    return string;
  }
}
