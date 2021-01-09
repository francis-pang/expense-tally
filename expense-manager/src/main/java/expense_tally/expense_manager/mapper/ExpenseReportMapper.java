package expense_tally.expense_manager.mapper;

import expense_tally.model.persistence.database.ExpenseReport;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.type.JdbcType;

import java.util.List;

/**
 * This interface stores all the functionality to interact with a specific data source.
 */
@Mapper
public interface ExpenseReportMapper {
  /**
   * Retrieve all expense reports from the data source
   *
   * @return all expense reports from the data source
   * @see {@link ExpenseReport}
   */
  @Results(
      id = "allExpenseReports",
      value = {
          @Result(property = "id", column = "_id", javaType = Integer.class, id = true, jdbcType = JdbcType.INTEGER),
          @Result(property = "account", column = "account", javaType = String.class, jdbcType = JdbcType.VARCHAR),
          @Result(property = "amount", column = "amount", javaType = String.class, jdbcType = JdbcType.VARCHAR),
          @Result(property = "category", column = "category", javaType = String.class, jdbcType = JdbcType.VARCHAR),
          @Result(property = "subcategory", column = "subcategory", javaType = String.class, jdbcType = JdbcType.VARCHAR),
          @Result(property = "paymentMethod", column = "payment_method", javaType = String.class,
              jdbcType = JdbcType.VARCHAR),
          @Result(property = "description", column = "description", javaType = String.class, jdbcType = JdbcType.VARCHAR),
          @Result(property = "expensedTime", column = "expensed", javaType = Long.class, jdbcType = JdbcType.BIGINT),
          @Result(property = "modificationTime", column = "modified", javaType = Long.class, jdbcType = JdbcType.BIGINT),
          @Result(property = "referenceNumber", column = "reference_number", javaType = String.class,
              jdbcType = JdbcType.VARCHAR),
          @Result(property = "status", column = "status", javaType = String.class, jdbcType = JdbcType.VARCHAR),
          @Result(property = "property1", column = "property", javaType = String.class, jdbcType = JdbcType.VARCHAR),
          @Result(property = "property2", column = "property2", javaType = String.class, jdbcType = JdbcType.VARCHAR),
          @Result(property = "property3", column = "property3", javaType = String.class, jdbcType = JdbcType.VARCHAR),
          @Result(property = "property4", column = "property4", javaType = String.class, jdbcType = JdbcType.VARCHAR),
          @Result(property = "property5", column = "property5", javaType = String.class, jdbcType = JdbcType.VARCHAR),
          @Result(property = "tax", column = "tax", javaType = String.class, jdbcType = JdbcType.VARCHAR),
          @Result(property = "expenseTag", column = "expense_tag", javaType = String.class, jdbcType = JdbcType.VARCHAR),
      })
  @Select(value = "SELECT * FROM expense_report")
  List<ExpenseReport> getAllExpenseReports();
}