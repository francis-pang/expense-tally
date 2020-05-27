package expense_tally.expense_manager;

import expense_tally.expense_manager.persistence.ExpenseReport;
import expense_tally.expense_manager.transformation.ExpenseCategory;
import expense_tally.expense_manager.transformation.ExpenseManagerTransaction;
import expense_tally.expense_manager.transformation.ExpenseSubCategory;
import expense_tally.expense_manager.transformation.ExpenseTransactionMapper;
import expense_tally.expense_manager.transformation.PaymentMethod;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.atIndex;
import static org.assertj.core.api.Assertions.entry;

@ExtendWith(SoftAssertionsExtension.class)
class ExpenseManagerTransactionMapperTest {
  private ExpenseReport constructExpenseReport(int id, String account, double amount, String category,
                                               String subcategory, String paymentMethod, String description,
                                               String expensedTime, String modificationTime, double referenceAmount,
                                               String status, String property1, String property2, String property3,
                                               String property4, String property5, String tax, String expenseTag) {
    ExpenseReport expenseReport = new ExpenseReport();
    expenseReport.setId(id);
    expenseReport.setAccount(account);
    expenseReport.setAmount(Double.toString(amount));
    expenseReport.setCategory(category);
    expenseReport.setSubcategory(subcategory);
    expenseReport.setPaymentMethod(paymentMethod);
    expenseReport.setDescription(description);
    expenseReport.setExpensedTime(Instant.parse(expensedTime).toEpochMilli());
    expenseReport.setModificationTime(Instant.parse(modificationTime).toEpochMilli());
    if (referenceAmount > 0) {
      expenseReport.setReferenceNumber(Double.toString(referenceAmount));
    } else {
      expenseReport.setReferenceNumber("");
    }
    expenseReport.setStatus(status);
    expenseReport.setProperty1(property1);
    expenseReport.setProperty2(property2);
    expenseReport.setProperty3(property3);
    expenseReport.setProperty4(property4);
    expenseReport.setProperty5(property5);
    expenseReport.setTax(tax);
    expenseReport.setExpenseTag(expenseTag);
    return expenseReport;
  }

  private ExpenseManagerTransaction constructExpenseManagerTransaction(
      double amount,
      ExpenseCategory category,
      ExpenseSubCategory subcategory,
      PaymentMethod paymentMethod,
      String description,
      String expensedTime,
      double referenceAmount) {
    ExpenseManagerTransaction expenseManagerTransaction = ExpenseManagerTransaction.createInstanceOf(amount,
        category, subcategory, paymentMethod, description, Instant.parse(expensedTime));
    expenseManagerTransaction.setReferenceAmount(referenceAmount);
    return expenseManagerTransaction;
  }

  /**
   * 1. Empty list
   * 2. Null list
   * 3. List with 1 elements
   * 4. List with more than 1 elements, maybe 3. Among the elements, we will have varying test cases
   * 5. List with 1 element, blank reference
   */

  /*
   * Test Case: Take in an empty expense reports
   * Expected Output: The mapped expense transactions is empty
   */
  @Test
  void mapExpenseReportsToList_emptyExpenseReports() {
    List<ExpenseReport> testingExpenseReports = new ArrayList<>();
    assertThat(ExpenseTransactionMapper.mapExpenseReportsToList(testingExpenseReports)).isEmpty();
  }

  /*
   * Test Case: Take in an null expense reports
   * Expected Output: An NullPointerException will occur
   */
  @Test
  void mapExpenseReportsToList_nullExpenseReports() {
    Assertions.assertThatThrownBy(() -> {
      ExpenseTransactionMapper.mapExpenseReportsToList(null);
    }).isInstanceOf(NullPointerException.class);
  }

  /*
   * Test Case: Take in one expense reports, and all the reference doesn't match any enum
   * Expected Output: A list containing of 1 expense transaction will be returned
   */
  @Test
  void mapExpenseReportsToList_oneExpenseReport(SoftAssertions softly) {
    // Create test data
    List<ExpenseReport> testingExpenseReports = new ArrayList<>();
    testingExpenseReports.add(constructExpenseReport(
        1,
        "Test Account",
        1.78,
        "Vacation",
        "Airplane/ Train",
        "Credit Card",
        "Description",
        "2019-05-20T20:54:03.00Z",
        "2019-05-20T20:54:03.00Z",
        0,
        "Clear",
        "Property",
        "Property2",
        "Property3",
        "Property4",
        "Property5",
        "1.45",
        "ExpenseTag"
    ));

    List<ExpenseManagerTransaction> actualExpenseManagerTransactions =
        ExpenseTransactionMapper.mapExpenseReportsToList(testingExpenseReports);
    softly.assertThat(actualExpenseManagerTransactions).hasSize(1);
    softly.assertThat(actualExpenseManagerTransactions)
        .extracting("amount").contains(1.78, atIndex(0));
    softly.assertThat(actualExpenseManagerTransactions)
        .extracting("category").contains(ExpenseCategory.VACATION, atIndex(0));
    softly.assertThat(actualExpenseManagerTransactions)
        .extracting("subcategory").contains(ExpenseSubCategory.AIRPLANE_TRAIN, atIndex(0));
    softly.assertThat(actualExpenseManagerTransactions)
        .extracting("paymentMethod").contains(PaymentMethod.CREDIT_CARD, atIndex(0));
    softly.assertThat(actualExpenseManagerTransactions)
        .extracting("description").contains("Description", atIndex(0));
    softly.assertThat(actualExpenseManagerTransactions)
        .extracting("expendedTime").contains(Instant.parse("2019-05-20T20:54:03.00Z"), atIndex(0));
    softly.assertThat(actualExpenseManagerTransactions)
        .extracting("referenceAmount").contains(0.0, atIndex(0));
    softly.assertAll();
  }

  /*
   * Test Case: Take in one expense reports, and all the reference doesn't match any enum
   * Expected Output: A list containing of multiple expense transaction will be returned, and all of them matches
   * what we expect
   */
  @Test
  void mapExpenseReportsToList_multiple(SoftAssertions softly) {
    // Create test data
    List<ExpenseReport> testingExpenseReports = new ArrayList<>();
    testingExpenseReports.add(constructExpenseReport(
        1,
        "Test Account",
        6.8,
        "Food",
        "Food court/ Fast food",
        "Credit Card",
        "Dinner. Single bowl salad. Happy Tummy",
        "2019-05-20T20:54:03.00Z",
        "2019-05-20T20:54:03.00Z",
        0,
        "Clear",
        "",
        "",
        "",
        "",
        "",
        "",
        ""
    ));

    testingExpenseReports.add(constructExpenseReport(
        1, //Same ID, to show that ID doesn't matter
        "Test Account",
        944,
        "Personal",
        "Karaoke/ Party",
        "Electronic Transfer",
        "Monthly allowed expenditure",
        "2018-01-31T16:00:00.00Z",
        "2019-05-20T20:54:03.00Z",
        7,
        "Clear",
        "",
        "",
        "",
        "",
        "",
        "",
        ""
    ));

    testingExpenseReports.add(constructExpenseReport(
        3,
        "Test Account",
        5.2,
        "Aesthetic",
        "Clothing",
        "NETS",
        "Ice 2.5 kg",
        "2018-01-31T16:00:00.00Z",
        "2019-05-20T20:54:03.00Z",
        0,
        "Clear",
        "",
        "2018-01-16-18-02-00-906.jpg",
        "",
        "",
        "",
        "",
        "1 PCS"
    ));

    List<ExpenseManagerTransaction> actualExpenseManagerTransactions =
        ExpenseTransactionMapper.mapExpenseReportsToList(testingExpenseReports);
    softly.assertThat(actualExpenseManagerTransactions).hasSize(3);
    //TODO: can be refactored in this way: https://github.com/joel-costigliola/assertj-examples/blob/master/assertions-examples/src/test/java/org/assertj/examples/MapAssertionsExamples.java
    softly.assertThat(actualExpenseManagerTransactions)
        .extracting("amount").contains(6.8, atIndex(0));
    softly.assertThat(actualExpenseManagerTransactions)
        .extracting("category").contains(ExpenseCategory.FOOD, atIndex(0));
    softly.assertThat(actualExpenseManagerTransactions)
        .extracting("subcategory").contains(ExpenseSubCategory.FOOD_COURT_AND_FAST_FOOD, atIndex(0));
    softly.assertThat(actualExpenseManagerTransactions)
        .extracting("paymentMethod").contains(PaymentMethod.CREDIT_CARD, atIndex(0));
    softly.assertThat(actualExpenseManagerTransactions)
        .extracting("description").contains("Dinner. Single bowl salad. Happy Tummy", atIndex(0));
    softly.assertThat(actualExpenseManagerTransactions)
        .extracting("expendedTime").contains(Instant.parse("2019-05-20T20:54:03.00Z"), atIndex(0));
    softly.assertThat(actualExpenseManagerTransactions)
        .extracting("referenceAmount").contains(0.0, atIndex(0));

    softly.assertThat(actualExpenseManagerTransactions)
        .extracting("amount").contains(944.0, atIndex(1));
    softly.assertThat(actualExpenseManagerTransactions)
        .extracting("category").contains(ExpenseCategory.PERSONAL, atIndex(1));
    softly.assertThat(actualExpenseManagerTransactions)
        .extracting("subcategory").contains(ExpenseSubCategory.KARAOKE_PARTY, atIndex(1));
    softly.assertThat(actualExpenseManagerTransactions)
        .extracting("paymentMethod").contains(PaymentMethod.ELECTRONIC_TRANSFER, atIndex(1));
    softly.assertThat(actualExpenseManagerTransactions)
        .extracting("description").contains("Monthly allowed expenditure", atIndex(1));
    softly.assertThat(actualExpenseManagerTransactions)
        .extracting("expendedTime").contains(Instant.parse("2018-01-31T16:00:00.00Z"), atIndex(1));
    softly.assertThat(actualExpenseManagerTransactions)
        .extracting("referenceAmount").contains(7.0, atIndex(1));

    softly.assertThat(actualExpenseManagerTransactions)
        .extracting("amount").contains(5.2, atIndex(2));
    softly.assertThat(actualExpenseManagerTransactions)
        .extracting("category").contains(ExpenseCategory.AESTHETIC, atIndex(2));
    softly.assertThat(actualExpenseManagerTransactions)
        .extracting("subcategory").contains(ExpenseSubCategory.CLOTHING, atIndex(2));
    softly.assertThat(actualExpenseManagerTransactions)
        .extracting("paymentMethod").contains(PaymentMethod.NETS, atIndex(2));
    softly.assertThat(actualExpenseManagerTransactions)
        .extracting("description").contains("Ice 2.5 kg", atIndex(2));
    softly.assertThat(actualExpenseManagerTransactions)
        .extracting("expendedTime").contains(Instant.parse("2018-01-31T16:00:00.00Z"), atIndex(2));
    softly.assertThat(actualExpenseManagerTransactions)
        .extracting("referenceAmount").contains(0.0, atIndex(2));

    softly.assertAll();
  }

  /*
   * Test Case: Take in one expense reports, and there is nothing declared inside the object
   * Expected Output: NullPointerException will occur
   */
  @Test
  void mapExpenseReportsToList_listWithoutAnyCsv() {
    // Create test data
    List<ExpenseReport> testingExpenseReports = new ArrayList<>();
    ExpenseReport emptyExpenseReport = new ExpenseReport();
    testingExpenseReports.add(emptyExpenseReport);

    Assertions.assertThatThrownBy(() -> {
      ExpenseTransactionMapper.mapExpenseReportsToList(null);
    }).isInstanceOf(NullPointerException.class);
  }

  /*
   * Test Case: Take in an expense reports
   * Expected Output: A empty map will be produced
   */
  @Test
  void mapExpenseReportsToMap_emptyExpenseReports() {
    // Create test data
    List<ExpenseReport> testingExpenseReports = new ArrayList<>();

    Assertions.assertThat(ExpenseTransactionMapper.mapExpenseReportsToMap(testingExpenseReports)).isEmpty();
  }

  /*
   * Test Case: Take in null expense reports
   * Expected Output: NullPointerException will occur
   */
  @Test
  void mapExpenseReportsToMap_nullExpenseReports() {
    Assertions.assertThatThrownBy(() -> {
      ExpenseTransactionMapper.mapExpenseReportsToMap(null);
    }).isInstanceOf(NullPointerException.class);
  }

  /*
   * Test Case: Take in one expense reports
   * Expected Output: A map of one item will appear
   */
  @Test
  void mapExpenseReportsToMap_oneExpenseReport(SoftAssertions softly) {
    // Create test data
    List<ExpenseReport> testingExpenseReports = new ArrayList<>();
    testingExpenseReports.add(constructExpenseReport(
        1,
        "Test Account",
        1.78,
        "Entertainment",
        "Breakfast",
        "Grab Pay",
        "Description",
        "2019-05-20T20:54:03.00Z",
        "2019-05-20T20:54:03.00Z",
        0,
        "Clear",
        "Property",
        "Property2",
        "Property3",
        "Property4",
        "Property5",
        "1.45",
        "ExpenseTag"
    ));

    // Expected data
    List<ExpenseManagerTransaction> expectedExpenseManagerTransactionList = new ArrayList<>();
    expectedExpenseManagerTransactionList.add(constructExpenseManagerTransaction(
        1.78,
        ExpenseCategory.ENTERTAINMENT,
        ExpenseSubCategory.BREAKFAST,
        PaymentMethod.GRAY_PAY,
        "Description",
        "2019-05-20T20:54:03.00Z",
        0
    ));
    Map<Double, Map<PaymentMethod, List<ExpenseManagerTransaction>>> actualExpenseManagerMap =
        ExpenseTransactionMapper.mapExpenseReportsToMap(testingExpenseReports);
    softly.assertThat(actualExpenseManagerMap).isNotEmpty();
    softly.assertThat(actualExpenseManagerMap).hasSize(1);
    softly.assertThat(actualExpenseManagerMap).containsOnlyKeys(1.78);
    softly.assertThat(actualExpenseManagerMap.get(1.78)).containsOnlyKeys(PaymentMethod.GRAY_PAY);

    Map<PaymentMethod, List<ExpenseManagerTransaction>> expectedExpensesPaymentMethod = new HashMap<>();
    expectedExpensesPaymentMethod.put(PaymentMethod.GRAY_PAY, expectedExpenseManagerTransactionList);

    softly.assertThat(actualExpenseManagerMap).containsExactly(entry(1.78, expectedExpensesPaymentMethod));
    softly.assertAll();
  }

  /*
   * Test Case: Take in multiple ExpenseReport
   * Expected Output: A map of one item will appear, because it is combined
   */
  @Test
  void mapExpenseReportsToMap_multipleExpenseReport(SoftAssertions softly) {
    // Create test data
    List<ExpenseReport> testingExpenseReports = new ArrayList<>();
    testingExpenseReports.add(constructExpenseReport(
        1,
        "Test Account",
        1.78,
        "Food",
        "Pay For Others",
        "Cash",
        "Description",
        "2019-05-20T20:54:03.00Z",
        "2019-05-20T20:54:03.00Z",
        0,
        "Clear",
        "Property",
        "Property2",
        "Property3",
        "Property4",
        "Property5",
        "1.45",
        "ExpenseTag"
    ));

    testingExpenseReports.add(constructExpenseReport(
        2,
        "Test Account",
        1.78,
        "Food",
        "Pay For Others",
        "Cash",
        "Description",
        "2019-05-21T20:54:03.00Z",
        "2019-05-20T20:54:03.00Z",
        0,
        "Clear",
        "Property",
        "Property2",
        "Property3",
        "Property4",
        "Property5",
        "1.45",
        "ExpenseTag"
    ));

    testingExpenseReports.add(constructExpenseReport(
        3,
        "Test Account",
        2,
        "Food",
        "Clothing",
        "Grab Pay",
        "Description",
        "2019-05-22T20:54:03.00Z",
        "2019-05-20T20:54:03.00Z",
        3.0,
        "Clear",
        "Property",
        "Property2",
        "Property3",
        "Property4",
        "Property5",
        "1.45",
        "ExpenseTag"
    ));

    testingExpenseReports.add(constructExpenseReport(
        4,
        "Test Account",
        200.0,
        "Food",
        "Airplane/ Train",
        "Grab Pay",
        "Description",
        "2019-05-23T20:54:03.00Z",
        "2019-05-20T20:54:03.00Z",
        3.0,
        "Clear",
        "Property",
        "Property2",
        "Property3",
        "Property4",
        "Property5",
        "1.45",
        "ExpenseTag"
    ));

    // Expected data
    List<ExpenseManagerTransaction> expectedCashExpenseManagerTransactionList = new ArrayList<>();
    expectedCashExpenseManagerTransactionList.add(constructExpenseManagerTransaction(
        1.78,
        ExpenseCategory.FOOD,
        ExpenseSubCategory.PAY_FOR_OTHERS,
        PaymentMethod.CASH,
        "Description",
        "2019-05-20T20:54:03.00Z",
        0
    ));
    expectedCashExpenseManagerTransactionList.add(constructExpenseManagerTransaction(
        1.78,
        ExpenseCategory.FOOD,
        ExpenseSubCategory.PAY_FOR_OTHERS,
        PaymentMethod.CASH,
        "Description",
        "2019-05-21T20:54:03.00Z",
        0
    ));

    List<ExpenseManagerTransaction> expectedGrabPayExpenseManagerTransactionList = new ArrayList<>();
    expectedGrabPayExpenseManagerTransactionList.add(constructExpenseManagerTransaction(
        2.0,
        ExpenseCategory.FOOD,
        ExpenseSubCategory.CLOTHING,
        PaymentMethod.GRAY_PAY,
        "Description",
        "2019-05-22T20:54:03.00Z",
        3.0
    ));
    expectedGrabPayExpenseManagerTransactionList.add(constructExpenseManagerTransaction(
        200.0,
        ExpenseCategory.FOOD,
        ExpenseSubCategory.AIRPLANE_TRAIN,
        PaymentMethod.GRAY_PAY,
        "Description",
        "2019-05-23T20:54:03.00Z",
        3.0
    ));

    Map<Double, Map<PaymentMethod, List<ExpenseManagerTransaction>>> actualExpenseManagerMap =
        ExpenseTransactionMapper.mapExpenseReportsToMap(testingExpenseReports);
    softly.assertThat(actualExpenseManagerMap).isNotEmpty();
    softly.assertThat(actualExpenseManagerMap).hasSize(2);

    Map<PaymentMethod, List<ExpenseManagerTransaction>> expectedExpensesMapByCash = new HashMap<>();
    expectedExpensesMapByCash.put(PaymentMethod.CASH, expectedCashExpenseManagerTransactionList);
    Map<PaymentMethod, List<ExpenseManagerTransaction>> expectedExpensesByGrabPay = new HashMap<>();
    expectedExpensesByGrabPay.put(PaymentMethod.GRAY_PAY, expectedGrabPayExpenseManagerTransactionList);
    softly.assertThat(actualExpenseManagerMap).containsOnly(
        entry(1.78, expectedExpensesMapByCash),
        entry(3.0, expectedExpensesByGrabPay));

    softly.assertAll();

  }
}