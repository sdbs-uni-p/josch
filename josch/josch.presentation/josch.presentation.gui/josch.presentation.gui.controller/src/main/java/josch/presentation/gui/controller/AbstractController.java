package josch.presentation.gui.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;
import javafx.application.Platform;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.DatePicker;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import josch.model.dto.SettingsDto;
import josch.model.dto.ValidationDto;
import josch.model.enums.ESystemConstants;
import josch.presentation.gui.App;
import org.fxmisc.flowless.VirtualizedScrollPane;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.GenericStyledArea;
import org.fxmisc.richtext.LineNumberFactory;
import org.fxmisc.richtext.model.Paragraph;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;
import org.fxmisc.richtext.model.StyledSegment;
import org.reactfx.collection.ListModification;

import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Collection;
import java.util.Collections;
import java.util.ResourceBundle;
import java.util.concurrent.Callable;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * This controller class is the super class for all controllers. It is used in the
 * AbstractControllerFactory to return a controller of a specific type.
 *
 * @author Kai Dauberschmidt
 */
public abstract class AbstractController implements Initializable {

  /** The pattern to match names of JSON objects */
  private static final String NAME_PATTERN = "\"([^\"\\\\]|\\\\.)*\":";

  /** The pattern to match parentheses */
  private static final String PARENTHESIS_PATTERN = "[()]";

  /** The pattern to match braces */
  private static final String BRACE_PATTERN = "[{}]";

  /** The pattern to match brackets */
  private static final String BRACKET_PATTERN = "[\\[\\]]";

  /** The pattern to match semicolons */
  private static final String SEMICOLON_PATTERN = ";";

  /** The pattern to match strings */
  private static final String STRING_PATTERN = "\"([^\"\\\\]|\\\\.)*\"";

  /** The pattern to match numbers */
  private static final String NUMBER_PATTERN = "\\d";

  private static final String DEFAULT_PATTERN = ".";

  /** The global pattern for JSON */
  private static final Pattern PATTERN =
      Pattern.compile(
          "(?<NAME>"
              + NAME_PATTERN
              + ")"
              + "|(?<PARENTHESIS>"
              + PARENTHESIS_PATTERN
              + ")"
              + "|(?<BRACE>"
              + BRACE_PATTERN
              + ")"
              + "|(?<BRACKET>"
              + BRACKET_PATTERN
              + ")"
              + "|(?<SEMICOLON>"
              + SEMICOLON_PATTERN
              + ")"
              + "|(?<STRING>"
              + STRING_PATTERN
              + ")"
              + "|(?<NUMBER>"
              + NUMBER_PATTERN
              + ")"
              + "|(?<DEFAULT>"
              + DEFAULT_PATTERN
              + ")");

  /**
   * Computes syntax highlighting for JSON Objects on a given text and returns its style spans.
   *
   * @param text The text to compute the highlighting on.
   * @return Essentially a list of given styles that span a certain length.
   */
  private StyleSpans<Collection<String>> computeHighlighting(String text) {

    // Get the patterns for styling. */
    Matcher matcher = PATTERN.matcher(text);
    int lastNameIndex = 0;
    StyleSpansBuilder<Collection<String>> spansBuilder = new StyleSpansBuilder<>();

    // Find the different keywords for a JSON Object and style them accordingly. */
    while (matcher.find()) {
      String styleClass =
          matcher.group("NAME") != null
              ? "name"
              : matcher.group("PARENTHESIS") != null
                  ? "parenthesis"
                  : matcher.group("BRACE") != null
                      ? "brace"
                      : matcher.group("BRACKET") != null
                          ? "bracket"
                          : matcher.group("SEMICOLON") != null
                              ? "semicolon"
                              : matcher.group("STRING") != null
                                  ? "string"
                                  : matcher.group("NUMBER") != null
                                      ? "number"
                                      : matcher.group("DEFAULT") != null ? "default" : null;
      assert styleClass != null;

      spansBuilder.add(Collections.emptyList(), matcher.start() - lastNameIndex);
      spansBuilder.add(Collections.singleton(styleClass), matcher.end() - matcher.start());
      lastNameIndex = matcher.end();
    }
    spansBuilder.add(Collections.emptyList(), text.length() - lastNameIndex);
    return spansBuilder.create();
  }

  /**
   * Called to initialize a controller after its root element has been completely processed. This
   * initializes the view dynamically depending on the implementation of the method.
   *
   * @param url The location used to resolve relatives paths for the root object or null if unknown.
   * @param rb The resources used to localize the root object or null if not localized.
   */
  @Override
  public void initialize(URL url, ResourceBundle rb) {}

  /**
   * Called to pretty up an ugly JSON String using GSON.
   *
   * @param str The string to pretty up.
   * @return A pretty string, i.e. a human readable JSON string.
   */
  static String pretty(String str) {
    Gson gson = new GsonBuilder().setPrettyPrinting().create();
    return gson.toJson(JsonParser.parseString(str));
  }

  /**
   * Initializes a given code area. The code area does have line numbers on the left side, syntax
   * highlighting and auto-indent. This is being achieved within this method.
   *
   * @param area The {@code CodeArea} to initialize.
   */
  void initializeCodeArea(CodeArea area) {

    // Add Line numbers to the left of the area.
    area.setParagraphGraphicFactory(LineNumberFactory.get(area));

    // Compute syntax highlighting.
    area.getVisibleParagraphs()
        .addModificationObserver(new Highlighter<>(area, this::computeHighlighting));

    // Add auto-indent: insert the previous line's indents on enter.
    final Pattern whiteSpace = Pattern.compile("^\\s+");
    area.addEventHandler(
        KeyEvent.KEY_PRESSED,
        KE -> {
          if (KE.getCode() == KeyCode.ENTER) {
            int caret = area.getCaretPosition();
            int paragraph = area.getCurrentParagraph();
            Matcher matcher =
                whiteSpace.matcher(area.getParagraph(paragraph - 1).getSegments().get(0));
            if (matcher.find()) Platform.runLater(() -> area.insertText(caret, matcher.group()));
          }
        });
  }

  /**
   * This method is used for the execution of a background process {@code backgroundProcess}.
   *
   * @param backgroundProcess The method that implements the background process.
   * @see DialogueController#process(Callable, Callable)
   */
  void execute(Callable<String> backgroundProcess) {
    DialogueController.process(backgroundProcess, null);
  }

  /**
   * This method is used for the confirmation before executing a background process. Note that
   * while currently only validateAll() requires a confirmation before execution and hence
   * the {@code backgroundProcess} could be inlined null there are many possible scenarios in which
   * this could be expanded and thus it is not being inlined.
   *
   * @param confirmationMessage The message to show to the user.
   * @param backgroundProcess The background process to execute on confirmation.
   * @see DialogueController#confirmProcess(String, Callable, Callable)
   */
  @SuppressWarnings("SameParameterValue")
  void confirmExecute(String confirmationMessage, Callable<String> backgroundProcess, Callable<ValidationDto> validationProcess) {
    DialogueController.confirmProcess(confirmationMessage, backgroundProcess, validationProcess);
  }

  /**
   * This method is used to notify users with a given message.
   *
   * @param message The message for the user.
   * @see DialogueController#inform(String)
   */
  void notifyUser(String message) {
    DialogueController.inform(message);
  }

  /** handles exceptions by notifying the user about what went wrong. */
  void handle(Exception e) {
    e.printStackTrace();
    String message = ESystemConstants.ERROR.getValue() + e.getMessage();
    notifyUser(message);
  }

  /**
   * Gets the date of the specified DatePicker, or {@code null} if none exists. Note that the
   * Datepicker fields can be accessed by two Methods:
   *
   * <ul>
   *   <li>{@code datePicker.getValue()} (short {@code getValue()}). This gets the {@code LocalDate}
   *       of the field, if it's a valid date, or {@code null} else.
   *   <li>{@code .getEditor().textProperty().getValue()} (short {@code getText()}). This gets the
   *       String representation of the field.
   * </ul>
   *
   * <p>And these Datepicker fields have three possible cases of data input:
   *
   * <ul>
   *   <li><b>case 1:</b> Date pick by mouse click. In this case {@code getValue()} returns a proper
   *       LocalDate and {@code getText()} returns a string representation of the date, e.g.
   *       "01.01.2020".
   *   <li><b>case 2:</b> manual date entry <i>without</i> hitting enter. In this case {@code
   *       getValue()} will ignore whatever is inside the field and will return the previous value
   *       or {@code null}, if none existed earlier.
   *   <li><b>case 3:</b> manual date entry <i>with</i> hitting enter. It behaves pretty much like
   *       case 1, except for invalid data will be rendered to the previous date.
   * </ul>
   *
   * <p>I consider <em>case 2</em> a crucial quality-of-life feature that might also lead to a lot
   * of unwanted filtering. Therefore this method uses the {@code getText()} to access the
   * DatePicker's values. These string representations have to be parsed to a LocalDate.
   *
   * @param datePicker The date field to get the date from.
   * @return the {@code LocalDate} of the field or {@code null} if none exists.
   */
  LocalDate getDate(DatePicker datePicker) {
    LocalDate date;

    // Get the string representation.
    String dateString = datePicker.getEditor().textProperty().getValue();

    // Attempt to parse ISO date notation.
    try {
      date = LocalDate.parse(dateString);
    } catch (DateTimeParseException e0) {

      // Attempt to parse an European date notation.
      DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
      try {
        date = LocalDate.parse(dateString, formatter);
      } catch (DateTimeParseException e1) {
        date = null;
      }
    }
    return date;
  }

  /**
   * Gets the system's settings. These are being held within the MainController as a dto. However
   * this is being hidden.
   *
   * @return The system's settings as a dto.
   */
  public SettingsDto getSettings() {
    return MainController.getInstance().getSettingsDto();
  }

  /**
   * Sets the system's settings. These are being held within the MainController as a dto.
   *
   * @param settingsDto The concrete settings as a dto object.
   */
  public void setSettings(SettingsDto settingsDto) {
    MainController.getInstance().setSettingsDto(settingsDto);
  }

  /**
   * Shows documents of a collection in a scrollable pop up window.
   * The window is created on the fly within this method.
   */
  public void showDocuments(String documents, String cpt) {

    // The popup window
    Stage popShowAll = new Stage();
    popShowAll.setTitle(cpt);

    // The layout
    CodeArea docs = new CodeArea();
    initializeCodeArea(docs);
    docs.setWrapText(true);
    docs.editableProperty().setValue(false);
    VirtualizedScrollPane<CodeArea> pane = new VirtualizedScrollPane<>(docs);
    VBox.setVgrow(pane, Priority.ALWAYS);
    VBox layout = new VBox(pane);
    layout.getStyleClass().removeAll();
    layout.getStyleClass().add("bg-white");
    Scene showAllDocs = new Scene(layout, 600, 400);
    showAllDocs.getStylesheets().add(App.class.getResource("view/css/main.css").toExternalForm());
    showAllDocs.getStylesheets().add(App.class.getResource("view/css/text.css").toExternalForm());
    // Add the documents to the view.
    docs.replaceText(documents);
    docs.getStyleClass().add("docs");

    // Show the popup.
    popShowAll.setScene(showAllDocs);
    popShowAll.showAndWait();
  }

  /**
   * This class is used to syntax highlight code areas in JSON style.
   *
   * @param <PS> The paragraph style, e.g. alignment.
   * @param <SEG> The segment.
   * @param <S> The actual styling, e.g. colors.
   */
  @SuppressWarnings("ALL")
  private static class Highlighter<PS, SEG, S> implements Consumer<ListModification> {
    /** CodeArea is a child of this */
    private final GenericStyledArea<PS, SEG, S> area;

    /** The function to compute the styles */
    private final Function<String, StyleSpans<S>> computeStyles;

    /** The previous paragraph index */
    private int prevParagraph;

    /** The previous paragraph's text length */
    private int prevLength;

    /** Constructs a Highlighter with a given area and highlight function. */
    public Highlighter(
        GenericStyledArea<PS, SEG, S> area, Function<String, StyleSpans<S>> computeStyles) {
      this.computeStyles = computeStyles;
      this.area = area;
    }

    /**
     * Performs the computeStyles function on the modifications.
     *
     * @param modifications Modifications to the text of the code area.
     */
    @Override
    @SuppressWarnings("rawtypes")
    public void accept(ListModification modifications) {
      if (modifications.getAddedSize() > 0) {

        // if the modification is highlighted do nothing.
        Paragraph par = (Paragraph) modifications.getAddedSubList().get(0);
        Stream<StyledSegment<SEG, S>> stream = par.getStyledSegments().stream();
        boolean isHighlighted =
            stream.anyMatch(ss -> ss.getStyle().toString().contains("highlight"));

        if (isHighlighted) {
          return;
        }

        // Get the current paragraph.
        int paragraph =
            Math.min(
                area.firstVisibleParToAllParIndex() + modifications.getFrom(),
                area.getParagraphs().size() - 1);

        // Get the current text.
        String text = area.getText(paragraph, 0, paragraph, area.getParagraphLength(paragraph));

        // if the paragraph differs set the style.
        if (paragraph != prevParagraph || text.length() != prevLength) {
          int startPos = area.getAbsolutePosition(paragraph, 0);
          Platform.runLater(() -> area.setStyleSpans(startPos, computeStyles.apply(text)));
          prevLength = text.length();
          prevParagraph = paragraph;
        }
      }
    }
  }
}
