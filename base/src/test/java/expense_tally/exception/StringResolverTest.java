package expense_tally.exception;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class StringResolverTest {
  @Test
  void resolveNullableString_nonNullNonEmpty() {
    assertThat(StringResolver.resolveNullableString("waha"))
        .isNotNull()
        .isNotBlank()
        .isEqualTo("waha");
  }

  @Test
  void resolveNullableString_onlyBlankSpace() {
    assertThat(StringResolver.resolveNullableString("   "))
        .isNotNull()
        .isBlank();
  }

  @Test
  void resolveNullableString_null() {
    assertThat(StringResolver.resolveNullableString(null))
        .isNotNull()
        .isNotBlank()
        .isEqualTo("NULL");
  }
}