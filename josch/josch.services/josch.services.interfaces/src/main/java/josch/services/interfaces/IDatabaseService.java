package josch.services.interfaces;

import josch.model.dto.CollectionDto;
import josch.model.dto.DatabaseDto;
import josch.model.dto.SchemaDto;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * This {@code IDatabaseService} provides an interface for different NoSQL systems to interact with
 * their databases and collections. The systems are built hierarchically: NoSQL system ⊇ database ⊇
 * collection ⊇ document, e.g. MongoDB ⊇ databases (like default: test) ⊇ collections (like
 * students) ⊇ documents (like 5301). And this interface is looking at the database layer, so not
 * the NoSQL system layer and their collections. <br>
 * The database methods are used when the user didn't specify any database during the connecting
 * process. It is then to assume the user doesn't know the system's structure and therefore wants to
 * view all databases on the specified server to select one from.
 *
 * @author Kai Dauberschmidt
 * @see CollectionDto
 */
public interface IDatabaseService {

  /**
   * Gets the collection with the specified name as a {@code CollectionDto}.
   *
   * @param collectionName The name of the collection to get.
   * @return A {@code CollectionDto} that encapsulates the collection.
   * @see CollectionDto
   */
  CollectionDto getCollection(String collectionName);

  /**
   * Gets all collections within the given database.
   *
   * @return a list of {@code CollectionDto}s that encapsulate each collection of a database.
   */
  List<CollectionDto> getAllCollections();

  /**
   * Gets all collections for a given database name.
   *
   * @param database The name of the database to get its collections.
   * @return a list of {@code CollectionDto}s that encapsulate each collection of a database.
   */
  List<CollectionDto> getAllCollections(String database);

  /**
   * Gets all database names of the NoSQL system server as a {@code List} of Strings. They are
   * represented as Strings because different systems represent their Databases differently and The
   * interaction with database objects is limited to getting their collections.
   *
   * @return A {@code List<String>} that contain all databases found on the NoSQL server.
   */
  List<DatabaseDto> getAllDatabases();

  /**
   * Sets the current database according to the given name.
   *
   * @param name The name of the database.
   * @return {@code true} on success.
   */
  boolean setDatabase(String name);

  /**
   * Gets the current database according to the connection information.
   *
   * @return The name of the current database.
   */
  String getDatabase();

  /**
   * Generates a validator for the DBMS from a given JSON Schema and returns it as a String.
   * Note that possibly not all NoSQL DBMS do have a validation - in this case return the JSON
   * Schema to validate against this or return a notification message.
   */
  String generateValidator(String schema);

  /**
   * Gets the amount of collections within a specific database.
   *
   * @param database The name of the database.
   * @return The amount of collections as a positive {@code int}.
   */
  int getCollectionCount(String database);

  /**
   * Gets a random document from the collection that is hold within the settings.
   *
   * @return The concrete document as a String.
   */
  String getRandomDocument();

  /**
   * Gets all documents from the collection that is hold within the settings.
   *
   * @return The concrete documents as a {@code List} where each document is a {@code String}.
   */
  List<String> getAllDocuments();

  /**
   * Inserts a specific document into the collection hold within the settings.
   *
   * @param document The document to insert into the collection.
   * @return A message that informs the user about the status of the insertion.
   */
  String insertOne(String document);

  /**
   * Stores a schema at the database specified in the settings.
   *
   * @param schema The schema to insert.
   * @param name   The name of the schema to save it with.
   * @return A message that informs the user about the status of storage.
   */
  String storeSchema(String schema, String name);

  /**
   * Sets the validation for the collection hold within the settings.
   *
   * @param validationOptions A Map that holds the validation options.
   * @return A message containing the information about the status of validation.
   */
  String setValidation(Map<String, String> validationOptions);

  /**
   * Gets all stored schemas in the specified date range. These dates are inclusive, i.e. the range
   * is [min, max].
   *
   * @param min The minimal inclusive date.
   * @param max The maximal inclusive date.
   * @return A list of all schemas represented as DTOs.
   */
  List<SchemaDto> getSchemasInDateRange(LocalDate min, LocalDate max);
}
