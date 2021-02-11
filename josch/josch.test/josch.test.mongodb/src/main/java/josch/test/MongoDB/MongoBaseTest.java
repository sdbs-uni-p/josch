package josch.test.MongoDB;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import josch.model.dto.CollectionDto;
import josch.model.dto.ConnectionInfoDto;
import josch.model.dto.SettingsDto;
import josch.model.enums.EContainmentTools;
import josch.model.enums.EDatabaseSystems;
import josch.model.enums.EExtractionTools;
import josch.test.interfaces.AbstractBaseTest;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

/**
 * This is the implementation of the {@code AbstractBaseTest} for an underlying MongoDB system.
 *
 * @see AbstractBaseTest ;
 */
public class MongoBaseTest extends AbstractBaseTest {

  /** The connection as a URL */
  protected static final String URL = "mongodb://localhost:27017/";

  /** Valid system settings */
  protected static SettingsDto settings;

    /** A valid MongoDB JSON Schema for the collection. */
  protected static final String VALIDATOR =
      String.join(
          "\n",
          new String[] {
            "{"
                + "  \"$jsonSchema\": {"
                + "    \"type\": \"object\","
                + "    \"title\": \"students\","
                + "    \"additionalProperties\": true,"
                + "    \"properties\":"
                + "    {"
                + "      \"year\": { \"type\": \"number\" },"
                + "      \"class\": { \"type\": [ \"number\", \"string\"] },"
                + "      \"language\": { \"type\": \"string\" },"
                + "      \"name\": { \"anyOf\":"
                + "      ["
                + "        { \"type\": \"string\" },"
                + "        {"
                + "          \"type\": \"object\","
                + "          \"additionalProperties\": false,"
                + "          \"properties\":"
                + "          {"
                + "              \"first\": { \"type\": \"string\" },"
                + "              \"nick\": { \"type\": \"string\" }"
                + "          }"
                + "        }"
                + "        ]"
                + "      }"
                + "    },"
                + "    \"required\": [ \"year\", \"class\" ]"
                + "  }"
                + "}"
          });

  /** {@inheritDoc} */
  @Override
  public void setup() {

    // Create the settings.
    settings = new SettingsDto(EDatabaseSystems.MONGO, EExtractionTools.HACK, EContainmentTools.JSS);
    ConnectionInfoDto connectionInfoDto = new ConnectionInfoDto(URL);
    connectionInfoDto.setDatabase(DATABASE);
    settings.setConnectionInfo(connectionInfoDto);
    CollectionDto collectionDto = new CollectionDto();
    collectionDto.setName(COLLECTION);
    collectionDto.setCount(26);
    settings.setCollection(collectionDto);

    // Delete the database and create it anew.
    MongoClient client = MongoClients.create(URL);
    MongoDatabase db = client.getDatabase(DATABASE);
    db.drop();

    // Create and get the Collection.
    db.getCollection(COLLECTION).drop();
    client.getDatabase(DATABASE).createCollection(COLLECTION);
    MongoCollection<Document> collection = db.getCollection(COLLECTION);

    // Prepare the documents and add them to the collection.
    addDocuments();
    List<Document> mongoDocuments = new ArrayList<>();
    for (String document : documents) {
      mongoDocuments.add(Document.parse(document));
    }
    collection.insertMany(mongoDocuments);
    client.close();
  }

}
