package expense_tally.persistence;

import expense_tally.model.ExpenseTransaction;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * A static class to provide methods for mapping {@link expense_tally.persistence.ExpenseReport} to
 * {@link expense_tally.model.ExpenseTransaction}.
 *
 * <p>This class is an use case of the Mapper design pattern, in which the class's main purpose is to provide a set of
 * helpful method to map {@link expense_tally.persistence.ExpenseReport} into
 * {@link expense_tally.model.ExpenseTransaction} of various forms. This implementation is inspired by
 * <a href="https://stackoverflow.com/a/11832149/1522867">a stackoverflow answer</a>. The deciding factor between
 * the design patterns of Mapper, Builder and Factory pattern is essential <q>as things evolve, some applications are
 * not aware of additional attributes that needs to go under constructor where one either computes it or use default.
 * The critical thing is that mapper can do this for you</q>, taken from
 * <a href="https://softwareengineering.stackexchange.com/a/117527/88556">here</a>.</p>
 *
 * @see ExpenseTransaction
 * @see ExpenseReport
 */
public final class ExpenseTransactionMapper {
    private static final Logger LOGGER = Logger.getLogger(ExpenseTransactionMapper.class.getName());

    /**
     * Return a {@link Map} of {@link ExpenseTransaction} mapped from a list of {@link ExpenseReport}.
     * @param expenseReports the list of expense reports
     * @return a {@link Map} with transaction amount as key, and the list of expense transactions as value.
     */
    public static Map<Double, List<ExpenseTransaction>> mapExpenseReportsToMap(List<ExpenseReport> expenseReports) {
        Map<Double, List<ExpenseTransaction>> expenseTransactionMap = new HashMap();
        for(ExpenseReport expenseReport : expenseReports) {
            ExpenseTransaction expenseTransaction = mapAExpenseReport(expenseReport);
            Double transactionAmount = (expenseTransaction.getReferenceAmount() > 0)
                    ? expenseTransaction.getReferenceAmount()
                    : expenseTransaction.getAmount();
            List<ExpenseTransaction> expenseTransactionList;
            if (expenseTransactionMap.containsKey(transactionAmount)) {
                expenseTransactionList = expenseTransactionMap.get(transactionAmount);
            } else {
                expenseTransactionList = new ArrayList();
            }
            expenseTransactionList.add(expenseTransaction);
            expenseTransactionMap.put(transactionAmount, expenseTransactionList);
        }
        return expenseTransactionMap;
    }

    /**
     * Return a list of {@link ExpenseTransaction} mapped from a list of {@link ExpenseReport}.
     * @param expenseReports the list of expense reports
     * @return a list of {@link ExpenseTransaction}
     */
    public static List<ExpenseTransaction> mapExpenseReportsToList(List<ExpenseReport> expenseReports) {
        List<ExpenseTransaction> expenseTransactions = new ArrayList<>();
        for(ExpenseReport expenseReport : expenseReports) {
            expenseTransactions.add(mapAExpenseReport(expenseReport));
        }
        return expenseTransactions;
    }

    /**
     * Return a {@link ExpenseTransaction} mapped from a {@link ExpenseReport}
     * @param expenseReport the {@link ExpenseReport} to be mapped
     * @return the mapped {@link ExpenseTransaction}
     */
    private static ExpenseTransaction mapAExpenseReport (ExpenseReport expenseReport) {
        ExpenseTransaction expenseTransaction = new ExpenseTransaction();
        expenseTransaction.setAmount(Double.parseDouble(expenseReport.getAmount()));
        expenseTransaction.setCategory(expenseReport.getCategory());
        expenseTransaction.setDescription(expenseReport.getDescription());
        Instant expensedTime = Instant.ofEpochMilli(expenseReport.getExpensed()); //This time is in UTC
        expenseTransaction.setExpensedTime(Instant.ofEpochMilli(expenseReport.getExpensed()));
        expenseTransaction.setPaymentMethod(expenseReport.getPaymentMethod());
        if (!expenseReport.getReferenceNumber().isBlank()) {
            expenseTransaction.setReferenceAmount(Double.parseDouble(expenseReport.getReferenceNumber().replaceAll("[^\\d\\.]+", "")));
            LOGGER.fine("Reference Amount is " + expenseTransaction.getReferenceAmount());
        } else {
            expenseTransaction.setReferenceAmount(0.0);
        }
        expenseTransaction.setSubcategory(expenseReport.getSubcategory());
        return expenseTransaction;
    }
}