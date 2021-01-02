package expense_tally;

/**
 * This enum stores all the string constants used throughout the application, regardless of module.
 */
public enum AppStringConstant {
  /**
   * Refers to a null object
   */
  NULL("NULL");

  private final String value;

  /**
   * Default private constructor
   * @param value value
   */
  AppStringConstant(String value) {
    this.value = value;
  }

  /**
   * Return {@link AppStringConstant} that represent this string
   * @param value String value of the {@link AppStringConstant}
   * @return {@link AppStringConstant} that represent this string
   * @throws IllegalArgumentException if value is null or empty.
   */
  public static AppStringConstant resolve(String value) {
    if (value == null || value.isBlank()) {
      throw new IllegalArgumentException("value cannot be null or empty");
    }
    for (AppStringConstant appStringConstant : values()) {
      if (appStringConstant.value.contentEquals(value)) {
        return appStringConstant;
      }
    }
    return null;
  }

  /**
   * Return String represented value of the enumeration.
   * @return String represented value of the enumeration.
   */
  public String value() {
    return this.value;
  }
}
