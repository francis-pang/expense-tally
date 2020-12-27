package expense_tally.expense_manager.transformation;

import expense_tally.model.persistence.database.ExpenseReport;
import expense_tally.model.persistence.transformation.ExpenseCategory;
import expense_tally.model.persistence.transformation.ExpenseManagerTransaction;
import expense_tally.model.persistence.transformation.ExpenseSubCategory;
import expense_tally.model.persistence.transformation.PaymentMethod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A static class to provide methods for mapping {@link ExpenseReport} to {@link ExpenseManagerTransaction}.
 *
 * <p>This class is an use case of the Mapper design pattern, in which the class's main purpose is to provide a set of
 * helpful method to map {@link ExpenseReport} into
 * {@link ExpenseManagerTransaction} of various forms. This implementation is inspired by
 * <a href="https://stackoverflow.com/a/11832149/1522867">a stackoverflow answer</a>. The deciding factor between
 * the design patterns of Mapper, Builder and Factory pattern is essential <q>as things evolve, some applications are
 * not aware of additional attributes that needs to go under constructor where one either computes it or use default.
 * The critical thing is that mapper can do this for you</q>, taken from
 * <a href="https://softwareengineering.stackexchange.com/a/117527/88556">here</a>.</p>
 *
 * @see ExpenseManagerTransaction
 * @see ExpenseReport
 */
public final class ExpenseTransactionMapper {
  private static final Logger LOGGER = LogManager.getLogger(ExpenseTransactionMapper.class);

  private static final String REFERENCE_AMOUNT_NUMBER_FORMAT = "[^\\d\\.]+";
  private static final double ZERO_AMOUNT = 0.0;

  /**
   * Private constructor to avoid creation of object
   */
  private ExpenseTransactionMapper() {
  }

  /**
   * Return a list {@link ExpenseManagerTransaction} filtered by the transaction amount followed by the payment
   * method.
   *
   * @param expenseReports the list of expense reports
   * @return a list {@link ExpenseManagerTransaction} filtered by the transaction amount followed by the payment method.
   */
  public static Map<Double, Map<PaymentMethod, List<ExpenseManagerTransaction>>> mapExpenseReportsToMap(
      List<ExpenseReport> expenseReports) {
    Map<Double, Map<PaymentMethod, List<ExpenseManagerTransaction>>> expensesByAmountAndPaymentMethod = new HashMap<>();
    for (ExpenseReport expenseReport : expenseReports) {
      ExpenseManagerTransaction expenseManagerTransaction;
      try {
        expenseManagerTransaction = mapAExpenseReport(expenseReport);
      } catch (RuntimeException runtimeException) {
        LOGGER.atWarn()
            .withThrowable(runtimeException)
            .log("Unable to parse expense report entry. expenseReport={}", expenseReport);
        continue;
      }
      Double transactionAmount = (expenseManagerTransaction.getReferenceAmount() > 0)
          ? expenseManagerTransaction.getReferenceAmount()
          : expenseManagerTransaction.getAmount();
      PaymentMethod paymentMethod = expenseManagerTransaction.getPaymentMethod();
      Map<PaymentMethod, List<ExpenseManagerTransaction>> expenseTransactionsByPaymentMethod =
          expensesByAmountAndPaymentMethod.computeIfAbsent(transactionAmount, k -> new HashMap<>());
      List<ExpenseManagerTransaction> expenseManagerTransactionList =
          expenseTransactionsByPaymentMethod.computeIfAbsent(paymentMethod, k -> new ArrayList<>());
      expenseManagerTransactionList.add(expenseManagerTransaction);
    }
    return expensesByAmountAndPaymentMethod;
  }

  /**
   * Return a {@link ExpenseManagerTransaction} mapped from a {@link ExpenseReport}
   *
   * @param expenseReport the {@link ExpenseReport} to be mapped
   * @return the mapped {@link ExpenseManagerTransaction}
   */
  private static ExpenseManagerTransaction mapAExpenseReport(ExpenseReport expenseReport) {
    String amountString = expenseReport.getAmount();
    double amount = Double.parseDouble(amountString);
    String expenseCategoryString = expenseReport.getCategory();
    ExpenseCategory expenseCategory = ExpenseCategory.resolve(expenseCategoryString);
    String expenseCategorySubcategoryString = expenseReport.getSubcategory();
    ExpenseSubCategory expenseSubCategory = ExpenseSubCategory.resolve(expenseCategorySubcategoryString);
    String description = expenseReport.getDescription();
    long expendedTimeInSecondSinceEpoch = expenseReport.getExpensedTime();
    Instant expendedTime = Instant.ofEpochMilli(expendedTimeInSecondSinceEpoch); //This time is in UTC
    String paymentMethodString = expenseReport.getPaymentMethod();
    PaymentMethod paymentMethod = PaymentMethod.resolve(paymentMethodString);
    ExpenseManagerTransaction expenseManagerTransaction = ExpenseManagerTransaction.create(amount, expenseCategory,
        expenseSubCategory, paymentMethod, description, expendedTime);
    String referenceNumber = expenseReport.getReferenceNumber();
    double referenceAmount = (referenceNumber.isBlank()) ? ZERO_AMOUNT : parseReferenceAmount(referenceNumber);
    expenseManagerTransaction.setReferenceAmount(referenceAmount);
    return expenseManagerTransaction;
  }

  private static double parseReferenceAmount(final String referenceAmountString) {
    String cleansedReferenceAmountString = referenceAmountString.replaceAll(REFERENCE_AMOUNT_NUMBER_FORMAT, "");
    double referenceAmount = Double.parseDouble(cleansedReferenceAmountString);
    LOGGER.atTrace().log("Converted a reference amount string {} to {}", referenceAmountString, referenceAmount);
    return referenceAmount;
  }
}