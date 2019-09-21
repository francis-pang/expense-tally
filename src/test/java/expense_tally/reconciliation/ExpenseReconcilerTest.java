package expense_tally.reconciliation;

import expense_tally.csv_parser.model.CsvTransaction;
import expense_tally.csv_parser.model.TransactionType;
import expense_tally.expense_manager.model.ExpenseManagerMapKey;
import expense_tally.expense_manager.model.ExpenseManagerTransaction;
import expense_tally.expense_manager.model.PaymentMethod;
import org.junit.jupiter.api.Test;

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
        testCsvTransactions.add(
            new CsvTransactionTestBuilder()
                .debitAmount(1.0)
                .transactionType(TransactionType.BILL_PAYMENT)
                .build()
        );
        assertThat(ExpenseReconciler.reconcileBankData(testCsvTransactions, new HashMap<>())).isEqualTo(1);
    }

    /**
     * Test Input:
     * 1 Expense Manager
     * 0 CSV Transaction
     * <p>
     * Output: No matching records
     */
    @Test
    void reconcileBankData_noCsvTransaction() {
        Map<ExpenseManagerMapKey, List<ExpenseManagerTransaction>> testExpenseTransactionMap = new ExpnseMngrTrnsctnTestMapBuilder(0).build();
        assertThat(ExpenseReconciler.reconcileBankData(new ArrayList<>(), testExpenseTransactionMap)).isEqualTo(0);
    }

    /**
     * Test Input
     * null expense manager
     * 1 CSV Transaction
     * <p>
     * Output: Exception thrown
     */
    @Test
    void reconcileBankData_nullExpenseManager() {
        List<CsvTransaction> testCsvTransactions = new ArrayList<>();
        testCsvTransactions.add(new CsvTransactionTestBuilder()
            .debitAmount(1.0)
            .transactionType(TransactionType.BILL_PAYMENT)
            .build()
        );
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
     * <p>
     * Output: Exception thrown
     */
    @Test
    void reconcileBankData_nullCsvTransaction() {
        Map<ExpenseManagerMapKey, List<ExpenseManagerTransaction>> testExpenseTransactionMap =
            new ExpnseMngrTrnsctnTestMapBuilder(0).build();
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
        List<CsvTransaction> testCsvTransactionList = new ArrayList<>();
        testCsvTransactionList.add(new CsvTransactionTestBuilder()
            .reference("MST")
            .transactionType(TransactionType.MASTERCARD)
            .build()
        );

        Map<ExpenseManagerMapKey, List<ExpenseManagerTransaction>> testExpenseManagerMap =
            new ExpnseMngrTrnsctnTestMapBuilder(1)
                .amount(0.0)
                .build();
        assertThat(ExpenseReconciler.reconcileBankData(testCsvTransactionList, testExpenseManagerMap)).isEqualTo(1);
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
        List<CsvTransaction> testCsvTransactionList = new ArrayList<>();
        testCsvTransactionList.add(new CsvTransactionTestBuilder()
            .reference("MST")
            .transactionType(TransactionType.MASTERCARD)
            .build()
        );
        Map<ExpenseManagerMapKey, List<ExpenseManagerTransaction>> testExpenseManagerMap =
            new ExpnseMngrTrnsctnTestMapBuilder(1)
                .build();
        assertThat(ExpenseReconciler.reconcileBankData(testCsvTransactionList, testExpenseManagerMap)).isEqualTo(0);
    }

    /*
     * Test Input:
     * - 1 Expense Manager transaction
     * - 1 CSV Transaction
     *
     * Scenario: That that they do not match
     */
    @Test
    void reconcileBankData_singleNonMatchingTransaction() {
        List<CsvTransaction> testCsvTransactionList = new ArrayList<>();
        testCsvTransactionList.add(new CsvTransactionTestBuilder()
            .reference("MST")
            .transactionType(TransactionType.MASTERCARD)
            .build()
        );

        Map<ExpenseManagerMapKey, List<ExpenseManagerTransaction>> testExpenseManagerMap =
            new ExpnseMngrTrnsctnTestMapBuilder(1)
                .amount(0.5)
                .build();
        assertThat(ExpenseReconciler.reconcileBankData(testCsvTransactionList, testExpenseManagerMap)).isEqualTo(1);
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
        List<CsvTransaction> testCsvTransactionList = new ArrayList<>();
        testCsvTransactionList.add(new CsvTransactionTestBuilder()
            .reference("MST")
            .transactionType(TransactionType.MASTERCARD)
            .build()
        );

        testCsvTransactionList.add(new CsvTransactionTestBuilder()
            .transactionType(TransactionType.PAY_NOW)
            .build()
        );

        testCsvTransactionList.add(new CsvTransactionTestBuilder()
            .debitAmount(5.0)
            .transactionType(TransactionType.PAY_NOW)
            .build()
        );

        Map<ExpenseManagerMapKey, List<ExpenseManagerTransaction>> testExpenseManagerMap =
            new ExpnseMngrTrnsctnTestMapBuilder(3)
                .addCustomisedTransaction(5.0, PaymentMethod.ELECTRONIC_TRANSFER, 2009, 4, 24)
                .build();
        assertThat(ExpenseReconciler.reconcileBankData(testCsvTransactionList, testExpenseManagerMap)).isEqualTo(1);
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
        List<CsvTransaction> testCsvTransactionList = new ArrayList<>();
        testCsvTransactionList.add(new CsvTransactionTestBuilder()
            .reference("MST")
            .transactionType(TransactionType.MASTERCARD)
            .build()
        );

        Map<ExpenseManagerMapKey, List<ExpenseManagerTransaction>> testExpenseManagerMap =
            new ExpnseMngrTrnsctnTestMapBuilder(3)
                .build();
        assertThat(ExpenseReconciler.reconcileBankData(testCsvTransactionList, testExpenseManagerMap)).isEqualTo(0);
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
        List<CsvTransaction> testCsvTransactionList = new ArrayList<>();
        testCsvTransactionList.add(new CsvTransactionTestBuilder()
            .transactionDate(2015,4,24)
            .build()
        );

        Map<ExpenseManagerMapKey, List<ExpenseManagerTransaction>> testExpenseManagerMap =
            new ExpnseMngrTrnsctnTestMapBuilder(3)
                .build();
        assertThat(ExpenseReconciler.reconcileBankData(testCsvTransactionList, testExpenseManagerMap)).isEqualTo(1);
    }
}