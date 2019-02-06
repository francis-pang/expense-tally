package expense_tally.persistence;

import expense_tally.model.ExpenseManager.ExpenseManagerTransaction;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ExpenseManagerTransactionMapperTest {

    /**
     * 1. Empty list
     * 2. Null list
     * 3. List with 1 elements
     * 4. List with more than 1 elements, maybe 3. Among the elements, we will have varying test cases
     */
    @Test
    void mapExpenseReportsToMap() {
    }

    @Test
    void mapEmptyExpenseReportsToList() {
        List<ExpenseReport> testingExpenseReports = new ArrayList<>();
        assertEquals(0, ExpenseTransactionMapper.mapExpenseReportsToList(testingExpenseReports).size());
    }

    @Test
    void mapNullExpenseReportsToList() {
        assertThrows(NullPointerException.class, () -> ExpenseTransactionMapper.mapExpenseReportsToList(null));
    }

    @Test
    void mapOneExpenseReportToList() {
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
        ExpenseManagerTransaction expectedExpenseManagerTransaction = new ExpenseManagerTransaction();
        expectedExpenseManagerTransaction.setAmount(1.78);
        expectedExpenseManagerTransaction.setCategory("Category");
        expectedExpenseManagerTransaction.setSubcategory("Subcategory");
        expectedExpenseManagerTransaction.setPaymentMethod("PaymentMethod");
        expectedExpenseManagerTransaction.setDescription("Description");
        expectedExpenseManagerTransaction.setExpensedTime(Instant.ofEpochMilli(1543509392));
        expectedExpenseManagerTransaction.setReferenceAmount(7.00);

        assertEquals(expectedExpenseManagerTransaction, ExpenseTransactionMapper.mapExpenseReportsToList(testingExpenseReports).get(0));
    }
}