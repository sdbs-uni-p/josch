package josch.presentation.gui.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import josch.model.dto.DifferenceDto;
import josch.model.dto.ExtractionDto;
import josch.model.dto.SchemaDto;
import josch.model.dto.ValidationDto;
import josch.model.enums.EContainmentRelations;
import josch.model.enums.EContainmentTools;
import josch.model.enums.EDatabaseSystems;
import josch.model.enums.ESystemConstants;
import josch.presentation.gui.model.fields.IntegerTextField;
import josch.presentation.gui.model.validator.InputValidator;
import josch.services.interfaces.AbstractComparisonService;
import josch.services.interfaces.IDatabaseService;
import josch.services.interfaces.IExtractionService;
import josch.services.interfaces.IValidationService;
import org.everit.json.schema.Schema;
import org.everit.json.schema.SchemaException;
import org.everit.json.schema.ValidationException;
import org.everit.json.schema.loader.SchemaLoader;
import org.fxmisc.richtext.CodeArea;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.prefs.Preferences;

/**
 * This {@code CollectionViewController} class is used as the controller component in the MVC
 * pattern for the CollectionView view. The logical structure is as following:
 *
 * <ul>
 *   <li>FXML elements outside of the tabs.
 *   <li>FXML elements of the view tab.
 *   <li>FXML elements of the analyze tab.
 *   <li>{@code initialize()} method to initialize the controller.
 *   <li>FXML methods in alphabetical order.
 *   <li>private Helper methods in alphabetical order.
 * </ul>
 *
 * Note that only programmatically interacted elements are listed. There might be some unresolved
 * references - they will be resolved (interacted with) on frontend automated testing.
 *
 * @author Kai Dauberschmidt
 */
public class CollectionViewController extends AbstractDatabaseController {

  // Content outside of tabs
  /**
   * The name of the collection
   */
  @FXML
  private Text txtCollection;

  /**
   * The button to go back to the overview
   */
  @FXML
  private Button btnBack;

  /** The tab pane for view and analyze tabs */
  @FXML private TabPane tabPane;
  // View Tab
  /**
   * The view Tab
   */
  @FXML
  private Tab tabView;

  // Document side
  /**
   * The button to show all documents in a popup.
   */
  @FXML
  private Button btnShowAll;

  /**
   * The button to get a sample document.
   */
  @FXML
  private Button btnGetSampleDocument;

  /**
   * The button to save a document in the database.
   */
  @FXML
  private Button btnInsertDocument;

  /**
   * The text area of the document
   */
  @FXML
  private CodeArea caDocument;

  // Center
  /**
   * The button to validate a document against a validator or schema
   */
  @FXML
  private Button btnValidateDocument;

  // Validator.
  /**
   * The validator label
   */
  @FXML
  private Text cptValidator;

  /**
   * The dropdown for the sampling method for validator
   */
  @FXML
  private ComboBox<String> slcValMethod;

  /**
   * The text field for the sampling size
   */
  @FXML
  private IntegerTextField inpValSize;

  /**
   * The button to generate a validator
   */
  @FXML
  private Button btnGenerateValidator;

  /**
   * The button to reset the validator
   */
  @FXML
  private Button btnResetValidator;

  /**
   * The button to register a validator at the database
   */
  @FXML
  private Button btnSaveValidator;

  /**
   * The validator itself.
   */
  @FXML
  private CodeArea caValidator;
  private String validator;

  /**
   * The dropdown for validation level
   */
  @FXML
  private ComboBox<String> slcLevel;

  /**
   * The dropdown for validation action
   */
  @FXML
  private ComboBox<String> slcAction;

  // Analyze Tab
  @FXML
  private Tab tabAnalyze;

  /**
   * An object that holds the syntax differences
   */
  private DifferenceDto differences;

  /**
   * The dropdown for the tools.
   */
  @FXML
  private ComboBox<EContainmentTools> slcCompare;

  /**
   * The display of the check result.
   */
  @FXML
  private Button btnSyntaxResult;
  @FXML
  private Button btnContainmentResult;

  /**
   * The button to check containment with
   */
  @FXML
  private Button btnContainment;

  /**
   * The button to check syntax with
   */
  @FXML
  private Button btnSyntax;

  // left side 
  /**
   * The left caption
   */
  @FXML
  private ComboBox<String> slcLeft;

  /**
   * dynamic content on row 1
   */
  @FXML
  private GridPane grdLeftAnalyze;
  @FXML
  private GridPane grdLeftFilter;
  @FXML
  private GridPane grdLeftLoad;

  /**
   * The code area of the left side
   */
  @FXML
  private CodeArea leftCodeArea;
  private String leftSchema;

  // Right side 
  /**
   * The label for right side
   */
  @FXML
  private ComboBox<String> slcRight;

  /**
   * dynamic content on row 1
   */
  @FXML
  private GridPane grdRightAnalyze;
  @FXML
  private GridPane grdRightFilter;
  @FXML
  private GridPane grdRightLoad;

  /**
   * The right code area
   */
  @FXML
  private CodeArea rightCodeArea;
  private String rightSchema;


  // dynamic content for both left and right side. 
  /**
   * The button to get a JSON Schema from the database
   */
  @FXML
  private Button leftBtnAnalyze;
  @FXML
  private Button rightBtnAnalyze;

  /**
   * The button to save the JSON Schema in the database
   */
  @FXML
  private Button leftBtnSaveAnalyze;
  @FXML
  private Button rightBtnSaveAnalyze;

  /**
   * The dropdown for the sampling method
   */
  @FXML
  private ComboBox<String> leftSlcMethod;
  @FXML
  private ComboBox<String> rightSlcMethod;

  /**
   * The label for the units.
   */
  @FXML
  private Label leftTxtUnit;
  @FXML
  private Label rightTxtUnit;
  @FXML
  private Label txtUnit;

  /**
   * The text field for the sampling size
   */
  @FXML
  private IntegerTextField leftInpSize;
  @FXML
  private IntegerTextField rightInpSize;

  /**
   * The button to apply the filters.
   */
  @FXML
  private Button leftBtnFilter;
  @FXML
  private Button rightBtnFilter;

  /**
   * The selection for min date filter
   */
  @FXML
  private DatePicker leftSlcMinDate;
  @FXML
  private DatePicker rightSlcMinDate;

  /**
   * The selection for min date filter
   */
  @FXML
  private DatePicker leftSlcMaxDate;
  @FXML
  private DatePicker rightSlcMaxDate;

  /**
   * The selection for the schema.
   */
  @FXML
  private ComboBox<SchemaDto> leftSlcSchema;
  @FXML
  private ComboBox<SchemaDto> rightSlcSchema;

  /**
   * The button to skip to the first schema.
   */
  @FXML
  private Button leftBtnFirstSchema;
  @FXML
  private Button rightBtnFirstSchema;

  /**
   * The button to get to the previous schema.
   */
  @FXML
  private Button leftBtnPrevSchema;
  @FXML
  private Button rightBtnPrevSchema;

  /**
   * The button to get to the next schema.
   */
  @FXML
  private Button leftBtnNextSchema;
  @FXML
  private Button rightBtnNextSchema;

  /**
   * The button to skip to the last schema.
   */
  @FXML
  private Button leftBtnLastSchema;
  @FXML
  private Button rightBtnLastSchema;

  /**
   * The Textfield to enter the schema name
   */
  @FXML
  private TextField rightInpSave;
  @FXML
  private TextField leftInpSave;

  /**
   * The Textfield to enter the external schema path.
   */
  @FXML private TextField leftExternalPath;
  @FXML private TextField rightExternalPath;

  /**
   * {@inheritDoc}
   */
  @Override
  public void initialize(URL url, ResourceBundle rb) {

    // Initialize the code areas.
    initializeCodeArea(leftCodeArea);
    initializeCodeArea(caDocument);
    initializeCodeArea(rightCodeArea);
    initializeCodeArea(caValidator);

    // Load the containment tools.
    List<EContainmentTools> containmentTools = List.of(EContainmentTools.values());
    slcCompare.setItems(FXCollections.observableList(containmentTools));

    // preload schemas and render buttons.
    filter();
    renderQuickActionButtons();


    // bind buttons to schema selection.
    rightSlcSchema
            .valueProperty()
            .addListener(
                    (observable, oldValue, newValue) -> {
                      try {
                        rightCodeArea.replaceText(pretty(newValue.getJson()));
                      } catch (NullPointerException ignored) {
                      }
                      renderQuickActionButtons("right");
                    });
    leftSlcSchema
            .valueProperty()
            .addListener(
                    (observable, oldValue, newValue) -> {
                      try {
                        leftCodeArea.replaceText(pretty(newValue.getJson()));
                      } catch (NullPointerException ignored) {
                      }
                      renderQuickActionButtons("left");
                    });

    // preload containment tool
    slcCompare.setValue(getSettings().getComparison());

    if (getSettings().getDbms().equals(EDatabaseSystems.MONGO)) {
      // Load a pre-existing validator.
      resetValidator();

      // bind validation of fields.
      rightInpSize.setOnKeyPressed(event -> validateSize("rightInpSize"));
      leftInpSize.setOnKeyPressed(event -> validateSize("leftInpSize"));
      inpValSize.setOnKeyPressed(event -> validateSize("inpValSize"));
      leftSlcMethod
              .valueProperty()
              .addListener((observable, oldValue, newValue) -> {
                validateSize("leftInpSize");
                setUnit(leftTxtUnit, newValue);
              });

      rightSlcMethod
              .valueProperty()
              .addListener((observable, oldValue, newValue) -> {
                validateSize("rightInpSize");
                setUnit(rightTxtUnit, newValue);
              });
      slcValMethod
              .valueProperty()
              .addListener((observable, oldValue, newValue) -> {
                validateSize("inpValSize");
                setUnit(txtUnit, newValue);
              });
    } else {
      // If not MongoDB hide the mongodb specifics.
      slcAction.setVisible(false);
      slcLevel.setVisible(false);
      slcValMethod.setVisible(false);
      inpValSize.setVisible(false);
      btnGenerateValidator.setVisible(false);
      btnSaveValidator.setVisible(false);
      btnResetValidator.setVisible(false);

      // Rename the Validator caption to Schema.
      cptValidator.setText("Schema");
    }

    // Hide *show all* on collections > 300 docs.
    btnShowAll.setVisible(!(getSettings().getCollection().getCount() > 300));

    // Preload the previously used dropdowns in the analyze.
    Preferences preferences = Preferences.userNodeForPackage(getClass());
    slcLeft.setValue(preferences.get("left", "Current JSON Schema"));
    handleLeftSelection();
    slcRight.setValue(preferences.get("right", "Legacy JSON Schema"));
    handleRightSelection();

    // Add tooltip to sampling method.
    Tooltip ttMethod = new Tooltip();
    String text = "An absolute analysis will consider the fixed amount of documents. " +
            "A relative analysis will consider a percentage of the collection.";
    ttMethod.setText(text);
    ttMethod.setWrapText(true);
    ttMethod.setPrefWidth(300);
    slcValMethod.setTooltip(ttMethod);
    leftSlcMethod.setTooltip(ttMethod);
    rightSlcMethod.setTooltip(ttMethod);
  }

  // FXML methods: These are being called by the user primarily.

  /**
   * Checks the schema containment using the value at {@code slcTools}.
   */
  @FXML
  private void checkContainment() {
    // store the left and right dropdown selection.
    saveSelection();

    String leftSchema = leftCodeArea.getText();
    String rightSchema = rightCodeArea.getText();

    // Note that the & operator is used instead of && to forcefully validate both sides.
    boolean isValid = validate(leftSchema, "left") & validate(rightSchema, "right");

    if (isValid) {
      getSettings().setComparison(slcCompare.getValue());
      execute(processContainment(leftSchema, rightSchema));
    }
  }

  /**
   * Checks the schema syntax
   */
  @FXML
  private void checkSyntax() {
    // store the left and right dropdown selection.
    saveSelection();

    String leftSchema = leftCodeArea.getText();
    String rightSchema = rightCodeArea.getText();

    // Note that the & operator is used instead of && to forcefully validate both sides.
    boolean isValid = validate(leftSchema, "left") & validate(rightSchema, "right");

    if (isValid) {
      execute(processSyntax(leftSchema, rightSchema));
      highlightDifferences();
    }
  }

  /**
   * action method for left filtering button.
   */
  @FXML
  private void leftFilter() {
    filter("left");
  }

  /**
   * action method for right filtering button.
   */
  @FXML
  private void rightFilter() {
    filter("right");
  }

  /**
   * Updates both selections with the according filters.
   */
  private void filter() {
    filter("left");
    filter("right");
  }



  /**
   * This applies a minimum and maximum date if existent to the schema selection and fills the
   * selection with options.
   */
  private void filter(String side) {

    // Globals:
    LocalDate min;
    LocalDate max;
    ComboBox<SchemaDto> slcSchema;

    // Get the two boundaries and validate them.
    if (side.equals("right")) {
      min = getDate(rightSlcMinDate);
      max = getDate(rightSlcMaxDate);
      slcSchema = rightSlcSchema;
    } else {
      min = getDate(leftSlcMinDate);
      max = getDate(leftSlcMaxDate);
      slcSchema = leftSlcSchema;
    }


    // if range is valid do the filtering, else notify the user.
    if (InputValidator.validateRange(min, max)) {

      // get the schemas and loop through them.
      IDatabaseService service = getDbService();
      List<SchemaDto> schemas = service.getSchemasInDateRange(min, max);
      slcSchema.getItems().clear();
      slcSchema.getItems().addAll(schemas);
    } else {
      notifyUser(
              ESystemConstants.ERROR.getValue() + "Minimal date has to be before or at maximal date.");
    }
  }

  /**
   * Gets the first item on the {@code leftSlcSchema} ComboBox.
   */
  @FXML
  private void leftFirstSchema() {
    leftSlcSchema.getSelectionModel().select(0);
  }

  /**
   * Gets the first item on the {@code rightSlcSchema} ComboBox.
   */
  @FXML
  private void rightFirstSchema() {
    rightSlcSchema.getSelectionModel().select(0);
  }

  /**
   * Generates a validator based on the current extraction tool and database for a practical
   * document validation on persistency layer and prints it to the {@code inpValidator} text area.
   *
   * @see AbstractController#execute(Callable)
   * @see CollectionViewController#processGenerateValidator()
   */
  @FXML
  private void generateValidator() {
    execute(processGenerateValidator());

    if (validator.contains(ESystemConstants.ERROR.getValue())) {
      notifyUser(validator);
    } else {
      caValidator.replaceText(validator);
    }
  }

  /** Action method to generate a validator from the right code area. */
  @FXML
  private void rightGenerateValidator() {
    generateValidator("right");
  }

  /** Action method to generate a validator from the left code area. */
  @FXML
  private void leftGenerateValidator() {
    generateValidator("left");
  }

  /**
   * Generates a validator from a given JSON Schema that is written on a given side. It converts
   * the JSON Schema to the DBMS validator using the respective service.
   *
   * @param side The side to get the JSON Schema from.
   */
  private void generateValidator(String side) {

    // Get the JSON Schema and validate it.
    CodeArea codeArea = side.equals("right") ? rightCodeArea : leftCodeArea;
    String JsonSchema = codeArea.getText();
    boolean isValid = validate(JsonSchema, side);
    if (isValid) {

      // Generate a validator for the DBMS.
      IDatabaseService service = getDbService();
      String validator = service.generateValidator(JsonSchema);

      // If a validator does contain an error message notify the user else process it.
      if (validator.contains(ESystemConstants.ERROR.getValue())) {
        notifyUser(validator);
      } else { // Else replace the validator text and switch tabs.
        caValidator.replaceText(pretty(validator));
        tabPane.getSelectionModel().select(tabView);
      }
    }
  }

  /**
   * Gets a sample document as a String, converts it to a pretty JSON String and shows it within the
   * document input CodeArea.
   */
  @FXML
  private void getSample() {
    // load a random document.
    IDatabaseService service = getDbService();
    String document = service.getRandomDocument();

    // show the random document as a pretty JSON String.
    caDocument.replaceText(pretty(document));
  }

  /**
   * Inserts the input document into the database and shows it's status in the status text.
   */
  @FXML
  private void insertDocument() {
    String document = caDocument.getText(); // Get the document.
    IDatabaseService service = getDbService();
    String message = service.insertOne(document);
    notifyUser(message);
  }

  /**
   * Action method to analyze left side.
   */
  @FXML
  private void leftAnalyze() {
    analyze("left");
  }

  /**
   * Action method to analyze right side
   */
  @FXML
  private void rightAnalyze() {
    analyze("right");
  }

  /**
   * Analyzes the current collection prints a valid JSON Schema to the {@code inpIntrospect}
   * CodeArea.
   *
   * @param side the side the introspection is called on.
   * @see AbstractController#execute(Callable);
   * @see CollectionViewController#processIntrospect(String side)
   */
  private void analyze(String side) {
    execute(processIntrospect(side));

    if (side.equals("right")) {
      if (rightSchema.contains(ESystemConstants.ERROR.getValue())) {
        notifyUser(rightSchema);
      } else {
        rightCodeArea.replaceText(rightSchema);
      }
    } else {
      if (leftSchema.contains(ESystemConstants.ERROR.getValue())) {
        notifyUser(leftSchema);
      } else {
        leftCodeArea.replaceText(leftSchema);
      }
    }

  }

  /**
   * Gets the last item on the {@code leftSlcSchema} ComboBox.
   */
  @FXML
  private void leftLastSchema() {
    int i = leftSlcSchema.getItems().size() - 1;
    leftSlcSchema.getSelectionModel().select(i);
  }

  /**
   * Gets the last item on the {@code rightSlcSchema} ComboBox.
   */
  @FXML
  private void rightLastSchema() {
    int i = rightSlcSchema.getItems().size() - 1;
    rightSlcSchema.getSelectionModel().select(i);
  }

  /**
   * Gets the next item on the {@code leftSlcSchema} ComboBox by increasing its index by one.
   */
  @FXML
  private void leftNextSchema() {
    int i = leftSlcSchema.getSelectionModel().getSelectedIndex();
    leftSlcSchema.getSelectionModel().select(i + 1);
  }

  /**
   * Gets the next item on the {@code leftSlcSchema} ComboBox by increasing its index by one.
   */
  @FXML
  private void rightNextSchema() {
    int i = rightSlcSchema.getSelectionModel().getSelectedIndex();
    rightSlcSchema.getSelectionModel().select(i + 1);
  }

  /**
   * Gets the previous item on the {@code leftSlcSchema} ComboBox by decreasing its index by one.
   */
  @FXML
  private void leftPrevSchema() {
    int i = leftSlcSchema.getSelectionModel().getSelectedIndex();
    leftSlcSchema.getSelectionModel().select(i - 1);
  }

  /**
   * Gets the previous item on the {@code rightSlcSchema} ComboBox by decreasing its index by one.
   */
  @FXML
  private void rightPrevSchema() {
    int i = rightSlcSchema.getSelectionModel().getSelectedIndex();
    rightSlcSchema.getSelectionModel().select(i - 1);
  }

  /**
   * Handles the action event of right selection.
   */
  @FXML
  private void handleRightSelection() {
    renderSide("right");
  }

  /**
   * Handles the action event of left selection.
   */
  @FXML
  private void handleLeftSelection() {
    renderSide("left");
  }

  /** Action method to choose a file on the left side. */
  @FXML
  private void leftChooseFile() {
    chooseFile("left");
    leftLoadFromFile();
  }

  /** Action method to choose a file on the right side. */
  @FXML
  private void rightChooseFile() {
    chooseFile("right");
    rightLoadFromFile();
  }


  /**
   *  Lets the user choose a file from the computer and loads its path to the respective
   *  TextField on the side that invoked the method.
   *
   *  @param side The side to invoke the method from.
   */
  private void chooseFile(String side) {
    // Select the textfield to print the path to.
    TextField externalPath = side.equals("left") ? leftExternalPath : rightExternalPath;

    // Opens a file chooser that allows the user to pick .json files and selects it.
    FileChooser fileChooser = new FileChooser();
    fileChooser.setTitle("Select JSON Schema.");
    fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSON Files", "*.json"));
    File selectedFile = fileChooser.showOpenDialog(new Stage());

    // Set the path if the file exists.
    if (selectedFile != null && selectedFile.exists()) {
      externalPath.setText(selectedFile.getAbsolutePath());
    }
  }

  /** Action method to load a schema from the path of the left path textfield. */
  @FXML private void leftLoadFromFile() {
    loadFromFile("left");
  }

  /** Action method to load a schema from the path of the right path textfield. */
  @FXML private void rightLoadFromFile() {
    loadFromFile("right");
  }

  /**
   * Loads a file from the path found at the respective path textfield depending on the side this
   * method is being invoked. The content of the file gets inserted into the CodeArea of this side.
   * Does check the existence of the file to load.
   *
   * @param side The side this is being invoked.
   */
  private void loadFromFile(String side) {

    // Choose the fxml elements depending on the specified side.
    boolean isLeft = side.equals("left");
    TextField pathField = isLeft ? leftExternalPath : rightExternalPath;
    CodeArea codeArea = isLeft ? leftCodeArea : rightCodeArea;

    // Check whether the path is not empty.
    String path = pathField.getText();
    if (path == null || path.isEmpty()) {
      notifyUser(ESystemConstants.ERROR.getValue() + "Please specify a schema first.");
      return;
    }

    // Attempt to read the file from the path.
    String schema;
    try (Scanner reader = new Scanner(new File(path))) {

      // Build the schema as a string line by line.
      StringBuilder schemaBuilder = new StringBuilder();
      while (reader.hasNextLine()) {
        schemaBuilder.append(reader.nextLine()).append("\n");
      }
      schema = schemaBuilder.toString();

      // show the schema.
      codeArea.replaceText(schema);

    } catch (IOException e) {
      notifyUser(ESystemConstants.ERROR.getValue() + e.getMessage());
      e.printStackTrace();
    }
  }

  /**
   * Renders a side depending on its selection
   */
  @FXML
  private void renderSide(String side) {

    // Set the respective side.
    boolean isRight = (side.equals("right"));
    ComboBox<String> selector = isRight ? slcRight : slcLeft;
    GridPane grdAnalyze = isRight ? grdRightAnalyze : grdLeftAnalyze;
    GridPane grdFilter = isRight ? grdRightFilter : grdLeftFilter;
    GridPane grdLoad = isRight ? grdRightLoad : grdLeftLoad;

    // Get the selected value and act on it.
    switch (selector.getValue()) {
      case "Legacy JSON Schema" -> { // Show only filtering.
        grdFilter.setVisible(true);
        grdAnalyze.setVisible(false);
        grdLoad.setVisible(false);
      }
      case "Current JSON Schema" -> { // Show only analysis
        grdFilter.setVisible(false);
        grdAnalyze.setVisible(true);
        grdLoad.setVisible(false);
      }
      case "Load JSON Schema" -> {
        grdLoad.setVisible(true);
        grdFilter.setVisible(false);
        grdAnalyze.setVisible(false);
      }
      default -> { // Show nothing.
        grdFilter.setVisible(false);
        grdAnalyze.setVisible(false);
        grdLoad.setVisible(false);
      }
    }
  }

  /**
   * Resets the Validator to database default.
   */
  @FXML
  private void resetValidator() {
    // Get a pretty JSON String as validator or "null" if none exists.
    String validator = pretty(getSettings().getCollection().getValidator());
    validator = (validator.equals("null")) ? "" : validator; // Delete "null" in that case.
    caValidator.replaceText(validator);

    // Get and set the validation level.
    String level = getSettings().getCollection().getValidationLevel();
    level = (level == null) ? "off" : level;
    slcLevel.setValue(level);

    // Get and set the validation action.
    String action = getSettings().getCollection().getValidationAction();
    action = (action == null) ? "warn" : action;
    slcAction.setValue(action);
  }

  /** Registers the validator at the database */
  @FXML
  private void saveValidator() {
    // Store the validator and its options.
    Map<String, String> validationOptions = new HashMap<>();
    validationOptions.put("validator", caValidator.getText()); // Validator.
    validationOptions.put("validationLevel", slcLevel.getValue()); // Level.
    validationOptions.put("validationAction", slcAction.getValue()); // Action.

    // Get and call the service.
    IDatabaseService service = getDbService();
    String message = service.setValidation(validationOptions);
    notifyUser(message);
  }

  /** Sets the unit of the given unit label to the value. */
  private void setUnit(Label label, String value) {
    String unit = value.equals("absolute") ? "documents." : "%.";
    label.setText(unit);
  }


  /**
   * Shows all documents of a collection in a scrollable pop up window. The window is created on the
   * fly within this method.
   */
  @FXML
  private void showAll() {
    String cpt = "Show all documents of " + getSettings().getCollection().getName();

    // Get the documents.
    IDatabaseService service = getDbService();
    List<String> documents = service.getAllDocuments();
    StringBuilder concatDocs = new StringBuilder();

    // make a coherent string.
    for (int i = 0; i < documents.size(); i++) {
      concatDocs.append(pretty(documents.get(i)));

      // If the document is not the last one append a comma and start a new line.
      if (i < documents.size() - 1) {
        concatDocs.append(",\n");
      }
    }

    showDocuments(concatDocs.toString(), cpt);
  }

  /**
   * Action method to save the left schema.
   */
  @FXML
  private void leftStoreSchema() {
    storeSchema("left");
  }

  /**
   * Action method to save the right schema.
   */
  @FXML
  private void rightStoreSchema() {
    storeSchema("right");
  }

  /**
   * Stores a schema at the database
   */
  private void storeSchema(String side) {

    // Get the basics.
    boolean isRight = side.equals("right");
    CodeArea codeArea = isRight ? rightCodeArea : leftCodeArea;
    TextField textField = isRight ? rightInpSave : leftInpSave;

    // Get the schema and its name.
    String schema = codeArea.getText();
    String name = textField.getText();

    // Inform the user if the schema is empty.
    if (schema == null || schema.trim().equals("")) {
      notifyUser(
              ESystemConstants.ERROR.getValue() + "No schema specified, please insert one first.");

      // Save the schema, inform the user about the storage and possibly update fields.
    } else {
      IDatabaseService service = getDbService();
      String message = service.storeSchema(schema, name);
      notifyUser(message);
      filter();
    }
  }

  /**
   * Validates all documents of this collection against the schema in the validator field.
   */
  @FXML
  private void validateAll() {
    // Define the confirmation message.
      String notification = "Validating all can impact the performance of "
              + "the database and the computer. Do you want to proceed?";
      confirmExecute(notification, null, processValidateAll());
  }



  /**
   * Validates the document in the input field against the schema in the validator field.
   */
  @FXML
  private void validateDocument() {
    // Get the document, schema and service.
    String schema = caValidator.getText();
    String document = caDocument.getText();
    IValidationService service = getValidationService();

    // Validate and print the message.
    String message = service.validate(document, schema);
    notifyUser(message);
  }

  /**
   * Validate the input of the sampling size with the given id.
   *
   * @param id The id of the IntegerTextField for sampling size.
   */
  @FXML
  private void validateSize(String id) {

    // Set the three elements used in sampling according to the given id.
    Button button;
    String method;
    String size;
    if (id.equals("inpValSize")) {
      button = btnGenerateValidator;
      method = slcValMethod.getValue();
      size = inpValSize.getText();
    } else if (id.equals("leftInpSize")) {
      button = leftBtnAnalyze;
      method = leftSlcMethod.getValue();
      size = leftInpSize.getText();
    } else {
      button = rightBtnAnalyze;
      method = rightSlcMethod.getValue();
      size = rightInpSize.getText();
    }

    // get the integer value.
    int value;
    if (size.equals("") || size.isEmpty()) {
      button.setDisable(true);
    } else {
      value = Integer.parseInt(size);
      // Determine whether the value is in the legal range.
      int min = 1;
      int max = (method.equals("relative")) ? 100 : 100000;
      boolean outOfRange = !inRange(value, min, max);

      // Disable the button if out of range.
      button.setDisable(outOfRange);

      // notify the user if out of range.
      if (outOfRange) {
        String note = ESystemConstants.ERROR.getValue() + "The sampling size is too ";
        note += (value < min) ? "small." : "big.";
        note += "\n Make sure the value is in the range of [" + min + ", " + max + "].";
        notifyUser(note);
      }
    }
  }

  // Helper Methods in alphabetical order.

  /** Checks whether a given {@code value} is in the interval of [min, max]. */
  private boolean inRange(int value, int min, int max) {
    return (value >= min) && (value <= max);
  }

  /**
   * Checks the containment using the specified tool.
   */
  private Callable<String> processContainment(String current, String legacy) {
    return (() -> {
      AbstractComparisonService service = getComparisonService();

      // Check containment and render result.
//      System.out.println("left: " + pretty(current));
//      System.out.println("-----");
//      System.out.println("right: " + pretty(legacy));
//      System.out.println("-----");
      String result = service.contains(current, legacy);
      renderResult(result, "containment");
      if (result.contains("error")) {
        return ESystemConstants.ERROR.getValue() + result;
      } else {
        System.out.println("\n containment result: " + result);
        EContainmentRelations relation = EContainmentRelations.get(result);
        relation = relation == null ? EContainmentRelations.NEQ : relation;
        return switch (relation) {
          case EQV -> "The schemas are equivalent.  That means all documents that validate against " +
                  "the left one also validate against the right one and vice versa.";
          case NEQV -> "The schemas are incomparable. That means there exist documents that validate" +
                  " against the left one but not to the right one and vice versa.";
          case SUB -> "The right schema contains the left one. That means all documents that " +
                  "validate against the left one also validate against the right one " +
                  "but not vice versa.";
          case SUP -> "The left schema contains the right one. That means all documents that " +
                  "validate against the right one also validate against the left one " +
                  "but not vice versa.";
          default -> ESystemConstants.ERROR.getValue() + "The relation cannot be determined.";
        };
      }
    });
  }

  /**
   * Implements the background process for the {@code btnIntrospect} and returns it as a runnable
   * which is going to be called by {@code execute()} in the button's method. This is done to avoid
   * code duplication.
   *
   * @return A runnable with the background process for introspection.
   */
  private Callable<String> processGenerateValidator() {
    return (() -> {
      ExtractionDto extractionDto = getSettings().getExtraction();
      extractionDto.setSize(Integer.parseInt(inpValSize.getText()));
      extractionDto.setMethod(slcValMethod.getValue());

      // Get the service and a pretty validator, if exists.
      IExtractionService service = getExtractionService();
      String validator = service.getValidator();
      if (!validator.contains("Error!")) {
        validator = pretty(validator);
      }
      this.validator = validator;
      return null;
    });
  }

  /**
   * Implements the background process for the {@code btnGenerateValidator} and returns it as a
   * runnable which is going to be called by {@code execute()} in the button's method. This is done
   * to avoid code duplication.
   *
   * @return A runnable with the background process for introspection.
   */
  private Callable<String> processIntrospect(String side) {

    // side specifics
    boolean isRight = side.equals("right");
    IntegerTextField inpSize = isRight ? rightInpSize : leftInpSize;
    ComboBox<String> slcMethod = isRight ? rightSlcMethod : leftSlcMethod;

    return (() -> {
      // Complete the getSettings().
      ExtractionDto extractionDto = getSettings().getExtraction();
      extractionDto.setSize(Integer.parseInt(inpSize.getText()));
      extractionDto.setMethod(slcMethod.getValue());

      // Get the service and get the pretty JSON Schema if exists.
      IExtractionService extractionService = getExtractionService();
      String jsonSchema = extractionService.getJsonSchema();
      if (!jsonSchema.contains("Error!")) {
        jsonSchema = pretty(jsonSchema);
      }

      if (side.equals("right")) {
        this.rightSchema = jsonSchema;
      } else {
      this.leftSchema = jsonSchema;
      }
      return null;
    });
  }

  /**
   * Returns a Runnable that validates all documents of a given collection against the validator
   * found in the CodeArea for validators.
   *
   * @return The method to be executed.
   */
  private Callable<ValidationDto> processValidateAll() {
    return (() -> {
      // Get the validator and the service.
      String schema = caValidator.getText();
      IValidationService service = getValidationService();

      // Validate and print the message.
      return service.validate(schema);
    });
  }

  /**
   * Checks the containment using the specified tool.
   */
  private Callable<String> processSyntax(String current, String legacy) {
    return (() -> {
      AbstractComparisonService service = getComparisonService();

      // Check containment and render result.
      differences = service.equals(current, legacy);
      String ico = differences.hasNone() ? "equal" : "not equal";
      renderResult(ico, "syntax");
      return "The two schemas are syntactically " + ico + ".";
    });
  }

  /**
   * Renders the icon that is displayed as a result for both containment and syntax checks.
   *
   * @param result The value of a EContainmentRelation.
   */
  private void renderResult(String result, String method) {

    Button btnResult = method.equals("syntax") ? btnSyntaxResult : btnContainmentResult;

    // Remove all previous style classes and add the basic one.
    if (!result.contains(ESystemConstants.ERROR.getValue())) {
      btnResult.getStyleClass().clear();
      btnResult.getStyleClass().add("ico-compare");

      // Render the style class based on the result.
      EContainmentRelations icon = EContainmentRelations.get(result);
      String styleClass;
      if (icon != null) {
        styleClass = switch (icon) {
          case EQV -> "i-eqv";
          case NEQV -> "i-neqv";
          case EQ -> "i-eq";
          case NEQ -> "i-neq";
          case SUB -> "i-sub";
          case SUP -> "i-sup";
        };
      } else  {
        styleClass = method.equals("syntax") ? "i-is-eq" : "i-is-eqv";
      }
      btnResult.getStyleClass().add(styleClass);
    } else {
      notifyUser(result);
    }
  }

  /**
   * Renders both quick action buttons
   */
  private void renderQuickActionButtons() {
    renderQuickActionButtons("left");
    renderQuickActionButtons("right");
  }

  /**
   * Shows or hides the quick action buttons depending on the selection index. They must not be
   * shown on the first and last item of the {@code ObservableList} depending on whether they regard
   * the first or last item.
   */
  private void renderQuickActionButtons(String side) {

    // side specifics.
    boolean isRight = side.equals("right");
    ComboBox<SchemaDto> slcSchema = isRight ? rightSlcSchema : leftSlcSchema;
    Button btnPrevSchema = isRight ? rightBtnPrevSchema : leftBtnPrevSchema;
    Button btnFirstSchema = isRight ? rightBtnFirstSchema : leftBtnFirstSchema;
    Button btnNextSchema = isRight ? rightBtnNextSchema : leftBtnNextSchema;
    Button btnLastSchema = isRight ? rightBtnLastSchema : leftBtnLastSchema;

    // Actual logic.
    int currentIndex = slcSchema.getSelectionModel().getSelectedIndex();
    int maxIndex = slcSchema.getItems().size() - 1; // Index out of bounds @ size.
    int minIndex = 0; // Index out of bounds @ -1.

    // back scroll buttons.
    boolean visible = currentIndex > minIndex;
    btnPrevSchema.setVisible(visible);
    btnFirstSchema.setVisible(visible);

    // forward scroll buttons.
    visible = currentIndex < maxIndex;
    btnNextSchema.setVisible(visible);
    btnLastSchema.setVisible(visible);
  }

  /**
   * Notifies the user about differences and highlights them within the code area.
   */

  private void highlightDifferences() {
    System.out.println(differences.getLeftDifferences());
    System.out.println(differences.getRightDifferences());
    if (differences.hasNone()) {
      leftCodeArea.clearStyle(0, leftCodeArea.getText().length() - 1);
      rightCodeArea.clearStyle(0, rightCodeArea.getText().length() - 1);
    }

    if (differences.hasLeftDifferences()) {
      highlightDifferences(leftCodeArea, differences.getLeftDifferences());
    }
    if (differences.hasRightDifferences()) {
      highlightDifferences(rightCodeArea, differences.getRightDifferences());
    }
  }


  /**
   * Highlights the differences of the given code area, i.e. changes the background color according
   * to the stylesheets.
   *
   * @param area        The area to modify.
   * @param differences The properties that differ.
   */
  private void highlightDifferences(CodeArea area, List<String> differences) {
    area.clearStyle(0, area.getText().length() - 1);
    String text = area.getText();

    // Traverse all differences.
    for (String difference : differences) {

      /*
       * Split the difference properties into individual strings. The : represents the value of the
       * property and should be ignored because it is on the same line as the property name.
       */
      difference = difference.replaceFirst("\\.", "").replaceAll(":", ".");
      String[] properties = difference.split("\\.");
      properties = Arrays.copyOf(properties, properties.length - 1);
      int from = 0;
      int to;

      /*
       * Calculate the ranges of highlighting and highlight these if they are either on the same
       * line or an array is given. Arrays are being highlighted as a whole.
       */
      for (int i = 0; i < properties.length; i++) {
        // boolean to determine a pre-exit on arrays. Mark the whole array.
        boolean exit = false;

        /*
         * If array is given get the range of the whole array.
         * Else get the range of the line from the property.
         */
        if (isInteger(properties[i])) {
          from = text.indexOf('[', from);
          to = text.indexOf(']', from) + 1;
          exit = true;
        } else {
          from = text.indexOf(properties[i], from) - 1;
          to = text.indexOf("\n", from);
          }

        // If the following content is an array highlight the property.
        if (i < properties.length - 1 && isInteger(properties[i+1])) {
          highlight(area, from, to);
        }

        // Highlight the last part.
        if (i == properties.length - 1 || exit) {
          highlight(area, from, to);
        }

        // If an array is given exit the highlighting
        if (exit) {
          break;
        }
      }
    }
  }

  /** Helper method to highlight the CodeArea in a given range. */
  private void highlight(CodeArea area, int from, int to) {
    try {
      area.setStyleClass(from, to, "highlight");
    } catch (IndexOutOfBoundsException e) {
      System.out.println(e.getMessage());
    }
  }

  private boolean isInteger(String string) {
    try {
      Integer.parseInt(string);
      return true;
    } catch (NumberFormatException e) {
      return false;
    }
  }

  /**
   * Saves the left and right dropdown selections to preferences. This is done to load the
   * content depending on what was used the last time.
   */
  private void saveSelection() {
      Preferences preferences = Preferences.userNodeForPackage(getClass());

      // Set the properties. save the file also.
      preferences.put("left", slcLeft.getValue());
      preferences.put("right", slcRight.getValue());
  }

  /**
   * Validates a given schema, i.e. checks whether the schema  has JSON Syntax, is of draft-04,
   * which it is required to be by the containment tools.
   *
   * @param schema The schema to validate.
   * @param side The side to specify the error message.
   * @return {@code true} for valid schema, {@code false} else.
   */
  private boolean validate(String schema, String side) {
    String error = ESystemConstants.ERROR.getValue() + "The " + side + " schema ";
    try {
      // Get the draft and the schema as JSON Objects. The draft can be found in the location above.
      JSONObject draft = new JSONObject(DRAFT);
      JSONObject objectSchema = new JSONObject(schema);

      // Validate schema vs draft.
      Schema draftSchema = SchemaLoader.load(draft);
      draftSchema.validate(objectSchema);
      return true;

    } catch (JSONException e) {
      notifyUser(error + "has no valid JSON syntax given.");
    } catch (SchemaException e) {
      notifyUser(ESystemConstants.ERROR.getValue() + "The draft-04 could not be loaded.");
    } catch (ValidationException e) {
      notifyUser(error + "is no valid JSON Schema draft-04");
    }
    return false;
  }
}
