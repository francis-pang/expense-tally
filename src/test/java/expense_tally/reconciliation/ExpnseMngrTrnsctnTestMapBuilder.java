package expense_tally.reconciliation;

import expense_tally.expense_manager.model.*;

import java.time.Instant;
import java.util.*;

public class ExpnseMngrTrnsctnTestMapBuilder {
    private Double amount = 0.8;
    private PaymentMethod paymentMethod = PaymentMethod.DEBIT_CARD;
    private Double referenceAmount = 0.0;
    private ExpenseCategory category = ExpenseCategory.ENTERTAINMENT;
    private ExpenseSubCategory subcategory = ExpenseSubCategory.ALCOHOL_AND_RESTAURANT;
    private String description = "";
    private Instant expensedTime = Instant.parse("2009-04-24T10:15:30.00Z");
    private int numberOfTransaction;

    public ExpnseMngrTrnsctnTestMapBuilder(int numberOfTransaction) {
        this.numberOfTransaction = numberOfTransaction;
    }

    public Map<ExpenseManagerMapKey, List<ExpenseManagerTransaction>> build() {
        Map<ExpenseManagerMapKey, List<ExpenseManagerTransaction>> expenseManagerMapKeyListMap = new HashMap<>();
        ExpenseManagerMapKey expenseManagerMapKey = new ExpenseManagerMapKey(paymentMethod, amount);
        List<ExpenseManagerTransaction> expenseManagerTransactionList = new ArrayList<>();
        for (int index = 0; index < numberOfTransaction; index++) {
            ExpenseManagerTransaction expenseManagerTransaction = new ExpenseManagerTransaction();
            expenseManagerTransaction.setAmount(amount);
            expenseManagerTransaction.setCategory(category);
            expenseManagerTransaction.setSubcategory(subcategory);
            expenseManagerTransaction.setPaymentMethod(paymentMethod);
            expenseManagerTransaction.setDescription(description);
            expenseManagerTransaction.setExpensedTime(expensedTime);
            expenseManagerTransaction.setReferenceAmount(referenceAmount);
            expenseManagerTransactionList.add(expenseManagerTransaction);
        }
        expenseManagerMapKeyListMap.put(expenseManagerMapKey, expenseManagerTransactionList);
        return expenseManagerMapKeyListMap;
    }

    public ExpnseMngrTrnsctnTestMapBuilder amount(double amount) {
        this.amount = amount;
        return this;
    }

    public ExpnseMngrTrnsctnTestMapBuilder paymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
        return this;
    }

    public ExpnseMngrTrnsctnTestMapBuilder referenceAmount(Double referenceAmount) {
        this.referenceAmount = referenceAmount;
        return this;
    }

    public ExpnseMngrTrnsctnTestMapBuilder category(ExpenseCategory category) {
        this.category = category;
        return this;
    }

    public ExpnseMngrTrnsctnTestMapBuilder category(ExpenseSubCategory subcategory) {
        this.subcategory = subcategory;
        return this;
    }

    public ExpnseMngrTrnsctnTestMapBuilder description(String description) {
        this.description = description;
        return this;
    }

    public ExpnseMngrTrnsctnTestMapBuilder expensedTime(String expensedTime) {
        this.expensedTime = Instant.parse(expensedTime);
        return this;
    }
}
