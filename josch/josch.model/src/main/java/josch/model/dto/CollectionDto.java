package josch.model.dto;

import org.bson.Document;

import java.util.HashMap;
import java.util.Map;

/**
 * The {@code CollectionDto} encapsulates the attributes of a given collection. A collection has a
 * name, a count of documents and optionally an already registered validator.
 *
 * @author Kai Dauberschmidt
 */
public class CollectionDto extends AbstractDto {

  /** The name of the collection */
  private String name;

  /** The amount of documents within the collection */
  private long count;

  /** The validator if existent */
  private Map<String, String> validationOptions;

  /** A document from the collection */
  private Document document;

  /** Checks whether a validator exists and is not empty. */
  public boolean hasValidator() {
    boolean exists = validationOptions != null;
    if (exists) {
      exists = !validationOptions.get("validator").equals("");
    }
    return exists;
  }

  /**
   * Gets the {@code name}
   *
   * @return The value of {@code name}
   */
  public String getName() {
    return name;
  }

  /**
   * Sets the {@code name}.
   *
   * @param name The concrete value of {@code name}.
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * Gets the {@code count}
   *
   * @return The value of {@code count}
   */
  public long getCount() {
    return count;
  }

  /**
   * Sets the {@code count}.
   *
   * @param count The concrete value of {@code count}.
   */
  public void setCount(long count) {
    this.count = count;
  }

  /**
   * Gets the {@code validator}
   *
   * @return The value of {@code validator}
   */
  public String getValidator() {
    return getValidationOptions("validator");
  }

  /**
   * Sets the {@code validator}.
   *
   * @param validator The concrete value of {@code validator}.
   */
  public void setValidator(String validator) {
    setValidationOptions("validator", validator);
  }

  /**
   * Gets the validation level.
   *
   * @return The concrete validation level.
   */
  public String getValidationLevel() {
    return getValidationOptions("level");
  }

  /**
   * Sets the validation level.
   *
   * @param level The concrete validation level.
   */
  public void setValidationLevel(String level) {
    setValidationOptions("level", level);
  }

  /**
   * Gets the validation action.
   *
   * @return The concrete validation action.
   */
  public String getValidationAction() {
    return getValidationOptions("action");
  }

  /**
   * Sets the validation action.
   *
   * @param action The concrete validation action.
   */
  public void setValidationAction(String action) {
    setValidationOptions("action", action);
  }

  /**
   * Inserts validation option (option, value) tuples into the {@code validationOptions} map.
   *
   * @param option The validation option to set.
   * @param value The value of the option to set.
   */
  private void setValidationOptions(String option, String value) {
    // if none exists yet create new options.
    if (validationOptions == null) {
      validationOptions = new HashMap<>();
    }

    // if the option does exist remove it.
    if (getValidator() != null) {
      validationOptions.remove(option);
    }

    // put the new option and its value into the map.
    validationOptions.put(option, value);
  }

  /** Gets the value of the validation option */
  private String getValidationOptions(String option) {
    if (validationOptions != null) {
      return validationOptions.get(option);
    } else {
      return null;
    }
  }

  /**
   * Gets the {@code document}
   *
   * @return The value of {@code document}
   */
  public Document getDocument() {
    return document;
  }

  /**
   * Sets the {@code document}.
   *
   * @param document The concrete value of {@code document}.
   */
  public void setDocument(Document document) {
    this.document = document;
  }
}
