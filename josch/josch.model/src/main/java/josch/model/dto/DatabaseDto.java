package josch.model.dto;

/**
 * The {@code DatabaseDto} class encapsulates the attributes from a database. It is designed by a
 * <i>data transfer object (dto)</i> pattern to pass multiple data in a single object throughout the
 * architectural layers.
 *
 * @author Kai Dauberschmidt
 */
public class DatabaseDto extends AbstractDto {

  /** The name of the database */
  private String name;

  /** The collectionCount of the database */
  private int collectionCount;

  /** Constructs a DatabaseDto with both its attributes given */
  public DatabaseDto(String name, int collectionCount) {
    this.name = name;
    this.collectionCount = collectionCount;
  }

  /** Constructs an empty DatabaseDto */
  public DatabaseDto() {}

  /**
   * Gets the {@code name}
   *
   * @return The value of {@code name}
   */
  public String getName() {
    return name;
  }


  /**
   * Sets the {@code name}.
   *
   * @param name The concrete value of {@code name}.
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * Gets the {@code collectionCount}
   *
   * @return The value of {@code collectionCount}
   */
  public int getCollectionCount() {
    return collectionCount;
  }

  /**
   * Sets the {@code collectionCount}.
   *
   * @param collectionCount The concrete value of {@code collectionCount}.
   */
  public void setCollectionCount(int collectionCount) {
    this.collectionCount = collectionCount;
  }
}
