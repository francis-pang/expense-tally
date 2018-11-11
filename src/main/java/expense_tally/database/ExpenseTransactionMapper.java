package expense_tally.database;

import expense_tally.model.ExpenseTransaction;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class ExpenseTransactionMapper {
    private static final Logger LOGGER = Logger.getLogger(ExpenseTransactionMapper.class.getName());

    public static ExpenseTransaction mapAExpenseReport (ExpenseReport expenseReport) {
        ExpenseTransaction expenseTransaction = new ExpenseTransaction();
        expenseTransaction.setAmount(Double.parseDouble(expenseReport.getAmount()));
        expenseTransaction.setCategory(expenseReport.getCategory());
        expenseTransaction.setDescription(expenseReport.getDescription());
        expenseTransaction.setExpensedTime(Instant.ofEpochMilli(Long.valueOf(expenseReport.getExpensed())));
        expenseTransaction.setPaymentMethod(expenseReport.getPaymentMethod());
        if (!expenseReport.getReferenceNumber().isBlank()) {
            expenseTransaction.setReferenceAmount(Double.parseDouble(expenseReport.getReferenceNumber().replaceAll("[^0-9]+", "")));
        } else {
            expenseTransaction.setReferenceAmount(0.0);
        }

        expenseTransaction.setSubcategory(expenseReport.getSubcategory());
        return expenseTransaction;
    }

    public static List<ExpenseTransaction> mapExpenseReportsByList(List<ExpenseReport> expenseReports) {
        List<ExpenseTransaction> expenseTransactions = new ArrayList<>();

        for(ExpenseReport expenseReport : expenseReports) {
            expenseTransactions.add(mapAExpenseReport(expenseReport));
        }
        return expenseTransactions;
    }

    public static Map<Double, List<ExpenseTransaction>> mapExpenseReports(List<ExpenseReport> expenseReports) {
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
}
