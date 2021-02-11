package josch.model.dto;

import java.util.LinkedList;
import java.util.List;

/**
 * This {@code DifferenceDto} is used to encapsulate the syntactical differences in schemas which
 * are in return used for syntax highlighting on the frontend. It has two Lists that contain the
 * differences, each representing one side. A list is chosen because the amount of differences can
 * vary.
 *
 * @author Kai Dauberschmidt
 */
public class DifferenceDto {

  /**
   * The list to store the differences on the left side.
   */
  private final List<String> LEFT_DIFFERENCES;

  /**
   * The list to store the differences on the right side.
   */
  private final List<String> RIGHT_DIFFERENCES;

  /**
   * Constructs a DifferenceDTO with two empty lists.
   */
  public DifferenceDto() {
    LEFT_DIFFERENCES = new LinkedList<>();
    RIGHT_DIFFERENCES = new LinkedList<>();
  }

  /**
   * Indicates whether the differences are nonexistent.
   *
   * @return {@code true} when the dto has no differences, {@code false} else.
   */
  public boolean hasNone() {
    return (LEFT_DIFFERENCES.size() == 0) && (RIGHT_DIFFERENCES.size() == 0);
  }

  /**
   * Indicates whether the left differences exists;
   *
   * @return {@code true} when the dto has left differences, {@code false} else.
   */
  public boolean hasLeftDifferences() {
    return !(LEFT_DIFFERENCES.size() == 0);
  }

  /**
   * Indicates whether the right differences exists;
   *
   * @return {@code true} when the dto has right differences, {@code false} else.
   */
  public boolean hasRightDifferences() {
    return !(RIGHT_DIFFERENCES.size() == 0);
  }

  /**
   * Gets the left differences as a list.
   */
  public List<String> getLeftDifferences() {
    return LEFT_DIFFERENCES;
  }

  /**
   * Gets the right differences as a list.
   */
  public List<String> getRightDifferences() {
    return RIGHT_DIFFERENCES;
  }

  /**
   * Adds a left difference.
   */
  public void addLeft(String difference) {
    LEFT_DIFFERENCES.add(difference);
  }

  /**
   * Adds a right difference.
   */
  public void addRight(String difference) {
    RIGHT_DIFFERENCES.add(difference);
  }
}
