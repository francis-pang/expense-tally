package expense_tally.reconciliation;

import expense_tally.model.csv.GenericCsvTransaction;
import expense_tally.model.csv.TransactionType;
import expense_tally.model.persistence.transformation.ExpenseCategory;
import expense_tally.model.persistence.transformation.ExpenseManagerTransaction;
import expense_tally.model.persistence.transformation.ExpenseSubCategory;
import expense_tally.model.persistence.transformation.PaymentMethod;
import org.assertj.core.api.Assertions;
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
   * GenericCsvTransaction:
   * Size - 0, 1, many,
   * Transaction Type: refers to {@link TransactionType}. e.g POS, ATR, AWL
   * debit amount - negative, 0, positive
   * credit amount - negative, 0, positive
   * Reference - ?? (There are some extra logic)
   * Transaction ref1/2/3 - What is the special holder for each of the ref?
   * <p>
   * ExpenseManagerTransaction:
   * Size - 0, 1, many
   * amount - negative, 0, positive
   * ExpenseCategory - Refers to {@link ExpenseCategory}
   * ExpenseSubCategory - Refers to {@link ExpenseSubCategory}
   * Payment Method - Refers to {@link PaymentMethod}
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
    List<GenericCsvTransaction> testGenericCsvTransactions = new ArrayList<>();
    testGenericCsvTransactions.add(
        new CsvTransactionTestBuilder()
            .debitAmount(1.0)
            .transactionType(TransactionType.BILL_PAYMENT)
            .build()
    );

    assertThat(ExpenseReconciler.reconcileBankData(testGenericCsvTransactions, new HashMap<>()))
        .isNotNull()
        .hasSize(1)
        .extracting(DiscrepantTransaction::getAmount, DiscrepantTransaction::getDescription, DiscrepantTransaction::getTime, DiscrepantTransaction::getType)
        .contains(Assertions.tuple(1.0, "KOUFU PTE LTD SI NG 24APR,5548-2741-0014-1067", LocalDate.of(2009, 4, 24), TransactionType.BILL_PAYMENT));
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
    final Map<Double, Map<PaymentMethod, List<ExpenseManagerTransaction>>> testExpenseTransactionMap = new ExpnseMngrTrnsctnTestMapBuilder(0).build();
    assertThat(ExpenseReconciler.reconcileBankData(new ArrayList<>(), testExpenseTransactionMap))
        .isNotNull()
        .hasSize(0);
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
    List<GenericCsvTransaction> testGenericCsvTransactions = new ArrayList<>();
    testGenericCsvTransactions.add(new CsvTransactionTestBuilder()
        .debitAmount(1.0)
        .transactionType(TransactionType.BILL_PAYMENT)
        .build()
    );
    assertThatThrownBy(() -> ExpenseReconciler.reconcileBankData(testGenericCsvTransactions, null))
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
    final Map<Double, Map<PaymentMethod, List<ExpenseManagerTransaction>>> expensesByAmountAndPaymentMethod
        = new ExpnseMngrTrnsctnTestMapBuilder(0).build();
    assertThatThrownBy(() -> ExpenseReconciler.reconcileBankData(null, expensesByAmountAndPaymentMethod))
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
    List<GenericCsvTransaction> testGenericCsvTransactionList = new ArrayList<>();
    testGenericCsvTransactionList.add(new CsvTransactionTestBuilder()
        .transactionType(TransactionType.MASTERCARD)
        .build()
    );
    final Map<Double, Map<PaymentMethod, List<ExpenseManagerTransaction>>> expensesByAmountAndPaymentMethod =
        new ExpnseMngrTrnsctnTestMapBuilder(1)
            .amount(0.0)
            .build();

    assertThat(ExpenseReconciler.reconcileBankData(testGenericCsvTransactionList, expensesByAmountAndPaymentMethod))
        .isNotNull()
        .hasSize(1)
        .extracting(DiscrepantTransaction::getAmount, DiscrepantTransaction::getDescription, DiscrepantTransaction::getTime, DiscrepantTransaction::getType)
        .contains(Assertions.tuple(0.8, "KOUFU PTE LTD SI NG 24APR,5548-2741-0014-1067", LocalDate.of(2009, 4, 24), TransactionType.MASTERCARD));
  }

  /*
   * Test Input:
   * - 1 Expense Manager transaction
   * - 1 CSV Transaction
   *
   * Scenario: That that they match
   */
  @Test
  void reconcileBankData_singleMatchingTransaction() {
    List<GenericCsvTransaction> testGenericCsvTransactionList = new ArrayList<>();
    testGenericCsvTransactionList.add(new CsvTransactionTestBuilder()
        .transactionType(TransactionType.MASTERCARD)
        .build()
    );
    final Map<Double, Map<PaymentMethod, List<ExpenseManagerTransaction>>> expensesByAmountAndPaymentMethod =
        new ExpnseMngrTrnsctnTestMapBuilder(1)
            .build();

    assertThat(ExpenseReconciler.reconcileBankData(testGenericCsvTransactionList, expensesByAmountAndPaymentMethod))
        .isNotNull()
        .hasSize(0);
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
    List<GenericCsvTransaction> testGenericCsvTransactionList = new ArrayList<>();
    testGenericCsvTransactionList.add(new CsvTransactionTestBuilder()
        .transactionType(TransactionType.MASTERCARD)
        .build()
    );

    final Map<Double, Map<PaymentMethod, List<ExpenseManagerTransaction>>> expensesByAmountAndPaymentMethod =
        new ExpnseMngrTrnsctnTestMapBuilder(1)
            .amount(0.5)
            .build();

    assertThat(ExpenseReconciler.reconcileBankData(testGenericCsvTransactionList, expensesByAmountAndPaymentMethod))
        .isNotNull()
        .hasSize(1)
        .extracting(
            DiscrepantTransaction::getAmount,
            DiscrepantTransaction::getDescription,
            DiscrepantTransaction::getTime,
            DiscrepantTransaction::getType
        )
        .contains(
            Assertions.tuple(0.8, "KOUFU PTE LTD SI NG 24APR,5548-2741-0014-1067", LocalDate.of(2009, 4, 24), TransactionType.MASTERCARD)
        );
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
    List<GenericCsvTransaction> testGenericCsvTransactionList = new ArrayList<>();
    testGenericCsvTransactionList.add(new CsvTransactionTestBuilder()
        .transactionType(TransactionType.MASTERCARD)
        .build()
    );

    testGenericCsvTransactionList.add(new CsvTransactionTestBuilder()
        .transactionType(TransactionType.PAY_NOW)
        .build()
    );

    testGenericCsvTransactionList.add(new CsvTransactionTestBuilder()
        .debitAmount(5.0)
        .transactionType(TransactionType.PAY_NOW)
        .build()
    );

    final Map<Double, Map<PaymentMethod, List<ExpenseManagerTransaction>>> expensesByAmountAndPaymentMethod =
        new ExpnseMngrTrnsctnTestMapBuilder(6)
            .addCustomisedTransaction(5.0, PaymentMethod.ELECTRONIC_TRANSFER, 2009, 4, 24)
            .build();

    assertThat(ExpenseReconciler.reconcileBankData(testGenericCsvTransactionList, expensesByAmountAndPaymentMethod))
        .isNotNull()
        .hasSize(1)
        .extracting(DiscrepantTransaction::getAmount, DiscrepantTransaction::getDescription, DiscrepantTransaction::getTime, DiscrepantTransaction::getType)
        .contains(Assertions.tuple(0.8, "KOUFU PTE LTD SI NG 24APR,5548-2741-0014-1067", LocalDate.of(2009, 4, 24), TransactionType.PAY_NOW));
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
    List<GenericCsvTransaction> testGenericCsvTransactionList = new ArrayList<>();
    testGenericCsvTransactionList.add(new CsvTransactionTestBuilder()
        .transactionType(TransactionType.MASTERCARD)
        .build()
    );

    final Map<Double, Map<PaymentMethod, List<ExpenseManagerTransaction>>> expensesByAmountAndPaymentMethod =
        new ExpnseMngrTrnsctnTestMapBuilder(3)
            .build();
    assertThat(ExpenseReconciler.reconcileBankData(testGenericCsvTransactionList, expensesByAmountAndPaymentMethod))
        .isNotNull()
        .hasSize(0);
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
    List<GenericCsvTransaction> testGenericCsvTransactionList = new ArrayList<>();
    testGenericCsvTransactionList.add(new CsvTransactionTestBuilder()
        .transactionDate(2015, 4, 24)
        .build()
    );

    final Map<Double, Map<PaymentMethod, List<ExpenseManagerTransaction>>> expensesByAmountAndPaymentMethod =
        new ExpnseMngrTrnsctnTestMapBuilder(3)
            .build();

    assertThat(ExpenseReconciler.reconcileBankData(testGenericCsvTransactionList, expensesByAmountAndPaymentMethod))
        .isNotNull()
        .hasSize(1)
        .extracting(DiscrepantTransaction::getAmount, DiscrepantTransaction::getDescription, DiscrepantTransaction::getTime, DiscrepantTransaction::getType)
        .contains(Assertions.tuple(0.8, "KOUFU PTE LTD SI NG 24APR,5548-2741-0014-1067", LocalDate.of(2015, 4, 24), TransactionType.MASTERCARD));
  }

  /**
   * Test that there is no matching transaction, but cannot be detected because the transaction type is different
   */
  @Test
  void reconcileBankData_testNullTransactionType() {
    List<GenericCsvTransaction> testGenericCsvTransactionList = new ArrayList<>();
    GenericCsvTransaction testGenericCsvTransaction = new CsvTransactionTestBuilder()
        .transactionType(TransactionType.BILL_PAYMENT)
        .build();
    testGenericCsvTransaction.setTransactionType(null);
    testGenericCsvTransactionList.add(testGenericCsvTransaction);


    final Map<Double, Map<PaymentMethod, List<ExpenseManagerTransaction>>> expensesByAmountAndPaymentMethod =
        new ExpnseMngrTrnsctnTestMapBuilder(1)
            .amount(0.5)
            .build();

    assertThat(ExpenseReconciler.reconcileBankData(testGenericCsvTransactionList, expensesByAmountAndPaymentMethod))
        .isNotNull()
        .hasSize(0);
  }

  /**
   * Test that no debit amount inside csv, but cannot be detected
   */
  @Test
  void reconcileBankData_testZeroDebitAmountInCsv() {
    List<GenericCsvTransaction> testGenericCsvTransactionList = new ArrayList<>();
    testGenericCsvTransactionList.add(new CsvTransactionTestBuilder()
        .debitAmount(0.0)
        .build()
    );

    final Map<Double, Map<PaymentMethod, List<ExpenseManagerTransaction>>> expensesByAmountAndPaymentMethod =
        new ExpnseMngrTrnsctnTestMapBuilder(1)
            .amount(0.5)
            .build();

    assertThat(ExpenseReconciler.reconcileBankData(testGenericCsvTransactionList, expensesByAmountAndPaymentMethod))
        .isNotNull()
        .hasSize(0);
  }

  /**
   * Test that NETS payment method inside csv, but cannot be detected
   */
  @Test
  void reconcileBankData_testNetsInCsv_Matching() {
    List<GenericCsvTransaction> testGenericCsvTransactionList = new ArrayList<>();
    testGenericCsvTransactionList.add(new CsvTransactionTestBuilder()
        .transactionType(TransactionType.POINT_OF_SALE)
        .build()
    );

    final Map<Double, Map<PaymentMethod, List<ExpenseManagerTransaction>>> expensesByAmountAndPaymentMethod =
        new ExpnseMngrTrnsctnTestMapBuilder(1)
            .paymentMethod(PaymentMethod.NETS)
            .build();

    assertThat(ExpenseReconciler.reconcileBankData(testGenericCsvTransactionList, expensesByAmountAndPaymentMethod))
        .isNotNull()
        .hasSize(0);
  }

  /**
   * Test that mismatch because of payment methods
   */
  @Test
  void reconcileBankData_testGiroInCsv_NonMatching() {
    List<GenericCsvTransaction> testGenericCsvTransactionList = new ArrayList<>();
    testGenericCsvTransactionList.add(new CsvTransactionTestBuilder()
        .transactionType(TransactionType.GIRO)
        .build()
    );

    final Map<Double, Map<PaymentMethod, List<ExpenseManagerTransaction>>> expensesByAmountAndPaymentMethod =
        new ExpnseMngrTrnsctnTestMapBuilder(1)
            .paymentMethod(PaymentMethod.ELECTRONIC_TRANSFER)
            .build();

    assertThat(ExpenseReconciler.reconcileBankData(testGenericCsvTransactionList, expensesByAmountAndPaymentMethod))
        .isNotNull()
        .hasSize(1)
        .extracting(DiscrepantTransaction::getAmount, DiscrepantTransaction::getDescription, DiscrepantTransaction::getTime, DiscrepantTransaction::getType)
        .contains(Assertions.tuple(0.8, "KOUFU PTE LTD SI NG 24APR,5548-2741-0014-1067", LocalDate.of(2009, 4, 24), TransactionType.GIRO));
  }

  /**
   * Test that mismatch because of a CREDIT payment methods, this will never happen. If happen, there is something wrong
   */
  @Test
  void reconcileBankData_testInterestInCsv_NonMatching() {
    List<GenericCsvTransaction> testGenericCsvTransactionList = new ArrayList<>();
    testGenericCsvTransactionList.add(new CsvTransactionTestBuilder()
        .transactionType(TransactionType.INTEREST_EARNED)
        .build()
    );

    final Map<Double, Map<PaymentMethod, List<ExpenseManagerTransaction>>> expensesByAmountAndPaymentMethod =
        new ExpnseMngrTrnsctnTestMapBuilder(1)
            .paymentMethod(PaymentMethod.ELECTRONIC_TRANSFER)
            .build();

    assertThat(ExpenseReconciler.reconcileBankData(testGenericCsvTransactionList, expensesByAmountAndPaymentMethod))
        .isNotNull()
        .hasSize(0);
  }
}