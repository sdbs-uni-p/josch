package josch.model.enums;

/**
 * This enum is used to store the view names in a single point across the whole application.
 * New views must be registered here in order to be embedded within the application. Note that
 * its title and navigation rules must be set within the {@code MainController} class and the
 * controllers must be registered within the {@code AbstractControllerFactory} additionally.
 * 
 * @see josch.presentation.gui.controller 
 * @author Kai Dauberschmidt
 */
public enum EViews {
    COLLECTION("Collection"),
    COLLECTION_LIST("CollectionOverview"),
    DATABASE_LIST("DatabaseOverview"),
    MAIN("Main"),
    OPTIONS("Options");
    
    /** The name of the view. */
    private final String NAME;

    /** Constructs a view with a string representation. */
    EViews(String name) {
        this.NAME = name;
    }

    /** Returns the string representation of the view. */
    @Override
    public String toString() {
        return NAME;
    }
    

    /** Gets the view based on its name */
    public static EViews get(String name) {
        for (EViews view : EViews.values()) {
            if (name.equals(view.NAME)) {
                return view;
            }
        }
        throw new IllegalArgumentException("Unknown view. Please check registration of it first.");
    }
}
