package josch.presentation.gui.model.fields;

import javafx.scene.control.TextField;

/**
 * This {@code IntegerTextField} class is a custom JavaFX property class. It is used to limit the
 * input of a regular TextField to <em>positive Integers</em>. Some inputs are required to be
 * limited to numbers, like the timeout options or a port.
 *
 * @author Kai Dauberschmidt
 * @see TextField
 */
public class IntegerTextField extends TextField {

  /**
   * Replaces a range of characters with the given numerical text. TextFields are always initialized
   * with an empty string, so null pointer exceptions cannot happen.
   *
   * @param start The starting index in the range exclusive. Must be >= 0 and < end.
   * @param end The ending index in the range exclusive. Must be > start and <= length of text.
   * @param text The text that is to replace the range. Must not be null.
   */
  @Override
  public void replaceText(int start, int end, String text) {
    // text to replace contains only > 0 digits ([0-9]) or is ""
    if (text.matches("\\d*") || text.isEmpty()) {
      super.replaceText(start, end, text);
    }
  }

  /**
   * Replaces the selection with the given replacement string. If there is no selection, then the
   * replacement text is simply inserted at the current caret position. If there was a selection,
   * then the selection is cleared and the given replacement text inserted.
   *
   * @param text The replacement string to replace the selection with. Must not be null.
   */
  @Override
  public void replaceSelection(String text) {
    // text to replace contains only > 0 digits ([0-9]) or is ""
    if (text.matches("\\d*") || text.isEmpty()) {
      super.replaceSelection(text);
    }
  }
}
