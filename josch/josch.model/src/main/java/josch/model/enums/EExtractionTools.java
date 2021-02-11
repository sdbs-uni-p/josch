package josch.model.enums;

/**
 * This enum lists all extraction tools. These tools do have a concrete name stored as a string.
 *
 * @author Kai Dauberschmidt
 */
public enum EExtractionTools {

    /**
     * The Hackolade tool.
     */
    HACK("hackolade"),

    /**
     * The json-schema-inferrer
     */
    JSI("JSON Schema Inferrer");

    /**
     * The concrete name of the containment tool
     */
    private final String NAME;

    /**
     * Constructs a containment tool with a given name.
     */
    EExtractionTools(String name) {
        this.NAME = name;
    }

    /**
     * Gets the correct tool with a given name.
     */
    public static EExtractionTools getTool(String name) {
        return switch (name.toLowerCase()) {
            case "hackolade" -> HACK;
            case "json schema inferrer", "ijs", "json-schema-inferrer" -> JSI;
            default -> throw new IllegalArgumentException("Extraction tool does not exist.");
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