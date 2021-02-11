package josch.model.enums;

/**
 * This enum lists all System constants. The constants have a generic name with a concrete value
 * stored as a String.
 *
 * @author Kai Dauberschmidt
 */
public enum ESystemConstants {
  /**
   * The constant for the entity, i.e. collection for MongoDB, that stores the extracted schemas.
   * TODO: ensure that this entity is not present in the container, i.e. database for MongoDB.
   */
  SCHEMA_STORAGE("joschSchemaStorage"),

  /**
   * The message that occurs when a schema is stored.
   */
  SCHEMA_INSERTED("Schema stored."),

  /**
   * The global error message that is leading every exception message.
   */
  ERROR("Error! "),

  /**
   * The paths to the schema containment files.
   */
  SCHEMA_CURRENT_PATH(System.getProperty("user.home") + "/josch/containment/s1.json"),
  SCHEMA_LEGACY_PATH(System.getProperty("user.home") + "/josch/containment/s2.json"),
  SCHEMA_RESULT_PATH(System.getProperty("user.home") + "/josch/containment/result.json"),
  PATH(System.getProperty("user.home") + "/josch"),

  /**
   * The success message.
   */
  SUCCESS("Success.");

  /**
   * The concrete and constant value for the system constant.
   */
  private final String VALUE;

  /**
   * Constructs a system constant with a concrete value.
   */
  ESystemConstants(String value) {
    this.VALUE = value;
  }

  /**
   * Gets the concrete value of a system constant.
   *
   * @return The concrete {@code VALUE} of the constant.
   */
  public String getValue() {
    return VALUE;
  }
}
