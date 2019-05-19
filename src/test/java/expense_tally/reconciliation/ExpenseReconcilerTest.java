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

    /**
     *
     */
    @Test
    void reconcileBankData_singleNonMatchingCreditCsvTransaction() {
        List<CsvTransaction> csvTransactionList = new ArrayList<>();
        //24 Apr 2019,MST, 0.80, ,KOUFU PTE LTD          SI NG 23APR,5548-2741-0014-1067,,
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
        expenseManagerMapKey.setAmount(0.80);
        List<ExpenseManagerTransaction> expenseManagerTransactionList = new ArrayList<>();
        expenseManagerTransactionList.add(constructExpenseManagerTransaction(
            0.80,
            ExpenseCategory.FOOD,
            ExpenseSubCategory.FOOD_COURT_AND_FAST_FOOD,
            PaymentMethod.ELECTRONIC_TRANSFER,
            "test description",
"2009-04-24T10:15:30.00Z",
            0.00));
        expenseManagerMap.put(expenseManagerMapKey, expenseManagerTransactionList);

        ExpenseReconciler.reconcileBankData(csvTransactionList, expenseManagerMap);
    }


    @Test
    void reconcileBankData_singleMastchingTransaction() {
        List<CsvTransaction> csvTransactionList = new ArrayList<>();
        CsvTransaction csvTransaction = new CsvTransaction();

    }

    @Test
    void reconcileBankData_singleNonMatchingTransaction() {

    }

    @Test
    void reconcileBankData_multipleNonMatchingCsvTransaction() {

    }

    @Test
    void reconcileBankData_multipleMatchingTransactionSameDay() {

    }

    @Test
    void reconcileBankData_multipleMatchingTransactionDifferentDay() {

    }
}