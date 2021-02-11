package josch.presentation.gui;

import javafx.application.Application;
import javafx.application.HostServices;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import josch.model.enums.EThemes;
import josch.model.enums.EViews;
import josch.presentation.gui.controller.AbstractController;
import josch.presentation.gui.controller.AbstractControllerFactory;
import josch.presentation.gui.controller.MainController;
import josch.presentation.gui.controller.OptionsController;

import java.io.IOException;
import java.util.prefs.Preferences;

/** JavaFX App */
public class App extends Application {

  /** The current scene of the Application */
  private static Scene scene;

  /** The stage of the Application */
  private static Stage stage;

  /** The theme of the application. */
  private static String theme;

  /** Array of cascading stylesheets names */
  private static final String[] stylesheets = {"main", "icons", "text"};

  /** Gets the Scene of the App */
  public static Scene getScene() {
    return scene;
  }

  /** Gets the Stage of the App */
  public static Stage getStage() {
    return stage;
  }

  /**
   * The main entry point for all JavaFX applications. The start method is called after the init
   * method has returned, and after the system is ready for the application to begin running.
   *
   * <p>NOTE: This method is called on the JavaFX Application Thread.
   *
   * @param primaryStage the primary stage for this application, onto which the application scene
   *     can be set. Applications may create other stages, if needed, but they will not be primary
   *     stages.
   * @throws Exception if something goes wrong
   */
  @Override
  public void start(Stage primaryStage) throws Exception {

    // Load the font.
    String url = App.class.getResource("view/css/fonts/lucon.ttf").toExternalForm();
    Font.loadFont(url, 14);

    // Start the application
    Parent root = loadFXML("Main");
    scene = new Scene(root);
    addStylesheets();

    // Pass the host services to main controller.
    MainController.setHostServices(getHostServices());

    // Get the theme.
    Preferences prefs = OptionsController.getPreferences();
    EThemes theme = EThemes.getTheme(prefs.get("theme", "default"));
    setTheme(theme.getStylesheet());
    stage = primaryStage;
    stage.setScene(scene);
    stage.show();
  }

  /**
   * Loads a fxml file from the respective directory and Returns it. If the file is not present the
   * user is redirected to an error scene.
   *
   * @param fxml The name of the fxml file except for the trailing View.fxml
   * @return The view as a node with a controller.
   */
  private static Parent loadFXML(String fxml) throws IOException {
    // The relative path to the fxml.
    String node = fxml.toLowerCase().contains("overview") ? fxml : fxml + "View";
    String path = "view/" + node + ".fxml";

    // Prepare the loader by passing the path and the controller
    FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(path));
    AbstractController controller = AbstractControllerFactory.getController(EViews.get(fxml));
    fxmlLoader.setController(controller);
    return fxmlLoader.load();
  }

  /**
   * Sets the scene's root to a given {@code fxml} file, i.e. changes the view to said fxml.
   *
   * @param fxml The {@code fxml} File name to change the root to.
   * @throws IOException When the file does not exist.
   */
  public static void setRoot(String fxml) throws IOException {
    scene.setRoot(loadFXML(fxml));
    stage.hide();
    stage.show();
  }

  /** Reloads the User Interface. */
  public static void reload() {
    stage.hide();
    stage.show();
  }

  public static void main(String[] args) {
    launch();
  }

  /**
   * Adds the stylesheets to the root scene.
   */
  private static void addStylesheets() {
    for (String css : stylesheets) {
      String path = App.class.getResource("view/css/" + css + ".css").toExternalForm();
      scene.getStylesheets().add(path);
    }
  }

  /**
   * Sets the theme of the root scene.
   *
   * @param css The path to the stylesheet that holds the theme.
   */
  public static void setTheme(String css) {
    scene.getStylesheets().removeAll();
    addStylesheets();
    scene.getStylesheets().add(App.class.getResource(css).toExternalForm());
  }
}
