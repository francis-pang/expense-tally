package expense_tally.persistence.database;

import expense_tally.persistence.database.ExpenseReport;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ExpenseReportTest {

  @Test
  void testEquals_sameItem() {
    ExpenseReport testExpenseReport = new ExpenseReport();
    assertThat(testExpenseReport.equals(testExpenseReport))
        .isTrue();
  }

  @Test
  void testEquals_null() {
    ExpenseReport testExpenseReport = new ExpenseReport();
    assertThat(testExpenseReport.equals(null))
        .isFalse();
  }

  @Test
  void testEquals_notSameInstanceType() {
    ExpenseReport testExpenseReport = new ExpenseReport();
    assertThat(testExpenseReport.equals(1))
        .isFalse();
  }

  @Test
  void testEquals_differentId() {
    ExpenseReport testExpenseReport = new ExpenseReport();
    ExpenseReport expenseReport = new ExpenseReport();
    expenseReport.setId(1);
    assertThat(testExpenseReport.equals(expenseReport))
        .isFalse();
  }

  @Test
  void testEquals_differentExpensedTime() {
    ExpenseReport testExpenseReport = new ExpenseReport();
    ExpenseReport expenseReport = new ExpenseReport();
    expenseReport.setExpensedTime(1000000);
    assertThat(testExpenseReport.equals(expenseReport))
        .isFalse();
  }

  @Test
  void testEquals_differentModificationTime() {
    ExpenseReport testExpenseReport = new ExpenseReport();
    ExpenseReport expenseReport = new ExpenseReport();
    expenseReport.setModificationTime(1000000);
    assertThat(testExpenseReport.equals(expenseReport))
        .isFalse();
  }

  @Test
  void testEquals_differentAccount() {
    ExpenseReport testExpenseReport = new ExpenseReport();
    ExpenseReport expenseReport = new ExpenseReport();
    expenseReport.setAccount("test account");
    assertThat(testExpenseReport.equals(expenseReport))
        .isFalse();
  }

  @Test
  void testEquals_differentAmount() {
    ExpenseReport testExpenseReport = new ExpenseReport();
    ExpenseReport expenseReport = new ExpenseReport();
    expenseReport.setAmount("test amount");
    assertThat(testExpenseReport.equals(expenseReport))
        .isFalse();
  }

  @Test
  void testEquals_differentCategory() {
    ExpenseReport testExpenseReport = new ExpenseReport();
    ExpenseReport expenseReport = new ExpenseReport();
    expenseReport.setCategory("test category");
    assertThat(testExpenseReport.equals(expenseReport))
        .isFalse();
  }

  @Test
  void testEquals_differentSubcategory() {
    ExpenseReport testExpenseReport = new ExpenseReport();
    ExpenseReport expenseReport = new ExpenseReport();
    expenseReport.setSubcategory("test subcategory");
    assertThat(testExpenseReport.equals(expenseReport))
        .isFalse();
  }

  @Test
  void testEquals_differentPaymentMethod() {
    ExpenseReport testExpenseReport = new ExpenseReport();
    ExpenseReport expenseReport = new ExpenseReport();
    expenseReport.setPaymentMethod("test payment method");
    assertThat(testExpenseReport.equals(expenseReport))
        .isFalse();
  }

  @Test
  void testEquals_differentDescription() {
    ExpenseReport testExpenseReport = new ExpenseReport();
    ExpenseReport expenseReport = new ExpenseReport();
    expenseReport.setDescription("test description");
    assertThat(testExpenseReport.equals(expenseReport))
        .isFalse();
  }

  @Test
  void testEquals_differentReferenceNumber() {
    ExpenseReport testExpenseReport = new ExpenseReport();
    ExpenseReport expenseReport = new ExpenseReport();
    expenseReport.setReferenceNumber("test referenceNumber");
    assertThat(testExpenseReport.equals(expenseReport))
        .isFalse();
  }

  @Test
  void testEquals_differentStatus() {
    ExpenseReport testExpenseReport = new ExpenseReport();
    ExpenseReport expenseReport = new ExpenseReport();
    expenseReport.setStatus("test status");
    assertThat(testExpenseReport.equals(expenseReport))
        .isFalse();
  }

  @Test
  void testEquals_differentProperty1() {
    ExpenseReport testExpenseReport = new ExpenseReport();
    ExpenseReport expenseReport = new ExpenseReport();
    expenseReport.setProperty1("test property 1");
    assertThat(testExpenseReport.equals(expenseReport))
        .isFalse();
  }

  @Test
  void testEquals_differentProperty2() {
    ExpenseReport testExpenseReport = new ExpenseReport();
    ExpenseReport expenseReport = new ExpenseReport();
    expenseReport.setProperty2("test property 2");
    assertThat(testExpenseReport.equals(expenseReport))
        .isFalse();
  }

  @Test
  void testEquals_differentProperty3() {
    ExpenseReport testExpenseReport = new ExpenseReport();
    ExpenseReport expenseReport = new ExpenseReport();
    expenseReport.setProperty3("test property 3");
    assertThat(testExpenseReport.equals(expenseReport))
        .isFalse();
  }

  @Test
  void testEquals_differentProperty4() {
    ExpenseReport testExpenseReport = new ExpenseReport();
    ExpenseReport expenseReport = new ExpenseReport();
    expenseReport.setProperty4("test property 4");
    assertThat(testExpenseReport.equals(expenseReport))
        .isFalse();
  }

  @Test
  void testEquals_differentProperty5() {
    ExpenseReport testExpenseReport = new ExpenseReport();
    ExpenseReport expenseReport = new ExpenseReport();
    expenseReport.setProperty5("test property 5");
    assertThat(testExpenseReport.equals(expenseReport))
        .isFalse();
  }

  @Test
  void testEquals_differentTax() {
    ExpenseReport testExpenseReport = new ExpenseReport();
    ExpenseReport expenseReport = new ExpenseReport();
    expenseReport.setTax("test tax");
    assertThat(testExpenseReport.equals(expenseReport))
        .isFalse();
  }

  @Test
  void testEquals_noExpenseTag() {
    ExpenseReport testExpenseReport = new ExpenseReport();
    ExpenseReport expenseReport = new ExpenseReport();
    expenseReport.setExpenseTag("test expense tag");
    assertThat(testExpenseReport.equals(expenseReport))
        .isFalse();
  }

  @Test
  void testEquals_differentExpenseTag() {
    ExpenseReport testExpenseReport = new ExpenseReport();
    testExpenseReport.setExpenseTag("");
    ExpenseReport expenseReport = new ExpenseReport();
    expenseReport.setExpenseTag("test expense tag");
    assertThat(testExpenseReport.equals(expenseReport))
        .isFalse();
  }


  @Test
  void testHashCode() {
    ExpenseReport testExpenseReport = new ExpenseReport();
    assertThat(testExpenseReport.hashCode())
        .isNotZero()
        .isEqualTo(new ExpenseReport().hashCode());
  }
}