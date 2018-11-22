package expense_tally.util;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class TemporalUtil {
    public static LocalDateTime atEndOfDay(LocalDate date) {
        final int POSITIVE_DAY_OFFSET = 1;
        final int SMALLEST_TEMPORAL_DIFFERENCE = 1;
        return date.atStartOfDay()
                   .plusDays(POSITIVE_DAY_OFFSET)
                   .minusNanos(SMALLEST_TEMPORAL_DIFFERENCE);
    }
}
