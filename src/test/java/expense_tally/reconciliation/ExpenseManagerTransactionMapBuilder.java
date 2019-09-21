package expense_tally.reconciliation;

import expense_tally.expense_manager.model.*;

import java.time.Instant;
import java.util.*;

public class ExpenseManagerTransactionMapBuilder {
    private SortedMap<ExpenseManagerMapKey, List<ExpenseManagerTransaction>> expenseTransactionMap;

    public ExpenseManagerTransactionMapBuilder() {
        this(1.0, PaymentMethod.DEBIT_CARD, 0.0);
    }

    /**
     * This is the most common changing parameters for a {@code ExpenseManagerTransaction}.
     *
     * @param amount
     * @param paymentMethod
     * @param referenceAmount
     */
    public ExpenseManagerTransactionMapBuilder(Double amount, PaymentMethod paymentMethod, Double referenceAmount) {
        expenseTransactionMap = new TreeMap<>();

        ExpenseManagerTransaction expenseManagerTransaction = new ExpenseManagerTransaction();
        expenseManagerTransaction.setAmount(amount);
        expenseManagerTransaction.setCategory(ExpenseCategory.ENTERTAINMENT);
        expenseManagerTransaction.setSubcategory(ExpenseSubCategory.ALCOHOL_AND_RESTAURANT);
        expenseManagerTransaction.setPaymentMethod(paymentMethod);
        expenseManagerTransaction.setDescription("");
        expenseManagerTransaction.setExpensedTime(Instant.parse("2009-04-24T10:15:30.00Z"));
        expenseManagerTransaction.setReferenceAmount(referenceAmount);

        List<ExpenseManagerTransaction> expenseManagerTransactions = new ArrayList<>();
        expenseManagerTransactions.add(expenseManagerTransaction);

        ExpenseManagerMapKey expenseManagerMapKey = new ExpenseManagerMapKey(
            expenseManagerTransaction.getPaymentMethod(), expenseManagerTransaction.getAmount());

        expenseTransactionMap.put(expenseManagerMapKey, expenseManagerTransactions);
    }

    public Map<ExpenseManagerMapKey, List<ExpenseManagerTransaction>> build() {
        return expenseTransactionMap;
    }

    public void setAmount(Double amount) {
        ExpenseManagerTransaction expenseManagerTransaction = expenseTransactionMap.get(expenseTransactionMap.firstKey()).get(0);
        expenseManagerTransaction.setAmount(amount);
    }

    public void setCategory(ExpenseCategory expenseCategory,
                            ExpenseSubCategory expenseSubCategory) {
        ExpenseManagerTransaction expenseManagerTransaction = expenseTransactionMap.get(expenseTransactionMap.firstKey()).get(0);
        expenseManagerTransaction.setCategory(expenseCategory);
        expenseManagerTransaction.setSubcategory(expenseSubCategory);
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        ExpenseManagerTransaction expenseManagerTransaction = expenseTransactionMap.get(expenseTransactionMap.firstKey()).get(0);
        expenseManagerTransaction.setPaymentMethod(paymentMethod);
    }

    public void setDescription(String description) {
        ExpenseManagerTransaction expenseManagerTransaction = expenseTransactionMap.get(expenseTransactionMap.firstKey()).get(0);
        expenseManagerTransaction.setDescription(description);
    }

    public void setExpensedTime(String expensedTime) {
        ExpenseManagerTransaction expenseManagerTransaction = expenseTransactionMap.get(expenseTransactionMap.firstKey()).get(0);
        expenseManagerTransaction.setExpensedTime(Instant.parse(expensedTime));
    }

    public void setReferenceAmount(Double referenceAmount) {
        ExpenseManagerTransaction expenseManagerTransaction = expenseTransactionMap.get(expenseTransactionMap.firstKey()).get(0);
        expenseManagerTransaction.setReferenceAmount(referenceAmount);
    }
}
