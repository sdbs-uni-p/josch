package josch.persistency.implementation.mongodb;

import com.mongodb.MongoTimeoutException;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.CreateCollectionOptions;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ValidationOptions;
import josch.model.dto.CollectionDto;
import josch.model.dto.ConnectionInfoDto;
import org.bson.Document;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * This {@code josch.persistency.implementation.mongodb.ClientTest} class is the super class for the
 * MongoDbClient testing. It implements a setup to get the database in its correct state and also
 * implements the testing methods. Its children actually run the testing methods. A local mongoDB
 * server is required für the tests to work as they address the localhost directly with a default
 * timeout of 300ms.
 *
 * @author Kai Dauberschmidt
 */
public class ClientTest {

  /** a valid ConnectionInfoDto with a working database */
  ConnectionInfoDto validDto;

  /** an invalid ConnectionInfoDto that does not provide a working MongoDB. */
  ConnectionInfoDto invalidDto;

  /** a valid ConnectionInfoDto without a database */
  ConnectionInfoDto validBlankDto;

  /** the valid database name */
  final String VALID_DB = "clientTest";

  /** an non existing database name */
  final String INVALID_DB = "notClientTest";

  /** the Book collection name */
  final String COLLECTION_BOOK = "Book";

  /** the Reads collection name */
  final String COLLECTION_READS = "Reads";

  /** The name of the empty collection */
  final String EMPTY_COLLECTION = "ThisIsEmpty";

  /** a non existing collection name */
  final String FALSE_COLLECTION = "Animals";

  /** The validator as a jsonSchema */
  JSONObject jsonSchema;

  /** A valid Schema as a string */
  final String validSchema =
      "{\"$jsonSchema\": { "
          + "\"bsonType\": \"object\", "
          + "\"title\": \"speaks\", "
          + "\"properties\": {"
          + "\"name\": {\"bsonType\": \"string\"}, "
          + "\"language\": {\"bsonType\": \"string\"}, "
          + "\"since\": {\"bsonType\": \"int\"}"
          + "}}}";

  /** A valid document as a string */
  final String validDocument = "{ \"name\": \"Paul\", \"language\": \"German\", \"since\": 2015}";

  /** An invalid schema as a string: type integer is not supported. */
  final String invalidSchema =
      "{\"$jsonSchema\": { "
          + "\"bsonType\": \"object\", "
          + "\"title\": \"speaks\", "
          + "\"properties\": {"
          + "\"name\": {\"bsonType\": \"string\"}, "
          + "\"language\": {\"bsonType\": \"string\"}, "
          + "\"since\": {\"bsonType\": \"integer\"}"
          + "}}}";

  /** An invalid schema as a string: type integer is not supported. */
  final String noJsonSchema =
      "{ \"bsonType\": \"object\", "
          + "\"title\": \"speaks\", "
          + "\"properties\": {"
          + "\"name\": {\"bsonType\": \"string\"}, "
          + "\"language\": {\"bsonType\": \"string\"}, "
          + "\"since\": {\"bsonType\": \"integer\"}"
          + "}}";

  /** An invalid document as a string: attribute since has to be int. */
  final String invalidDocument =
      "{ \"name\": \"Paul\", \"language\": \"German\", \"since\": \"2015\"}";

  /**
   * Ensures that the localhost is in the correct state, i.e. has the proper databases as well as
   * collections for testing.
   */
  void setup() {
    MongoClient client = MongoClients.create(); // localhost per default

    // Drops the database and its collections.
    MongoDatabase database = client.getDatabase(INVALID_DB);
    database.drop();
    database = client.getDatabase(VALID_DB);
    database.drop();

    // Create a valid jsonSchema.
    jsonSchema =
        new JSONObject()
            .put(
                "$jsonSchema",
                new JSONObject()
                    .put("bsonType", "object")
                    .put(
                        "properties",
                        new JSONObject()
                            .put("title", new JSONObject().put("type", "string"))
                            .put("author", new JSONObject().put("type", "string"))
                            .put("price", new JSONObject().put("type", "number"))));

    // Create the validation
    ValidationOptions validationOptions = new ValidationOptions();
    validationOptions.validator(Document.parse(jsonSchema.toString()));

    // Add the collections listed above and add the schema to a book collection.
    database.createCollection(
        COLLECTION_BOOK, new CreateCollectionOptions().validationOptions(validationOptions));

    // Create the reads collection.
    database.createCollection(COLLECTION_READS);

    // Create the empty collection.
    database.createCollection(EMPTY_COLLECTION);

    // Insert book documents into the Book collection.
    MongoCollection<Document> books = database.getCollection(COLLECTION_BOOK);

    Document book = new Document();
    book.put("title", "Datenbanksysteme");
    book.put("author", "Alfons Kemper, Andre Eickler");
    book.put("price", 35.99);
    books.insertOne(book);

    Document otherBook = new Document();
    otherBook.put("title", "Lew Tolstoj");
    otherBook.put("author", "Geir Kjetsaa");
    otherBook.put("price", 100.00);
    books.insertOne(otherBook);

    // Insert a reader into the Reads collection.
    Document reader = new Document();
    reader.put("name", "Kai");
    reader.put(COLLECTION_BOOK, "Datenbanksysteme");
    MongoCollection<Document> reads = database.getCollection(COLLECTION_READS);
    reads.insertOne(reader);
  }

  /**
   * Tests the construction of a client with a valid ConnectionInfoDto. If the client fails to
   * connect it will throw an exception that will fail the test.
   */
  void construct_validDto() {
    MongoDbClient client = new MongoDbClient(validDto);
    Assertions.assertTrue(client.isInitialized());
    client.close();
  }

  /**
   * Tests the construction of a client with an invalid ConnectionInfoDto. An exception being thrown
   * is the expected behavior.
   */
  void construct_invalidDto() {
    Assertions.assertThrows(
        MongoTimeoutException.class,
        () -> {
          MongoDbClient client = new MongoDbClient(invalidDto);
        });
  }

  /**
   * Tests the correct closure of the client, i.e. checks if the state is set correctly and no
   * methods can be called.
   */
  void close_connectedClient() {
    // Connect at first.
    MongoDbClient client = new MongoDbClient(validDto);
    assertTrue(client.isInitialized());

    // Quit
    client.close();
    Assertions.assertFalse(client.isInitialized());

    // The database operaterations should cause an IllegalStateException.
    Assertions.assertThrows(IllegalStateException.class, () -> client.getCollection(INVALID_DB));
    Assertions.assertThrows(IllegalStateException.class, () -> client.getAllCollections(VALID_DB));
    Assertions.assertThrows(IllegalStateException.class, () -> client.setDatabase(VALID_DB));
    Assertions.assertThrows(IllegalStateException.class, () -> client.getCollectionCount(VALID_DB));
    Assertions.assertThrows(IllegalStateException.class, client::getAllCollections);
    Assertions.assertThrows(IllegalStateException.class, client::getAllDatabases);
  }

  /** Tests whether an existing collection gets successfully retrieved. */
  void getCollection_exists() {
    MongoDbClient client = new MongoDbClient(validDto);
    CollectionDto collection = client.getCollection(COLLECTION_BOOK);

    // the actual object should exist.
    Assertions.assertNotNull(collection);

    // its name should match.
    Assertions.assertEquals(COLLECTION_BOOK, collection.getName());

    // its count should match the 1 document.
    Assertions.assertEquals(1, collection.getCount());

    // its validator should match the jsonSchema.
    Assertions.assertEquals(parse(jsonSchema),parse(collection.getValidator()));
  }

  /** Tests whether an non existing collection throws the illegal argument exception. */
  void getCollection_notExists() {
    MongoDbClient client = new MongoDbClient(validDto);
    Assertions.assertThrows(
        IllegalArgumentException.class, () -> client.getCollection(FALSE_COLLECTION));
  }

  /** Tests whether all collections get retrieved on a valid database. */
  void getAllCollections_validDatabase() {
    MongoDbClient client = new MongoDbClient(validDto);
    List<CollectionDto> collections = client.getAllCollections();

    // There are two collections registered: Books and Reads.
    Assertions.assertEquals(2, collections.size());

    for (CollectionDto collection : collections) {

      // Each collection has exactly one document.
      Assertions.assertEquals(1, collection.getCount());

      // Check the collection's attributes.
      if (collection.getName().equals(COLLECTION_BOOK)) {
        Assertions.assertEquals(parse(jsonSchema), parse(collection.getValidator()));
      } else {
        Assertions.assertEquals(COLLECTION_READS, collection.getName());
        Assertions.assertNull(collection.getValidator());
      }
    }
  }

  /**
   * Tests whether the set up database is contained in getAllDatabases, when it is called on the
   * correct server.
   */
  void getAllDatabases_validDto() {
    MongoDbClient client = new MongoDbClient(validDto);
    List<String> dbNames = client.getAllDatabases();

    // Without completely wiping the other databases we only know that the size is >= 1.
    Assertions.assertTrue(dbNames.size() >= 1);

    // We only know the list contains the validDb database at some index.
    Assertions.assertTrue(dbNames.contains(VALID_DB));
  }

  /** Tests whether the an existing database can be set. */
  void setDatabase_exists() {
    MongoDbClient client = new MongoDbClient(validBlankDto);
    client.setDatabase(VALID_DB);
    Assertions.assertDoesNotThrow(() -> client.setDatabase(VALID_DB));
  }

  /**
   * Tests whether the attempt to set an non-existing database causes an IllegalArgumentException.
   */
  void setDatabase_notExists() {
    MongoDbClient client = new MongoDbClient(validBlankDto);
    Assertions.assertThrows(IllegalArgumentException.class, () -> client.setDatabase(INVALID_DB));
  }

  /** Tests whether the correct collection count is returned on a valid database. */
  void getCollectionCount_validDatabase() {
    MongoDbClient client = new MongoDbClient(validBlankDto);
    int count = client.getCollectionCount(VALID_DB);
    Assertions.assertEquals(2, count);
  }

  /** Tests whether the database name is returned on a valid database. */
  void getDatabase_exists() {
    MongoDbClient client = new MongoDbClient(validDto);
    Assertions.assertEquals(VALID_DB, client.getDatabase());
  }

  /** Tests whether null is returned when no database was specified. */
  void getDatabase_notExists() {
    MongoDbClient client = new MongoDbClient(validBlankDto);
    Assertions.assertNull(client.getDatabase());
  }

  /**
   * This method tests whether two random documents (1,2) are being retrieved by the
   * getRandomDocument Method when exactly two documents exists within a collection. Assuming the
   * LaPlace Space, both documents are equally probable.
   */
  void getRandomDocument_twoExist() {
    MongoDbClient client = new MongoDbClient(validDto);

    // get two docs and check whether they contain the attributes.
    String doc1 = client.getRandomDocument(COLLECTION_BOOK);
    Assertions.assertTrue(containsAttrs(doc1));
    String doc2 = client.getRandomDocument(COLLECTION_BOOK);
    Assertions.assertTrue(containsAttrs(doc2));

    // initialize a counter
    int count = 0;
    boolean equals = doc1.equals(doc2);

    // get random documents while the two documents equal each other or the counter is < 5.
    while (equals) {
      doc1 = client.getRandomDocument(COLLECTION_BOOK);
      Assertions.assertTrue(containsAttrs(doc1));
      doc2 = client.getRandomDocument(COLLECTION_BOOK);
      Assertions.assertTrue(containsAttrs(doc2));
      count += 1;
      equals = doc1.equals(doc2) && count < 5;
    }

    // assert this is reached within 5 tries.
    Assertions.assertTrue(count < 5);
  }

  /** Tests whether an empty string is being returned when no document is within the collection. */
  void getRandomDocument_notExists() {
    MongoDbClient client = new MongoDbClient(validDto);
    String doc1 = client.getRandomDocument(EMPTY_COLLECTION);
    Assertions.assertEquals("", doc1);
  }

  /**
   * Checks whether a given document contains the attributes of the book collection, i.e. author,
   * title, price.
   *
   * @param document The document to check
   * @return {@code true} if the document contains all attributes, {@code false} else.
   */
  private boolean containsAttrs(String document) {
    return document.contains("title") && document.contains("author") && document.contains("price");
  }

  /**
   * Takes a document from a collection and attempts to insert it into the same collection. A
   * duplicate keys error should occur. Checks whether a correct error message is being returned.
   */
  void insertOne_duplicateKeys() {
    MongoDbClient client = new MongoDbClient(validDto);
    String doc = client.getRandomDocument(COLLECTION_BOOK);
    String result = client.insertOne(doc, COLLECTION_BOOK);
    Assertions.assertTrue(result.toLowerCase().contains("duplicate"));
  }

  /** Checks whether a valid document is being inserted. */
  void insertOne_validDocument() {
    // The valid document
    String book = "{\"title\": \"Die Kammer\", \"author\": \"John Grisham\", \"price\":5.00}";

    // Call the method on the document.
    MongoDbClient client = new MongoDbClient(validDto);
    String result = client.insertOne(book, COLLECTION_BOOK);
    // Check the error message.
    Assertions.assertTrue(result.toLowerCase().contains("insert"));

    // Check the actual database.
    MongoClient mongoClient = MongoClients.create();
    MongoCollection<Document> coll =
        mongoClient.getDatabase(VALID_DB).getCollection(COLLECTION_BOOK);
    Assertions.assertNotNull(coll.find(Filters.eq("title", "Die Kammer")).first());
  }

  /** Checks whether invalid documents are being caught and not inserted into the database. */
  void insertOne_invalidDocument() {
    // Typo in price -> invalid document
    String book = "{\"title\": \"Die Kammer\", \"author\": \"John Grisham\", \"price\":5.00\"}";

    // Call the method on the document.
    MongoDbClient client = new MongoDbClient(validDto);
    String result = client.insertOne(book, COLLECTION_BOOK);

    // Check the error message
    Assertions.assertTrue(result.toLowerCase().contains("invalid"));

    // Check the actual database.
    MongoClient mongoClient = MongoClients.create();
    MongoCollection<Document> coll =
        mongoClient.getDatabase(VALID_DB).getCollection(COLLECTION_BOOK);
    Assertions.assertNull(coll.find(Filters.eq("title", "Die Kammer")).first());
  }

  /** Checks whether the validation works with correct input. */
  void validate_validDocument_validSchema() {
    MongoDbClient client = new MongoDbClient(validDto);
    String message = client.validate(validDocument, validSchema);
    Assertions.assertTrue(message.contains("validate"));
  }

  /** Checks whether the validation prints a failure when validation fails. */
  void validate_invalidDocument_validSchema() {
    MongoDbClient client = new MongoDbClient(validDto);
    String message = client.validate(invalidDocument, validSchema);
    Assertions.assertFalse(message.contains("validate"));
    Assertions.assertTrue(message.contains("failed"));
  }

  /**
   * Checks whether the validation is rejected when the schema is invalid but the document is valid.
   */
  void validate_validDocument_invalidSchema() {
    MongoDbClient client = new MongoDbClient(validDto);
    String message = client.validate(validDocument, invalidSchema);
    System.out.println(message);
    Assertions.assertFalse(message.contains("validate"));
    Assertions.assertTrue(message.toLowerCase().contains("type"));

    message = client.validate(validDocument, noJsonSchema);
    System.out.println(message);
    Assertions.assertFalse(message.contains("validate"));
    Assertions.assertTrue(message.toLowerCase().contains("failed"));
  }

  /** Checks whether the validation is rejected on completely invalid input. */
  void validate_invalidDocument_invalidSchema() {
    MongoDbClient client = new MongoDbClient(validDto);
    String message = client.validate(invalidDocument, invalidSchema);
    System.out.println(message);
    Assertions.assertFalse(message.contains("validate"));
    Assertions.assertTrue(message.toLowerCase().contains("type"));

    message = client.validate(invalidDocument, noJsonSchema);
    System.out.println(message);
    Assertions.assertFalse(message.contains("validate"));
    Assertions.assertTrue(message.toLowerCase().contains("failed"));
  }

  /** Helper method to parse JSON objects to documents */
  private Document parse(JSONObject object) {
    return parse(object.toString());
  }

  /** Helper method to parse Strings to Documents. */
  private Document parse(String string) {
    return Document.parse(string);
  }
}
