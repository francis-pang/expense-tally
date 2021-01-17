package expense_tally.expense_manager.persistence.database;

import expense_tally.AppStringConstant;
import expense_tally.exception.StringResolver;
import expense_tally.expense_manager.persistence.ExpenseReadable;
import expense_tally.expense_manager.persistence.ExpenseUpdatable;
import expense_tally.expense_manager.persistence.database.mapper.ExpenseManagerTransactionMapper;
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
import java.util.Objects;

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

  /**
   * Default class constructor
   * @param databaseConnectable Container for database connection object
   * @param databaseSessionFactoryBuilder Container for database session object
   * @param environmentId Identifier of the database configuration setting specific in the database configuration
   * @throws IllegalArgumentException if any of the parameter is null, or <i>environmentId</i> contains only blank space
   */
  public ExpenseManagerTransactionDatabaseProxy(DatabaseConnectable databaseConnectable,
                                                DatabaseSessionFactoryBuilder databaseSessionFactoryBuilder,
                                                String environmentId) {
    this.databaseConnectable = Objects.requireNonNull(databaseConnectable);
    this.databaseSessionFactoryBuilder = Objects.requireNonNull(databaseSessionFactoryBuilder);
    if (StringUtils.isBlank(environmentId)) {
      LOGGER.atWarn()
          .log("environmentId is null or empty: {}", StringResolver.resolveNullableString(environmentId));
      throw new IllegalArgumentException("Environment ID cannot be null or empty");
    }
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
   * {@link expense_tally.expense_manager.persistence.database.mapper.ExpenseManagerTransactionMapper.ExpnsMngrTrnsctnMpprIntermediate}
   * into a list of {@link ExpenseManagerTransaction}
   * @param expnsMngrTrnsctnMpprIntermediates ExpnsMngrTrnsctnMpprIntermediate to be converted
   * @return a list of converted {@link ExpenseManagerTransaction}
   */
  private List<ExpenseManagerTransaction> convert(
      List<ExpenseManagerTransactionMapper.ExpnsMngrTrnsctnMpprIntermediate> expnsMngrTrnsctnMpprIntermediates) {
    List<ExpenseManagerTransaction> expenseManagerTransactions = new ArrayList<>();
    expnsMngrTrnsctnMpprIntermediates.forEach(expnsMngrTrnsctnMpprIntermediate -> {
      ExpenseManagerTransaction expenseManagerTransaction = null;
      try {
        expenseManagerTransaction = convert(expnsMngrTrnsctnMpprIntermediate);
      } catch (RuntimeException runtimeException) {
        LOGGER
            .atWarn()
            .withThrowable(runtimeException)
            .log("Unable to convert expnsMngrTrnsctnMpprIntermediate: {}", expnsMngrTrnsctnMpprIntermediate);
      }
      if (expenseManagerTransaction != null) {
        expenseManagerTransactions.add(expenseManagerTransaction);
      }
    });
    return expenseManagerTransactions;
  }

  private ExpenseManagerTransaction convert(
      ExpenseManagerTransactionMapper.ExpnsMngrTrnsctnMpprIntermediate expnsMngrTrnsctnMpprIntermediate) {
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
    return expenseManagerTransaction;
  }

  @Override
  public boolean add(ExpenseManagerTransaction expenseManagerTransaction) throws IOException, SQLException {
    //TODO: Consider having a validator class
    long id = expenseManagerTransaction.getId();
    int idInt = (int) id;
    if (idInt <= 0) {
      LOGGER.atWarn().log("id is non positive:{}", id);
      throw new IllegalArgumentException("ID cannot be non positive.");
    }
    Double amount = expenseManagerTransaction.getAmount();
    ExpenseCategory expenseCategory = expenseManagerTransaction.getCategory();
    if (expenseCategory == null) {
      LOGGER.atWarn().log("category is null");
      throw new IllegalArgumentException("Category cannot be null.");
    }
    String expenseCategoryString = expenseCategory.value();
    if (StringUtils.isBlank(expenseCategoryString)) {
      LOGGER.atWarn()
          .log("category is null/ blank:{}", StringResolver.resolveNullableString(expenseCategoryString));
      throw new IllegalArgumentException("Category cannot be null or empty.");
    }
    ExpenseSubCategory expenseSubCategory = expenseManagerTransaction.getSubcategory();
    if (expenseSubCategory == null) {
      LOGGER.atWarn().log("subcategory is null.");
      throw new IllegalArgumentException("Subcategory cannot be null.");
    }
    String expenseSubCategoryString = expenseSubCategory.value();
    if (StringUtils.isBlank(expenseSubCategoryString)) {
      LOGGER.atWarn()
          .log("subcategory is null/ blank:{}", StringResolver.resolveNullableString(expenseSubCategoryString));
      throw new IllegalArgumentException("Subcategory cannot be null or empty.");
    }
    PaymentMethod paymentMethod = expenseManagerTransaction.getPaymentMethod();
    if (paymentMethod == null) {
      LOGGER.atWarn().log("paymentMethod is null");
      throw new IllegalArgumentException("Payment Method cannot be null.");
    }
    String paymentMethodString = paymentMethod.value();
    if (StringUtils.isBlank(paymentMethodString)) {
      LOGGER.atWarn()
          .log("paymentMethod is null/ blank:{}", StringResolver.resolveNullableString(paymentMethodString));
      throw new IllegalArgumentException("Payment Method cannot be null or empty.");
    }
    String description = expenseManagerTransaction.getDescription();
    if (StringUtils.isBlank(description)) {
      LOGGER.atWarn().log("description is null/ blank:{}",
          StringUtils.defaultString(description, AppStringConstant.NULL.value()));
      throw new IllegalArgumentException("Description cannot be null or empty.");
    }
    Instant expensedTime = expenseManagerTransaction.getExpendedTime();
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
    try (Connection connection = databaseConnectable.connect()) {
      SqlSessionFactory sqlSessionFactory = databaseSessionFactoryBuilder.buildSessionFactory(environmentId);
      try (SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.SIMPLE, connection)) {
        expenseManagerTransactionMapper = sqlSession.getMapper(ExpenseManagerTransactionMapper.class);
        numberOfInsertedEntry = expenseManagerTransactionMapper.addExpenseManagerTransaction(idInt, amount,
            expenseCategoryString, expenseSubCategoryString, paymentMethodString, description, expensedTime,
            referenceAmount);
      }
    }
    return (numberOfInsertedEntry == 1);
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

}
