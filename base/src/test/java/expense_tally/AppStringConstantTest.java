package expense_tally;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AppStringConstantTest {
    @Test
    void resolve_value() {
        assertThat(AppStringConstant.resolve("NULL"))
                .isNotNull()
                .isEqualByComparingTo(AppStringConstant.NULL);
    }

    @Test
    void resolve_null() {
        assertThatThrownBy(() -> AppStringConstant.resolve(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("value cannot be null or empty");
    }

    @Test
    void resolve_manyBlankSpaces() {
        assertThatThrownBy(() -> AppStringConstant.resolve("      "))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("value cannot be null or empty");
    }

    @Test
    void resolve_nonEnumString() {
        assertThat(AppStringConstant.resolve("Rubbish"))
                .isNull();
    }

    @Test
    void value() {
        assertThat(AppStringConstant.NULL.value())
                .isNotNull()
                .isEqualTo("NULL");
    }
}