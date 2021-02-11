package josch.model.enums;

/**
 * This enum is used to store the different possible relations between two schemas. Caution:
 * These can only be accessed from Java code and <i>not</i> by the containment scripts themselves.
 * But the values have to be exactly the same so they <i>should</i> be handled with care.
 *
 * @author Kai Dauberschmidt
 */
public enum EContainmentRelations {
    EQV("equivalent"),
    NEQV("incomparable"),
    EQ("equal"),
    NEQ("not equal"),
    SUB("subset"),
    SUP("superset");

    /** The concrete describing value of the relation. */
    private final String VALUE;

    /** Constructs a relation with a string representation. */
    EContainmentRelations(String value) {
        this.VALUE = value;
    }

    /** Returns the string representation of the relation. */
    @Override
    public String toString() {
        return VALUE;
    }

    /** Gets the relation based on its value */
    public static EContainmentRelations get(String value) {
        for (EContainmentRelations relation : EContainmentRelations.values()) {
            if (value.equals(relation.toString())) {
                return relation;
            }
        }
        return null;
    }
}
