package josch.presentation.gui.model.validator;

import java.time.LocalDate;

/**
 * This {@code InputValidator} validates user input when necessary. It is designed as a static
 * class, i.e. has a private constructor and only static methods.
 *
 * @author Kai Dauberschmidt
 */
public class InputValidator {

  /** Do not allow instances of this {@code InputValidator}. */
  private InputValidator() {}

  /**
   * Validates a date range and returns {@code true} if the range is valid, {@code false} else. Note
   * that the structure if-else if-else is required to avoid {@code NullPointerExceptions} and
   * cannot be simplified to assign the conditions to a boolean.
   *
   * @param min The minimal inclusive date.
   * @param max The maximal inclusive date.
   * @return {@code true} if min <= max or either is {@code null}, {@code false} else.
   */
  public static boolean validateRange(LocalDate min, LocalDate max) {

    // Check for min first:
    if (min == null) {

      // Select everything.
      if (max == null) {
        return true;

        // Check for legal max date.
      } else {
        return max.isAfter(LocalDate.of(1970, 6, 1));
      }

      // No upper border but an existing lower border: Check for legal min date.
    } else if (max == null) {
      return min.isBefore(LocalDate.now());

      // Both borders are the same: Select one date.
    } else if (min.isEqual(max)) {
      return true;

    } else {
      return min.isBefore(max);
    }
  }
}
