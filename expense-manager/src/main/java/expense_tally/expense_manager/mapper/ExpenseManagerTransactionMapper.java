package expense_tally.expense_manager.mapper;

import expense_tally.model.persistence.transformation.ExpenseManagerTransaction;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.type.JdbcType;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

/**
 * This interface stores all the functionality to interact {@link ExpenseManagerTransaction} with a specific data
 * source.
 */
@Mapper
public interface ExpenseManagerTransactionMapper {
  /**
   * Retrieve all expense manager transactions from the data source
   *
   * @return all expense manager transactions from the data source
   * @see {@link ExpenseManagerTransaction}
   */
  @Results(
      id = "allExpenseManagerTransactions",
      value = {
          @Result(property = "id", column = "id", javaType = Long.class, id = true, jdbcType = JdbcType.INTEGER),
          @Result(property = "amount", column = "amount", javaType = Double.class, jdbcType = JdbcType.DECIMAL),
          @Result(property = "category", column = "category", javaType = String.class, jdbcType = JdbcType.VARCHAR),
          @Result(property = "subcategory", column = "subcategory", javaType = String.class, jdbcType =
              JdbcType.VARCHAR),
          @Result(property = "paymentMethod", column = "payment_method", javaType = String.class,
              jdbcType = JdbcType.VARCHAR),
          @Result(property = "description", column = "description", javaType = String.class, jdbcType =
              JdbcType.VARCHAR),
          @Result(property = "expensedTime", column = "expensed", javaType = Instant.class, jdbcType =
              JdbcType.TIMESTAMP),
          @Result(property = "referenceAmount", column = "reference_amount", javaType = Double.class,
              jdbcType = JdbcType.DECIMAL)
      })
  @Select(value = "SELECT * FROM expense_manager")
  List<ExpnsMngrTrnsctnMpprIntermediate> getAllExpenseManagerTransactions();

  /**
   * Remove all {@link ExpenseManagerTransaction} entries in the data source
   * @return true if the operation succeeds. Otherwise, false.
   */
  @Delete(value = "DELETE FROM expense_manager")
  boolean deleteAllExpenseManagerTransactions();

  /**
   * Create a new expense report to be stored in the data source.
   * @param id Identifier of the expense report
   * @param amount expensed amount
   * @param category category
   * @param subcategory sub-category
   * @param paymentMethod payment method used to pay
   * @param description description of expense
   * @param expensedTime time that the expense occurred
   * @param referenceAmount receipt number or reference amount in alternate data source
   * @return the number of entries created from this operation
   */
  @Insert(value = "INSERT INTO expense_manager(id, amount, category, subcategory, payment_method, description," +
      "expensed_time, reference_amount) VALUES (#{id}, #{amount}, #{category},#{subcategory}, #{payment_method}, " +
      "#{description}, #{expensed_time}, #{reference_amount})")
  int addExpenseManagerTransaction(
      @Param(value = "id") int id,
      @Param(value = "amount") double amount,
      @Param(value = "category") String category,
      @Param(value = "subcategory") String subcategory,
      @Param(value = "payment_method") String paymentMethod,
      @Param(value = "description") String description,
      @Param(value = "expensed_time") Instant expensedTime,
      @Param(value = "reference_amount") double referenceAmount
  );

  class ExpnsMngrTrnsctnMpprIntermediate {
    private long id;
    private double amount;
    private String category;
    private String subcategory;
    private String paymentMethod;
    private String description;
    private Instant expensedTime;
    private Double referenceAmount;

    public long getId() {
      return id;
    }

    public double getAmount() {
      return amount;
    }

    public String getCategory() {
      return category;
    }

    public String getSubcategory() {
      return subcategory;
    }

    public String getPaymentMethod() {
      return paymentMethod;
    }

    public String getDescription() {
      return description;
    }

    public Instant getExpensedTime() {
      return expensedTime;
    }

    public Double getReferenceAmount() {
      return referenceAmount;
    }
  }
}
