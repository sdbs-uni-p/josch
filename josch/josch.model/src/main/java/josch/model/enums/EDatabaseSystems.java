package josch.model.enums;

/**
 * This enum lists all database systems. These do have a concrete name stored as a string.
 *
 * @author Kai Dauberschmidt
 */
public enum EDatabaseSystems {

    /**  MongoDB */
    MONGO("MongoDB");

    /**
     * The concrete name of the containment tool
     */
    private final String NAME;

    /**
     * Constructs a containment tool with a given name.
     */
    EDatabaseSystems(String name) {
        this.NAME = name;
    }

    /**
     * Gets the correct tool with a given name.
     */
    public static EDatabaseSystems getSystem(String name) {
        //noinspection SwitchStatementWithTooFewBranches
        return switch (name) {
            case "MongoDB" -> MONGO;
            default -> throw new IllegalArgumentException("DBMS does not exist.");
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
