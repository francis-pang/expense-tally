package expense_tally.reconciliation;

import expense_tally.csv_parser.model.CsvTransaction;
import expense_tally.csv_parser.model.TransactionType;
import expense_tally.expense_manager.model.ExpenseManagerMapKey;
import expense_tally.expense_manager.model.ExpenseManagerTransaction;
import expense_tally.expense_manager.model.PaymentMethod;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ExpenseReconcilerTest {

    /**
     * Test Case:
     * CsvTransaction:
     * Size - 0, 1, many,
     * Transaction Type: refers to {@link expense_tally.csv_parser.model.TransactionType}. e.g POS, ATR, AWL
     * debit amount - negative, 0, positive
     * credit amount - negative, 0, positive
     * Reference - ?? (There are some extra logic)
     * Transaction ref1/2/3 - What is the special holder for each of the ref?
     * <p>
     * ExpenseManagerTransaction:
     * Size - 0, 1, many
     * amount - negative, 0, positive
     * ExpenseCategory - Refers to {@link expense_tally.expense_manager.model.ExpenseCategory}
     * ExpenseSubCategory - Refers to {@link expense_tally.expense_manager.model.ExpenseSubCategory}
     * Payment Method - Refers to {@link expense_tally.expense_manager.model.PaymentMethod}
     * Description - empty, non-empty
     * Expensed Time- past/ future?
     * reference amount - tally?
     * <p>
     * Cross Link case:
     * 1) Matching everything except for Payment Method
     * 2a) Match everything except for transaction date (out of acceptable range)
     * 2b) Match everything except for transaction date (within acceptable range)
     * 3) Debit amount doesn't tally
     * 4) Debit amount doesn't tally, but credit amount tally
     * 5)
     */

    //TODO: Generalise a builder class to build CsvTransaction. This can be used for all test classes
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

    /*
     * Test Input:
     * 0 Expense Manager
     * 1 CSV Transaction
     *
     * Output: No matching records, and 1 mis-matching record
     */
    @Test
    void reconcileBankData_noExpenseManager() {
        List<CsvTransaction> testCsvTransactions = new ArrayList<>();
        testCsvTransactions.add(constructCsvTransaction("24-6-2019", "", 1, 0, "", "", "",
                TransactionType.BILL_PAYMENT));
        assertThat(ExpenseReconciler.reconcileBankData(testCsvTransactions, new HashMap<>())).isEqualTo(1);
    }


    /**
     * Test Input:
     * 1 Expense Manager
     * 0 CSV Transaction
     *
     * Output: No matching records
     */
    @Test
    void reconcileBankData_noCsvTransaction() {
        Map<ExpenseManagerMapKey, List<ExpenseManagerTransaction>> testExpenseTransactionMap = new HashMap<>();
        ExpenseManagerTransactionBuilder builder = new ExpenseManagerTransactionBuilder();
        ExpenseManagerTransaction expenseManagerTransaction = builder.build();
        List<ExpenseManagerTransaction> expenseManagerTransactionList = new ArrayList<>();
        expenseManagerTransactionList.add(expenseManagerTransaction);
        ExpenseManagerMapKey expenseManagerMapKey = new ExpenseManagerMapKey(expenseManagerTransaction.getPaymentMethod());
        expenseManagerMapKey.setAmount(expenseManagerTransaction.getAmount());
        testExpenseTransactionMap.put(expenseManagerMapKey, expenseManagerTransactionList);
        assertThat(ExpenseReconciler.reconcileBankData(new ArrayList<>(), testExpenseTransactionMap)).isEqualTo(0);
    }

    /**
     * Test Input
     * null expense manager
     * 1 CSV Transaction
     *
     * Output: Exception thrown
     */
    @Test
    void reconcileBankData_nullExpenseManager() {
        List<CsvTransaction> testCsvTransactions = new ArrayList<>();
        testCsvTransactions.add(constructCsvTransaction("24-6-2019", "", 1, 0, "", "", "",
                TransactionType.BILL_PAYMENT));
        assertThatThrownBy(() -> {
            ExpenseReconciler.reconcileBankData(testCsvTransactions, null);
        })
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Null reference is not an accepted expenseTransactionMap value.");
    }

    /**
     * Test Input
     * null expense manager
     * 1 CSV Transaction
     *
     * Output: Exception thrown
     */
    @Test
    void reconcileBankData_nullCsvTransaction() {
        Map<ExpenseManagerMapKey, List<ExpenseManagerTransaction>> testExpenseTransactionMap = new HashMap<>();
        ExpenseManagerTransactionBuilder builder = new ExpenseManagerTransactionBuilder();
        ExpenseManagerTransaction expenseManagerTransaction = builder.build();
        List<ExpenseManagerTransaction> expenseManagerTransactionList = new ArrayList<>();
        expenseManagerTransactionList.add(expenseManagerTransaction);
        ExpenseManagerMapKey expenseManagerMapKey = new ExpenseManagerMapKey(expenseManagerTransaction.getPaymentMethod());
        expenseManagerMapKey.setAmount(expenseManagerTransaction.getAmount());
        testExpenseTransactionMap.put(expenseManagerMapKey, expenseManagerTransactionList);

        assertThatThrownBy(() -> {
            ExpenseReconciler.reconcileBankData(null, testExpenseTransactionMap);
        })
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Null reference is not an accepted csvTransactions value.");
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
        expenseManagerTransactionList.add(new ExpenseManagerTransactionBuilder(0.7, PaymentMethod.ELECTRONIC_TRANSFER
                , 0.0).build());
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
        ExpenseManagerTransactionBuilder expenseManagerTransactionBuilder = new ExpenseManagerTransactionBuilder(0.8, PaymentMethod.ELECTRONIC_TRANSFER
                , 0.0);
        expenseManagerTransactionBuilder.setExpensedTime("2019-04-24T10:15:30.00Z");
        expenseManagerTransactionList.add(expenseManagerTransactionBuilder.build());
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