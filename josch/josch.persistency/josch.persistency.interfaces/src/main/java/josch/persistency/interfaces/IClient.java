package josch.persistency.interfaces;

import josch.model.dto.CollectionDto;
import josch.model.dto.SchemaDto;
import josch.model.dto.ValidationDto;

import java.time.LocalDate;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * This {@code IClient} interface encapsulates the functionality to interact with the underlying
 * database.
 *
 * @author Kai Dauberschmidt
 */
public interface IClient {

  /** Closes the connection and restores the state before initialization */
  void close();

  /** Gets the state of the client, i.e. whether it is initialized or not. */
  boolean isInitialized();

  /**
   * Gets the collection with the specified name as a {@code CollectionDto}.
   *
   * @param collectionName The name of the collection to get.
   * @return A {@code CollectionDto} that encapsulates the collection.
   * @see CollectionDto
   */
  CollectionDto getCollection(String collectionName);

  /**
   * Gets the collection with the specified name as a {@code CollectionDto} from the specified
   * database.
   *
   * @param collectionName The name of the collection to get.
   * @param dbName The name of the Database
   * @return A {@code CollectionDto} that encapsulates the collection.
   * @see CollectionDto
   */
  CollectionDto getCollection(String collectionName, String dbName);

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
  List<String> getAllDatabases();

  /**
   * Sets the current database according to the given name.
   *
   * @param name The name of the database.
   */
  void setDatabase(String name);

  /**
   * Gets the amount of collections within a specific database.
   *
   * @param database The name of the database.
   * @return The amount of collections as a positive {@code int}.
   */
  int getCollectionCount(String database);

  /**
   * Gets the current database name.
   *
   * @return The name of the database.
   */
  String getDatabase();

  /**
   * Gets a random document from a specific collection.
   *
   * @param collection The collection to get the document from.
   * @return The concrete Document as a {@code String}.
   */
  String getRandomDocument(String collection);

  /**
   * Gets all documents of a specific collection.
   *
   * @param collection The collection to get the documents from.
   * @return The concrete documents as a {@code List}.
   */
  List<String> getAllDocuments(String collection);

  /**
   * Get an Iterator that iterates over the given collection. If {@code randomize} is set to true,
   * the iterator has to iterate at random over a given collection, i.e. randomly visiting each
   * document in the collection, but no document more than once.
   *
   * @param collectionName The collection to iterate over.
   * @param randomize A boolean to determine whether the collection should be randomized or not.
   */
  Iterator<String> getDocumentIterator(String collectionName, boolean randomize);


    /**
   * Inserts a document as a string into the collection and returns a status message.
   *
   * @param document The document to insert.
   * @param collection The collection to insert into
   * @return a message to indicate success or failure.
   */
  String insertOne(String document, String collection);

  /**
   * Sets the validation of the collection according to the validation options.
   *
   * @param validationOptions The options for validation.
   * @param collectionName The name of the collection.
   * @return A message to indicate success or failure.
   */
  String setValidation(Map<String, String> validationOptions, String collectionName);
  
  /**
   * Stores a schema within the database. The schema is stored within the {@code SCHEMA_STORAGE}
   * collection. This collection represents all schemas.
   *
   * @param schema The schema as a String.
   * @param title The name of the collection.
   * @param name The name of the stored schema, i.e. the demo feature note.
   * @return A message that indicates success or failure.
   */
  String storeSchema(String schema, String title, String name);

  /**
   * Get all schemas for a specific date.
   *
   * @param date The date of the schemas.
   * @param title The name of the collection.
   * @return The schemas as a string list.
   */
  List<String> getSchemasByDate(LocalDate date, String title);

  /**
   * Get all schemas for a specific date range.
   *
   * @param min The minimal inclusive date.
   * @param max The maximal inclusive date.
   * @param title The title of the collection.
   * @return The schemas as a string list.
   */
  List<SchemaDto> getSchemasByDateRange(LocalDate min, LocalDate max, String title);

  /**
   * Validates a document against a given schema on server side. Note that the existing collection's
   * state must not be changed by doing so. The simplest approach is to create a new temporary
   * collection with the given schema as a validator, validate the document against it and drop that
   * collection again after validation. <br>
   * Also note that not all NoSQL Systems have server side validation. In this case this method
   * should return some kind of unsupported operation message or throw an Exception.
   *
   * @param document The document to validate.
   * @param schema The schema to validate against.
   * @return a message regarding the document's validity.
   */
  String validate(String document, String schema);

  /** 
   * Validates all documents against a given $jsonSchema on server side and returns the amount of
   * invalid documents. Create a new temporary collection with the given schema as a validator
   * and attempt to insert the documents of the collection.
   * 
   * @param schema The schema to validate against.
   * @return An integer representing the amount of invalid documents.
   * @see #validate(String, String) 
   */
  ValidationDto validateAll(String collection, String schema);
}
