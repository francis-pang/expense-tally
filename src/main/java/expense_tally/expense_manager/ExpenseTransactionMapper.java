package expense_tally.expense_manager;

import expense_tally.expense_manager.model.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A static class to provide methods for mapping {@link ExpenseReport} to
 * {@link ExpenseManagerTransaction}.
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

    /**
     * Return a {@link Map} of {@link ExpenseManagerTransaction} mapped from a list of {@link ExpenseReport}.
     *
     * @param expenseReports the list of expense reports
     * @return a {@link Map} with transaction amount as key, and the list of expense transactions as value.
     */
    public static Map<ExpenseManagerMapKey, List<ExpenseManagerTransaction>> mapExpenseReportsToMap(List<ExpenseReport> expenseReports) {
        Map<ExpenseManagerMapKey, List<ExpenseManagerTransaction>> expenseTransactionMap = new HashMap<>();
        for (ExpenseReport expenseReport : expenseReports) {
            ExpenseManagerTransaction expenseManagerTransaction = mapAExpenseReport(expenseReport);
            Double transactionAmount = (expenseManagerTransaction.getReferenceAmount() > 0)
                    ? expenseManagerTransaction.getReferenceAmount()
                    : expenseManagerTransaction.getAmount();
            ExpenseManagerMapKey expenseManagerMapKey = new ExpenseManagerMapKey(PaymentMethod.resolve(expenseReport.getPaymentMethod()), transactionAmount);
            List<ExpenseManagerTransaction> expenseManagerTransactionList;
            if (expenseTransactionMap.containsKey(expenseManagerMapKey)) {
                expenseManagerTransactionList = expenseTransactionMap.get(expenseManagerMapKey);
            } else {
                expenseManagerTransactionList = new ArrayList<>();
            }
            expenseManagerTransactionList.add(expenseManagerTransaction);
            expenseTransactionMap.put(expenseManagerMapKey, expenseManagerTransactionList);
        }
        return expenseTransactionMap;
    }

    /**
     * Return a list of {@link ExpenseManagerTransaction} mapped from a list of {@link ExpenseReport}.
     *
     * @param expenseReports the list of expense reports
     * @return a list of {@link ExpenseManagerTransaction}
     */
    public static List<ExpenseManagerTransaction> mapExpenseReportsToList(List<ExpenseReport> expenseReports) {
        List<ExpenseManagerTransaction> expenseManagerTransactions = new ArrayList<>();
        for (ExpenseReport expenseReport : expenseReports) {
            expenseManagerTransactions.add(mapAExpenseReport(expenseReport));
        }
        return expenseManagerTransactions;
    }

    /**
     * Return a {@link ExpenseManagerTransaction} mapped from a {@link ExpenseReport}
     *
     * @param expenseReport the {@link ExpenseReport} to be mapped
     * @return the mapped {@link ExpenseManagerTransaction}
     */
    private static ExpenseManagerTransaction mapAExpenseReport(ExpenseReport expenseReport) {
        ExpenseManagerTransaction expenseManagerTransaction = new ExpenseManagerTransaction();
        expenseManagerTransaction.setAmount(Double.parseDouble(expenseReport.getAmount()));
        expenseManagerTransaction.setCategory(ExpenseCategory.resolve(expenseReport.getCategory()));
        expenseManagerTransaction.setDescription(expenseReport.getDescription());
        expenseManagerTransaction.setExpensedTime(Instant.ofEpochMilli(expenseReport.getExpensedTime())); //This time is in UTC
        expenseManagerTransaction.setPaymentMethod(PaymentMethod.resolve(expenseReport.getPaymentMethod()));
        if (!expenseReport.getReferenceNumber().isBlank()) {
            expenseManagerTransaction.setReferenceAmount(Double.parseDouble(expenseReport.getReferenceNumber().replaceAll("[^\\d\\.]+", "")));
            LOGGER.trace("TransactionType Amount is " + expenseManagerTransaction.getReferenceAmount());
        } else {
            expenseManagerTransaction.setReferenceAmount(0.0);
        }
        expenseManagerTransaction.setSubcategory(ExpenseSubCategory.resolve(expenseReport.getSubcategory()));
        return expenseManagerTransaction;
    }
}