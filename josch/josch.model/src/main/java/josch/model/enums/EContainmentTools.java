package josch.model.enums;

/**
 * This enum lists all containment tools. These tools do have a concrete name stored as a string.
 *
 * @author Kai Dauberschmidt
 */
public enum EContainmentTools {

  /**
   * The JsonSubSchema tool, alias Stuttgarter Tool, alias Tool A.
   */
  JSS("jsonsubschema"),

  /**
   * The Is Json Schema Subset tool, alias Tool B.
   */
  IJS_SUBSET("is-json-schema-subset");

  /**
   * The concrete name of the containment tool
   */
  private final String NAME;

  /**
   * Constructs a containment tool with a given name.
   */
  EContainmentTools(String name) {
    this.NAME = name;
  }

  /**
   * Gets the correct tool with a given name.
   */
  public static EContainmentTools getTool(String name) {
    return switch (name) {
      case "jsonsubschema" -> JSS;
      case "is-json-schema-subset" -> IJS_SUBSET;
      default -> throw new IllegalArgumentException("Containment tool does not exist.");
    };
  }

  /**
   * Gets the concrete name of the containment tool.
   */
  public String getName() {
    return NAME;
  }

  /**
   * Returns the string representation of the containment tool.
   */
  @Override
  public String toString() {
    return getName();
  }
}
