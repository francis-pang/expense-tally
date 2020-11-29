package expense_tally.views;

/**
 * This enum defines all the parameters used to pass into the application via the command line tool.
 */
public enum AppParameter {
  DATABASE_PATH("database-filepath", true),
  CSV_PATH("csv-filepath", true)
  ;

  private String value;
  private boolean isCompulsory;

  AppParameter(String value, boolean isCompulsory) {
    this.value = value;
    this.isCompulsory = isCompulsory;
  }

  public String value() {
    return value;
  }

  public boolean isCompulsory() {
    return isCompulsory;
  }

  /**
   * Returns the {@link AppParameter} given its string form
   *
   * @param appParametersStr application per type in string form
   * @return the type of transaction given its string form, null if not found
   */
  public static AppParameter resolve(String appParametersStr) {
    for (AppParameter appParameter : values()) {
      if (appParameter.value.equals(appParametersStr)) {
        return appParameter;
      }
    }
    return null;
  }
}
