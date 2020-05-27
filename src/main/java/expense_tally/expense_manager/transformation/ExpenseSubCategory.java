package expense_tally.expense_manager.transformation;

public enum ExpenseSubCategory {
  ALCOHOL_AND_RESTAURANT("Alcohol/ Restaurant"),
  KARAOKE_PARTY("Karaoke/ Party"),
  FOOD_COURT_AND_FAST_FOOD("Food court/ Fast food"),
  SPORTS("Sports"),
  SNACK_AND_SWEET("Snack/ sweet"),
  CLOTHING("Clothing"),
  TAXI_AND_PRIVATE_HIRE("Taxi/ Private Hire"),
  PAY_FOR_OTHERS("Pay For Others"),
  CONSUMABLES("Consumables"),
  DENTAL("Dental"),
  FOOD_OTHER("Food other"),
  EYE_CARE("Eye Care"),
  BREAKFAST("Breakfast"),
  PERSONAL_GROOMING("Personal Grooming"),
  OTHER_ENTERTAINMENT("Entertainment Other"),
  TREAT("Friend/ Girl treat"),
  OTHER_HEALTHCARE("Healthcare Other"),
  TELEPHONE_BILL("Telephone bill"),
  UNKNOWN("Personal/ unknown"),
  ELECTRONICS("Appliance/ Electronic"),
  VACATION_PUBLIC_TRANSPORT("Public Transport"),
  COMPANY_EXPENDITURE("Company expenditure"),
  PUBLIC_TRANSPORT("Bus/ MRT/ LRT"),
  HEALTH_INSURANCE("Health Insurance"),
  VACATION_FOOD("Food"),
  ATTRACTION_ENTRY_FEE("Entrance Fee For Attraction"),
  MISC("Misc"),
  VACATION_ACCOMODATION("Accomodation"),
  ALCOHOL_PARTY("Alcohol / Parties"),
  BEAUTY_PRODUCT("Beauty product"),
  MOVIE_CONCERT("Movies / Concert"),
  GROCERY("Grocery"),
  SOUVENIR("Souvenirs"),
  TRANSPORT_OTHER("Transport Other"),
  MEDICIAL_PRESCRIPTION("Prescription/ medicine"),
  MEDICAL("Medical"),
  AIRPLANE_TRAIN("Airplane/ Train"),
  HOUSEHOLD_OTHER("Household Other"),
  VACATION_TRANSPORT_INTRACITY("Transport within city"),
  GIFT_TREAT("Gift/ Treat");

  private final String value;

  /**
   * Default constructor
   *
   * @param value the value of the value
   */
  ExpenseSubCategory(String value) {
    this.value = value;
  }

  /**
   * Returns the {@link ExpenseCategory} given its string form
   *
   * @param subExpenseCategoryStr expense category in string form
   * @return the category of the expense record given its string form, null if not found
   */
  public static ExpenseSubCategory resolve(String subExpenseCategoryStr) {
    if (subExpenseCategoryStr == null || "".equals(subExpenseCategoryStr.trim())) {
      return null;
    }
    for (ExpenseSubCategory expenseSubCategory : values()) {
      if (expenseSubCategory.value.equals(subExpenseCategoryStr)) {
        return expenseSubCategory;
      }
    }
    return null;
  }

  public String value() {
    return value;
  }
}
