package expense_tally.expense_manager.persistence.database;

import expense_tally.AppStringConstant;
import expense_tally.expense_manager.mapper.ExpenseManagerTransactionMapper;
import expense_tally.expense_manager.persistence.ExpenseReadable;
import expense_tally.expense_manager.persistence.ExpenseUpdatable;
import expense_tally.model.persistence.transformation.ExpenseCategory;
import expense_tally.model.persistence.transformation.ExpenseManagerTransaction;
import expense_tally.model.persistence.transformation.ExpenseSubCategory;
import expense_tally.model.persistence.transformation.PaymentMethod;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * This class provides proxy functionalities to interact with the {@link ExpenseManagerTransactionMapper} objects
 * stores at the data source.
 */
public class ExpenseManagerTransactionDatabaseProxy implements ExpenseReadable, ExpenseUpdatable {
  private static final Logger LOGGER = LogManager.getLogger(ExpenseManagerTransactionDatabaseProxy.class);
  private ExpenseManagerTransactionMapper expenseManagerTransactionMapper;
  private final DatabaseConnectable databaseConnectable;
  private final DatabaseSessionFactoryBuilder databaseSessionFactoryBuilder;
  private final String environmentId;

  public ExpenseManagerTransactionDatabaseProxy(DatabaseConnectable databaseConnectable,
                                                DatabaseSessionFactoryBuilder databaseSessionFactoryBuilder,
                                                String environmentId) {
    this.databaseConnectable = databaseConnectable;
    this.databaseSessionFactoryBuilder = databaseSessionFactoryBuilder;
    this.environmentId = environmentId;
  }

  @Override
  public List<ExpenseManagerTransaction> getAllExpenseManagerTransaction() throws IOException, SQLException {
    try (Connection connection = databaseConnectable.connect()) {
      SqlSessionFactory sqlSessionFactory = databaseSessionFactoryBuilder.buildSessionFactory(environmentId);
      try (SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.SIMPLE, connection)) {
        expenseManagerTransactionMapper = sqlSession.getMapper(ExpenseManagerTransactionMapper.class);
        List<ExpenseManagerTransactionMapper.ExpnsMngrTrnsctnMpprIntermediate> expnsMngrTrnsctnMpprIntermediates =
            expenseManagerTransactionMapper.getAllExpenseManagerTransactions();
        return convert(expnsMngrTrnsctnMpprIntermediates);
      }
    }
  }

  /**
   * Convert a list of
   * {@link expense_tally.expense_manager.mapper.ExpenseManagerTransactionMapper.ExpnsMngrTrnsctnMpprIntermediate} into
   * a list of {@link ExpenseManagerTransaction}
   * @param expnsMngrTrnsctnMpprIntermediates ExpnsMngrTrnsctnMpprIntermediate to be converted
   * @return a list of converted {@link ExpenseManagerTransaction}
   */
  private List<ExpenseManagerTransaction> convert(
      List<ExpenseManagerTransactionMapper.ExpnsMngrTrnsctnMpprIntermediate> expnsMngrTrnsctnMpprIntermediates) {
    List<ExpenseManagerTransaction> expenseManagerTransactions = new ArrayList<>();
    expnsMngrTrnsctnMpprIntermediates.forEach(expnsMngrTrnsctnMpprIntermediate -> {
      ExpenseManagerTransaction expenseManagerTransaction = ExpenseManagerTransaction.create(
          expnsMngrTrnsctnMpprIntermediate.getId(),
          expnsMngrTrnsctnMpprIntermediate.getAmount(),
          ExpenseCategory.resolve(expnsMngrTrnsctnMpprIntermediate.getCategory()),
          ExpenseSubCategory.resolve(expnsMngrTrnsctnMpprIntermediate.getSubcategory()),
          PaymentMethod.resolve(expnsMngrTrnsctnMpprIntermediate.getPaymentMethod()),
          expnsMngrTrnsctnMpprIntermediate.getDescription(),
          expnsMngrTrnsctnMpprIntermediate.getExpensedTime()
      );
      double referenceAmount = expnsMngrTrnsctnMpprIntermediate.getReferenceAmount();
      if (referenceAmount > 0) {
        expenseManagerTransaction.setReferenceAmount(referenceAmount);
      }
      expenseManagerTransactions.add(expenseManagerTransaction);
    });
    return expenseManagerTransactions;
  }

  /**
   * Adds a new {@link ExpenseManagerTransaction} to the data source. Returns if this data source changes as a result
   * .
   * @param id Identifier of the expense report
   * @param amount expensed amount
   * @param category category
   * @param subcategory sub-category
   * @param paymentMethod payment method used to pay
   * @param description description of expense
   * @param expensedTime time that the expense occurred
   * @param referenceAmount receipt number or reference amount in alternate data source
   * @return the number of entries created from this operation
   * @return true if this data source changes. Otherwise, false.
   */
  public boolean add (int id, double amount, String category, String subcategory, String paymentMethod,
                      String description, Instant expensedTime, double referenceAmount)
      throws IOException, SQLException {
    if (id <= 0) {
      LOGGER.atWarn().log("id is non positive:{}", id);
      throw new IllegalArgumentException("ID cannot be non positive.");
    }
    if (StringUtils.isBlank(category)) {
      LOGGER.atWarn().log("category is null/ blank:{}",
          StringUtils.defaultString(category, AppStringConstant.NULL.value()));
      throw new IllegalArgumentException("Category cannot be null or empty.");
    }
    if (StringUtils.isBlank(subcategory)) {
      LOGGER.atWarn().log("subcategory is null/ blank:{}",
          StringUtils.defaultString(subcategory, AppStringConstant.NULL.value()));
      throw new IllegalArgumentException("Subcategory cannot be null or empty.");
    }
    if (StringUtils.isBlank(paymentMethod)){
      LOGGER.atWarn().log("paymentMethod is null/ blank:{}",
          StringUtils.defaultString(paymentMethod, AppStringConstant.NULL.value()));
      throw new IllegalArgumentException("Payment Method cannot be null or empty.");
    }
    if (StringUtils.isBlank(description)) {
      LOGGER.atWarn().log("description is null/ blank:{}",
          StringUtils.defaultString(description, AppStringConstant.NULL.value()));
      throw new IllegalArgumentException("Description cannot be null or empty.");
    }
    Instant currentInstant = Instant.now();
    if (expensedTime == null || expensedTime.isAfter(currentInstant)) {
      LOGGER.atWarn().log("expensedTime is null/ in future time:{}",
          StringUtils.defaultString(expensedTime.toString(), AppStringConstant.NULL.value()));
      throw new IllegalArgumentException("Expensed time cannot be null or in future.");
    }
    if (referenceAmount < 0) {
      LOGGER.atWarn().log("referenceAmount is negative:{}", referenceAmount);
      throw new IllegalArgumentException("Reference amount cannot be negative.");
    }
    if (expenseManagerTransactionMapper == null) {
      connectToDatabase();
    }
    int numberOfInsertedEntry;
    try (Connection connection = databaseConnectable.connect()) {
      SqlSessionFactory sqlSessionFactory = databaseSessionFactoryBuilder.buildSessionFactory(environmentId);
      try (SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.SIMPLE, connection)) {
        expenseManagerTransactionMapper = sqlSession.getMapper(ExpenseManagerTransactionMapper.class);
        numberOfInsertedEntry = expenseManagerTransactionMapper.addExpenseManagerTransaction(id, amount, category,
            subcategory, paymentMethod, description, expensedTime, referenceAmount);
      }
    }
    return (numberOfInsertedEntry == 1);
  }

  @Override
  public boolean add(ExpenseManagerTransaction expenseManagerTransaction) throws IOException, SQLException {
    long id = expenseManagerTransaction.getId();
    int idInt = (int) id;
    Double amount = expenseManagerTransaction.getAmount();
    ExpenseCategory expenseCategory = expenseManagerTransaction.getCategory();
    String  expenseCategoryString = expenseCategory.value();
    ExpenseSubCategory expenseSubCategory = expenseManagerTransaction.getSubcategory();
    String expenseSubCategoryString = expenseSubCategory.value();
    PaymentMethod paymentMethod = expenseManagerTransaction.getPaymentMethod();
    String paymentMethodString = paymentMethod.value();
    String description = expenseManagerTransaction.getDescription();
    Instant expensedTime = expenseManagerTransaction.getExpendedTime();
    Double referenceAmount = expenseManagerTransaction.getReferenceAmount();
    return add(idInt, amount, expenseCategoryString, expenseSubCategoryString, paymentMethodString, description,
        expensedTime, referenceAmount);
  }



  @Override
  public boolean clear() throws IOException, SQLException {
    try (Connection connection = databaseConnectable.connect()) {
      SqlSessionFactory sqlSessionFactory = databaseSessionFactoryBuilder.buildSessionFactory(environmentId);
      try (SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.SIMPLE, connection)) {
        expenseManagerTransactionMapper = sqlSession.getMapper(ExpenseManagerTransactionMapper.class);
        return expenseManagerTransactionMapper.deleteAllExpenseManagerTransactions();
      }
    }
  }

  private void connectToDatabase() throws IOException, SQLException {
    try (Connection connection = databaseConnectable.connect()) {
      SqlSessionFactory sqlSessionFactory = databaseSessionFactoryBuilder.buildSessionFactory(environmentId);
      try (SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.SIMPLE, connection)) {
        expenseManagerTransactionMapper = sqlSession.getMapper(ExpenseManagerTransactionMapper.class);
      }
    }
  }
}
