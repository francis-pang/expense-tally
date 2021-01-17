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
    ExpenseReport expenseReport = new ExpenseReport();
    softAssertions.assertThat(expenseReport.getAmount()).isNull();
    softAssertions.assertThat(expenseReport.getCategory()).isNull();
    softAssertions.assertThat(expenseReport.getSubcategory()).isNull();
    softAssertions.assertThat(expenseReport.getPaymentMethod()).isNull();
    softAssertions.assertThat(expenseReport.getDescription()).isNull();
    softAssertions.assertThat(expenseReport.getExpensedTime()).isZero();
    softAssertions.assertThat(expenseReport.getReferenceNumber()).isNull();
  }

  @Test
  void equals_sameItem() {
    ExpenseReport expenseReport = new ExpenseReport();
    assertThat(expenseReport.equals(expenseReport))
        .isTrue();
  }

  @Test
  void equals_null() {
    ExpenseReport expenseReport = new ExpenseReport();
    assertThat(expenseReport.equals(null))
        .isFalse();
  }

  @Test
  void equals_notSameInstanceType() {
    ExpenseReport expenseReport = new ExpenseReport();
    assertThat(expenseReport.equals(1))
        .isFalse();
  }

  @Test
  void equals_differentId() {
    ExpenseReport testExpenseReport = new ExpenseReport();
    ExpenseReport expectedExpenseReport = new ExpenseReport();
    testExpenseReport.setId(1);
    assertThat(testExpenseReport.equals(expectedExpenseReport))
        .isFalse();
  }

  @Test
  void equals_differentExpensedTime() {
    ExpenseReport testExpenseReport = new ExpenseReport();
    ExpenseReport expectedExpenseReport = new ExpenseReport();
    testExpenseReport.setExpensedTime(1000000);
    assertThat(testExpenseReport.equals(expectedExpenseReport))
        .isFalse();
  }

  @Test
  void equals_differentModificationTime() {
    ExpenseReport testExpenseReport = new ExpenseReport();
    ExpenseReport expectedExpenseReport = new ExpenseReport();
    testExpenseReport.setModificationTime(1000000);
    assertThat(testExpenseReport.equals(expectedExpenseReport))
        .isFalse();
  }

  @Test
  void equals_differentAccount() {
    ExpenseReport testExpenseReport = new ExpenseReport();
    ExpenseReport expectedExpenseReport = new ExpenseReport();
    testExpenseReport.setAccount(" account");
    assertThat(testExpenseReport.equals(expectedExpenseReport))
        .isFalse();
  }

  @Test
  void equals_differentAmount() {
    ExpenseReport testExpenseReport = new ExpenseReport();
    ExpenseReport expectedExpenseReport = new ExpenseReport();
    testExpenseReport.setAmount(" amount");
    assertThat(testExpenseReport.equals(expectedExpenseReport))
        .isFalse();
  }

  @Test
  void equals_differentCategory() {
    ExpenseReport testExpenseReport = new ExpenseReport();
    ExpenseReport expectedExpenseReport = new ExpenseReport();
    testExpenseReport.setCategory(" category");
    assertThat(testExpenseReport.equals(expectedExpenseReport))
        .isFalse();
  }

  @Test
  void equals_differentSubcategory() {
    ExpenseReport testExpenseReport = new ExpenseReport();
    ExpenseReport expectedExpenseReport = new ExpenseReport();
    testExpenseReport.setSubcategory(" subcategory");
    assertThat(testExpenseReport.equals(expectedExpenseReport))
        .isFalse();
  }

  @Test
  void equals_differentPaymentMethod() {
    ExpenseReport testExpenseReport = new ExpenseReport();
    ExpenseReport excpectedExpenseReport = new ExpenseReport();
    testExpenseReport.setPaymentMethod(" payment method");
    assertThat(testExpenseReport.equals(excpectedExpenseReport))
        .isFalse();
  }

  @Test
  void equals_differentDescription() {
    ExpenseReport testExpenseReport = new ExpenseReport();
    ExpenseReport expectedExpenseReport = new ExpenseReport();
    testExpenseReport.setDescription(" description");
    assertThat(testExpenseReport.equals(expectedExpenseReport))
        .isFalse();
  }

  @Test
  void equals_differentReferenceNumber() {
    ExpenseReport testExpenseReport = new ExpenseReport();
    ExpenseReport expectedExpenseReport = new ExpenseReport();
    testExpenseReport.setReferenceNumber(" referenceNumber");
    assertThat(testExpenseReport.equals(expectedExpenseReport))
        .isFalse();
  }

  @Test
  void equals_differentStatus() {
    ExpenseReport testExpenseReport = new ExpenseReport();
    ExpenseReport expectedExpenseReport = new ExpenseReport();
    testExpenseReport.setStatus(" status");
    assertThat(testExpenseReport.equals(expectedExpenseReport))
        .isFalse();
  }

  @Test
  void equals_differentProperty1() {
    ExpenseReport testExpenseReport = new ExpenseReport();
    ExpenseReport expectedExpenseReport = new ExpenseReport();
    testExpenseReport.setProperty1(" property 1");
    assertThat(testExpenseReport.equals(expectedExpenseReport))
        .isFalse();
  }

  @Test
  void equals_differentProperty2() {
    ExpenseReport testExpenseReport = new ExpenseReport();
    ExpenseReport expectedExpenseReport = new ExpenseReport();
    testExpenseReport.setProperty2(" property 2");
    assertThat(testExpenseReport.equals(expectedExpenseReport))
        .isFalse();
  }

  @Test
  void equals_differentProperty3() {
    ExpenseReport testExpenseReport = new ExpenseReport();
    ExpenseReport expectedExpenseReport = new ExpenseReport();
    testExpenseReport.setProperty3(" property 3");
    assertThat(testExpenseReport.equals(expectedExpenseReport))
        .isFalse();
  }

  @Test
  void equals_differentProperty4() {
    ExpenseReport testExpenseReport = new ExpenseReport();
    ExpenseReport expectedExpenseReport = new ExpenseReport();
    testExpenseReport.setProperty4(" property 4");
    assertThat(testExpenseReport.equals(expectedExpenseReport))
        .isFalse();
  }

  @Test
  void equals_differentProperty5() {
    ExpenseReport testExpenseReport = new ExpenseReport();
    ExpenseReport expectedExpenseReport = new ExpenseReport();
    testExpenseReport.setProperty5(" property 5");
    assertThat(testExpenseReport.equals(expectedExpenseReport))
        .isFalse();
  }

  @Test
  void equals_differentTax() {
    ExpenseReport testExpenseReport = new ExpenseReport();
    ExpenseReport expectedExpenseReport = new ExpenseReport();
    testExpenseReport.setTax(" tax");
    assertThat(testExpenseReport.equals(expectedExpenseReport))
        .isFalse();
  }

  @Test
  void equals_noExpenseTag() {
    ExpenseReport testExpenseReport = new ExpenseReport();
    ExpenseReport expectedExpenseReport = new ExpenseReport();
    expectedExpenseReport.setExpenseTag(" expense tag");
    assertThat(testExpenseReport.equals(expectedExpenseReport))
        .isFalse();
  }

  @Test
  void equals_differentExpenseTag() {
    ExpenseReport testExpenseReport = new ExpenseReport();
    testExpenseReport.setExpenseTag("");
    ExpenseReport expectedExpenseReport = new ExpenseReport();
    expectedExpenseReport.setExpenseTag(" expense tag");
    assertThat(testExpenseReport.equals(expectedExpenseReport))
        .isFalse();
  }

  @Test
  void equals_sameObject() {
    ExpenseReport expenseReport = new ExpenseReport();
    expenseReport.setId(7669);
    expenseReport.setAccount("2020");
    expenseReport.setAmount("1");
    expenseReport.setCategory("Food");
    expenseReport.setSubcategory("Food court/ Fast food");
    expenseReport.setPaymentMethod("Electronic Transfer");
    expenseReport.setExpenseTag("2011-11-08");
    expenseReport.setDescription("Lunch. Illy. Suvai restaurant. PayNow.");
    expenseReport.setExpensedTime(Long.valueOf("1597554900000"));
    expenseReport.setModificationTime(Long.valueOf("1599614178334"));
    expenseReport.setStatus("Cleared");
    expenseReport.setProperty4("1 PCS");
    expenseReport.setExpenseTag("No tax");

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
    assertThat(expenseReport.equals(expectedExpenseReport))
        .isTrue();
  }

  @Test
  void hashCode_pass() {
    ExpenseReport expenseReport = new ExpenseReport();
    assertThat(expenseReport.hashCode())
        .isNotZero()
        .isEqualTo(new ExpenseReport().hashCode());
  }

  @Test
  void toString_pass() {
    ExpenseReport expenseReport = new ExpenseReport();
    expenseReport.setId(7669);
    expenseReport.setAccount("2020");
    expenseReport.setAmount("1");
    expenseReport.setCategory("Food");
    expenseReport.setSubcategory("Food court/ Fast food");
    expenseReport.setPaymentMethod("Electronic Transfer");
    expenseReport.setExpenseTag("2011-11-08");
    expenseReport.setDescription("Lunch. Illy. Suvai restaurant. PayNow.");
    expenseReport.setExpensedTime(Long.valueOf("1597554900000"));
    expenseReport.setModificationTime(Long.valueOf("1599614178334"));
    expenseReport.setStatus("Cleared");
    expenseReport.setProperty4("1 PCS");
    expenseReport.setExpenseTag("No tax");
    assertThat(expenseReport.toString())
        .isEqualTo("ExpenseReport[id=7669, account='2020', amount='1', category='Food', subcategory='Food court/ Fast food', paymentMethod='Electronic Transfer', description='Lunch. Illy. Suvai restaurant. PayNow.', expensedTime=1597554900000, modificationTime=1599614178334, referenceNumber='null', status='Cleared', property1='null', property2='null', property3='null', property4='1 PCS', property5='null', tax='null', expenseTag='No tax']");
  }

  @Test
  void getId_pass() {
    ExpenseReport expenseReport = new ExpenseReport();
    expenseReport.setId(7669);
    assertThat(expenseReport.getId())
        .isEqualTo(7669);
  }
}