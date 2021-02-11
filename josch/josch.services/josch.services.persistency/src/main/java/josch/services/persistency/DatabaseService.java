package josch.services.persistency;

import com.google.gson.*;
import josch.model.dto.CollectionDto;
import josch.model.dto.DatabaseDto;
import josch.model.dto.SchemaDto;
import josch.model.dto.SettingsDto;
import josch.model.enums.ESystemConstants;
import josch.persistency.factory.AbstractClientFactory;
import josch.persistency.interfaces.IClient;
import josch.services.interfaces.IDatabaseService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * This {@code CollectionService} class is an implementation of it's interface. It uses the client
 * factory to get a client and call the client's interface methods to achieve the service. These
 * methods do seem similar but in order to not mess with closing connections this was done on
 * purpose.
 *
 * @author Kai Dauberschmidt
 */
public class DatabaseService implements IDatabaseService {

  /** The settings required to connect to a database */
  private final SettingsDto settings;
  
  public DatabaseService(SettingsDto settings) {
    this.settings = settings;
  }

  /** {@inheritDoc} */
  @Override
  public CollectionDto getCollection(String collectionName) {
    IClient client = AbstractClientFactory.getClient(settings);
    CollectionDto collectionDto = client.getCollection(collectionName);
    client.close();
    return collectionDto;
  }

  /** {@inheritDoc} */
  @Override
  public List<CollectionDto> getAllCollections() {
    IClient client = AbstractClientFactory.getClient(settings);
    List<CollectionDto> collectionDtoList = client.getAllCollections();
    client.close();
    return collectionDtoList;
  }

  /** {@inheritDoc} */
  @Override
  public List<CollectionDto> getAllCollections(String database) {
    IClient client = AbstractClientFactory.getClient(settings);
    List<CollectionDto> collectionDtoList = client.getAllCollections(database);
    client.close();
    if (collectionDtoList != null && !collectionDtoList.isEmpty()) {
      return collectionDtoList;
    } else {
      throw new IllegalStateException("There are no collections on the server yet.");
    }
  }

  /**
   * {@inheritDoc}
   *
   * @return
   */
  @Override
  public List<DatabaseDto> getAllDatabases() {
    IClient client = AbstractClientFactory.getClient(settings);
    List<String> databaseNames = client.getAllDatabases();

    // Check for existing databases.
    if (databaseNames != null && !databaseNames.isEmpty()) {
      List<DatabaseDto> databases = new ArrayList<>();
      for (String database : databaseNames) {
        int count = client.getCollectionCount(database);
        databases.add(new DatabaseDto(database, count));
      }
      client.close();
      return databases;
    } else {
      client.close();
      throw new IllegalStateException("There are no databases on the server yet.");
    }
  }

  /** {@inheritDoc} */
  @Override
  public boolean setDatabase(String name) {
    IClient client = AbstractClientFactory.getClient(settings);
    try {
      client.setDatabase(name);
    } catch (IllegalArgumentException e) {
      return false;
    }
    client.close();
    return true;
  }

  /**
   * {@inheritDoc}
   *
   * <p>MongoDB does (only) support JSON Schema draft-04 with the exception of some of the
   * specifications. These are being checked for.
   *
   * <ul>
   *   <li>$schema is being removed.
   *   <li>type: integer is being replaced with type number.
   *   <li>unsupported keywords: notify the user about it.
   * </ul>
   *
   * <p>Even though recursion would probably be the desired method to implement this, note that
   * calling recursive methods on nested JsonObjects can cause unpredictable
   * ConcurrentModificationExceptions. Therefore no recursion is used in here.
   */
  @Override
  public String generateValidator(String schema) {

    // Check for unsupported keywords.
    try {
      hasUnsupportedKeywords(schema);
    } catch (IllegalArgumentException e) {
      return ESystemConstants.ERROR.getValue() + e.getMessage();
    }

    // Replace integers.
    schema = schema.replace("integer", "number");
    schema = schema.replace("Integer", "number");

    // Remove $schema. This is a top level property so no recursion needed.
    JsonObject jsonSchema = JsonParser.parseString(schema).getAsJsonObject();
    jsonSchema.remove("$schema");

    // Modify the _id:  $oid requires bson type objectId.
    JsonObject id = jsonSchema.get("properties").getAsJsonObject().getAsJsonObject("_id");
    if (id != null) {
      JsonElement prevIdTypes = id.get("type");
      id.remove("type");
      JsonArray idTypes;

      // Make a JsonArray out of the previous id types.
      if (prevIdTypes.isJsonArray()) {
        idTypes = prevIdTypes.getAsJsonArray();
      } else {
        idTypes = new JsonArray();
        idTypes.add(prevIdTypes);
      }

      // Add the objectId and the keyword bsonType.
      idTypes.add("objectId");
      id.add("bsonType", idTypes);
    }

    // Add the validator keyword around it.
    JsonObject validator = new JsonObject();
    validator.add("$jsonSchema", jsonSchema);
    return validator.toString();
  }

  /**
   * Checks for unsupported JSON Schema keywords in a given JSON Schema as a String.
   *
   * @throws IllegalArgumentException When such a keyword is found.
   */
  private static void hasUnsupportedKeywords(String schema) {
    String[] unsupportedKeywords = {
      "$ref", "default", "definition", "format", "id", "links", "media", "readOnly", "pathStart"
    };

    for (String keyword : unsupportedKeywords) {
      if (schema.contains("\"" + keyword + "\"")) {
        throw new IllegalArgumentException("Unsupported keyword: " + keyword);
      }
    }
  }

  /** {@inheritDoc} */
  @Override
  public String getDatabase() {
    IClient client = AbstractClientFactory.getClient(settings);
    String dbName = client.getDatabase();
    client.close();
    return dbName;
  }

  /** {@inheritDoc} */
  @Override
  public int getCollectionCount(String database) {
    IClient client = AbstractClientFactory.getClient(settings);
    int count = client.getCollectionCount(database);
    client.close();
    return count;
  }

  /** {@inheritDoc} */
  @Override
  public String getRandomDocument() {
    IClient client = AbstractClientFactory.getClient(settings);
    String document = client.getRandomDocument(settings.getCollection().getName());
    client.close();
    return document;
  }

  /** {@inheritDoc} */
  @Override
  public List<String> getAllDocuments() {
    IClient client = AbstractClientFactory.getClient(settings);
    List<String> documents = client.getAllDocuments(settings.getCollection().getName());
    client.close();
    return documents;
  }

  /** {@inheritDoc} */
  @Override
  public String insertOne(String document) {
    IClient client = AbstractClientFactory.getClient(settings);
    String message = client.insertOne(document, settings.getCollection().getName());
    client.close();
    return message;
  }

  /** {@inheritDoc} */
  @Override
  public String setValidation(Map<String, String> validationOptions) {
    IClient client = AbstractClientFactory.getClient(settings);
    String message = client.setValidation(validationOptions, settings.getCollection().getName());
    client.close();
    return message;
  }

  /** {@inheritDoc} */
  @Override
  public String storeSchema(String schema, String name) {
    IClient client = AbstractClientFactory.getClient(settings);

    // Check whether the schema is already stored with the same date.
    JsonElement s1;
    try {
      s1 = JsonParser.parseString(schema);
    } catch (JsonSyntaxException e) {
      e.printStackTrace();
      return ESystemConstants.ERROR.getValue() + "Malformed JSON.";
    }
    List<String> schemas =
        client.getSchemasByDate(LocalDate.now(), settings.getCollection().getName());
    boolean equals = false;
    int i = 0;

    while (i < schemas.size() && !equals) {
      JsonElement s2 = JsonParser.parseString(schemas.get(i));
      equals = s1.equals(s2);
      i += 1;
    }

    // if it is already stored ignore it:
    if (equals) {
      return ESystemConstants.SCHEMA_INSERTED.getValue();
    }

    // else store it;
    String message = client.storeSchema(schema, settings.getCollection().getName(), name);
    client.close();
    return message;
  }

  /** {@inheritDoc} */
  @Override
  public List<SchemaDto> getSchemasInDateRange(LocalDate min, LocalDate max) {

    // Check input: if min is null select everything from 1970-06-01 to now (hint: Codd)
    min = (min == null) ? LocalDate.of(1970, 6, 1) : min;

    // Check input: if max is null select everything from min up to today.
    max = (max == null) ? LocalDate.now() : max;

    IClient client = AbstractClientFactory.getClient(settings);
    List<SchemaDto> schemas =
        client.getSchemasByDateRange(min, max, settings.getCollection().getName());
    client.close();
    return schemas;
  }
}
