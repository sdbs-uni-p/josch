package josch.services.interfaces;

/**
 * This {@code IExtractionService} provides an interface to interact with different implementations
 * of external extraction tools. An extraction tool is used to generate both a JSON Schema as well
 * as a validator from a given collection in a database.
 *
 * @author Kai Dauberschmidt
 */
public interface IExtractionService {

  /**
   * Extract a <em>valid</em> JSON Schema from a given {@code collection} in the database by
   * sampling a given amount of documents within the {@code collection} and return it as a {@code
   * JSONObject}.
   *
   * @return The <em>valid</em> JSON Schema as a {@code JSONObject}.
   */
  String getJsonSchema();

  /**
   * Creates a validator from a given {@code collection} in the database by sampling a given amount
   * of documents within the {@code collection} and return it as a {@code JSONObject}. A validator
   * is valid, when it can be used for validation within the collection.
   *
   * @return A validator as a {@code JSONObject}.
   */
  String getValidator();
}
