package expense_tally.expense_manager;

import expense_tally.expense_manager.model.*;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.JUnitJupiterSoftAssertions;
import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;

@ExtendWith(SoftAssertionsExtension.class)
class ExpenseManagerTransactionMapperTest {
    private ExpenseReport constructExpenseReport(
            int id, String account, double amount, String category, String subcategory, String paymentMethod,
            String description, String expensedTime, String modificationTime, double referenceAmount, String status,
            String property1, String property2, String property3, String property4, String property5, String tax,
            String expenseTag) {
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
        ExpenseManagerTransaction expenseManagerTransaction = new ExpenseManagerTransaction();
        expenseManagerTransaction.setAmount(amount);
        expenseManagerTransaction.setCategory(category);
        expenseManagerTransaction.setSubcategory(subcategory);
        expenseManagerTransaction.setPaymentMethod(paymentMethod);
        expenseManagerTransaction.setDescription(description);
        expenseManagerTransaction.setExpensedTime(Instant.parse(expensedTime));
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
                "Category",
                "Subcategory",
                "PaymentMethod",
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
                .extracting("category").contains(null, atIndex(0));
        softly.assertThat(actualExpenseManagerTransactions)
                .extracting("subcategory").contains(null, atIndex(0));
        softly.assertThat(actualExpenseManagerTransactions)
                .extracting("paymentMethod").contains(null, atIndex(0));
        softly.assertThat(actualExpenseManagerTransactions)
                .extracting("description").contains("Description", atIndex(0));
        softly.assertThat(actualExpenseManagerTransactions)
                .extracting("expensedTime").contains(Instant.parse("2019-05-20T20:54:03.00Z"), atIndex(0));
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
                "Income",
                "",
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
                .extracting("expensedTime").contains(Instant.parse("2019-05-20T20:54:03.00Z"), atIndex(0));
        softly.assertThat(actualExpenseManagerTransactions)
                .extracting("referenceAmount").contains(0.0, atIndex(0));

        softly.assertThat(actualExpenseManagerTransactions)
                .extracting("amount").contains(944.0, atIndex(1));
        softly.assertThat(actualExpenseManagerTransactions)
                .extracting("category").contains(null, atIndex(1));
        softly.assertThat(actualExpenseManagerTransactions)
                .extracting("subcategory").contains(null, atIndex(1));
        softly.assertThat(actualExpenseManagerTransactions)
                .extracting("paymentMethod").contains(PaymentMethod.ELECTRONIC_TRANSFER, atIndex(1));
        softly.assertThat(actualExpenseManagerTransactions)
                .extracting("description").contains("Monthly allowed expenditure", atIndex(1));
        softly.assertThat(actualExpenseManagerTransactions)
                .extracting("expensedTime").contains(Instant.parse("2018-01-31T16:00:00.00Z"), atIndex(1));
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
                .extracting("expensedTime").contains(Instant.parse("2018-01-31T16:00:00.00Z"), atIndex(2));
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
                "Category",
                "Subcategory",
                "PaymentMethod",
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
                null,
                null,
                null,
                "Description",
                "2019-05-20T20:54:03.00Z",
                0
        ));

        ExpenseManagerMapKey expectedExpenseManagerMapKey = new ExpenseManagerMapKey(null);
        expectedExpenseManagerMapKey.setAmount(1.78);

        Map<ExpenseManagerMapKey, List<ExpenseManagerTransaction>> actualExpenseManagerMapKeyListMap =
                ExpenseTransactionMapper.mapExpenseReportsToMap(testingExpenseReports);
        softly.assertThat(actualExpenseManagerMapKeyListMap).isNotEmpty();
        softly.assertThat(actualExpenseManagerMapKeyListMap).hasSize(1);
        softly.assertThat(actualExpenseManagerMapKeyListMap).containsExactly(
                entry(expectedExpenseManagerMapKey, expectedExpenseManagerTransactionList));

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
                "Subcategory",
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
                "Subcategory",
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
                "Subcategory",
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
                "Subcategory",
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
                null,
                PaymentMethod.CASH,
                "Description",
                "2019-05-20T20:54:03.00Z",
                0
        ));
        expectedCashExpenseManagerTransactionList.add(constructExpenseManagerTransaction(
                1.78,
                ExpenseCategory.FOOD,
                null,
                PaymentMethod.CASH,
                "Description",
                "2019-05-21T20:54:03.00Z",
                0
        ));

        ExpenseManagerMapKey expectedcashExpenseManagerMapKey = new ExpenseManagerMapKey(PaymentMethod.CASH);
        expectedcashExpenseManagerMapKey.setAmount(1.78);

        List<ExpenseManagerTransaction> expectedGrabPayExpenseManagerTransactionList = new ArrayList<>();
        expectedGrabPayExpenseManagerTransactionList.add(constructExpenseManagerTransaction(
                2.0,
                ExpenseCategory.FOOD,
                null,
                PaymentMethod.GRAY_PAY,
                "Description",
                "2019-05-22T20:54:03.00Z",
                3.0
        ));
        expectedGrabPayExpenseManagerTransactionList.add(constructExpenseManagerTransaction(
                200.0,
                ExpenseCategory.FOOD,
                null,
                PaymentMethod.GRAY_PAY,
                "Description",
                "2019-05-23T20:54:03.00Z",
                3.0
        ));

        ExpenseManagerMapKey expectedGrabPayExpenseManagerMapKey = new ExpenseManagerMapKey(PaymentMethod.GRAY_PAY);
        expectedGrabPayExpenseManagerMapKey.setAmount(3.0);


        Map<ExpenseManagerMapKey, List<ExpenseManagerTransaction>> actualExpenseManagerMapKeyListMap =
                ExpenseTransactionMapper.mapExpenseReportsToMap(testingExpenseReports);
        softly.assertThat(actualExpenseManagerMapKeyListMap).isNotEmpty();
        softly.assertThat(actualExpenseManagerMapKeyListMap).hasSize(2);
        softly.assertThat(actualExpenseManagerMapKeyListMap).containsOnly(
                entry(expectedcashExpenseManagerMapKey, expectedCashExpenseManagerTransactionList),
                entry(expectedGrabPayExpenseManagerMapKey, expectedGrabPayExpenseManagerTransactionList));

        softly.assertAll();

    }
}