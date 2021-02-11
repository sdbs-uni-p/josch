package josch.presentation.gui.controller;

import josch.model.enums.EViews;

/**
 * This factory is used to return a specific controller for a given view.
 *
 * @author Kai Dauberschmidt
 */
public abstract class AbstractControllerFactory {

    /**
     * Gets a controller for a given view.
     * @param view the view as an enum.
     * @return the controller for the view.
     */
    public static AbstractController getController(EViews view) {
        return switch (view) {
            case COLLECTION -> new CollectionViewController();
            case COLLECTION_LIST -> new CollectionOverviewController();
            case DATABASE_LIST -> new DatabaseOverviewController();
            case MAIN -> MainController.getInstance();
            case OPTIONS -> new OptionsController();
        };
    }
}
