package expense_tally.model.persistence.database;

import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SoftAssertionsExtension.class)
class ExpenseReportTest {

  @Test
  void getTest(SoftAssertions softAssertions) {
    ExpenseReport testExpenseReport = new ExpenseReport();
    softAssertions.assertThat(testExpenseReport.getAmount()).isNull();
    softAssertions.assertThat(testExpenseReport.getCategory()).isNull();
    softAssertions.assertThat(testExpenseReport.getSubcategory()).isNull();
    softAssertions.assertThat(testExpenseReport.getPaymentMethod()).isNull();
    softAssertions.assertThat(testExpenseReport.getDescription()).isNull();
    softAssertions.assertThat(testExpenseReport.getExpensedTime()).isZero();
    softAssertions.assertThat(testExpenseReport.getReferenceNumber()).isNull();
  }

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
  void testEquals_sameObject() {
    ExpenseReport testExpenseReport = new ExpenseReport();
    testExpenseReport.setId(7669);
    testExpenseReport.setAccount("2020");
    testExpenseReport.setAmount("1");
    testExpenseReport.setCategory("Food");
    testExpenseReport.setSubcategory("Food court/ Fast food");
    testExpenseReport.setPaymentMethod("Electronic Transfer");
    testExpenseReport.setExpenseTag("2011-11-08");
    testExpenseReport.setDescription("Lunch. Illy. Suvai restaurant. PayNow.");
    testExpenseReport.setExpensedTime(Long.valueOf("1597554900000"));
    testExpenseReport.setModificationTime(Long.valueOf("1599614178334"));
    testExpenseReport.setStatus("Cleared");
    testExpenseReport.setProperty4("1 PCS");
    testExpenseReport.setExpenseTag("No tax");

    ExpenseReport expectedExpenseReport = new ExpenseReport();
    expectedExpenseReport.setId(7669);
    expectedExpenseReport.setAccount("2020");
    expectedExpenseReport.setAmount("1");
    expectedExpenseReport.setCategory("Food");
    expectedExpenseReport.setSubcategory("Food court/ Fast food");
    expectedExpenseReport.setPaymentMethod("Electronic Transfer");
    expectedExpenseReport.setExpenseTag("2011-11-08");
    expectedExpenseReport.setDescription("Lunch. Illy. Suvai restaurant. PayNow.");
    expectedExpenseReport.setExpensedTime(Long.valueOf("1597554900000"));
    expectedExpenseReport.setModificationTime(Long.valueOf("1599614178334"));
    expectedExpenseReport.setStatus("Cleared");
    expectedExpenseReport.setProperty4("1 PCS");
    expectedExpenseReport.setExpenseTag("No tax");
    assertThat(testExpenseReport.equals(expectedExpenseReport))
        .isTrue();
  }

  @Test
  void testHashCode() {
    ExpenseReport testExpenseReport = new ExpenseReport();
    assertThat(testExpenseReport.hashCode())
        .isNotZero()
        .isEqualTo(new ExpenseReport().hashCode());
  }

  @Test
  void testToString() {
    ExpenseReport testExpenseReport = new ExpenseReport();
    testExpenseReport.setId(7669);
    testExpenseReport.setAccount("2020");
    testExpenseReport.setAmount("1");
    testExpenseReport.setCategory("Food");
    testExpenseReport.setSubcategory("Food court/ Fast food");
    testExpenseReport.setPaymentMethod("Electronic Transfer");
    testExpenseReport.setExpenseTag("2011-11-08");
    testExpenseReport.setDescription("Lunch. Illy. Suvai restaurant. PayNow.");
    testExpenseReport.setExpensedTime(Long.valueOf("1597554900000"));
    testExpenseReport.setModificationTime(Long.valueOf("1599614178334"));
    testExpenseReport.setStatus("Cleared");
    testExpenseReport.setProperty4("1 PCS");
    testExpenseReport.setExpenseTag("No tax");
    assertThat(testExpenseReport.toString())
        .isEqualTo("ExpenseReport[id=7669, account='2020', amount='1', category='Food', subcategory='Food court/ Fast food', paymentMethod='Electronic Transfer', description='Lunch. Illy. Suvai restaurant. PayNow.', expensedTime=1597554900000, modificationTime=1599614178334, referenceNumber='null', status='Cleared', property1='null', property2='null', property3='null', property4='1 PCS', property5='null', tax='null', expenseTag='No tax']");
  }
}