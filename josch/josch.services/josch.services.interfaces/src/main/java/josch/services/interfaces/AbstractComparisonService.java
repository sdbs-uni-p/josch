package josch.services.interfaces;

import com.google.common.collect.MapDifference;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import josch.model.dto.DifferenceDto;
import josch.model.dto.SettingsDto;
import josch.model.enums.ESystemConstants;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.nio.file.Paths;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * This {@code AbstractComparisonService} serves as a interface for the comparison of two schemas.
 * It is used to compare two schemas semantically and syntactically. An abstract class is chosen
 * over an actual interface, because the actual implementations share details like the storage of
 * files, the syntax equality check or interacting with the shell.
 */
public abstract class AbstractComparisonService extends AbstractShellService {

  protected final SettingsDto SETTINGS;

  public AbstractComparisonService(SettingsDto settings) {
    this.SETTINGS = settings;
  }

  /**
   * Checks whether two schemas are in a containment relationship and returns the relationship type
   * as a String. Let S,R be two schemas, then the following containment relationships possibly
   * exist:
   *
   * <ul>
   *   <li>S is subset of R
   *   <li>S is superset of R
   *   <li>S is semantically equivalent to R
   *   <li>S is contrary to R
   * </ul>
   *
   * @param current The current schema (left side)
   * @param legacy The legacy schema (right side)
   * @return The containment relationship.
   */
  public String contains(String current, String legacy) {
    String result = storeSchemas(current, legacy);

    // If storage was successful the operation can be executed.
    if (result.equals(ESystemConstants.SUCCESS.getValue())) {

      // Do the comparison.
      result = runScript();

      // Read its result
      if (result == null) {
        result = "An error occurred in the tool " + SETTINGS.getComparison().getName()
                + " while comparing these schemas.";
      } else if (result.equals(ESystemConstants.SUCCESS.getValue())) {
        result = readResult();
      }
    }
    return result;
  }

  /**
   * Executes a script with a given relative path. The path is relative to the
   * josch.logic.containment module that holds the different scripts.
   *
   * @return A message that indicates success or failure.
   */
  public abstract String runScript();

  /**
   * Stores the given schemas at the path registered in the System Constants.
   *
   * @param current The current schema (left side of the view)
   * @param legacy The legacy schema (right side of the view)
   * @return A message indicating success or failure;
   */
  private String storeSchemas(String current, String legacy) {
    String success = storeFile(current, Paths.get(ESystemConstants.SCHEMA_CURRENT_PATH.getValue()));

    // Check the legacy.
    if (success.equals(ESystemConstants.SUCCESS.getValue())) {
      success = storeFile(legacy, Paths.get(ESystemConstants.SCHEMA_LEGACY_PATH.getValue()));
    }
    return success;
  }

  /**
   * Flattens a given map using the JSON 'dot' notation.
   *
   * @param map The map to flatten.
   * @return The flattened Map.
   */
  private static Map<String, Object> flatten(Map<String, Object> map) {
    return map.entrySet().stream()
        .flatMap(AbstractComparisonService::flatten)
        .collect(
            HashMap::new,
            (hashMap, entry) -> hashMap.put("." + entry.getKey(), entry.getValue()),
            HashMap::putAll);
  }

  /**
   * Resolves the arguments for the scripts as an array. The arguments are the current schema path,
   * the the legacy schema path and the result path.
   *
   * @return The arguments as an argument string array.
   */
  protected String[] getArguments() {
    String[] arguments = new String[3];
    arguments[0] = ESystemConstants.SCHEMA_CURRENT_PATH.getValue();
    arguments[1] = ESystemConstants.SCHEMA_LEGACY_PATH.getValue();
    arguments[2] = ESystemConstants.SCHEMA_RESULT_PATH.getValue();
    return arguments;
  }

  /**
   * Reads the result value from the respective result.json file found in {@code ESystemConstants}.
   *
   * @return The value of the containment result.
   */
  private String readResult() {
    String path = ESystemConstants.SCHEMA_RESULT_PATH.getValue();
    // Read the file
    try (InputStream inputStream = new FileInputStream(path)) {
      JSONObject resultObject = new JSONObject(new JSONTokener(inputStream));
      return resultObject.getString("result");
    } catch (IOException e) {
      e.printStackTrace();
      return ESystemConstants.ERROR.getValue() + e.getMessage();
    }
  }

  /**
   * Flattens a given map entry to a stream of map entries.
   *
   * @param entry the given map entry.
   * @return The stream of map entries.
   */
  private static Stream<Map.Entry<String, Object>> flatten(Map.Entry<String, Object> entry) {
    if (entry == null) {
      return Stream.empty();
    }

    // If the entry is another map return a sequential stream of its flattened map entries.
    if (entry.getValue() instanceof Map<?, ?>) {
      return ((Map<?, ?>) entry.getValue())
          .entrySet().stream()
              .flatMap(
                  mapEntry ->
                      flatten(
                          new AbstractMap.SimpleEntry<>(
                              entry.getKey() + "." + mapEntry.getKey(), mapEntry.getValue())));
    }

    // If the entry is a list get it and flatten its entries.
    if (entry.getValue() instanceof List<?>) {
      List<?> list = (List<?>) entry.getValue();
      return IntStream.range(0, list.size())
          .mapToObj(
              i ->
                  new AbstractMap.SimpleEntry<String, Object>(
                      entry.getKey() + "." + i, list.get(i)))
          .flatMap(AbstractComparisonService::flatten);
    }

    return Stream.of(entry);
  }

  /**
   * Checks whether the two given Schemas are syntactically equal or not. Equality is commutative,
   * so (A = B) = (B = A) and therefore the order of input does not matter at all. Two schemas are
   * syntactically equal when they do have the same keys with the same values but as schemas can be
   * considered sets in terms of math the keys and values do not have to be within the same order.
   *
   * @param schema The first schema.
   * @param other The other schema.
   * @return {@code equal} when they are equal or the difference.
   */
  public DifferenceDto equals(String schema, String other) {
    // Basic setup
    Gson gson = new Gson();
    Type mapType = new TypeToken<Map<String, Object>>() {}.getType();

    // Make flat maps from schemas.
    Map<String, Object> leftMap = gson.fromJson(schema, mapType);
    leftMap = flatten(leftMap);
    Map<String, Object> rightMap = gson.fromJson(other, mapType);
    rightMap = flatten(rightMap);

    // Get the differences.
    DifferenceDto dto = new DifferenceDto();
    MapDifference<String, Object> differences = Maps.difference(leftMap, rightMap);

    // Left only differences.
    differences.entriesOnlyOnLeft().forEach((key, value) -> dto.addLeft(key + ": " + value));

    // Right only differences.
    differences.entriesOnlyOnRight().forEach((key, value) -> dto.addRight(key + ": " + value));

    // Both differences:
    differences
        .entriesDiffering()
        .forEach(
            (key, value) -> {
              String difference = key + ": " + value;
              dto.addLeft(difference);
              dto.addRight(difference);
            });

    return dto;
  }
}
