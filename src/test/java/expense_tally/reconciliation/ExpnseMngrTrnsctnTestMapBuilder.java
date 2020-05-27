package expense_tally.reconciliation;

import expense_tally.expense_manager.transformation.ExpenseCategory;
import expense_tally.expense_manager.transformation.ExpenseManagerMapKey;
import expense_tally.expense_manager.transformation.ExpenseManagerTransaction;
import expense_tally.expense_manager.transformation.ExpenseSubCategory;
import expense_tally.expense_manager.transformation.PaymentMethod;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExpnseMngrTrnsctnTestMapBuilder {
  private Double amount = 0.8;
  private PaymentMethod paymentMethod = PaymentMethod.DEBIT_CARD;
  private Double referenceAmount = 0.0;
  private ExpenseCategory category = ExpenseCategory.ENTERTAINMENT;
  private ExpenseSubCategory subcategory = ExpenseSubCategory.ALCOHOL_AND_RESTAURANT;
  private String description = "test description";
  private Instant expensedTime = Instant.parse("2009-04-24T10:15:30.00Z");
  private int numberOfTransaction;
  private List<ExpenseManagerTransaction> expenseManagerTransactionList = new ArrayList<>();

  public ExpnseMngrTrnsctnTestMapBuilder(int numberOfTransaction) {
    this.numberOfTransaction = numberOfTransaction;
  }

  public Map<ExpenseManagerMapKey, List<ExpenseManagerTransaction>> build() {
    Map<ExpenseManagerMapKey, List<ExpenseManagerTransaction>> expenseManagerMapKeyListMap = new HashMap<>();
    ExpenseManagerMapKey expenseManagerMapKey = new ExpenseManagerMapKey(paymentMethod, amount);
    addCustomisedTransations(expenseManagerMapKeyListMap, expenseManagerTransactionList);
    for (int index = 0; index < numberOfTransaction; index++) {
      ExpenseManagerTransaction expenseManagerTransaction = ExpenseManagerTransaction.createInstanceOf(amount,
          category, subcategory, paymentMethod, description, expensedTime);
      expenseManagerTransaction.setReferenceAmount(referenceAmount);
      expenseManagerTransactionList.add(expenseManagerTransaction);
    }
    expenseManagerMapKeyListMap.put(expenseManagerMapKey, expenseManagerTransactionList);
    return expenseManagerMapKeyListMap;
  }

  private void addCustomisedTransations(Map<ExpenseManagerMapKey, List<ExpenseManagerTransaction>> expenseManagerMapKeyListMap,
                                        List<ExpenseManagerTransaction> expenseManagerTransactionList) {
    for (ExpenseManagerTransaction expenseManagerTransaction : expenseManagerTransactionList) {
      ExpenseManagerMapKey expenseManagerMapKey = new ExpenseManagerMapKey(expenseManagerTransaction.getPaymentMethod(), expenseManagerTransaction.getAmount());

      List<ExpenseManagerTransaction> containingExpenseManagerTransactionList = expenseManagerMapKeyListMap.getOrDefault(expenseManagerMapKey, new ArrayList<>());
      containingExpenseManagerTransactionList.add(expenseManagerTransaction);
      expenseManagerMapKeyListMap.put(expenseManagerMapKey, containingExpenseManagerTransactionList);
    }
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

  public ExpnseMngrTrnsctnTestMapBuilder addCustomisedTransaction(Double amount,
                                                                  PaymentMethod paymentMethod,
                                                                  int expensedYear,
                                                                  int expensedMonth,
                                                                  int expensedDay) {
    final int HOURS = 10;
    final int MINUTES = 15;
    final int SECONDS = 30;
    Instant expensedTime = LocalDateTime.of(expensedYear, expensedMonth, expensedDay, HOURS, MINUTES, SECONDS).toInstant(ZoneOffset.UTC);
    ExpenseManagerTransaction expenseManagerTransaction = ExpenseManagerTransaction.createInstanceOf(amount,
        category, subcategory, paymentMethod, description, expensedTime);
    expenseManagerTransactionList.add(expenseManagerTransaction);
    return this;
  }
}
