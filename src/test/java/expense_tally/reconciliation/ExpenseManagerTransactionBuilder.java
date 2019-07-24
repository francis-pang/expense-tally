package expense_tally.reconciliation;

import expense_tally.expense_manager.model.ExpenseCategory;
import expense_tally.expense_manager.model.ExpenseManagerTransaction;
import expense_tally.expense_manager.model.ExpenseSubCategory;
import expense_tally.expense_manager.model.PaymentMethod;

import java.time.Instant;

public class ExpenseManagerTransactionBuilder {
    private ExpenseManagerTransaction expenseManagerTransaction;

    public ExpenseManagerTransactionBuilder() {
        expenseManagerTransaction = new ExpenseManagerTransaction();
        expenseManagerTransaction.setAmount(Double.valueOf(0));
        expenseManagerTransaction.setCategory(ExpenseCategory.ENTERTAINMENT);
        expenseManagerTransaction.setSubcategory(ExpenseSubCategory.ALCOHOL_AND_RESTAURANT);
        expenseManagerTransaction.setPaymentMethod(PaymentMethod.DEBIT_CARD);
        expenseManagerTransaction.setDescription("");
        expenseManagerTransaction.setExpensedTime(Instant.parse("2009-04-24T10:15:30.00Z"));
        expenseManagerTransaction.setReferenceAmount(Double.valueOf(0));
    }

    /**
     * This is the most common changing parameters for a {@code ExpenseManagerTransaction}.
     *
     * @param amount
     * @param paymentMethod
     * @param referenceAmount
     */
    public ExpenseManagerTransactionBuilder(Double amount, PaymentMethod paymentMethod, Double referenceAmount) {
        expenseManagerTransaction = new ExpenseManagerTransaction();
        expenseManagerTransaction.setAmount(amount);
        expenseManagerTransaction.setCategory(ExpenseCategory.ENTERTAINMENT);
        expenseManagerTransaction.setSubcategory(ExpenseSubCategory.ALCOHOL_AND_RESTAURANT);
        expenseManagerTransaction.setPaymentMethod(paymentMethod);
        expenseManagerTransaction.setDescription("");
        expenseManagerTransaction.setExpensedTime(Instant.parse("2009-04-24T10:15:30.00Z"));
        expenseManagerTransaction.setReferenceAmount(referenceAmount);
    }

    public ExpenseManagerTransaction build() {
        return expenseManagerTransaction;
    }

    public void setAmount(Double amount) {
        expenseManagerTransaction.setAmount(amount);
    }

    public void setCategory(ExpenseCategory expenseCategory,
                            ExpenseSubCategory expenseSubCategory) {
        expenseManagerTransaction.setCategory(expenseCategory);
        expenseManagerTransaction.setSubcategory(expenseSubCategory);
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        expenseManagerTransaction.setPaymentMethod(paymentMethod);
    }

    public void setDescription(String description) {
        expenseManagerTransaction.setDescription(description);
    }

    public void setExpensedTime(String expensedTime) {
        expenseManagerTransaction.setExpensedTime(Instant.parse(expensedTime));
    }

    public void setReferenceAmount(Double referenceAmount) {
        expenseManagerTransaction.setReferenceAmount(referenceAmount);
    }
}
