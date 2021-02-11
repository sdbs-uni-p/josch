package josch.presentation.gui.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import josch.model.dto.ConnectionInfoDto;
import josch.model.dto.SettingsDto;
import josch.model.enums.*;
import josch.presentation.gui.App;
import josch.presentation.gui.model.fields.IntegerTextField;
import josch.presentation.gui.model.utils.ELicenses;
import josch.presentation.gui.model.utils.ESoftware;
import josch.services.factory.AbstractServiceFactory;
import josch.services.interfaces.IDatabaseService;

import java.io.File;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;

/**
 * This {@code OptionsController class} is the controller for the options view in the <em>model view
 * controller pattern.</em>. It responds to the user input and performs interactions on the logic
 * layer which represents a part of the model. <em>data transfer objects</em> are used to pass data
 * around. It is used to pass the system and server settings to the backend of the application.
 *
 * @author Kai Dauberschmidt
 */
public class OptionsController extends AbstractController {

  // Organisational components.
  // Captions
  /** Headline */
  @FXML private Text cptHeadline;

  /** Caption for system settings */
  @FXML private Text cptSystemSettings;

  /** Caption for server settings */
  @FXML private Text cptServerSettings;

  // Grid component.
  /** The components to encapsulate the system settings */
  @FXML private GridPane serverRoot;

  @FXML private GridPane systemRoot;

  // System Settings.
  // DBMS component.
  /** The label for the database management system selection. */
  @FXML private Label txtDbSystem;

  /** The ComboBox for the database management system. */
  @FXML private ComboBox<EDatabaseSystems> slcDbSystem;

  // Extraction tool component.
  /** The label for the extraction tool selection. */
  @FXML private Label txtExtract;

  /** The ComboBox for the extraction tool. */
  @FXML private ComboBox<EExtractionTools> slcExtract;

  /** The label for connect name */
  @FXML private Label txtConnectName;

  /** The input field for connect name, i.e. how the connection is called in hackolade */
  @FXML private TextField inpConnectName;

  // Compare tool component.
  /** The label for the comparison tool selection. */
  @FXML private Label txtCompare;

  /** The ComboBox for the comparison tool. */
  @FXML private ComboBox<EContainmentTools> slcCompare;

  // Timeout components.
  /** The label for timeout. */
  @FXML private Label txtTimeout;

  /** The text field for timeout. It accepts only positive integers. */
  @FXML private IntegerTextField inpTimeout;

  // url components.
  /** The label for the url */
  @FXML private Label txtUrl;

  /** The text field for the url */
  @FXML private TextField inpUrl;

  // host components.
  /** The label for the host */
  @FXML private Label txtHost;

  /** The text field for the host */
  @FXML private TextField inpHost;

  // port component.
  /** The label for the port */
  @FXML private Label txtPort;

  /** The text field for port. It accepts only positive integers. */
  @FXML private IntegerTextField inpPort;

  // user name component.
  /** The label for the user name */
  @FXML private Label txtUser;

  /** The text field for the user name for authentication */
  @FXML private TextField inpUser;

  // password component.
  /** The label for the password. */
  @FXML private Label txtPwd;
  /** The password field for the password for authentication */
  @FXML private PasswordField inpPwd;

  // database component.
  /** The label for the database to work on */
  @FXML private Label txtDb;

  /** The input field for the database to work on */
  @FXML private TextField inpDb;

  // save settings component.
  @FXML private CheckBox cbSave;

  // Connect Button.
  @FXML private Button btnConnect;

  // The logo
  @FXML private ImageView imgLogo;
  @FXML private ImageView imgLogoOptions;
  @FXML private ImageView imgLogoSettings;

  // Color components
  @FXML private Label txtTheme;
  @FXML private ComboBox<String> slcTheme;

  // Option components.
  @FXML private TextField folderJSS;
  @FXML private TextField folderIJS;

  // License component.
  @FXML private GridPane licenseList;

  /** Set some tooltips on initialization. */
  public void initialize() {
    // Timeout tooltip
    Tooltip mTimeout = new Tooltip();
    mTimeout.setText("Enter a positive timeout in milliseconds (ms).");
    inpTimeout.setTooltip(mTimeout);

    // URL tooltip
    Tooltip mUrl = new Tooltip();
    mUrl.setText("entering a url will wipe and block the latter settings.");
    inpUrl.setTooltip(mUrl);

    // Load the containment tools.
    List<EContainmentTools> containmentTools = List.of(EContainmentTools.values());
    slcCompare.setItems(FXCollections.observableList(containmentTools));

    // Load the extraction tools.
    List<EExtractionTools> extractionTools = List.of(EExtractionTools.values());
    slcExtract.setItems(FXCollections.observableList(extractionTools));

    // Load the databases.
    List<EDatabaseSystems> databaseSystems = List.of(EDatabaseSystems.values());
    slcDbSystem.setItems(FXCollections.observableList(databaseSystems));

    // Load the themes.
    List<String> themes = new ArrayList<>();
    for (EThemes theme : EThemes.values()) {
      themes.add(theme.getName());
    }
    slcTheme.setItems(FXCollections.observableList(themes));

    // Add the licenses.
    addSoftware();
  }

  private void addSoftware() {
    ESoftware[] usedSoftware = ESoftware.values();

    // Captions. Note that these cannot be cloned easily "Duplicate children exception".
    Label cptSoftware = new Label("Software");
    cptSoftware.getStyleClass().add("cpt");
    licenseList.add(cptSoftware, 0,0);

    Label cptVersion= new Label("Version");
    cptVersion.getStyleClass().add("cpt");
    licenseList.add(cptVersion, 1,0);

    Label cptLicense= new Label("License");
    cptLicense.getStyleClass().add("cpt");
    licenseList.add(cptLicense, 2,0);


    int i = 1;
    for (ESoftware app : usedSoftware) {

      // Hyperlink of software and source.
      Hyperlink software = new Hyperlink(app.getName());
      software.setOnAction(e -> MainController.getHostServices().showDocument(app.getSource()));
      licenseList.add(software,0,i);

      // Version.
      licenseList.add(new Label(app.getVersion()), 1, i);

      // Hyperlink of License and source.
      ELicenses eLicense = app.getLicense();
      Hyperlink license = new Hyperlink(eLicense.getName());
      license.setOnAction(e -> MainController.getHostServices().showDocument(eLicense.getUrl()));
      licenseList.add(license, 2, i);

      i++;
    }
  }


  /**
   * Wraps the given information in a {@code SettingsDto}, connects to the database and switches the
   * view to the {@code databaseOverview} or {@code collectionOverview} view depending on whether a
   * database is given or not.
   */
  @FXML
  private void connect() {

    // Check the installation settings.
    boolean isIjsInstalled = checkFolder(folderIJS.getText(), EContainmentTools.IJS_SUBSET);
    boolean isJssInstalled = checkFolder(folderJSS.getText(), EContainmentTools.JSS);

    // If either is missing notify the user.
    if (!(isIjsInstalled && isJssInstalled)) {
      StringBuilder sb = new StringBuilder("The containment tool");
      if (!(isIjsInstalled || isJssInstalled)) { // If both are missing add plural.
        sb.append("s' location is not set correctly. Please check them.");
      } else { // Get the tool name that is missing.
        String tool =
            !isIjsInstalled
                ? EContainmentTools.IJS_SUBSET.getName()
                : EContainmentTools.JSS.getName();
        sb.append(" '").append(tool).append("' has an invalid location. Please check it.");
      }
      notifyUser(sb.toString());
    } else {

      // Save the settings if save settings is selected.
      saveSettings();

      // Create the settingsDto
      SettingsDto settingsDto =
          new SettingsDto(slcDbSystem.getValue(), slcExtract.getValue(), slcCompare.getValue());

      // Add the connectName if necessary.
      if (hasConnectName()) {
        settingsDto.getExtraction().setDbInstance(inpConnectName.getText());
      }

      // Create the connection information based on whether a url or params are given.
      ConnectionInfoDto connectionInfoDto;
      if (!inpUrl.getText().trim().isEmpty()) {
        connectionInfoDto = new ConnectionInfoDto(inpUrl.getText());
      } else {
        connectionInfoDto = new ConnectionInfoDto(inpHost.getText(), inpPort.getText());
        connectionInfoDto.setUser(inpUser.getText());
        connectionInfoDto.setPassword(inpPwd.getText());
        connectionInfoDto.setDatabase(inpDb.getText());
      }

      // is a timeout given ?
      String timeout = inpTimeout.getText().trim();
      timeout = (timeout.isEmpty()) ? "300" : timeout; // default timeout is set to 300ms
      connectionInfoDto.setTimeout(Long.parseLong(timeout));

      // Add the connection information to the settings.
      settingsDto.setConnectionInfo(connectionInfoDto);

      // Add the Tool paths to the Settings.
      setToolDirectories(settingsDto);

      // Get the service.
      IDatabaseService dbService = AbstractServiceFactory.getDatabaseService(settingsDto);
      boolean isDatabaseGiven = dbService.getDatabase() != null;

      setSettings(settingsDto);

      // Change view depending on whether database is given to DatabaseOverview or
      // CollectionOverview.
      if (isDatabaseGiven) {
        MainController.getInstance().loadComponent(EViews.COLLECTION_LIST);
      } else {
        MainController.getInstance().loadComponent(EViews.DATABASE_LIST);
      }
    }
  }

  /**
   * This method is a {@code keyReleasedProperty()} method for the {@code url} textfield. It will
   * disable the other fields when the {@code url} is not empty, because the url overwrites the
   * others.
   */
  @FXML
  private void urlIsEmpty() {

    // Gets the value of the url textfield and check whether it is empty.
    boolean isEmpty = (inpUrl.getText().trim().isEmpty());

    // Disables the other text fields when the url is not empty.
    TextField[] otherFields = {inpHost, inpPort, inpUser, inpDb, inpPwd};
    for (TextField field : otherFields) {
      field.clear();
      field.setDisable(!isEmpty);
    }

    disableButton();
  }

  /**
   * Disables the connect button when either the url or the required fields are empty. Note that
   * only the host and the port are actually required while the other fields are optional, e.g. for
   * {@code localhost:27017}.
   */
  @FXML
  private void disableButton() {
    String hostValue = inpHost.getText().trim();
    String portValue = inpPort.getText().trim();

    // Get the arguments for xor: A XOR B = (a && !b) || (!a && b).
    boolean hostIsEmpty = (hostValue.isEmpty() || portValue.isEmpty()); // Is either is missing.
    boolean urlIsEmpty = inpUrl.getText().trim().isEmpty();
    boolean isEnabled = (hostIsEmpty && !urlIsEmpty) || (!hostIsEmpty && urlIsEmpty); // XOR.
    btnConnect.setDisable(!isEnabled);
  }

  /** Renders the ConnectName depending on whether the extraction tool has a connect name. */
  @FXML
  private void renderName() {
    txtConnectName.setVisible(hasConnectName());
    inpConnectName.setVisible(hasConnectName());
  }

  /** Action method to choose the IJS Folder. */
  @FXML
  private void chooseIjsFolder() {
    chooseFolder(EContainmentTools.IJS_SUBSET);
  }

  /** Action method to choose the JSS Folder. */
  @FXML
  private void chooseJssFolder() {
    chooseFolder(EContainmentTools.JSS);
  }

  /**
   * Choose a Folder and set its absolute path to the corresponding TextField text.
   *
   * @param tool The tool to decide the Textfield on.
   */
  private void chooseFolder(EContainmentTools tool) {
    // Open a File chooser Dialog to select the directory.
    DirectoryChooser directoryChooser = new DirectoryChooser();
    directoryChooser.setTitle("Select the Location of " + tool.getName());
    File directory = directoryChooser.showDialog(new Stage());

    // Set the path.
    TextField textField = tool.equals(EContainmentTools.JSS) ? folderJSS : folderIJS;
    if (directory != null && directory.exists()) {
      textField.setText(directory.getAbsolutePath());
    }
  }

  /**
   * Checks whether the folder does contain all files the scripts require to be executed.
   *
   * @param Path The path to the folder to check.
   * @param tool The tool that decides which files should be present.
   * @return {@code true} if all files are present, {@code false} else.
   */
  private boolean checkFolder(String Path, EContainmentTools tool) {
    Path root = Paths.get(Path);
    boolean filesExist = true;
    int i = 0;
    String[] files;

    // The files that have to be at least present.
    if (tool.equals(EContainmentTools.IJS_SUBSET)) {
      files = new String[] {"i_ijs_subset.js"};
    } else {
      files = new String[] {"i_jss.py", "Pipfile", "Pipfile.lock"};
    }

    // Check whether the files are in the folder found at Path.
    while (filesExist && i < files.length) {
      filesExist = root.resolve(files[i]).toFile().exists();
      i++;
    }
    return filesExist;
  }

  /** Helper Method to set both directories in the settings at the same time. */
  private void setToolDirectories(SettingsDto settings) {
    settings.setJssPath(folderJSS.getText());
    settings.setIjsPath(folderIJS.getText());
  }

  /** Sets the theme of the application */
  @FXML
  private void setTheme() {
    setColorTheme();
    // reload ui
    App.reload();
  }

  /** Sets the color theme of the application */
  private void setColorTheme() {
    EThemes theme = EThemes.getTheme(slcTheme.getValue());

    // get the stylesheet and logo paths.
    String stylesheet = theme.getStylesheet();
    Image logo = new Image(App.class.getResourceAsStream(theme.getLogo()));

    // Sets the theme.
    if (stylesheet == null) {
      stylesheet = EThemes.ORANGE.getStylesheet();
    }
    App.setTheme(stylesheet);

    // Set the logo accordingly.
    imgLogo.setImage(logo);
    imgLogoOptions.setImage(logo);
    imgLogoSettings.setImage(logo);
  }

  /** Initialization */
  @Override
  public void initialize(URL location, ResourceBundle resources) {
    // Add the tooltips.
    initialize();

    // Scale the image.

    imgLogo.fitWidthProperty().bind(serverRoot.widthProperty().divide(3));
    imgLogoOptions.fitWidthProperty().bind(systemRoot.widthProperty().divide(3));
    imgLogoSettings.fitWidthProperty().bind(systemRoot.widthProperty().divide(3));

    // Load the properties.
    Preferences preferences = Preferences.userNodeForPackage(getClass());
    inpUrl.setText(preferences.get("url", "mongodb://localhost:27017/"));
    inpHost.setText(preferences.get("host", ""));
    inpPort.setText(preferences.get("port", ""));
    inpUser.setText(preferences.get("user", ""));
    inpPwd.setText(preferences.get("pwd", ""));
    inpDb.setText(preferences.get("db", ""));
    inpTimeout.setText(preferences.get("timeout", ""));
    slcDbSystem.setValue(EDatabaseSystems.getSystem(preferences.get("dbms", "MongoDB")));
    slcExtract.setValue(EExtractionTools.getTool(preferences.get("extraction", "Hackolade")));
    slcCompare.setValue(EContainmentTools.getTool(preferences.get("containment", "jsonsubschema")));
    inpConnectName.setText(preferences.get("connectName", "localhost"));
    slcTheme.setValue(preferences.get("theme", "default"));
    folderIJS.setText(preferences.get("ijsPath", ""));
    folderJSS.setText(preferences.get("jssPath", ""));

    // render fields.
    urlIsEmpty();
    renderName();

    // load the image.
    EThemes theme = EThemes.getTheme(preferences.get("theme", "default"));
    Image logo = new Image(App.class.getResourceAsStream(theme.getLogo()));
    // Set the logo accordingly.
    imgLogo.setImage(logo);
    imgLogoOptions.setImage(logo);
    imgLogoSettings.setImage(logo);
  }

  /** Returns {@code true} when the extraction tool has a connect name, {@code false} else. */
  private boolean hasConnectName() {
    return slcExtract.getValue().equals(EExtractionTools.HACK);
  }

  public static Preferences getPreferences() {
    return Preferences.userNodeForPackage(OptionsController.class);
  }

  /** Save the properties if the checkbox of "save properties" is selected. */
  private void saveSettings() {
    if (cbSave.isSelected()) {
      Preferences preferences = Preferences.userNodeForPackage(getClass());

      // Set the properties
      preferences.put("url", inpUrl.getText());
      preferences.put("host", inpHost.getText());
      preferences.put("port", inpPort.getText());
      preferences.put("user", inpUser.getText());
      preferences.put("pwd", inpPwd.getText());
      preferences.put("timeout", inpTimeout.getText());
      preferences.put("dbms", slcDbSystem.getValue().getName());
      preferences.put("extraction", slcExtract.getValue().getName());
      preferences.put("containment", slcCompare.getValue().getName());
      preferences.put("connectName", inpConnectName.getText());
      preferences.put("theme", slcTheme.getValue());
      preferences.put("jssPath", folderJSS.getText());
      preferences.put("ijsPath", folderIJS.getText());
    }
  }
}
