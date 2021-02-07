package expense_tally.expense_manager.persistence.database;

import expense_tally.AppStringConstant;
import expense_tally.expense_manager.persistence.ExpenseReadable;
import expense_tally.expense_manager.persistence.ExpenseUpdatable;
import expense_tally.expense_manager.persistence.database.mapper.ExpenseManagerTransactionMapper;
import expense_tally.model.persistence.transformation.ExpenseCategory;
import expense_tally.model.persistence.transformation.ExpenseManagerTransaction;
import expense_tally.model.persistence.transformation.ExpenseSubCategory;
import expense_tally.model.persistence.transformation.PaymentMethod;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.session.SqlSession;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.Instant;
import java.util.List;
import java.util.Objects;

/**
 * This class provides proxy functionalities to interact with the {@link ExpenseManagerTransactionMapper} objects
 * stores at the data source.
 */
public class ExpenseManagerTransactionDatabaseProxy implements ExpenseReadable, ExpenseUpdatable {
  private static final Logger LOGGER = LogManager.getLogger(ExpenseManagerTransactionDatabaseProxy.class);
  private final SqlSession sqlSession;

  /**
   * Default class constructor
   * @param sqlSession SQL session
   */
  public ExpenseManagerTransactionDatabaseProxy(SqlSession sqlSession) {
    this.sqlSession = Objects.requireNonNull(sqlSession);
  }

  @Override
  public List<ExpenseManagerTransaction> getAllExpenseManagerTransaction() {
    ExpenseManagerTransactionMapper expenseManagerTransactionMapper =
        sqlSession.getMapper(ExpenseManagerTransactionMapper.class);
    return expenseManagerTransactionMapper.getAllExpenseManagerTransactions();
  }

  @Override
  public boolean add(ExpenseManagerTransaction expenseManagerTransaction) {
    //TODO: Consider having a validator class
    int id = expenseManagerTransaction.getId();
    if (id <= 0) {
      LOGGER.atWarn().log("id is non positive:{}", id);
      throw new IllegalArgumentException("ID cannot be non positive.");
    }
    Double amount = expenseManagerTransaction.getAmount();
    ExpenseCategory expenseCategory = expenseManagerTransaction.getCategory();
    if (expenseCategory == null) {
      LOGGER.atWarn().log("category is null");
      throw new IllegalArgumentException("Category cannot be null.");
    }
    ExpenseSubCategory expenseSubCategory = expenseManagerTransaction.getSubcategory();
    if (expenseSubCategory == null) {
      LOGGER.atWarn().log("subcategory is null.");
      throw new IllegalArgumentException("Subcategory cannot be null.");
    }
    PaymentMethod paymentMethod = expenseManagerTransaction.getPaymentMethod();
    if (paymentMethod == null) {
      LOGGER.atWarn().log("paymentMethod is null");
      throw new IllegalArgumentException("Payment Method cannot be null.");
    }
    String description = expenseManagerTransaction.getDescription();
    if (StringUtils.isBlank(description)) {
      LOGGER.atWarn().log("description is null/ blank:{}",
          StringUtils.defaultString(description, AppStringConstant.NULL.value()));
      throw new IllegalArgumentException("Description cannot be null or empty.");
    }
    Instant expensedTime = expenseManagerTransaction.getExpensedTime();
    Instant currentInstant = Instant.now();
    if (expensedTime == null || expensedTime.isAfter(currentInstant)) {
      LOGGER.atWarn().log("expensedTime is null/ in future time:{}",
          (expensedTime == null) ? AppStringConstant.NULL.value() : expensedTime.toString());
      throw new IllegalArgumentException("Expensed time cannot be null or in future.");
    }
    Double referenceAmount = expenseManagerTransaction.getReferenceAmount();
    if (referenceAmount == null) {
      referenceAmount = Double.parseDouble("0");
    } else if (referenceAmount < 0) {
      LOGGER.atWarn().log("referenceAmount is negative:{}", referenceAmount);
      throw new IllegalArgumentException("Reference amount cannot be negative.");
    }
    int numberOfInsertedEntry;
    try {
      ExpenseManagerTransactionMapper expenseManagerTransactionMapper =
          sqlSession.getMapper(ExpenseManagerTransactionMapper.class);
      numberOfInsertedEntry = expenseManagerTransactionMapper.addExpenseManagerTransaction(id, amount,
          expenseCategory, expenseSubCategory, paymentMethod, description, expensedTime,
          referenceAmount);
    } catch (RuntimeException runtimeException) {
      LOGGER.atWarn()
          .log("Unable to insert expense manager transaction. expenseManagerTransaction:{}", expenseManagerTransaction);
      throw  runtimeException;
    }
    return (numberOfInsertedEntry == 1);
  }

  @Override
  public boolean clear() {
    LOGGER.atTrace().log("Retrieve a database connection.");
    try {
      ExpenseManagerTransactionMapper expenseManagerTransactionMapper =
          sqlSession.getMapper(ExpenseManagerTransactionMapper.class);
      return expenseManagerTransactionMapper.deleteAllExpenseManagerTransactions();
    } catch (RuntimeException runtimeException) {
      LOGGER
          .atWarn()
          .log("Unable to delete all entries from database. configuration:{}, connection:{}",
              sqlSession.getConfiguration(),
              sqlSession.getConnection());
      throw runtimeException;
    }
  }
}
