package josch.persistency.implementation.mongodb;

import josch.model.dto.ConnectionInfoDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * This class performs the testing methods of the {@code
 * josch.persistency.implementation.mongodb.ClientTest} with parameters being passed to the client
 * in contrast to a url. A local mongoDB server is required für the tests to work as they address
 * the localhost directly with a default timeout of 300ms.
 *
 * @author Kai Dauberschmidt
 */
public class UrlClientTest extends ClientTest {

  /** Ensures that the database is in the correct state and creates the DTOs with parameters. */
  @BeforeEach
  public void setup() {
    super.setup();

    // create the valid dto
    validDto = new ConnectionInfoDto("mongodb://localhost:27017/ext");

    // create the valid blank dto.
    validBlankDto = new ConnectionInfoDto("mongodb://localhost:27017/");

    // create the invalid dto
    invalidDto = new ConnectionInfoDto("mongodb://foo:13/ext");
  }

  // Perform the tests of the parent class.
  /** {@inheritDoc} */
  @Test
  public void construct_validDto() {
    super.construct_validDto();
  }

  /** {@inheritDoc} */
  @Test
  public void construct_invalidDto() {
    super.construct_invalidDto();
  }

  /** {@inheritDoc} */
  @Test
  public void close_connectedClient() {
    super.close_connectedClient();
  }

  /** {@inheritDoc} */
  @Test
  public void getCollection_exists() {
    super.getCollection_exists();
  }

  /** {@inheritDoc} */
  @Test
  public void getCollection_notExists() {
    super.getCollection_notExists();
  }

  /** {@inheritDoc} */
  @Test
  public void getAllCollections_validDatabase() {
    super.getAllCollections_validDatabase();
  }

  /** {@inheritDoc} */
  @Test
  public void getAllDatabases_validDto() {
    super.getAllDatabases_validDto();
  }

  /** {@inheritDoc} */
  @Test
  public void setDatabase_exists() {
    super.setDatabase_exists();
  }

  /** {@inheritDoc} */
  @Test
  public void setDatabase_notExists() {
    super.setDatabase_notExists();
  }

  /** {@inheritDoc} */
  @Test
  public void getCollectionCount_validDatabase() {
    super.getCollectionCount_validDatabase();
  }

  /** {@inheritDoc} */
  @Test
  public void getDatabase_exists() {
    super.getDatabase_exists();
  }

  /** {@inheritDoc} */
  @Test
  public void getDatabase_notExists() {
    super.getDatabase_notExists();
  }

  /** {@inheritDoc} */
  @Test
  public void getRandomDocument_twoExist() {
    super.getRandomDocument_twoExist();
  }

  /** {@inheritDoc} */
  @Test
  public void getRandomDocument_notExists() {
    super.getRandomDocument_notExists();
  }

  /** {@inheritDoc} */
  @Test
  public void insertOne_duplicateKeys() {
    super.insertOne_duplicateKeys();
  }

  /** {@inheritDoc} */
  @Test
  public void insertOne_validDocument() {
    super.insertOne_validDocument();
  }

  /** {@inheritDoc} */
  @Test
  public void insertOne_invalidDocument() {
    super.insertOne_invalidDocument();
  }

  /** {@inheritDoc} */
  @Test
  public void validate_validDocument_validSchema() {
    super.validate_validDocument_validSchema();
  }

  /** {@inheritDoc} */
  @Test
  public void validate_invalidDocument_validSchema() {
    super.validate_invalidDocument_validSchema();
  }

  /** {@inheritDoc} */
  @Test
  public void validate_invalidDocument_invalidSchema() {
    super.validate_invalidDocument_invalidSchema();
  }

  /** {@inheritDoc} */
  @Test
  public void validate_validDocument_invalidSchema() {
    super.validate_validDocument_invalidSchema();
  }
}
