package josch.services.comparison.isjsonsubset;

import josch.model.dto.SettingsDto;
import josch.model.enums.ESystemConstants;
import josch.services.interfaces.AbstractComparisonService;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * This is the implementation of the {@code AbstractComparisonService} that uses the Is Json Schema
 * Subset (ijs subset or short ijs) tool for schema containment.
 *
 * @author Kai Dauberschmidt
 */
public class IjsComparisonService extends AbstractComparisonService {

  /**
   * Constructs a concrete service with the settings
   */
  public IjsComparisonService(SettingsDto settings) {
    super(settings);
  }

  /** {@inheritDoc} */
  @Override
  public String runScript() {
    // The actual command, Determine the OS -> Windows needs .cmd call
    boolean isWindows = System.getProperty("os.name").toLowerCase().startsWith("windows");
    ArrayList<String> installCmd = new ArrayList<>();
    installCmd.add(isWindows ? "yarn.cmd" : "yarn");
    installCmd.add("install");

    ArrayList<String> cmd = new ArrayList<>();
    cmd.add(isWindows ? "yarn.cmd" : "yarn");
    cmd.add("run");
    cmd.add("start");


    // The arguments for the command
    cmd.addAll(Arrays.asList(getArguments()));

    // The directory of the module in which the yarn specs are in.
    File directory = Paths.get(SETTINGS.getIjsPath()).toFile();

    // Execute the command and wait for its result.
    executeCommand(installCmd.toArray(String[]::new), directory);
    return executeCommand(cmd.toArray(String[]::new), directory);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected String getMessage(Process p) {
    // Read the shell until the process is finished or an exception occurred.
    String shellLine;
    BufferedReader shellReader = new BufferedReader(new InputStreamReader(p.getInputStream()));
    do {
      try {
        shellLine = shellReader.readLine();
      } catch (IOException e) {
        e.printStackTrace();
        return ESystemConstants.ERROR.getValue() + e.getMessage();
      }
    } while (!(shellLine.contains("Done")
            || shellLine.equals(ESystemConstants.SUCCESS.getValue())));
    return shellLine;
  }
}
