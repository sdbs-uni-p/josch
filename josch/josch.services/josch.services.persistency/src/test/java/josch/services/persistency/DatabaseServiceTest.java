package josch.services.persistency;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import josch.model.dto.CollectionDto;
import josch.model.dto.ConnectionInfoDto;
import josch.model.dto.DatabaseDto;
import josch.model.dto.SchemaDto;
import josch.model.enums.ESystemConstants;
import josch.test.MongoDB.MongoBaseTest;
import org.bson.Document;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class is used as a test case for the DatabaseService. The ConnectionInfoDto will only be
 * created with urls, because params have been tested at the
 * josch.persistency.implementation.mongodb.ClientTest excessively.
 *
 * <p>In order to make the tests work uncomment the "requires org.mongodb.driver.sync.client;" line
 * in this module's module-info.java.
 *
 * @author Kai Dauberschmidt
 */
public class DatabaseServiceTest extends MongoBaseTest {

  /** The service */
  DatabaseService service;

  /**
   * An valid JSON schema draft-04 that cannot be converted to a validator, because the keyword
   * <em>default</em> is present.
   */
  final String INVALID_JSON_SCHEMA =
      String.join(
          "\n",
          new String[] {
            "{"
                + "  \"$schema\": \"http://json-schema.org/draft-04/schema#\","
                + "  \"type\": \"object\","
                + "  \"title\": \"students\","
                + "  \"additionalProperties\": true,"
                + "  \"properties\": {"
                + "    \"year\": { \"type\": \"number\", \"default\": 2020 },"
                + "    \"class\": { \"type\": \"number\" },"
                + "    \"language\": { \"type\": \"string\" },"
                + "    \"name\": {"
                + "      \"anyOf\": ["
                + "        { \"type\": \"string\" },"
                + "        {"
                + "          \"type\": \"object\","
                + "          \"additionalProperties\": false,"
                + "          \"properties\": {"
                + "            \"first\": { \"type\": \"string\" },"
                + "            \"nick\": { \"type\": \"string\" }"
                + "          }"
                + "        }"
                + "      ]"
                + "    }"
                + "  },"
                + "  \"required\": [ \"year\", \"class\" ]"
                + "}"
          });

  /**
   * Ensures that the localhost is in the correct state, i.e. has the proper databases as well as
   * collections for testing.
   */
  @BeforeEach
  private void setupDatabase() {
    setup();

    service = new DatabaseService(settings);
  }

  /** Tests whether a valid JSON Schema corresponds to an expected validator. */
  @Test
  public void generateValidator_validSchema() {
    // Prepare.

    // The validator is not pretty printed yet so remove whitespace intend.
    String expected = VALIDATOR.replace(" ", "");

    // Generate the validator.
    String actual = service.generateValidator(JSON_SCHEMA);

    Assertions.assertEquals(expected, actual);
  }

  /** Tests whether a valid JSON Schema with illegal keyword causes an expected error. */
  @Test
  public void generateValidator_invalidSchema() {
    // Prepare.

    String expected = "Error! Unsupported keyword: default";

    // Attempt to generate the validator.
    String actual = service.generateValidator(INVALID_JSON_SCHEMA);

    Assertions.assertEquals(expected, actual);
  }

  /** Tests whether an existing collection gets successfully retrieved. */
  @Test
  public void getCollection_exists() {

    // Actual functionality of the test.
    CollectionDto collection = service.getCollection(COLLECTION);

    // the actual object should exist.
    Assertions.assertNotNull(collection);

    // its name should match.
    Assertions.assertEquals(COLLECTION, collection.getName());

    // its count should match the 1 document.
    Assertions.assertEquals(26, collection.getCount());
  }

  /** Tests whether an non existing collection throws the illegal argument exception. */
  @Test
  public void getCollection_notExists() {

    Assertions.assertThrows(AssertionError.class, () -> service.getCollection("noName"));
  }

  /** Tests whether all collections get retrieved on a valid database. */
  @Test
  public void getAllCollections_validDatabase() {
    getAllCollections(null);
    getAllCollections(DATABASE);
  }

  /**
   * The action method of the getALLCollection_validDatabase() Test. It tests the overloaded method
   * getAllCollections() that has an optional database parameter. However there is only one database
   * in use for testing, so the results must be the same.
   */
  private void getAllCollections(String db) {
    List<CollectionDto> collections =
        (db == null) ? service.getAllCollections() : service.getAllCollections(db);

    // There is only one collection present.
    Assertions.assertEquals(1, collections.size());

    // Fetch it, get its name and count: It should have 26 documents.
    CollectionDto collection = collections.get(0);
    Assertions.assertEquals(COLLECTION, collection.getName());
    Assertions.assertEquals(26, collection.getCount());
  }

  /**
   * Tests whether the set up database is contained in getAllDatabases, when it is called on the
   * correct server.
   */
  @Test
  public void getAllDatabases() {
    List<DatabaseDto> databases = service.getAllDatabases();

    // Find the test database.
    boolean contained = false;
    int i = 0;
    while (!contained && i < databases.size()) {
      contained = databases.get(i).getName().equals(DATABASE);
      i++;
    }
    Assertions.assertTrue(contained);

    // Without completely wiping the other databases we only know that the size is >= 1.
    Assertions.assertTrue(databases.size() >= 1);
  }

  /** Tests whether the an existing database can be set. */
  @Test
  public void setDatabase_exists() {
    boolean success = service.setDatabase(DATABASE);
    Assertions.assertTrue(success);
  }

  /**
   * Tests whether the attempt to set an non-existing database causes an IllegalArgumentException.
   */
  @Test
  public void setDatabase_notExists() {
    // todo: ensure this does not exist.
    boolean success = service.setDatabase("IDoNotExistInThisSystem");
    Assertions.assertFalse(success);
  }

  /** Tests whether the correct collection count is returned on a valid database. */
  @Test
  public void getCollectionCount_validDatabase() {

    int count = service.getCollectionCount(DATABASE);
    Assertions.assertEquals(1, count);
  }

  /** Tests whether the database name is returned on a valid database. */
  @Test
  public void getDatabase_exists() {
    Assertions.assertEquals(DATABASE, service.getDatabase());
  }

  /** Tests whether null is returned when no database was specified. */
  @Test
  public void getDatabase_notExists() {
    ConnectionInfoDto cInfo = settings.getConnectionInfo();
    cInfo.setDatabase("");
    settings.setConnectionInfo(cInfo);
    Assertions.assertNull(service.getDatabase());
  }

  /** Tests the getting and inserting of a random document */
  @Test
  public void getRandomDocument_insert() {
    String expected = "Document inserted.";

    // Get the doc.
    Document document = Document.parse(service.getRandomDocument());
    Assertions.assertTrue(document.containsKey("name"));
    Assertions.assertTrue(document.containsKey("class"));
    Assertions.assertTrue(document.containsKey("year"));

    // Remove the id.
    document.remove("_id");
    String actual = service.insertOne(document.toJson());
    Assertions.assertEquals(expected, actual);
  }

  /** Tests the getting of all documents in the collection. */
  @Test
  public void getAllDocuments() {
    List<String> documents = service.getAllDocuments();
    Assertions.assertEquals(26, documents.size());

    // All documents have the required fields.
    for (String doc : documents) {
      Document document = Document.parse(doc);
      Assertions.assertTrue(document.containsKey("name"));
      Assertions.assertTrue(document.containsKey("class"));
      Assertions.assertTrue(document.containsKey("year"));
    }
  }

  /** Tests the setting of validation. */
  @Test
  public void setValidation() {
    String expected = "Validator saved.";
    // Store the validator and its options.
    Map<String, String> validationOptions = new HashMap<>();
    validationOptions.put("validator", VALIDATOR); // Validator.
    validationOptions.put("validationLevel", "strict"); // Level.
    validationOptions.put("validationAction", "error"); // Action.

    // Get and call the service.
    String actual = service.setValidation(validationOptions);
    Assertions.assertEquals(expected, actual);
  }

  /** Tests the storage and getting of schemas */
  @Test
  public void storeSchema_getSchema_valid() {
    // Store the schema
    String expected = ESystemConstants.SCHEMA_INSERTED.getValue();
    String actual = service.storeSchema(JSON_SCHEMA, "handmade schema");
    Assertions.assertEquals(expected, actual);

    // Get the schema.
    LocalDate now = LocalDate.now();
    List<SchemaDto> schemas = service.getSchemasInDateRange(now, now);
    Assertions.assertEquals(1, schemas.size());
    Assertions.assertEquals(JSON_SCHEMA, schemas.get(0).getJson());
  }

  /** Tests the double storage of schemas. */
  @Test
  public void storeSchema_twice() {
    // Store the schema
    String expected = ESystemConstants.SCHEMA_INSERTED.getValue();
    String actual = service.storeSchema(JSON_SCHEMA, "handmade schema");
    Assertions.assertEquals(expected, actual);

    // "store" it again.
    actual = service.storeSchema(JSON_SCHEMA, "handmade schema");
    Assertions.assertEquals(expected, actual);


    // Get the schema.
    LocalDate now = LocalDate.now();
    List<SchemaDto> schemas = service.getSchemasInDateRange(now, now);
    Assertions.assertEquals(1, schemas.size());
    Assertions.assertEquals(JSON_SCHEMA, schemas.get(0).getJson());
  }

  /** Tests the storage of malformed JSON */
  @Test
  public void storeSchema_malformed() {
    String schema = "\"foo\" : \"bar\"";
    String expected = ESystemConstants.ERROR.getValue() + "Malformed JSON.";
    String actual = service.storeSchema(schema, "Should not work.");
    Assertions.assertEquals(expected,actual);
  }

  /** Tests the generation of a validator with an _id. */
  @Test
  public void generateValidator_CheckId() {
    // Create an id.
    JsonObject id = new JsonObject();
    id.addProperty("type", "object");
    // Add the id.
    JsonObject schema = JsonParser.parseString(JSON_SCHEMA).getAsJsonObject();
    JsonObject props = schema.get("properties").getAsJsonObject();
    props.add("_id", id);

    // Create a validator.
    JsonObject validator = JsonParser.parseString(service.generateValidator(schema.toString())).getAsJsonObject();
    Assertions.assertTrue(validator.has("$jsonSchema"));

    // Check some of its properties.
    schema = validator.get("$jsonSchema").getAsJsonObject();
    Assertions.assertTrue(schema.has("type"));
    Assertions.assertTrue(schema.has("title"));
    Assertions.assertTrue(schema.has("properties"));
  }
}
