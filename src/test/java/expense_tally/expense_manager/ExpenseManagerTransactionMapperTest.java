package expense_tally.expense_manager;

import expense_tally.expense_manager.model.ExpenseManagerTransaction;
import expense_tally.expense_manager.model.ExpenseReport;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ExpenseManagerTransactionMapperTest {

    private ExpenseManagerTransaction constuctExpectedExpenseManagerTransaction (
        double amount,
        String catagory,
        String subcategory,
        String paymethMethod,
        String description,
        Instant expensedTime,
        double referenceAmount
    ) {
        ExpenseManagerTransaction expectedExpenseManagerTransaction = new ExpenseManagerTransaction();
        expectedExpenseManagerTransaction.setAmount(amount);
        expectedExpenseManagerTransaction.setCategory(catagory);
        expectedExpenseManagerTransaction.setSubcategory(subcategory);
        expectedExpenseManagerTransaction.setPaymentMethod(paymethMethod);
        expectedExpenseManagerTransaction.setDescription(description);
        expectedExpenseManagerTransaction.setExpensedTime(expensedTime);
        expectedExpenseManagerTransaction.setReferenceAmount(referenceAmount);
        return expectedExpenseManagerTransaction;
    }

    /**
     * 1. Empty list
     * 2. Null list
     * 3. List with 1 elements
     * 4. List with more than 1 elements, maybe 3. Among the elements, we will have varying test cases
     * 5. List with 1 element, blank reference
     */
    @Test
    void mapExpenseReportsToMap() {
    }

    @Test
    void mapExpenseReportsToList_emptyExpenseReports() {
        List<ExpenseReport> testingExpenseReports = new ArrayList<>();
        assertEquals(0, ExpenseTransactionMapper.mapExpenseReportsToList(testingExpenseReports).size());
    }

    @Test
    void mapExpenseReportsToList_nullExpenseReports() {
        assertThrows(NullPointerException.class, () -> ExpenseTransactionMapper.mapExpenseReportsToList(null));
    }

    @Test
    void mapExpenseReportsToList_oneExpenseReport() {
        // Create test data
        ExpenseReport expenseReport = new ExpenseReport();
        expenseReport.setId(1);
        expenseReport.setAccount("Test Account");
        expenseReport.setAmount("1.78");
        expenseReport.setCategory("Category");
        expenseReport.setSubcategory("Subcategory");
        expenseReport.setPaymentMethod("PaymentMethod");
        expenseReport.setDescription("Description");
        expenseReport.setExpensed(1543509392);
        expenseReport.setModified(6);
        expenseReport.setReferenceNumber("7");
        expenseReport.setStatus("Clear");
        expenseReport.setProperty("Property");
        expenseReport.setProperty2("Property2");
        expenseReport.setProperty3("Property3");
        expenseReport.setProperty4("Property4");
        expenseReport.setProperty5("Property5");
        expenseReport.setTax("1.45");
        expenseReport.setExpenseTag("ExpenseTag");
        List<ExpenseReport> testingExpenseReports = new ArrayList<>();
        testingExpenseReports.add(expenseReport);

        // Expected result
        ExpenseManagerTransaction expectedExpenseManagerTransaction = constuctExpectedExpenseManagerTransaction(
            1.78,
            "Category",
            "Subcategory",
            "PaymentMethod",
            "Description",
            Instant.ofEpochMilli(1543509392),
            7.00);

        assertEquals(expectedExpenseManagerTransaction, ExpenseTransactionMapper.mapExpenseReportsToList(testingExpenseReports).get(0));
    }

    @Test
    void mapExpenseReportsToList_oneExpenseWithNullReference() {
        //TODO: Fill this up
    }
}