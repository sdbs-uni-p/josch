package josch.services.comparison.jsonsubschema;

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
 * This is the implementation of the {@code AbstractComparisonService} that uses the JsonSubSchema
 * (jss) tool for schema containment.
 *
 * @author Kai Dauberschmidt
 */
public class JssComparisonService extends AbstractComparisonService {

  /**
   * Constructs a ComparisonService
   */
  public JssComparisonService(SettingsDto settings) {
    super(settings);
  }

  /** {@inheritDoc} */
  @Override
  public String runScript() {
    // Build the command
    ArrayList<String> cmd = new ArrayList<>();
    cmd.add("pipenv");
    cmd.add("run");
    cmd.add("python");
    cmd.add("i_jss.py");

    // Install command to be sure.
    ArrayList<String> install = new ArrayList<>();
    install.add("pipenv");
    install.add("install");

    // Add its arguments.
    cmd.addAll(Arrays.asList(getArguments()));

    // Resolve the directory to execute in.
    File directory = Paths.get(SETTINGS.getJssPath()).toFile();

    // Execute the command and wait for its result.
    executeCommand(install.toArray(String[]::new), directory);
    return executeCommand(cmd.toArray(String[]::new), directory);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected String getMessage(Process p) {
    BufferedReader shellReader = new BufferedReader(new InputStreamReader(p.getInputStream()));
    try {
      return shellReader.readLine();
    } catch (IOException e) {
      e.printStackTrace();
      return ESystemConstants.ERROR.getValue() + e.getMessage();
    }
  }
}
