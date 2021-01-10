package expense_tally.expense_manager.transformation;

import expense_tally.expense_manager.mapper.ExpenseManagerTransactionMapper;
import expense_tally.model.persistence.database.ExpenseReport;
import expense_tally.model.persistence.transformation.ExpenseCategory;
import expense_tally.model.persistence.transformation.ExpenseManagerTransaction;
import expense_tally.model.persistence.transformation.ExpenseSubCategory;
import expense_tally.model.persistence.transformation.PaymentMethod;
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
import static org.assertj.core.api.Assertions.assertThatThrownBy;

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
      int id,
      double amount,
      ExpenseCategory category,
      ExpenseSubCategory subcategory,
      PaymentMethod paymentMethod,
      String description,
      String expensedTime,
      double referenceAmount) {
    ExpenseManagerTransaction expenseManagerTransaction = ExpenseManagerTransaction.create(id, amount,
        category, subcategory, paymentMethod, description, Instant.parse(expensedTime));
    expenseManagerTransaction.setReferenceAmount(referenceAmount);
    return expenseManagerTransaction;
  }

  /*
   * Test Case: Take in an expense reports
   * Expected Output: A empty map will be produced
   */
  @Test
  void mapExpenseReportsToMap_emptyExpenseReports() {
    // Create test data
    List<ExpenseReport> testingExpenseReports = new ArrayList<>();

    assertThat(ExpenseTransactionMapper.mapExpenseReports(testingExpenseReports)).isEmpty();
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
        77,
        1.78,
        ExpenseCategory.ENTERTAINMENT,
        ExpenseSubCategory.BREAKFAST,
        PaymentMethod.GRAY_PAY,
        "Description",
        "2019-05-20T20:54:03.00Z",
        0
    ));
    List<ExpenseManagerTransaction> expenseManagerTransactions =
        ExpenseTransactionMapper.mapExpenseReports(testingExpenseReports);
    Map<Double, Map<PaymentMethod, List<ExpenseManagerTransaction>>> actualExpenseManagerMap =
        ExpenseTransactionMapper.convertToTableOfAmountAndPaymentMethod(expenseManagerTransactions);

    softly.assertThat(actualExpenseManagerMap).isNotEmpty();
    softly.assertThat(actualExpenseManagerMap).hasSize(1);
    softly.assertThat(actualExpenseManagerMap).containsOnlyKeys(1.78);
    softly.assertThat(actualExpenseManagerMap.get(1.78)).containsOnlyKeys(PaymentMethod.GRAY_PAY);

    Map<PaymentMethod, List<ExpenseManagerTransaction>> expectedExpensesPaymentMethod = new HashMap<>();
    expectedExpensesPaymentMethod.put(PaymentMethod.GRAY_PAY, expectedExpenseManagerTransactionList);

    softly.assertThat(actualExpenseManagerMap).containsExactly(Assertions.entry(1.78, expectedExpensesPaymentMethod));
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
        77,
        1.78,
        ExpenseCategory.FOOD,
        ExpenseSubCategory.PAY_FOR_OTHERS,
        PaymentMethod.CASH,
        "Description",
        "2019-05-20T20:54:03.00Z",
        0
    ));
    expectedCashExpenseManagerTransactionList.add(constructExpenseManagerTransaction(
        77,
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
        77,
        2.0,
        ExpenseCategory.FOOD,
        ExpenseSubCategory.CLOTHING,
        PaymentMethod.GRAY_PAY,
        "Description",
        "2019-05-22T20:54:03.00Z",
        3.0
    ));
    expectedGrabPayExpenseManagerTransactionList.add(constructExpenseManagerTransaction(
        77,
        200.0,
        ExpenseCategory.FOOD,
        ExpenseSubCategory.AIRPLANE_TRAIN,
        PaymentMethod.GRAY_PAY,
        "Description",
        "2019-05-23T20:54:03.00Z",
        3.0
    ));

    List<ExpenseManagerTransaction> expenseManagerTransactions =
        ExpenseTransactionMapper.mapExpenseReports(testingExpenseReports);
    Map<Double, Map<PaymentMethod, List<ExpenseManagerTransaction>>> actualExpenseManagerMap =
        ExpenseTransactionMapper.convertToTableOfAmountAndPaymentMethod(expenseManagerTransactions);
    softly.assertThat(actualExpenseManagerMap).isNotEmpty();
    softly.assertThat(actualExpenseManagerMap).hasSize(2);

    Map<PaymentMethod, List<ExpenseManagerTransaction>> expectedExpensesMapByCash = new HashMap<>();
    expectedExpensesMapByCash.put(PaymentMethod.CASH, expectedCashExpenseManagerTransactionList);
    Map<PaymentMethod, List<ExpenseManagerTransaction>> expectedExpensesByGrabPay = new HashMap<>();
    expectedExpensesByGrabPay.put(PaymentMethod.GRAY_PAY, expectedGrabPayExpenseManagerTransactionList);
    softly.assertThat(actualExpenseManagerMap).containsOnly(
        Assertions.entry(1.78, expectedExpensesMapByCash),
        Assertions.entry(3.0, expectedExpensesByGrabPay));
    softly.assertAll();
  }

  @Test
  void mapExpenseReportsToMap_oneExpenseReportHasError(SoftAssertions softly) {
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

    testingExpenseReports.add(constructExpenseReport(
        1,
        "Test Account",
        1.78,
        "",
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
        77,
        1.78,
        ExpenseCategory.ENTERTAINMENT,
        ExpenseSubCategory.BREAKFAST,
        PaymentMethod.GRAY_PAY,
        "Description",
        "2019-05-20T20:54:03.00Z",
        0
    ));
    List<ExpenseManagerTransaction> expenseManagerTransactions =
        ExpenseTransactionMapper.mapExpenseReports(testingExpenseReports);
    Map<Double, Map<PaymentMethod, List<ExpenseManagerTransaction>>> actualExpenseManagerMap =
        ExpenseTransactionMapper.convertToTableOfAmountAndPaymentMethod(expenseManagerTransactions);
    softly.assertThat(actualExpenseManagerMap).isNotEmpty();
    softly.assertThat(actualExpenseManagerMap).hasSize(1);
    softly.assertThat(actualExpenseManagerMap).containsOnlyKeys(1.78);
    softly.assertThat(actualExpenseManagerMap.get(1.78)).containsOnlyKeys(PaymentMethod.GRAY_PAY);

    Map<PaymentMethod, List<ExpenseManagerTransaction>> expectedExpensesPaymentMethod = new HashMap<>();
    expectedExpensesPaymentMethod.put(PaymentMethod.GRAY_PAY, expectedExpenseManagerTransactionList);

    softly.assertThat(actualExpenseManagerMap).containsExactly(Assertions.entry(1.78, expectedExpensesPaymentMethod));
    softly.assertAll();
  }

}