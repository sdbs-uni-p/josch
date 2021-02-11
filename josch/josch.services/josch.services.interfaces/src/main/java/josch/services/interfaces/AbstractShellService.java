package josch.services.interfaces;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import josch.model.enums.ESystemConstants;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * This {@code AbstractShellService} is used to let services interact with the actual environment.
 * It is used when a service needs to access a file or needs to access the operating system's
 * command line.
 *
 * @author Kai Dauberschmidt
 */
public abstract class AbstractShellService {

  /** A success message for successful execution. */
  protected static final String SUCCESS = "execution was successful.";

  /**
   * Creates the directories along the given path.
   *
   * @param path The directories to create.
   * @param hasFile A boolean to decide whether the path ends at a file or directory.
   * @throws IOException When they cannot be created.
   */
  protected static void createDirectories(Path path, boolean hasFile) throws IOException {
    if (hasFile) {
      path = path.getParent();
    }
    File rootPath = path.toFile();
    if (!rootPath.exists()) {
      if (!rootPath.mkdirs()) {
        throw new IOException("Failed to create directories.");
      }
    }
  }


  /**
   * Deletes a file from a given path as string. The path has to be converted to a Path object at
   * first before delete can be called.
   *
   * @param path the path to the file as a String.
   */
  protected static void delete(Path path) throws IOException {
    Files.delete(path);
  }

  /**
   * Gets the success or error message from the process while executing a shell (cli) command. This
   * is realized using a do-while but the conditions can be quite different and thus this is
   * declared abstract - even though the implementations might seem similar.
   *
   * @param p The process that executes the command.
   * @return The success or error message that is printed in the shell.
   */
  protected abstract String getMessage(Process p);

  /**
   * Loads a JSON String from a .json file using the GSON framework and returns it.
   *
   * @param path the path to the .json file
   * @return the JSON String representation of the file.
   */
  protected static String loadJsonString(Path path) {
    Gson gson = new Gson();
    String pathString = path.toString();

    try (FileReader fileReader = new FileReader(pathString)) {

      // Make an JsonObject out if the file.
      JsonObject json = JsonParser.parseReader(fileReader).getAsJsonObject();

      // Check properties._id.
      JsonObject id = json.getAsJsonObject("properties").getAsJsonObject("_id");
      if (id != null) {
        JsonElement type = id.get("type");

        // Check for pattern.
        if (id.has("pattern")) {
          String pattern = id.get("pattern").getAsString();
          String typeValue = type.getAsString();

          // If ObjectId given.
          if (typeValue.equals("string") && pattern.trim().equals("^[a-fA-F0-9]{24}$")) {
            id.remove("pattern");
            id.remove("type");
            id.addProperty("type", "object");
          }
        }
      }

      // Replace integers with numbers because this has led to failed validation quite often.
      return json.toString().replace("integer", "number");

     } catch (IOException e) {
      e.printStackTrace();
      return "Error! " + e.getMessage();
      }
  }

  /**
   * Executes a given string command anywhere and returns its success or failure message.
   *
   * @param command The command to execute.
   * @return A message indicating its success or failure.
   */
  protected String executeCommand(String[] command) {
    return executeCommand(command, null);
  }

  /**
   * Executes a given string command and returns its success or failure message.
   *
   * @param command The command to execute.
   * @param directory The location of the execution as a file.
   * @return A message indicating its success or failure.
   */
  protected String executeCommand(String[] command, File directory) {
    String msg;
    Process process;

    // Print the command to the console.
    for (String arg : command) {
      System.out.print(arg);
      System.out.print(" ");
    }

    try {
      if (directory == null) {
        process = Runtime.getRuntime().exec(command);
      } else {
        process = Runtime.getRuntime().exec(command, null, directory);
      }
      msg = getMessage(process);
    } catch (IOException | IllegalArgumentException e) {
      e.printStackTrace();
      msg = ESystemConstants.ERROR.getValue() + e.getMessage();
    }
    return msg;
  }

  /**
   * Stores a given String content within the file specified at the path.
   *
   * @param content The String to store to the file.
   * @param path The path of the file.
   * @return A message to indicate success or failure.
   */
  public static String storeFile(String content, Path path) {

    // Get the directory of the files and create it if it does not exist yet.
    try {
      createDirectories(path, true);
    } catch (IOException e) {
      e.printStackTrace();
      return ESystemConstants.ERROR.getValue() + "Failed to create directories";
    }

    File contentFile = path.toFile();

    try {
      //noinspection ResultOfMethodCallIgnored
      contentFile.createNewFile();
      BufferedWriter writer = new BufferedWriter(new FileWriter(contentFile));
      writer.write(content);
      writer.close();
      return ESystemConstants.SUCCESS.getValue();
    } catch (IOException e) {
      e.printStackTrace();
      return ESystemConstants.ERROR.getValue() + "Failed to write the file.";
    }
  }
}
