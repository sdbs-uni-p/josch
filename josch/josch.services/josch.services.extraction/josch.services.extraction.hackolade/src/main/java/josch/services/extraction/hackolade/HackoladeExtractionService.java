package josch.services.extraction.hackolade;

import josch.model.dto.ExtractionDto;
import josch.model.dto.SettingsDto;
import josch.model.enums.ESystemConstants;
import josch.services.interfaces.AbstractShellService;
import josch.services.interfaces.IExtractionService;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * This {@code HackoladeExtractionService} is used to implement the methods to extract a valid JSON Schema of
 * draft-04 and validator for the underlying dbms by using the external Hackolade tool. In order to
 * use this tool a licence is required and the tool has to be installed. Furthermore a hackolade
 * system variable has to be set and the connection has to be setup with the same name as the
 * connectName (alias dbInstance).
 *
 * @author Kai Dauberschmidt
 * @see <a href="https://hackolade.com/">https://hackolade.com</a>
 */
public class HackoladeExtractionService extends AbstractShellService implements IExtractionService {

  /** Contains the settings for extraction. */
  private final ExtractionDto DTO;

  /** The string to start the cli executions with. */
  private final String START_STATEMENT;

  /** The container name */
  private final String CONTAINER;

  /** The entity name */
  private final String ENTITY;

  /**
   * The path to the root directory in which the files are saved.
   */
  private final Path PATH;

  /** The schema path in which the schemas are stored. */
  private final Path SCHEMA_PATH;

  /** The validator path in which the validators are stored. */
  private final Path VALIDATOR_PATH;

  /** Constructs an HackoladeExtractionService with a given extraction dto. */
  public HackoladeExtractionService(SettingsDto dto) {
    this.DTO = dto.getExtraction();
    this.CONTAINER = DTO.getContainer();
    this.ENTITY = DTO.getEntity();

    // The string to start the cli executions with depends on the OS.
    this.START_STATEMENT = "hackolade";

    PATH = Paths.get(System.getProperty("user.home") + "/josch");
    SCHEMA_PATH = PATH.resolve("rawSchema - JSON Schema/" + CONTAINER);
    VALIDATOR_PATH = PATH.resolve("rawSchema - MongoDB Script/" + CONTAINER);

    try {
      createDirectories(PATH, false);
    } catch (IOException e) {
      System.err.println(e.getMessage());
    }
  }

  /** {@inheritDoc} */
  @Override
  public String getJsonSchema() {

    // Reverse engineering and early return on errors.
    String notification = reverseEngineer();
    if (!notification.equals(ESystemConstants.SUCCESS.getValue())) {
      return notification;
    }

    // Forward engineering and early return on errors.
    notification = forwardEngineer(EOutputType.JSONSCHEMA);
    if (!notification.equals(ESystemConstants.SUCCESS.getValue())) {
      return notification;
    }

    // Load and return the JSON String or the error message.
    return loadJsonString(SCHEMA_PATH.resolve(ENTITY + ".json"));
  }

  /** {@inheritDoc} */
  @Override
  public String getValidator() {

    // Reverse engineering and early return on errors.
    String notification = reverseEngineer();
    if (!notification.equals(ESystemConstants.SUCCESS.getValue())) {
      return notification;
    }

    // Forward engineering and early return on errors.
    notification = forwardEngineer(EOutputType.SCRIPT);
    if (!notification.equals(ESystemConstants.SUCCESS.getValue())) {
      return notification;
    }

    // Load and return the JSON String or the error message.
    return getValidatorFromJS(VALIDATOR_PATH.resolve(ENTITY + ".js"));
  }

  /**
   * Builds Hackolades RevEng CLI command and executes it on the shell. Also reads the Shell to
   * determine when the task is complete. The file is stored in the user's home directory within the
   * folder "josch".
   *
   * @return A status message regarding success or failure of the execution of the command.
   */
  private String reverseEngineer() {

    // Conversion to Hackolade requirements.
    String samplingMethod = DTO.getMethod();
    samplingMethod = (samplingMethod.equals("absolute")) ? "abs" : "rel";

    // The actual command.
    ArrayList<String> cmd = new ArrayList<>();
    cmd.add(START_STATEMENT);
    cmd.add("revEng");
    cmd.add("--target=" + DTO.getTarget()); // The target for model.
    cmd.add("--connectName=" + DTO.getDbInstance()); // The hackolade name for the db server.
    cmd.add("--model=" + PATH.resolve("rawSchema.json")); // The output file.
    cmd.add("--samplingValue=" + DTO.getSize()); // Sampling size.
    cmd.add("--samplingMethod=" + samplingMethod);  // Sampling method.
    cmd.add("--selectedObjects=\"" + CONTAINER + ":[" + ENTITY + "]\"");
    return executeCommand(cmd.toArray(String[]::new));
  }

  /**
   * Builds the forwEng CLI command from Hackolade and executes it on the shell. also reads the
   * command to determine the completion of the task. Depending on the {@code outputType} given the
   * file will be stored in home/josch/ for script or in home/josch/schema for jsonschema.
   *
   * @param outputType The outputType of the forward engineering.
   * @return A message indicating its success or failure.
   */
  private String forwardEngineer(EOutputType outputType) {

    // The command to execute.
    ArrayList<String> cmd = new ArrayList<>();
    cmd.add(START_STATEMENT);
    cmd.add("forwEng");
    cmd.add("--model=" + PATH.resolve("rawSchema.json"));  // The model to work on.
    cmd.add("--path=" + PATH); // The path to save the output to.
    cmd.add("--outputType=" + outputType.toString());

    return executeCommand(cmd.toArray(String[]::new));
  }

  /**
   * Extracts the validator part from a JavaScript File and returns it as a JSON String.
   *
   * @param path The path and .js file to extract from.
   * @return The JSON String representation of the validator.
   */
  private String getValidatorFromJS(Path path) {
    File javaScript = path.toFile();
    String notification;
    String line;
    try {
      Scanner scanner = new Scanner(javaScript);
      boolean isPartOfValidator = false;
      StringBuilder validator = new StringBuilder();

      while (scanner.hasNextLine()) {
        line = scanner.nextLine(); // read the current line;

        // Append parts of the validator to the validator.
        if (line.contains("validationLevel") || line.contains("validationAction")) {
          break;
        } else if (isPartOfValidator) {
          validator.append(line);
        } else if (line.contains("\"validator\": {")) {
          validator.append("{");
          isPartOfValidator = true;
        }
      }

      // Formatting.
      notification = validator.toString();
      int lastIndex = notification.length() - 1;
      if (notification.length() > 0 && notification.charAt(lastIndex) == ',') {
        notification = notification.substring(0, lastIndex); // Exclusive at the end.
      }
      scanner.close();

      // delete the .js file.
      delete(path);

    } catch (IOException e) {
      e.printStackTrace();
      notification = "Error! " + e.getMessage();
    }

    return notification;
  }

  /**
   * Gets the success or error message from the process while executing a shell (cli) command.
   *
   * @param p The process that executes the command.
   * @return The success or error message that is printed in the shell.
   */
  @Override
  protected String getMessage(Process p) {
    BufferedReader shellReader = new BufferedReader(new InputStreamReader(p.getInputStream()));
    String shellLine;

    // try to read the lines from the shell.
    do {
      try {
        shellLine = shellReader.readLine();
      } catch (IOException e) { // report error message.
        shellLine = ESystemConstants.ERROR.getValue() + e.getMessage();
        e.printStackTrace();
      }
    } while ((shellLine.contains("processing") && !shellLine.toLowerCase().contains("success"))
            || shellLine.isEmpty());

    // If it wasn't successful throw an illegal argument exception.
    if (shellLine.equals("Model saved.") || shellLine.toLowerCase().contains("success")) {
      shellLine = ESystemConstants.SUCCESS.getValue();
    } else {
      throw new IllegalArgumentException(shellLine);
    }
    return shellLine;
  }
}
