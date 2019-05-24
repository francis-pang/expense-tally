package expense_tally.reconciliation;

import expense_tally.csv_parser.model.CsvTransaction;
import expense_tally.csv_parser.model.TransactionType;
import expense_tally.expense_manager.model.ExpenseCategory;
import expense_tally.expense_manager.model.ExpenseManagerMapKey;
import expense_tally.expense_manager.model.ExpenseManagerTransaction;
import expense_tally.expense_manager.model.ExpenseSubCategory;
import expense_tally.expense_manager.model.PaymentMethod;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class ExpenseReconcilerTest {

    /**
     * Test Case:
     * 1. Take a
     */

    private CsvTransaction constructCsvTransaction(String transactionDate, String reference, double debitAmount,
                                                   double creditAmount, String transactionRef1,
                                                   String transactionRef2, String transactionRef3,
                                                   TransactionType transactionType) {
        CsvTransaction csvTransaction = new CsvTransaction();
        String[] transactionDataStringArray = transactionDate.split("-");
        csvTransaction.setTransactionDate(LocalDate.of(
            Integer.parseInt(transactionDataStringArray[2]),
            Integer.parseInt(transactionDataStringArray[1]),
            Integer.parseInt(transactionDataStringArray[0])));
        csvTransaction.setReference(reference);
        csvTransaction.setDebitAmount(debitAmount);
        csvTransaction.setCreditAmount(creditAmount);
        csvTransaction.setTransactionRef1(transactionRef1);
        csvTransaction.setTransactionRef2(transactionRef2);
        csvTransaction.setTransactionRef3(transactionRef3);
        csvTransaction.setType(transactionType);
        return csvTransaction;
    }

    private ExpenseManagerTransaction constructExpenseManagerTransaction(
        double amount, ExpenseCategory expenseCategory, ExpenseSubCategory expenseSubCategory,
        PaymentMethod paymentMethod, String description, String expensedTime, double referenceAmount) {
        ExpenseManagerTransaction expenseManagerTransaction = new ExpenseManagerTransaction();
        expenseManagerTransaction.setAmount(amount);
        expenseManagerTransaction.setCategory(expenseCategory);
        expenseManagerTransaction.setSubcategory(expenseSubCategory);
        expenseManagerTransaction.setPaymentMethod(paymentMethod);
        expenseManagerTransaction.setDescription(description);
        expenseManagerTransaction.setExpensedTime(Instant.parse(expensedTime));
        expenseManagerTransaction.setReferenceAmount(referenceAmount);
        return expenseManagerTransaction;
    }

    /*
     * Test Input:
     * - 1 Expense Manager transaction
     * - 1 CSV Transaction
     *
     * Scenario: That that they don't match due to different debit amount
     */
    @Test
    void reconcileBankData_singleNonMatchingCreditCsvTransaction() {
        List<CsvTransaction> csvTransactionList = new ArrayList<>();
        csvTransactionList.add(constructCsvTransaction(
            "24-4-2019",
             "MST",
            0.80,
            0.0,
            "KOUFU PTE LTD          SI NG 23APR,5548-2741-0014-1067",
            "",
            "",
            TransactionType.MASTERCARD));

        Map<ExpenseManagerMapKey, List<ExpenseManagerTransaction>> expenseManagerMap = new HashMap<>();
        ExpenseManagerMapKey expenseManagerMapKey = new ExpenseManagerMapKey(PaymentMethod.ELECTRONIC_TRANSFER);
        expenseManagerMapKey.setAmount(0.70);
        List<ExpenseManagerTransaction> expenseManagerTransactionList = new ArrayList<>();
        expenseManagerTransactionList.add(constructExpenseManagerTransaction(
            0.70,
            ExpenseCategory.FOOD,
            ExpenseSubCategory.FOOD_COURT_AND_FAST_FOOD,
            PaymentMethod.ELECTRONIC_TRANSFER,
            "test description",
"2009-04-24T10:15:30.00Z",
            0.00));
        expenseManagerMap.put(expenseManagerMapKey, expenseManagerTransactionList);

        assertThat(ExpenseReconciler.reconcileBankData(csvTransactionList, expenseManagerMap)).isEqualTo(1);
    }

    /*
     * Test Input:
     * - 1 Expense Manager transaction
     * - 1 CSV Transaction
     *
     * Scenario: That that they match
     */
    @Test
    void reconcileBankData_singleMastchingTransaction() {
        List<CsvTransaction> csvTransactionList = new ArrayList<>();
        csvTransactionList.add(constructCsvTransaction(
            "24-4-2019",
            "MST",
            0.80,
            0.0,
            "KOUFU PTE LTD          SI NG 23APR,5548-2741-0014-1067",
            "",
            "",
            TransactionType.MASTERCARD));

        Map<ExpenseManagerMapKey, List<ExpenseManagerTransaction>> expenseManagerMap = new HashMap<>();
        ExpenseManagerMapKey expenseManagerMapKey = new ExpenseManagerMapKey(PaymentMethod.DEBIT_CARD);
        expenseManagerMapKey.setAmount(0.80);
        List<ExpenseManagerTransaction> expenseManagerTransactionList = new ArrayList<>();
        expenseManagerTransactionList.add(constructExpenseManagerTransaction(
            0.80,
            ExpenseCategory.FOOD,
            ExpenseSubCategory.FOOD_COURT_AND_FAST_FOOD,
            PaymentMethod.DEBIT_CARD,
            "test description",
            "2019-04-24T10:15:30.00Z",
            0.00));
        expenseManagerMap.put(expenseManagerMapKey, expenseManagerTransactionList);
        assertThat(ExpenseReconciler.reconcileBankData(csvTransactionList, expenseManagerMap)).isEqualTo(0);
    }

    /*
     * Test Input:
     * - 1 Expense Manager transaction
     * - 1 CSV Transaction
     *
     * Scenario: That that they match
     */
    @Test
    void reconcileBankData_singleNonMatchingTransaction() {

    }

    /*
     * Test Input:
     * - 6 Expense Manager transaction
     * - 3 CSV Transaction
     *
     * Scenario: Mixture of matches and non-matches
     */
    @Test
    void reconcileBankData_multipleNonMatchingCsvTransaction() {

    }

    /*
     * Test Input:
     * - 3 Expense Manager transaction on the same day
     * - 1 CSV Transaction with matching date
     *
     * Scenario: That that they match
     */
    @Test
    void reconcileBankData_multipleMatchingTransactionSameDay() {

    }

    /*
     * Test Input:
     * - 1 Expense Manager transaction
     * - 1 CSV Transaction with matching transaction, but different reference date from MasterCard
     *
     * Scenario: The transaction does not match
     */
    @Test
    void reconcileBankData_singleMatchingTransactionDifferentDay() {

    }
}