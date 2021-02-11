package josch.presentation.gui.controller;

import javafx.application.HostServices;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Region;
import javafx.scene.text.Text;
import josch.model.dto.SettingsDto;
import josch.model.enums.EViews;
import josch.presentation.gui.App;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

/**
 * This {@code MainController} represents the implementation of the composite part of the composite
 * pattern within the Controller. The component itself is the AbstractController where the leaves
 * are the actual view controllers, e.g. OptionsController. <br/>
 * This class as a nested component defines the values that all leaves and child composites have in
 * common, i.e. the settings of the application. It furthermore defines the setting and getting of
 * The main parts that the controllers have in common, i.e. the back button, the caption and the
 * content area. <br/>
 * Since there should be only one main controller present at any given time and since controllers
 * need to be instanced this class is furthermore designed by a singleton pattern. Note that this
 * prohibits the inheritance. <br />
 * The methods are structured as follows:
 * <ul>
 *     <li> FXML methods, i.e. the back() method.
 *     <li> public methods except for getters and setters.
 *     <li> private methods except for getters and setters.
 *     <li> getters and setters in alphabetical order of their attributes.
 * </ul>
 *
 * @author Kai Dauberschmidt
 */
public class MainController extends AbstractController {

    /** The system's settings */
    private SettingsDto settings;

    /** The instance of the main controller */
    private static MainController instance;

    /** A string representation of the current child node. */
    private EViews newNode;

    private static HostServices hostServices;

    /** The caption object of the main view. */
    @FXML
    private Text caption;

    /** The button to navigate back. */
    @FXML
    private Button btnBack;

    /** The content area of the main view. */
    @FXML
    AnchorPane content;

    /** The veil to gray out the main view */
    @FXML
    Region veil;

    /** A private constructor prohibits the creation of objects via other methods. */
    private MainController() {}

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        loadComponent(EViews.OPTIONS);
    }

    /** Navigate backwards. This is depending on the current node. */
    @FXML
    private void back() {
        switch (newNode)
        {
            case COLLECTION -> loadComponent(EViews.COLLECTION_LIST);
            case COLLECTION_LIST -> loadComponent(EViews.DATABASE_LIST);
            case DATABASE_LIST -> loadComponent(EViews.OPTIONS);
        }
    }

    /**
     * Loads the specified component or leaf to the main view. It sets the currentNode to this
     * node and changes the content and title of the main view. Also renders the button accordingly.
     *
     * @param node The node to load.
     */
    public void loadComponent(EViews node) {

        // Replace the internal node.
        try {
        newNode = node;
        } catch (IllegalArgumentException e) {
            handle(e);
        }

        // Prepare the node to load by giving its location to the loader and link its controller.
        FXMLLoader loader = getFXMLLoader(newNode);
        try {
            replaceContent(loader.load()); // Attempt to load the view to the main layout.
            setCaption(); // Replace the caption on success.
            renderBackButton();
        } catch (IOException e) {
            handle(e);
        }

    }

    /**
     * Gets the {@code FXMLLoader} for the current node.  It does so by defining the view name and
     * path (as a URL) by conventions and assign a controller to the node found at the path.
     *
     * @return The {@code FXMLLoader} that holds the current node.
     */
    private FXMLLoader getFXMLLoader(EViews newNode) {
        // Get the path of the node.
        String node = isOverview() ? newNode.toString() : newNode.toString() + "View";
        String path = "view/" + node + ".fxml";

        // Create a loader with the view at the given path.
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(path));

        // Load its controller.
        AbstractController controller = AbstractControllerFactory.getController(this.newNode);
        fxmlLoader.setController(controller);

        // Return the loader that holds the location and controller information but do not load yet.
        return fxmlLoader;
    }

    /**
     * Toggles the visibility of the veil. The veil grays out the main area. This should be used
     * on popup dialogues.
     */
    public void toggleVeil() {
        veil.setVisible(!veil.isVisible());
    }

    /** Checks whether this is an overview */
    private boolean isOverview() {
        return newNode.toString().toLowerCase().contains("overview");
    }

    /**
     * Renders the back button depending on whether a back button is expected or not. Currently the
     * button is only not expected on empty Main and Options views as these are the primary starting
     * views.
     */
    private void renderBackButton() {
        // List of views in which the button should be hidden.
        EViews[] hiddenViews = {EViews.MAIN, EViews.OPTIONS};
        List<EViews> hiddenList = new ArrayList<>(Arrays.asList(hiddenViews));

        // Show the button if the hiddenList does not contain the current node.
        btnBack.setVisible(!hiddenList.contains(newNode));
    }

    /** Replaces the content of the main area. */
    private void replaceContent(Node node) {
        ObservableList<Node> contentChildren = content.getChildren();
        contentChildren.removeAll();
        contentChildren.add(node);
        AnchorPane.setLeftAnchor(node, 0.0);
        AnchorPane.setRightAnchor(node, 0.0);
        AnchorPane.setTopAnchor(node, 0.0);
        AnchorPane.setBottomAnchor(node, 0.0);
    }


    /** Sets the caption of the main view depending on the current node. */
    private void setCaption() {
        String title = switch (newNode) // Specify the title depending on the current node.
                {
                    case COLLECTION -> "Collection: " + settings.getCollection().getName();
                    case COLLECTION_LIST -> "Collection Overview";
                    case DATABASE_LIST -> "Database Overview";
                    default -> "Welcome to Josch.";
                };

        caption.setText(title);
    }

    /**
     * The access method to the class. It creates a concrete object only once and then returns
     * it on every successive call. Synchronizing this method assures that this is being run through
     * by one thread at any given time. Successive calls on successive threads already get an
     * initialized instance.
     *
     * @return The initialized instance of the main controller.
     */
    public static synchronized MainController getInstance() {
        if (instance == null) {
            instance = new MainController();
        }
        return instance;
    }

    /** Gets the system's settings as a DTO. */
    public SettingsDto getSettingsDto() {
        return settings;
    }

    /** Sets the system's settings passing a DTO. */
    public void  setSettingsDto(SettingsDto settings) {
        this.settings = settings;
    }

    /** Gets the host services. This is used for web interaction. */
    public static HostServices getHostServices() {
        return hostServices;
    }

    /** Sets the host services. This is used for web interaction. */
    public static void setHostServices(HostServices services) {
        hostServices = services;
    }

}
