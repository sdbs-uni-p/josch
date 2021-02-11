package josch.presentation.gui.model.validator;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

/** This class is used to test the input validation. */
public class InputValidatorTest {

    final LocalDate birthday = LocalDate.of(1989,11, 8);
    final LocalDate now = LocalDate.now();

    /** checks whether date range validation works with min < max */
    @Test
    public void validateRangeDate_minLtMax() {
        Assertions.assertTrue(InputValidator.validateRange(birthday, now));
    }

    /** checks whether date range validation works with min = max */
    @Test
    public void validateRangeDate_minEqMax() {
        Assertions.assertTrue(InputValidator.validateRange(now, now));
    }

    /** checks whether date range validation works with min = null */
    @Test
    public void validateRangeDate_minIsNull() {
        Assertions.assertTrue(InputValidator.validateRange(null, now));
    }

    /** checks whether date range validation works with max = null */
    @Test
    public void validateRangeDate_maxIsNull() {
        Assertions.assertTrue(InputValidator.validateRange(birthday, null));
    }

    /** checks whether date range validation works with min and max = null */
    @Test
    public void validateRangeDate_isNull() {
        Assertions.assertTrue(InputValidator.validateRange(null, null));
    }

    /** checks whether date range validation works with min and max = null */
    @Test
    public void validateRangeDate_minGtMax() {
        Assertions.assertFalse(InputValidator.validateRange(now, birthday));
    }
}
