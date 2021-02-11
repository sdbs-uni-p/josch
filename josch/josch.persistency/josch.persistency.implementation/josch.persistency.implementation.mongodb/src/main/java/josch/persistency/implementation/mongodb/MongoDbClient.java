package josch.persistency.implementation.mongodb;

import com.mongodb.BasicDBObject;
import com.mongodb.ConnectionString;
import com.mongodb.MongoCommandException;
import com.mongodb.MongoTimeoutException;
import com.mongodb.client.*;
import com.mongodb.client.model.*;
import josch.model.dto.CollectionDto;
import josch.model.dto.ConnectionInfoDto;
import josch.model.dto.SchemaDto;
import josch.model.dto.ValidationDto;
import josch.model.enums.ESystemConstants;
import josch.persistency.interfaces.IClient;
import org.bson.Document;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

/**
 * This {@code de.passau.uni.fim.josch.persistency.implementation.mongodb.MongoDbClient} class
 * implements the according interface.
 *
 * @author Kai Dauberschmidt
 * @see IClient
 */
public class MongoDbClient implements IClient {

  /** The MongoClient from the driver that holds the connection */
  private MongoClient driverClient;

  /** The currently inspected database */
  private String database;

  /** The status of the MongoClient */
  private boolean isInitialized;

  /**
   * Constructs the MongoDbClient with a given Connection.
   *
   * @param connectionInfo the {@code ConnectionInfoDto} that holds the information.
   * @see ConnectionInfoDto
   */
  public MongoDbClient(ConnectionInfoDto connectionInfo) {

    String url = (connectionInfo.getUrl() == null) ? "" : connectionInfo.getUrl().trim();
    long timeout = connectionInfo.getTimeout();

    // Build the connection string
    StringBuilder uriBuilder;

    if (url.isEmpty()) {
      // todo: Sharded Cluster and Replica Set
      uriBuilder = new StringBuilder("mongodb://");

      // Add username and password if existent.
      String userName = connectionInfo.getUser().trim();
      String password = connectionInfo.getPassword().trim();

      // todo authSource percent encoding
      boolean authSource = !userName.isEmpty() || !password.isEmpty();
      if (authSource) {
        uriBuilder.append(userName).append(':').append(password).append('@');
      }

      // Add the required host and port.
      uriBuilder
          .append(connectionInfo.getHost())
          .append(':')
          .append(connectionInfo.getPort())
          .append('/');

      // Add the database if existent.
      if (!connectionInfo.getDatabase().trim().isEmpty()) {
        uriBuilder.append(connectionInfo.getDatabase());
      }

      // If enforced access control add the authsource admin
      if (authSource) {
        uriBuilder.append("?authSource=admin");
      }

      url = uriBuilder.toString();
    } else {
      uriBuilder = new StringBuilder(url);
    }

    // Does the url already have options?
    if (url.contains("?")) {

      String cTimeout = "connectTimeoutMS=";
      String sTimeout = "serverSelectionTimeoutMS=";

      // Append connection timeout if it isn't present already.
      if (!url.contains(cTimeout)) {
        uriBuilder.append('&').append(cTimeout).append(timeout);
      }

      // Append server timeout if it isn't present already.
      if (!url.contains(sTimeout)) {
        uriBuilder.append('&').append(sTimeout).append(timeout);
      }

    } else {
      // Add all options.
      uriBuilder
          .append("?")
          .append("connectTimeoutMS=")
          .append(timeout)
          .append("&serverSelectionTimeoutMS=")
          .append(timeout);
    }

    ConnectionString connectionString = new ConnectionString(uriBuilder.toString());
    database = connectionString.getDatabase();
    driverClient = MongoClients.create(connectionString);

    // Lazy connection, so something has to be done to check whether the connection was successful.
    try {
      ListDatabasesIterable<Document> list = driverClient.listDatabases();
      list.first();
      Thread.sleep(timeout); // To check whether an exception is thrown within timeout.
      isInitialized = true;
    } catch (MongoTimeoutException | InterruptedException e) {
      e.printStackTrace();
    }
    isInitialized = true;
  }

  /** {@inheritDoc} */
  @Override
  public void close() {
    // Delete eventual temporary files.
    deleteTemporaryFiles();

    /*
     * if the driver's client isn't already closed, close it to release resources and
     * set it to null. Also set the state of the client to not initialized.
     */
    if (driverClient != null) {
      driverClient.close();
      driverClient = null;
      isInitialized = false;
    }
  }

  /** Deletes temporary files that are eventually used. */
  private void deleteTemporaryFiles() {
    // Delete the file for Stream.
    File streamFile = new File(ESystemConstants.PATH.getValue() + "/collection/collection.txt");
    try {
      Files.delete(streamFile.toPath());
    } catch (IOException ignored) {
    } // IO Exception if file does not exist.
  }

  /** {@inheritDoc} */
  @Override
  public boolean isInitialized() {
    return isInitialized;
  }

  /** {@inheritDoc} */
  @Override
  public CollectionDto getCollection(String collectionName) {
    return getCollection(collectionName, database);
  }

  /** {@inheritDoc} */
  @Override
  public CollectionDto getCollection(String collectionName, String dbName) {
    checkState();
    try {
      MongoDatabase db = driverClient.getDatabase(dbName);
      MongoCollection<Document> collection = db.getCollection(collectionName);

      // Create the dto.
      CollectionDto collectionDto = new CollectionDto();
      collectionDto.setName(collectionName);
      collectionDto.setCount(collection.estimatedDocumentCount());

      // Find a validator.
      Document collectionInfo =
          db.listCollections().filter(Filters.eq("name", collectionName)).first();
      assert collectionInfo != null;
      Document options = (Document) collectionInfo.get("options");
      Document validator = (Document) options.get("validator");
      String action = (String) options.get("validationAction");
      String level = (String) options.get("validationLevel");

      // Prepare the String representation of the validator.
      String validatorStr;

      // set the validator to null if it is already null or if it is empty.
      if (validator == null) {
        validatorStr = "";
      } else {
        try {
          validatorStr = validator.toJson();
        } catch (Exception e) { // Unknown codec
          validatorStr = validator.toString();
        }
      }
      collectionDto.setValidator(validatorStr);
      collectionDto.setValidationLevel(level);
      collectionDto.setValidationAction(action);
      return collectionDto;

    } catch (NullPointerException e) {
      throw new IllegalArgumentException(collectionName + "does not exist in " + dbName + ".");
    }
  }

  /** {@inheritDoc} */
  @Override
  public List<CollectionDto> getAllCollections() {
    return getAllCollections(database);
  }

  /** {@inheritDoc} */
  @Override
  public List<CollectionDto> getAllCollections(String dbName) {
    checkState();
    MongoDatabase db = driverClient.getDatabase(dbName);
    List<CollectionDto> dtoList = new ArrayList<>();
    MongoIterable<String> collections = db.listCollectionNames();
    for (String name : collections) {
      if (!name.equals(ESystemConstants.SCHEMA_STORAGE.getValue())) {
        dtoList.add(getCollection(name, dbName));
      }
    }

    return dtoList;
  }

  /** {@inheritDoc} */
  @Override
  public List<String> getAllDatabases() {
    checkState();
    List<String> databaseList = new ArrayList<>();

    // Get the databases and traverse through them. Add them to the database list if no system db.
    MongoIterable<String> databases = driverClient.listDatabaseNames();
    for (String database : databases) {
      if (!isSystemDatabase(database)) {
        databaseList.add(database);
      }
    }

    return databaseList;
  }

  /** Checks whether a given {@code database} is a System database. */
  private boolean isSystemDatabase(String database) {
    return database.contains("admin") || database.contains("local") || database.contains("config");
  }

  /** {@inheritDoc} */
  @Override
  public void setDatabase(String name) {
    checkState();
    List<String> databaseList = getAllDatabases();
    if (databaseList.contains(name)) {
      this.database = name;
    } else {
      throw new IllegalArgumentException("no such database on this server.");
    }
  }

  /** {@inheritDoc} */
  @Override
  public int getCollectionCount(String database) {
    List<CollectionDto> collectionsList = getAllCollections(database);
    return collectionsList.size();
  }

  /** {@inheritDoc} */
  @Override
  public String getDatabase() {
    checkState();
    return database;
  }

  /** {@inheritDoc} */
  @Override
  public String getRandomDocument(String collectionName) {
    checkState();

    // Set up the collection.
    MongoDatabase db = driverClient.getDatabase(database);
    MongoCollection<Document> collection = db.getCollection(collectionName);

    // Generate a random number in the range of [0,n) where n is the document count.
    long docCount = collection.countDocuments();
    int random = (int) (Math.random() * docCount);

    // get a single random document based on the previously calculated random number.
    Document doc = collection.find(new Document()).limit(1).skip(random).first();

    // return the json string representation of the document if it exists.
    return (doc != null) ? doc.toJson() : "";
  }

  /** {@inheritDoc} */
  @Override
  public List<String> getAllDocuments(String collectionName) {

    // The list to store the documents in.
    List<String> documents = new ArrayList<>();

    // Retrieve the document iterable.
    FindIterable<Document> documentIterable = getDocumentIterable(collectionName);

    // Store them as JSON Strings to the list.
    for (Document document : documentIterable) {
      documents.add(document.toJson());
    }
    return documents;
  }

  /**
   * Gets the document iterable from the MongoDB. This is implemented as a cursor and is not
   * ram-based.
   *
   * @param name The name of the document iterable.
   * @return The document iterable.
   */
  private FindIterable<Document> getDocumentIterable(String name) {
    checkState();
    MongoDatabase db = driverClient.getDatabase(database);
    MongoCollection<Document> collection = db.getCollection(name);
    return collection.find();
  }

  /** {@inheritDoc} */
  @Override
  public Iterator<String> getDocumentIterator(String collectionName, boolean randomize) {
    return new DocumentIterator(
        driverClient.getDatabase(database).getCollection(collectionName), randomize);
  }

  /** {@inheritDoc} */
  @Override
  public String insertOne(String document, String collectionName) {
    checkState();

    // Set up the collection and document.
    MongoDatabase db = driverClient.getDatabase(database);
    MongoCollection<Document> collection = db.getCollection(collectionName);

    // Attempt to insert and return the status.
    String status;
    try {
      Document doc = Document.parse(document); // Get a document from the String.
      collection.insertOne(doc);
      status = "Document inserted.";
    } catch (Exception e) {
      System.err.println(e.getMessage());
      status = handle(e);
    }
    return status;
  }

  /** {@inheritDoc} */
  @Override
  public String setValidation(Map<String, String> options, String collectionName) {

    // Set up.
    MongoDatabase db = driverClient.getDatabase(database);
    Document command = new Document();
    command.append("collMod", collectionName);
    command.append("validationAction", options.get("validationAction"));
    command.append("validationLevel", options.get("validationLevel"));
    String message;

    // Attempt to parse the validator and register it at the database.
    try {
      Document validator = Document.parse(options.get("validator"));
      command.append("validator", validator);
      db.runCommand(command);
      message = "Validator saved.";
    } catch (Exception e) {
      System.err.println(e.getMessage());
      message = handle(e);
    }
    return message;
  }

  /** {@inheritDoc} */
  @Override
  public String validate(String document, String schema) {

    // Prerequisites to creating the collection.
    MongoDatabase db = driverClient.getDatabase(database);
    // Todo: ensure this is not used by the system.
    String collectionName = "temporaryJoschValidation";
    String message;

    // Validation Options.
    ValidationOptions options = createStrictValidationOptions(schema);

    // Create the collection with the options above and fetch it.
    try {
      db.createCollection(collectionName, new CreateCollectionOptions().validationOptions(options));

      // Attempt to insert the document and store its validation message.
      message = insertOne(document, collectionName);

      // Change that success message.
      if (message.contains("inserted")) {
        message = "The schema validates this document.";
      }
    } catch (MongoCommandException e) {
      message = handle(e);
    }

    // Drop the collection again.
    MongoCollection<Document> collection = db.getCollection(collectionName);
    collection.drop();
    return message;
  }

  /**
   * {@inheritDoc}
   *
   * @return
   */
  @Override
  public ValidationDto validateAll(String collection, String schema) {
    ValidationDto result = new ValidationDto();
    long invalidDocuments = 0;
    MongoDatabase db = driverClient.getDatabase(database);
    String temporaryCollection = "temporaryJoschValidation"; // todo: ensure is not already used.
    db.getCollection(temporaryCollection).drop();
    ValidationOptions options = createStrictValidationOptions(schema);

    // Create the temporary collection and fetch it.
    try {
      db.createCollection(
          temporaryCollection, new CreateCollectionOptions().validationOptions(options));
      MongoCollection<Document> mongoCollection = db.getCollection(temporaryCollection);

      // Fetch the iterator for documents.
      FindIterable<Document> it = getDocumentIterable(collection);

      // Attempt to insert and count the failures.
      for (Document doc : it) {
        try {
          mongoCollection.insertOne(doc);
        } catch (Exception e) {
          invalidDocuments += 1;
          result.addInvalid(doc.toJson());
        }
      }
      result.setAmountInvalidDocuments(invalidDocuments);

      // Delete the temporary collection again.
      mongoCollection.drop();
    } catch (MongoCommandException e) {
      e.printStackTrace();
      handle(e);
    }
    return result;
  }

  /**
   * Creates ValidationOptions with strict validation and throw error on validation failure.
   *
   * @param schema The validator to go with the options.
   * @return The ValidationOptions as described above.
   */
  private ValidationOptions createStrictValidationOptions(String schema) {
    ValidationOptions options = new ValidationOptions();
    options.validationAction(ValidationAction.ERROR);
    options.validationLevel(ValidationLevel.STRICT);
    options.validator(Document.parse(schema));
    return options;
  }

  /** Checks whether the client is initialized. Throws an illegal state exception if not. */
  private void checkState() {
    if (!isInitialized) {
      throw new IllegalStateException("Client is not is initialized.");
    }
  }

  /** {@inheritDoc} */
  @Override
  public String storeSchema(String schema, String title, String name) {
    checkState();

    // Set up the collection and document.
    String collectionName = ESystemConstants.SCHEMA_STORAGE.getValue();
    MongoDatabase db = driverClient.getDatabase(database);
    MongoCollection<Document> collection;

    // Get the collection if exists, else create a new collection and get it.
    try {
      collection = db.getCollection(collectionName);
    } catch (IllegalArgumentException e) {
      db.createCollection(collectionName);
      collection = db.getCollection(collectionName);
    }

    // Attempt to insert and return the status.
    // todo: Figure out whether there is a limit. biggest tested schema has 448 line
    String status;
    try {
      Document doc = new Document();
      doc.put("title", title);
      doc.put("note", name);
      doc.put("schema", schema);
      doc.put("date", LocalDate.now());
      collection.insertOne(doc);
      status = ESystemConstants.SCHEMA_INSERTED.getValue();
    } catch (Exception e) {
      status = handle(e);
    }
    return status;
  }

  /** {@inheritDoc} */
  @Override
  public List<String> getSchemasByDate(LocalDate date, String title) {
    List<String> schemas = new ArrayList<>();
    MongoCollection<Document> collection = getSchemaStorage();
    if (collection == null) {
      return schemas;
    }

    // Find schemas by date filtering.
    FindIterable<Document> documentIterable =
        collection.find(Filters.and(Filters.eq("date", date), Filters.eq("title", title)));

    /*
     * Store them as JSON Strings without the date and the _id. Also rename the 'schema' back to
     * '$schema', because it is a forbidden label name in mongodb but an expected label in JSON
     * Schema.
     */
    for (Document currentSchema : documentIterable) {
      String json = currentSchema.get("schema").toString();
      schemas.add(json);
    }

    return schemas;
  }

  /** {@inheritDoc} */
  @Override
  public List<SchemaDto> getSchemasByDateRange(LocalDate min, LocalDate max, String title) {
    List<SchemaDto> schemas = new ArrayList<>();
    MongoCollection<Document> collection = getSchemaStorage();
    if (collection == null) {
      return schemas;
    }

    /*
     * SQL translation
     * SELECT *
     * FROM joschSchemaStorage
     * WHERE title = title
     * AND date >= min AND date <= max
     * ORDER BY title ASC, date DESC
     */
    FindIterable<Document> documentIterable =
        collection
            .find(
                Filters.and(
                    Filters.eq("title", title),
                    Filters.and(Filters.gte("date", min), Filters.lte("date", max))))
            .sort(new BasicDBObject("title", 1))
            .sort(new BasicDBObject("date", -1));

    /*
     * Store them as JSON Strings without the _id but with the 'date'. Also rename the 'schema' back
     * to '$schema', because it is a forbidden label name in mongodb but an expected label in JSON
     * Schema.
     * The date is required for differentiating them on the frontend.
     */
    for (Document currentSchema : documentIterable) {

      // get the schema and its note.
      String schema = currentSchema.get("schema").toString();
      Object noteObject = currentSchema.get("note");
      String note = noteObject != null ? noteObject.toString() : "";

      // Get and remove the date.
      Date tmp = (Date) currentSchema.get("date");
      Instant instant = tmp.toInstant();
      LocalDate date = LocalDate.ofInstant(instant, ZoneId.systemDefault());

      SchemaDto schemaDto = new SchemaDto(title, schema, date, note);
      schemas.add(schemaDto);
    }

    return schemas;
  }

  /**
   * Gets the {@code MongoCollection<Document>} collection for storing schemas if existent, else
   * {@code null}.
   *
   * @return The {@code MongoCollection<Document>} that contains the stored schemas.
   */
  private MongoCollection<Document> getSchemaStorage() {
    checkState();
    // Get the collection name.
    String collectionName = ESystemConstants.SCHEMA_STORAGE.getValue();

    // Get the database.
    MongoDatabase db = driverClient.getDatabase(database);

    // Get the collection if exists else return null.
    try {
      return db.getCollection(collectionName);
    } catch (IllegalArgumentException e) {
      return null;
    }
  }

  /** Handles MongoDB exceptions. */
  private String handle(Exception e) {
    e.printStackTrace();
    String message = e.getMessage();

    // Handles duplicate keys.
    if (message.contains("duplicate")) {
      return ESystemConstants.ERROR.getValue() + "Duplicate key found.";

      // Handles failed validation.
    } else if (message.contains("validation")) {
      return "Document failed validation";
    } else if (message.contains("readStartDocument")) {
      return ESystemConstants.ERROR.getValue() + "Has to be a valid JSON Object.";
    } else if (message.contains("Parsing of collection validator failed")) {
      return ESystemConstants.ERROR.getValue()
          + "This is not a valid MongoDB Validator. \n \n"
          + message;
    } else {
      return ESystemConstants.ERROR.getValue() + message;
    }
  }
}
